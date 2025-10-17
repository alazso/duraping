#!/bin/bash

# 🎯 Dual Release Script - Release both MC versions simultaneously
# Usage: ./scripts/dual-release.sh [version] [type]

set -e

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

VERSION=${1:-"0.5.0"}
TYPE=${2:-"stable"}

echo -e "${BLUE}🎯 DuraPing Dual Release${NC}"
echo -e "${BLUE}=======================${NC}"
echo -e "Version: ${GREEN}${VERSION}${NC}"
echo -e "Type: ${GREEN}${TYPE}${NC}"
echo -e "Releasing for both Minecraft 1.21.9 and 1.21.10"
echo ""

# Store current branch
ORIGINAL_BRANCH=$(git branch --show-current)

# Release for 1.21.9
echo -e "${BLUE}📦 Releasing for Minecraft 1.21.9...${NC}"
./scripts/release.sh "$VERSION" "$TYPE" "1.21.9"

# Wait a moment
sleep 2

# Release for 1.21.10  
echo -e "${BLUE}📦 Releasing for Minecraft 1.21.10...${NC}"
./scripts/release.sh "$VERSION" "$TYPE" "1.21.10"

# Return to original branch
echo -e "${BLUE}🔄 Returning to original branch...${NC}"
git checkout "$ORIGINAL_BRANCH"

echo -e "${GREEN}✅ Dual release completed!${NC}"
echo -e "${BLUE}🌐 Both releases are now building on GitHub Actions.${NC}"
echo -e "${BLUE}📋 Monitor progress at: https://github.com/$(git config --get remote.origin.url | sed 's/.*github.com[:/]\([^.]*\).*/\1/')/actions${NC}"
