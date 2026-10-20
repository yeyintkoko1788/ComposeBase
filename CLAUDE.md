# CLAUDE.md

Project conventions and hard-won gotchas. Read this before changing anything.

This project was cloned from **ComposeBase**, an in-house Android scaffold. Two earlier apps came
from the same scaffold (Easy Choice Merchant, Easy Choice User); the rules below are the ones that
cost real debugging time there. They are not style preferences.

---

## Stack

Jetpack Compose · Navigation 3 (`NavDisplay` + `entryProvider`) · Hilt + KSP · Room · Retrofit +
OkHttp · Coil · Kotlin Flows. Java 17, `minSdk 26`.

---

## Architecture

### The dependency rule

```
Compose screen → ViewModel → Repository (interface) → RemoteDataSource (interface)
                                  │                        └─ Fake…  /  …Impl
                                  └─ Room DAO
```

Four rules hold this together. Each exists because breaking it caused a specific problem.

**1. Fakes live at the *datasource* seam, never as a fake repository.**
A `MockXRepository` means the caching policy, optimistic-send reconciliation and refresh
de-duplication are throwaway code — deleted the day the backend arrives and rewritten under
deadline, untested. Faking at the datasource means *the repository you run today is the one you
ship*.

**2. Reads are a single cache-backed `Flow`.** No `getXFromNetwork()` beside `observeX()`. The
moment a repository offers both, the cache-vs-network decision leaks into ViewModels — which is
exactly the knowledge the repository exists to contain.

**3. Datasources return domain models, not DTOs.** Mapping and error normalisation are the
datasource's job. Nothing above it may see a Retrofit type.

**4. Room's DAO *is* the cache datasource.** No `XCacheDataSource` wrapping it.

### Adding a backend endpoint

Change **one file**: the `@Binds` in `RemoteDataSourceModule`. Write the Retrofit service, write
`XRemoteDataSourceImpl` against the existing interface, bind it instead of the fake. Repositories,
ViewModels and screens are untouched.

> If swapping a binding forces an edit above the repository, the abstraction has leaked. Stop and
> find where — that leak is what this structure exists to prevent.

### Deliberately absent

**Use cases** (one-line pass-throughs at this size), **`Resource<T>` wrappers on every read**
(loading/error is screen-specific and already in each `XUiState`), **a cache datasource
interface**.

---

## Gotchas that will bite

These are all real failures from the sibling projects, in rough order of how long they took to find.

### Build and DI

- **JNI symbols must match the package.** `native-lib.c` declares
  `Java_<package_with_underscores>_ConstantModule_getBaseURL`. Rename the package and the build
  still succeeds — it fails at *runtime* with `UnsatisfiedLinkError`. `./gradlew verifyJniSymbols`
  (wired into `preBuild`, so every build runs it) now fails with the exact names to fix, and also
  checks `proguard-rules.pro` still names the class. Note that in the bare scaffold R8 shrinks
  `ConstantModule` away entirely (nothing injects `@Named("base_url")` yet), so a release build of
  the scaffold itself can't exercise the binding at runtime — a clone's first real service does.
- **R8 is on for release** (`isMinifyEnabled` + `isShrinkResources`), because turning it on late
  surfaces every reflection/JNI/Gson assumption at once as a release-only crash. Rules live in
  `app/proguard-rules.pro`. After adding a library that uses reflection, JNI, or serialization by
  field name, add its keep and run `./gradlew assembleProductionRelease` — a debug build proves
  nothing about R8. The `network.response.**` keep exists so a DTO can't silently lose fields.
- **`local.properties` holds the release signing keys** (`RELEASE_STORE_FILE`,
  `RELEASE_STORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`; gitignored). Without them
  a release build is produced **unsigned** with a warning, not a failure — Play will reject it.
- **The manifest declares `INTERNET` and `ACCESS_NETWORK_STATE`** (omitting either fails at runtime
  on the first call) and **removes the three advertising-ID permissions** with `tools:node="remove"`
  so a later Firebase/ads dependency can't reintroduce them unnoticed.
- **Retrofit is Gson + suspend functions only.** The RxJava2 adapter and SimpleXML converter were
  scaffold leftovers and are gone — `converter-simplexml` drags in `xpp3`, which R8 refuses to shrink
  alongside the platform's `XmlPullParser`. Don't add them back.
- **Room entity fields must be constructor parameters.** A body `val x: T = ...` has a backing
  field but no constructor slot, and KSP fails with *"Cannot find setter for property"* — reported
  against the wrong line, often the end of the file. `val x get() = ...` is fine: no backing field.
- **`@OptIn` cannot annotate an `init` block.** Move the code into a private function.
- **The `kotlin("plugin.serialization")` version in the root `build.gradle.kts` must equal the Kotlin
  version.** It was pinned to 1.9.22 against Kotlin 2.2.21 and still built — serialization breaks
  later, at runtime. It is a different artifact from the `kotlinx-serialization` *library* version in
  the catalog, so the catalog alias can't be reused for it.
- **Hilt 2.59+ requires AGP 9**, and bumping Hilt alone fails at configuration time. The
  `android.builtInKotlin=false` / `android.newDsl=false` (and related) lines in `gradle.properties`
  are deliberate AGP 9 opt-outs, each deprecated and gone in AGP 10 — migrate on purpose, don't
  delete them to silence the warnings.
- **Each flavor needs its own `google-services.json`** in `app/src/<flavor>/`, from a Firebase app
  registered with that flavor's *application ID*. A missing file fails only that flavor's build; a
  file whose package doesn't match fails with "no matching client". Give each environment its own
  application ID (`applicationIdSuffix`) so both install side by side and keep separate push tokens.
- **Work started from a ViewModel on a Nav3 entry dies with that entry.** A screen popped the moment
  its call succeeds cancels its own `viewModelScope` — so "refetch after submit" launched there never
  finishes. Do it from the entry that stays, or from a singleton.

### Network

- **The shared Gson uses `FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES`.** APIs here are
  camelCase. **Put an explicit `@SerializedName` on every DTO field** — without it Gson looks for
  `access_token`, finds nothing, and you get a "successful" response full of nulls.
  `ResponseDtoSerializedNameTest` fails the build for any camelCase field in `network.response`
  that lacks one (it already caught `MetaData.statusCode`, which was silently null on every
  response). Single-word lower-case names are exempt — the policy doesn't change them.
- **Gson writes `null` into non-null Kotlin enum fields** when it meets an unrecognised value — it
  uses reflection and never runs the null check. Filter those rows out on read, or the first `when`
  on them throws.
- **Login must not run on the authenticated client.** `AuthenticationInterceptor` treats any 401 as
  an expired token and fires a refresh, so a wrong password causes a pointless refresh *and* a
  session-expired event that bounces the user off the login screen. Use the unauthenticated client.
- **Check `meta.success`, not just the HTTP status.** A 2xx carrying `success: false` otherwise
  lands as a success with null fields.
- **Server-computed fields are authoritative.** e.g. `isOpen` — the server knows the business's
  timezone and holiday overrides; a client deriving it from the device clock disagrees with what
  customers see.
- **A "no longer available" answer is a domain state, not an error banner.** Map it once (a typed
  check on `NetworkException.errorCode`, e.g. 409 on accept *and* on the fetch that precedes it) and
  show the dedicated sheet; scattering `errorCode == 409` across ViewModels guarantees one path is
  missed.
- **Maintenance arrives as an HTTP status on any call**, so it is detected in
  `NetworkExceptionInterceptor` and surfaced through `MaintenanceEventBus` to take over the whole app
  — not handled per screen. The status value is the backend's convention; confirm it per project.

### Compose

- **`Scaffold` reserves the system bars by default.** With edge-to-edge that paints a band above
  every screen. `contentWindowInsets = WindowInsets(0)` and let each screen pad itself — a gradient
  header must apply `.statusBarsPadding()` *after* `.background()` so the gradient bleeds and only
  its content is inset.
- **`Popup` and `Dialog` compose in a new `AndroidComposeView`**, which re-provides `LocalContext`
  and `LocalConfiguration` from the Activity. Any per-composable locale scope is discarded inside
  them — re-apply it. The symptom only shows on strings that differ between locales, so it hides.
- **`detectTransformGestures` consumes single-finger drags** (pan is non-zero even at scale 1). Use
  it inside a pager and swiping stops working entirely. Hand-roll `awaitEachGesture` and only
  consume on a multi-touch pinch or a drag on an already-zoomed page.
- **`fillMaxHeight()` in a wrap-content `Row` is silently ignored** — the incoming max constraint
  is unbounded. Give the Row `Modifier.height(IntrinsicSize.Min)`.
- **`LocalLifecycleOwner` comes from `androidx.lifecycle.compose`**, not `androidx.compose.ui.platform`.
- **Android attenuates shadow colours** — roughly ×0.19 spot, ×0.039 ambient — before compositing.
  Literal alphas look far too strong on paper and far too weak on screen.
- **`android:windowSoftInputMode="adjustResize"`** is required for `WindowInsets.ime` to report
  anything. With `adjustNothing` a chat composer sits under the keyboard.
- **Pull-to-refresh needs something scrollable.** `PullToRefreshBox` receives the pull as nested
  scroll, and a plain non-scrollable `Column` (an empty or error state) never dispatches any, so the
  gesture silently does nothing there. Pass `scrollable = true` to `EmptyViewPod`. Inside it,
  `fillMaxSize()` must come **before** `verticalScroll()` on the same node — `verticalScroll` gives
  its content unbounded height, and a `fillMaxSize()` after it (or inside a scrollable wrapper
  around it) silently no-ops, which pins `Arrangement.Center` content to the top.
- **`ErrorSnackbar` / `SuccessSnackbar` are not a `SnackbarHost`** — they take layout space where
  placed. Put them in a `Box` with `Modifier.align(Alignment.BottomCenter)`; as `Column` children
  they push the footer/composer down and back each time they show.
- **A search field cannot live in a `DropdownMenu`.** The menu is a popup and the keyboard simply
  covers it. Use a `ModalBottomSheet` with `Modifier.imePadding()` and cap its height as a fraction of
  the screen (`LocalConfiguration`), not a fixed dp (`SearchableOptionSheet`).
- **Compose never destroys a `WebView`.** Give `AndroidView` an `onRelease { stopLoading();
  loadUrl("about:blank"); destroy() }`, or an embedded video keeps playing its audio after the user
  leaves, and a fullscreen player stays attached to the Activity.
- **YouTube embeds need a real origin.** `loadDataWithBaseURL(null, …)` gives an opaque origin and
  the player fails with errors 150/152/153; pass `https://<applicationId>`. The fullscreen button does
  nothing unless the `WebChromeClient` implements `onShowCustomView` (`FullscreenChromeClient`).
- **"List is empty" is not "still loading".** A screen that shows a spinner while `list.isEmpty()`
  spins forever on a legitimate empty response. Gate on the request's own state (`isLoading` /
  `error`), and start `isLoading` as `true` when the ViewModel fetches in `init`.
- **A collector that both applies a change and decides whether it happened must read the "before"
  value first.** Reading `state.status` after the update that set it always looks unchanged, so the
  follow-up (a refetch) never fires.
- **An optimistic local patch must update everything derived from the same event, or refetch.**
  Flipping `status` locally while a timeline built from server timestamps stays as of the last fetch
  shows a half-finished history.
- **`PullToRefreshBox`, `ModalBottomSheet`, `Dialog` and `Popup` all compose in their own window** —
  re-apply the language scope (`AppLanguageScope`) inside each.

### Localisation

Full writeup and the add-a-language / add-a-string recipes: [docs/LOCALIZATION.md](docs/LOCALIZATION.md).
In short: the language is switched **in-app, independent of the device language**, via
`LanguageManager` + `AppLanguageProvider` (`ui/locale/AppLanguage.kt`) — instant, no
`Activity.recreate()`. `LanguagePicker` is a sample switcher; any composable can change the
language through `LocalLanguage.current.onSelect(code)`.

- Strings live in `res/values/`, `values-my/`, `values-zh/`. **Keep the three at key parity** and
  check format-arg parity too — a `%1$s` present in one locale and not another is a runtime crash.
- **`bundle { language { enableSplit = false } }` in `app/build.gradle.kts` must stay.** Play's AAB
  default delivers only the languages in the device's *system* list, so an in-app switch to any
  other language silently falls back to English — on Play installs only; a sideloaded APK works
  fine, which is why it goes unnoticed. Test language switching from a Play-installed build.
- **Wrap `Dialog` / `Popup` / `ModalBottomSheet` content in `AppLanguageScope`** (see the Compose
  gotcha above — this is the same issue, and `LanguagePicker` shows the pattern).
- **`BasePreviewWrapper` provides `LocalLanguage` outside `BaseTheme`** — keep it there, or every
  preview crashes with "LocalLanguage not provided".
- Don't build one sentence from several concatenated strings; word order differs between
  languages. Use one string with placeholders.
- Store *types*, not rendered labels. Writing "Photo" into a cached preview freezes it to the
  language it was written in; storing the message type and resolving at render time re-labels
  existing rows when the language changes.

---

### Push notifications

- **Nothing arrives without Firebase set up** — the Google Services plugin plus a
  `google-services.json` per flavor. Without them `PushMessagingService` is inert and nothing crashes,
  which is how it goes unnoticed.
- **Send data-only messages.** A `notification` block lets the OS draw the push itself and skips
  `onMessageReceived` when the app is in the background — no tray id, no event, no sound.
- **Key the tray entry by the entity, not the push** (`trayNotificationIdFor`): pushes about one
  order replace each other, and opening the order can `cancel()` the entry. Cancel it when the
  entity's screen opens, however the user got there.
- **Every push is news to an open screen.** `PushMessagingService` emits on `PushEventBus`; the bell
  badge and notification list refetch from it. Badge counts are decremented per *read event*, so
  mark-as-read must check the notification's state first — marking blindly decrements twice.
- **"Read" endpoints take the notification id, screens know the entity id.** The push is the only
  place both are seen, so record the pair when it arrives and mark read when the entity's screen
  opens (`OrderNotificationTracker` in the rider app). Persist it: the process that got the push is
  often gone by then.
- **A channel's settings are frozen once created.** Changing sound or importance needs a new channel
  id, and the old one deleted so it doesn't linger in the system settings.
- **A channel sound isn't reliable on every phone** (a Redmi played none with ring mode and alarm
  volume up). An alert that must be heard is played directly with `MediaPlayer` on the alarm stream,
  and `onMessageReceived` blocks until it finishes — returning early lets the process drop to the
  background and cuts it off.

## Conventions

**Never swallow a failure silently.** Every `catch`, and every `runCatching { … }.getOrNull()` /
`.getOrDefault()`, that doesn't rethrow must call `logSwallowed("what failed", e)`
(`network/Logger.kt`). Swallowing is often right — a push-token failure must not block sign-in —
but with no log it is indistinguishable from "it worked and returned the default", which is how a
release-only push-registration failure went unnoticed. Filter Logcat on `SWALLOWED_EXCEPTIONS`.

**Log through `showLog*` / `logSwallowed`, never `android.util.Log` or `printStackTrace()`.** The
helpers are no-ops outside debug builds, so HTTP error bodies and parsed server errors never reach a
release Logcat; the raw calls bypass that gate. Arguments are evaluated before the gate runs — guard
an expensive or stream-reading argument with `if (BuildConfig.DEBUG)` (see
`NetworkExceptionInterceptor`).

**Comments explain *why*, not *what*.** Assume the reader can see the code. Comment the decision,
the constraint, the alternative that was rejected. A comment restating the line below it is noise.

**Design values are measured, not eyeballed.** Sample colours and geometry from the exported PNGs
programmatically, and state the scale you measured at.

**Verify structurally before claiming done.** Run `./gradlew testStagingDebugUnitTest` (includes the
DTO `@SerializedName` guard and the locale-parity guard), `./gradlew verifyJniSymbols`, and — after
dependency or rule changes — `./gradlew assembleProductionRelease`. Where there is no compiler in the
loop, at minimum check: brace/paren balance (string- and comment-aware), local imports resolve to declared symbols,
every `R.string`/`R.drawable` exists, locale key parity, DI graph closure, and interface/impl
method parity.

**Own mistakes plainly.** If a check fails, work out whether the code or the checker is wrong
before "fixing" anything — several times the checker was at fault.

---
**Money is `BigDecimal`, formatted with `formatAmount`.** A `Double` can't hold most prices exactly
and a `Long` loses the fraction the first time the backend sends one. Whole amounts print without
decimals, fractional ones always with two.

**Say so when a workaround stands in for a real endpoint.** A fake data source, a placeholder
illustration and a hardcoded UAT host each get a comment naming what replaces them — the next
project inherits the file, not the conversation.

## Repo map

```
data/            repository impls, common helpers (RefreshGate), datasource interfaces
domain/          models, repository interfaces — pure Kotlin, no Android imports
network/         Retrofit services, DTOs, interceptors, fakes, real datasource impls
cache/           Room database, DAOs, type converters
ui/              screens, common components, theme, navigation, viewmodels,
                 locale (in-app language switching — see docs/LOCALIZATION.md)
di/              Hilt modules — App, Network, Service, Data, Remote
scripts/         export_locale_strings.py — spreadsheet round-trip for translators
docs/            architecture notes; keep these current when the shape changes
```

`ui/common/` holds shared components. **Modify what is there rather than adding a parallel
version** — the previous project accumulated three button styles that way.
