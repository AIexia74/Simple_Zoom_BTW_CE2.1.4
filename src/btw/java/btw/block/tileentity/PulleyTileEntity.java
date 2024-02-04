// FCMOD

package btw.block.tileentity;

import btw.block.BTWBlocks;
import btw.block.blocks.AnchorBlock;
import btw.block.blocks.PulleyBlock;
import btw.inventory.util.InventoryUtils;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;

public class PulleyTileEntity extends TileEntity
    implements IInventory
{
	private static final int PULLEY_INVENTORY_SIZE = 4;
	private static final int PULLEY_STACK_SIZE_LIMIT = 64;
	private static final double PULLEY_MAX_PLAYER_INTERACTION_DIST = 64D;
	private static final int TICKS_TO_UPDATE_ROPE_STATE = 20;
	private boolean hasAssociatedAnchorEntity;

    private ItemStack pulleyContents[];
    public int updateRopeStateCounter;
    
    public int mechanicalPowerIndicator; // used to communicate power status from server to client.  0 for off, 1 for on
    
    public PulleyTileEntity()
    {
    	pulleyContents = new ItemStack[PULLEY_INVENTORY_SIZE];

		hasAssociatedAnchorEntity = false;

		updateRopeStateCounter = TICKS_TO_UPDATE_ROPE_STATE;

		mechanicalPowerIndicator = 0;
    }
    
    @Override
    public String getInvName()
    {
        return "Pulley";
    }

    @Override
    public int getSizeInventory()
    {
        return PULLEY_INVENTORY_SIZE;
    }
    
    @Override
    public int getInventoryStackLimit()
    {
        return PULLEY_STACK_SIZE_LIMIT;
    }   

    @Override
    public ItemStack getStackInSlot( int iSlot )
    {
        return pulleyContents[iSlot];
    }
    
    @Override
    public void setInventorySlotContents( int iSlot, ItemStack itemstack )
    {
    	pulleyContents[iSlot] = itemstack;
    	
        if( itemstack != null && itemstack.stackSize > getInventoryStackLimit() )
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
        
        onInventoryChanged();
    }

    @Override
    public ItemStack decrStackSize( int iSlot, int iAmount )
    {
    	return InventoryUtils.decreaseStackSize(this, iSlot, iAmount);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if ( pulleyContents[par1] != null )
        {
            ItemStack itemstack = pulleyContents[par1];
            pulleyContents[par1] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
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
        
        pulleyContents = new ItemStack[getSizeInventory()];
        
        for ( int i = 0; i < nbttaglist.tagCount(); i++ )
        {
            NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt( i );
            
            int j = nbttagcompound1.getByte( "Slot" ) & 0xff;
            
            if ( j >= 0 && j < pulleyContents.length )
            {
            	pulleyContents[j] = ItemStack.loadItemStackFromNBT( nbttagcompound1 );
            }
        }
        
        if ( nbttagcompound.hasKey( "iUpdateRopeStateCounter" ) )
        {
			updateRopeStateCounter = nbttagcompound.getInteger("iUpdateRopeStateCounter");
        }
        
        if ( nbttagcompound.hasKey( "m_bHasAssociatedAnchorEntity" ) )
        {
			hasAssociatedAnchorEntity = nbttagcompound.getBoolean("m_bHasAssociatedAnchorEntity");
        }
    }

    @Override
    public void writeToNBT( NBTTagCompound nbttagcompound )
    {
        super.writeToNBT(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();
        
        for ( int i = 0; i < pulleyContents.length; i++ )
        {
            if ( pulleyContents[i] != null )
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte( "Slot", (byte)i );
                
                pulleyContents[i].writeToNBT( nbttagcompound1 );
                
                nbttaglist.appendTag( nbttagcompound1 );
            }
        }     

        nbttagcompound.setTag( "Items", nbttaglist );
        
    	nbttagcompound.setInteger("iUpdateRopeStateCounter", updateRopeStateCounter);
    	
    	nbttagcompound.setBoolean("m_bHasAssociatedAnchorEntity", hasAssociatedAnchorEntity);
    }
    
    @Override
    public boolean isUseableByPlayer( EntityPlayer entityplayer )    
    {
        if( worldObj.getBlockTileEntity( xCoord, yCoord, zCoord ) != this )
        {
            return false;
        }
        
        return (entityplayer.getDistanceSq( (double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D )
				<= PULLEY_MAX_PLAYER_INTERACTION_DIST);
    }   
    
    @Override
    public void updateEntity()
    {
    	if ( worldObj.isRemote )
    	{
    		return;
    	}
    	
    	if ( isMechanicallyPowered() )
    	{
			mechanicalPowerIndicator = 1;
    	}
    	else
    	{
			mechanicalPowerIndicator = 0;
    	}
    	
    	updateRopeStateCounter--;
    	
    	if (updateRopeStateCounter <= 0 )
    	{
    		// if the pulley has an associated anchor, that's what controls the dispensing and retracting of rope
    		
    		if ( !hasAssociatedAnchorEntity)
    		{
	    		boolean bIsRedstoneOn = ( (PulleyBlock) BTWBlocks.pulley).isRedstoneOn(worldObj, xCoord, yCoord, zCoord);
	
	    		// redstone prevents the pulley from doing anything
	    		
	    		if ( !bIsRedstoneOn )
	    		{
	        		boolean bIsOn = ( (PulleyBlock) BTWBlocks.pulley).isBlockOn(worldObj, xCoord, yCoord, zCoord);
	        		
	        		if ( bIsOn )
	        		{
	        			attemptToRetractRope();
	        		}
	        		else
	        		{
	        			attemptToDispenseRope();
	        		}
	    		}
    		}

			updateRopeStateCounter = TICKS_TO_UPDATE_ROPE_STATE;
    	}
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
    
    //************* Class Specific Methods ************//
    
    private boolean isMechanicallyPowered()
    {
    	return ( (PulleyBlock) BTWBlocks.pulley).isBlockOn(worldObj, xCoord, yCoord, zCoord);
    }
    
    private boolean isRedstonePowered()
    {
    	return ( (PulleyBlock) BTWBlocks.pulley).isRedstoneOn(worldObj, xCoord, yCoord, zCoord);
    }
    
    public boolean isRaising()
    {
    	if ( !isRedstonePowered() )
    	{
    		if ( isMechanicallyPowered() )
    		{
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    public boolean isLowering()
    {
    	if ( !isRedstonePowered() )
    	{
    		if ( !isMechanicallyPowered() )
    		{
    			// we can only lower if we have rope in our inv
    			
				if ( InventoryUtils.getFirstOccupiedStackOfItem(this,
																BTWItems.rope.itemID) >= 0 )
				{
	    			return true;
				}
    		}
    	}
    	
    	return false;
    }
    
    public void notifyPulleyEntityOfBlockStateChange()
    {
		updateRopeStateCounter = TICKS_TO_UPDATE_ROPE_STATE;
    	
    	notifyAttachedAnchorOfEntityStateChange();
    }

    private void notifyAttachedAnchorOfEntityStateChange()
    {
    	// FCTODO: Notify attached anchor entities.
    	
    	// scan downward towards bottom of rope
    	
    	for ( int tempj = yCoord - 1; tempj >= 0; tempj-- )
    	{
    		int iTempBlockID = worldObj.getBlockId( xCoord, tempj, zCoord );
    		
    		if ( iTempBlockID == BTWBlocks.anchor.blockID )
    		{
    			if (( (AnchorBlock)( BTWBlocks.anchor) ).getFacing(worldObj, xCoord, tempj, zCoord) == 1 )
    			{    					
	    			if ( ( (AnchorBlock)( BTWBlocks.anchor) ).notifyAnchorBlockOfAttachedPulleyStateChange(this, worldObj, xCoord, tempj, zCoord) )
	    			{
						hasAssociatedAnchorEntity = true;
	    			}
    			}
    			else
    			{
    				break;
    			}
    		}
    		else if ( iTempBlockID != BTWBlocks.ropeBlock.blockID )
    		{
    			break;
    		}
    	}
    }
    
	public boolean attemptToRetractRope()
	{
    	// scan downward towards bottom of rope
    	
    	for ( int tempj = yCoord - 1; tempj >= 0; tempj-- )
    	{
    		int iTempBlockID = worldObj.getBlockId( xCoord, tempj, zCoord );
    		
    		if ( iTempBlockID == BTWBlocks.ropeBlock.blockID )
    		{
        		if ( worldObj.getBlockId( xCoord, tempj - 1, zCoord ) !=
						BTWBlocks.ropeBlock.blockID )
        		{
        			// we've found the bottom of the rope
        			
                    addRopeToInventory();
                    
                    // destroy the block
                    
                    Block targetBlock = BTWBlocks.ropeBlock;
                    
                    // Intentionally uses step instead of place sound to be less intrusive in automated contraptions
    		        worldObj.playSoundEffect( 
    		        		(float)xCoord + 0.5F, (float)tempj + 0.5F, (float)zCoord + 0.5F, 
    		        		targetBlock.stepSound.getStepSound(), 
    		        		targetBlock.stepSound.getStepVolume() / 4.0F, 
    		        		targetBlock.stepSound.getStepPitch() * 0.8F);
    		        
                    worldObj.setBlockWithNotify( xCoord, tempj, zCoord, 0 );
                    
                    return true;
        		}
    		}
    		else
    		{
    			return false;
    		}
    	}

    	return false;
	}
	
	public boolean attemptToDispenseRope()
	{		
		int iRopeSlot = InventoryUtils.getFirstOccupiedStackOfItem(this,
																   BTWItems.rope.itemID);

		updateRopeStateCounter = TICKS_TO_UPDATE_ROPE_STATE;
		
		if ( iRopeSlot >= 0 )
		{
	    	// scan downward towards bottom of rope
	    	
	    	for ( int tempj = yCoord - 1; tempj >= 0; tempj-- )
	    	{
        		int iTempBlockID = worldObj.getBlockId( xCoord, tempj, zCoord );
        		
        		if ( WorldUtils.isReplaceableBlock(worldObj, xCoord, tempj, zCoord) )
        		{
        			int iMetadata = BTWBlocks.ropeBlock.onBlockPlaced( worldObj, xCoord, tempj, zCoord, 0, 0F, 0F, 0F, 0 );
        			
                    if( worldObj.setBlockAndMetadataWithNotify( xCoord, tempj, zCoord, BTWBlocks.ropeBlock.blockID, iMetadata ) )
                    {	
	                    Block targetBlock = BTWBlocks.ropeBlock;
	                    
	                    // Intentionally uses step instead of place sound to be less intrusive in automated contraptions
	    		        worldObj.playSoundEffect( 
	    		        		(float)xCoord + 0.5F, (float)tempj + 0.5F, (float)zCoord + 0.5F, 
	    		        		targetBlock.stepSound.getStepSound(), 
	    		        		targetBlock.stepSound.getStepVolume() / 4.0F, 
	    		        		targetBlock.stepSound.getStepPitch() * 0.8F);
	    		        
	        			removeRopeFromInventory();

	        			// check for an upwards facing anchor below that we have just attached to
	        			
	        			int iBlockBelowTargetID = worldObj.getBlockId( xCoord, tempj - 1, zCoord );
	        			
	        			if ( iBlockBelowTargetID == BTWBlocks.anchor.blockID )
	        			{
	            			if (( (AnchorBlock)( BTWBlocks.anchor) ).getFacing(worldObj, xCoord, tempj - 1, zCoord) == 1 )
	                			{    					
	            	    			( (AnchorBlock)( BTWBlocks.anchor) ).notifyAnchorBlockOfAttachedPulleyStateChange(this, worldObj,
                                                                                                                      xCoord, tempj - 1, zCoord);
	                			}
	        			}
	                    
	                    return true;
                    }
                    
                    return false;
        		}
        		else if ( iTempBlockID != BTWBlocks.ropeBlock.blockID )
        		{
        			return false;
        		}
	    	}
		}

		return false;
	}
	
	public void addRopeToInventory()
	{
		ItemStack ropeStack = new ItemStack( BTWItems.rope);

		updateRopeStateCounter = TICKS_TO_UPDATE_ROPE_STATE;
		
		if ( InventoryUtils.addItemStackToInventory(this, ropeStack) )
		{
			worldObj.playSoundEffect( (double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D, 
				"random.pop", 0.05F, 
        		((worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.7F + 1.0F) );
		}
		else
		{
			ItemUtils.ejectStackWithRandomOffset(worldObj, xCoord, yCoord, zCoord, ropeStack);
		}		
	}
	
	public int getContainedRopeCount()
	{		
		return InventoryUtils.countItemsInInventory(this, BTWItems.rope.itemID, InventoryUtils.IGNORE_METADATA);
	}
	
	public void removeRopeFromInventory()
	{
		int iRopeSlot = InventoryUtils.getFirstOccupiedStackOfItem(this,
																   BTWItems.rope.itemID);
		
		if ( iRopeSlot >= 0 )
		{
			InventoryUtils.decreaseStackSize(this, iRopeSlot, 1);
		}
	}
	
	public void notifyOfLossOfAnchorEntity()
	{
		hasAssociatedAnchorEntity = false;
	}
}