//FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class FarmlandBlockFertilized extends FarmlandBlock
{
    public FarmlandBlockFertilized(int iBlockID)
    {
    	super( iBlockID );    	
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
		
		world.setBlockAndMetadataWithNotify( i, j, k, 
			BTWBlocks.farmland.blockID, iMetadata );
	}
	
	@Override
    protected boolean isFertilized(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		blockIcon = register.registerIcon( "dirt" );

		iconTopWet = register.registerIcon("FCBlockFarmlandFertilized_wet");
        iconTopDry = register.registerIcon("FCBlockFarmlandFertilized_dry");
    }
}
