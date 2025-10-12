# Development Guide

This guide is for developers who want to contribute to DuraPing or build it from source.

## Prerequisites

### Required Software

**Java Development Kit (JDK) 21**
- Download: [Adoptium](https://adoptium.net/) or [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)
- Verify installation:
  ```bash
  java -version
  javac -version
  ```

**Git**
- Download: [git-scm.com](https://git-scm.com/)
- Verify installation:
  ```bash
  git --version
  ```

### Recommended Tools

**IDE**:
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) (recommended, Community Edition is free)
- [Visual Studio Code](https://code.visualstudio.com/) with Java extensions
- [Eclipse](https://www.eclipse.org/) with Fabric development plugins

**Minecraft**:
- Minecraft 1.21.9 installed (for testing)
- Fabric Loader 0.17.3+
- Fabric API 0.134.0+1.21.9

---

## Getting the Source Code

### Clone the Repository

```bash
git clone https://github.com/redlynxlabs/duraping.git
cd duraping
```

### Branch Structure

- **`main`**: Stable releases only
- **`dev`**: Active development branch (contribute here)
- **Feature branches**: `feature/your-feature-name`

### Checkout Development Branch

```bash
git checkout dev
```

---

## Building DuraPing

### Using Gradle Wrapper (Recommended)

**Windows**:
```cmd
gradlew.bat build
```

**Linux/macOS**:
```bash
./gradlew build
```

### Build Output

Successful build produces:
- `build/libs/duraping-[version].jar` - Remapped for production
- `build/libs/duraping-[version]-sources.jar` - Source code JAR

### Install to Local Minecraft

After building:
1. Copy `build/libs/duraping-[version].jar`
2. Paste into `.minecraft/mods/`
3. Launch Minecraft with Fabric 1.21.9 profile

---

## Development Environment Setup

### IntelliJ IDEA (Recommended)

**1. Import Project**
- File → Open → Select `duraping` folder
- IntelliJ will automatically detect Gradle
- Wait for Gradle sync to complete

**2. Generate Run Configurations**
```bash
./gradlew genSources
./gradlew idea
```

**3. Run Minecraft Client**
- Gradle panel → Tasks → fabric → `runClient`
- Or terminal: `./gradlew runClient`

**4. Reload Changes**
After code changes:
- Hot Reload: Modify code while Minecraft is running (limited support)
- Full Reload: Stop Minecraft, rebuild, restart

### Visual Studio Code

**1. Install Extensions**
- Language Support for Java
- Gradle for Java
- Fabric Loom

**2. Import Project**
- Open folder: `duraping`
- Wait for Gradle sync

**3. Run Minecraft**
```bash
./gradlew runClient
```

### Eclipse

**1. Generate Eclipse Project**
```bash
./gradlew eclipse
```

**2. Import**
- File → Import → Existing Projects into Workspace
- Select `duraping` folder

**3. Run Minecraft**
- External Tools → `gradle runClient`

---

## Project Structure

```
duraping/
├── .github/                     # GitHub configuration
│   ├── ISSUE_TEMPLATE/          # Issue templates
│   └── workflows/               # CI/CD workflows
├── gradle/                      # Gradle wrapper files
├── src/
│   └── main/
│       ├── java/                # Java source code
│       │   └── sh/redkey/mc/duraping/
│       │       ├── DuraPingClient.java       # Main mod entrypoint
│       │       ├── config/                   # Configuration system
│       │       │   ├── ConfigManager.java
│       │       │   ├── DuraPingConfig.java
│       │       │   └── ModMenuIntegration.java
│       │       ├── hud/                      # HUD rendering
│       │       │   └── HudFlashOverlay.java
│       │       ├── keybind/                  # Keybind handlers
│       │       │   └── Keybinds.java
│       │       ├── sound/                    # Sound registration
│       │       │   └── ModSounds.java
│       │       └── util/                     # Utility classes
│       │           ├── AlertState.java
│       │           ├── DurabilityCalc.java
│       │           └── ItemKey.java
│       └── resources/
│           ├── assets/duraping/
│           │   ├── lang/                     # Translations
│           │   │   └── en_us.json
│           │   ├── sounds/                   # Sound files
│           │   │   ├── warn.ogg
│           │   │   └── critical.ogg
│           │   └── sounds.json               # Sound definitions
│           ├── duraping.mixins.json          # Mixin configuration
│           └── fabric.mod.json               # Mod metadata
├── build.gradle.kts                          # Gradle build script
├── gradle.properties                         # Project properties
├── settings.gradle.kts                       # Gradle settings
├── LICENSE                                   # GNU GPLv3 license
└── README.md                                 # Project documentation
```

---

## Code Overview

### Main Components

**DuraPingClient.java**
- Mod initialization
- Tick event handler
- Durability checking logic
- Alert triggering
- Keybind processing

**DuraPingConfig.java**
- Configuration data model
- Threshold management
- Per-item overrides

**ConfigManager.java**
- JSON serialization/deserialization
- File I/O for configuration

**ModMenuIntegration.java**
- Cloth Config UI builder
- Mod Menu integration

**HudFlashOverlay.java**
- Screen flash overlay rendering
- HUD render callback

**Keybinds.java**
- Keybinding registration
- Key press detection

### Key Concepts

**Alert State Tracking**:
```java
Map<String, AlertState> stateByKey
```
Tracks per-item/slot state:
- Last bucket (warn/danger/critical)
- Last percentage
- Last alert timestamp
- Armed/disarmed status

**Bucket System**:
- Bucket 0: Healthy (above warn)
- Bucket 1: Warn
- Bucket 2: Danger
- Bucket 3: Critical

**Crossing Detection**:
Alerts fire when crossing from higher bucket to lower bucket (e.g., 0→1, 1→2, 2→3).

**Cooldown Management**:
Per-bucket cooldowns prevent spam. Critical bucket has shortest cooldown.

---

## Making Changes

### Development Workflow

**1. Create a Feature Branch**
```bash
git checkout dev
git pull origin dev
git checkout -b feature/my-new-feature
```

**2. Make Changes**
- Edit Java files in `src/main/java/`
- Update resources in `src/main/resources/`
- Follow existing code style

**3. Test Changes**
```bash
./gradlew runClient
```

**4. Build and Verify**
```bash
./gradlew build
```

**5. Commit with Conventional Commits**
```bash
git add .
git commit -m "feat: add new alert type"
```

**6. Push and Create PR**
```bash
git push origin feature/my-new-feature
```
Then open a Pull Request to `dev` branch on GitHub.

---

## Code Style

### Conventions

**Java**:
- 4 spaces for indentation
- Braces on same line (Java standard)
- Descriptive variable names
- Comments for complex logic

**Example**:
```java
public void checkDurability(ItemStack stack) {
    if (stack.isEmpty() || !stack.isDamageable()) return;
    
    int remaining = stack.getMaxDamage() - stack.getDamage();
    int percentage = (remaining * 100) / stack.getMaxDamage();
    
    // Check against configured threshold
    if (percentage <= config.warn) {
        triggerAlert(stack, AlertLevel.WARN);
    }
}
```

**Configuration**:
- JSON files use 2-space indentation
- Property names use camelCase

---

## Testing

### Manual Testing Checklist

**Basic Functionality**:
- [ ] Alerts trigger at correct thresholds
- [ ] Chat messages appear with correct colors
- [ ] Sounds play correctly
- [ ] Flash overlay appears
- [ ] Toast notifications work

**Keybindings**:
- [ ] Toggle works (enable/disable)
- [ ] Snooze works (snooze/cancel)
- [ ] Show durability displays correct info

**Configuration**:
- [ ] Config saves correctly
- [ ] Per-item overrides work
- [ ] Mod Menu integration functional

**Edge Cases**:
- [ ] 1-durability emergency alerts
- [ ] Multiple items at low durability
- [ ] Rapid durability changes
- [ ] Activity-aware suppression during mining

### Test Scenarios

**Scenario 1: Basic Alert**
1. Equip diamond pickaxe
2. Use `/enchant @s minecraft:efficiency 5`
3. Mine until durability reaches 25%
4. Verify warn alert fires

**Scenario 2: Threshold Crossing**
1. Get item to 30% durability
2. Use item until 24% (crosses warn threshold)
3. Verify alert fires once
4. Continue to 9% (crosses danger threshold)
5. Verify danger alert fires

**Scenario 3: Cooldown**
1. Trigger warn alert
2. Immediately damage item more
3. Verify no immediate repeat alert
4. Wait for cooldown period
5. Damage item again
6. Verify alert fires after cooldown

---

## Adding New Features

### Example: Adding a New Alert Type

**1. Add Config Field**
```java
// DuraPingConfig.java
public boolean customAlert = false;
```

**2. Add Mod Menu Entry**
```java
// ModMenuIntegration.java
general.addEntry(entry.startBooleanToggle(
    Text.literal("Custom Alert"), 
    cfg.customAlert
).setSaveConsumer(v -> cfg.customAlert = v).build());
```

**3. Implement Alert Logic**
```java
// DuraPingClient.java
if (cfg.customAlert) {
    // Custom alert implementation
    performCustomAlert(item);
}
```

**4. Test Thoroughly**
- Enable/disable in config
- Verify behavior with other alerts
- Check for conflicts

---

## Debugging

### Enable Debug Logging

Add logging to code:
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DuraPingClient {
    private static final Logger LOGGER = LoggerFactory.getLogger("duraping");
    
    public void checkDurability(ItemStack stack) {
        LOGGER.info("Checking durability for: {}", stack.getName());
        // ...
    }
}
```

View logs:
- Development: Console output
- Production: `.minecraft/logs/latest.log`

### Common Issues

**Build Fails**:
```bash
./gradlew clean build --refresh-dependencies
```

**IDE Not Recognizing Code**:
```bash
./gradlew genSources
./gradlew idea  # or eclipse
```

**Minecraft Won't Launch**:
- Check Fabric Loader version in `gradle.properties`
- Verify Fabric API version
- Check for conflicting mods in run directory

---

## Contributing Guidelines

### Before Submitting

**Code Quality**:
- [ ] Code follows existing style
- [ ] No unused imports
- [ ] Descriptive variable/method names
- [ ] Comments for complex logic

**Testing**:
- [ ] Tested in-game
- [ ] No console errors
- [ ] Verified with multiple items/scenarios

**Documentation**:
- [ ] Updated README if needed
- [ ] Added javadocs for public methods
- [ ] Updated wiki if feature-adding

### Pull Request Process

**1. Target `dev` Branch**
All PRs should target `dev`, not `main`.

**2. Use PR Template**
Fill out the provided pull request template:
- Description of changes
- Type of change
- Testing performed
- Checklist completion

**3. Follow Conventional Commits**
Commit message format:
```
type(scope): subject

body (optional)
```

**Types**:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `refactor`: Code refactoring
- `test`: Adding tests
- `chore`: Maintenance tasks

**Examples**:
```
feat: add elytra flight detection
fix: prevent duplicate alerts during lag
docs: update configuration guide
refactor: simplify threshold calculation
```

**4. Address Review Comments**
- Respond to all review feedback
- Make requested changes
- Push updates to same branch

**5. Merge**
Once approved, maintainers will merge to `dev`. Releases are cut from `main` after thorough testing.

---

## Release Process

**Note**: Only maintainers can create releases.

**1. Merge `dev` to `main`**
```bash
git checkout main
git merge dev
git push origin main
```

**2. Create Signed Tag**
```bash
git tag -a v0.3.2 -m "Release 0.3.2"
git push origin v0.3.2
```

**3. GitHub Actions**
- Automatically builds JAR
- Creates GitHub Release
- Attaches artifacts

---

## Resources

### Documentation
- [Fabric Wiki](https://fabricmc.net/wiki/)
- [Yarn Mappings](https://github.com/FabricMC/yarn)
- [Cloth Config](https://github.com/shedaniel/cloth-config)
- [Mod Menu](https://github.com/TerraformersMC/ModMenu)

### Community
- [Fabric Discord](https://discord.gg/v6v4pMv)
- [Minecraft Modding Reddit](https://www.reddit.com/r/fabricmc/)

### Tools
- [MCP Bot](https://mcpbot.bspk.rs/) - Mapping lookup
- [Fabric API Javadocs](https://maven.fabricmc.net/docs/)

---

## License

DuraPing is licensed under [GNU General Public License v3.0](https://github.com/redlynxlabs/duraping/blob/main/LICENSE).

Contributions are licensed under the same terms.

---

## Questions?

- Check existing [issues](https://github.com/redlynxlabs/duraping/issues)
- Ask in [discussions](https://github.com/redlynxlabs/duraping/discussions) (if enabled)
- Open a [question issue](https://github.com/redlynxlabs/duraping/issues/new?template=question.yml)

---

**Happy coding!** Thank you for contributing to DuraPing.

