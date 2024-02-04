// FCMOD

package btw.block.blocks;

import btw.client.fx.BTWEffectManager;
import btw.item.BTWItems;
import btw.item.items.ChiselItem;
import btw.item.items.PickaxeItem;
import btw.item.items.ToolItem;
import btw.item.util.ItemUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class RoughStoneBlock extends FullBlock {
	public static RoughStoneBlock[] strataLevelBlockArray = new RoughStoneBlock[3];
	
	public int strataLevel;
	
	public RoughStoneBlock(int iBlockID, int iStrataLevel) {
		super(iBlockID, Material.rock);
		
		strataLevel = iStrataLevel;
		strataLevelBlockArray[iStrataLevel] = this;
		
		if (iStrataLevel == 0) {
			setHardness(2.25F);
			setResistance(10F);
		}
		else if (iStrataLevel == 1) {
			setHardness(3F);
			setResistance(13F);
		}
		else // 2
		{
			setHardness(4.5F);
			setResistance(20F);
		}
		
		setPicksEffectiveOn();
		setChiselsEffectiveOn();
		
		setStepSound(soundStoneFootstep);
		
		setUnlocalizedName("fcBlockStoneRough");
	}
	
	@Override
	public boolean canConvertBlock(ItemStack stack, World world, int i, int j, int k) {
		if (strataLevel == 0) {
			// special case for stone pick so that it harvests top strata stone, but as an improper tool
			
			if (stack != null && stack.getItem() instanceof PickaxeItem) {
				int iToolLevel = ((ToolItem) stack.getItem()).toolMaterial.getHarvestLevel();
				
				if (iToolLevel <= 1) {
					return false;
				}
			}
			
		}
		
		return true;
	}
	
	@Override
	public boolean convertBlock(ItemStack stack, World world, int i, int j, int k, int iFromSide) {
		int iMetadata = world.getBlockMetadata(i, j, k);
		
		if (iMetadata < 15) {
			iMetadata++;
			
			if (!world.isRemote && isEffectiveItemConversionTool(stack, world, i, j, k)) {
				if (iMetadata <= 8) {
					if ((iMetadata & 1) == 0) {
						world.playAuxSFX(BTWEffectManager.STONE_RIPPED_OFF_EFFECT_ID, i, j, k, 0);
						
						ItemUtils.ejectStackFromBlockTowardsFacing(world, i, j, k, new ItemStack(BTWItems.stone, 1, strataLevel), iFromSide);
					}
					else if (iMetadata <= 5 && isUberItemConversionTool(stack, world, i, j, k)) {
						// iron or better chisel on top two strata ejects bricks instead
						// of loose stones
						
						iMetadata += 3;
						
						world.playAuxSFX(BTWEffectManager.STONE_RIPPED_OFF_EFFECT_ID, i, j, k, 0);
						
						ItemUtils.ejectStackFromBlockTowardsFacing(world, i, j, k, new ItemStack(BTWItems.stoneBrick, 1, strataLevel), iFromSide);
					}
				}
				else if (iMetadata == 12) {
					world.playAuxSFX(BTWEffectManager.GRAVEL_RIPPED_OFF_EFFECT_ID, i, j, k, 0);
					
					ItemUtils.ejectStackFromBlockTowardsFacing(world, i, j, k, new ItemStack(BTWItems.gravelPile, 1), iFromSide);
				}
			}
			
			world.setBlockMetadataWithNotify(i, j, k, iMetadata);
			
			return true;
		}
		else {
			// final stage resulting in destruction
			
			if (!world.isRemote && isEffectiveItemConversionTool(stack, world, i, j, k)) {
				world.playAuxSFX(BTWEffectManager.GRAVEL_RIPPED_OFF_EFFECT_ID, i, j, k, 0);
				
				ItemUtils.dropStackAsIfBlockHarvested(world, i, j, k, new ItemStack(BTWItems.gravelPile, 1));
			}
			
			return false;
		}
	}
	
	@Override
	public void dropBlockAsItemWithChance(World world, int i, int j, int k, int iMetadata, float fChance, int iFortuneModifier) {
		if (!world.isRemote) {
			int iItemIDDropped = BTWItems.stone.itemID;
			int iNumDropped = 1;
			int metadataDropped = strataLevel;
			
			if (iMetadata < 8) {
				iNumDropped = 8 - (iMetadata / 2);
			}
			else {
				iItemIDDropped = BTWItems.gravelPile.itemID;
				metadataDropped = 0;
				
				if (iMetadata < 12) {
					iNumDropped = 2;
				}
			}
			
			for (int iTempCount = 0; iTempCount < iNumDropped; iTempCount++) {
				dropBlockAsItem_do(world, i, j, k, new ItemStack(iItemIDDropped, 1, metadataDropped));
			}
		}
	}
	
	@Override
	public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata) {
		world.playAuxSFX(BTWEffectManager.GRAVEL_RIPPED_OFF_EFFECT_ID, i, j, k, 0);
		
		dropComponentItemsWithChance(world, i, j, k, iMetadata, 1F);
	}
	
	@Override
	public boolean canDropFromExplosion(Explosion explosion) {
		return false;
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World world, int i, int j, int k, Explosion explosion) {
		int iMetadata = world.getBlockMetadata(i, j, k);
		
		float fChanceOfPileDrop = 1.0F;
		
		if (explosion != null) {
			fChanceOfPileDrop = 1.0F / explosion.explosionSize;
		}
		
		dropComponentItemsWithChance(world, i, j, k, iMetadata, fChanceOfPileDrop);
	}
	
	@Override
	public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k) {
		if (strataLevel > 1) {
			return strataLevel + 1;
		}
		
		return 2;
	}
	
	@Override
	public int getEfficientToolLevel(IBlockAccess blockAccess, int i, int j, int k) {
		if (strataLevel > 0) {
			return strataLevel + 1;
		}
		
		return 0;
	}
	
	@Override
	public boolean arechiselseffectiveon(World world, int i, int j, int k) {
		if (world.getBlockMetadata(i, j, k) >= 8) {
			// chisels stop being effective once all the whole stone items are harvested, to make tunneling problematic
			
			return false;
		}
		
		return super.arechiselseffectiveon(world, i, j, k);
	}
	
	@Override
	protected boolean canSilkHarvest() {
		return false;
	}
	
	@Override
	public boolean isNaturalStone(IBlockAccess blockAccess, int i, int j, int k) {
		return true;
	}
	
	@Override
	public ItemStack getStackRetrievedByBlockDispenser(World world, int i, int j, int k) {
		if (world.getBlockMetadata(i, j, k) != 0) {
			return null;
		}
		else {
			return super.getStackRetrievedByBlockDispenser(world, i, j, k);
		}
	}
	
	@Override
	public void onRemovedByBlockDispenser(World world, int i, int j, int k) {
		int metadata = world.getBlockMetadata(i, j, k);
		
		if (metadata != 0) {
			this.dropBlockAsItem(world, i, j, k, metadata, 0);
		}
		
		super.onRemovedByBlockDispenser(world, i, j, k);
	}
	
	//------------- Class Specific Methods ------------//
	
	public boolean isEffectiveItemConversionTool(ItemStack stack, World world, int i, int j, int k) {
		if (stack != null && stack.getItem() instanceof ChiselItem) {
			int iToolLevel = ((ChiselItem) stack.getItem()).toolMaterial.getHarvestLevel();
			
			return iToolLevel >= getEfficientToolLevel(world, i, j, k);
		}
		
		return false;
	}
	
	public boolean isUberItemConversionTool(ItemStack stack, World world, int i, int j, int k) {
		if (stack != null && stack.getItem() instanceof ChiselItem) {
			int iToolLevel = ((ChiselItem) stack.getItem()).toolMaterial.getHarvestLevel();
			
			return iToolLevel >= getUberToolLevel(world, i, j, k);
		}
		
		return false;
	}
	
	public int getUberToolLevel(IBlockAccess blockAccess, int i, int j, int k) {
		return 2;
	}
	
	private void dropComponentItemsWithChance(World world, int i, int j, int k, int iMetadata, float fChanceOfItemDrop) {
		if (iMetadata < 8) {
			int iNumStoneDropped = 4 - (iMetadata / 2);
			
			dropItemsIndividually(world, i, j, k, BTWItems.stone.itemID, iNumStoneDropped, strataLevel, fChanceOfItemDrop);
		}
		
		int iNumGravelDropped = 1;
		
		if (iMetadata < 12) {
			iNumGravelDropped = 2;
		}
		
		dropItemsIndividually(world, i, j, k, BTWItems.gravelPile.itemID, iNumGravelDropped, 0, fChanceOfItemDrop);
	}
	
	//----------- Client Side Functionality -----------//
	
	@Environment(EnvType.CLIENT)
	private Icon iconBroken;
	@Environment(EnvType.CLIENT)
	private Icon[] crackIcons;
	
	@Override
	@Environment(EnvType.CLIENT)
	public void registerIcons(IconRegister register) {
		String sBaseName = getUnlocalizedName2(); // "2" function strips "tile." at start of name
		
		if (strataLevel > 0) {
			sBaseName += "_" + strataLevel;
		}
		
		blockIcon = register.registerIcon(sBaseName);
		
		iconBroken = register.registerIcon(sBaseName + "_broken");
		
		crackIcons = new Icon[7];
		
		for (int iTempIndex = 0; iTempIndex < 7; iTempIndex++) {
			crackIcons[iTempIndex] = register.registerIcon("fcOverlayStoneRough_" + (iTempIndex + 1));
		}
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public Icon getIcon(int iSide, int iMetadata) {
		if (iMetadata >= 8) {
			return iconBroken;
		}
		
		return blockIcon;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void renderBlockSecondPass(RenderBlocks renderBlocks, int i, int j, int k, boolean bFirstPassResult) {
		if (bFirstPassResult) {
			IBlockAccess blockAccess = renderBlocks.blockAccess;
			int iMetadata = blockAccess.getBlockMetadata(i, j, k);
			
			if (iMetadata > 0 && iMetadata != 8) {
				int iTextureIndex = 0;
				
				if (iMetadata < 8) {
					iTextureIndex = MathHelper.clamp_int(iMetadata - 1, 0, 6);
				}
				else {
					iTextureIndex = MathHelper.clamp_int(iMetadata - 9, 0, 6);
				}
				
				Icon overlayTexture = crackIcons[iTextureIndex];
				
				if (overlayTexture != null) {
					renderBlockWithTexture(renderBlocks, i, j, k, overlayTexture);
				}
			}
		}
	}
}