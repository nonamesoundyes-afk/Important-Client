# Important — Fabric client mod

Minecraft 1.21.11 · Java 21 · Fabric

## Build on GitHub
Upload the contents of this folder to the repository root. The workflow is in
`.github/workflows/build.yml`. It builds the project and uploads the JAR as the
`Important-JAR` artifact.

## Menu
Open the menu with **Right Shift**. The interface has a dark sidebar, accent color,
module categories, per-module switches, reset, and automatic config saving.

## Currently implemented visual HUD
- Watermark
- FPS
- Coordinates
- Facing direction
- Keystrokes (W/A/S/D and Space)

The config also contains additional module toggles, but not every listed toggle is
implemented as a visual effect yet. Those need separate rendering/event/mixin work;
the switch itself does not magically implement an effect. The reference Enough Visuals
JAR is used only as a visual reference, not as source code to copy.

## HUD editor (new)
Open Right Shift → **Edit HUD**. Drag the main HUD preview or the WASD preview to reposition it.
Use Scale to cycle the main HUD spacing scale. Positions and scale are saved in `config/important.json`.
Armor HUD and Potion HUD render basic armor durability and active-effect timers when enabled.
