# DuraPing Release Process

## Overview

DuraPing uses **Axion Release** for dynamic versioning based on git tags. The version is automatically determined from the most recent git tag, eliminating the need for manual version updates in build files.

## Branch Structure

- **`main`** - Minecraft 1.21.10 (latest)
- **`stable/1.21.9`** - Minecraft 1.21.9 (stable)
- **`dev/1.21.10`** - Development for MC 1.21.10
- **`dev/1.21.9`** - Development for MC 1.21.9

## Version Format

Tags follow this format: `v{VERSION}-{TYPE}-{MC_VERSION}`

Examples:
- `v0.5.3-stable-1.21.10`
- `v0.5.3-stable-1.21.9`
- `v0.5.4-beta-1.21.10`
- `v0.6.0-rc-1.21.10`

## Release Process

### Quick Release (Single Version)

For a single Minecraft version:

```bash
# For MC 1.21.10 (main branch)
./scripts/release.sh 0.5.3 stable 1.21.10

# For MC 1.21.9 (stable/1.21.9 branch)
./scripts/release.sh 0.5.3 stable 1.21.9
```

### Dual Release (Both Versions)

To release for both Minecraft versions simultaneously:

```bash
./scripts/dual-release.sh 0.5.3 stable
```

This will:
1. Create tag `v0.5.3-stable-1.21.9` on `stable/1.21.9` branch
2. Create tag `v0.5.3-stable-1.21.10` on `main` branch
3. Push both tags to trigger GitHub Actions

### Manual Release

You can also create tags manually:

```bash
# Switch to the appropriate branch
git checkout main  # or stable/1.21.9

# Create the tag
git tag v0.5.3-stable-1.21.10 -m "Release 0.5.3-stable-1.21.10"

# Push the tag
git push origin v0.5.3-stable-1.21.10
```

## How It Works

### 1. Axion Release (Dynamic Versioning)

The `build.gradle` uses axion to determine the version from git tags:

```groovy
scmVersion {
    tag {
        prefix = "v"
    }
    useHighestVersion = true
    ignoreUncommittedChanges = true
}

version = scmVersion.version
```

**No manual version updates needed!** Axion reads the version from the git tag automatically.

### 2. GitHub Actions Workflow

When a tag matching `v*-stable-*`, `v*-beta-*`, or `v*-rc-*` is pushed:

1. **Detect Release Info** - Extracts version, Minecraft version, and release type from tag
2. **Build Artifacts** - Builds both Fabric and NeoForge JARs in parallel
3. **Create Release** - Creates a single GitHub release with both JARs attached
4. **Notify Completion** - Outputs release information

### 3. Tag Format Parsing

The workflow automatically parses tags:

- `v0.5.3-stable-1.21.10` → version: `0.5.3-stable-1.21.10`, MC: `1.21.10`, type: `stable`
- `v0.5.3-stable-1.21.9` → version: `0.5.3-stable-1.21.9`, MC: `1.21.9`, type: `stable`
- `v0.6.0-beta-1.21.10` → version: `0.6.0-beta-1.21.10`, MC: `1.21.10`, type: `beta`

## Release Checklist

- [ ] Ensure all changes are committed and pushed
- [ ] Verify CI builds pass on target branch
- [ ] Choose version number following semantic versioning
- [ ] Run release script for desired MC version(s)
- [ ] Verify GitHub Actions workflow completes successfully
- [ ] Check that release appears in GitHub Releases with both JARs

## Troubleshooting

### GPG Signing Issues

If you encounter GPG signing errors with tags:

```bash
# Temporarily disable GPG signing
git config --local tag.gpgSign false

# Create tag without signature
git tag v0.5.3-stable-1.21.10 -m "Release message"

# Push tag
git push origin v0.5.3-stable-1.21.10
```

### Version Not Detected

If axion isn't detecting the version:

```bash
# Check current version axion will use
./gradlew currentVersion

# List recent tags
git tag --list | sort -V | tail -5

# Ensure you're on the right branch
git branch --show-current
```

### Build Failures

If GitHub Actions build fails:

1. Check the Actions tab for detailed logs
2. Verify `gradle.properties` has correct Minecraft version for the branch
3. Ensure dependencies are available and versions are correct
4. Test build locally: `./gradlew :fabric:build :neoforge:build`

## Best Practices

1. **Always tag from the correct branch**
   - Use `main` for 1.21.10 releases
   - Use `stable/1.21.9` for 1.21.9 releases

2. **Keep version numbers in sync** across MC versions
   - `v0.5.3-stable-1.21.10` and `v0.5.3-stable-1.21.9` should have the same base version

3. **Test before releasing**
   - Run local builds to verify everything works
   - Check lint errors: `./gradlew check`

4. **Use semantic versioning**
   - `MAJOR.MINOR.PATCH` format
   - Increment PATCH for bug fixes
   - Increment MINOR for new features
   - Increment MAJOR for breaking changes

5. **Write meaningful release notes**
   - The workflow generates basic notes automatically
   - Edit the GitHub release after creation to add changelog details

## Advanced: Multi-Platform Publishing

For publishing to CurseForge/Modrinth, additional configuration is needed in the mod-specific build files. This is handled separately from the GitHub release process.
