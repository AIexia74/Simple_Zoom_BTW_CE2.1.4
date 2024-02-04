// FCMOD

package btw.entity.mob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;

import btw.block.blocks.BlockDispenserBlock;
import btw.block.tileentity.dispenser.BlockDispenserTileEntity;
import btw.client.fx.BTWEffectManager;
import btw.inventory.util.InventoryUtils;
import btw.entity.mob.behavior.*;
import btw.item.BTWItems;
import btw.item.items.ShearsItem;
import btw.util.ColorUtils;
import btw.util.MiscUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class SheepEntity extends EntitySheep
{
    private static final int FULL_WOOL_ACCUMULATION_COUNT = MiscUtils.TICKS_PER_GAME_DAY;
    
    private int woolAccumulationCount = 0;
    
    private int originalWoolColorWatcherID = 20;
    
    public SheepEntity(World world )
    {
        super( world );

        this.texture = "/btwmodtex/fcSheep.png";
        
        tasks.removeAllTasks();
        
        tasks.addTask( 0, new EntityAISwimming( this ) );
        tasks.addTask( 1, new AnimalFleeBehavior( this, 0.38F ) );
        tasks.addTask( 2, new EntityAIMate( this, 0.23F ) );
        tasks.addTask( 3, new MultiTemptBehavior( this, 0.25F ) );
        tasks.addTask( 4, new GrazeBehavior( this ) );
        tasks.addTask( 5, new MoveToLooseFoodBehavior( this, 0.23F ) );
        tasks.addTask( 6, new MoveToGrazeBehavior( this, 0.23F ) );
        tasks.addTask( 7, new EntityAIFollowParent( this, 0.25F ) );
        tasks.addTask( 8, new SimpleWanderBehavior( this, 0.25F ) );
        tasks.addTask( 9, new EntityAIWatchClosest( this, EntityPlayer.class, 6F ) );
        tasks.addTask( 10, new EntityAILookIdle( this ) );
    }
    
    @Override
    public boolean isAIEnabled()
    {
    	return !getWearingBreedingHarness();
    }

    @Override
    protected void dropFewItems( boolean bKilledByPlayer, int iLootingModifier )
    {
        if (!getSheared() && isFullyFed() )
        {
            entityDropItem( new ItemStack( BTWItems.wool.itemID, 1,
            	BlockCloth.getDyeFromBlock( getFleeceColor() ) ), 0.0F);
        }
        
        dropMutton(iLootingModifier);
    }
    
    @Override
    public boolean interact( EntityPlayer player )
    {
        ItemStack stack = player.inventory.getCurrentItem();

        if ( stack != null && stack.getItem() instanceof ShearsItem && !getSheared() && !isChild() )
        {
            if ( !worldObj.isRemote )
            {
                setSheared( true );
                
                int iNumItems = 1 + rand.nextInt( 3 );

                for ( int iTempCount = 0; iTempCount < iNumItems; ++iTempCount )
                {
                    EntityItem tempItem = entityDropItem( new ItemStack( 
                    	BTWItems.wool.itemID, 1,
                    	BlockCloth.getDyeFromBlock( getFleeceColor() ) ), 1.0F);
                    
                    tempItem.motionY += rand.nextFloat() * 0.05F;
                    tempItem.motionX += ( rand.nextFloat() - rand.nextFloat() ) * 0.1F;
                    tempItem.motionZ += ( rand.nextFloat() - rand.nextFloat() ) * 0.1F;
                }
            }

            stack.damageItem( 1, player );
            
            playSound( "mob.sheep.shear", 1F, 1F );
            
            attackEntityFrom( DamageSource.generic, 0 );
            
            if ( stack.stackSize <= 0 )
            {
            	player.inventory.mainInventory[player.inventory.currentItem] = null;
            }
        }

        return entityAnimalInteract(player); // skip super function
    }
    
    @Override
    protected int getDropItemId()
    {
        return BTWItems.wool.itemID;
    }
    
    @Override
    public void writeEntityToNBT( NBTTagCompound tag )
    {
        super.writeEntityToNBT( tag );
        
        tag.setByte( "fcOrgClr", (byte) this.getOriginalFleeceColor() );
        tag.setInteger("fcWoolCount", woolAccumulationCount);
    }

    @Override
    public void readEntityFromNBT( NBTTagCompound tag )
    {
        super.readEntityFromNBT( tag );
        
        if ( tag.hasKey( "fcOrgClr" ) )
        {
        	this.setOriginalFleeceColor(tag.getByte( "fcOrgClr" ));
        }
        
        if ( tag.hasKey( "fcWoolCount" ) )
    	{
			woolAccumulationCount = tag.getInteger("fcWoolCount");
    	}
    }

    @Override
    public boolean getCanCreatureTypeBePossessed()
    {
    	return true;
    }
    
    @Override
    protected void modSpecificOnLivingUpdate()
    {
    	super.modSpecificOnLivingUpdate();
    	
    	if (!isLivingDead && isFullyPossessed() && !getSheared() )
    	{
    		if ( !isInWater() && ! handleLavaMovement() )
    		{
    			if ( this.posY < 125F )
    			{
					this.motionY += 0.08341D; // gravity is 0.08
    			}
    			else
    			{
    				motionY += 0.0725D;
    			}
    			
    			if ( !onGround && !isCollidedHorizontally && worldObj.provider.dimensionId == 0 )
    			{
	    			if ( posY > 100F )
	    			{
	    				if ( !checkForWolfBomb() )
	    				{
		    				// drift with the clouds
		    				
		    				if ( !getSheared() && motionX > -0.012F )
		    				{
		    					motionX -= 0.005;
		    				}
	    				}
	    			}
    			}
    		}
    	}
    }

    @Override
    protected void fall(float par1)
    {
    	// override to prevent fall damage on possessed sheep
    	
    	if (!isFullyPossessed() || getSheared() )
    	{
    		super.fall( par1 );    	
		}
    }
    
    @Override
    public double getMountedYOffset()
    {
		return (double)height;
    }
    
    @Override
    public boolean isBreedingItem( ItemStack stack )
    {
        return stack.itemID == Item.pumpkinPie.itemID;
    }

    @Override
    public boolean isValidZombieSecondaryTarget(EntityZombie zombie)
    {
    	return true;
    }
    
    @Override
    public void initCreature()
    {
    	initHungerWithVariance();
    	
    	int iFleeceColor = getRandomFleeceColor( worldObj.rand );
    	
        if ( iFleeceColor == 0 )
        {
        	int iDiceRoll = worldObj.rand.nextInt( 500 );
        	
	        if ( iDiceRoll == 0 )
	        {
	        	iFleeceColor = 3; // light blue
	        }
	        else if ( iDiceRoll == 1 )
	        {
	        	iFleeceColor = 5; // lime green
	        }
        }
        
        setFleeceColor( iFleeceColor );
    }

    @Override
    public void setFleeceColor( int iColor )
    {
    	super.setFleeceColor( iColor );
    	
        this.setOriginalFleeceColor(iColor);
    }

    @Override
    public EntityAgeable createChild( EntityAgeable parent )
    {
		return spawnHardcoreBaby(parent);
    }

    @Override
    public boolean isSubjectToHunger()
    {
    	return true;
    }
    
    @Override
	public int getFoodValueMultiplier()
    {
    	return 3;
    }
    
    @Override
    public void onBecomeFamished()
    {
    	super.onBecomeFamished();
    	
    	if ( !getSheared() )
    	{
        	setSheared( true );
    	}

		woolAccumulationCount = 0;
    }
    
    @Override
	public void updateHungerState()
    {
		if (getSheared() && isFullyFed() && !isChild() && !getWearingBreedingHarness() )
		{
			// producing wool consumes extra food. Hunger will be validated in super method
			
			hungerCountdown--;
			
        	woolAccumulationCount++;
        	
        	if (woolAccumulationCount >= FULL_WOOL_ACCUMULATION_COUNT)
        	{
        		setFleeceColor( this.getOriginalFleeceColor() );
	    	
	        	setSheared( false );
				woolAccumulationCount = 0;
	        	
		        worldObj.playAuxSFX( BTWEffectManager.SHEEP_REGROW_WOOL_EFFECT_ID,
		        	MathHelper.floor_double( posX ), (int)posY + 1, 
		        	MathHelper.floor_double( posZ ), 0 );
        	}
		}
        
    	// must call super method after extra hunger consumed above to validate
    	
    	super.updateHungerState();
    }
    
    @Override
    protected void entityInit() {
    	super.entityInit();
    	this.dataWatcher.addObject(20, new Byte((byte) 0));
    }
    
	//------------- Class Specific Methods ------------//
    
    public int getOriginalFleeceColor() {
    	return this.dataWatcher.getWatchableObjectByte(originalWoolColorWatcherID) & 15;
    }
    
    public void setOriginalFleeceColor(int originalColor) {
        byte byte0 = dataWatcher.getWatchableObjectByte(originalWoolColorWatcherID);
        dataWatcher.updateObject(originalWoolColorWatcherID, Byte.valueOf((byte)(byte0 & 0xf0 | originalColor & 0xf)));
    }
    
    public void setSuperficialFleeceColor(int par1)
    {
        byte byte0 = dataWatcher.getWatchableObjectByte(16);
        dataWatcher.updateObject(16, Byte.valueOf((byte)(byte0 & 0xf0 | par1 & 0xf)));
    }

    public SheepEntity spawnHardcoreBaby(EntityAgeable parentAnimal)
    {
        SheepEntity parentSheep = (SheepEntity)parentAnimal;
        SheepEntity babySheep = (SheepEntity) EntityList.createEntityOfType(SheepEntity.class, this.worldObj);

		int iMutationChance = rand.nextInt( 100 );
		
		if ( iMutationChance == 0  )
		{
			// outright mutation
			
			int iBabyColor = getMutantColor( this, parentSheep );
			
			babySheep.setFleeceColor( iBabyColor );			
		}
		else if ( iMutationChance <= 3  )
		{			
			// The proverbial black sheep
			
			int iBabyColor = 15;  
			
			babySheep.setFleeceColor( iBabyColor );			
		}
		else if ( iMutationChance <= 23  )
		//else if ( iMutationChance <= 100  )
		{
			// blend the colors of the adults
			
			int iBabyColor = blendParentColors( this, parentSheep );
			
			babySheep.setFleeceColor( iBabyColor );
			
		}		
		else if (this.rand.nextBoolean())
        {
    		babySheep.setFleeceColor( this.getOriginalFleeceColor() );
        }
        else
        {
    		babySheep.setFleeceColor( parentSheep.getOriginalFleeceColor() );
        }

        return babySheep;
    }
    
    private static ArrayList<ColorBlendEntry> colorBlendList = new ArrayList();
    
    static {
    	try {
			colorBlendList.add(new ColorBlendEntry(ColorUtils.BLACK.colorID, ColorUtils.WHITE.colorID, ColorUtils.GRAY.colorID));
	    	colorBlendList.add(new ColorBlendEntry(ColorUtils.BLACK.colorID, ColorUtils.LIGHT_GRAY.colorID, ColorUtils.GRAY.colorID));
	    	colorBlendList.add(new ColorBlendEntry(ColorUtils.GRAY.colorID, ColorUtils.WHITE.colorID, ColorUtils.LIGHT_GRAY.colorID));
	    	colorBlendList.add(new ColorBlendEntry(ColorUtils.BLACK.colorID, ColorUtils.LIGHT_BLUE.colorID, ColorUtils.BLUE.colorID));
	    	colorBlendList.add(new ColorBlendEntry(ColorUtils.BLACK.colorID, ColorUtils.LIME.colorID, ColorUtils.GREEN.colorID));
	    	colorBlendList.add(new ColorBlendEntry(ColorUtils.BLACK.colorID, ColorUtils.PINK.colorID, ColorUtils.RED.colorID));
	    	colorBlendList.add(new ColorBlendEntry(ColorUtils.BLUE.colorID, ColorUtils.PINK.colorID, ColorUtils.MAGENTA.colorID));
	    	colorBlendList.add(new ColorBlendEntry(ColorUtils.RED.colorID, ColorUtils.LIGHT_BLUE.colorID, ColorUtils.MAGENTA.colorID));
	    	colorBlendList.add(new ColorBlendEntry(ColorUtils.BLACK.colorID, ColorUtils.MAGENTA.colorID, ColorUtils.PURPLE.colorID));
	    	colorBlendList.add(new ColorBlendEntry(ColorUtils.RED.colorID, ColorUtils.BLUE.colorID, ColorUtils.PURPLE.colorID));
	    	colorBlendList.add(new ColorBlendEntry(ColorUtils.BLUE.colorID, ColorUtils.GREEN.colorID, ColorUtils.CYAN.colorID));
	    	colorBlendList.add(new ColorBlendEntry(ColorUtils.RED.colorID, ColorUtils.GREEN.colorID, ColorUtils.YELLOW.colorID));
	    	colorBlendList.add(new ColorBlendEntry(ColorUtils.CYAN.colorID, ColorUtils.RED.colorID, ColorUtils.BROWN.colorID));
	    	colorBlendList.add(new ColorBlendEntry(ColorUtils.YELLOW.colorID, ColorUtils.RED.colorID, ColorUtils.ORANGE.colorID));
		} catch (InstanceAlreadyExistsException e) {
			e.printStackTrace();
		}
    }
    
    public static class ColorBlendEntry {
    	private final int color1;
    	private final int color2;
    	
    	private final int outputColor;
    	
    	private static Map<Integer, ColorBlendEntry> entryCache = new HashMap();

		public ColorBlendEntry(int color1, int color2, int outputColor) throws InstanceAlreadyExistsException {
			if (color1 > color2) {
				int tempColor = color1;
				color1 = color2;
				color2 = tempColor;
			}
			
			this.color1 = color1;
			this.color2 = color2;
			
			this.outputColor = outputColor;
			
			int index = ((color1 & 15) << 4) | (color2 & 15);
			
			if (entryCache.get(index) != null) {
				throw new InstanceAlreadyExistsException("Cannot make more than one instance of an entry per color pair");
			}
			
			entryCache.put(index, this);
		}
		
		public int getOutputColor() {
			return BlockCloth.getBlockFromDye(this.outputColor);
		}
		
		public static ColorBlendEntry getCachedEntry(int color1, int color2) {
			if (color1 > color2) {
				int tempColor = color1;
				color1 = color2;
				color2 = tempColor;
			}
			
			int index = ((color1 & 15) << 4) | (color2 & 15);
			
			return entryCache.get(index);
		}
    }
    
    public int blendParentColors(SheepEntity papa, SheepEntity mama) {
    	int papaItemColor = BlockCloth.getBlockFromDye(papa.getOriginalFleeceColor());
    	int mamaItemColor = BlockCloth.getBlockFromDye(mama.getOriginalFleeceColor());
    	
    	ColorBlendEntry blendEntry = ColorBlendEntry.getCachedEntry(papaItemColor, mamaItemColor);
    	
    	if (blendEntry != null) {
    		if (blendEntry == ColorBlendEntry.getCachedEntry(ColorUtils.BLACK.colorID, ColorUtils.WHITE.colorID)) {
    			return this.rand.nextBoolean() ? 
        				BlockCloth.getBlockFromDye(ColorUtils.GRAY.colorID) :
        				BlockCloth.getBlockFromDye(ColorUtils.LIGHT_GRAY.colorID);
    		}
    		
    		return blendEntry.getOutputColor();
    	}
    	else {
    		return this.rand.nextBoolean() ? 
    				BlockCloth.getBlockFromDye(papaItemColor) : 
    				BlockCloth.getBlockFromDye(mamaItemColor);
    	}
    }
    
    public int getMutantColor(SheepEntity papa, SheepEntity mama )
    {
    	// spawn lightened shades of primary colors for use in cross-breeding
    	
    	int iRandomFactor = rand.nextInt( 3 );
    	
    	switch ( iRandomFactor )
    	{
    		case 0:
    			
    	        return 3; // light blue
    	        
    		case 1:
    			
    	        return 5; // lime green
    	        
	        default: // 2
    			
	            return 6; // pink
    	}    	
    }
    
    private void dropMutton(int iLootingModifier)
    {
        if (!hasHeadCrabbedSquid() && !isStarving() )
        {
	        int iNumDropped = rand.nextInt( 2 ) + 1 + rand.nextInt( 1 + iLootingModifier );
	
	        if ( isFamished() )
	        {
	        	iNumDropped = iNumDropped / 2;
	        }
	        
	        for ( int iTempCount = 0; iTempCount < iNumDropped; ++iTempCount )
	        {
	            if ( isBurning() )
	            {
					if (worldObj.getDifficulty().shouldBurningMobsDropCookedMeat()) {
						dropItem(BTWItems.cookedMutton.itemID, 1);
					}
					else {
						dropItem(BTWItems.burnedMeat.itemID, 1);
					}
	            }
	            else
	            {
	                dropItem( BTWItems.rawMutton.itemID, 1 );
	            }
	        }
        }
    }
    
    private boolean checkForWolfBomb()
    {
		if ( !worldObj.isRemote && worldObj.worldInfo.getWorldTime() % 20 == 0 )
		{
			int iSheepI = MathHelper.floor_double( posX );
			int iSheepJ = MathHelper.floor_double( posY );
			int iSheepK = MathHelper.floor_double( posZ );
			
		    // Despite name, actually returns the block ABOVE the top one, and does not count liquids
			int iTopBlockJ = worldObj.getPrecipitationHeight( iSheepI, iSheepK ) - 1;
			
			// ensure sufficient drop onto hard surface to kill the sheep
			if ( iSheepJ - iTopBlockJ >= 16 )
			{
	            int iTopBlockID = worldObj.getBlockId( iSheepI, iTopBlockJ, iSheepK );
	            
            	Block topBlock = Block.blocksList[iTopBlockID];
            	
            	if ( topBlock != null && !topBlock.blockMaterial.isLiquid() )
            	{            		
            		if ( isPossessableWolfWithinRangeOfBlock(iSheepI, iTopBlockJ, iSheepK, 8) )
            		{
            			initiateWolfBomb();
            			
            	        worldObj.playSoundAtEntity( this, getDeathSound(), getSoundVolume(), (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F );
            	        worldObj.playSoundAtEntity( this, "mob.slime.attack", getSoundVolume(), ( rand.nextFloat() - rand.nextFloat()) * 0.2F + 0.6F );
            			
            			return true;
            		}
            	}
			}
		}
		
		return false;		
    }
    
    private boolean isPossessableWolfWithinRangeOfBlock(int i, int j, int k, int iRange)
    {
		AxisAlignedBB possessionBox = AxisAlignedBB.getAABBPool().getAABB( 
			(double)( i - iRange ), (double)( j - iRange ), (double)( k - iRange ),
			(double)( i + 1 + iRange ), (double)( j + 1 + iRange ), (double)( k + 1 + iRange ) );
		
        List wolvesInBox = worldObj.getEntitiesWithinAABB( WolfEntity.class, possessionBox );
        
        Iterator itemIterator = wolvesInBox.iterator();
    	
        while ( itemIterator.hasNext())
        {
    		WolfEntity tempWolf = (WolfEntity)itemIterator.next();
    		
	        if ( !tempWolf.isLivingDead && !tempWolf.isPossessed() )
	        {
        		return true;
	        }	        
        }
        
        return false;
    }
    
    private void initiateWolfBomb()
    {
    	// eject the sheep's wool so it drops out of the sky
    	
        setSheared( true );
        
        int iItemCount = 1 + this.rand.nextInt(3);

        for ( int iTempCount = 0; iTempCount < iItemCount; ++iTempCount )
        {
            EntityItem tempStack = entityDropItem( new ItemStack( BTWItems.wool.itemID, 1,
            	BlockCloth.getDyeFromBlock( getFleeceColor() ) ), 1.0F );
            
            tempStack.motionY += rand.nextFloat() * 0.05F;
            tempStack.motionX += ( rand.nextFloat() - rand.nextFloat() ) * 0.1F;
            tempStack.motionZ += ( rand.nextFloat() - rand.nextFloat() ) * 0.1F;
        }
    }
    
    protected boolean isTooHungryToProduceWool()
    {
    	return hungerCountdown < (FULL_HUNGER_COUNT * 3 ) / 4;
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void handleHealthUpdate( byte bUpdateType )
    {
    	// must override super or else EntitySheep will intercept update type 10 and
    	// set the wrong variable
    	
        if ( bUpdateType == 10 )
        {
            grazeProgressCounter = getGrazeDuration();
        }
        else
        {
            super.handleHealthUpdate( bUpdateType );
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public String getTexture()
    {
    	if ( getWearingBreedingHarness() )
    	{
			return "/btwmodtex/fc_mr_sheep.png";
    	}
    	
    	int iHungerLevel = getHungerLevel();
    	
    	if ( iHungerLevel == 1 )
    	{
			return "/btwmodtex/fcSheepFamished.png";
    	}
    	else if ( iHungerLevel == 2 )
    	{
			return "/btwmodtex/fcSheepStarving.png";
    	}

        return super.getTexture();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public float func_70894_j( float fPartialTick )
    {
		return getGrazeHeadVerticalOffset(fPartialTick);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public float func_70890_k( float fPartialTick )
    {
		return getGrazeHeadRotation(fPartialTick);
    }
	
	@Override
	public boolean onBlockDispenserConsume(BlockDispenserBlock blockDispenser, BlockDispenserTileEntity tileEntity) {

		if ( !getSheared() && !isChild() )
		{
			setSheared( true );
			
    		InventoryUtils.addSingleItemToInventory(tileEntity,
													BTWItems.wool.itemID,
													BlockCloth.getDyeFromBlock( getFleeceColor() ));
    		
            attackEntityFrom( DamageSource.generic, 0 );
            
			for ( int tempCount = 0; tempCount < 2; tempCount++ )
			{
				blockDispenser.spitOutItem(worldObj, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, new ItemStack(Item.silk ));
			}
			
            return true;
		}
		// sheep isn't shearable, check other entities
		return false;
	}
}
