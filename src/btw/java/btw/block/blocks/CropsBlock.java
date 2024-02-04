// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public abstract class CropsBlock extends PlantsBlock
{
	static protected final double BOUNDS_WIDTH = (1D - (4D / 16D ) );
	static protected final double BOUNDS_HALF_WIDTH = (BOUNDS_WIDTH / 2D );
	
    protected CropsBlock(int iBlockID )
    {
        super( iBlockID, Material.plants );
        
        setHardness( 0F );
        setBuoyant();
		setFireProperties(Flammability.CROPS);
        
        initBlockBounds(0.5D - BOUNDS_HALF_WIDTH, 0D, 0.5D - BOUNDS_HALF_WIDTH,
                        0.5D + BOUNDS_HALF_WIDTH, 1D, 0.5D + BOUNDS_HALF_WIDTH);
        
        setStepSound( Block.soundGrassFootstep );
        
        setTickRandomly( true );
        
        disableStats();
    }

    @Override
    public void onNeighborBlockChange(World world, int i, int j, int k, int iNeighborBlockID )
    {
        super.onNeighborBlockChange( world, i, j, k, iNeighborBlockID );
        
        updateIfBlockStays(world, i, j, k);
    }
	
	@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float chance, int fortuneModifier) {
		if (world.isRemote) {
			return;
		}
		if (onlyDropWhenFullyGrown() && !isFullyGrown(metadata)) {
			return;
		}
		
		super.dropBlockAsItemWithChance(world, x, y, z, metadata, chance, 0);
		
		dropSeeds(world, x, y, z, metadata);
	}
	
	protected boolean onlyDropWhenFullyGrown() {
		return true;
	}

    @Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
    {
    	if ( isFullyGrown(iMetadata) )
    	{
    		return getCropItemID();
    	}
    	
        return 0;
    }
    
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
        float dVerticalOffset = 0;
        Block blockBelow = Block.blocksList[blockAccess.getBlockId( i, j - 1, k )];
        
        if ( blockBelow != null )
        {
        	dVerticalOffset = blockBelow.groundCoverRestingOnVisualOffset(
        		blockAccess, i, j - 1, k);
        }
        
        int iMetadata = blockAccess.getBlockMetadata( i, j, k );
        
        if ( isFullyGrown(iMetadata) )
        {
        	return AxisAlignedBB.getAABBPool().getAABB(
                    0.5D - BOUNDS_HALF_WIDTH, 0D + dVerticalOffset, 0.5D - BOUNDS_HALF_WIDTH,
                    0.5D + BOUNDS_HALF_WIDTH, 1D + dVerticalOffset, 0.5D + BOUNDS_HALF_WIDTH);
        }
        else
        {
        	int iGrowthLevel = getGrowthLevel(iMetadata);
        	double dBoundsHeight = ( 1 + iGrowthLevel ) / 8D;
    		
        	int iWeedsGrowthLevel = getWeedsGrowthLevel(blockAccess, i, j, k);
        	
        	if ( iWeedsGrowthLevel > 0 )
        	{
        		dBoundsHeight = Math.max( dBoundsHeight, 
        			WeedsBlock.getWeedsBoundsHeight(iWeedsGrowthLevel));
        	}
        	
        	return AxisAlignedBB.getAABBPool().getAABB(
                    0.5D - BOUNDS_HALF_WIDTH, 0F + dVerticalOffset, 0.5D - BOUNDS_HALF_WIDTH,
                    0.5D + BOUNDS_HALF_WIDTH, dBoundsHeight + dVerticalOffset,
                    0.5D + BOUNDS_HALF_WIDTH);
        }            
    }
	
    @Override
    public boolean canBeGrazedOn(IBlockAccess blockAccess, int i, int j, int k,
                                 EntityAnimal animal)
    {
		return true;
    }
    
    @Override
    public void onGrazed(World world, int i, int j, int k, EntityAnimal animal)
    {
    	// drop the block as an item so that animals can get the base graze value + eat
    	// any tasties that drop
    	
        dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
        
        super.onGrazed(world, i, j, k, animal);
    }
    
    @Override
    public void updateTick( World world, int i, int j, int k, Random rand )
    {
        if ( updateIfBlockStays(world, i, j, k) )
        {
        	// no plants can grow in the end
        	
	        if ( world.provider.dimensionId != 1 && !isFullyGrown(world, i, j, k) )
	        {
	        	attemptToGrow(world, i, j, k, rand);
	        }
        }
    }

	@Override
	public boolean canWeedsGrowInBlock(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}	
	
	@Override
	public boolean getConvertsLegacySoil(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;		
	}
	
    @Override
    protected boolean canGrowOnBlock(World world, int i, int j, int k)
    {
    	Block blockOn = Block.blocksList[world.getBlockId( i, j, k )];
    	
    	return blockOn != null && blockOn.canDomesticatedCropsGrowOnBlock(world, i, j, k);
    }
    
    //------------- Class Specific Methods ------------//
    
    protected abstract int getCropItemID();
    
    protected abstract int getSeedItemID();

    protected boolean updateIfBlockStays(World world, int i, int j, int k)
    {
        if ( !canBlockStay( world, i, j, k ) )
        {
            dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
            
            world.setBlockToAir( i, j, k );
            
            return false;
        }
        
        return true;
    }
    
    protected void attemptToGrow(World world, int x, int y, int z, Random rand) {
    	if (getWeedsGrowthLevel(world, x, y, z) == 0 && canGrowAtCurrentLightLevel(world, x, y, z)) {
	        Block blockBelow = Block.blocksList[world.getBlockId(x, y - 1, z)];
	        
	        if (blockBelow != null && blockBelow.isBlockHydratedForPlantGrowthOn(world, x, y - 1, z)) {
	    		float fGrowthChance = getBaseGrowthChance(world, x, y, z) *
                                      blockBelow.getPlantGrowthOnMultiplier(world, x, y - 1, z, this);
	    		
	            if (rand.nextFloat() <= fGrowthChance) {
	            	incrementGrowthLevel(world, x, y, z);
	            }
	        }
	    }
    }
    
    public void dropSeeds(World world, int i, int j, int k, int iMetadata)
    {
    	int iSeedItemID = getSeedItemID();
    	
    	if ( iSeedItemID > 0 )
    	{
	        dropBlockAsItem_do( world, i, j, k, new ItemStack( iSeedItemID, 1, 0 ) );
    	}
    }
    
    public float getBaseGrowthChance(World world, int i, int j, int k)
    {
    	return 0.05F;
    }
    
    protected void incrementGrowthLevel(World world, int i, int j, int k)
    {
    	int iGrowthLevel = getGrowthLevel(world, i, j, k) + 1;
    	
        setGrowthLevel(world, i, j, k, iGrowthLevel);
        
        if ( isFullyGrown(world, i, j, k) )
        {
        	Block blockBelow = Block.blocksList[world.getBlockId( i, j - 1, k )];
        	
        	if ( blockBelow != null )
        	{
        		blockBelow.notifyOfFullStagePlantGrowthOn(world, i, j - 1, k, this);
        	}
        }
    }
    
    protected int getGrowthLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getGrowthLevel(blockAccess.getBlockMetadata(i, j, k));
    }
    
    protected int getGrowthLevel(int iMetadata)
    {
    	return iMetadata & 7;
    }
    
    protected void setGrowthLevel(World world, int i, int j, int k, int iLevel)
    {
    	int iMetadata = world.getBlockMetadata( i, j, k ) & (~7); // filter out old level   	
    	
    	world.setBlockMetadataWithNotify( i, j, k, iMetadata | iLevel );
    }
    
    protected void setGrowthLevelNoNotify(World world, int i, int j, int k, int iLevel)
    {
    	int iMetadata = world.getBlockMetadata( i, j, k ) & (~7); // filter out old level   	
    	
    	world.setBlockMetadata( i, j, k, iMetadata | iLevel );
    }
    
    protected boolean isFullyGrown(World world, int i, int j, int k)
    {
    	return isFullyGrown(world.getBlockMetadata(i, j, k));
    }
    
    protected boolean isFullyGrown(int iMetadata)
    {
    	return getGrowthLevel(iMetadata) >= 7;
    }
    
    protected int getLightLevelForGrowth() {
    	return 9;
    }
    
    protected boolean canGrowAtCurrentLightLevel(World world, int x, int y, int z) {
    	if (this.requiresNaturalLight()) {
    		return world.getBlockNaturalLightValue(x, y, z) >= getLightLevelForGrowth() ||
					world.getBlockId( x, y + 1, z ) == BTWBlocks.lightBlockOn.blockID ||
					world.getBlockId( x, y + 2, z ) == BTWBlocks.lightBlockOn.blockID;
    	}
    	else {
    		return world.getBlockLightValue(x, y, z) >= getLightLevelForGrowth();
    	}
    }
    
    protected boolean requiresNaturalLight() {
    	return true;
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
    	renderCrops(renderer, i, j, k);
    	
    	BTWBlocks.weeds.renderWeeds(this, renderer, i, j, k);
		
    	return true;
    }

    @Environment(EnvType.CLIENT)
    protected void renderCrops(RenderBlocks renderer, int i, int j, int k)
    {
        Tessellator tessellator = Tessellator.instance;
        
        tessellator.setBrightness( getMixedBrightnessForBlock( renderer.blockAccess, i, j, k ) );
        tessellator.setColorOpaque_F( 1F, 1F, 1F );
        
        double dVerticalOffset = 0D;
        Block blockBelow = Block.blocksList[renderer.blockAccess.getBlockId( i, j - 1, k )];
        
        if ( blockBelow != null )
        {
        	dVerticalOffset = blockBelow.groundCoverRestingOnVisualOffset(
        		renderer.blockAccess, i, j - 1, k);
        }
        
        renderCrossHatch(renderer, i, j, k, getBlockTexture(renderer.blockAccess, i, j, k, 0),
        	4D / 16D, dVerticalOffset);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked( World world, int i, int j, int k )
    {
        return getSeedItemID();
    }
}
