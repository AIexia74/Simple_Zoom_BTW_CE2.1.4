package btw.item.items;

import btw.block.BTWBlocks;
import btw.block.blocks.CandleBlock;
import btw.util.ColorUtils;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class CandleItem extends PlaceAsBlockItem {
	public CandleItem(int itemID) {
		super(itemID, BTWBlocks.plainCandle.blockID, 0, "fcItemCandle");

		this.setCreativeTab(CreativeTabs.tabDecorations);
		this.setHasSubtypes(true);

        this.setBuoyant();
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int facing, float clickX, float clickY, float clickZ) {
		if (itemStack.stackSize == 0) {
			return false;
		}

		if (!player.canPlayerEdit(x, y, z, facing, itemStack)) {
			return false;
		}

		if (attemptToCombineWithBlock(itemStack, player, world, x, y, z)) {
			return true;
		}

		BlockPos targetPos = new BlockPos(x, y, z, facing);

		if (attemptToCombineWithBlock(itemStack, player, world, targetPos.x, targetPos.y, targetPos.z)) {
			return true;
		}
		else {
			return super.onItemUse(itemStack, player, world, x, y, z, facing, clickX, clickY, clickZ);
		}
	}

	@Override
	public int getBlockIDToPlace(int itemDamage, int facing, float clickX, float clickY, float clickZ) {
		return this.getBlockIDForItemDamage(itemDamage);
	}

	@Override
	public String getItemDisplayName(ItemStack stack) {
		int itemDamage = stack.getItemDamage();
		
		if (itemDamage < 16) {
			return (StringTranslate.getInstance().translateNamedKey( "candle." + ColorUtils.colorOrder[itemDamage])).trim();
		}
		else {
			return super.getItemDisplayName(stack);
		}
	}

	//------------- Class Specific Methods ------------//

	public boolean attemptToCombineWithBlock(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z) {
		if (canCombineWithBlock(world, x, y, z, itemStack.getItemDamage())) {
			int targetBlockID = world.getBlockId(x, y, z);
			Block targetBlock = Block.blocksList[targetBlockID];

			if (incrementCandleCount(world, x, y, z)) {
				world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, 
						targetBlock.getStepSound(world, x, y, z).getPlaceSound(),
                                      (targetBlock.getStepSound(world, x, y, z).getPlaceVolume() + 1.0F) / 2.0F,
                                      targetBlock.getStepSound(world, x, y, z).getPlacePitch() * 0.8F);

				itemStack.stackSize--;
				
				return true;
			}
		}

		return false;
	}

	public boolean canCombineWithBlock(World world, int x, int y, int z, int itemDamage) {
		int blockID = world.getBlockId(x, y, z);
		Block block = Block.blocksList[blockID];

		if (blockID == this.getBlockIDForItemDamage(itemDamage)) {
			if (((CandleBlock) block).getCandleCount(world, x, y, z) < 4) {
				return true;
			}
		}

		return false;
	}
	
	public boolean incrementCandleCount(World world, int x, int y, int z) {
		int candleCount = ((CandleBlock) BTWBlocks.plainCandle).getCandleCount(world, x, y, z);
		((CandleBlock) BTWBlocks.plainCandle).setCandleCount(world, x, y, z, candleCount + 1);
		return true;
	}
	
	public int getBlockIDForItemDamage(int itemDamage) {
		if (itemDamage < 16) {
			return BTWBlocks.coloredCandle[itemDamage].blockID;
		}
		else {
			return BTWBlocks.plainCandle.blockID;
		}
	}

	//----------- Client Side Functionality -----------//
	
	private Icon[] icons = new Icon[17];
	
	@Override
	public void registerIcons(IconRegister register) {
		for (int i = 0; i < 16; i++) {
			icons[i] = register.registerIcon("fcItemCandle_" + ColorUtils.colorOrder[i]);
		}
		
		icons[16] = register.registerIcon("fcItemCandle_plain");
	}
	
	@Override
	public Icon getIconFromDamage(int itemDamage) {
		return icons[itemDamage];
	}

	@Override
	public void getSubItems(int itemID, CreativeTabs creativeTabs, List list) {
		for (int i = 0; i <= 16; i++) {
			list.add(new ItemStack(itemID, 1, i));
		}
	}
	
    @Override
	@Environment(EnvType.CLIENT)
    public boolean canPlaceItemBlockOnSide(World world, int x, int y, int z, int facing, EntityPlayer player, ItemStack itemStack) {
    	BlockPos targetPos = new BlockPos(x, y, z);
    	
        if (canCombineWithBlock(world, targetPos.x, targetPos.y, targetPos.z, itemStack.getItemDamage())) {
            return true;
        }
        
        targetPos.addFacingAsOffset(facing);
        
        if (canCombineWithBlock(world, targetPos.x, targetPos.y, targetPos.z, itemStack.getItemDamage())) {
        	return true;
        }
        
        return super.canPlaceItemBlockOnSide(world, x, y, z, facing, player, itemStack);
    }
}