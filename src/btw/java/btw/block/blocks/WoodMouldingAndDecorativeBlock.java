// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.crafting.util.FurnaceBurnTime;
import btw.client.render.util.RenderUtils;
import btw.item.BTWItems;
import btw.item.blockitems.WoodMouldingDecorativeStubBlockItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class WoodMouldingAndDecorativeBlock extends MouldingAndDecorativeBlock {
	public final static int OAK_TABLE_TOP_TEXTURE_ID = 93;
	public final static int OAK_TABLE_LEG_TEXTURE_ID = 94;
	
	public WoodMouldingAndDecorativeBlock(int iBlockID, String sTextureName, String sColumnSideTextureName, int iMatchingCornerBlockID, String name) {
		super(iBlockID, BTWBlocks.plankMaterial, sTextureName, sColumnSideTextureName, iMatchingCornerBlockID, 2F, 5F, Block.soundWoodFootstep, name);
		
		setAxesEffectiveOn(true);
		
		setBuoyancy(1F);
		setFurnaceBurnTime(FurnaceBurnTime.PLANKS_OAK.burnTime / 4);
		
		setFireProperties(5, 20);
	}
	
	@Override
	public int idDropped(int iMetadata, Random random, int iFortuneModifier) {
		if (isDecorative(iMetadata)) {
			return BTWItems.woodMouldingDecorativeStubID;
		}
		
		return BTWItems.woodMouldingStubID;
	}
	
	@Override
	public int damageDropped(int metadata) {
		return this.damageDropped(this.blockID, metadata);
	}
	
	@Override
	public boolean doesTableHaveLeg(IBlockAccess blockAccess, int i, int j, int k) {
		if (blockID == BTWBlocks.oakWoodMouldingAndDecorative.blockID) {
			int iBlockBelowID = blockAccess.getBlockId(i, j - 1, k);
			
			if (iBlockBelowID == Block.fence.blockID) {
				return true;
			}
		}
		
		return super.doesTableHaveLeg(blockAccess, i, j, k);
	}
	
	@Override
	public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k) {
		return 2; // iron or better
	}
	
	@Override
	public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata) {
		world.playAuxSFX(BTWEffectManager.WOOD_BLOCK_DESTROYED_EFFECT_ID, i, j, k, 0);
		
		int iNumDropped = getNumSawDustDroppedForType(iMetadata);
		
		dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, iNumDropped, 0, 1F);
	}
	
	@Override
	public boolean canDropFromExplosion(Explosion explosion) {
		return false;
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World world, int i, int j, int k, Explosion explosion) {
		float fChanceOfPileDrop = 1.0F;
		
		if (explosion != null) {
			fChanceOfPileDrop = 1.0F / explosion.explosionSize;
		}
		
		int iNumDropped = getNumSawDustDroppedForType(world.getBlockMetadata(i, j, k));
		
		dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, iNumDropped, 0, fChanceOfPileDrop);
	}
	
	//------------- Class Specific Methods ------------//
	
	protected int getWoodTypeFromBlockID(int blockID) {
		int woodType;
		
		if (blockID == BTWBlocks.oakWoodMouldingAndDecorative.blockID) {
			woodType = 0;
		}
		else if (blockID == BTWBlocks.spruceWoodMouldingAndDecorative.blockID) {
			woodType = 1;
		}
		else if (blockID == BTWBlocks.birchWoodMouldingAndDecorative.blockID) {
			woodType = 2;
		}
		else if (blockID == BTWBlocks.jungleWoodMouldingAndDecorative.blockID) {
			woodType = 3;
		}
		else { // blood
			woodType = 4;
		}
		
		return woodType;
	}
	
	public int getNumSawDustDroppedForType(int iMetadata) {
		if (isDecorative(iMetadata)) {
			return 2;
		}
		
		return 1; // moulding
	}
	
	private int damageDropped(int blockID, int metadata) {
		int woodType = this.getWoodTypeFromBlockID(blockID);
		
		if (!isDecorative(metadata)) {
			return woodType;
		}
		
		int iBlockType;
		
		if (metadata == SUBTYPE_COLUMN) {
			iBlockType = WoodMouldingDecorativeStubBlockItem.TYPE_COLUMN;
		}
		else if (metadata == SUBTYPE_PEDESTAL_UP || metadata == SUBTYPE_PEDESTAL_DOWN) {
			iBlockType = WoodMouldingDecorativeStubBlockItem.TYPE_PEDESTAL;
		}
		else { // table
			iBlockType = WoodMouldingDecorativeStubBlockItem.TYPE_TABLE;
		}
		
		return WoodMouldingDecorativeStubBlockItem.getItemDamageForType(woodType, iBlockType);
	}
	
	//----------- Client Side Functionality -----------//
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k) {
		IBlockAccess blockAccess = renderBlocks.blockAccess;
		
		int iMetadata = blockAccess.getBlockMetadata(i, j, k);
		
		if (iMetadata == SUBTYPE_TABLE && blockID == BTWBlocks.oakWoodMouldingAndDecorative.blockID) {
			return renderOakTable(renderBlocks, blockAccess, i, j, k, this);
		}
		
		return super.renderBlock(renderBlocks, i, j, k);
	}
	
	@Environment(EnvType.CLIENT)
	static public boolean renderOakTable(RenderBlocks renderBlocks, IBlockAccess blockAccess, int i, int j, int k, Block block) {
		MouldingAndDecorativeBlock tableBlock = (MouldingAndDecorativeBlock) block;
		
		// render top
		
		renderBlocks.setRenderBounds(0.0F, 1.0F - TABLE_TOP_HEIGHT, 0.0F, 1.0F, 1.0F, 1.0F);
		
		RenderUtils.renderStandardBlockWithTexture(renderBlocks, block, i, j, k, ((AestheticNonOpaqueBlock) BTWBlocks.aestheticNonOpaque).iconTableWoodOakTop);
		
		if (tableBlock.doesTableHaveLeg(blockAccess, i, j, k)) {
			// render leg
			
			renderBlocks.setRenderBounds(0.5F - TABLE_LEG_HALF_WIDTH, 0.0F, 0.5F - TABLE_LEG_HALF_WIDTH, 0.5F + TABLE_LEG_HALF_WIDTH, TABLE_LEG_HEIGHT,
					0.5F + TABLE_LEG_HALF_WIDTH);
			
			RenderUtils
					.renderStandardBlockWithTexture(renderBlocks, block, i, j, k, ((AestheticNonOpaqueBlock) BTWBlocks.aestheticNonOpaque).iconTableWoodOakLeg);
		}
		
		return true;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness) {
		if (blockID == BTWItems.woodMouldingDecorativeStubID) {
			Block block = this;
			
			int iItemType = WoodMouldingDecorativeStubBlockItem.getBlockType(iItemDamage);
			int iWoodType = WoodMouldingDecorativeStubBlockItem.getWoodType(iItemDamage);
			
			if (iItemType == WoodMouldingDecorativeStubBlockItem.TYPE_COLUMN) {
				iItemDamage = SUBTYPE_COLUMN;
			}
			else if (iItemType == WoodMouldingDecorativeStubBlockItem.TYPE_PEDESTAL) {
				iItemDamage = SUBTYPE_PEDESTAL_UP;
			}
			else // table
			{
				iItemDamage = SUBTYPE_TABLE;
			}
			
			if (iWoodType == 0) {
				block = BTWBlocks.oakWoodMouldingAndDecorative;
			}
			else if (iWoodType == 1) {
				block = BTWBlocks.spruceWoodMouldingAndDecorative;
			}
			else if (iWoodType == 2) {
				block = BTWBlocks.birchWoodMouldingAndDecorative;
			}
			else if (iWoodType == 3) {
				block = BTWBlocks.jungleWoodMouldingAndDecorative;
			}
			else // 4
			{
				block = BTWBlocks.bloodWoodMouldingAndDecorative;
			}
			
			renderDecorativeInvBlock(renderBlocks, block, iItemDamage, fBrightness);
		}
		else {
			renderBlocks.setRenderBounds(getBlockBoundsFromPoolForItemRender(iItemDamage));
			
			Icon woodTexture;
			
			switch (iItemDamage) {
				case 1: // spruce
					
					woodTexture = BTWBlocks.spruceWoodMouldingAndDecorative.blockIcon;
					
					break;
				
				case 2: // birch
					
					woodTexture = BTWBlocks.birchWoodMouldingAndDecorative.blockIcon;
					
					break;
				
				case 3: // jungle
					
					woodTexture = BTWBlocks.jungleWoodMouldingAndDecorative.blockIcon;
					
					break;
				
				case 4: // blood
					
					woodTexture = BTWBlocks.bloodWoodMouldingAndDecorative.blockIcon;
					
					break;
				
				default: // oak
					
					woodTexture = BTWBlocks.oakWoodMouldingAndDecorative.blockIcon;
			}
			
			RenderUtils.renderInvBlockWithTexture(renderBlocks, this, -0.5F, -0.5F, -0.5F, woodTexture);
		}
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public int getDamageValue(World world, int x, int y, int z) {
		int blockID = world.getBlockId(x, y, z);
		int metadata = world.getBlockMetadata(x, y, z);
		int damageDropped = this.damageDropped(blockID, metadata);
		
		return damageDropped;
	}
}
