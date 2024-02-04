// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.client.render.util.RenderUtils;
import btw.item.BTWItems;
import btw.world.util.WorldUtils;
import com.prupe.mcpatcher.cc.ColorizeBlock;
import com.prupe.mcpatcher.mal.block.RenderBlocksUtils;
import java.util.List;
import java.util.Random;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft; // client only
import net.minecraft.src.*;

public class DirtSlabBlock extends AttachedSlabBlock
{
	public static final int SUBTYPE_DIRT = 0;
	public static final int SUBTYPE_GRASS = 1;
	public static final int SUBTYPE_MYCELIUM = 2; // Not used.  Implemented within its own block
	public static final int SUBTYPE_PACKED_EARTH = 3;

	public final static int NUM_SUBTYPES = 4;

	public DirtSlabBlock(int iBlockID )
	{
		super( iBlockID, Material.ground );

		setHardness( 0.5F );
		setShovelsEffectiveOn(true);

		setStepSound( Block.soundGrassFootstep );

		setUnlocalizedName( "fcBlockSlabDirt" );

		setTickRandomly( true );

		setCreativeTab( CreativeTabs.tabBlock );
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random rand )
	{
		int iSubType = getSubtype(world, i, j, k);

		if (iSubType == SUBTYPE_GRASS)
		{
			world.setBlock(i, j, k, BTWBlocks.grassSlab.blockID);
			BTWBlocks.grassSlab.updateTick(world, i, j, k, rand);
		}
	}

	@Override
	public int damageDropped( int iMetadata )
	{
		int iSubtype = getSubtype(iMetadata);

		if (iSubtype == SUBTYPE_PACKED_EARTH)
		{
			return iSubtype;
		}

		return 0;
	}

	@Override
	public int idDropped( int iMetadata, Random random, int iFortuneModifier )
	{
		int iSubtype = getSubtype(iMetadata);

		if (iSubtype == SUBTYPE_PACKED_EARTH)
		{
			return super.idDropped( iMetadata, random, iFortuneModifier );
		}

		return BTWBlocks.looseDirtSlab.blockID;
	}

	@Override
	public float getMovementModifier(World world, int i, int j, int k)
	{
		float fModifier = 1.0F;

		int iSubtype = getSubtype(world, i, j, k);

		if (iSubtype == SUBTYPE_PACKED_EARTH)
		{
			fModifier = 1.2F;
		}

		return fModifier;
	}

	@Override
	public StepSound getStepSound(World world, int i, int j, int k)
	{
		int iSubtype = getSubtype(world, i, j, k);

		if (iSubtype == SUBTYPE_DIRT || iSubtype == SUBTYPE_PACKED_EARTH)
		{
			return Block.soundGravelFootstep;
		}

		return stepSound;
	}

	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		int iNumDropped = 3;

		if (getSubtype(iMetadata) == SUBTYPE_PACKED_EARTH)
		{
			iNumDropped = 6;
		}

		dropItemsIndividually(world, i, j, k, BTWItems.dirtPile.itemID,
							  iNumDropped, 0, fChanceOfDrop);

		return true;
	}

	@Override
	public boolean getCanGrassSpreadToBlock(World world, int i, int j, int k)
	{
		int iSubType = getSubtype(world, i, j, k);

		if (iSubType == SUBTYPE_DIRT)
		{        
			Block blockAbove = Block.blocksList[world.getBlockId( i, j + 1, k )];
			boolean bIsUpsideDown = getIsUpsideDown(world, i, j, k);

			if ( blockAbove == null || blockAbove.getCanGrassGrowUnderBlock(
					world, i, j + 1, k, !bIsUpsideDown) )
			{            
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean spreadGrassToBlock(World world, int x, int y, int z) {
		boolean isUpsideDown = getIsUpsideDown(world, x, y, z);
		
		world.setBlockWithNotify(x, y, z, BTWBlocks.grassSlab.blockID);
		BTWBlocks.grassSlab.setSparse(world, x, y, z);
		BTWBlocks.grassSlab.setIsUpsideDown(world, x, y, z, isUpsideDown);

		return true;
	}

	@Override
	public boolean getCanMyceliumSpreadToBlock(World world, int i, int j, int k)
	{
		int iSubType = getSubtype(world, i, j, k);

		if (iSubType == SUBTYPE_DIRT)
		{        
			return !getIsUpsideDown(world, i, j, k) ||
                   !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j + 1, k, 0);
		}

		return false;
	}

	@Override
	public boolean spreadMyceliumToBlock(World world, int x, int y, int z) {
		boolean isUpsideDown = getIsUpsideDown(world, x, y, z);
		
		world.setBlockWithNotify(x, y, z, BTWBlocks.myceliumSlab.blockID);
		BTWBlocks.myceliumSlab.setSparse(world, x, y, z);
		BTWBlocks.myceliumSlab.setIsUpsideDown(world, x, y, z, isUpsideDown);

		return true;
	}

	@Override
	public boolean attemptToCombineWithFallingEntity(World world, int i, int j, int k, EntityFallingSand entity)
	{
		if ( entity.blockID == BTWBlocks.looseDirtSlab.blockID )
		{
			int iMetadata = world.getBlockMetadata( i, j, k );

			if (getSubtype(iMetadata) != SUBTYPE_PACKED_EARTH && !getIsUpsideDown(iMetadata) )
			{			
				world.setBlockWithNotify( i, j, k, BTWBlocks.looseDirt.blockID );

				return true;
			}
		}

		return super.attemptToCombineWithFallingEntity(world, i, j, k, entity);
	}
	
	@Override
	protected void onAnchorBlockLost(World world, int i, int j, int k) {
		if (this.getSubtype(world.getBlockMetadata(i, j, k)) != SUBTYPE_PACKED_EARTH) {
			world.setBlock(i, j, k, BTWBlocks.looseDirtSlab.blockID, world.getBlockMetadata(i, j, k) & 3, 2);
		}
		else {
			dropComponentItemsOnBadBreak(world, i, j, k, world.getBlockMetadata(i, j, k), 1F);
			world.setBlockToAir(i, j, k);
		}
	}

	@Override
	public int getCombinedBlockID(int iMetadata)
	{
		int iSubtype = getSubtype(iMetadata);

		if (iSubtype == SUBTYPE_PACKED_EARTH)
		{
			return BTWBlocks.aestheticEarth.blockID;
		}

		return Block.dirt.blockID;
	}

	@Override
	public int getCombinedMetadata(int iMetadata)
	{
		int iSubtype = getSubtype(iMetadata);

		if (iSubtype == SUBTYPE_PACKED_EARTH)
		{
			return AestheticOpaqueEarthBlock.SUBTYPE_PACKED_EARTH;
		}

		return 0;
	}

	@Override
	public boolean canBePistonShoveled(World world, int i, int j, int k)
	{
		return true;
	}

	@Override
	protected boolean canSilkHarvest()
	{
		return true;
	}

	@Override
	protected ItemStack createStackedBlock( int metadata )
	{
		int subtype = getSubtype(metadata);
		
		if (subtype == this.SUBTYPE_GRASS) {
			return new ItemStack(BTWBlocks.grassSlab);
		}
		else {
			return new ItemStack(blockID, 1, subtype);
		}
	}

	@Override
	public boolean canBeGrazedOn(IBlockAccess blockAccess, int i, int j, int k,
								 EntityAnimal byAnimal)
	{
		return getSubtype(blockAccess, i, j, k) == SUBTYPE_GRASS;
	}

	@Override
	public void onGrazed(World world, int i, int j, int k, EntityAnimal animal)
	{
		if ( !animal.getDisruptsEarthOnGraze() )
		{
			world.setBlockWithNotify(i, j, k, BTWBlocks.grassSlab.blockID);
			BTWBlocks.grassSlab.setSparse(world, i, j, k);
		}
		else
		{
			world.setBlockWithNotify( i, j, k, BTWBlocks.looseDirtSlab.blockID );

			notifyNeighborsBlockDisrupted(world, i, j, k);
		}
	}

	@Override
	public void onVegetationAboveGrazed(World world, int i, int j, int k, EntityAnimal animal)
	{
		if ( animal.getDisruptsEarthOnGraze() )
		{
			world.setBlockWithNotify( i, j, k, BTWBlocks.looseDirtSlab.blockID );

			notifyNeighborsBlockDisrupted(world, i, j, k);
		}
	}

	@Override
	public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata)
	{
		super.onBlockDestroyedWithImproperTool(world, player, i, j, k, iMetadata);

		if (getSubtype(iMetadata) != SUBTYPE_PACKED_EARTH)
		{
			onDirtSlabDugWithImproperTool(world, i, j, k, getIsUpsideDown(iMetadata));
		}
	}

	@Override
	public void onBlockDestroyedByExplosion( World world, int i, int j, int k, Explosion explosion )
	{
		super.onBlockDestroyedByExplosion( world, i, j, k, explosion );

		if (getSubtype(world, i, j, k) != SUBTYPE_PACKED_EARTH)
		{
			onDirtSlabDugWithImproperTool(world, i, j, k, getIsUpsideDown(world, i, j, k));
		}
	}

	@Override
	protected void onNeighborDirtDugWithImproperTool(World world, int i, int j, int k,
													 int iToFacing)
	{
		int iSubtype = getSubtype(world, i, j, k);

		if (iSubtype != SUBTYPE_PACKED_EARTH)
		{
			// only disrupt grass/mycelium when block below is dug out

			if (iSubtype != SUBTYPE_GRASS || iToFacing == 0 )
			{			
				boolean bIsUpsideDown = getIsUpsideDown(world, i, j, k);

				// only disrupt the block if it's touching the dug neighbor

				if ( !( bIsUpsideDown && iToFacing == 0 ) && !( !bIsUpsideDown && iToFacing == 1 ) )
				{
					world.setBlockWithNotify( i, j, k, 
							BTWBlocks.looseDirtSlab.blockID );
				}
			}
		}
	}

	//------------- Class Specific Methods ------------//    

	public int getSubtype(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getSubtype(blockAccess.getBlockMetadata(i, j, k));
	}

	public int getSubtype(int iMetadata)
	{
		return ( iMetadata & (~1) ) >> 1;
	}

	public void setSubtype(World world, int i, int j, int k, int iSubtype)
	{
		int iMetadata = world.getBlockMetadata( i, j, k ) & 1; // clear old value

		iMetadata |= ( iSubtype << 1 );

		world.setBlockMetadataWithNotify( i, j, k, iMetadata );
	}

	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconGrassSide;
    @Environment(EnvType.CLIENT)
    private Icon iconGrassSideOverlay;
    @Environment(EnvType.CLIENT)
    private Icon iconGrassTop;
    @Environment(EnvType.CLIENT)
    private Icon iconGrassTopItem;

    @Environment(EnvType.CLIENT)
    private Icon iconGrassSideHalf;
    @Environment(EnvType.CLIENT)
    private Icon iconGrassSideOverlayHalf;

    @Environment(EnvType.CLIENT)
    private Icon iconPackedEarth;

    @Environment(EnvType.CLIENT)
    private Icon iconGrassWithSnowSide;
    @Environment(EnvType.CLIENT)
    private Icon iconGrassWithSnowSideHalf;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
	{
		blockIcon = register.registerIcon( "dirt" );

		iconGrassSide = register.registerIcon("grass_side");
		iconGrassSideOverlay = register.registerIcon("grass_side_overlay");
		iconGrassTop = register.registerIcon("grass_top");
		iconGrassTopItem = register.registerIcon("fcBlockSlabDirt_grass_top_item");

		iconGrassSideHalf = register.registerIcon("FCBlockSlabDirt_grass_side");
		iconGrassSideOverlayHalf = register.registerIcon("FCBlockSlabDirt_grass_side_overlay");

		iconPackedEarth = register.registerIcon("FCBlockPackedEarth");

		iconGrassWithSnowSide = register.registerIcon("snow_side");
		iconGrassWithSnowSideHalf = register.registerIcon("FCBlockSlabDirt_grass_snow_side");
	}

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
	{
		if ( iSide == 1 && getSubtype(iMetadata) == SUBTYPE_GRASS)
		{
			// precolored grass texture for item block top side

			return iconGrassTopItem;
		}

		return getIconFromMetadata(iSide, iMetadata);
	}

    @Environment(EnvType.CLIENT)
    private Icon getIconFromMetadata(int iSide, int iMetadata)
	{
		int iSubtype = getSubtype(iMetadata);

		if (iSubtype == SUBTYPE_GRASS && iSide != 0 )
		{
			if ( iSide != 1 )
			{
				boolean bIsUpsideDown = ( iMetadata & 1 ) > 0;

				if ( bIsUpsideDown )
				{
					return iconGrassSide;
				}
				else
				{
					return iconGrassSideHalf;
				}
			}
			else
			{
				return iconGrassTop;
			}
		}
		else if (iSubtype == SUBTYPE_PACKED_EARTH)
		{
			return iconPackedEarth;
		}

		return blockIcon;
	}

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture( IBlockAccess blockAccess, int i, int j, int k, int iSide )
	{
		int iMetadata = blockAccess.getBlockMetadata( i, j, k );
		int iSubtype = getSubtype(iMetadata);

		if (iSubtype == SUBTYPE_GRASS && iSide > 1 &&
			isSnowCoveringTopSurface(blockAccess, i, j, k) )
		{
			Icon betterGrassIcon = RenderBlocksUtils.getGrassTexture(Block.grass, blockAccess, i, j, k, iSide, this.iconGrassTop);

	        if (betterGrassIcon != null)
	        {
	            return betterGrassIcon;
	        }
	        else if ( getIsUpsideDown(iMetadata) )
			{
				return iconGrassWithSnowSide;
			}
			else
			{				
				return iconGrassWithSnowSideHalf;
			}
		}

		Icon icon = RenderBlocksUtils.getGrassTexture(Block.grass, blockAccess, i, j, k, iSide, this.iconGrassTop);

		if (icon != null && iSubtype == SUBTYPE_GRASS && iSide >= 1)
		{
			return icon;
		}

		return getIconFromMetadata(iSide, iMetadata);
	}

    @Environment(EnvType.CLIENT)
    public Icon getSideOverlayTexture(IBlockAccess blockAccess, int i, int j, int k)
	{
		if ( !getIsUpsideDown(blockAccess, i, j, k) )
		{
			return iconGrassSideOverlayHalf;
		}

		return iconGrassSideOverlay;
	}

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks( int iBlockID, CreativeTabs creativeTabs, List list )
	{
		list.add( new ItemStack(iBlockID, 1, SUBTYPE_DIRT));
		list.add( new ItemStack(iBlockID, 1, SUBTYPE_PACKED_EARTH));
	}

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
	{
		IBlockAccess blockAccess = renderer.blockAccess;

		renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
				renderer.blockAccess, i, j, k) );

		int iSubtype = ((DirtSlabBlock)(BTWBlocks.dirtSlab)).getSubtype(blockAccess, i, j, k);

		if (iSubtype == DirtSlabBlock.SUBTYPE_GRASS && !isSnowCoveringTopSurface(blockAccess, i, j, k))
		{
			int iColorMultiplier = colorMultiplier(blockAccess, i, j, k);

			float fRed = (float)(iColorMultiplier >> 16 & 0xff) / 255F;
			float fGreen = (float)(iColorMultiplier >> 8 & 0xff) / 255F;
			float fBlue = (float)(iColorMultiplier & 0xff) / 255F;

			if ( Minecraft.isAmbientOcclusionEnabled() )
			{
	        	return renderer.renderGrassBlockWithAmbientOcclusion(this, i, j, k, fRed, fGreen, fBlue, getSideOverlayTexture(blockAccess, i, j, k));
			}
			else
			{
				return renderer.renderGrassBlockWithColorMultiplier(this, i, j, k, fRed, fGreen, fBlue, getSideOverlayTexture(blockAccess, i, j, k));
			}
		}
		else
		{
			return renderer.renderStandardBlock( this, i, j, k );
		}
	}

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
	{
		renderBlocks.setRenderBounds( 0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F );

		RenderUtils.renderInvBlockWithMetadata(renderBlocks, this, -0.5F, -0.5F, -0.5F, iItemDamage << 1);
	}

    @Override
    @Environment(EnvType.CLIENT)
    public int colorMultiplier( IBlockAccess blockAccess, int i, int j, int k )
	{
		int iSubtype = getSubtype(blockAccess, i, j, k);

		if (iSubtype == SUBTYPE_GRASS && !isSnowCoveringTopSurface(blockAccess, i, j, k)) {
			if (ColorizeBlock.colorizeBlock(this, blockAccess, i, j, k)) {
				return ColorizeBlock.blockColor;
			}
			else
			{
				int iRed = 0;
				int iGreen = 0;
				int iBlue = 0;

				for (int iKOffset = -1; iKOffset <= 1; iKOffset++)
				{
					for (int iIOffset = -1; iIOffset <= 1; iIOffset++)
					{
						int iBiomeGrassColor = blockAccess.getBiomeGenForCoords( i + iIOffset, k + iKOffset ).getBiomeGrassColor();

						iRed += (iBiomeGrassColor & 0xff0000) >> 16;
						iGreen += (iBiomeGrassColor & 0xff00) >> 8;
						iBlue += iBiomeGrassColor & 0xff;
					}
				}

				return (iRed / 9 & 0xff) << 16 | (iGreen / 9 & 0xff) << 8 | iBlue / 9 & 0xff;
			}
		}
		else
		{
			return super.colorMultiplier( blockAccess, i, j, k );
		}
	}    
}