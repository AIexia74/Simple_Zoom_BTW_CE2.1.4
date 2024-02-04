// FCMOD

package btw.inventory.container;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

public class WorkbenchContainer extends ContainerWorkbench
{
	public World world;
	public int blockX, blockY, blockZ;
	
    public WorkbenchContainer(InventoryPlayer inventory, World world, int i, int j, int k )
    {
    	super( inventory, world, i, j, k );

        this.world = world;

        blockX = i;
        blockY = j;
        blockZ = k;
    }

    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
    	int iBlockID = world.getBlockId(blockX, blockY, blockZ);
    	
    	return ( iBlockID == BTWBlocks.workStump.blockID ||
    		iBlockID == BTWBlocks.workbench.blockID ||
    		iBlockID == Block.anvil.blockID ||
    		iBlockID == Block.workbench.blockID ) &&
               par1EntityPlayer.getDistanceSq(blockX + 0.5D, blockY + 0.5D, blockZ + 0.5D) <= 64D;
    }

    @Override
    public ItemStack transferStackInSlot( EntityPlayer player, int iSlotClicked )
    {
        ItemStack oldStackInSlotClicked = null;
        Slot slotClicked = (Slot)inventorySlots.get( iSlotClicked );

        if ( slotClicked != null && slotClicked.getHasStack() )
        {
            ItemStack newStackInSlotClicked = slotClicked.getStack();
            oldStackInSlotClicked = newStackInSlotClicked.copy();

            if ( iSlotClicked == 0 )  
            {
            	// crafting result slot
            	
                if (!this.mergeItemStack( newStackInSlotClicked, 10, 46, true ) )
                {
                    return null;
                }

                slotClicked.onSlotChange(newStackInSlotClicked, oldStackInSlotClicked);
            }
            else if (iSlotClicked >= 10 && iSlotClicked < 37)
            {
            	// Main inventory clicked, drop into the crafting grid
            	
                if ( !mergeItemStack( newStackInSlotClicked, 1, 10, false ) )
                {
                    return null;
                }
            }
            else if ( iSlotClicked >= 37 && iSlotClicked < 46 )
            {
            	// Hotbar clicked, drop into the crafting grid
            	
                if ( !mergeItemStack( newStackInSlotClicked, 1, 10, false ) )
                {
                    return null;
                }
            }
            else if ( !mergeItemStack( newStackInSlotClicked, 10, 46, true ) )
            {
                return null;
            }

            if ( newStackInSlotClicked.stackSize == 0 )
            {
                slotClicked.putStack( (ItemStack)null );
            }
            else
            {
                slotClicked.onSlotChanged();
            }

            if ( newStackInSlotClicked.stackSize == oldStackInSlotClicked.stackSize )
            {
                return null;
            }

            slotClicked.onPickupFromSlot( player, newStackInSlotClicked );
        }

        return oldStackInSlotClicked;
    }
}