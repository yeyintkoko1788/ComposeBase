# Localization

In-app language switching: English (`en`), Burmese (`my`), Chinese (`zh`), changeable at runtime
**independent of the device language**, instantly, with no `Activity.recreate()`. This is the setup
the Easy Choice Merchant / User and FDMS Rider apps ship; ComposeBase carries it with a working
sample so new projects start with it instead of rediscovering its gotchas.

## How it works

| Piece | File | Job |
|---|---|---|
| `LanguageManager` | `ui/locale/AppLanguage.kt` | Singleton holding the selected code as a `StateFlow`, persisted in `MyPreference` (`KEYS.LOCALE`). Defaults to `en`; there is no "follow system" mode. |
| `Context.withAppLanguage()` | same | Wraps a `Context` so `resources` resolve in the chosen locale. Also sets `Locale.setDefault()` so `SimpleDateFormat` follows the language. |
| `AppLanguageProvider` | same | Compose side: re-provides `LocalContext`/`LocalConfiguration` for the chosen language. A language change recomposes every `stringResource` reader — that is why no restart is needed. |
| `MainActivity.attachBaseContext` | `ui/app/MainActivity.kt` | Applies the saved language to the Activity context (Toasts, system dialogs) before Hilt injection runs, hence `readSavedLanguage()` reads preferences directly. |
| `AppLanguageScope` | `ui/locale/AppLanguage.kt` | Re-applies the language inside `Dialog` / `Popup` / `ModalBottomSheet` (see gotchas). |
| `SupportedLanguage` | `ui/locale/SupportedLanguage.kt` | The hardcoded list of languages (code + name string). |
| `LanguagePicker` | `ui/common/LanguagePicker.kt` | **Sample** switcher: button + dialog. Used on `ProfileScreen`. Restyle or delete. |
| `Accept-Language` header | `di/AuthenticationInterceptor.kt` | Sends the selected language to the backend as `Accept-Language` (`en`/`my`/`zh`). |

Any composable changes the language through `LocalLanguage.current.onSelect(code)`; no ViewModel
plumbing is required, so the picker also works on pre-login screens.

## Add a language

1. `uitls/Constants.kt` — add `LOCALE_xx` (skip if it exists).
2. `ui/locale/SupportedLanguage.kt` — add an entry.
3. `res/values/strings.xml` — add `language_xx` with `translatable="false"` (a language is always
   shown in its own script, in every locale).
4. `res/values-xx/strings.xml` — translate **every** translatable key from `values/strings.xml`.
5. `di/AuthenticationInterceptor.kt` — map the new code to its `Accept-Language` value.
6. If the language needs digits/fonts handled specially, that decision belongs in
   `withAppLanguage()` — the single place the app's locale is decided.

## Add a string

Add it to `values/`, `values-my/` and `values-zh/` in the same change. `LocalizationParityTest`
(run with `./gradlew testStagingDebugUnitTest`) fails on a missing key, an extra key, or a
format-argument mismatch (`%1$s` in one locale but not another is a runtime crash) — it discovers
`values-*` folders itself, so a new language is covered automatically. Pure-data keys (`pref_*`,
language names) are `translatable="false"` and excluded.

## Gotchas

- **AAB language splits — the one that only appears on Play.** Play's default is to deliver only
  the `values-<lang>` resources for languages in the device's *system* language list. An in-app
  switch to a language the phone isn't set to then silently falls back to English, while a directly
  installed APK (which contains everything) works. `app/build.gradle.kts` sets
  `bundle { language { enableSplit = false } }` to ship all languages in the base module. **Do not
  remove it, and re-check it after any AGP upgrade** — it is marked `@Incubating`; a rename would
  fail the build loudly rather than regress silently. Verify a bundle by decoding
  `BundleConfig.pb`: `split_dimension { value: 3 (LANGUAGE), negate: true }`.
- **`Dialog` / `Popup` / `ModalBottomSheet` discard the language scope.** They compose in a new
  window whose `LocalContext` comes from the Activity. Wrap their content in `AppLanguageScope`.
  The symptom only shows on strings that differ between locales, so it hides — language names are
  identical in every locale, which is why a picker built only from them looks fine.
- **`BasePreviewWrapper` must provide `LocalLanguage` outside `BaseTheme`.** Anything in the theme
  that reads it is evaluated one level above `BaseTheme`'s content lambda; nested inside, every
  preview crashes with "LocalLanguage not provided".
- **Digits are forced to Latin (`-u-nu-latn`).** Without it Burmese renders `%d` as native digits in
  every formatted string and date. Revisit only if a design actually wants native digits.
- **Non-Activity contexts don't carry the language.** `Application`, `Service`, `BroadcastReceiver`
  (e.g. a push receiver building a notification) use the process default. Wrap with
  `context.withAppLanguage(readSavedLanguage(context))` where the text is user-visible.
- **Don't assemble a sentence from concatenated string segments** (the legal line on `LoginScreen`
  does, and the Burmese translation can't express its word order). Use one string with
  `%1$s` placeholders, or an annotated string with one placeholder per link.
- **Store types, not rendered labels** — in Room, caches and models. A stored "Photo" is frozen to
  the language it was written in; a stored enum is re-labelled at render time.

## Known leftovers

`Extensions.kt`'s `formatTimesCount(locale)` still hardcodes per-language text ("time(s)", "ခါ",
"度") in Kotlin. Prefer `<plurals>` resources; it is left in place only because the scaffold's
callers (if any) were not audited.

## Checklist before shipping a build

1. `./gradlew testStagingDebugUnitTest` — `LocalizationParityTest` covers key and format-arg parity.
2. Switch language on a device whose **system language is English only**, using a build installed
   **from Play** (internal testing is enough) — not a sideloaded APK.
3. Open every dialog, bottom sheet and popup after switching; their text must follow the switch.
4. Confirm `Accept-Language` on a Chucker-captured request changes with the picker.
