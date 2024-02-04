// FCMOD

package btw.inventory.container;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;

public class HamperContainer extends InventoryContainer
{
	private static final int INVENTORY_ROWS = 2;
	private static final int INVENTORY_COLUMNS = 2;
	
	private static final int CONTAINER_INVENTORY_DISPLAY_X = 71;
	private static final int CONTAINER_INVENTORY_DISPLAY_Y = 17;
	
	private static final int PLAYER_INVENTORY_DISPLAY_X = 8;
	private static final int PLAYER_INVENTORY_DISPLAY_Y = 67;
	
    public HamperContainer(IInventory playerInventory, IInventory hamperInventory )
    {
    	super(playerInventory, hamperInventory, INVENTORY_ROWS, INVENTORY_COLUMNS, CONTAINER_INVENTORY_DISPLAY_X, CONTAINER_INVENTORY_DISPLAY_Y,
			  PLAYER_INVENTORY_DISPLAY_X, PLAYER_INVENTORY_DISPLAY_Y);
    	
		hamperInventory.openChest();
    }
    
    @Override
    public void onCraftGuiClosed( EntityPlayer player )
    {
        super.onCraftGuiClosed( player );
        
		containerInventory.closeChest();
    }
}