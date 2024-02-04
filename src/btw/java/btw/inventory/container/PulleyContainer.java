// FCMOD

package btw.inventory.container;

import btw.block.tileentity.PulleyTileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Iterator;

public class PulleyContainer extends Container
{
	private static final int NUM_SLOT_ROWS = 2;
	private static final int NUM_SLOT_COLUMNS = 2;

	private static final int NUM_SLOTS = (NUM_SLOT_ROWS * NUM_SLOT_COLUMNS);
	
    private PulleyTileEntity localTileEntity;

    private int lastMechanicalPowerIndicator;
    
    public PulleyContainer(IInventory playerinventory, PulleyTileEntity tileEntityPulley )
    {
        localTileEntity = tileEntityPulley;

        lastMechanicalPowerIndicator = 0;
    	
    	// pulley inventory slots
    	
        for (int iRow = 0; iRow < NUM_SLOT_ROWS; iRow++ )
        {
        	for(int iColumn = 0; iColumn < NUM_SLOT_COLUMNS; iColumn++ )
        	{
        		addSlotToContainer( new Slot(tileEntityPulley,
                		iColumn + iRow * NUM_SLOT_COLUMNS, // slot index
                		71 + ( iColumn ) * 18,  // bitmap x pos
                		43 + iRow * 18 ) ); // bitmap y pos
            }
        }

        // player inventory slots
        
        for ( int iRow = 0; iRow < 3; iRow++ )
        {
            for ( int iColumn = 0; iColumn < 9; iColumn++ )
            {
            	addSlotToContainer( new Slot( playerinventory, 
                		iColumn + iRow * 9 + 9, // slot index (incremented by 9 due to 9 slots used below ) 
                		8 + iColumn * 18, // bitmap x pos
                		93 + iRow * 18 ) ); // bitmap y pos
            }
        }

        // player hot-bar
        
        for ( int iColumn = 0; iColumn < 9; iColumn++ )
        {
        	addSlotToContainer( new Slot( playerinventory, iColumn, 8 + iColumn * 18, 151 ) );
        }
    }

	@Override
    public boolean canInteractWith( EntityPlayer entityplayer )
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
                slot.putStack( null );
            } 
            else
            {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }
	
	@Override
    public void addCraftingToCrafters( ICrafting craftingInterface )
    //public void onCraftGuiOpened( ICrafting craftingInterface )
    {
        super.addCraftingToCrafters( craftingInterface );
        //super.onCraftGuiOpened( craftingInterface );
        
        craftingInterface.sendProgressBarUpdate(this, 0, localTileEntity.mechanicalPowerIndicator);
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

            if (lastMechanicalPowerIndicator != localTileEntity.mechanicalPowerIndicator)
            {
                icrafting.sendProgressBarUpdate(this, 0, localTileEntity.mechanicalPowerIndicator);
            }
        }
        while (true);

        lastMechanicalPowerIndicator = localTileEntity.mechanicalPowerIndicator;
    }

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void updateProgressBar( int iVariableIndex, int iValue )
    {
        if ( iVariableIndex == 0 )
        {
            localTileEntity.mechanicalPowerIndicator = iValue;
        }
    }
}