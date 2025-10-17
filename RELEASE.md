# 🚀 DuraPing Release System

## Overview
This repository includes a comprehensive, automated release system that handles multi-branch releases with minimal interaction required.

## 🎯 Features

### ✨ **Automated Release Pipeline**
- **Multi-branch support**: Release from `main` (1.21.10) and `stable/1.21.9` simultaneously
- **Smart versioning**: Automatic version detection and tagging
- **GitHub Actions integration**: Automated builds and releases
- **Cross-platform scripts**: Works on Windows, Linux, and macOS
- **Minimal interaction**: One command releases

### 🏷️ **Version Naming Convention**
```
v0.5.0-stable-1.21.9    ← Stable release for MC 1.21.9
v0.5.0-stable-1.21.10   ← Stable release for MC 1.21.10
v0.5.0-beta-1.21.10     ← Beta release for MC 1.21.10
v0.5.0-rc-1.21.10       ← Release candidate for MC 1.21.10
```

## 🚀 Quick Start

### **Option 1: Quick Release (Recommended)**
```bash
# Automatically detect current version and release
./scripts/quick-release.sh
```

### **Option 2: Dual Release (Both MC versions)**
```bash
# Release both 1.21.9 and 1.21.10 simultaneously
./scripts/dual-release.sh 0.5.0 stable
```

### **Option 3: Manual Release**
```bash
# Release specific version
./scripts/release.sh 0.5.0 stable 1.21.10
```

## 📋 Release Types

| Type | Description | Example |
|------|-------------|---------|
| `stable` | Production-ready release | `v0.5.0-stable-1.21.10` |
| `beta` | Beta testing release | `v0.5.0-beta-1.21.10` |
| `rc` | Release candidate | `v0.5.0-rc-1.21.10` |

## 🏗️ GitHub Actions Workflow

The system automatically:
1. **Detects release information** from tags or manual dispatch
2. **Builds for both platforms** (Fabric + NeoForge)
3. **Creates GitHub releases** with proper naming
4. **Uploads JAR artifacts** with clean naming
5. **Sends notifications** on completion

### **Trigger Methods**
- **Tag push**: `git tag v0.5.0-stable-1.21.10 && git push origin v0.5.0-stable-1.21.10`
- **Manual dispatch**: GitHub Actions → Run workflow
- **Script execution**: Automated via release scripts

## 📦 Artifact Naming

### **Generated JARs**
```
duraping-fabric-0.5.0-stable-1.21.10.jar
duraping-neoforge-0.5.0-stable-1.21.10.jar
```

### **Release Assets**
- **Fabric JAR**: `duraping-fabric-{version}.jar`
- **NeoForge JAR**: `duraping-neoforge-{version}.jar`
- **Source JARs**: `duraping-{platform}-{version}-sources.jar`
- **Javadoc JARs**: `duraping-{platform}-{version}-javadoc.jar`

## 🔧 Configuration

### **Branch Mapping**
- `main` → Minecraft 1.21.10 (latest)
- `stable/1.21.9` → Minecraft 1.21.9 (stable)

### **Build Configuration**
- **Java**: OpenJDK 21
- **Gradle**: Wrapper version
- **Platforms**: Fabric + NeoForge
- **Artifacts**: JAR, Sources, Javadoc

## 🎮 Usage Examples

### **Release Stable for Both Versions**
```bash
# Release 0.5.0 stable for both MC versions
./scripts/dual-release.sh 0.5.0 stable
```

### **Release Beta for Latest**
```bash
# Release 0.5.0 beta for MC 1.21.10
./scripts/release.sh 0.5.0 beta 1.21.10
```

### **Quick Release (Auto-detect)**
```bash
# Automatically detect and release
./scripts/quick-release.sh
```

## 🛠️ Advanced Usage

### **Manual GitHub Actions Dispatch**
1. Go to **Actions** → **Multi-Branch Release Pipeline**
2. Click **Run workflow**
3. Select parameters:
   - **Release type**: `stable`, `beta`, or `rc`
   - **Minecraft version**: `1.21.9` or `1.21.10`
   - **Custom version**: Optional override

### **Custom Version Override**
```bash
# Use custom version format
./scripts/release.sh 0.5.1-hotfix stable 1.21.10
```

## 📊 Release Monitoring

### **GitHub Actions Dashboard**
- **URL**: `https://github.com/{owner}/{repo}/actions`
- **Workflow**: "Multi-Branch Release Pipeline"
- **Status**: Real-time build progress

### **Release Pages**
- **URL**: `https://github.com/{owner}/{repo}/releases`
- **Assets**: Download JARs and documentation

## 🔍 Troubleshooting

### **Common Issues**

#### **Uncommitted Changes**
```bash
# Error: Uncommitted changes detected
# Solution: Commit or stash changes first
git add .
git commit -m "feat: your changes"
```

#### **Wrong Branch**
```bash
# Error: Not on correct branch
# Solution: Script automatically switches branches
```

#### **Tag Already Exists**
```bash
# Error: Tag already exists
# Solution: Use different version or delete existing tag
git tag -d v0.5.0-stable-1.21.10
git push origin --delete v0.5.0-stable-1.21.10
```

## 🎯 Best Practices

### **Release Workflow**
1. **Develop features** in `dev/*` branches
2. **Merge to stable** branches when ready
3. **Run release scripts** for automated releases
4. **Monitor GitHub Actions** for build progress
5. **Verify releases** on GitHub releases page

### **Version Management**
- **Semantic versioning**: `MAJOR.MINOR.PATCH`
- **MC version suffix**: `-1.21.9` or `-1.21.10`
- **Release type**: `-stable`, `-beta`, `-rc`

### **Branch Strategy**
- **`main`**: Latest stable (1.21.10)
- **`stable/1.21.9`**: Legacy stable (1.21.9)
- **`dev/*`**: Development branches
- **Feature branches**: `feat/*`, `fix/*`

## 🚀 Future Enhancements

### **Planned Features**
- **Automatic changelog generation**
- **Release notes from commits**
- **Dependency updates**
- **Security scanning**
- **Performance metrics**

### **Integration Options**
- **Modrinth publishing**
- **CurseForge publishing**
- **Discord notifications**
- **Slack integration**

---

*Built with ❤️ for the Minecraft modding community*
