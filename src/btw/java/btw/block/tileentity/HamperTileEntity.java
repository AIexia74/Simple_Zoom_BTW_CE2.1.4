// FCMOD

package btw.block.tileentity;

import btw.block.BTWBlocks;
import btw.inventory.container.HamperContainer;
import btw.inventory.util.InventoryUtils;
import net.minecraft.src.*;

import java.util.Iterator;

public class HamperTileEntity extends BasketTileEntity
    implements IInventory
{
	public static final int INVENTORY_SIZE = 4;
	public static final String FC_HAMPER = "container.fcHamper";
	
	private static final int STACK_SIZE_LIMIT = 64; // standard
	private static final double MAX_PLAYER_INTERACTION_DIST_SQ = 64D; // standard
	
	private static final int FULL_UPDATE_INTERVAL = 200;

    private ItemStack contents[];
    
    private int numUsingPlayers;
    private int fullUpdateCounter = 0;
    
    public HamperTileEntity()
    {
    	super( BTWBlocks.hamper);

        contents = new ItemStack[INVENTORY_SIZE];
    }

    @Override
    public void readFromNBT( NBTTagCompound tag )
    {
        super.readFromNBT( tag );
        
        NBTTagList tagList = tag.getTagList( "Items" );

        contents = new ItemStack[getSizeInventory()];
        
        for ( int iTempIndex = 0; iTempIndex < tagList.tagCount(); iTempIndex++ )
        {
            NBTTagCompound tempTag = (NBTTagCompound)tagList.tagAt( iTempIndex );
            
            int tempSlot = tempTag.getByte( "Slot" ) & 0xff;
            
            if ( tempSlot >= 0 && tempSlot < contents.length )
            {
                contents[tempSlot] = ItemStack.loadItemStackFromNBT(tempTag);
            }
        }
    }
    
    @Override
    public void writeToNBT( NBTTagCompound tag )
    {
        super.writeToNBT(tag);
        
        NBTTagList tagList = new NBTTagList();
        
        for (int iTempIndex = 0; iTempIndex < contents.length; iTempIndex++ )
        {
            if (contents[iTempIndex] != null )
            {
                NBTTagCompound tempTag = new NBTTagCompound();
                
                tempTag.setByte( "Slot", (byte)iTempIndex );
                
                contents[iTempIndex].writeToNBT(tempTag);
                
                tagList.appendTag( tempTag );
            }
        }     

        tag.setTag( "Items", tagList );
    }

    @Override
    public void updateEntity()
    {
    	super.updateEntity();
    	
    	if ( !worldObj.isRemote )
    	{
    		if (numUsingPlayers > 0 && !blockBasket.getIsOpen(worldObj, xCoord, yCoord, zCoord) )
    		{
				worldObj.playSoundEffect( xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D,
					"step.gravel", 0.25F + ( worldObj.rand.nextFloat() * 0.1F ), 
		    		0.5F + ( worldObj.rand.nextFloat() * 0.1F ) );

				blockBasket.setIsOpen(worldObj, xCoord, yCoord, zCoord, true);
				
				if (closing)
				{
                    closing = false;
					
			        worldObj.addBlockEvent( xCoord, yCoord, zCoord, getBlockType().blockID, 1, 0 );
				}
    		}
    		
        	fullUpdateCounter++;
        	
    		if ((fullUpdateCounter + xCoord + yCoord + zCoord ) % FULL_UPDATE_INTERVAL == 0 )
    		{
        		if (numUsingPlayers != 0 )
        		{
        			int iOldNumUsing = numUsingPlayers;
                    numUsingPlayers = 0;
        			
                    Iterator<EntityPlayer> playerIterator = ( worldObj.getEntitiesWithinAABB( EntityPlayer.class, AxisAlignedBB.getAABBPool().getAABB(
                            xCoord - MAX_KEEP_OPEN_RANGE, yCoord - MAX_KEEP_OPEN_RANGE, zCoord - MAX_KEEP_OPEN_RANGE,
                            xCoord + 1 + MAX_KEEP_OPEN_RANGE, yCoord + 1 + MAX_KEEP_OPEN_RANGE, zCoord + 1 + MAX_KEEP_OPEN_RANGE)) ).
                		iterator();

                    while ( playerIterator.hasNext() )
                    {
                        EntityPlayer tempPlayer = playerIterator.next();

                        if ( tempPlayer.openContainer instanceof HamperContainer)
                        {
                            if (((HamperContainer)tempPlayer.openContainer).containerInventory == this )
                            {
                                numUsingPlayers++;
                            }
                        }
                    }
        		}
    		}    			
    	}
	}
    
    @Override
    public void ejectContents()
    {
        InventoryUtils.ejectInventoryContents(worldObj, xCoord, yCoord, zCoord, this);
    }    
    
    //------------- IInventory implementation -------------//
    
    @Override
    public int getSizeInventory()
    {
        return INVENTORY_SIZE;
    }

    @Override
    public ItemStack getStackInSlot( int iSlot )
    {
        return contents[iSlot];
    }

    @Override
    public ItemStack decrStackSize( int iSlot, int iAmount )
    {
    	return InventoryUtils.decreaseStackSize(this, iSlot, iAmount);
    }

    @Override
    public ItemStack getStackInSlotOnClosing( int iSlot )
    {
    	// I do not believe this is necessary other than to implement interface
    	
        return null;
    }
    
    @Override
    public void setInventorySlotContents( int iSlot, ItemStack itemstack )
    {
        contents[iSlot] = itemstack;
    	
        if ( itemstack != null && itemstack.stackSize > getInventoryStackLimit() )
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
        
        onInventoryChanged();
    }

    @Override
    public String getInvName()
    {
        return FC_HAMPER;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return STACK_SIZE_LIMIT;
    }

    @Override
    public boolean isUseableByPlayer( EntityPlayer entityplayer )
    {
        return worldObj.getBlockTileEntity( xCoord, yCoord, zCoord ) == this &&
               entityplayer.getDistanceSq( (double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D )
               <= MAX_PLAYER_INTERACTION_DIST_SQ;
    }
    
    @Override
    public void openChest()
    {
        if (numUsingPlayers < 0 )
        {
            numUsingPlayers = 0;
        }

        numUsingPlayers++;
    }

    @Override
    public void closeChest()
    {
        numUsingPlayers--;
    }

    @Override
    public boolean isStackValidForSlot( int iSlot, ItemStack stack )
    {
        return true;
    }
    
    @Override
    public boolean isInvNameLocalized()
    {
    	return false;
    }
    
    @Override
    public boolean shouldStartClosingServerSide()
    {
    	return !worldObj.isRemote && numUsingPlayers <= 0;
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//
}