// FCMOD

package btw.entity.mob;

import btw.block.blocks.BlockDispenserBlock;
import btw.block.blocks.GroundCoverBlock;
import btw.block.tileentity.dispenser.BlockDispenserTileEntity;
import btw.client.fx.BTWEffectManager;
import btw.inventory.util.InventoryUtils;
import btw.entity.mob.behavior.*;
import btw.item.BTWItems;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class ChickenEntity extends EntityChicken
{
    protected long timeToLayEgg = 0;
    
    public ChickenEntity(World world )
    {
        super( world );
        
        getNavigator().setAvoidsWater( true );
        
        tasks.removeAllTasks();
        
        tasks.addTask( 0, new EntityAISwimming( this ) );
        tasks.addTask( 1, new EntityAIPanic( this, 0.38F ) );
        tasks.addTask( 2, new EntityAIMate( this, 0.25F ) );
        tasks.addTask( 3, new MultiTemptBehavior( this, 0.25F ) );
        tasks.addTask( 4, new GrazeBehavior( this ) );
        tasks.addTask( 5, new MoveToLooseFoodBehavior( this, 0.25F ) );
        tasks.addTask( 6, new MoveToGrazeBehavior( this, 0.25F ) );
        tasks.addTask( 7, new EntityAIFollowParent( this, 0.28F ) );
        tasks.addTask( 8, new SimpleWanderBehavior( this, 0.25F ) );
        tasks.addTask( 9, new EntityAIWatchClosest( this, EntityPlayer.class, 6F ) );
        tasks.addTask( 10, new EntityAILookIdle( this ) );
        
        renderDistanceWeight = 2D; // render further away than their size would normally allow
    }
    
    @Override
    public void onLivingUpdate()
    {
    	timeUntilNextEgg = 6000; // reset the vanilla egg laying counter so it never completes
    	
        super.onLivingUpdate();
    }

    @Override
    public void writeEntityToNBT( NBTTagCompound tag )
    {
        super.writeEntityToNBT( tag );
        
	 	tag.setLong("fcTimeToLayEgg", timeToLayEgg);
    }
    
    @Override
    public void readEntityFromNBT( NBTTagCompound tag )
    {
        super.readEntityFromNBT( tag );

	    if ( tag.hasKey( "fcTimeToLayEgg" ) )
	    {
            timeToLayEgg = tag.getLong("fcTimeToLayEgg");
	    }
	    else
	    {
            timeToLayEgg = 0;
	    }
    }
    
    @Override
    protected void playStepSound( int iBlockI, int iBlockJ, int iBlockK, int iBlockID )
    {
    	// Override to get rid of annoying clicking vanilla sound
    }
    
    @Override
    protected void dropFewItems( boolean bKilledByPlayer, int iLootingModifier )
    {
    	if ( !isStarving() )
    	{
	        int iNumDrops = rand.nextInt(3) + rand.nextInt(1 + iLootingModifier) + 1;
	
	        if ( isFamished() )
	        {
	        	iNumDrops = iNumDrops / 2;
	        }
	
	        for ( int iTempCount = 0; iTempCount < iNumDrops; ++iTempCount )
	        {
	            dropItem( Item.feather.itemID, 1 );
	        }
	
	        if (isFullyFed() && !hasHeadCrabbedSquid() )
	        {
	            if ( isBurning() )
	            {
					if (worldObj.getDifficulty().shouldBurningMobsDropCookedMeat()) {
						dropItem(Item.chickenCooked.itemID, 1);
					}
					else {
						dropItem(BTWItems.burnedMeat.itemID, 1);
					}
	            }
	            else
	            {
	                dropItem( Item.chickenRaw.itemID, 1 );
	            }
	        }
    	}
    }
    
    @Override
    public ChickenEntity spawnBabyAnimal(EntityAgeable parent )
    {
        return (ChickenEntity) EntityList.createEntityOfType(ChickenEntity.class, worldObj);
    }
    
    @Override
    public boolean isReadyToEatBreedingItem()
    {
    	return isFullyFed() && getGrowingAge() == 0 && timeToLayEgg == 0;
    }
    
	@Override
    public void onEatBreedingItem()
    {
    	long lCurrentTime = WorldUtils.getOverworldTimeServerOnly();
    	
    	// following morning, at least half day from now

        timeToLayEgg = (((lCurrentTime + 12000L ) / 24000L ) + 1 ) * 24000L;
    	
    	// crack of dawn (22550) + 30 seconds random variance

        timeToLayEgg +=  (long)(-1450 + rand.nextInt(600) );
    	
		worldObj.playSoundAtEntity( this, 
        		getDeathSound(), getSoundVolume(), 
        		rand.nextFloat() * 0.2F + 1.5F );		
    }
    
	@Override
    public boolean isAffectedByMovementModifiers()
    {
    	return false;
    }

    @Override
    public boolean getCanCreatureTypeBePossessed()
    {
    	return true;
    }
    
	@Override
    public void onFullPossession()
    {
        worldObj.playAuxSFX( BTWEffectManager.POSSESSED_CHICKEN_EXPLOSION_EFFECT_ID,
    		MathHelper.floor_double( posX ), MathHelper.floor_double( posY ), MathHelper.floor_double( posZ ), 
    		0 );
        
        // explode feathers
        
        int iFeatherCount = rand.nextInt(3) + 3;

        for (int iTempCount = 0; iTempCount < iFeatherCount; iTempCount++)
        {
    		ItemStack itemStack = new ItemStack( Item.feather.itemID, 1, 0 );

        	double dFeatherX = posX + ( worldObj.rand.nextDouble() - 0.5D ) * 2D;
        	double dFeatherY = posY + 0.5D;
        	double dFeatherZ = posZ + ( worldObj.rand.nextDouble() - 0.5D ) * 2D;
        	
            EntityItem entityitem = (EntityItem) EntityList.createEntityOfType(EntityItem.class, worldObj, dFeatherX, dFeatherY, dFeatherZ, itemStack );
            
            entityitem.motionX = ( worldObj.rand.nextDouble() - 0.5D ) * 0.5D;
            entityitem.motionY = 0.2D + worldObj.rand.nextDouble() * 0.3D;
            entityitem.motionZ = ( worldObj.rand.nextDouble() - 0.5D ) * 0.5D;
            
            entityitem.delayBeforeCanPickup = 10;
            
            worldObj.spawnEntityInWorld( entityitem );
        }
        
        attemptToPossessNearbyCreatureOnDeath();
        
		setDead();
    }
        
    @Override
    public double getMountedYOffset()
    {
		return (double)height * 1.3F;
    }
    
    @Override
    public boolean isBreedingItem( ItemStack stack )
    {
        return stack.itemID == BTWItems.chickenFeed.itemID;
    }
    
    @Override
    protected String getLivingSound()
    {
    	if ( !isStarving() )
    	{
    		return "mob.chicken.say";
    	}
    	else
    	{
    		return "mob.chicken.hurt";
    	}
    }

    @Override
    public boolean isSubjectToHunger()
    {
    	return true;
    }
    
    @Override
    public int getFoodValueMultiplier()
    {
    	return 1;
    }    

    @Override
    public boolean shouldNotifyBlockOnGraze()
    {
    	// only clear a fraction of the blocks that we graze
    	
    	return rand.nextInt( 8 ) == 0;
    }

    @Override
    public void playGrazeFX(int i, int j, int k, int iBlockID)
    {
    	// chickens don't play block destroy effect since they are just pecking until
    	// the vegetation dies, rather than eating it
    }
    
    @Override
    public BlockPos getGrazeBlockForPos()
    {
    	// chickens can't graze under snow and ash
    	
    	BlockPos pos = super.getGrazeBlockForPos();
    	
    	if ( pos != null && GroundCoverBlock.isGroundCoverRestingOnBlock(worldObj,
																		 pos.x, pos.y, pos.z) )
    	{
			return null;
    	}
    	
    	return pos;
    }
    
    @Override
    public int getGrazeDuration()
    {
    	return 20;
    }
    
    @Override
    public boolean shouldStayInPlaceToGraze()
    {
    	// wander off occasionally so they don't OCD the same spot
    	
    	if ( rand.nextInt( 10 ) != 0 )
    	{
    		return super.shouldStayInPlaceToGraze();
    	}
    	
    	return false;
    }
    
    @Override
    public boolean isHungryEnoughToForceMoveToGraze()
    {
    	// chickens are more frantic in their movements
    	
		return isChild() || !isFullyFed() || hungerCountdown +
                                             (getGrazeHungerGain() * 3 / 4 ) <= FULL_HUNGER_COUNT;
    }
    
    @Override
    public int getItemFoodValue(ItemStack stack)
    {
    	return stack.getItem().getChickenFoodValue(stack.getItemDamage()) *
               getFoodValueMultiplier();
    }
    
    @Override
    public void onBecomeFamished()
    {
    	super.onBecomeFamished();
    	
		// if the chicken is ever not in the fully fed state, it cancels egg production

        timeToLayEgg = 0;
    }
    
    @Override
    public void updateHungerState()
    {
        if (!isChild() && isFullyFed() && timeToLayEgg > 0 && validateTimeToLayEgg() )
        {
			// producing eggs consumes extra food. Hunger will be validated in super method
			
        	// Decided against extra hunger on chickens as I didn't think it would read well
			//m_iHungerCountdown--;
			
    		if (WorldUtils.getOverworldTimeServerOnly() > timeToLayEgg)
    		{
	            playSound( "mob.slime.attack", 1.0F, getSoundPitch() );
	            
	            playSound( getDeathSound(), getSoundVolume(), ( getSoundPitch() + 0.25F ) * ( getSoundPitch() + 0.25F ) );
	            dropItem(Item.egg.itemID, 1);

                timeToLayEgg = 0;
    		}
        }
        
    	// must call super method after extra hunger consumed above to validate
    	
    	super.updateHungerState();
    }
    
	//------------- Class Specific Methods ------------//
    
    private boolean validateTimeToLayEgg()
    {
    	long lCurrentTime = WorldUtils.getOverworldTimeServerOnly();
    	long lDeltaTime = timeToLayEgg - lCurrentTime;
    	
    	if ( lDeltaTime > 48000L ) 
    	{
    		// we're more than 2 days before the time, something is wrong (like a time change command), so don't lay

            timeToLayEgg = 0;
    		
    		return false;
    	}
    	
    	return true;
    }

    @Override
    public float getGrazeHeadRotationMagnitudeDivisor()
    {
    	return 3F;
    }
    
    @Override
    public float getGrazeHeadRotationRateMultiplier()
    {
    	return 28.7F / 2F;
    }
   
	@Override
	public boolean onBlockDispenserConsume(BlockDispenserBlock blockDispenser, BlockDispenserTileEntity tileEntity) {
		
        worldObj.playAuxSFX( BTWEffectManager.CHICKEN_HURT_EFFECT_ID,tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, 0 );
        
		setDead();
		
		InventoryUtils.addSingleItemToInventory(tileEntity, Item.egg.itemID, 0);

		return true;
		
	}
	//----------- Client Side Functionality -----------//    

    @Override
    @Environment(EnvType.CLIENT)
    public String getTexture()
    {
    	int iHungerLevel = getHungerLevel();
    	
    	if ( iHungerLevel == 1 )
    	{
			return "/btwmodtex/fcChickenFamished.png";
    	}
    	else if ( iHungerLevel == 2 )
    	{
			return "/btwmodtex/fcChickenStarving.png";
    	}
    	
        return super.getTexture();
    }
}
