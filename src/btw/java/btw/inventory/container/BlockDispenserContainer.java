// FCMOD

package btw.inventory.container;

import btw.block.tileentity.dispenser.BlockDispenserTileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Iterator;

public class BlockDispenserContainer extends Container
{
    private BlockDispenserTileEntity localTileEntity;

	private static final int NUM_SLOTS = 16;
	
    private int lastNextSlotIndexToDispense;
	
    public BlockDispenserContainer(IInventory iinventory, BlockDispenserTileEntity tileEntityBlockDispenser )
    {
        localTileEntity = tileEntityBlockDispenser;
    	
    	localTileEntity.openChest();
        
        for(int i = 0; i < 4; i++)
        {
            for(int l = 0; l < 4; l++)
            {
            	addSlotToContainer(new Slot(tileEntityBlockDispenser, l + i * 4, 53 + l * 18, 17 + i * 18));
            }
        }

        for(int j = 0; j < 3; j++)
        {
            for(int i1 = 0; i1 < 9; i1++)
            {
            	addSlotToContainer(new Slot(iinventory, i1 + j * 9 + 9, 8 + i1 * 18, 102 + j * 18));
            }

        }

        for(int k = 0; k < 9; k++)
        {
        	addSlotToContainer(new Slot(iinventory, k, 8 + k * 18, 160));
        }

    }

	@Override
    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return localTileEntity.isUseableByPlayer(entityplayer);
    }
    
	@Override
    public ItemStack transferStackInSlot(EntityPlayer player, int iSlotIndex )
    {
        // this function is performed when the gui is shift-clicked on
    	
        ItemStack itemstack = null;
        Slot slot = (Slot)inventorySlots.get( iSlotIndex );
        
        if ( slot != null && slot.getHasStack() )
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            
            if (iSlotIndex < NUM_SLOTS)
            {
                if ( !mergeItemStack(itemstack1, NUM_SLOTS, inventorySlots.size(), true) )
                {
                    return null;
                }
            } 
            else if ( !mergeItemStack(itemstack1, 0, NUM_SLOTS, false)  )
            {
                return null;
            }
            
            if ( itemstack1.stackSize == 0 )
            {
                slot.putStack(null);
            } 
            else
            {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }
    
	@Override
    public ItemStack slotClick( int i, int j, int k, EntityPlayer entityplayer )
    {
    	// manual changes to the dispensers inventory cause the order to reset
    	
    	if (i < NUM_SLOTS)
		{
            localTileEntity.nextSlotIndexToDispense = 0;
		}
    	
    	return super.slotClick( i, j, k, entityplayer);    	
    }
    
	@Override
    public void onCraftGuiClosed(EntityPlayer entityplayer)
    {
        super.onCraftGuiClosed(entityplayer);
        
        localTileEntity.closeChest();
    }
	
	@Override
    public void addCraftingToCrafters( ICrafting craftingInterface )
    //public void onCraftGuiOpened( ICrafting craftingInterface )
    {
        super.addCraftingToCrafters( craftingInterface );
        //super.onCraftGuiOpened( craftingInterface );
        
        craftingInterface.sendProgressBarUpdate(this, 0, localTileEntity.nextSlotIndexToDispense);
    }
	
	@Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        
        Iterator iterator = crafters.iterator();

        do
        {
            if (!iterator.hasNext())
            {
                break;
            }

            ICrafting icrafting = (ICrafting)iterator.next();

            if (lastNextSlotIndexToDispense != localTileEntity.nextSlotIndexToDispense)
            {
                icrafting.sendProgressBarUpdate(this, 0, localTileEntity.nextSlotIndexToDispense);
            }
        }
        while (true);

        lastNextSlotIndexToDispense = localTileEntity.nextSlotIndexToDispense;
    }

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void updateProgressBar( int iVariableIndex, int iValue )
    {
        if ( iVariableIndex == 0 )
        {
            localTileEntity.nextSlotIndexToDispense = iValue;
        }
    }
}