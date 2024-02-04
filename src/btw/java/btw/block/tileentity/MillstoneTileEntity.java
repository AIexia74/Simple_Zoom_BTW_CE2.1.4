//FCMOD

package btw.block.tileentity;

import btw.BTWMod;
import btw.block.BTWBlocks;
import btw.block.blocks.MillstoneBlock;
import btw.client.fx.BTWEffectManager;
import btw.inventory.util.InventoryUtils;
import btw.crafting.manager.MillStoneCraftingManager;
import btw.item.util.ItemUtils;
import net.minecraft.src.*;

import java.util.Iterator;
import java.util.List;

public class MillstoneTileEntity extends TileEntity
	implements IInventory
{
	public ItemStack stackMilling = null;
	
	public static final String UNLOCALIZED_NAME = "container.fcMillStone";

	private static final double MAX_PLAYER_INTERACTION_DIST_SQ = 64D;

    private static final int TIME_TO_GRIND = 200;
    
    private boolean validateContentsOnUpdate;
    private boolean containsIngredientsToGrind;

    public int grindCounter;
    
	private static final int LEGACY_INVENTORY_SIZE = 3;
    private ItemStack legacyInventory[] = null;

    public MillstoneTileEntity()
    {
		grindCounter = 0;

		validateContentsOnUpdate = true;
    }

    @Override
    public void readFromNBT( NBTTagCompound tag )
    {
        super.readFromNBT( tag );
        
        if ( tag.hasKey( "Items" ) )
        {
	        NBTTagList tagList = tag.getTagList( "Items" );

			legacyInventory = new ItemStack[LEGACY_INVENTORY_SIZE];
	        
	        for ( int iTempTag = 0; iTempTag < tagList.tagCount(); iTempTag++ )
	        {
	            NBTTagCompound tempSlotTag = (NBTTagCompound)tagList.tagAt( iTempTag );
	            
	            int iTempSlot = tempSlotTag.getByte( "Slot" ) & 0xff;
	            
	            if ( iTempSlot >= 0 && iTempSlot < legacyInventory.length )
	            {
					legacyInventory[iTempSlot] = ItemStack.loadItemStackFromNBT(tempSlotTag);;
	            }
	        }
        }
        
        NBTTagCompound millingTag = tag.getCompoundTag( "stackMilling" );

        if ( millingTag != null )
        {
			stackMilling = ItemStack.loadItemStackFromNBT(millingTag);
        }
        
        if ( tag.hasKey( "grindCounter" ) )
        {
			grindCounter = tag.getInteger("grindCounter");
        }
    }

    @Override
    public void writeToNBT( NBTTagCompound tag )
    {
        super.writeToNBT(tag);
        
        if ( !isLegacyInventoryEmpty() )
        {
	        NBTTagList tagList = new NBTTagList();
	        
	        for (int iTempSlot = 0; iTempSlot < legacyInventory.length; iTempSlot++ )
	        {
	            if (legacyInventory[iTempSlot] != null )
	            {
	                NBTTagCompound tempSlotTag = new NBTTagCompound();
	                
	                tempSlotTag.setByte( "Slot", (byte)iTempSlot );
	                
	                legacyInventory[iTempSlot].writeToNBT(tempSlotTag);
	                
	                tagList.appendTag( tempSlotTag );
	            }
	        }     

	        tag.setTag( "Items", tagList );
        }
        
        if (stackMilling != null)
        {
            NBTTagCompound millingTag = new NBTTagCompound();
            
            stackMilling.writeToNBT(millingTag);
            
            tag.setCompoundTag( "stackMilling", millingTag );
        }
        
        tag.setInteger("grindCounter", grindCounter);
    }

    @Override
    public void updateEntity()
    {
    	super.updateEntity();
    	
    	if ( !worldObj.isRemote )
    	{
			int iBlockID = worldObj.getBlockId( xCoord, yCoord, zCoord );
			
			MillstoneBlock millStoneBlock = (MillstoneBlock)Block.blocksList[iBlockID];
			
			if (validateContentsOnUpdate)
			{
				validateContentsForGrinding(millStoneBlock);
			}
			
			if (containsIngredientsToGrind && millStoneBlock.getIsMechanicalOn(worldObj, xCoord, yCoord, zCoord) )
			{
				grindCounter++;
				
	    		if (grindCounter >= TIME_TO_GRIND)
	    		{
	    			grindContents(millStoneBlock);

					grindCounter = 0;
					validateContentsOnUpdate = true;
	    		}
	    		
	    		checkForNauseateNearbyPlayers(millStoneBlock);
	    	}
    	}		
    }
    
    //------------- IInventory implementation -------------//
    
    @Override
    public int getSizeInventory()
    {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot( int iSlot )
    {
    	if ( iSlot == 0 )
    	{
    		return stackMilling;
    	}
    	
        return null;
    }

    @Override
    public ItemStack decrStackSize( int iSlot, int iAmount )
    {
    	return InventoryUtils.decreaseStackSize(this, iSlot, iAmount);
    }

    @Override
    public ItemStack getStackInSlotOnClosing( int iSlot )
    {
    	if ( iSlot == 0 && stackMilling != null )
    	{
            ItemStack itemstack = stackMilling;

			stackMilling = null;
            
            return itemstack;
        }
    	
        return null;
    }
    
    @Override
    public void setInventorySlotContents( int iSlot, ItemStack stack )
    {
    	if ( iSlot == 0 )
    	{
			stackMilling = stack;
	    	
	        if( stack != null && stack.stackSize > getInventoryStackLimit() )
	        {
	            stack.stackSize = getInventoryStackLimit();
	        }
	        
	        onInventoryChanged();
    	}
    }

    @Override
    public String getInvName()
    {
        return UNLOCALIZED_NAME;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1; // only single stack items can fit in the millstone
    }

    @Override
    public void onInventoryChanged()
    {
    	super.onInventoryChanged();
    	
    	if ( worldObj != null && !worldObj.isRemote )
    	{
	    	if ( containsWholeCompanionCube() )
	    	{
	            worldObj.playSoundEffect( (float)xCoord + 0.5F, (float)yCoord + 0.5F, (float)zCoord + 0.5F, 
	        		"mob.wolf.whine", 
	        		0.5F, 2.6F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.8F);
	    	}

			validateContentsOnUpdate = true;
    	}
    }
    
    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer)
    {
        if ( worldObj.getBlockTileEntity( xCoord, yCoord, zCoord ) == this )
        {
            return (entityplayer.getDistanceSq( (double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D )
					<= MAX_PLAYER_INTERACTION_DIST_SQ);
        }
        
        return false;
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
    	return false;
    }
    
    //------------- Class Specific Methods ------------//
    
    public void ejectStackOnMilled(ItemStack stack)
    {
    	int iFacing = 2 + worldObj.rand.nextInt( 4 ); // random direction to the sides
    	
    	Vec3 ejectPos = Vec3.createVectorHelper( worldObj.rand.nextDouble() * 1.25F - 0.125F, 
    		worldObj.rand.nextFloat() * ( 1F / 16F ) + ( 7F / 16F ), 
    		-0.2F );
    	
    	ejectPos.rotateAsBlockPosAroundJToFacing(iFacing);
    	
        EntityItem entity = (EntityItem) EntityList.createEntityOfType(EntityItem.class, worldObj, xCoord + ejectPos.xCoord, 
        		yCoord + ejectPos.yCoord, zCoord + ejectPos.zCoord, stack );

    	Vec3 ejectVel = Vec3.createVectorHelper( worldObj.rand.nextGaussian() * 0.025D, 
    		worldObj.rand.nextGaussian() * 0.025D + 0.1F, 
    		-0.06D + worldObj.rand.nextGaussian() * 0.04D );
    	
    	ejectVel.rotateAsVectorAroundJToFacing(iFacing);
    	
        entity.motionX = ejectVel.xCoord;
        entity.motionY = ejectVel.yCoord;
        entity.motionZ = ejectVel.zCoord;
        
        entity.delayBeforeCanPickup = 10;
        
        worldObj.spawnEntityInWorld( entity );
    }
    
    public int getGrindProgressScaled( int iScale )
    {
        return (grindCounter * iScale ) / TIME_TO_GRIND;
    }    
    
    public boolean isGrinding()
    {
    	return grindCounter > 0;
    }
    
    public boolean containsWholeCompanionCube()
    {    	
		return stackMilling != null && stackMilling.itemID == BTWBlocks.companionCube.blockID &&
			   stackMilling.getItemDamage() == 0;
    }
    
    private boolean grindContents(MillstoneBlock millStoneBlock)
    {
		if (stackMilling != null && MillStoneCraftingManager.getInstance().hasRecipeForSingleIngredient(stackMilling) )
		{
			List<ItemStack> outputList = MillStoneCraftingManager.getInstance().getCraftingResult(stackMilling);

			if ( outputList != null )
			{
				if (stackMilling.itemID == BTWBlocks.companionCube.blockID && stackMilling.getItemDamage() == 0 )
				{
	    	        worldObj.playAuxSFX( BTWEffectManager.COMPANION_CUBE_DEATH_EFFECT_ID, xCoord, yCoord, zCoord, 0 );
				}
	            
	            // eject the product stacks
	            
	            for ( int listIndex = 0; listIndex < outputList.size(); listIndex++ )
	            {
		    		ItemStack groundStack = ((ItemStack)outputList.get( listIndex )).copy();
		    		
		    		if ( groundStack != null )
		    		{
		    			ejectStackOnMilled(groundStack);
		    		}
	            }

				stackMilling = null;

	            return true;
			}
		}

    	return false;
    }
    
    private void validateContentsForGrinding(MillstoneBlock millStoneBlock)
    {
    	int iOldGrindingType = millStoneBlock.getCurrentGrindingType(worldObj, xCoord, yCoord, zCoord);
    	int iNewGrindingType = MillstoneBlock.CONTENTS_NOTHING;
    	
    	migrateLegacyInventory();
    	
		if (stackMilling != null )
		{
			if ( MillStoneCraftingManager.getInstance().hasRecipeForSingleIngredient(stackMilling) )
			{
				containsIngredientsToGrind = true;
				
				int iItemIndex = stackMilling.getItem().itemID;
				
				if ( iItemIndex == BTWBlocks.companionCube.blockID && stackMilling.getItemDamage() == 0 )
				{
					iNewGrindingType = MillstoneBlock.CONTENTS_COMPANION_CUBE;
				}
				else if ( iItemIndex == Block.netherrack.blockID )
				{
					iNewGrindingType = MillstoneBlock.CONTENTS_NETHERRACK;
				}
				else
				{
					iNewGrindingType = MillstoneBlock.CONTENTS_NORMAL_GRINDING;
				}
			}
			else
			{
				iNewGrindingType = MillstoneBlock.CONTENTS_JAMMED;

				grindCounter = 0;

				containsIngredientsToGrind = false;
			}
		}
		else
		{
			grindCounter = 0;

			containsIngredientsToGrind = false;
		}

		validateContentsOnUpdate = false;
		
    	if ( iOldGrindingType != iNewGrindingType )
    	{
    		millStoneBlock.setCurrentGrindingType(worldObj, xCoord, yCoord, zCoord, iNewGrindingType);
    	}
    }
    
    private void checkForNauseateNearbyPlayers(MillstoneBlock block) {
    	int iGrindType = block.getCurrentGrindingType(worldObj, xCoord, yCoord, zCoord);
		
    	if ( iGrindType == MillstoneBlock.CONTENTS_NETHERRACK && worldObj.getTotalWorldTime() % 40L == 0L) {
    		applyPotionEffectToPlayersInRange(Potion.confusion.getId(), 120, 0, 10D);
    	}
    }
    
    private void applyPotionEffectToPlayersInRange(int iEffectID, int iDuration, int iEffectLevel, double dRange)
    {
        Iterator playerIterator = worldObj.playerEntities.iterator();
        
        while ( playerIterator.hasNext() )
        {
        	EntityPlayer player = (EntityPlayer)playerIterator.next();
        	
        	if ( !player.isDead && !player.capabilities.isCreativeMode && 
        		Math.abs( xCoord - player.posX ) <= dRange &&
        		Math.abs( yCoord - player.posY ) <= dRange &&
        		Math.abs( zCoord - player.posZ ) <= dRange )
        	{        	
                player.addPotionEffect( new PotionEffect( iEffectID, iDuration, iEffectLevel, true ) );                            
        	}
        }
    }
    
    private void migrateLegacyInventory()
    {
    	if (stackMilling == null && legacyInventory != null )
    	{
	        for (int iTempSlot = 0; iTempSlot < legacyInventory.length; iTempSlot++ )
	        {
	            if (legacyInventory[iTempSlot] != null )
	            {
            		ItemStack legacyStack = legacyInventory[iTempSlot];

					stackMilling = legacyStack.copy();
					stackMilling.stackSize = 1;
            		
            		legacyInventory[iTempSlot].stackSize--;
            		
            		if ( legacyStack.stackSize <= 0 )
            		{
						legacyInventory[iTempSlot] = null;
            			
            			if ( isLegacyInventoryEmpty() )
            			{
							legacyInventory = null;
            				
            				break;
            			}
            		}	            		
	            }
	        }
    	}
    }
    
    private boolean isLegacyInventoryEmpty()
    {
    	if (legacyInventory != null )
    	{
	        for (int iTempSlot = 0; iTempSlot < legacyInventory.length; iTempSlot++ )
	        {
	        	if (legacyInventory[iTempSlot] != null && legacyInventory[iTempSlot].stackSize > 0 )
	        	{
	        		return false;
	        	}
	        }
    	}
    	
    	return true;
    }
    
    public void ejectContents(int iFacing)
    {
    	if ( iFacing < 2 )
    	{
    		// always eject towards the sides
    		
    		iFacing = worldObj.rand.nextInt( 4 ) + 2;
    	}
        
    	if (legacyInventory != null )
    	{
	        for (int iTempSlot = 0; iTempSlot < legacyInventory.length; iTempSlot++ )
	        {
	        	if (legacyInventory[iTempSlot] != null && legacyInventory[iTempSlot].stackSize > 0 )
	        	{
	        		ItemUtils.ejectStackFromBlockTowardsFacing(worldObj, xCoord, yCoord, zCoord, legacyInventory[iTempSlot], iFacing);

					legacyInventory[iTempSlot] = null;
	        	}
	        }

			legacyInventory = null;
    	}        
    	
    	if (stackMilling != null )
    	{
    		ItemUtils.ejectStackFromBlockTowardsFacing(worldObj, xCoord, yCoord, zCoord, stackMilling, iFacing);

			stackMilling = null;
    		
	        onInventoryChanged();
    	}
    }
    
    public void attemptToAddSingleItemFromStack(ItemStack stack)
    {
    	if (stackMilling == null )
    	{
			stackMilling = stack.copy();

			stackMilling.stackSize = 1;
    		
    		stack.stackSize--;
    		
	        onInventoryChanged();
    	}
    }
}