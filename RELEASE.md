# DuraPing Release Process

DuraPing uses static semantic versioning. The release version is the `version=`
value in `gradle.properties`, and a release is cut by pushing a matching `vX.Y.Z`
tag. One tag produces one GitHub release with both the Fabric and NeoForge jars,
and publishes them to Modrinth and CurseForge.

## Cutting a release

1. **Bump the version.** Edit `version=` in `gradle.properties` (for example
   `version=0.6.1`), following [Semantic Versioning](https://semver.org):
   patch for fixes, minor for features, major for breaking changes.
2. **Update the changelog.** Add a `## [X.Y.Z] - YYYY-MM-DD` section to
   `CHANGELOG.md` describing the changes, and add the matching link reference at
   the bottom. The release workflow extracts this section for the release notes.
3. **Commit and push** to `main`.
4. **Tag and push.** The tag must match the version exactly:

   ```bash
   git tag -a v0.6.1 -m "Release 0.6.1"
   git push origin v0.6.1
   ```

The `Release` workflow then validates the tag against `gradle.properties`, builds
both loaders, creates the GitHub release, and publishes to Modrinth and CurseForge.

## Dry run

To build and stage the artifacts without publishing, run the `Release` workflow
manually (`workflow_dispatch`) with **dry run** enabled. It builds and uploads the
jars as a workflow artifact but creates no release and publishes nothing.

## Notes

- The workflow is **idempotent**: if a release for the tag already has its jars
  attached, it skips.
- Publishing requires the `MODRINTH_TOKEN` and `CURSEFORGE_API_KEY` repository
  secrets. Without them, the Modrinth and CurseForge publish runs as a dry run.
- The build targets Minecraft 1.21.10 and the jars declare support for
  1.21.9 through 1.21.11. A separate line will target 26.x later.
