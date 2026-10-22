# AbyssalCraft (Forge 1.20.1 port)

> **This is an unofficial Minecraft Forge 1.20.1 port of AbyssalCraft.** It was not made, released or endorsed by the original author — please do not contact the original author about this port. **The source code is provided as-is, with no support: issues and pull requests are not accepted.**

- Original: Shinoow (https://github.com/Shinoow/AbyssalCraft)
- License: same as the original — code is LGPL-3.0 (`LICENSE.md`). **Textures, models, sounds, language files and structure files are All Rights Reserved by the original author and are NOT included in this repository**
- Target: Minecraft 1.20.1 / Forge 47.x / Java 17

## Build

```
./gradlew build
```

The jar is written to `build/libs/`; put it in your `mods/` folder.

## Assets

The original's restricted assets are not included. Clone the original 1.12.2 source, then import the assets into the git-ignored `src/restricted/`:

```
python tools/import_assets.py <path to original AbyssalCraft source>
python tools/import_structures.py <path to original AbyssalCraft source>
```

A jar built with these assets is for personal use only and must not be distributed.
