// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Block;
import net.minecraft.src.EntityFallingSand;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.World;

import java.util.Random;

public class FallingStairsBlock extends StairsBlock
{
    protected FallingStairsBlock(int iBlockID, Block referenceBlock, int iReferenceBlockMetadata )
    {
    	super( iBlockID, referenceBlock, iReferenceBlockMetadata );
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
        if ( !checkForFall(world, i, j, k) )
        {
        	if ( getIsUpsideDown(world, i, j, k) )
        	{
        		setIsUpsideDown(world, i, j, k, false);
        	}
        }
    }
    
    @Override
    public int tickRate( World par1World )
    {
		return FallingBlock.FALLING_BLOCK_TICK_RATE;
    }
    
    protected void onStartFalling( EntityFallingSand entity )
    {
    	entity.metadata = setIsUpsideDown(entity.metadata, false);
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void renderFallingBlock(RenderBlocks renderBlocks, int i, int j, int k, int iMetadata)
    {
    	renderBlocks.setRenderAllFaces(true);
    	
        renderBlocks.setRenderBounds(getBoundsFromPoolForBase(iMetadata));
        renderBlocks.renderStandardFallingBlock(this, i, j, k, iMetadata);
        
        renderBlocks.setRenderBounds(getBoundsFromPoolForSecondaryPiece(iMetadata));
        renderBlocks.renderStandardFallingBlock(this, i, j, k, iMetadata);
        
    	renderBlocks.setRenderAllFaces(false);
    }
}
