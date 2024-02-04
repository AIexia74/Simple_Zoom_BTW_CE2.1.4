// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.client.fx.BTWEffectManager;
import btw.item.BTWItems;
import btw.item.items.ChiselItem;
import btw.item.util.ItemUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class LogBlock extends BlockLog {
	public static Block[] chewedLogArray;
	
	public LogBlock(int iBlockID) {
		super(iBlockID, BTWBlocks.logMaterial);
		
		setHardness(1.25F); // vanilla 2
		setResistance(3.33F);  // odd value to match vanilla resistance set through hardness of 2
		
		setAxesEffectiveOn();
		setChiselsEffectiveOn();
		
		setBuoyant();
		
		setFireProperties(Flammability.LOGS);
		
		setStepSound(soundWoodFootstep);
		
		setUnlocalizedName("log");
	}
	
	@Override
	public float getBlockHardness(World world, int i, int j, int k) {
		float fHardness = super.getBlockHardness(world, i, j, k);
		
		int iMetadata = world.getBlockMetadata(i, j, k);
		
		if (getIsStump(iMetadata)) {
			fHardness *= 3;
		}
		
		return fHardness;
	}
	
	@Override
	public boolean getIsProblemToRemove(ItemStack toolStack, IBlockAccess blockAccess, int i, int j, int k) {
		if (getIsStump(blockAccess, i, j, k)) {
			return !isWorkStumpItemConversionTool(toolStack, (World) blockAccess, i, j, k);
		}
		
		return false;
	}
	
	
	@Override
	public boolean getDoesStumpRemoverWorkOnBlock(IBlockAccess blockAccess, int i, int j, int k) {
		return getIsStump(blockAccess, i, j, k);
	}
	
	@Override
	public boolean canConvertBlock(ItemStack stack, World world, int i, int j, int k) {
		return true;
	}
	
	@Override
	public boolean convertBlock(ItemStack stack, World world, int x, int y, int z, int iFromSide) {
		int oldMetadata = world.getBlockMetadata(x, y, z);
		int newMetadata = 0;
		
		int chewedLogID = chewedLogArray[oldMetadata & 3].blockID;
		
		if (getIsStump(oldMetadata)) {
			if (isWorkStumpItemConversionTool(stack, world, x, y, z)) {
				world.playAuxSFX(BTWEffectManager.SHAFT_RIPPED_OFF_EFFECT_ID, x, y, z, 0);
				world.setBlockAndMetadataWithNotify(x, y, z, BTWBlocks.workStump.blockID, oldMetadata & 3 | 8);
				return true;
			}
			else {
				newMetadata = BTWBlocks.oakChewedLog.setIsStump(oldMetadata & 12);
			}
		}
		else {
			int orientation = (oldMetadata >> 2) & 3;
			newMetadata = BTWBlocks.oakChewedLog.setOrientation(oldMetadata & 12, orientation);
		}
		
		world.setBlockAndMetadataWithNotify(x, y, z, chewedLogID, newMetadata);
		
		if (!world.isRemote) {
			ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(BTWItems.bark, 1, oldMetadata & 3), iFromSide);
		}
		
		return true;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int blockID, int metadata) {
		byte leafCheckRange = 4;
		int chunkCheckRange = leafCheckRange + 1;
		
		if (world.checkChunksExist(x - chunkCheckRange, y - chunkCheckRange, z - chunkCheckRange, x + chunkCheckRange, y + chunkCheckRange, z + chunkCheckRange)) {
			for (int i = -leafCheckRange; i <= leafCheckRange; ++i) {
				for (int j = -leafCheckRange; j <= leafCheckRange; ++j) {
					for (int k = -leafCheckRange; k <= leafCheckRange; ++k) {
						int offsetBlockID = world.getBlockId(x + i, y + j, z + k);
						Block offsetBlock = Block.blocksList[offsetBlockID];
						
						if (offsetBlock != null && offsetBlock.isLeafBlock(world, x, y, z)) {
							int oldMetadata = world.getBlockMetadata(x + i, y + j, z + k);
							
							if ((oldMetadata & 8) == 0) {
								world.setBlockMetadataWithNotify(x + i, y + j, z + k, oldMetadata | 8, 4);
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public boolean getCanBlockBeIncinerated(World world, int i, int j, int k) {
		return !getIsStump(world, i, j, k);
	}
	
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop) {
		dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, 6, 0, fChanceOfDrop);
		dropItemsIndividually(world, i, j, k, BTWItems.bark.itemID, 1, iMetadata & 3, fChanceOfDrop);
		
		return true;
	}
	
	@Override
	public void onDestroyedByFire(World world, int i, int j, int k, int iFireAge, boolean bForcedFireSpread) {
		convertToSmouldering(world, i, j, k);
	}
	
	@Override
	public int rotateMetadataAroundJAxis(int iMetadata, boolean bReverse) {
		int iAxisAlignment = iMetadata & 0xc;
		
		if (iAxisAlignment != 0) {
			if (iAxisAlignment == 4) {
				iAxisAlignment = 8;
			}
			else if (iAxisAlignment == 8) {
				iAxisAlignment = 4;
			}
			
			iMetadata = (iMetadata & (~0xc)) | iAxisAlignment;
		}
		
		return iMetadata;
	}
	
	@Override
	public int getFurnaceBurnTime(int iItemDamage) {
		return PlanksBlock.getFurnaceBurnTimeByWoodType(iItemDamage) * 4;
	}
	
	@Override
	public boolean isLog(IBlockAccess blockAccess, int x, int y, int z) {
		return true;
	}
	
	@Override
	public boolean canSupportLeaves(IBlockAccess blockAccess, int x, int y, int z) {
		return !this.isDeadStump(blockAccess, x, y, z);
	}
	
	//------------- Class Specific Methods ------------//
	
	public void convertToSmouldering(World world, int i, int j, int k) {
		int iNewMetadata = BTWBlocks.smolderingLog.setIsStump(0, getIsStump(world, i, j, k));
		
		world.setBlockAndMetadataWithNotify(i, j, k, BTWBlocks.smolderingLog.blockID, iNewMetadata);
	}
	
	public boolean getIsStump(int iMetadata) {
		return (iMetadata & 12) == 12;
	}
	
	public boolean getIsStump(IBlockAccess blockAccess, int i, int j, int k) {
		int iBlockID = blockAccess.getBlockId(i, j, k);
		
		if (iBlockID == Block.wood.blockID) {
			int iMetadata = blockAccess.getBlockMetadata(i, j, k);
			
			if (getIsStump(iMetadata)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isDeadStump(IBlockAccess blockAccess, int i, int j, int k) {
		if (getIsStump(blockAccess, i, j, k)) {
			int iBlockAboveID = blockAccess.getBlockMetadata(i, j + 1, k);
			
			if (iBlockAboveID != Block.wood.blockID) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isWorkStumpItemConversionTool(ItemStack stack, World world, int i, int j, int k) {
		if (stack != null && stack.getItem() instanceof ChiselItem) {
			int iToolLevel = ((ChiselItem) stack.getItem()).toolMaterial.getHarvestLevel();
			
			return iToolLevel >= 2;
		}
		
		return false;
	}
	
	//----------- Client Side Functionality -----------//
	
	@Environment(EnvType.CLIENT)
	public static final String[] topTextureTypes = new String[]{"tree_top", "fcBlockLogTopSpruce", "fcBlockLogTopBirch", "fcBlockLogTopJungle"};
	@Environment(EnvType.CLIENT)
	private Icon[] topIconArray;
	
	@Environment(EnvType.CLIENT)
	public static final String[] trunkTextureTypes = new String[]{"fcBlockTrunkOak", "fcBlockTrunkSpruce", "fcBlockTrunkBirch", "fcBlockTrunkJungle"};
	@Environment(EnvType.CLIENT)
	private Icon[] trunkIconArray;
	
	@Environment(EnvType.CLIENT)
	public static final String[] trunkTopTextureTypes =
			new String[]{"fcBlockTrunkTop", "fcBlockTrunkTopSpruce", "fcBlockTrunkTopBirch", "fcBlockTrunkTopJungle"};
	@Environment(EnvType.CLIENT)
	private Icon[] trunkTopIconArray;
	
	@Override
	@Environment(EnvType.CLIENT)
	public Icon getIcon(int side, int metadata) {
		int facing = metadata >> 2 & 3;
		
		if ((metadata & 12) == 12) {
			if (side > 1) {
				return trunkIconArray[metadata & 3];
			}
			else {
				return trunkTopIconArray[metadata & 3];
			}
		}
		else {
			if (facing == 0 && (side == 0 || side == 1)) {
				return topIconArray[metadata & 3];
			}
			else if (facing == 1 && (side == 4 || side == 5)) {
				return topIconArray[metadata & 3];
			}
			else if (facing == 2 && (side == 2 || side == 3)) {
				return topIconArray[metadata & 3];
			}
		}
		
		return super.getIcon(side, metadata);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		topIconArray = new Icon[trunkTextureTypes.length];
		trunkIconArray = new Icon[trunkTextureTypes.length];
		trunkTopIconArray = new Icon[trunkTextureTypes.length];
		
		for (int i = 0; i < trunkIconArray.length; i++) {
			topIconArray[i] = iconRegister.registerIcon(topTextureTypes[i]);
			trunkIconArray[i] = iconRegister.registerIcon(trunkTextureTypes[i]);
			trunkTopIconArray[i] = iconRegister.registerIcon(trunkTopTextureTypes[i]);
		}
		
		super.registerIcons(iconRegister);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean renderBlock(RenderBlocks renderer, int i, int j, int k) {
		renderer.setRenderBounds(0D, 0D, 0D, 1D, 1D, 1D);
		
		return renderer.renderBlockLog(this, i, j, k);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void renderBlockSecondPass(RenderBlocks renderBlocks, int i, int j, int k, boolean bFirstPassResult) {
		renderCookingByKiLnOverlay(renderBlocks, i, j, k, bFirstPassResult);
	}
}