// FCMOD

package btw.inventory.container;

import btw.block.tileentity.LoomTileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Iterator;

public class LoomContainer extends Container {
	private static final int NUM_HOPPER_SLOT_ROWS = 2;
	private static final int NUM_HOPPER_SLOT_COLUMNS = 9;
	
	private static final int NUM_HOPPER_SLOTS = (NUM_HOPPER_SLOT_ROWS * NUM_HOPPER_SLOT_COLUMNS);
	
	private final LoomTileEntity loomEntity;
	
	private int lastMechanicalPowerIndicator;
	
	public LoomContainer(IInventory playerinventory, LoomTileEntity loomEntity) {
		this.loomEntity = loomEntity;
		this.lastMechanicalPowerIndicator = 0;
		
		// cauldron inventory slots
		for (int iRow = 0; iRow < NUM_HOPPER_SLOT_ROWS; iRow++) {
			for (int iColumn = 0; iColumn < NUM_HOPPER_SLOT_COLUMNS; iColumn++) {
				addSlotToContainer(new Slot(loomEntity, iColumn + iRow * NUM_HOPPER_SLOT_COLUMNS, // slot index
						8 + iColumn * 18,  // bitmap x pos
						60 + iRow * 18)); // bitmap y pos
			}
		}
		
		// draw filter slot
		addSlotToContainer(new Slot(loomEntity, 18, // slot index
				80,  // bitmap x pos
				37)); // bitmap y pos
		
		// player inventory slots
		
		for (int iRow = 0; iRow < 3; iRow++) {
			for (int iColumn = 0; iColumn < 9; iColumn++) {
				addSlotToContainer(new Slot(playerinventory, iColumn + iRow * 9 + 9, // slot index (incremented by 9 due to 9 slots used below )
						8 + iColumn * 18, // bitmap x pos
						111 + iRow * 18)); // bitmap y pos
			}
			
		}
		
		for (int iColumn = 0; iColumn < 9; iColumn++) {
			addSlotToContainer(new Slot(playerinventory, iColumn, 8 + iColumn * 18, 169));
		}
		
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return loomEntity.isUseableByPlayer(entityplayer);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int iSlotIndex) {
		// this function is performed when the gui is shift-clicked on
		
		ItemStack clickedStack = null;
		Slot slot = (Slot) inventorySlots.get(iSlotIndex);
		
		if (slot != null && slot.getHasStack()) {
			ItemStack processedStack = slot.getStack();
			clickedStack = processedStack.copy();
			
			if (iSlotIndex < NUM_HOPPER_SLOTS + 1) {
				if (!mergeItemStack(processedStack, NUM_HOPPER_SLOTS + 1, inventorySlots.size(), true)) {
					return null;
				}
			}
			else if (!mergeItemStack(processedStack, 0, NUM_HOPPER_SLOTS, false)) {
				return null;
			}
			
			if (processedStack.stackSize == 0) {
				slot.putStack(null);
			}
			else {
				slot.onSlotChanged();
			}
		}
		return clickedStack;
	}
	
	@Override
	public void addCraftingToCrafters(ICrafting craftingInterface)
	//public void onCraftGuiOpened( ICrafting craftingInterface )
	{
		super.addCraftingToCrafters(craftingInterface);
		//super.onCraftGuiOpened( craftingInterface );
		
		//craftingInterface.sendProgressBarUpdate(this, 0, loomEntity.mechanicalPowerIndicator);
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		
		Iterator iterator = crafters.iterator();
		
		do {
			if (!iterator.hasNext()) {
				break;
			}
			
			ICrafting icrafting = (ICrafting) iterator.next();
			
			//if (lastMechanicalPowerIndicator != loomEntity.mechanicalPowerIndicator) {
			//	icrafting.sendProgressBarUpdate(this, 0, loomEntity.mechanicalPowerIndicator);
			//}
		} while (true);
		
		//lastMechanicalPowerIndicator = loomEntity.mechanicalPowerIndicator;
	}
	
	//----------- Client Side Functionality -----------//
	
	@Override
	@Environment(EnvType.CLIENT)
	public void updateProgressBar(int iVariableIndex, int iValue) {
		if (iVariableIndex == 0) {
			//loomEntity.mechanicalPowerIndicator = iValue;
		}
	}
}