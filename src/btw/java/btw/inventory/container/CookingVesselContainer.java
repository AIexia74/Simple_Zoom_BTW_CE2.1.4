// FCMOD

package btw.inventory.container;

import btw.block.tileentity.CookingVesselTileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICrafting;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;

import java.util.Iterator;

public class CookingVesselContainer extends InventoryContainer
{
    private CookingVesselTileEntity associatedTileEntity;

    private int lastCookCounter;

    public CookingVesselContainer(IInventory playerinventory, CookingVesselTileEntity tileEntity )
    {
    	super( playerinventory, tileEntity, 3, 9, 8, 43, 8, 111 );

        associatedTileEntity = tileEntity;

        lastCookCounter = 0;
    }

	@Override
    public ItemStack slotClick(int i, int j, int k, EntityPlayer entityplayer)
    {
    	// this is necessary as not all slot clicks properly generate onInventoryChanged events

    	ItemStack returnValue = super.slotClick( i, j, k, entityplayer );
    	
    	associatedTileEntity.onInventoryChanged();
    	
    	return returnValue; 
    }
    
	@Override
    public void addCraftingToCrafters( ICrafting craftingInterface ) // client
    //public void onCraftGuiOpened( ICrafting craftingInterface ) // server
    {
        super.addCraftingToCrafters( craftingInterface ); // client
        //super.onCraftGuiOpened( craftingInterface ); // server
        
        craftingInterface.sendProgressBarUpdate(this, 0, associatedTileEntity.scaledCookCounter);
    }
	
	@Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        
        Iterator iterator = crafters.iterator();

        while ( iterator.hasNext() )
        {
            ICrafting icrafting = (ICrafting)iterator.next();

            if (lastCookCounter != associatedTileEntity.scaledCookCounter)
            {
                icrafting.sendProgressBarUpdate(this, 0, associatedTileEntity.scaledCookCounter);
            }
        }

        lastCookCounter = associatedTileEntity.scaledCookCounter;
    }

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void updateProgressBar( int iVariableIndex, int iValue )
    {
        if ( iVariableIndex == 0 )
        {
            associatedTileEntity.scaledCookCounter = iValue;
        }
    }
}