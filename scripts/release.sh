#!/bin/bash

# 🚀 DuraPing Automated Release Script
# Usage: ./scripts/release.sh [version] [type] [mc_version]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Default values
VERSION=${1:-"0.5.0"}
TYPE=${2:-"stable"}
MC_VERSION=${3:-"1.21.10"}

# Determine branch
if [ "$MC_VERSION" = "1.21.9" ]; then
    BRANCH="stable/1.21.9"
else
    BRANCH="main"
fi

FULL_VERSION="${VERSION}-${TYPE}-${MC_VERSION}"
TAG_NAME="v${FULL_VERSION}"

echo -e "${BLUE}🚀 DuraPing Release Automation${NC}"
echo -e "${BLUE}================================${NC}"
echo -e "Version: ${GREEN}${FULL_VERSION}${NC}"
echo -e "Type: ${GREEN}${TYPE}${NC}"
echo -e "Minecraft: ${GREEN}${MC_VERSION}${NC}"
echo -e "Branch: ${GREEN}${BRANCH}${NC}"
echo -e "Tag: ${GREEN}${TAG_NAME}${NC}"
echo ""

# Check if we're on the right branch
CURRENT_BRANCH=$(git branch --show-current)
if [ "$CURRENT_BRANCH" != "$BRANCH" ]; then
    echo -e "${YELLOW}⚠️  Switching to ${BRANCH} branch...${NC}"
    git checkout "$BRANCH"
fi

# Check for uncommitted changes
if ! git diff-index --quiet HEAD --; then
    echo -e "${RED}❌ Uncommitted changes detected!${NC}"
    echo -e "${YELLOW}Please commit or stash your changes before releasing.${NC}"
    exit 1
fi

# Update version in build.gradle
echo -e "${BLUE}📝 Updating version in build.gradle...${NC}"
sed -i "s/version = \".*\"/version = \"${FULL_VERSION}\"/" build.gradle

# Update Minecraft version in gradle.properties
echo -e "${BLUE}📝 Updating Minecraft version in gradle.properties...${NC}"
sed -i "s/minecraft_version=.*/minecraft_version=${MC_VERSION}/" gradle.properties

# Commit version changes
echo -e "${BLUE}💾 Committing version changes...${NC}"
git add build.gradle gradle.properties
git commit -m "chore: bump version to ${FULL_VERSION}

- Update version to ${FULL_VERSION}
- Update Minecraft version to ${MC_VERSION}
- Prepare for ${TYPE} release"

# Create and push tag
echo -e "${BLUE}🏷️  Creating tag ${TAG_NAME}...${NC}"
git tag -a "$TAG_NAME" -m "Release ${FULL_VERSION}

🎯 DuraPing ${FULL_VERSION}
📦 Minecraft Version: ${MC_VERSION}
🚀 Release Type: ${TYPE}

### Features
- ⚡ Durability monitoring and alerts
- 🔊 Sound, chat, and visual notifications  
- 🔄 Auto-swap functionality
- ⚙️ Highly configurable
- 🎮 Multi-platform support (Fabric + NeoForge)

### Installation
1. Download the appropriate JAR for your mod loader
2. Place in your \`mods\` folder
3. Launch Minecraft and enjoy!

---
*Built with ❤️ for the Minecraft community*"

echo -e "${BLUE}📤 Pushing changes and tag...${NC}"
git push origin "$BRANCH"
git push origin "$TAG_NAME"

echo -e "${GREEN}✅ Release ${FULL_VERSION} initiated!${NC}"
echo -e "${BLUE}🌐 GitHub Actions will now build and publish the release.${NC}"
echo -e "${BLUE}📋 Monitor progress at: https://github.com/$(git config --get remote.origin.url | sed 's/.*github.com[:/]\([^.]*\).*/\1/')/actions${NC}"
