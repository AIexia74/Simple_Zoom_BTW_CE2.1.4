// FCMOD

package btw.block.blocks;

import net.minecraft.src.Block;
import net.minecraft.src.Material;
import net.minecraft.src.World;

import java.util.Random;

public class FallingBlock extends Block
{
	public static final int FALLING_BLOCK_TICK_RATE = 2;
	public static final int TACKY_FALLING_BLOCK_TICK_RATE = 40;
	
    public FallingBlock(int iBlockID, Material material )
    {
    	super( iBlockID, material );
    }
    
    @Override
    public boolean isFallingBlock()
    {
    	return true;
    }
    
    @Override
    public void onBlockAdded(World world, int i, int j, int k )
    {
    	scheduleCheckForFall(world, i, j, k);
    }
    
    @Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iNeighborBlockID ) 
    {    	
    	scheduleCheckForFall(world, i, j, k);
    }

    @Override
    public void updateTick( World world, int i, int j, int k, Random rand ) 
    {    	
        checkForFall(world, i, j, k);
    }
    
    @Override
    public int tickRate( World par1World )
    {
		return FALLING_BLOCK_TICK_RATE;
    }
}
