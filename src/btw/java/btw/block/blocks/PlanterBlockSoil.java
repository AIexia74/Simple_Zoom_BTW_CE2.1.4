// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class PlanterBlockSoil extends PlanterBlockBase
{
    public PlanterBlockSoil(int iBlockID)
    {
    	super( iBlockID );
    	
        setUnlocalizedName( "fcBlockPlanterSoil" );     
    }
    
	@Override
    public void updateTick(World world, int i, int j, int k, Random rand )
    {
		boolean bHasIrrigation = hasIrrigatingBlocks(world, i, j, k) ||
								 world.isRainingAtPos(i, j + 1, k);
		
		if (bHasIrrigation != getIsHydrated(world, i, j, k) )
		{
			setIsHydrated(world, i, j, k, bHasIrrigation);
		}
    }
	
	@Override
	public boolean attemptToApplyFertilizerTo(World world, int i, int j, int k)
	{
		if ( !getIsFertilized(world, i, j, k) )
		{
			setIsFertilized(world, i, j, k, true);
			
			return true;
		}
		
		return false;
	}
	
	@Override
    public boolean canDomesticatedCropsGrowOnBlock(World world, int i, int j, int k)
    {
		return true;
    }
    
	@Override
    public boolean canReedsGrowOnBlock(World world, int i, int j, int k)
    {
		return true;
    }
    
	@Override
    public boolean canSaplingsGrowOnBlock(World world, int i, int j, int k)
    {
		return true;
    }
    
	@Override
    public boolean canWildVegetationGrowOnBlock(World world, int i, int j, int k)
    {
		return true;
    }
	
	@Override
    public boolean canCactusGrowOnBlock(World world, int i, int j, int k)
    {
		return true;
    }
	
	@Override
	public boolean isBlockHydratedForPlantGrowthOn(World world, int i, int j, int k)
	{
		return getIsHydrated(world, i, j, k);
	}
	
	@Override
	public boolean isConsideredNeighbouringWaterForReedGrowthOn(World world, int i, int j, int k)
	{
		return getIsHydrated(world, i, j, k);
	}
	
	@Override
	public boolean getIsFertilizedForPlantGrowth(World world, int i, int j, int k)
	{
		return getIsFertilized(world, i, j, k);
	}
	
	@Override
	public void notifyOfFullStagePlantGrowthOn(World world, int i, int j, int k, Block plantBlock)
	{
		if ( getIsFertilized(world, i, j, k) )
		{
			setIsFertilized(world, i, j, k, false);
		}
	}
	
    protected boolean hasIrrigatingBlocks(World world, int i, int j, int k)
    {
    	// planters can only be irrigated by direct neighbors

    	if ( world.getBlockMaterial( i, j - 1, k ) == Material.water ||
    		world.getBlockMaterial( i, j + 1, k ) == Material.water ||
    		world.getBlockMaterial( i, j, k - 1 ) == Material.water ||
    		world.getBlockMaterial( i, j, k + 1 ) == Material.water ||
    		world.getBlockMaterial( i - 1, j, k ) == Material.water ||
    		world.getBlockMaterial( i + 1, j, k ) == Material.water )
    	{
    		return true;
    	}
    	
    	return false;
    }
    
    //------------- Class Specific Methods ------------//
	
	protected boolean getIsHydrated(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getIsHydrated(blockAccess.getBlockMetadata(i, j, k));
	}
    
	protected boolean getIsHydrated(int iMetadata)
	{
		return ( iMetadata & 1 ) != 0;
	}
	
	protected void setIsHydrated(World world, int i, int j, int k, boolean bHydrated)
	{
		int iMetadata = setIsHydrated(world.getBlockMetadata(i, j, k), bHydrated);
		
		world.setBlockMetadataWithNotify( i, j, k, iMetadata );
	}
	
	protected int setIsHydrated(int iMetadata, boolean bHydrated)
	{
		if ( bHydrated)
		{
			iMetadata |= 1;
		}
		else
		{
			iMetadata &= (~1);
		}
		
		return iMetadata;
	}
	
	protected boolean getIsFertilized(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getIsFertilized(blockAccess.getBlockMetadata(i, j, k));
	}
    
	protected boolean getIsFertilized(int iMetadata)
	{
		return ( iMetadata & 2 ) != 0;
	}
	
	protected void setIsFertilized(World world, int i, int j, int k, boolean bFertilized)
	{
		int iMetadata = setIsFertilized(world.getBlockMetadata(i, j, k), bFertilized);
		
		world.setBlockMetadataWithNotify( i, j, k, iMetadata );
	}
	
	protected int setIsFertilized(int iMetadata, boolean bFertilized)
	{
		if ( bFertilized)
		{
			iMetadata |= 2;
		}
		else
		{
			iMetadata &= (~2);
		}
		
		return iMetadata;
	}
	
	//------------ Client Side Functionality ----------//

    @Environment(EnvType.CLIENT)
    private Icon iconTopSoilDry;
    @Environment(EnvType.CLIENT)
    private Icon iconTopSoilDryFertilized;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

		iconTopSoilDry = register.registerIcon("fcBlockPlanter_top_dry");
		iconTopSoilDryFertilized = register.registerIcon("fcBlockPlanter_top_dry_fertilized");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		if ( iSide == 1  )
		{
			if ( getIsFertilized(iMetadata) )
	        {
				if ( getIsHydrated(iMetadata) )
				{
					return iconTopSoilWetFertilized;
				}
				else
				{
					return iconTopSoilDryFertilized;
				}
        	}
			else
			{
				if ( getIsHydrated(iMetadata) )
				{
					return iconTopSoilWet;
				}
				else
				{
					return iconTopSoilDry;
				}
			}
		}        
    	
        return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
		return renderFilledPlanterBlock(renderer, i, j, k);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderer, int iItemDamage, float fBrightness)
    {
		renderFilledPlanterInvBlock(renderer, this, iItemDamage);
    }
}
