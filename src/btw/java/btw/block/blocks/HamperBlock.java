// FCMOD

package btw.block.blocks;

import btw.BTWMod;
import btw.block.model.BlockModel;
import btw.block.model.HamperModel;
import btw.block.tileentity.HamperTileEntity;
import btw.inventory.BTWContainers;
import btw.inventory.container.HamperContainer;
import btw.item.BTWItems;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class HamperBlock extends BasketBlock
{
	public static final HamperModel model = new HamperModel();
	
    public HamperBlock(int iBlockID)
    {
        super( iBlockID );
        
        setUnlocalizedName( "fcBlockHamper" );
        
        this.initBlockBounds(1 / 16D, 0, 1 / 16D, 15 / 16D, 1, 15 / 16D);
    }
    
    @Override
    public TileEntity createNewTileEntity(World world )
    {
        return new HamperTileEntity();
    }
    
	@Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
    {
        if ( !world.isRemote)
        {
        	if (!WorldUtils.doesBlockHaveCenterHardpointToFacing(world, i, j + 1, k, 0, true) &&
				!WorldUtils.isBlockRestingOnThatBelow(world, i, j + 1, k) )
    		{
	            HamperTileEntity tileEntity = (HamperTileEntity)world.getBlockTileEntity( i, j, k );
	            
	        	if ( player instanceof EntityPlayerMP ) // should always be true
	        	{
	        		HamperContainer container = new HamperContainer( player.inventory, tileEntity );
	        		
	        		BTWMod.serverOpenCustomInterface( (EntityPlayerMP)player, container, BTWContainers.hamperContainerID);
	        	}
    		}
        }
        
		return true;
    }
	
    @Override
	public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
		return iFacing == 0 || ( iFacing == 1 && !getIsOpen(blockAccess, i, j, k) );
	}
    
    @Override
    public void onCrushedByFallingEntity(World world, int i, int j, int k, EntityFallingSand entity)
    {
    	if ( !world.isRemote )
    	{
    		dropItemsIndividually(world, i, j, k, BTWItems.wickerPane.itemID, 2, 0, 0.75F);
    	}
    }
    
    @Override
	public BlockModel getLidModel(int iMetadata)
    {
		return model.lid;
    }
	
    @Override
	public Vec3 getLidRotationPoint()
	{
    	return model.getLidRotationPoint();
	}

    @Override
    public MovingObjectPosition collisionRayTrace( World world, int i, int j, int k, Vec3 startRay, Vec3 endRay )
    {
    	int iFacing = getFacing(world, i, j, k);
    	
    	BlockModel transformedModel = model.makeTemporaryCopy();
    	
    	transformedModel.rotateAroundYToFacing(iFacing);
    	
    	return transformedModel.collisionRayTrace(world, i, j, k, startRay, endRay);
    }
    
	//------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private boolean renderingBase = false;

    @Environment(EnvType.CLIENT)
    private Icon iconBaseOpenTop;
    @Environment(EnvType.CLIENT)
    private Icon iconFront;
    @Environment(EnvType.CLIENT)
    private Icon iconTop;
    @Environment(EnvType.CLIENT)
    private Icon iconBottom;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

		iconBaseOpenTop = register.registerIcon("fcBlockHamper_open_top");
		iconFront = register.registerIcon("fcBlockHamper_front");
		iconTop = register.registerIcon("fcBlockHamper_top");
		iconBottom = register.registerIcon("fcBlockHamper_bottom");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		if ( iSide == 1 )
		{
			if (renderingBase)
			{
				return iconBaseOpenTop;
			}
			else
			{
				return iconTop;
			}
		}
		else if ( iSide == 0 )
		{
			return iconBottom;
		}
		else
		{				
			int iFacing = this.getFacing(iMetadata);
			
			if ( iSide == iFacing )
			{
				return iconFront;
			}
		}
		
		return super.getIcon( iSide, iMetadata );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {    	
    	int iMetadata = renderBlocks.blockAccess.getBlockMetadata( i, j, k );
    	
		int iFacing = getFacing(iMetadata);
		
    	BlockModel transformedModel = model.makeTemporaryCopy();
    	
		transformedModel.rotateAroundYToFacing(iFacing);
		
		renderBlocks.setUVRotateTop(convertFacingToTopTextureRotation(iFacing));
		renderBlocks.setUVRotateBottom(convertFacingToBottomTextureRotation(iFacing));

		renderingBase = true;
		
		boolean bReturnValue = transformedModel.renderAsBlock(renderBlocks, this, i, j, k);

		renderingBase = false;
		
		if ( !getIsOpen(iMetadata) )
		{
			transformedModel = model.lid.makeTemporaryCopy();
			
			transformedModel.rotateAroundYToFacing(iFacing);
			
			transformedModel.renderAsBlock(renderBlocks, this, i, j, k);
		}
    	
		renderBlocks.clearUVRotation();
		
		return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
    	model.renderAsItemBlock(renderBlocks, this, iItemDamage);
    	model.lid.renderAsItemBlock(renderBlocks, this, iItemDamage);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool( World world, int i, int j, int k )
    {
		int iMetadata = world.getBlockMetadata( i, j, k );
		
		AxisAlignedBB transformedBox;
		
		if ( getIsOpen(iMetadata) )
		{
			transformedBox = model.selectionBoxOpen.makeTemporaryCopy();
		}
		else
		{
			transformedBox = model.selectionBox.makeTemporaryCopy();
		}
		
		transformedBox.rotateAroundYToFacing(getFacing(iMetadata));
		
		transformedBox.offset( i, j, k );
		
		return transformedBox;		
    }
}
