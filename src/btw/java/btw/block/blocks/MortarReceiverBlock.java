// FCMOD

package btw.block.blocks;

import btw.client.fx.BTWEffectManager;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Material;
import net.minecraft.src.World;

public abstract class MortarReceiverBlock extends FallingFullBlock
{
    public MortarReceiverBlock(int iBlockID, Material material )
    {
    	super( iBlockID, material );
    }
    
    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata)
    {
        dropBlockAsItem( world, i, j, k, iMetadata, 0 );
    }
    
    @Override
    public void onBlockAdded( World world, int i, int j, int k ) 
    {
    	if ( hasNeighborWithMortarInContact(world, i, j, k) )
    	{
	        world.playAuxSFX( BTWEffectManager.LOOSE_BLOCK_STUCK_TO_MORTAR_EFFECT_ID, i, j, k, 0 );

            world.scheduleBlockUpdate(i, j, k, blockID, TACKY_FALLING_BLOCK_TICK_RATE);
    	}
    	else
    	{
    		scheduleCheckForFall(world, i, j, k);
    	}
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//    
}
