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

## Repo map

```
data/            repository impls, common helpers (RefreshGate), datasource interfaces
domain/          models, repository interfaces — pure Kotlin, no Android imports
network/         Retrofit services, DTOs, interceptors, fakes, real datasource impls
cache/           Room database, DAOs, type converters
ui/              screens, common components, theme, navigation, viewmodels,
                 locale (in-app language switching — see docs/LOCALIZATION.md)
di/              Hilt modules — App, Network, Service, Data, Remote
docs/            architecture notes; keep these current when the shape changes
```

`ui/common/` holds shared components. **Modify what is there rather than adding a parallel
version** — the previous project accumulated three button styles that way.
