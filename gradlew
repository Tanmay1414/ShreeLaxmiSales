#!/usr/bin/env sh
set -eu
if command -v gradle >/dev/null 2>&1; then exec gradle "$@"; fi
V=8.7; D="$HOME/.gradle-bootstrap/gradle-$V"; Z="$HOME/.gradle-bootstrap/gradle-$V.zip"; mkdir -p "$HOME/.gradle-bootstrap"; if [ ! -x "$D/bin/gradle" ]; then curl -fL --retry 3 -o "$Z" "https://services.gradle.org/distributions/gradle-$V-bin.zip"; rm -rf "$D"; unzip -q "$Z" -d "$HOME/.gradle-bootstrap"; rm -f "$Z"; fi; exec "$D/bin/gradle" "$@"
