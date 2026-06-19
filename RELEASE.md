# DuraPing Release Process

DuraPing uses static semantic versioning and [Stonecutter](https://stonecutter.kikugie.dev)
for multi-version builds. The release version is the `version=` value in
`gradle.properties`, and a release is cut by pushing a matching `vX.Y.Z` tag. One
tag builds every supported Minecraft version for both Fabric and NeoForge and
publishes them to GitHub, Modrinth, and CurseForge.

## Supported versions

Declared in the Stonecutter `create` block in `settings.gradle.kts`. Minecraft
1.21.9, 1.21.10, and 1.21.11 are supported on both loaders, packaged as three jars:

- **Fabric** (one jar, 1.21.9-1.21.11): Fabric's intermediary mappings stay stable
  across these patches, including the 1.21.11 `ResourceLocation` -> `Identifier`
  rename, so a single jar runs on all three.
- **NeoForge** (two jars): NeoForge runs against Mojang mappings with no
  intermediary layer, so the 1.21.11 rename forces a split into a 1.21.9-1.21.10 jar
  and a 1.21.11 jar.

Per-version dependency coordinates live in `gradle.properties`, keyed by Minecraft
version (for example `fabric_api_1.21.11`).

## Building locally

- `./gradlew chiseledBuild` builds all three jars, into `versions/<node>/build/libs/`.
- `./gradlew :1.21.9-fabric:build` builds a single node (nodes: `1.21.9-fabric`,
  `1.21.9-neoforge`, `1.21.11-neoforge`).
- `./gradlew "Reset active project"` normalizes the working tree to the canonical
  node (1.21.9-fabric). Run this before committing so the versioned `//?`
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
`chiseledBuild`, creates the GitHub release with all three jars, and runs
`chiseledPublish` to Modrinth and CurseForge.

## Dry run

Run the `Release` workflow manually (`workflow_dispatch`) with **dry run** enabled
to build and stage the jars without creating a release or publishing.

## Notes

- Idempotent: the workflow skips if the tag's release already has its three jars.
- Publishing needs the `MODRINTH_TOKEN` and `CURSEFORGE_API_KEY` repository
  secrets; without them the publish runs as a dry run.
- Adding a Minecraft version: fill its coordinates in `gradle.properties`. A Fabric
  patch inside the current line usually just needs the `minecraft` range widened (no
  new node). A new NeoForge jar is only needed at an API break like the 1.21.11
  rename: add a `version("X.Y.Z-neoforge", "X.Y.Z")` node in `settings.gradle.kts`
  and extend the `neoMcRange`/`neoLabel` split in `build.neoforge.gradle.kts`. Run
  `./gradlew "Refresh active project"` after changing the matrix. 26.x will be a
  separate, larger bracket (unobfuscated, Java 25).
