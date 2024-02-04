// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Block;
import net.minecraft.src.BlockNetherStalk;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.World;

import java.util.Random;

public class NetherWartBlock extends BlockNetherStalk
{
    public NetherWartBlock(int iBlockID)
    {
    	super( iBlockID );
    	
        initBlockBounds(0D, 0D, 0D, 1D, 0.25F, 1D);
        
        setStepSound( Block.soundGrassFootstep );
    }
    
    @Override
    public boolean canBlockStay(World world, int i, int j, int k )
    {
        return canGrowOnBlock(world, i, j - 1, k);
    }
    
    @Override
    public void updateTick( World world, int i, int j, int k, Random rand )
    {
        // prevent growth outside of the nether
        
        if ( world.provider.dimensionId != -1 )
        {
        	// replicate update functionality two levels above us in the hierarchy
        	
            checkFlowerChange( world, i, j, k );
        }
        else
        {        
        	super.updateTick( world, i, j, k, rand );
        }
    }
    
    @Override
    protected boolean canGrowOnBlock(World world, int i, int j, int k)
    {
    	Block blockOn = Block.blocksList[world.getBlockId( i, j, k )];
    	
    	return blockOn != null && blockOn.canNetherWartGrowOnBlock(world, i, j, k);
    }
    @Override
    public boolean attemptToAffectBlockWithSoul(World world, int x, int y, int z) {
        int iTargetMetadata = world.getBlockMetadata(x, y, z);
    
        if (iTargetMetadata < 3) {
            world.setBlockMetadataWithNotify(x, y, z, 3);
    
            world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
        
            return true;
        }
        return false;
    }
    
    //------------- Class Specific Methods ------------//

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
    	return renderer.renderBlockCrops( this, i, j, k );
    }    
}
