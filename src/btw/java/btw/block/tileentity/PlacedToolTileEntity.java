// FCMOD

package btw.block.tileentity;

import btw.item.util.ItemUtils;
import net.minecraft.src.*;

public class PlacedToolTileEntity extends TileEntity
	implements TileEntityDataPacketHandler
{
	private ItemStack toolStack = null;
	
    public PlacedToolTileEntity()
    {
    	super();
    }
    
    @Override
    public void readFromNBT( NBTTagCompound tag )
    {
        super.readFromNBT( tag );
        
        NBTTagCompound storageTag = tag.getCompoundTag( "fcToolStack" );

        if ( storageTag != null )
        {
            toolStack = ItemStack.loadItemStackFromNBT(storageTag);
        }
    }
    
    @Override
    public void writeToNBT( NBTTagCompound tag )
    {
        super.writeToNBT( tag );
        
        if (toolStack != null)
        {
            NBTTagCompound storageTag = new NBTTagCompound();
            
            toolStack.writeToNBT(storageTag);
            
            tag.setCompoundTag( "fcToolStack", storageTag );
        }
    }
        
    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound tag = new NBTTagCompound();
        
        if (toolStack != null )
        {
            NBTTagCompound storageTag = new NBTTagCompound();
            
            toolStack.writeToNBT(storageTag);
            
            tag.setCompoundTag( "x", storageTag );
        }
        
        return new Packet132TileEntityData( xCoord, yCoord, zCoord, 1, tag );
    }
    
    //------------- FCITileEntityDataPacketHandler ------------//
    
    @Override
    public void readNBTFromPacket( NBTTagCompound tag )
    {
        NBTTagCompound storageTag = tag.getCompoundTag( "x" );

        if ( storageTag != null )
        {
            toolStack = ItemStack.loadItemStackFromNBT(storageTag);
        }        
        
        // force a visual update for the block since the above variables affect it
        
        worldObj.markBlockRangeForRenderUpdate( xCoord, yCoord, zCoord, xCoord, yCoord, zCoord );        
    }    
    
    //------------- Class Specific Methods ------------//
    
    public void setToolStack(ItemStack stack)
    {
    	if ( stack != null )
    	{
            toolStack = stack.copy();
    	}
    	else
    	{
            toolStack = null;
    	}    	
    }
    
    public ItemStack getToolStack()
    {
    	return toolStack;
    }
    
    public void ejectContents()
    {
    	if (toolStack != null )
    	{
    		ItemUtils.ejectStackWithRandomOffset(worldObj, this.xCoord, yCoord, zCoord, toolStack);

            toolStack = null;
    	}
    }    
    
	//----------- Client Side Functionality -----------//
}