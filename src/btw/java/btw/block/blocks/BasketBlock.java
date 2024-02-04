// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.block.model.BlockModel;
import btw.block.tileentity.BasketTileEntity;
import btw.util.MiscUtils;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public abstract class BasketBlock extends BlockContainer
{
    protected BasketBlock(int iBlockID )
    {
        super( iBlockID, BTWBlocks.basketMaterial);
        
        setHardness( 0.05F );        
		
        setBuoyant();
        
		setFireProperties(Flammability.WICKER);
		
        setStepSound( soundGrassFootstep );        
        
        setCreativeTab( CreativeTabs.tabDecorations );
    }
    
	@Override
    public void breakBlock(World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
        BasketTileEntity tileEntity = (BasketTileEntity)world.getBlockTileEntity( i, j, k );
        if(tileEntity != null) {
			tileEntity.ejectContents();
		}
        
        super.breakBlock( world, i, j, k, iBlockID, iMetadata );	        
    }

	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }

	@Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
	@Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
        return setFacing(iMetadata, iFacing);
    }
    
	@Override
    public int preBlockPlacedBy(World world, int i, int j, int k, int iMetadata, EntityLiving entityBy)
	{
		int iFacing = MiscUtils.convertOrientationToFlatBlockFacingReversed(entityBy);

		return setFacing(iMetadata, iFacing);
	}
	
	@Override
    public boolean canPlaceBlockAt( World world, int i, int j, int k )
    {
        if ( !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j - 1, k, 1, true) )
		{
			return false;
		}
		
        return super.canPlaceBlockAt( world, i, j, k );
    }
    
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iBlockID )
    {    	
        if ( !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j - 1, k, 1, true) )
        {
            dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
            
            world.setBlockToAir( i, j, k );
        }
    }
    
    @Override
    public boolean canBeCrushedByFallingEntity(World world, int i, int j, int k, EntityFallingSand entity)
    {
    	return true;
    }
    
	@Override
	public int getFacing(int iMetadata)
	{
		return ( iMetadata & 3 ) + 2;
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
		iMetadata &= ~3; // filter out old facing
		
		iMetadata |= MathHelper.clamp_int( iFacing, 2, 5 ) - 2; // convert to flat facing
		
		return iMetadata;
	}
	
	@Override
	public boolean canRotateOnTurntable(IBlockAccess iBlockAccess, int i, int j, int k)
	{
		return true;
	}
	
	@Override
	public boolean getPreventsFluidFlow(World world, int i, int j, int k, Block fluidBlock)
	{
        return false;
	}
	
	//------------- Class Specific Methods ------------//
	
	public void setHasContents(World world, int i, int j, int k, boolean bHasContents)
	{
		int iMetadata = setHasContents(world.getBlockMetadata(i, j, k), bHasContents);
		
		world.setBlockMetadataWithNotify( i, j, k, iMetadata );
	}
	
	public int setHasContents(int iMetadata, boolean bHasContents)
	{
		if ( bHasContents )
		{
			iMetadata |= 4;
		}
		else
		{
			iMetadata &= (~4);
		}
		
		return iMetadata;
	}
	
	public boolean getHasContents(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getHasContents(blockAccess.getBlockMetadata(i, j, k));
	}
	
	public boolean getHasContents(int iMetadata)
	{
		return ( iMetadata & 4 ) != 0;
	}
	
	public void setIsOpen(World world, int i, int j, int k, boolean bOpen)
	{
		int iMetadata = setIsOpen(world.getBlockMetadata(i, j, k), bOpen);
		
		world.setBlockMetadataWithNotify( i, j, k, iMetadata );
	}
	
	public int setIsOpen(int iMetadata, boolean bOpen)
	{
		if ( bOpen )
		{
			iMetadata |= 8;
		}
		else
		{
			iMetadata &= (~8);
		}
		
		return iMetadata;
	}
	
	public boolean getIsOpen(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getIsOpen(blockAccess.getBlockMetadata(i, j, k));
	}
	
	public boolean getIsOpen(int iMetadata)
	{
		return ( iMetadata & 8 ) != 0;
	}
	
	public abstract BlockModel getLidModel(int iMetadata);
	
	public abstract Vec3 getLidRotationPoint();
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    public boolean renderingOpenLid = false;

    @Environment(EnvType.CLIENT)
    public int openLidBrightness;

    @Override
    @Environment(EnvType.CLIENT)
    public int getMixedBrightnessForBlock(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
    	if (renderingOpenLid)
    	{
    		// prevents weird rendering artifacts where some faces appear completely dark
    		
    		return openLidBrightness;
    	}
    	
    	return super.getMixedBrightnessForBlock( par1IBlockAccess, par2, par3, par4 );
    }
}
