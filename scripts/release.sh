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

echo -e "${BLUE}DuraPing Release Automation${NC}"
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

# Check for uncommitted changes (warn only)
if ! git diff-index --quiet HEAD --; then
    echo -e "${YELLOW}⚠️  Note: Uncommitted changes detected.${NC}"
    echo -e "${YELLOW}The release will use the current committed state.${NC}"
fi

# Create and push tag (axion will handle versioning automatically)
echo -e "${BLUE}Creating tag ${TAG_NAME}...${NC}"
git tag -s "$TAG_NAME" -m "Release ${FULL_VERSION} - DuraPing for Minecraft ${MC_VERSION}"

echo -e "${BLUE}Pushing changes and tag...${NC}"
git push origin "$BRANCH"
git push origin "$TAG_NAME"

echo -e "${GREEN}Release ${FULL_VERSION} initiated!${NC}"
echo -e "${BLUE}GitHub Actions will now build and publish the release.${NC}"
echo -e "${BLUE}Monitor progress at: https://github.com/$(git config --get remote.origin.url | sed 's/.*github.com[:/]\([^.]*\).*/\1/')/actions${NC}"
