# Shree Laxmi Sales - Codemagic

Upload the contents of this folder to the root of your GitHub repository. The repository root must contain `codemagic.yaml`, `gradlew`, `build.gradle`, `settings.gradle`, `gradle.properties`, and `app/`.

In Codemagic, refresh/check for configuration files, then select the `android-debug-apk` workflow and start the build. The output is `app-debug.apk` under Artifacts. This is a debug-signed APK suitable for personal installation; no keystore setup is required.
