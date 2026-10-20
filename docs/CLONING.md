# Cloning ComposeBase into a new project

Done twice before (Easy Choice Merchant, Easy Choice User). The steps are mechanical; **step 4 is
the one that fails silently** if skipped.

Throughout, substitute:

| Placeholder | Example |
|---|---|
| `<NEW_PACKAGE>` | `com.joygroup.acmedelivery` |
| `<NEW_PATH>` | `com/joygroup/acmedelivery` |
| `<APP_NAME>` | `Acme Delivery` |
| `<THEME>` | `AcmeDelivery` |

---

## 1. Copy the source tree, not the build output

```bash
rsync -a --exclude build --exclude .gradle --exclude .idea --exclude .git \
      --exclude .kotlin --exclude local.properties \
      ComposeBase/ <new-project>/
cd <new-project> && git init
```

Copying `.gradle/` or `.idea/` drags absolute paths from the other machine into the new repo, and
they end up committed. ComposeBase's own history had this problem.

## 2. Move the package directories

```bash
cd app/src/main/java
mkdir -p <NEW_PATH> && git mv com/yeyint/composebase/* <NEW_PATH>/ 2>/dev/null || \
  { cp -r com/yeyint/composebase/* <NEW_PATH>/ && rm -rf com/yeyint; }
```

Do the same under `app/src/test/java` and `app/src/androidTest/java` if present.

## 3. Rewrite package and import statements

```bash
grep -rl 'com\.yeyint\.composebase' app/src app/proguard-rules.pro \
  | xargs sed -i '' 's/com\.yeyint\.composebase/<NEW_PACKAGE>/g'
```

(Drop the `''` after `-i` on Linux.) **Include `app/proguard-rules.pro`** — it is outside `app/src`,
and its serialization and DTO keep rules name the package. Missed, they silently match nothing and
the damage shows up only in a release build (renamed DTO fields, broken NavRoute serialization).
Then check nothing was missed:

```bash
grep -rn 'com\.yeyint\.composebase' app/src app/proguard-rules.pro && echo "STILL PRESENT" || echo "clean"
```

## 4. Fix the JNI symbols — **do not skip this**

`app/src/main/jni/native-lib.c` declares functions whose names encode the fully-qualified class:

```c
Java_com_yeyint_composebase_di_ConstantModule_getBaseURL
Java_com_yeyint_composebase_di_ConstantModule_getBaseStagingURL
```

Rename them to match `<NEW_PACKAGE>`, dots replaced by underscores:

```c
Java_com_joygroup_acmedelivery_di_ConstantModule_getBaseURL
```

Step 3 rewrites the `package` of the Kotlin classes, so the C symbols must follow. Left alone, the
build used to succeed and fail at runtime with `UnsatisfiedLinkError` on the first network call —
which looks like a networking bug. `./gradlew verifyJniSymbols` (it also runs on every build, via
`preBuild`) now fails with the exact missing and stale symbol names, and also checks that
`proguard-rules.pro` still names `ConstantModule`. ComposeBase's own symbols match its package.

While you are in this file, set both URLs — production and staging usually want to differ, and the
last two projects shipped with both pointing at the same host.

## 5. Gradle and manifest identity

- `app/build.gradle.kts` → `namespace` and `applicationId` → `<NEW_PACKAGE>`
- `settings.gradle.kts` → `rootProject.name`
- `res/values/strings.xml` → `app_name` → `<APP_NAME>`
- `res/values/themes.xml` → `Theme.<THEME>`, and the `android:theme` references in the manifest
- `ui/theme/Theme.kt` → rename the `…Theme` composable to match

## 6. Housekeeping the scaffold does not do for you

- **Add a root `.gitignore`** before the first commit — `build/`, `.gradle/`, `.idea/`, `.kotlin/`,
  `local.properties`. Retrofitting it later means a large, noisy "untrack everything" commit.
- **Release signing.** Put the four keys in the (gitignored) `local.properties` before cutting a
  release; without them the build is produced unsigned with a warning and Play rejects it:

  ```properties
  RELEASE_STORE_FILE=/absolute/path/to/release.jks
  RELEASE_STORE_PASSWORD=...
  RELEASE_KEY_ALIAS=...
  RELEASE_KEY_PASSWORD=...
  ```
- **Smoke-test the R8 build early.** Release is minified from the start (`proguard-rules.pro`); run
  `./gradlew assembleProductionRelease` after each dependency you add, not once at the end.
- **Check Chucker is release-safe.** It must be `debugImplementation` plus a no-op release
  variant, or the network inspector ships to users.
- **Decide the language list.** The scaffold ships English, Burmese and Chinese. If the new app
  needs fewer or different ones, edit `ui/locale/SupportedLanguage.kt` and the `values-*` folders
  together (recipe in [LOCALIZATION.md](LOCALIZATION.md)). Leave
  `bundle { language { enableSplit = false } }` in `app/build.gradle.kts` whatever you choose.
- **`SecurePreference.FILE_NAME`** is referenced by `res/xml/backup_rules.xml`. If you rename it,
  rename it in both places or credentials start getting cloud-backed up.

## 7. Verify before the first commit

```bash
grep -rn 'composebase\|ComposeBase' app/src app/proguard-rules.pro --include=*.kt --include=*.xml --include=*.c --include=*.pro
./gradlew verifyJniSymbols testStagingDebugUnitTest assembleDebug assembleProductionRelease
```

Then **launch it and make one network call.** A clean build proves nothing about step 4.

---

## What you inherit

Working: Hilt graph, Retrofit + OkHttp with auth/refresh interceptors, `SecurePreference`
(EncryptedSharedPreferences), Room setup, Navigation 3 with per-tab back stacks
(`TopLevelBackStack`), theme and typography, common components in `ui/common/`, and in-app
language switching (en / my / zh, sample picker on `ProfileScreen` — see
[LOCALIZATION.md](LOCALIZATION.md)).

Guards that run with the unit tests: every camelCase DTO field has `@SerializedName`, and every
`values-*` locale matches the default's keys and format arguments. Release builds are minified and
the JNI symbols are verified on every build.

Known gaps to expect: little test coverage beyond those guards; `RefreshGate` has no staleness policy (a refresh fires on first
collection, not "if older than N minutes"); refresh failures are swallowed into the cached-data
path, so a screen cannot show "couldn't refresh" without adding a `syncState` flow.
