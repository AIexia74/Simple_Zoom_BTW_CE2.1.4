package btw.block.tileentity.dispenser;

import btw.client.fx.BTWEffectManager;
import net.minecraft.src.*;

public class MortarApplicationDispenserBehavior extends BehaviorDefaultDispenseItem {
	@Override
	public ItemStack dispenseStack(IBlockSource blockSource, ItemStack stack) {
		EnumFacing facing = BlockDispenser.getDispenserFacing(blockSource.getBlockMetadata());
        int x = blockSource.getXInt() + facing.getFrontOffsetX();
        int y = blockSource.getYInt();
        int z = blockSource.getZInt() + facing.getFrontOffsetZ();
		
        Block blockToFront = Block.blocksList[blockSource.getWorld().getBlockId(x, y, z)];
        
        if (blockToFront != null && blockToFront.onMortarApplied(blockSource.getWorld(), x, y, z)) {
        	blockSource.getWorld().playAuxSFX(BTWEffectManager.APPLY_MORTAR_EFFECT_ID, x, y, z, 0 );
        	
        	stack.stackSize--;
    		return stack;
        }
        else {
        	return super.dispenseStack(blockSource, stack);
        }
    }
}