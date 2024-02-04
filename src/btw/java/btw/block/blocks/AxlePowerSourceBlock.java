// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;

import java.util.Random;

public class AxlePowerSourceBlock extends AxleBlock
{
	public AxlePowerSourceBlock(int iBlockID)
	{
		super( iBlockID );
    	
        setUnlocalizedName( "fcBlockAxlePowerSource" );        

        setCreativeTab( CreativeTabs.tabRedstone );
	}
	
	@Override
    public void updateTick(World world, int i, int j, int k, Random random )
    {
		// override of super to prevent power level validation
    }
	
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iBlockID )
    {
		// override of super to prevent power level validation
    }
	
	@Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
    {
        return BTWBlocks.axle.blockID;
    }
	
	@Override
    public int getMechanicalPowerLevelProvidedToAxleAtFacing(World world, int i, int j, int k, int iFacing)
    {
		int iAlignment = getAxisAlignment(world, i, j, k);
		
		if ( ( iFacing >> 1 ) == iAlignment )
		{
			return 4;
		}
		
    	return 0;
    }
	
	@Override
	protected void validatePowerLevel(World world, int i, int j, int k)
	{
		// power source axles are validated externally by whatever entity is providing power
	}
	
	@Override
    public int getPowerLevel(IBlockAccess iBlockAccess, int i, int j, int k)
    {
		return 4;
    }
    
	@Override
    public int getPowerLevelFromMetadata(int iMetadata)
    {
    	return 4;
    }
    
	@Override
    public void setPowerLevel(World world, int i, int j, int k, int iPowerLevel)
    {
    }
    
	@Override
    public int setPowerLevelInMetadata(int iMetadata, int iPowerLevel)
    {
    	return iMetadata;
    }
    
	@Override
    public void setPowerLevelWithoutNotify(World world, int i, int j, int k, int iPowerLevel)
    {
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean clientCheckIfPowered(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
}
