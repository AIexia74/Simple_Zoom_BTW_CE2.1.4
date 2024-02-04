// FCMOD

package btw.block.blocks.legacy;

import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class LegacySugarCaneBlock extends BlockReed
{
    private static final double WIDTH = 0.75D;
    private static final double HALF_WIDTH = (WIDTH / 2D );
    
    public LegacySugarCaneBlock(int iBlockID)
    {
    	super( iBlockID );
    	
        initBlockBounds(0.5D - HALF_WIDTH, 0F, 0.5D - HALF_WIDTH,
                        0.5D + HALF_WIDTH, 1F, 0.5D + HALF_WIDTH);
    }
    
    @Override
    public void updateTick(World world, int i, int j, int k, Random rand )
    {
    	// override to reduce growth rate and prevent growth in the end dimension
    	
    	if ( world.provider.dimensionId != 1 )
    	{
            if ( rand.nextInt( 2 ) == 0 && world.isAirBlock( i, j + 1, k ) )
            {
                int iReedHeight = 1;

                while ( world.getBlockId( i, j - iReedHeight, k ) == blockID )
                {
                	iReedHeight++;
                }

                if ( iReedHeight < 3 )
                {
                    int iMetadata = world.getBlockMetadata( i, j, k );

                    if ( iMetadata == 15 )
                    {
                        world.setBlock( i, j + 1, k, blockID );
                        
                        world.SetBlockMetadataWithNotify( i, j, k, 0, 4 );
                    }
                    else
                    {
                        world.SetBlockMetadataWithNotify( i, j, k, iMetadata + 1, 4 );
                    }
                }
            }
    	}
    }
    
    @Override
    public void dropBlockAsItemWithChance( World world, int i, int j, int k, int iMetadata, float fChance, int iFortuneModifier )
    {
    	int idBelow = world.getBlockId(i, j - 1, k);
    	
    	if (idBelow == Block.reed.blockID || idBelow == 0) {
    		super.dropBlockAsItemWithChance( world, i, j, k, iMetadata, fChance, iFortuneModifier );
    	}
    	else if (!world.isRemote) {
    		dropItemsIndividually(world, i, j, k, BTWItems.sugarCaneRoots.itemID, 1, 0, 1);
    	}
    }
    
    @Override
    public boolean canPlaceBlockAt( World world, int i, int j, int k )
    {
    	if (world.getBlockMaterial(i, j, k) == Material.water)
    		return false;
    	
        int blockBelowID = world.getBlockId( i, j - 1, k );
        Block blockBelow = Block.blocksList[blockBelowID];

    	return blockBelowID == blockID || (blockBelow != null &&
                                           blockBelow.canReedsGrowOnBlock(world, i, j - 1, k) &&
                                           blockBelow.isConsideredNeighbouringWaterForReedGrowthOn(world, i, j - 1, k));
    }
    
    @Override
    public void onEntityCollidedWithBlock( World world, int i, int j, int k, Entity entity )
    {
    	if (entity.isAffectedByMovementModifiers() && entity.onGround )
    	{    		
	        entity.motionX *= 0.8D;
	        entity.motionZ *= 0.8D;
    	}
    }    
    
	@Override
    public boolean doesBlockDropAsItemOnSaw(World world, int i, int j, int k)
    {
		return true;
    }

    @Override
	public boolean getPreventsFluidFlow(World world, int i, int j, int k, Block fluidBlock)
	{
    	return false;
	}
    
    @Override
    public boolean canBeGrazedOn(IBlockAccess blockAccess, int i, int j, int k,
                                 EntityAnimal animal)
    {
		return animal.canGrazeOnRoughVegetation();
    }

    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
    	return renderer.renderCrossedSquares( this, i, j, k );
    }    
}
