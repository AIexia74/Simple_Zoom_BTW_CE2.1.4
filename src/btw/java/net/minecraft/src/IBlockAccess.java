package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface IBlockAccess
{
    /**
     * Returns the block ID at coords x,y,z
     */
    int getBlockId(int var1, int var2, int var3);

    /**
     * Returns the TileEntity associated with a given block in X,Y,Z coordinates, or null if no TileEntity exists
     */
    TileEntity getBlockTileEntity(int var1, int var2, int var3);

    /**
     * Any Light rendered on a 1.8 Block goes through here
     */
    @Environment(EnvType.CLIENT)
    int getLightBrightnessForSkyBlocks(int var1, int var2, int var3, int var4);

    @Environment(EnvType.CLIENT)
    float getBrightness(int var1, int var2, int var3, int var4);

    /**
     * Returns how bright the block is shown as which is the block's light value looked up in a lookup table (light
     * values aren't linear for brightness). Args: x, y, z
     */
    @Environment(EnvType.CLIENT)
    float getLightBrightness(int var1, int var2, int var3);

    /**
     * Returns the block metadata at coords x,y,z
     */
    int getBlockMetadata(int var1, int var2, int var3);

    /**
     * Returns the block's material.
     */
    Material getBlockMaterial(int var1, int var2, int var3);

    /**
     * Returns true if the block at the specified coordinates is an opaque cube. Args: x, y, z
     */
    @Environment(EnvType.CLIENT)
    boolean isBlockOpaqueCube(int var1, int var2, int var3);

    /**
     * Indicate if a material is a normal solid opaque cube.
     */
    boolean isBlockNormalCube(int var1, int var2, int var3);

    /**
     * Returns true if the block at the specified coordinates is empty
     */
    @Environment(EnvType.CLIENT)
    boolean isAirBlock(int var1, int var2, int var3);

    /**
     * Gets the biome for a given set of x/z coordinates
     */
    BiomeGenBase getBiomeGenForCoords(int var1, int var2);

    /**
     * Returns current world height.
     */
    @Environment(EnvType.CLIENT)
    int getHeight();

    /**
     * set by !chunk.getAreLevelsEmpty
     */
    @Environment(EnvType.CLIENT)
    boolean extendedLevelsInChunkCache();

    /**
     * Returns true if the block at the given coordinate has a solid (buildable) top surface.
     */
    @Environment(EnvType.CLIENT)
    boolean doesBlockHaveSolidTopSurface(int var1, int var2, int var3);

    /**
     * Return the Vec3Pool object for this world.
     */
    Vec3Pool getWorldVec3Pool();

    /**
     * Is this block powering in the specified direction Args: x, y, z, direction
     */
    int isBlockProvidingPowerTo(int var1, int var2, int var3, int var4);
    
    /**
     * Not a real method, this is used to change the generated code to force the build process to export this class
     */
    default void editClass() {}
}
