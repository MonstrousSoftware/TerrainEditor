# TerrainEditor

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template including simple application launchers and an `ApplicationAdapter` extension that draws libGDX logo.


# Dev Notes

Derived from ClipMappingTerrain project.

Using a Perlin noise height map for now.

To do: 
- Normals. note that the meshes are reused due to the clipmapping so you cannot use
normal attributes of the mesh. Use a normal map?

note: height map image file has height in alpha channel, perlin noise has it in color component.
Adapt the shader accordingly.



## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `teavm`: Web backend that supports most JVM languages.
