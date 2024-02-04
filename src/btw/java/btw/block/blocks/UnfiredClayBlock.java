// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;

import java.util.Random;

public class UnfiredClayBlock extends Block
{
    public UnfiredClayBlock(int iBlockID )
    {
        super( iBlockID, Material.clay );
        
        setHardness( 0.6F );
        setShovelsEffectiveOn();
        
        setStepSound( BTWBlocks.stepSoundSquish);
        
        setUnlocalizedName( "fcBlockUnfiredClay" );
        
        setCreativeTab( CreativeTabs.tabBlock );
    }

    @Override
    public int idDropped( int iMetaData, Random rand, int iFortuneModifier )
    {
        return Item.clay.itemID;
    }

    @Override
    public int quantityDropped( Random rand )
    {
        return 9;
    }
    
	@Override
    public boolean canPlaceBlockAt( World world, int i, int j, int k )
    {
		return WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j - 1, k, 1, true);
    }
    
	@Override
    public void onBlockAdded( World world, int i, int j, int k )
    {
		// check beneath for valid block due to piston pushing not sending a notify
		
		if ( !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j - 1, k, 1, true) )
		{
	        dropBlockAsItem(world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
	        world.setBlockWithNotify(i, j, k, 0);
		}
    }
	
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iBlockID )
    {
    	if ( !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j - 1, k, 1, true) )
    	{
            dropBlockAsItem(world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
            world.setBlockWithNotify(i, j, k, 0);
    	}
    }
    
	@Override
	public boolean canTransmitRotationVerticallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return false;
	}
	
	@Override
    public boolean canBePistonShoveled(World world, int i, int j, int k)
    {
    	return true;
    }
	
    //------------- Class Specific Methods ------------//    
	
	//----------- Client Side Functionality -----------//
}
