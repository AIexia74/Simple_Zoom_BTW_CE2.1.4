// FCMOD

package btw.block.blocks.legacy;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class LegacyFarmlandBlockFertilized extends LegacyFarmlandBlockBase
{
	private static final int DEFAULT_TEXTURE = 2;
	private static final int TOP_WET_TEXTURE = 136;
	public static final int TOP_DRY_TEXTURE = 137;
	
    public LegacyFarmlandBlockFertilized(int iBlockID)
    {
        super( iBlockID );
        
        setUnlocalizedName( "FCBlockFarmlandFertilized" );
    }
    
	@Override
	public float getPlantGrowthOnMultiplier(World world, int i, int j, int k, Block plantBlock)
	{
		return 2F;
	}
	
	@Override
	public boolean getIsFertilizedForPlantGrowth(World world, int i, int j, int k)
	{
		return true;
	}
	
	@Override
	public void notifyOfFullStagePlantGrowthOn(World world, int i, int j, int k, Block plantBlock)
	{
		// revert back to unfertilized soil
		
		int iMetadata = world.getBlockMetadata( i, j, k );
		
		world.setBlockAndMetadataWithNotify( i, j, k, Block.tilledField.blockID, iMetadata );
	}
	
	@Override
    protected boolean isFertilized(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
    
	@Override
	protected void convertToNewSoil(World world, int i, int j, int k)
	{
		int iNewMetadata = 0;
		
		if ( isHydrated(world, i, j, k) )
		{
			iNewMetadata = BTWBlocks.fertilizedFarmland.setFullyHydrated(
				iNewMetadata);
		}
		
    	world.setBlockAndMetadataWithNotify( i, j, k, 
    		BTWBlocks.fertilizedFarmland.blockID, iNewMetadata );
	}
	
    //------------- Class Specific Methods ------------//

	//----------- Client Side Functionality -----------//    

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		blockIcon = register.registerIcon( "dirt" );

		iconTopWet = register.registerIcon("FCBlockFarmlandFertilized_wet");
        iconTopDry = register.registerIcon("FCBlockFarmlandFertilized_dry");
    }
}