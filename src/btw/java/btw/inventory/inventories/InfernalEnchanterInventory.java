// FCMOD

package btw.inventory.inventories;

import btw.inventory.container.InfernalEnchanterContainer;
import net.minecraft.src.InventoryBasic;

public class InfernalEnchanterInventory extends InventoryBasic
{
    final InfernalEnchanterContainer container;

    public InfernalEnchanterInventory(InfernalEnchanterContainer container, String name, int iNumSlots)
    {
        super( name, true, iNumSlots );

        this.container = container;
    }

    /*
    public int getInventoryStackLimit()
    {
        return 1;
    }
    */

    public void onInventoryChanged()
    {
        super.onInventoryChanged();
        
        container.onCraftMatrixChanged(this);
    }
}
