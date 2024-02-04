package btw.block.tileentity;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;

public class LoomTileEntity extends TileEntity implements IInventory {
	@Override
	public int getSizeInventory() {
		return 0;
	}
	
	@Override
	public ItemStack getStackInSlot(int var1) {
		return null;
	}
	
	@Override
	public ItemStack decrStackSize(int var1, int var2) {
		return null;
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return null;
	}
	
	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
	
	}
	
	@Override
	public String getInvName() {
		return null;
	}
	
	@Override
	public boolean isInvNameLocalized() {
		return false;
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 0;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return false;
	}
	
	@Override
	public void openChest() {
	
	}
	
	@Override
	public void closeChest() {
	
	}
	
	@Override
	public boolean isStackValidForSlot(int var1, ItemStack var2) {
		return false;
	}
}
