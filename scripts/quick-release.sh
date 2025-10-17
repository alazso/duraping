#!/bin/bash

# ⚡ Quick Release Script - Minimal Interaction
# Usage: ./scripts/quick-release.sh

set -e

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}⚡ DuraPing Quick Release${NC}"
echo -e "${BLUE}========================${NC}"

# Get current version from build.gradle
CURRENT_VERSION=$(grep 'version = ' build.gradle | sed 's/.*version = "\(.*\)".*/\1/')
echo -e "Current version: ${GREEN}${CURRENT_VERSION}${NC}"

# Extract components
if [[ $CURRENT_VERSION =~ ^([0-9]+\.[0-9]+\.[0-9]+)-([^-]+)-([0-9]+\.[0-9]+\.[0-9]+)$ ]]; then
    BASE_VERSION="${BASH_REMATCH[1]}"
    TYPE="${BASH_REMATCH[2]}"
    MC_VERSION="${BASH_REMATCH[3]}"
else
    echo -e "${YELLOW}⚠️  Could not parse version format. Using defaults.${NC}"
    BASE_VERSION="0.5.0"
    TYPE="stable"
    MC_VERSION="1.21.10"
fi

echo -e "Detected: ${GREEN}${BASE_VERSION}-${TYPE}-${MC_VERSION}${NC}"

# Determine branch
if [ "$MC_VERSION" = "1.21.9" ]; then
    BRANCH="stable/1.21.9"
else
    BRANCH="main"
fi

echo -e "Target branch: ${GREEN}${BRANCH}${NC}"

# Check if we're on the right branch
CURRENT_BRANCH=$(git branch --show-current)
if [ "$CURRENT_BRANCH" != "$BRANCH" ]; then
    echo -e "${YELLOW}⚠️  Switching to ${BRANCH} branch...${NC}"
    git checkout "$BRANCH"
fi

# Run the release script
echo -e "${BLUE}🚀 Starting automated release...${NC}"
./scripts/release.sh "$BASE_VERSION" "$TYPE" "$MC_VERSION"
