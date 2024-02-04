// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class CactusBlock extends BlockCactus
{
    public CactusBlock(int iBlockID)
    {
    	super( iBlockID );
    	
        setAxesEffectiveOn(true);
    	setHardness( 0.4F );
        
        setBuoyant();
        
        setStepSound( soundClothFootstep );
        
        setUnlocalizedName( "cactus" );
    }

    @Override
    public void updateTick(World world, int i, int j, int k, Random rand )
    {
    	// prevent growth in the end dimension
    	
    	if ( world.provider.dimensionId != 1 )
    	{
    		super.updateTick( world, i, j, k, rand );
    	}
    }
    
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
    	// only allow replanting onto planters
    	
    	if (super.canPlaceBlockAt(world, x, y, z)) {
    		int blockBelowID = world.getBlockId(x, y - 1, z);
    		
    		return blockBelowID == BTWBlocks.planter.blockID ||
    			blockBelowID == BTWBlocks.planterWithSoil.blockID ||
    			blockBelowID == this.blockID;
    	}
    	
    	return false;
    }
    
    @Override
    public boolean canBlockStay( World world, int i, int j, int k )
    {
        if (canStayNextToBlock(world, i - 1, j, k) &&
            canStayNextToBlock(world, i + 1, j, k) &&
            canStayNextToBlock(world, i, j, k - 1) &&
            canStayNextToBlock(world, i, j, k + 1) )
        {
            int iBlockBelowID = world.getBlockId( i, j - 1, k );
            Block blockBelow = Block.blocksList[iBlockBelowID];
            
            return iBlockBelowID == blockID || ( blockBelow != null && 
            	blockBelow.canCactusGrowOnBlock(world, i, j - 1, k) );
        }
        
        return false;
    }
    
    @Override
    public void onStruckByLightning(World world, int i, int j, int k)
    {
    	world.setBlockToAir( i, j, k );
    	
        world.playAuxSFX( BTWEffectManager.CACTUS_EXPLOSION_EFFECT_ID, i, j, k, 0 );
        
        if ( world.getBlockId( i, j - 1, k ) == blockID )
        {
        	// relay the strike downwards to other cactus blocks
        	
        	onStruckByLightning(world, i, j - 1, k);
        }
    }
    
    //------------- Class Specific Methods ------------//

    protected boolean canStayNextToBlock(World world, int i, int j, int k)
    {
    	return !world.getBlockMaterial( i, j, k ).isSolid() || 
    		world.getBlockId( i, j, k ) == BTWBlocks.web.blockID;
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
    	return renderer.renderBlockCactus( this, i, j, k );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int i, int j, int k, int iSide )
    {
    	if ( iSide == 0 || iSide == 1 )
    	{
    		return !( blockAccess.isBlockOpaqueCube(i, j, k) || blockAccess.getBlockId( i, j, k ) == Block.cactus.blockID );
    	}
    	
    	return true;
    }
}
