// FCMOD

package btw.block.tileentity;

import btw.block.BTWBlocks;
import btw.item.util.ItemUtils;
import net.minecraft.src.*;

public class WickerBasketTileEntity extends BasketTileEntity
	implements TileEntityDataPacketHandler
{
	private ItemStack storageStack = null;
	
    public WickerBasketTileEntity()
    {
    	super( BTWBlocks.wickerBasket);
    }
    
    @Override
    public void updateEntity()
    {
    	super.updateEntity();   

		updateVisualContentsState();
    }
    
    @Override
    public void readFromNBT( NBTTagCompound tag )
    {
        super.readFromNBT( tag );
        
        NBTTagCompound storageTag = tag.getCompoundTag( "fcStorageStack" );

        if ( storageTag != null )
        {
            storageStack = ItemStack.loadItemStackFromNBT(storageTag);
        }
    }
    
    @Override
    public void writeToNBT( NBTTagCompound tag )
    {
        super.writeToNBT( tag );
        
        if (storageStack != null)
        {
            NBTTagCompound storageTag = new NBTTagCompound();
            
            storageStack.writeToNBT(storageTag);
            
            tag.setCompoundTag( "fcStorageStack", storageTag );
        }
    }
        
    @Override
    public void ejectContents()
    {
    	if (storageStack != null )
    	{
    		ItemUtils.ejectStackWithRandomOffset(worldObj, this.xCoord, yCoord, zCoord, storageStack);

            storageStack = null;
    	}
    }    
    
    //------------- FCITileEntityDataPacketHandler ------------//
    
    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound tag = new NBTTagCompound();
        
        if (storageStack != null)
        {
            NBTTagCompound storageTag = new NBTTagCompound();
            
            storageStack.writeToNBT(storageTag);
            
            tag.setCompoundTag( "x", storageTag );
        }
        
        return new Packet132TileEntityData( xCoord, yCoord, zCoord, 1, tag );
    }

    @Override
    public void readNBTFromPacket( NBTTagCompound tag )
    {
        NBTTagCompound storageTag = tag.getCompoundTag( "x" );

        if ( storageTag != null )
        {
            storageStack = ItemStack.loadItemStackFromNBT(storageTag);
        }        
    }    
    
    @Override
    public boolean shouldStartClosingServerSide()
    {
    	return !worldObj.isRemote && worldObj.getClosestPlayer(xCoord, yCoord, zCoord, MAX_KEEP_OPEN_RANGE) == null;
    }
    
    //------------- Class Specific Methods ------------//
    
    public void setStorageStack(ItemStack stack)
    {
    	if ( stack != null )
    	{
            storageStack = stack.copy();
    	}
    	else
    	{
            storageStack = null;
    	}
    	
		worldObj.markBlockForUpdate( xCoord, yCoord, zCoord );
    }
    
    public ItemStack getStorageStack()
    {
    	return storageStack;
    }
    
    private void updateVisualContentsState()
    {
    	if ( !worldObj.isRemote )
    	{
			// validate the block's indication of contents in metadata, since it wasn't included in the initial releases
			
			boolean bHasContents = BTWBlocks.wickerBasket.getHasContents(worldObj, xCoord, yCoord, zCoord);
			
			if ( bHasContents != (storageStack != null ) )
			{
				BTWBlocks.wickerBasket.setHasContents(worldObj, xCoord, yCoord, zCoord, storageStack != null);
			}
    	}
    }
    
	//----------- Client Side Functionality -----------//
}