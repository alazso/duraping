# DuraPing Wiki

Welcome to the comprehensive documentation for DuraPing, a client-side durability alert mod for Minecraft 1.21.9 Fabric.

## Quick Links

- **[Installation Guide](Installation)** - Get started with DuraPing
- **[Configuration](Configuration)** - Customize alert behavior and thresholds
- **[Features & Usage](Features-and-Usage)** - Learn about all available features
- **[Keybindings](Keybindings)** - Keyboard shortcuts and controls
- **[Troubleshooting](Troubleshooting)** - Common issues and solutions
- **[FAQ](FAQ)** - Frequently asked questions
- **[Advanced Configuration](Advanced-Configuration)** - Per-item overrides and fine-tuning
- **[Development](Development)** - Contributing and building from source

## What is DuraPing?

DuraPing is a lightweight, client-only Fabric mod that prevents you from accidentally breaking your valuable tools, armor, and elytra. It provides customizable alerts when your equipment durability drops below configurable thresholds.

### Key Features

**Multi-Tier Alert System**
- Warn threshold (default 25%)
- Danger threshold (default 10%)
- Critical threshold (default 5%)
- Emergency 1-durability alerts

**Alert Types**
- Chat notifications with color coding
- Custom sound effects
- Screen flash overlay
- Toast (hotbar) notifications

**Smart Monitoring**
- Tracks all equipment slots (hands and armor)
- Activity-aware suppression during mining
- Per-bucket cooldown management
- Hysteresis support for Mending

**Quality of Life**
- Elytra flight guard
- Three customizable keybindings
- Snooze functionality
- In-game configuration via Mod Menu
- No server-side installation required

## System Requirements

### Minimum Requirements
- **Minecraft**: 1.21.9
- **Fabric Loader**: 0.17.3 or higher
- **Fabric API**: 0.134.0+1.21.9 or higher
- **Java**: 21 or higher

### Recommended
- **Mod Menu**: For in-game configuration
- **Cloth Config**: Automatically installed with Mod Menu

### Performance
DuraPing is designed to be extremely lightweight:
- Client-side only (no network traffic)
- Minimal CPU usage (tick-based checking)
- No impact on server performance
- Compatible with performance mods (Sodium, Lithium, etc.)

## Compatibility

**Works With**
- Vanilla Minecraft servers
- Modded servers (no server-side installation needed)
- Singleplayer worlds
- Realms

**Known Compatible Mods**
- Sodium, Lithium, Phosphor (performance mods)
- Iris Shaders
- Mod Menu
- REI (Roughly Enough Items)
- JEI (Just Enough Items)
- All client-side optimization mods

**Potential Conflicts**
DuraPing has no known mod conflicts. If you encounter issues with other mods, please report them on our [issue tracker](https://github.com/redlynxlabs/duraping/issues).

## Support

Need help? Here's how to get support:

1. **Check the wiki** - Most common questions are answered here
2. **Search existing issues** - Someone may have already reported your problem
3. **Create an issue** - Use our issue templates for bugs, features, or questions
4. **Visit RedLynx Labs** - [https://redlynx.io](https://redlynx.io)

## License

DuraPing is licensed under GNU General Public License v3.0. You are free to:
- Use the mod for personal or commercial purposes
- Modify and distribute the source code
- Include in modpacks (attribution appreciated)

See the [LICENSE](https://github.com/redlynxlabs/duraping/blob/main/LICENSE) file for full terms.

## Credits

- **Organization**: [RedLynx Labs](https://redlynx.io)
- **Author**: redkey
- **Built with**: Fabric, Cloth Config, Mod Menu
- **Community**: Thank you to all contributors and users!

---

**Last Updated**: December 2024  
**DuraPing Version**: 0.3.x  
**Minecraft Version**: 1.21.9

