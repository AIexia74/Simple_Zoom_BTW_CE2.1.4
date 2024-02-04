// FCMOD

package btw.block.blocks;

import btw.BTWMod;
import btw.block.BTWBlocks;
import btw.block.model.BlockModel;
import btw.block.model.SoulforgeModel;
import btw.block.tileentity.AnvilTileEntity;
import btw.inventory.BTWContainers;
import btw.inventory.container.SoulforgeContainer;
import btw.util.MiscUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class SoulforgeBlock extends BlockContainer
{
    private final BlockModel blockModel = new SoulforgeModel();
    
    public SoulforgeBlock(int iBlockID )
    {
        super( iBlockID, BTWBlocks.soulforgedSteelMaterial);
        
        setHardness( 3.5F );        
        setStepSound( soundMetalFootstep );        
        
        setUnlocalizedName( "fcBlockAnvil" );
        
        setCreativeTab( CreativeTabs.tabDecorations );
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
    public TileEntity createNewTileEntity( World world )
    {
        return new AnvilTileEntity();
    }
    
	@Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
    	// flat facing only
    	
    	if ( iFacing < 2 )
    	{
    		iFacing = 2;
    	}
    	else
    	{
        	iFacing = Block.getOppositeFacing(iFacing);
    	}
    	
        return setFacing(iMetadata, iFacing);
    }
    
	@Override
	public void onBlockPlacedBy( World world, int i, int j, int k, EntityLiving entityLiving, ItemStack stack )
	{
		int iFacing = MiscUtils.convertOrientationToFlatBlockFacingReversed(entityLiving);
		
		setFacing(world, i, j, k, iFacing);
	}
	
	@Override
    public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
    {
        if ( !world.isRemote )
        {
        	if ( player instanceof EntityPlayerMP ) // should always be true
        	{
        		SoulforgeContainer container = new SoulforgeContainer( player.inventory, world, i, j, k );
        		
        		BTWMod.serverOpenCustomInterface( (EntityPlayerMP)player, container, BTWContainers.soulforgeContainerID);
        	}
        }

        return true;
    }
    
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
        int iFacing = getFacing(blockAccess, i, j, k);
        
        if ( iFacing == 2 || iFacing == 3 )
        {
        	return AxisAlignedBB.getAABBPool().getAABB(         	
        		0.5F - SoulforgeModel.ANVIL_HALF_BASE_WIDTH, 0.0F, 0.0F,
				0.5F + SoulforgeModel.ANVIL_HALF_BASE_WIDTH, 1.0F, 1.0F);
        }
        else
        {
        	return AxisAlignedBB.getAABBPool().getAABB(         	
        		0.0F, 0.0F, 0.5F - SoulforgeModel.ANVIL_HALF_BASE_WIDTH,
        		1.0F, 1.0F, 0.5F + SoulforgeModel.ANVIL_HALF_BASE_WIDTH);
        }    	
    }
    
	@Override
    public void breakBlock( World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
        AnvilTileEntity tileEntityAnvil = (AnvilTileEntity)world.getBlockTileEntity( i, j, k );
        
        if ( tileEntityAnvil != null )
        {
        	tileEntityAnvil.ejectMoulds(); // legacy for old mould items
        }

        super.breakBlock( world, i, j, k, iBlockID, iMetadata );
    }

    @Override
    public MovingObjectPosition collisionRayTrace( World world, int i, int j, int k, Vec3 startRay, Vec3 endRay )
    {
    	int iFacing = getFacing(world, i, j, k);
    	
    	BlockModel transformedModel = blockModel.makeTemporaryCopy();
    	
    	transformedModel.rotateAroundYToFacing(iFacing);
    	
    	return transformedModel.collisionRayTrace(world, i, j, k, startRay, endRay);
    }
    
	@Override
	public int getFacing(int iMetadata)
	{
		return iMetadata;
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
		return iFacing;
	}
	
	@Override
	public boolean canRotateOnTurntable(IBlockAccess iBlockAccess, int i, int j, int k)
	{
		return true;
	}
	
	@Override
	public boolean canTransmitRotationVerticallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
	@Override
	public boolean toggleFacing(World world, int i, int j, int k, boolean bReverse)
	{
		rotateAroundJAxis(world, i, j, k, bReverse);
		
		return true;
	}
	
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, 
    	int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(
			iNeighborI, iNeighborJ, iNeighborK, iSide);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	int iFacing = getFacing(renderBlocks.blockAccess, i, j, k);
    	
    	BlockModel transformedModel = blockModel.makeTemporaryCopy();
    	
    	transformedModel.rotateAroundYToFacing(iFacing);
    	
    	return transformedModel.renderAsBlock(renderBlocks, this, i, j, k);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
    	blockModel.renderAsItemBlock(renderBlocks, this, iItemDamage);
    }    
}