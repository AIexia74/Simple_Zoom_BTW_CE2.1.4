// FCMOD

package btw.block;

import net.minecraft.src.World;

public interface FluidSource
{
	/*
	 * Returns the height level of the source (0 to 8) if a valid source for the fluid block, -1 otherwise
	 */
	public int isSourceToFluidBlockAtFacing(World world, int i, int j, int k, int iFacing);
}