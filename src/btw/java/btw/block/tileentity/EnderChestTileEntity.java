// FCMOD

package btw.block.tileentity;

import net.minecraft.src.InventoryEnderChest;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.TileEntityEnderChest;

public class EnderChestTileEntity extends TileEntityEnderChest
{
    private InventoryEnderChest localChestInventory = new InventoryEnderChest();
    
	@Override
    public void readFromNBT( NBTTagCompound tag)
    {
        super.readFromNBT( tag );
        
	    if ( tag.hasKey( "FCEnderItems" ) )
	    {
	        NBTTagList itemList = tag.getTagList( "FCEnderItems" );
	        
	    	localChestInventory.loadInventoryFromNBT(itemList);
	    }	    
    }

	@Override
    public void writeToNBT( NBTTagCompound tag )
    {
        super.writeToNBT( tag );
        
	    if (localChestInventory != null )
	    {
	    	tag.setTag("FCEnderItems", localChestInventory.saveInventoryToNBT());
	    }	    
    }
	
    public InventoryEnderChest getLocalEnderChestInventory()
    {
    	return localChestInventory;
    }	
}
