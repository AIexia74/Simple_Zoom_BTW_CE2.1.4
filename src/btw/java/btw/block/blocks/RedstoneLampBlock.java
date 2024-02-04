// FCMOD

package btw.block.blocks;

import net.minecraft.src.Block;
import net.minecraft.src.BlockRedstoneLight;
import net.minecraft.src.World;

import java.util.Random;

public class RedstoneLampBlock extends BlockRedstoneLight
{
	// copy of parent variable to get around private declaration
	
    private final boolean powered;

    public RedstoneLampBlock(int iBlockID, boolean bIsLit )
    {
    	super( iBlockID, bIsLit );
    	
    	setPicksEffectiveOn(true);
    	
    	powered = bIsLit;
    }

    @Override
    protected boolean canSilkHarvest()
    {
        return false;
    }
    
    @Override
    public void onBlockAdded(World world, int i, int j, int k)
    {
    	// copy of parent function in order to provide block notifications on setBlock()
    	
        if ( !world.isRemote )
        {
            if ( powered && !world.isBlockIndirectlyGettingPowered( i, j, k ) )
            {
                world.scheduleBlockUpdate( i, j, k, blockID, 4 );
            }
            else if ( !powered && world.isBlockIndirectlyGettingPowered( i, j, k ) )
            {
                world.setBlock( i, j, k, Block.redstoneLampActive.blockID );
            }
        }
    }
    
    @Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iNeighborBlockID )
    {
    	// copy of parent function in order to provide block notifications on setBlock()
    	// and to check for already pending updates.
    	
        if ( powered )
        {
        	if ( !world.isBlockIndirectlyGettingPowered( i, j, k ) &&
        		!world.isUpdatePendingThisTickForBlock(i, j, k, blockID) )
        	{
        		world.scheduleBlockUpdate( i, j, k, blockID, 4 );
        	}
        }
        else 
        {
        	if ( world.isBlockIndirectlyGettingPowered( i, j, k ) )
        	{
        		world.setBlockWithNotify( i, j, k, Block.redstoneLampActive.blockID );
        	}
        }
    }
    
    @Override
    public void updateTick( World world, int i, int j, int k, Random rand )
    {
    	// copy of parent function in order to provide block notifications on setBlock()
    	
        if ( !world.isRemote && powered && !world.isBlockIndirectlyGettingPowered( i, j, k ) )
        {
            world.setBlock( i, j, k, Block.redstoneLampIdle.blockID );
        }
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
