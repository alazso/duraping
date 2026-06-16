# DuraPing Release Process

DuraPing uses static semantic versioning and [Stonecutter](https://stonecutter.kikugie.dev)
for multi-version builds. The release version is the `version=` value in
`gradle.properties`, and a release is cut by pushing a matching `vX.Y.Z` tag. One
tag builds every supported Minecraft version for both Fabric and NeoForge and
publishes them to GitHub, Modrinth, and CurseForge.

## Supported versions

Declared in the Stonecutter `create` block in `settings.gradle.kts`: Minecraft
1.21.9, 1.21.10, and 1.21.11, each for Fabric and NeoForge (six jars). Per-version
dependency coordinates live in `gradle.properties`, keyed by Minecraft version
(for example `fabric_api_1.21.11`).

## Building locally

- `./gradlew chiseledBuild` builds all six jars, into `versions/<version>-<loader>/build/libs/`.
- `./gradlew :1.21.10-fabric:build` builds a single node.
- `./gradlew "Reset active project"` normalizes the working tree to the canonical
  version (1.21.10-fabric). Run this before committing so the versioned `//?`
  comments are in a consistent state.

## Cutting a release

1. Bump `version=` in `gradle.properties` ([Semantic Versioning](https://semver.org)).
2. Add a `## [X.Y.Z] - YYYY-MM-DD` section to `CHANGELOG.md` (the workflow extracts it for the notes).
3. Run `./gradlew "Reset active project"`, commit, and push to `main`.
4. Tag and push (the tag must equal the version exactly):

   ```bash
   git tag -a v0.6.1 -m "Release 0.6.1"
   git push origin v0.6.1
   ```

The `Release` workflow validates the tag against `gradle.properties`, runs
`chiseledBuild`, creates the GitHub release with all six jars, and runs
`chiseledPublish` to Modrinth and CurseForge.

## Dry run

Run the `Release` workflow manually (`workflow_dispatch`) with **dry run** enabled
to build and stage the jars without creating a release or publishing.

## Notes

- Idempotent: the workflow skips if the tag's release already has its six jars.
- Publishing needs the `MODRINTH_TOKEN` and `CURSEFORGE_API_KEY` repository
  secrets; without them the publish runs as a dry run.
- Adding a Minecraft version: add `mc("X.Y.Z")` in `settings.gradle.kts`, fill its
  coordinates in `gradle.properties`, run `./gradlew "Refresh active project"`, and
  add any `//?` blocks or `replacements` for API differences. 26.x will be a
  separate, larger bracket (unobfuscated, Java 25).
