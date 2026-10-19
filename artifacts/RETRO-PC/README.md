# RETRO PC: 3D Environment Enhancement

## Project overview
This C++ application uses OpenGL and GLU to render a retro computer desk scene. The enhanced version preserves the existing scene and camera controls while adding a scene-object registry, view-frustum culling, and Shift-to-sprint movement.

## Algorithms and data structures
The enhanced application stores scene objects in a `std::vector<SceneObject>`. Each entry contains an object name, bounding-sphere center and radius, drawing-function pointer, and an option to remain visible.

For each frame, the application iterates over the registered objects. The `IsObjectVisible` function projects each object's center into camera-relative coordinates and tests its bounding sphere against the camera's near/far depth limits and horizontal/vertical field of view. Objects that pass the visibility test are drawn; objects outside the tested view are skipped.

## Complexity
For n registered objects, the visibility pass performs O(n) object checks per frame. Each individual visibility test uses a fixed number of arithmetic operations, so its time complexity is O(1). The registry uses O(n) storage. The drawing cost depends on the geometry of the objects that are rendered.

The algorithm adds per-object visibility calculations and is most beneficial when enough objects can be skipped. This scene is relatively small, so a measurable frame-rate improvement has not been established.

## Design trade-offs
Bounding spheres provide inexpensive visibility tests but approximate object shapes. Their radii must be conservative enough to avoid hiding visible geometry. The desk is configured to remain visible to preserve the scene's large supporting surface. The vector keeps object registration and rendering organized, although a linear scan is appropriate for this small scene rather than a more complex spatial index.

## Additional usability enhancement
Holding Shift while moving activates sprinting at 2.5 times the normal movement speed. Releasing Shift restores normal speed. The movement change uses the existing Windows keyboard-state API and does not require a new input subsystem.

## Validation performed
- The original source compiled successfully.
- The enhanced source compiled successfully with the Visual Studio C++ toolchain.
- The enhanced executable launched successfully.
- Visual inspection confirmed that the scene appeared correctly and the existing movement controls worked.
- Shift-to-sprint was tested interactively and reported to work correctly.

## Limitations
The scene-object bounds are manually specified. Future work could add automated visibility tests, adjustable bounds, frame-time measurements, and a larger scene to quantify performance effects.
