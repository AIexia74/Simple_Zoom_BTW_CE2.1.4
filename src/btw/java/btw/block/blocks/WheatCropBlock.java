// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class WheatCropBlock extends DailyGrowthCropsBlock
{
    public WheatCropBlock(int iBlockID)
    {
    	super( iBlockID );    	
        
        setUnlocalizedName( "fcBlockWheatCrop" );
    }
    
	@Override
    protected int getSeedItemID()
    {
        return 0;
    }

	@Override
    protected int getCropItemID()
    {
        return BTWItems.straw.itemID;
    }

	@Override
	public boolean isBlockHydratedForPlantGrowthOn(World world, int i, int j, int k)
	{
		// relays hydration from soil to top block
		
    	Block blockBelow = Block.blocksList[world.getBlockId( i, j - 1, k )];
    	
    	return blockBelow != null && 
    		blockBelow.isBlockHydratedForPlantGrowthOn(world, i, j - 1, k);
	}
	
	@Override
    protected void incrementGrowthLevel(World world, int i, int j, int k)
    {
    	int iGrowthLevel = getGrowthLevel(world, i, j, k);
    	
    	if ( iGrowthLevel == 6 )
    	{
    		if ( world.isAirBlock( i, j + 1, k ) )
    		{
    			// can only grow to last stage, and into top block if it is empty
    			// intentionally don't notify block below of full stage growth, as that only
    			// occurs on top block
    			
    	        setGrowthLevel(world, i, j, k, iGrowthLevel + 1);

    	    	int iTopMetadata = 0;    	    	
    	        Block blockBelow = Block.blocksList[world.getBlockId( i, j - 1, k )];
    	        
    	        if ( blockBelow == null || 
    	        	!blockBelow.getIsFertilizedForPlantGrowth(world, i, j - 1, k) )
    	        {
    	        	iTopMetadata = BTWBlocks.wheatCrop.setHasGrownToday(
    	        		iTopMetadata, true);
    	        }
    	    	
        		world.setBlockAndMetadataWithNotify( i, j + 1, k, 
        			BTWBlocks.wheatCropTop.blockID, iTopMetadata );
    		}
    	}
    	else
    	{
        	super.incrementGrowthLevel(world, i, j, k);
    	}    	
    }
    
    @Override
    public float groundCoverRestingOnVisualOffset(IBlockAccess blockAccess, int i, int j, int k)
    {
    	// relay offset to top block
    	
    	Block blockBelow = Block.blocksList[blockAccess.getBlockId( i, j - 1, k )];
    	
    	if ( blockBelow != null )
    	{
        	return blockBelow.groundCoverRestingOnVisualOffset(blockAccess, i, j - 1, k);
    	}
    	
    	return 0F;
    }
    
	@Override
	public boolean getIsFertilizedForPlantGrowth(World world, int i, int j, int k)
	{
		// relays to soil from top block
		
    	Block blockBelow = Block.blocksList[world.getBlockId( i, j - 1, k )];
    	
    	return blockBelow != null && blockBelow.getIsFertilizedForPlantGrowth(world, i, j - 1, k);
	}
	
	@Override
	public void notifyOfFullStagePlantGrowthOn(World world, int i, int j, int k, Block plantBlock)
	{
		// relays to soil from top block
		
    	Block blockBelow = Block.blocksList[world.getBlockId( i, j - 1, k )];

    	if ( blockBelow != null )
    	{
    		blockBelow.notifyOfFullStagePlantGrowthOn(world, i, j - 1, k, plantBlock);
    	}
	}
    
	@Override
    protected int getLightLevelForGrowth() {
    	return 11;
    }
	
    //------------- Class Specific Methods ------------//
    
    public boolean hasTopBlock(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return blockAccess.getBlockId( i, j + 1, k ) == 
    		BTWBlocks.wheatCropTop.blockID;
    }

    /**
     * Assumes the block above is a wheat top
     */
    public int getTopBlockGrowthLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return BTWBlocks.wheatCropTop.getGrowthLevel(blockAccess, i, j + 1, k);
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconArray;
    @Environment(EnvType.CLIENT)
    private Icon[] connectToTopIconArray;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        iconArray = new Icon[8];

        for (int iTempIndex = 0; iTempIndex < iconArray.length; iTempIndex++ )
        {
            iconArray[iTempIndex] = register.registerIcon("fcBlockWheatCrop_" + iTempIndex);
        }
        
        blockIcon = iconArray[7]; // for block hit effects and item render

        connectToTopIconArray = new Icon[4];

        for (int iTempIndex = 0; iTempIndex < connectToTopIconArray.length; iTempIndex++ )
        {
            connectToTopIconArray[iTempIndex] = register.registerIcon("fcBlockWheatCrop_7_" +
                                                                      iTempIndex);
        }        
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture( IBlockAccess blockAccess, int i, int j, int k, int iSide )
    {
    	int iGrowthLevel = getGrowthLevel(blockAccess, i, j, k);
    	
    	iGrowthLevel = MathHelper.clamp_int( iGrowthLevel, 0, 7 );
    	
        if ( iGrowthLevel == 7 && hasTopBlock(blockAccess, i, j, k) )
        {
            int iTopGrowthLevel = getTopBlockGrowthLevel(blockAccess, i, j, k);
            
            iTopGrowthLevel = MathHelper.clamp_int( iTopGrowthLevel, 0, 3 );
            
            return connectToTopIconArray[iTopGrowthLevel];
        }
        else
        {
        	return iconArray[iGrowthLevel];
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		// combine bounds with top block
		if (isFullyGrown(world, x, y, z) && hasTopBlock(world, x, y, z)) {
			int topGrowthLevel = getTopBlockGrowthLevel(world, x, y, z);
		
			float verticalOffset = 0;
			Block blockBelow = Block.blocksList[world.getBlockId(x, y - 1, z)];
		
			if (blockBelow != null) {
				verticalOffset = blockBelow.groundCoverRestingOnVisualOffset(world, x, y - 1, z);
			}
		
			double topHeight = (1 + topGrowthLevel) / 8D;
		
			return AxisAlignedBB.getAABBPool().getAABB(0.5D - BOUNDS_HALF_WIDTH, 0D + verticalOffset, 0.5D - BOUNDS_HALF_WIDTH,
					0.5D + BOUNDS_HALF_WIDTH, 1D + verticalOffset + topHeight, 0.5D + BOUNDS_HALF_WIDTH).offset(x, y, z);
		}
	
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}
}
