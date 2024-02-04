// FCMOD

package btw.block.blocks;

import btw.util.MiscUtils;
import net.minecraft.src.*;

public class DispenserBlock extends BlockDispenser
{
    public DispenserBlock(int iBlockID)
    {
    	super( iBlockID );
    	
    	setHardness( 3.5F );
    	
    	setStepSound( soundStoneFootstep );
    	
    	setUnlocalizedName( "dispenser" );
    }

    @Override
	public int getFacing(int iMetadata)
	{
		return iMetadata & 7;
	}
	
    @Override
	public int setFacing(int iMetadata, int iFacing)
	{
		return ( iMetadata & (~7) ) | iFacing;
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entityLiving, ItemStack stack)
	{
		int facing = MiscUtils.convertPlacingEntityOrientationToBlockFacingReversed(entityLiving);
		
		setFacing(world, x, y, z, facing);
		
		if (stack.hasDisplayName())
		{
			((TileEntityDispenser)world.getBlockTileEntity(x, y, z)).setCustomName(stack.getDisplayName());
		}
	}
	
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}

