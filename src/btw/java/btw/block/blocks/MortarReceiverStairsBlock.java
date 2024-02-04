// FCMOD

package btw.block.blocks;

import btw.client.fx.BTWEffectManager;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

public abstract class MortarReceiverStairsBlock extends FallingStairsBlock
{
    protected MortarReceiverStairsBlock(int iBlockID, Block referenceBlock, int iReferenceBlockMetadata )
    {
        super( iBlockID, referenceBlock, iReferenceBlockMetadata );
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

            world.scheduleBlockUpdate( i, j, k, blockID, FallingBlock.TACKY_FALLING_BLOCK_TICK_RATE);
    	}
    	else
    	{
    		scheduleCheckForFall(world, i, j, k);
    	}
    }
    
    @Override
    protected int validateMetadataForLocation(World world, int i, int j, int k, int iMetadata)
    {
    	// force stairs to be right side up with there's no mortar supporting them
    	
    	if ( getIsUpsideDown(iMetadata) )
    	{
    		int iFacing = convertDirectionToFacing(getDirection(iMetadata));
    		
    		if ( !hasNeighborWithMortarInContact(world, i, j, k, iFacing, true) )
    		{
    			iMetadata = setIsUpsideDown(iMetadata, false);
    		}
    	}
    	
    	return iMetadata;
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//
}
