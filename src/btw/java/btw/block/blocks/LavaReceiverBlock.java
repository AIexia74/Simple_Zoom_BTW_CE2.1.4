// FCMOD

package btw.block.blocks;

import btw.client.fx.BTWEffectManager;
import btw.client.render.util.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public abstract class LavaReceiverBlock extends MortarReceiverBlock
{
	public static final int LAVA_FILL_TICK_RATE = 20;
	public static final int LAVA_HARDEN_TICK_RATE = 2;
	
    public LavaReceiverBlock(int iBlockID, Material material )
    {
        super( iBlockID, material );
        
        setTickRandomly( true );
    }
    
    @Override
    public void onBlockAdded(World world, int i, int j, int k )
    {
    	if ( !scheduleUpdatesForLavaAndWaterContact(world, i, j, k) )
    	{
    		super.onBlockAdded( world, i, j, k );
    	}    	
    }
    
    @Override
    public void updateTick( World world, int i, int j, int k, Random rand ) 
    {
    	if ( !checkForFall(world, i, j, k) )
    	{
	    	if ( getHasLavaInCracks(world, i, j, k) )
	    	{
	    		if ( hasWaterAbove(world, i, j, k) )
	    		{
	                world.playAuxSFX( BTWEffectManager.FIRE_FIZZ_EFFECT_ID, i, j, k, 0 );
	                
	    			world.setBlockAndMetadataWithNotify( i, j, k, Block.stone.blockID, getStrata(world, i, j, k) );
	    			
	    			return;
	    		}
	    	}
	    	else if ( hasLavaAbove(world, i, j, k) )
	    	{
	            setHasLavaInCracks(world, i, j, k, true);
	    	}
    	}
    }
    
	@Override
    public void randomUpdateTick(World world, int i, int j, int k, Random rand)
    {
    	if ( getHasLavaInCracks(world, i, j, k) )
    	{
	        if ( world.isRainingAtPos(i, j + 1, k) )
	        {
	        	world.playAuxSFX( BTWEffectManager.FIRE_FIZZ_EFFECT_ID, i, j, k, 0 );
            
	        	world.setBlockAndMetadataWithNotify( i, j, k, Block.stone.blockID, getStrata(world, i, j, k));
	        }
    	}
    }
	
    @Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iNeighborBlockID ) 
    {
    	if ( !scheduleUpdatesForLavaAndWaterContact(world, i, j, k) )
    	{    	
    		super.onNeighborBlockChange( world, i, j, k, iNeighborBlockID );
    	}
    }
    
    @Override
    protected boolean canSilkHarvest()
    {
    	// prevent havest of version with lava in cracks
    	
        return false;
    }
    
    @Override
    public boolean getIsBlockWarm(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getHasLavaInCracks(blockAccess, i, j, k);
    }
    
    //------------- Class Specific Methods ------------//
    
	/**
	 * returns 0 - 2 regardless of what metadata is used to store strata. 
	 * BEWARE: different blocks store strata differently
	 */
	public int getStrata(IBlockAccess blockAccess, int i, int j, int k) {
		return getStrata(blockAccess.getBlockMetadata(i, j, k));
	}

	/**
	 * returns 0 - 2 regardless of what metadata is used to store strata. 
	 * BEWARE: different blocks store strata differently
	 */
	public int getStrata(int iMetadata) {
		return (iMetadata & 12) >>> 2;
	}
    
    protected boolean getHasLavaInCracks(int iMetadata)
    {
    	return ( iMetadata & 1 ) != 0;
    }
    
    protected boolean getHasLavaInCracks(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getHasLavaInCracks(blockAccess.getBlockMetadata(i, j, k));
    }
    
    protected int setHasLavaInCracks(int iMetadata, boolean bHasLava)
    {
    	if ( bHasLava )
    	{
    		iMetadata |= 1;
    	}
    	else
    	{
    		iMetadata &= (~1);
    	}
    	
    	return iMetadata;
    }
    
    protected void setHasLavaInCracks(World world, int i, int j, int k, boolean bHasLava)
    {
    	int iMetadata =  setHasLavaInCracks(world.getBlockMetadata(i, j, k), bHasLava);
    	
    	world.setBlockMetadataWithNotify( i, j, k, iMetadata );
    }    	
    
    protected boolean hasLavaAbove(IBlockAccess blockAccess, int i, int j, int k)
    {
		Block blockAbove = Block.blocksList[blockAccess.getBlockId( i, j + 1, k )];
		
		return blockAbove != null && blockAbove.blockMaterial == Material.lava;
    }
    
    protected boolean hasWaterAbove(IBlockAccess blockAccess, int i, int j, int k)
    {
		Block blockAbove = Block.blocksList[blockAccess.getBlockId( i, j + 1, k )];
		
		return blockAbove != null && blockAbove.blockMaterial == Material.water;
    }
    
    protected boolean scheduleUpdatesForLavaAndWaterContact(World world, int i, int j, int k)
    {
    	if ( getHasLavaInCracks(world, i, j, k) )
    	{
    		if ( hasWaterAbove(world, i, j, k) )
    		{
    			if ( !world.isUpdatePendingThisTickForBlock(i, j, k, blockID) )
    			{
    				world.scheduleBlockUpdate(i, j, k, blockID, LAVA_HARDEN_TICK_RATE);
    			}
                
                return true;
    		}
    	}
    	else if ( hasLavaAbove(world, i, j, k) )
    	{
			if ( !world.isUpdatePendingThisTickForBlock(i, j, k, blockID) )
			{
				world.scheduleBlockUpdate(i, j, k, blockID, LAVA_FILL_TICK_RATE);
			}
            
            return true;
    	}
    	
    	return false;
    }
    
    //------------ Client Side Functionality ----------//

    @Environment(EnvType.CLIENT)
    protected abstract Icon getLavaCracksOverlay();

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks renderBlocks, int i, int j, int k, boolean bFirstPassResult)
    {
    	if (bFirstPassResult && getHasLavaInCracks(renderBlocks.blockAccess, i, j, k) )
    	{
	        RenderUtils.renderBlockFullBrightWithTexture(renderBlocks,
                                                         renderBlocks.blockAccess, i, j, k, getLavaCracksOverlay());
    	}
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderFallingBlock(RenderBlocks renderBlocks, int i, int j, int k, int iMetadata)
    {
    	renderBlocks.setRenderAllFaces(true);
    	
        renderBlocks.setRenderBounds(getFixedBlockBoundsFromPool());
        
        renderBlocks.renderStandardFallingBlock(this, i, j, k, iMetadata);
        
    	if ( getHasLavaInCracks(iMetadata) )
    	{
    		RenderUtils.renderBlockFullBrightWithTexture(renderBlocks,
                                                         renderBlocks.blockAccess, i, j, k, getLavaCracksOverlay());
    	}
        
    	renderBlocks.setRenderAllFaces(false);
    }
}