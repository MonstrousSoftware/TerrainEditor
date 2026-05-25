# TerrainEditor

Visualize and edit large scale terrain using geometry clipmapping.

The idea is that the height map is so large that for full resolution it cannot be kept in memory, it has to be paged in on demand.




A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).





# Dev Notes

Derived from ClipMappingTerrain project.

Using a Perlin noise height map for now.

To do: 
- Normals. note that the meshes are reused due to the clipmapping so you cannot use
normal attributes of the mesh. Use a normal map?
- Adapt terrain shader to perform lighting using the normals (note we are not using the default shader).

note: height map image file has height in alpha channel, perlin noise has it in color component.
Adapt the shader accordingly.



## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `teavm`: Web backend that supports most JVM languages.
