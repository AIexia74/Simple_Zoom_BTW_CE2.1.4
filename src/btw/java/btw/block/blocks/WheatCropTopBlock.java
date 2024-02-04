// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class WheatCropTopBlock extends DailyGrowthCropsBlock
{
    public WheatCropTopBlock(int iBlockID)
    {
    	super( iBlockID );    	
        
        setUnlocalizedName( "fcBlockWheatCropTop" );
    }
    
	@Override
    public boolean doesBlockDropAsItemOnSaw(World world, int i, int j, int k)
    {
		return true;
    }
	
	@Override
    protected int getSeedItemID()
    {
        return 0;
    }

	@Override
    protected int getCropItemID()
    {
        return BTWItems.wheat.itemID;
    }

	@Override
    protected boolean isFullyGrown(int iMetadata)
    {
    	return getGrowthLevel(iMetadata) >= 3;
    }
	
	@Override
    protected boolean canGrowOnBlock(World world, int i, int j, int k)
    {
    	return world.getBlockId( i, j, k ) == BTWBlocks.wheatCrop.blockID;
    }
    
    @Override
	public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
		// doesn't take weed height into account
		float verticalOffset = 0;
		Block blockBelow = Block.blocksList[blockAccess.getBlockId(x, y - 1, z)];
	
		if (blockBelow != null) {
			verticalOffset = blockBelow.groundCoverRestingOnVisualOffset(blockAccess, x, y - 1, z);
		}
	
		int metadata = blockAccess.getBlockMetadata(x, y, z);
		
		int growthLevel = getGrowthLevel(metadata);
	
		return AxisAlignedBB.getAABBPool()
				.getAABB(0.5D - BOUNDS_HALF_WIDTH, verticalOffset - 1, 0.5D - BOUNDS_HALF_WIDTH,
						0.5D + BOUNDS_HALF_WIDTH, (1 + growthLevel) / 8D + verticalOffset, 0.5D + BOUNDS_HALF_WIDTH);
	}
    
	@Override
	public void breakBlock(World world, int x, int y, int z, int blockID, int metadata) {
		super.breakBlock(world, x, y, z, blockID, metadata);
		
		int blockBelowID = world.getBlockId(x, y - 1, z);
		
		// Remove the bottom block as well when broken
		if (blockBelowID == BTWBlocks.wheatCrop.blockID) {
			int blockBelowMetadata = world.getBlockMetadata(x, y - 1, z);
			
			if (isFullyGrown(metadata)) {
				BTWBlocks.wheatCrop.dropBlockAsItemWithChance(world, x, y, z, blockBelowMetadata, 1, 0);
			}
			
			world.setBlockToAir(x, y - 1, z);
		}
	}

	@Override
	protected void updateFlagForGrownToday(World world, int i, int j, int k)
	{
    	// fertilized crops can grow twice in a day
		
        Block blockBelow = Block.blocksList[world.getBlockId( i, j - 1, k )];
        
        if ( blockBelow != null )
        {
        	// inverted growth level staggering from parent to maintain overall count
        	
	    	if (!blockBelow.getIsFertilizedForPlantGrowth(world, i, j - 1, k) ||
                getGrowthLevel(world, i, j, k) % 2 == 1 )
	    	{
	    		setHasGrownToday(world, i, j, k, true);
	    	}
        }
	}
	
    //------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconArray;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        iconArray = new Icon[4];

        for (int iTempIndex = 0; iTempIndex < iconArray.length; iTempIndex++ )
        {
            iconArray[iTempIndex] = register.registerIcon("fcBlockWheatCropTop_" + iTempIndex);
        }
        
        blockIcon = iconArray[3]; // for block hit effects and item render
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture( IBlockAccess blockAccess, int i, int j, int k, int iSide )
    {
    	int iGrowthLevel = getGrowthLevel(blockAccess, i, j, k);
    	
    	iGrowthLevel = MathHelper.clamp_int( iGrowthLevel, 0, 3 );
    	
    	return iconArray[iGrowthLevel];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
    	// don't render weeds on top block
    	
    	renderCrops(renderer, i, j, k);
		
    	return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool( World world, int i, int j, int k )
    {
    	// combine bounds with bottom block unless fully grown
    	
    	if ( !isFullyGrown(world, i, j, k) )
    	{
	        Block blockBelow = Block.blocksList[world.getBlockId( i, j - 1, k )];
	        
	        if ( blockBelow != null )
	        {
	        	return blockBelow.getSelectedBoundingBoxFromPool( world, i, j - 1, k );
	        }
    	}
    	
        return super.getSelectedBoundingBoxFromPool( world, i, j, k );
    }
}
