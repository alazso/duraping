# Changelog

All notable changes to DuraPing are documented here. This project follows
[Semantic Versioning](https://semver.org). Releases before 0.6.0 are listed on the
[GitHub releases](https://github.com/redlynxlabs/duraping/releases) page.

## [0.6.0] - 2026-06-15

### Changed
- Relicensed from GPL-3.0 to MIT.
- Supported Minecraft versions consolidated to 1.21.9, 1.21.10, and 1.21.11 from
  a single source tree. Support for 1.21.8 and older is dropped.
- Versioning is now static semantic versioning read from `gradle.properties`; the
  build no longer derives the version from git tags.
- Auto-swap diagnostics now log at debug level through the mod logger instead of
  printing to the console, so the game log stays clean.

### Added
- A documented release pipeline that builds the Fabric and NeoForge jars and
  publishes them to GitHub Releases, Modrinth, and CurseForge from a single
  `vX.Y.Z` tag, with release notes taken from this changelog.
- A CI workflow that builds, tests, and uploads a snapshot jar on every push and
  pull request to `main`.

### Fixed
- Auto-swap now moves items through server-synced inventory actions instead of
  editing the client inventory directly, so swaps (tools and armor) hold on real
  servers instead of reverting. It also no longer acts while another container,
  such as a chest, is open.

### Removed
- Per-version release branches and the `v...-stable-1.21.x` tag scheme, replaced
  by plain `vX.Y.Z` tags on a single branch.

[0.6.0]: https://github.com/redlynxlabs/duraping/releases/tag/v0.6.0
