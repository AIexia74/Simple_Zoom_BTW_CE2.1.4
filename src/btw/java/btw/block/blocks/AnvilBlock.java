// FCMOD

package btw.block.blocks;

import btw.BTWMod;
import btw.block.model.BlockModel;
import btw.block.model.AnvilModel;
import btw.client.fx.BTWEffectManager;
import btw.inventory.BTWContainers;
import btw.inventory.container.WorkbenchContainer;
import btw.item.BTWItems;
import btw.util.MiscUtils;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class AnvilBlock extends Block
{
	AnvilModel model = new AnvilModel();
	
    public AnvilBlock(int iBlockID)
    {
        super( iBlockID, Material.anvil );
    	
    	setHardness( 50F );
    	setResistance( 10F );
    	setPicksEffectiveOn();
    	
        setLightOpacity( 0 );
        
    	setStepSound( soundAnvilFootstep );
    	
    	setUnlocalizedName( "anvil" );
        
        setCreativeTab( CreativeTabs.tabDecorations );
    }
    
	@Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public int quantityDropped( Random rand )
    {
        return 7;
    }

    @Override
    public int idDropped( int iMetaData, Random random, int iFortuneModifier )
    {
        return BTWItems.metalFragment.itemID;
    }
    
	@Override
    public void harvestBlock( World world, EntityPlayer player, int i, int j, int k, int iMetadata )
    {
        if ( !canSilkHarvest( iMetadata ) || !EnchantmentHelper.getSilkTouchModifier( player ) )
        {
            world.playAuxSFX( BTWEffectManager.BLOCK_DESTROYED_WITH_IMPROPER_TOOL_EFFECT_ID, i, j, k, blockID + ( iMetadata << 12 ) );
        }
        
        super.harvestBlock( world, player, i, j, k, iMetadata );
    }
    
    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata)
    {
    	// regular harvest regardless of tool type
    	
		dropBlockAsItem( world, i, j, k, iMetadata, 0 );
    }
    
	@Override
    protected boolean canSilkHarvest( int iMetadata )
    {
    	return true;
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
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
        return setFacing(iMetadata, iFacing);
    }
    
	@Override
    public int preBlockPlacedBy(World world, int i, int j, int k, int iMetadata, EntityLiving entityBy)
	{
		int iFacing = MiscUtils.convertOrientationToFlatBlockFacing(entityBy);

		return setFacing(iMetadata, iFacing);
	}
    
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iBlockID )
    {    	
        if ( !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j - 1, k, 1, true) )
        {
        	int iMetadata = world.getBlockMetadata( i, j, k );
        	
            world.playAuxSFX( BTWEffectManager.BLOCK_DESTROYED_WITH_IMPROPER_TOOL_EFFECT_ID, i, j, k, blockID + ( iMetadata << 12 ) );
            
            dropBlockAsItem( world, i, j, k, iMetadata, 0 );
            
            world.setBlockToAir( i, j, k );
        }
    }
    
	@Override
    public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
	{
		// prevent access if solid block above
		
        if ( !world.isRemote && !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j + 1, k, 0) )
        {
        	if ( player instanceof EntityPlayerMP ) // should always be true
        	{
        		WorkbenchContainer container = new WorkbenchContainer( player.inventory, world, i, j, k );
        		
        		BTWMod.serverOpenCustomInterface( (EntityPlayerMP)player, container, BTWContainers.anvilContainerID);
        	}
        }

        return true;
	}	

    @Override
    public MovingObjectPosition collisionRayTrace( World world, int i, int j, int k, Vec3 startRay, Vec3 endRay )
    {
    	int iFacing = getFacing(world, i, j, k);
    	
    	BlockModel transformedModel = model.makeTemporaryCopy();
    	
    	transformedModel.rotateAroundYToFacing(iFacing);
    	
    	return transformedModel.collisionRayTrace(world, i, j, k, startRay, endRay);
    }
    
	@Override
	public int getFacing(int iMetadata)
	{
		// fucked up metadata due to vanilla legacy block
		
		int iOrientation = iMetadata & 3;
		
		if ( ( iOrientation & 1 ) == 0 )
		{
			if ( iOrientation == 0 )
			{
				return 3;
			}
			
			return 2;
		}
		else
		{
			if ( iOrientation == 1 )
			{
				return 4;
			}
			
			return 5;
		}
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
		int iOrientation;
		
		if ( iFacing == 2 )
		{
			iOrientation = 2;
		}
		else if ( iFacing == 3 )
		{
			iOrientation = 0;
		}
		else if ( iFacing == 4 )
		{
			iOrientation = 1;
		}
		else // iFacing == 5
		{
			iOrientation = 3;
		}
		
		iMetadata &= ~3; // filter out old facing
		
		return iMetadata | iOrientation;
	}
	
	@Override
	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        blockIcon = register.registerIcon( "anvil_base" );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {    	
    	int iMetadata = renderer.blockAccess.getBlockMetadata( i, j, k );
    	
		int iFacing = getFacing(iMetadata);
		
    	BlockModel transformedModel = model.makeTemporaryCopy();
    	
		transformedModel.rotateAroundYToFacing(iFacing);
		
		renderer.setUVRotateTop(convertFacingToTopTextureRotation(iFacing));
		renderer.setUVRotateBottom(convertFacingToBottomTextureRotation(iFacing));
		
		boolean bReturnValue = transformedModel.renderAsBlock(renderer, this, i, j, k);
    	
		renderer.clearUVRotation();
		
		return bReturnValue;
    }

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
    public void renderBlockAsItem(RenderBlocks renderer, int iItemDamage, float fBrightness)
    {
    	model.renderAsItemBlock(renderer, this, iItemDamage);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool( World world, int i, int j, int k )
    {
		AxisAlignedBB transformedBox = model.boxSelection.makeTemporaryCopy();
		
		transformedBox.rotateAroundYToFacing(getFacing(world, i, j, k));
		
		transformedBox.offset( i, j, k );
		
		return transformedBox;		
    }
}