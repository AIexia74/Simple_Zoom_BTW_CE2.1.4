//FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.item.BTWItems;
import btw.item.blockitems.WoodSidingDecorativeStubBlockItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class WoodSidingAndCornerAndDecorativeBlock extends SidingAndCornerAndDecorativeBlock {
	public WoodSidingAndCornerAndDecorativeBlock(int iBlockID, String sTextureName, String name) {
		super(iBlockID, BTWBlocks.plankMaterial, sTextureName, 2F, 5F, Block.soundWoodFootstep, name);
		
		setAxesEffectiveOn(true);
		
		setBuoyancy(1.0F);
		
		setFireProperties(5, 20);
	}
	
	@Override
	public int idDropped(int iMetadata, Random random, int iFortuneModifier) {
		if (isDecorativeFromMetadata(iMetadata)) {
			return BTWItems.woodSidingDecorativeStubID;
		}
		else if (getIsCorner(iMetadata)) {
			return BTWItems.woodCornerStubID;
		}
		else {
			return BTWItems.woodSidingStubID;
		}
	}
	
	@Override
	public int damageDropped(int metadata) {
		return this.damageDropped(this.blockID, metadata);
	}
	
	@Override
	public boolean doesBenchHaveLeg(IBlockAccess blockAccess, int i, int j, int k) {
		if (blockID == BTWBlocks.oakWoodSidingAndCorner.blockID) {
			int iBlockBelowID = blockAccess.getBlockId(i, j - 1, k);
			
			if (iBlockBelowID == Block.fence.blockID) {
				return true;
			}
		}
		
		return super.doesBenchHaveLeg(blockAccess, i, j, k);
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
		
		if (blockID == BTWBlocks.oakWoodSidingAndCorner.blockID) {
			woodType = 0;
		}
		else if (blockID == BTWBlocks.spruceWoodSidingAndCorner.blockID) {
			woodType = 1;
		}
		else if (blockID == BTWBlocks.birchWoodSidingAndCorner.blockID) {
			woodType = 2;
		}
		else if (blockID == BTWBlocks.jungleWoodSidingAndCorner.blockID) {
			woodType = 3;
		}
		else { // blood
			woodType = 4;
		}
		
		return woodType;
	}
	
	public int getNumSawDustDroppedForType(int iMetadata) {
		if (this.isDecorativeFromMetadata(iMetadata) || !getIsCorner(iMetadata)) {
			return 2;
		}
		
		return 1; // corner
	}
	
	private int damageDropped(int blockID, int metadata) {
		int woodType = getWoodTypeFromBlockID(blockID);
		
		if (isDecorativeFromMetadata(metadata)) {
			int blockType;
			
			if (metadata == SUBTYPE_BENCH) {
				blockType = WoodSidingDecorativeStubBlockItem.TYPE_BENCH;
			}
			else { // fence
				blockType = WoodSidingDecorativeStubBlockItem.TYPE_FENCE;
			}
			
			return WoodSidingDecorativeStubBlockItem.getItemDamageForType(woodType, blockType);
		}
		else {
			return woodType;
		}
	}
	
	//----------- Client Side Functionality -----------//
	
	@Override
	@Environment(EnvType.CLIENT)
	public void getSubBlocks(int iBlockID, CreativeTabs creativeTabs, List list) {
		if (iBlockID == BTWBlocks.oakWoodSidingAndCorner.blockID) {
			// don't add the fence on oak siding since it's vanilla
			
			list.add(new ItemStack(iBlockID, 1, SUBTYPE_BENCH));
			
			list.add(new ItemStack(iBlockID, 1, 0));
			list.add(new ItemStack(iBlockID, 1, 1));
		}
		else {
			super.getSubBlocks(iBlockID, creativeTabs, list);
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
	
	@Override
	@Environment(EnvType.CLIENT)
	public void renderBlockAsItem(RenderBlocks renderBlocks, int itemDamage, float brightness) {
		int subtype = itemDamage;
		
		Block block = this;
		
		int itemType = WoodSidingDecorativeStubBlockItem.getBlockType(itemDamage);
		int woodType = WoodSidingDecorativeStubBlockItem.getWoodType(itemDamage);
		
		if (blockID == BTWItems.woodSidingDecorativeStubID) {
			if (itemType == WoodSidingDecorativeStubBlockItem.TYPE_BENCH) {
				subtype = SUBTYPE_BENCH;
			}
			else {
				subtype = SUBTYPE_FENCE;
			}
			
			if (woodType == 0) {
				block = BTWBlocks.oakWoodSidingAndCorner;
			}
			else if (woodType == 1) {
				block = BTWBlocks.spruceWoodSidingAndCorner;
			}
			else if (woodType == 2) {
				block = BTWBlocks.birchWoodSidingAndCorner;
			}
			else if (woodType == 3) {
				block = BTWBlocks.jungleWoodSidingAndCorner;
			}
			else {
				block = BTWBlocks.bloodWoodSidingAndCorner;
			}
		}
		
		if (subtype == SUBTYPE_BENCH) {
			renderBenchInvBlock(renderBlocks, block, subtype);
		}
		else if (subtype == SUBTYPE_FENCE) {
			renderFenceInvBlock(renderBlocks, block, subtype);
		}
		else {
			super.renderBlockAsItem(renderBlocks, itemDamage, brightness);
		}
	}
}