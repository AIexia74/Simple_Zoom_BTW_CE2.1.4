// FCMOD

package btw.block.tileentity;

import btw.block.BTWBlocks;
import btw.block.blocks.CampfireBlock;
import btw.block.blocks.FireBlock;
import btw.client.fx.BTWEffectManager;
import btw.crafting.manager.CampfireCraftingManager;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import btw.util.MiscUtils;
import net.minecraft.src.*;

public class CampfireTileEntity extends TileEntity
	implements TileEntityDataPacketHandler
{
	private static final int CAMPFIRE_BURN_TIME_MULTIPLIER = 8;
	
    private static final int TIME_TO_COOK = (TileEntityFurnace.DEFAULT_COOK_TIME * CAMPFIRE_BURN_TIME_MULTIPLIER *
											 3 / 2 ); // this line represents efficiency relative to furnace cooking
    
	private static final int MAX_BURN_TIME = (5 * MiscUtils.TICKS_PER_MINUTE);
	
    private static final int INITIAL_BURN_TIME = (50 * 4 * CAMPFIRE_BURN_TIME_MULTIPLIER *
												  TileEntityFurnace.BASE_BURN_TIME_MULTIPLIER); // 50 is the furnace burn time of a shaft
    
    private static final int WARMUP_TIME = (10 * MiscUtils.TICKS_PER_SECOND);
    private static final int REVERT_TO_SMALL_TIME = (20 * MiscUtils.TICKS_PER_SECOND);
    private static final int BLAZE_TIME = (INITIAL_BURN_TIME * 3 / 2 );
    
    private static final int SMOULDER_TIME = (5 * MiscUtils.TICKS_PER_MINUTE); // used to be 2 minutes
    
    private static final int TIME_TO_BURN_FOOD = (TIME_TO_COOK / 2 );

    private static final float CHANCE_OF_FIRE_SPREAD = 0.05F;
    private static final float CHANCE_OF_GOING_OUT_FROM_RAIN = 0.01F;
    
	private ItemStack spitStack = null;
	private ItemStack cookStack = null;
	
	private int burnTimeCountdown = 0;
	private int burnTimeSinceLit = 0;
	private int cookCounter = 0;
	private int smoulderCounter = 0;
	private int cookBurningCounter = 0;
	
    public CampfireTileEntity()
    {
    	super();
    }
    
    @Override
    public void readFromNBT( NBTTagCompound tag )
    {
        super.readFromNBT( tag );
        
        NBTTagCompound spitTag = tag.getCompoundTag( "fcSpitStack" );

        if ( spitTag != null )
        {
			spitStack = ItemStack.loadItemStackFromNBT(spitTag);
        }
        
        NBTTagCompound cookTag = tag.getCompoundTag( "fcCookStack" );

        if ( cookTag != null )
        {
			cookStack = ItemStack.loadItemStackFromNBT(cookTag);
        }
        
        if ( tag.hasKey( "fcBurnCounter" ) )
        {
			burnTimeCountdown = tag.getInteger("fcBurnCounter");
        }
        
        if ( tag.hasKey( "fcBurnTime" ) )
        {
			burnTimeSinceLit = tag.getInteger("fcBurnTime");
        }
        
        if ( tag.hasKey( "fcCookCounter" ) )
        {
			cookCounter = tag.getInteger("fcCookCounter");
        }
        
        if ( tag.hasKey( "fcSmoulderCounter" ) )
        {
			smoulderCounter = tag.getInteger("fcSmoulderCounter");
        }
        
        if ( tag.hasKey( "fcCookBurning" ) )
        {
			cookBurningCounter = tag.getInteger("fcCookBurning");
        }        	
    }
    
    @Override
    public void writeToNBT( NBTTagCompound tag)
    {
        super.writeToNBT( tag );
        
        if (spitStack != null)
        {
            NBTTagCompound spitTag = new NBTTagCompound();
            
            spitStack.writeToNBT(spitTag);
            
            tag.setCompoundTag( "fcSpitStack", spitTag );
        }
        
        if (cookStack != null)
        {
            NBTTagCompound cookTag = new NBTTagCompound();
            
            cookStack.writeToNBT(cookTag);
            
            tag.setCompoundTag( "fcCookStack", cookTag );
        }
        
        tag.setInteger("fcBurnCounter", burnTimeCountdown);
        tag.setInteger("fcBurnTime", burnTimeSinceLit);
        tag.setInteger("fcCookCounter", cookCounter);
        tag.setInteger("fcSmoulderCounter", smoulderCounter);
        tag.setInteger("fcCookBurning", cookBurningCounter);
    }
        
    @Override
    public void updateEntity()
    {
    	super.updateEntity();   

    	if ( !worldObj.isRemote )
    	{
    		int iCurrentFireLevel = getCurrentFireLevel();
    		
	    	if ( iCurrentFireLevel > 0 )
	    	{
	    		if ( iCurrentFireLevel > 1 && worldObj.rand.nextFloat() <= CHANCE_OF_FIRE_SPREAD)
	    		{
    				FireBlock.checkForFireSpreadFromLocation(worldObj, xCoord, yCoord, zCoord, worldObj.rand, 0);
	    		}
	    		
	    		burnTimeSinceLit++;
	    		
	    		if (burnTimeCountdown > 0 )
	    		{
	    			burnTimeCountdown--;
	    			
	    			if ( iCurrentFireLevel == 3 )
	    			{
	    				// blaze burns extra fast
	    				
		    			burnTimeCountdown--;
	    			}
	    		}
	    		
	    		iCurrentFireLevel = validateFireLevel();
	    		
	        	if ( iCurrentFireLevel > 0 )
	        	{
		    		updateCookState();
		    		
	        		if (worldObj.rand.nextFloat() <= CHANCE_OF_GOING_OUT_FROM_RAIN && isRainingOnCampfire() )
	        		{
	        			extinguishFire(false);
	        		}
	        	}
	    	}
	    	else if (smoulderCounter > 0 )
    		{
	    		smoulderCounter--;
	    		
	    		if (smoulderCounter == 0 || worldObj.rand.nextFloat() <= CHANCE_OF_GOING_OUT_FROM_RAIN && isRainingOnCampfire() )
	    		{
	    			stopSmouldering();
	    		}
    		}
    	}
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
        
        if (spitStack != null)
        {
            NBTTagCompound spitTag = new NBTTagCompound();
            
            spitStack.writeToNBT(spitTag);
            
            tag.setCompoundTag( "y", spitTag );
        }
        
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
        
        NBTTagCompound spitTag = tag.getCompoundTag( "y" );

        if ( spitTag != null )
        {
			spitStack = ItemStack.loadItemStackFromNBT(spitTag);
        }        
        
        // force a visual update for the block since the above variables affect it
        
        worldObj.markBlockRangeForRenderUpdate( xCoord, yCoord, zCoord, xCoord, yCoord, zCoord );        
    }    
    
    //------------- Class Specific Methods ------------//
    
    public void setSpitStack(ItemStack stack)
    {
    	if ( stack != null )
    	{
			spitStack = stack.copy();

			spitStack.stackSize = 1;
    	}
    	else
    	{
			spitStack = null;
    	}
    	
        worldObj.markBlockForUpdate( xCoord, yCoord, zCoord );
    }
    
    public ItemStack getSpitStack()
    {
    	return spitStack;
    }
    
    public void setCookStack(ItemStack stack)
    {
    	if ( stack != null )
    	{
			cookStack = stack.copy();

			cookStack.stackSize = 1;
    	}
    	else
    	{
			cookStack = null;

			cookBurningCounter = 0;
    	}

		cookCounter = 0;
		
        worldObj.markBlockForUpdate( xCoord, yCoord, zCoord );
    }
    
    public ItemStack getCookStack()
    {
    	return cookStack;
    }
    
    public void ejectContents()
    {
    	if (spitStack != null )
    	{
    		ItemUtils.ejectStackWithRandomOffset(worldObj, this.xCoord, yCoord, zCoord, spitStack);

			spitStack = null;
    	}
    	
    	if (cookStack != null )
    	{
    		ItemUtils.ejectStackWithRandomOffset(worldObj, this.xCoord, yCoord, zCoord, cookStack);

			cookStack = null;
    	}
    }    
    
    public void addBurnTime(int iBurnTime)
    {
		burnTimeCountdown += iBurnTime * CAMPFIRE_BURN_TIME_MULTIPLIER *
							 TileEntityFurnace.BASE_BURN_TIME_MULTIPLIER;
    	
    	if (burnTimeCountdown > MAX_BURN_TIME)
    	{
			burnTimeCountdown = MAX_BURN_TIME;
    	}
    	
    	validateFireLevel();
    }
 
    public void onFirstLit()
    {
		burnTimeCountdown = INITIAL_BURN_TIME;
		burnTimeSinceLit = 0;
    }
    
    public int validateFireLevel()
    {
    	int iCurrentFireLevel = getCurrentFireLevel();
    	
    	if ( iCurrentFireLevel > 0 )
    	{
    		//int iFuelState = FCBetterThanWolves.fcBlockCampfireUnlit.GetFuelState( worldObj, xCoord, yCoord, zCoord );
    		
    		if (burnTimeCountdown <= 0 )
    		{
				extinguishFire(true);
				
				return 0;
    		}
    		else
    		{
				int iDesiredFireLevel = 2;
				
	    		if (burnTimeSinceLit < WARMUP_TIME || burnTimeCountdown < REVERT_TO_SMALL_TIME)
				{
					iDesiredFireLevel = 1;
				}
				else if (burnTimeCountdown > BLAZE_TIME)
				{
					iDesiredFireLevel = 3;
				}
				
				if ( iDesiredFireLevel != iCurrentFireLevel )
				{
					changeFireLevel(iDesiredFireLevel);
					
					if ( iDesiredFireLevel == 1 && iCurrentFireLevel == 2 )
					{
						worldObj.playAuxSFX(BTWEffectManager.FIRE_FIZZ_EFFECT_ID, xCoord, yCoord, zCoord, 1 );
					}
	    			
	    			return iDesiredFireLevel;
				}
    		}
				
    	}
		else // iCurrenFireLevel == 0 
		{
			if (burnTimeCountdown > 0 &&
                 BTWBlocks.unlitCampfire.getFuelState(worldObj, xCoord, yCoord, zCoord) ==
                 CampfireBlock.CAMPFIRE_FUEL_STATE_SMOULDERING)
			{
				relightSmouldering();
				
				return 1;
			}
		}
    	
    	return iCurrentFireLevel;
    }
    
    private void extinguishFire(boolean bSmoulder)
    {
    	if ( bSmoulder )
    	{
			smoulderCounter = SMOULDER_TIME;
    	}
    	else
    	{
			smoulderCounter = 0;
    	}

		cookCounter = 0; // reset cook counter in case fire is relit later
		cookBurningCounter = 0;
    	
    	CampfireBlock block = (CampfireBlock)(Block.blocksList[worldObj.getBlockId( xCoord, yCoord, zCoord )]);
    	
    	block.extinguishFire(worldObj, xCoord, yCoord, zCoord, bSmoulder);
    }
    
    private void changeFireLevel(int iNewLevel)
    {
    	CampfireBlock block = (CampfireBlock)(Block.blocksList[worldObj.getBlockId( xCoord, yCoord, zCoord )]);
    	
    	block.changeFireLevel(worldObj, xCoord, yCoord, zCoord, iNewLevel, worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
    }
    
    private int getCurrentFireLevel()
    {
    	CampfireBlock block = (CampfireBlock)(Block.blocksList[worldObj.getBlockId( xCoord, yCoord, zCoord )]);
    	
    	return block.fireLevel;
    }
    
	private void updateCookState()
	{
		if (cookStack != null )
		{
			int iFireLevel = getCurrentFireLevel();
			
			if ( iFireLevel >= 2 )
			{				
				ItemStack cookResult = CampfireCraftingManager.instance.getRecipeResult(cookStack.getItem().itemID);
				
				if ( cookResult != null )
				{
					cookCounter++;
					
					if (cookCounter >= TIME_TO_COOK)
					{
						setCookStack(cookResult);

						cookCounter = 0;
						
						// don't reset burn counter here, as the food can still burn after cooking
					}
				}
				
				if ( iFireLevel >= 3 && cookStack.itemID != BTWItems.burnedMeat.itemID )
				{
					cookBurningCounter++;
					
					if (cookBurningCounter >= TIME_TO_BURN_FOOD)
					{
						setCookStack(new ItemStack(BTWItems.burnedMeat));

						cookCounter = 0;
						cookBurningCounter = 0;
					}
				}
			}
		}
	}
	
	public boolean getIsCooking()
	{
		if (cookStack != null && getCurrentFireLevel() >= 2 )
		{
			ItemStack cookResult = CampfireCraftingManager.instance.getRecipeResult(cookStack.getItem().itemID);
			
			if ( cookResult != null )
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean getIsFoodBurning()
	{
		if (cookStack != null && getCurrentFireLevel() >= 3 && cookStack.itemID != BTWItems.burnedMeat.itemID )
		{
			return true;
		}
		
		return false;
	}
	
    public boolean isRainingOnCampfire()
    {
    	CampfireBlock block = (CampfireBlock)(Block.blocksList[worldObj.getBlockId( xCoord, yCoord, zCoord )]);
    	
    	return block.isRainingOnCampfire(worldObj, xCoord, yCoord, zCoord);
    }
    
    private void stopSmouldering()
    {
		smoulderCounter = 0;
    	
    	CampfireBlock block = (CampfireBlock)(Block.blocksList[worldObj.getBlockId( xCoord, yCoord, zCoord )]);
    	
    	block.stopSmouldering(worldObj, xCoord, yCoord, zCoord);
    }
    
    private void relightSmouldering()
    {
		burnTimeSinceLit = 0;
    	
    	CampfireBlock block = (CampfireBlock)(Block.blocksList[worldObj.getBlockId( xCoord, yCoord, zCoord )]);
    	
    	block.relightFire(worldObj, xCoord, yCoord, zCoord);
    }
    
    //----------- Client Side Functionality -----------//
}