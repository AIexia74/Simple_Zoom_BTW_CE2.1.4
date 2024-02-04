// FCMOD

package btw.inventory.container;

import net.minecraft.src.*;

public class InventoryContainer extends Container
{
    public IInventory containerInventory;

	private int numSlotRows;
	private int numSlotColumns;

	private int numSlots;
	
    public InventoryContainer(IInventory playerInventory, IInventory containerInventory, int iNumSlotRows, int iNumSlotColumns,
                              int iContainerInventoryX, int iContainerInventoryY, int iPlayerInventoryX, int iPlayerInventoryY )
    {
        this.containerInventory = containerInventory;

        numSlotRows = iNumSlotRows;
        numSlotColumns = iNumSlotColumns;

        numSlots = numSlotRows * numSlotColumns;
    	
    	// vessel inventory slots
    	
        for (int iRow = 0; iRow < numSlotRows; iRow++ )
        {
        	for(int iColumn = 0; iColumn < numSlotColumns; iColumn++ )
        	{
        		addSlotToContainer( new Slot(containerInventory,
            		iColumn + iRow * numSlotColumns, // slot index
            		iContainerInventoryX + iColumn * 18,  // bitmap x pos
            		iContainerInventoryY + iRow * 18 ) ); // bitmap y pos
            }

        }

        // player inventory slots
        
        for ( int iRow = 0; iRow < 3; iRow++ )
        {
            for ( int iColumn = 0; iColumn < 9; iColumn++ )
            {
            	addSlotToContainer( new Slot( playerInventory, 
            		iColumn + iRow * 9 + 9, // slot index (incremented by 9 due to 9 slots used below ) 
            		iPlayerInventoryX + iColumn * 18, // bitmap x pos
            		iPlayerInventoryY + iRow * 18 ) ); // bitmap y pos
            }

        }

        for ( int iColumn = 0; iColumn < 9; iColumn++ )
        {
        	addSlotToContainer( new Slot( playerInventory, iColumn, 
        		iPlayerInventoryX + iColumn * 18, iPlayerInventoryY + 58 ) );
        }
    }
    
	@Override
    public boolean canInteractWith( EntityPlayer entityplayer )
    {
        return containerInventory.isUseableByPlayer(entityplayer);
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
            
            if (iSlotIndex < numSlots)
            {
                if ( !mergeItemStack(itemstack1, numSlots, inventorySlots.size(), true) )
                {
                    return null;
                }
            } 
            else if ( !mergeItemStack(itemstack1, 0, numSlots, false)  )
            {
                return null;
            }
            
            if ( itemstack1.stackSize == 0 )
            {
                slot.putStack( null );
            } 
            else
            {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }

	//------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//	
}