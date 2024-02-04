//FCMOD

package btw.block.tileentity;

import btw.inventory.util.InventoryUtils;
import net.minecraft.src.*;

public class VaseTileEntity extends TileEntity
    implements IInventory
{	
	private static final int VASE_INVENTORY_SIZE = 1;
	private static final int VASE_STACK_SIZE_LIMIT = 1;
	private static final double VASE_MAX_PLAYER_INTERACTION_DIST = 64D;

    private ItemStack vaseContents[];
    
    public VaseTileEntity()
    {
        vaseContents = new ItemStack[VASE_INVENTORY_SIZE];
    }
    
    @Override
    public int getSizeInventory()
    {
        return VASE_INVENTORY_SIZE;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return VASE_STACK_SIZE_LIMIT;
    }
    
    @Override
    public ItemStack getStackInSlot( int iSlot )
    {
        return vaseContents[iSlot];
    }

    @Override
    public ItemStack decrStackSize( int iSlot, int iAmount )
    {
    	return InventoryUtils.decreaseStackSize(this, iSlot, iAmount);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (vaseContents[par1] != null)
        {
            ItemStack itemstack = vaseContents[par1];
            vaseContents[par1] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public void setInventorySlotContents( int iSlot, ItemStack itemstack )
    {
        vaseContents[iSlot] = itemstack;
    	
        if( itemstack != null && itemstack.stackSize > getInventoryStackLimit() )
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
        
        onInventoryChanged();
    }

    @Override
    public String getInvName()
    {
        return "Vase";
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer)
    {
        if( worldObj.getBlockTileEntity( xCoord, yCoord, zCoord ) != this )
        {
            return false;
        }

        return (entityplayer.getDistanceSq( (
    		double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D )
                <= VASE_MAX_PLAYER_INTERACTION_DIST);
    }
    
    @Override
    public void openChest()
    {
    }

    @Override
    public void closeChest()
    {
    }

    @Override
    public void readFromNBT( NBTTagCompound nbttagcompound )
    {
        super.readFromNBT(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.getTagList("Items");

        vaseContents = new ItemStack[getSizeInventory()];
        
        for ( int i = 0; i < nbttaglist.tagCount(); i++ )
        {
            NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt( i );
            
            int j = nbttagcompound1.getByte( "Slot" ) & 0xff;
            
            if ( j >= 0 && j < vaseContents.length )
            {
                vaseContents[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
    }
    
    @Override
    public void writeToNBT( NBTTagCompound nbttagcompound )
    {
        super.writeToNBT(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();
        
        for (int i = 0; i < vaseContents.length; i++ )
        {
            if (vaseContents[i] != null )
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte( "Slot", (byte)i );
                
                vaseContents[i].writeToNBT(nbttagcompound1);
                
                nbttaglist.appendTag( nbttagcompound1 );
            }
        }
        
        nbttagcompound.setTag( "Items", nbttaglist );
    }
    
    @Override
    public boolean isStackValidForSlot( int iSlot, ItemStack stack )
    {
        return true;
    }
    
    @Override
    public boolean isInvNameLocalized()
    {
    	return true;
    }    
}
