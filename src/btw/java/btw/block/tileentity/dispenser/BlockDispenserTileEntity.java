// FCMOD

package btw.block.tileentity.dispenser;

import btw.inventory.util.InventoryUtils;
import net.minecraft.src.*;

public class BlockDispenserTileEntity extends TileEntity
    implements IInventory
{
    private ItemStack dispenserContents[];
    public int nextSlotIndexToDispense;
    
    public BlockDispenserTileEntity()
    {
        dispenserContents = new ItemStack[16];
        nextSlotIndexToDispense = 0;
    }

    @Override
    public int getSizeInventory()
    {
        return 16;
    }

    @Override
    public ItemStack getStackInSlot(int i)
    {
        return dispenserContents[i];
    }

    @Override
    public ItemStack decrStackSize( int iSlot, int iAmount )
    {
    	return InventoryUtils.decreaseStackSize(this, iSlot, iAmount);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (dispenserContents[par1] != null)
        {
            ItemStack itemstack = dispenserContents[par1];
            dispenserContents[par1] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public void setInventorySlotContents( int iSlot, ItemStack itemstack)
    {
    	super.onInventoryChanged();
    	
        dispenserContents[iSlot] = itemstack;
        
        if ( itemstack != null && itemstack.stackSize > getInventoryStackLimit() )
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
        
        onInventoryChanged();
    }

    @Override
    public String getInvName()
    {
        return "BlockDispenser";
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.getTagList("Items");
        dispenserContents = new ItemStack[getSizeInventory()];
        for(int i = 0; i < nbttaglist.tagCount(); i++)
        {
            NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
            int j = nbttagcompound1.getByte("Slot") & 0xff;
            if(j >= 0 && j < dispenserContents.length)
            {
                dispenserContents[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }

        if( nbttagcompound.hasKey( "iNextSlotIndexToDispense" ) )
        {
            nextSlotIndexToDispense = nbttagcompound.getInteger("iNextSlotIndexToDispense");
        }        
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();
        for(int i = 0; i < dispenserContents.length; i++)
        {
            if(dispenserContents[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                dispenserContents[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        nbttagcompound.setTag("Items", nbttaglist);
        
        nbttagcompound.setInteger("iNextSlotIndexToDispense", nextSlotIndexToDispense);
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer)
    {
        if(worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this)
        {
            return false;
        }
        return entityplayer.getDistanceSq((double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D) <= 64D;
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
    public boolean isStackValidForSlot( int iSlot, ItemStack stack )
    {
        return true;
    }
    
    @Override
    public boolean isInvNameLocalized()
    {
    	return true;
    }
    
    //------------- Class Specific Methods ------------//
    
    public ItemStack getCurrentItemToDispense()
    {
    	if (nextSlotIndexToDispense >= dispenserContents.length ||
            dispenserContents[nextSlotIndexToDispense] == null )
    	{
    		int iTempSlot = findNextValidSlotIndex(nextSlotIndexToDispense);
    		
    		if ( iTempSlot < 0 )
    		{
    			return null;
    		}

            nextSlotIndexToDispense = iTempSlot;
    	}    	
    	
    	ItemStack stack = getStackInSlot(nextSlotIndexToDispense).copy();
    	
    	stack.stackSize = 1;
    	
    	return stack;
    }
    
    public void onDispenseCurrentSlot()
    {
    	decrStackSize(nextSlotIndexToDispense, 1);
    	
		int iTempSlot = findNextValidSlotIndex(nextSlotIndexToDispense);
		
		if ( iTempSlot < 0 )
		{
            nextSlotIndexToDispense = 0;
		}
		else
		{
            nextSlotIndexToDispense = iTempSlot;
		}
    }
    
    private int findNextValidSlotIndex(int iCurrentSlot)
    {
    	for ( int iTempSlot = iCurrentSlot + 1; iTempSlot < dispenserContents.length; iTempSlot++ )
    	{
    		if ( dispenserContents[iTempSlot] != null )
    		{
    			return iTempSlot;
    		}
    	}
    	
    	for ( int iTempSlot = 0;  iTempSlot < iCurrentSlot; iTempSlot++ )
    	{
    		if ( dispenserContents[iTempSlot] != null )
    		{
    			return iTempSlot;
    		}
    	}
    	
    	if ( dispenserContents[iCurrentSlot] != null )
    	{
    		return iCurrentSlot;
    	}
    	
    	return -1;
    }    
}