// FCMOD

package btw.world.util;

import net.minecraft.src.Facing;

public class BlockPos
{
	public int x, y, z;
	
    public BlockPos()
    {
		x = y = z = 0;
    }
    
    public BlockPos(int x, int y, int z )
    {
		this.x = x;
		this.y = y;
		this.z = z;
    }
    
    public BlockPos(int baseX, int baseY, int baseZ, int facing )
    {
    	this( baseX, baseY, baseZ );
    	
    	addFacingAsOffset(facing);
    }
    
    public void addFacingAsOffset(int facing)
    {
		x += Facing.offsetsXForSide[facing];
		y += Facing.offsetsYForSide[facing];
		z += Facing.offsetsZForSide[facing];
    }
    
    public void invert()
    {
		x = -x;
		y = -y;
		z = -z;
    }
    
    public void addPos(BlockPos pos)
    {
		x += pos.x;
		y += pos.y;
		z += pos.z;
    }
    
    public void set(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
    }
    
    public void set(BlockPos pos)
    {
		x = pos.x;
		y = pos.y;
		z = pos.z;
    }    
}
