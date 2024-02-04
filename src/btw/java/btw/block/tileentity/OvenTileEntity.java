// FCMOD

package btw.block.tileentity;

import btw.block.blocks.FireBlock;
import btw.block.blocks.FurnaceBlock;
import btw.item.util.ItemUtils;
import btw.world.util.BlockPos;
import net.minecraft.src.*;

public class OvenTileEntity extends TileEntityFurnace
	implements TileEntityDataPacketHandler
{
    static private final float CHANCE_OF_FIRE_SPREAD = 0.01F;
    
	private boolean lightOnNextUpdate = false;
	
	private ItemStack cookStack = null;
	
	private int unlitFuelBurnTime = 0;
	private int visualFuelLevel = 0;

	private final int brickBurnTimeMultiplier = 4; // applied on top of base multiplier of standard furnace
	private final int cookTimeMultiplier = 4;
	
	// the following is not the actual maximum time, but rather the point above which additional fuel can no longer be added 
	//private final int m_iMaxFuelBurnTime = ( 1600 * 2 * 2 ); // 1600 oak log burn time, 2x base furnace multiplier, 2x brick furnace multiplier
	// the following is an actual max
	private final int maxFuelBurnTime = ((64 + 7 ) * 25 * 2 * brickBurnTimeMultiplier); // 64 + 7 buffer, 25x saw dust, 2x base furnace multiplier
	
	private final int visualFuelLevelIncrement = (200 * 2 * brickBurnTimeMultiplier);
	private final int visualSputterFuelLevel = (visualFuelLevelIncrement / 4 );

	@Override
    public void updateEntity()
    {
        boolean bWasBurning = furnaceBurnTime > 0;
        boolean bInventoryChanged = false;

        if ( furnaceBurnTime > 0 )
        {
            --furnaceBurnTime;
        }

        if ( !worldObj.isRemote )
        {        	
            if (bWasBurning || lightOnNextUpdate)
            {
            	furnaceBurnTime += unlitFuelBurnTime;
				unlitFuelBurnTime = 0;

				lightOnNextUpdate = false;
            }

            if ( isBurning() && canSmelt() )
            {
                ++furnaceCookTime;

                if (furnaceCookTime >= getCookTimeForCurrentItem() )
                {
                    furnaceCookTime = 0;
                    smeltItem();
                    bInventoryChanged = true;
                }
            }
            else
            {
                furnaceCookTime = 0;
            }
            
    		if ( isBurning() && worldObj.rand.nextFloat() <= CHANCE_OF_FIRE_SPREAD)
    		{
    			BlockPos frontPos = new BlockPos( xCoord, yCoord, zCoord );
    			int iFacing = worldObj.getBlockMetadata( xCoord, yCoord, zCoord ) & 7;

    			frontPos.addFacingAsOffset(iFacing);
    			
				FireBlock.checkForFireSpreadAndDestructionToOneBlockLocation(worldObj, frontPos.x, frontPos.y, frontPos.z);
    		}

            FurnaceBlock furnaceBlock = (FurnaceBlock) Block.blocksList[worldObj.getBlockId( xCoord, yCoord, zCoord )];
            
            if ( bWasBurning != isBurning() )
            {
                bInventoryChanged = true;
                
                furnaceBlock.updateFurnaceBlockState( furnaceBurnTime > 0, worldObj, xCoord, yCoord, zCoord, false );
            }
            
            updateCookStack();
            updateVisualFuelLevel();
        }

        if ( bInventoryChanged )
        {
            onInventoryChanged();
        }
    }

	@Override
    public String getInvName()
    {
        return "container.fcFurnaceBrick";
    }
	
	@Override
    public void readFromNBT( NBTTagCompound tag )
    {
        super.readFromNBT( tag );
        
        if ( tag.hasKey( "fcUnlitFuel" ) )
        {
			unlitFuelBurnTime = tag.getInteger("fcUnlitFuel");
        }
        
        if ( tag.hasKey( "fcVisualFuel" ) )
        {
			visualFuelLevel = tag.getByte("fcVisualFuel");
        }
    }
	
	@Override
    public void writeToNBT( NBTTagCompound tag )
    {
        super.writeToNBT( tag );
        
        tag.setInteger("fcUnlitFuel", unlitFuelBurnTime);
        tag.setByte( "fcVisualFuel", (byte) visualFuelLevel);
    }	
    
	@Override
    public int getItemBurnTime( ItemStack stack )
    {
    	return super.getItemBurnTime( stack ) * brickBurnTimeMultiplier;
    }

	@Override
    protected int getCookTimeForCurrentItem()
    {
		return super.getCookTimeForCurrentItem() * cookTimeMultiplier;
    }
	
    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound tag = new NBTTagCompound();
        
        if (cookStack != null)
        {
            NBTTagCompound cookTag = new NBTTagCompound();
            
            cookStack.writeToNBT(cookTag);
            
            tag.setCompoundTag( "x", cookTag );
        }
        
        tag.setByte( "y", (byte) visualFuelLevel);

        return new Packet132TileEntityData( xCoord, yCoord, zCoord, 1, tag );
    }
    
    //------------- FCITileEntityDataPacketHandler ------------//
    
    @Override
    public void readNBTFromPacket( NBTTagCompound tag )
    {
        NBTTagCompound cookTag = tag.getCompoundTag( "x" );

        if ( cookTag != null )
        {
			cookStack = ItemStack.loadItemStackFromNBT(cookTag);
        }

		visualFuelLevel = tag.getByte("y");
        
        // force a visual update for the block since the above variables affect it
        
        worldObj.markBlockRangeForRenderUpdate( xCoord, yCoord, zCoord, xCoord, yCoord, zCoord );        
    }    
    
    //------------- Class Specific Methods ------------//
    
	public boolean attemptToLight()
	{
		if (unlitFuelBurnTime > 0 )
		{
			// lighting has to be done on update to prevent funkiness with tile entity removal on block being set

			lightOnNextUpdate = true;
		
			return true;
		}
		
		return false;
	}
	
	public boolean hasValidFuel()
	{
		return unlitFuelBurnTime > 0;
	}
	
    private void updateCookStack()
    {
    	ItemStack newCookStack = furnaceItemStacks[0];
		
    	if ( newCookStack == null )
    	{
        	newCookStack = furnaceItemStacks[2];
        	
        	if ( newCookStack == null )
        	{
        		newCookStack = furnaceItemStacks[1];
        	}
    	}
    	
    	if ( !ItemStack.areItemStacksEqual(newCookStack, cookStack) )
    	{
            setCookStack(newCookStack);
    	}
    }
    
    public void setCookStack(ItemStack stack)
    {
    	if ( stack != null )
    	{
			cookStack = stack.copy();
    	}
    	else
    	{
			cookStack = null;
    	}
    	
        worldObj.markBlockForUpdate( xCoord, yCoord, zCoord );
    }
    
    public ItemStack getCookStack()
    {
    	return cookStack;
    }
    
    public void givePlayerCookStack(EntityPlayer player, int iFacing)
    {    	
		if ( !worldObj.isRemote )
		{
			// this is legacy support to clear all inventory items that may have been added through the GUI
			
			ejectAllNotCookStacksToFacing(player, iFacing);
		}
		
		ItemUtils.givePlayerStackOrEjectFromTowardsFacing(player, cookStack, xCoord, yCoord, zCoord, iFacing);
		
		furnaceItemStacks[0] = null;
		furnaceItemStacks[1] = null;
		furnaceItemStacks[2] = null;
		
		setCookStack(null);
    }
    
    private void ejectAllNotCookStacksToFacing(EntityPlayer player, int iFacing)
    {
    	if ( furnaceItemStacks[0] != null && !ItemStack.areItemStacksEqual(furnaceItemStacks[0], cookStack) )
    	{
    		ItemUtils.ejectStackFromBlockTowardsFacing(worldObj, xCoord, yCoord, zCoord, furnaceItemStacks[0], iFacing);
    		
    		furnaceItemStacks[0] = null;
    	}
    	
    	if ( furnaceItemStacks[1] != null && !ItemStack.areItemStacksEqual(furnaceItemStacks[1], cookStack) )
    	{
    		ItemUtils.ejectStackFromBlockTowardsFacing(worldObj, xCoord, yCoord, zCoord, furnaceItemStacks[1], iFacing);
    		
    		furnaceItemStacks[1] = null;
    	}
    	
    	if ( furnaceItemStacks[2] != null && !ItemStack.areItemStacksEqual(furnaceItemStacks[2], cookStack) )
    	{
    		ItemUtils.ejectStackFromBlockTowardsFacing(worldObj, xCoord, yCoord, zCoord, furnaceItemStacks[2], iFacing);
    		
    		furnaceItemStacks[2] = null;
    	}		
		
		onInventoryChanged();
    }
    
    public void addCookStack(ItemStack stack)
    {
    	furnaceItemStacks[0] = stack;
		
		onInventoryChanged();
    }
    
    public int attemptToAddFuel(ItemStack stack)
    {
    	int iTotalBurnTime = unlitFuelBurnTime + furnaceBurnTime;
    	int iDeltaBurnTime = maxFuelBurnTime - iTotalBurnTime;
    	int iNumItemsBurned = 0;
    	
    	if ( iDeltaBurnTime > 0 )
    	{
    		iNumItemsBurned = iDeltaBurnTime / getItemBurnTime( stack );

    		if ( iNumItemsBurned == 0 && getVisualFuelLevel() <= 2 )
    		{
    			// once the fuel level hits the bottom visual stage, you can jam anything in
    			
    			iNumItemsBurned = 1;
    		}
    		
    		if ( iNumItemsBurned > 0 )
    		{
    			if ( iNumItemsBurned > stack.stackSize )
    			{
    				iNumItemsBurned = stack.stackSize;
    			}

				unlitFuelBurnTime += getItemBurnTime(stack) * iNumItemsBurned;
        		
        		onInventoryChanged();
    		}    		
    	}
    	
    	return iNumItemsBurned;
    }
    
    private void updateVisualFuelLevel()
    {
    	int iTotalBurnTime = unlitFuelBurnTime + furnaceBurnTime;
    	int iNewFuelLevel = 0;
    	
    	if ( iTotalBurnTime > 0 )
    	{
    		if (iTotalBurnTime < visualSputterFuelLevel)
    		{
    			iNewFuelLevel = 1;
    		}
    		else
    		{
    			iNewFuelLevel = (iTotalBurnTime / visualFuelLevelIncrement) + 2;
    		}
    	}
    	
    	setVisualFuelLevel(iNewFuelLevel);
    }
    
    public int getVisualFuelLevel()
    {
    	return visualFuelLevel;
    }
    
    public void setVisualFuelLevel(int iLevel)
    {
    	if (visualFuelLevel != iLevel )
    	{
			visualFuelLevel = iLevel;
	    	
	        worldObj.markBlockForUpdate( xCoord, yCoord, zCoord );
    	}
    }    
}
