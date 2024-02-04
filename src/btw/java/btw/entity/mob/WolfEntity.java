// FCMOD

package btw.entity.mob;

import btw.block.BTWBlocks;
import btw.block.blocks.BlockDispenserBlock;
import btw.block.tileentity.dispenser.BlockDispenserTileEntity;
import btw.client.fx.BTWEffectManager;
import btw.entity.mob.villager.VillagerEntity;
import btw.inventory.util.InventoryUtils;
import btw.entity.mob.behavior.*;
import btw.item.BTWItems;
import btw.util.MiscUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Iterator;
import java.util.List;

public class WolfEntity extends EntityWolf
{
	private static final int IS_ENGAGED_IN_POSSESSION_ATTEMPT_DATA_WATCHER_ID = 26;

	private static final float MOVE_SPEED_AGGRESSIVE = 0.45F;
	private static final float MOVE_SPEED_PASSIVE = 0.3F;

	public int howlingCountdown = 0;

	public int heardHowlCountdown = 0;

	public int infectionCountdown = -1;

	private static final int MINIMUM_INFECTION_TIME = 12000;
	private static final int INFECTION_TIME_VARIANCE = 12000;

	private float possessionHeadRotation = 0F;
	private boolean isDoingHeadSpin = false;
	private boolean hasHeadSpunOnThisPossessionAttempt = false;
	private int possessionAttemptCountdown = 0;

	private static final int CHANCE_OF_POSSESSION_ATTEMPT = (MiscUtils.TICKS_PER_MINUTE * 10 );
	private static final int POSSESSION_ATTEMPT_TIME = (MiscUtils.TICKS_PER_SECOND * 10 );

	protected static final int HUNGER_COUNT_VARIANCE = ( MiscUtils.TICKS_PER_MINUTE);

	public WolfEntity(World world )
	{
		super( world );

		moveSpeed = MOVE_SPEED_AGGRESSIVE;

		tasks.removeAllTasks();

		tasks.addTask( 1, new EntityAISwimming( this ) );
		tasks.addTask( 2, new PanicOnHeadCrabBehavior(this, MOVE_SPEED_AGGRESSIVE));
		tasks.addTask( 3, aiSit );
		tasks.addTask( 4, new EntityAILeapAtTarget( this, 0.4F ) );
		// FCTODO: Should the following be passive move speed or agressive?
		tasks.addTask( 5, new EntityAIAttackOnCollide(this, MOVE_SPEED_PASSIVE, true ));
		tasks.addTask( 6, new EntityAIFollowOwner(this, MOVE_SPEED_PASSIVE, 10F, 2F ));
		tasks.addTask( 7, new EntityAIMate(this, MOVE_SPEED_PASSIVE));
		tasks.addTask( 7, new MultiTemptBehavior(this, MOVE_SPEED_PASSIVE));
		tasks.addTask( 8, new WolfHowlBehavior( this ) );
		tasks.addTask( 8, new SittingWolfHowlBehavior( this ) );
		tasks.addTask( 9, new MoveToLooseFoodBehavior(this, MOVE_SPEED_PASSIVE));
		tasks.addTask( 10, new SimpleWanderBehavior(this, MOVE_SPEED_PASSIVE));
		tasks.addTask( 11, new EntityAIBeg( this, 8F ) );
		tasks.addTask( 12, new EntityAIWatchClosest( this, EntityPlayer.class, 8F ) );
		tasks.addTask( 13, new EntityAILookIdle( this ) );        

		targetTasks.removeAllTasks();

		targetTasks.addTask( 1, new EntityAIOwnerHurtByTarget( this ) );
		targetTasks.addTask( 2, new EntityAIOwnerHurtTarget( this ) );
		targetTasks.addTask( 3, new EntityAIHurtByTarget( this, true ) );

		targetTasks.addTask( 4, new WildWolfTargetIfStarvingOrHostileBehavior( this,
				VillagerEntity.class, 16F, 0, false ) );

		targetTasks.addTask( 4, new WildWolfTargetIfStarvingOrHostileBehavior( this,
				EntityPlayer.class, 16F, 0, false ) );

		targetTasks.addTask( 4, new WildWolfTargetIfHungryBehavior( this,
				ChickenEntity.class, 16F, 0, false ) );

		targetTasks.addTask( 4, new WildWolfTargetIfHungryBehavior( this,
				SheepEntity.class, 16F, 0, false ) );

		targetTasks.addTask( 4, new WildWolfTargetIfHungryBehavior( this,
				PigEntity.class, 16F, 0, false ) );

		targetTasks.addTask( 4, new WildWolfTargetIfStarvingBehavior( this,
				CowEntity.class, 16F, 0, false ) );
	}

	@Override
	public void setAttackTarget( EntityLiving target )
	{
		entityLivingSetAttackTarget(target); // bypass parent
	}
	
	@Override
	public void setRevengeTarget(EntityLiving target)	{
		super.setRevengeTarget(target);
		
		//Wolves hold grudges :)
		if (!isTamed()) {
			if(target instanceof EntityPlayer) {
				setAngry(true);
			}

			setAttackTarget(target);
		}
	}

	@Override
	public void onKillEntity( EntityLiving entityKilled )
	{
		//    	On kill player
		if (entityKilled instanceof EntityPlayer)
		{

			int breedableWolf = 0;
			int maxWolvesFed = 4;
			//    		set breeding self
			if (this.isReadyToEatBreedingItem())
			{
				this.onEatBreedingItem();
				breedableWolf++;
			}
			//        	Get entities in certain range
			List nearbyEntityList = worldObj.getEntitiesWithinAABBExcludingEntity( this, 
					boundingBox.expand( 16D, 8D, 16D ) );

			Iterator nearbyEntityIterator = nearbyEntityList.iterator();

			//              set a max to number of wolves being fed breedingitem
			while ( nearbyEntityIterator.hasNext() && breedableWolf <=maxWolvesFed)
			{
				Entity nearbyEntity = (Entity)nearbyEntityIterator.next();

				//                	check entities for being wolf
				if ( nearbyEntity instanceof WolfEntity)
				{

					WolfEntity nearbyWolf = (WolfEntity)nearbyEntity;
					//                        only go into breeding mode when able to
					if (nearbyWolf.isReadyToEatBreedingItem())
					{
						nearbyWolf.onEatBreedingItem();
						breedableWolf++;
					}
				}
			}
		}

	}

	@Override
	public int getMaxHealth()
	{
		return 20;
	}    

	@Override
	protected void entityInit()
	{
		super.entityInit();

		dataWatcher.addObject(IS_ENGAGED_IN_POSSESSION_ATTEMPT_DATA_WATCHER_ID, new Byte((byte)0 ));

		resetHungerCountdown();
	}

	@Override
	public void writeEntityToNBT( NBTTagCompound tag )
	{
		super.writeEntityToNBT(tag);

		tag.setInteger("fcInfection", infectionCountdown);
	}

	@Override
	public void readEntityFromNBT( NBTTagCompound tag )
	{
		super.readEntityFromNBT( tag );

		if ( tag.hasKey( "bIsFed" ) )
		{
			boolean bIsFed = tag.getBoolean( "bIsFed" );

			if ( bIsFed )
			{
				setHungerLevel(0);
			}
			else
			{
				setHungerLevel(1);
			}
		}

		if ( tag.hasKey( "fcInfection" ) )
		{
			infectionCountdown = tag.getInteger("fcInfection");
		}
	}

	@Override
	protected boolean canDespawn()
	{
		return false;
	}

	@Override
	protected String getLivingSound()
	{
		if ( isWildAndHostile() )
		{
			return "mob.wolf.growl";
		}
		else if ( this.rand.nextInt(3) == 0 )
		{
			if ( isTamed() && ( dataWatcher.getWatchableObjectInt(18) < 10 || !isFullyFed() ) )
			{
				if ( isStarving() )
				{
					return "mob.wolf.growl";
				}
				else
				{
					return "mob.wolf.whine";
				}
			}
			else
			{
				return "mob.wolf.panting";
			}
		}
		else
		{
			return "mob.wolf.bark";
		}        
	}

	@Override
	protected int getDropItemId()
	{
		if ( !worldObj.isRemote )
		{
			// yummy yummy wolfchops

			if ( isBurning() )
			{
				if (worldObj.getDifficulty().shouldBurningMobsDropCookedMeat()) {
					dropItem(BTWItems.cookedWolfChop.itemID, 1);
				}
				else {
					dropItem(BTWItems.burnedMeat.itemID, 1);
				}
			} 
			else
			{            
				return BTWItems.rawWolfChop.itemID;
			}
		}

		return -1;
	}

	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();

		if ( worldObj.isRemote )
		{
			howlingCountdown = Math.max(0, howlingCountdown - 1);
		}
		else
		{
			heardHowlCountdown = Math.max(0, heardHowlCountdown - 1);

			if (infectionCountdown > 0 )
			{
				infectionCountdown--;

				if (infectionCountdown <= 0 )
				{
					transformToDire();

					return; // entity is no longer valid after transformation
				}
			}
		}
	}

	@Override
	public boolean attackEntityFrom( DamageSource source, int iDamageAmount )
	{
		if ( !isEntityInvulnerable() && !worldObj.isRemote && !isTamed() )
		{
			if ( source.getEntity() instanceof EntityPlayer )
			{
				// When wild wolves are attacked by a player they turn permanently hostile

				setAngry( true );
			}           
		}

		return super.attackEntityFrom(source, iDamageAmount);
	}

	@Override
	public int getMeleeAttackStrength(Entity target)
	{
		return 4;
	}

	@Override
	public boolean attackEntityAsMob( Entity target )
	{
		// override parent, so wild and tame wolves do the same damage

		return meleeAttack(target);
	}

	@Override
	public boolean interact( EntityPlayer player )
	{
		ItemStack playerStack = player.inventory.getCurrentItem();

		if ( playerStack != null )
		{
			if (playerStack.itemID == BTWItems.rawWolfChop.itemID || playerStack.itemID == BTWItems.cookedWolfChop.itemID) {
				// turn wolves hostile when you try to feed them wolf meat

				worldObj.playSoundAtEntity(this, "mob.wolf.growl", getSoundVolume(), (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);

				setAngry(true);
				setTamed(false);
				setOwner("");

				setAttackTarget( player );

				return true;
			}

			if ( attemptUseStackOn(player, playerStack) )
			{
				if ( !player.capabilities.isCreativeMode )
				{
					playerStack.stackSize--;

					if ( playerStack.stackSize <= 0 )
					{
						player.inventory.setInventorySlotContents(
								player.inventory.currentItem, null );
					}
				}

				return true;
			}
		}
		if ( entityAnimalInteract(player) )
			return true;

		if (!worldObj.isRemote && isTamed() && player.username.equalsIgnoreCase(getOwnerName()) && (playerStack == null || !isEdibleItem(playerStack) || !isBreedingItem(playerStack) ))
		{
			aiSit.setSitting( !isSitting() );
			isJumping = false;
			setPathToEntity( null );
		}

		return false;
	}

	@Override
	public boolean getCanBeHeadCrabbed(boolean bSquidInWater)
	{
		// can only head crabbed when they randomly collide with a squid out of water, 
		// but are never actually targetted

		return !bSquidInWater && riddenByEntity == null && isEntityAlive() && !isChild();
	}

	@Override
	public double getMountedYOffset()
	{
		return (double)height * 1.2D;
	}
	
	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return (stack !=null && (stack.itemID == BTWItems.rawMysteryMeat.itemID || stack.itemID == BTWItems.cookedMysteryMeat.itemID));
	}

	@Override
	public void setAngry( boolean bAngry )
	{
		super.setAngry( bAngry );

		if ( bAngry )
		{
			setSitting(false);
		}
	}

	@Override
	public boolean isReadyToEatLooseFood() {
		return !isFullyFed();
	}

	@Override
	public boolean isReadyToEatLooseItem(ItemStack stack)
	{
		Item tempItem = stack.getItem();

		if (this.isBreedingItem(stack) && isReadyToEatBreedingItem()) {
			return true;
		}
		else if (isReadyToEatLooseFood() && tempItem.isWolfFood() && (tempItem.itemID != Item.rottenFlesh.itemID || isStarving() ) )
		{
			return true;
		}

		return false;
	}

	@Override
	public boolean attemptToEatLooseItem(ItemStack stack)
	{
		if ( isReadyToEatLooseItem(stack) )
		{
			onEat(stack.getItem());

			return true;
		}

		return false;
	}

	@Override
	public boolean isEdibleItem(ItemStack stack)
	{
		return stack.getItem().isWolfFood();
	}
	public boolean attemptToBeHandFedItem(ItemStack stack) {
		if (dataWatcher.getWatchableObjectInt(18) < 20 || !isFullyFed() || (isBreedingItem(stack) && isReadyToEatBreedingItem())) {
			this.onEat(stack.getItem());
			return true;
		}

		return false;
	}

	@Override
	public void onNearbyPlayerBlockAddOrRemove(EntityPlayer player)
	{
		// do nothing so that the player isn't attacked by wolves every time they place or remove a block
	}

	@Override
	protected void onNearbyPlayerStartles(EntityPlayer player)
	{
		if ( !isTamed() )
		{
			if ( getAttackTarget() == null )
			{
				setAttackTarget( player );
			}
		}
	}

	@Override
    public boolean getCanCreatureTypeBePossessed()
	{
		return true;
	}

	@Override
	protected void handlePossession()
	{
		super.handlePossession();

		if ( isFullyPossessed() )
		{
			if ( isEngagedInPossessionAttempt() )
			{
				if ( !hasHeadSpunOnThisPossessionAttempt)
				{
					hasHeadSpunOnThisPossessionAttempt = true;

					isDoingHeadSpin = true;

					if ( !worldObj.isRemote )
					{
						playSound( "portal.portal", 3.0F, rand.nextFloat() * 0.1F + 0.75F );
					}
				}

				if ( !worldObj.isRemote )
				{    				
					possessionAttemptCountdown--;

					if (possessionAttemptCountdown <= 0 )
					{
						setEngagedInPossessionAttempt(false);

						attemptToPossessNearbyCreature(16D, false);
					}
				}
			}
			else
			{
				hasHeadSpunOnThisPossessionAttempt = false;

				if ( !worldObj.isRemote )
				{
					if (rand.nextInt(CHANCE_OF_POSSESSION_ATTEMPT) == 0 )
					{
						setEngagedInPossessionAttempt(true);

						possessionAttemptCountdown = POSSESSION_ATTEMPT_TIME;
					}
				}
			}

			updateHeadSpin();
		}
	}

	@Override
	public void onNearbyAnimalAttacked(EntityAnimal attackedAnimal, EntityLiving attackSource)
	{
		// wolves and ocelots don't give a shit if a nearby animal is attacked
	}

	@Override
	public void notifyOfWolfHowl(Entity sourceEntity)
	{
		if ( !isLivingDead )
		{
			double dDeltaX = posX - sourceEntity.posX;
			double dDeltaZ = posZ - sourceEntity.posZ;

			double dDistSq = dDeltaX * dDeltaX + dDeltaZ * dDeltaZ;

			if ( dDistSq < WolfHowlBehavior.HEAR_HOWL_DISTANCE_SQ)
			{
				if ( this != sourceEntity )
				{
					heardHowlCountdown = WolfHowlBehavior.HEARD_HOWL_DURATION;
				}
			}
		}
	}
	
	//	Hiracho: changed so pups are always wild, so wild wolves can breed too.
	@Override
	public WolfEntity spawnBabyAnimal(EntityAgeable parent )
	{
		return (WolfEntity) EntityList.createEntityOfType(WolfEntity.class, worldObj);
	}


	@Override
	public boolean isSubjectToHunger()
	{
		return !this.isPossessed();
	}

	@Override
	public void updateHungerState()
	{
		super.updateHungerState();

		updateShitState();
	}

	@Override
	public void onStarvingCountExpired()
	{
		super.onStarvingCountExpired();

		if ( isEntityAlive() && !isFullyPossessed() && this.worldObj.getDifficulty().canAnimalsStarve())
		{
			// Turn tamed wolves wild and hostile when they've been starving for too long

			setAngry( true );

			setTamed( false );		            
			setOwner( "" );
		}		
	}

	@Override
	public void resetHungerCountdown()
	{
		hungerCountdown = FULL_HUNGER_COUNT + (rand.nextInt(
				HUNGER_COUNT_VARIANCE * 2) - HUNGER_COUNT_VARIANCE);
	}

	@Override
	public float getHungerSpeedModifier()
	{
		// wolves don't slow down when they get hungry... they just get mad

		return 1F;
	}

	@Override
	public boolean isTooHungryToGrow()
	{
		return !isFullyFed();
	}

	@Override
	public boolean isTooHungryToHeal()
	{
		// handles own healing independent of hunger

		return true; 
	}

	@Override
	public int getItemFoodValue(ItemStack stack)
	{
		// class processes its own food values, so this prevents EntityAnimal from treating
		// it like a herbivore.

		return 0;
	}
	@Override
	public boolean canMateWith(EntityAnimal par1EntityAnimal) {
		if (par1EntityAnimal == this) {
			return false;
		}
		else if (!(par1EntityAnimal instanceof EntityWolf)) {
			return false;
		}
		else {
			WolfEntity partner = (WolfEntity) par1EntityAnimal;
			return !partner.isSitting() && this.isInLove() && partner.isInLove();
		}
	}
	//------------- Class Specific Methods ------------//

	public boolean isEngagedInPossessionAttempt()
	{
		return dataWatcher.getWatchableObjectByte(IS_ENGAGED_IN_POSSESSION_ATTEMPT_DATA_WATCHER_ID) > 0;
	}

	public void setEngagedInPossessionAttempt(boolean bIsEngaged)
	{
		Byte tempByte = 0;

		if ( bIsEngaged )
		{
			tempByte = 1;
		}

		dataWatcher.updateObject(IS_ENGAGED_IN_POSSESSION_ATTEMPT_DATA_WATCHER_ID, Byte.valueOf(tempByte));
	}

	private void updateShitState()
	{
		if ( isFullyFed() )
		{
			int chanceOfShitting = 1;

			if ( isDarkEnoughToAffectShitting() )
			{
				chanceOfShitting *= 2;
			}

			// a wolf shits on average every 20 minutes if in the light
			if ( worldObj.rand.nextInt( 24000 ) < chanceOfShitting && !this.isPossessed() )
			{
				attemptToShit();
			}
		}        
	}

	private boolean attemptUseStackOn(EntityPlayer player, ItemStack playerStack)
	{
		if ( isTamed() )
		{
			if ( playerStack.itemID == Item.dyePowder.itemID )
			{
				int iNewColor = BlockCloth.getBlockFromDye( playerStack.getItemDamage() );

				if ( iNewColor != getCollarColor() )
				{
					setCollarColor( iNewColor );

					return true;
				}
			}
			else if ( playerStack.itemID == BTWItems.dung.itemID )
			{
				int iNewColor = 12;

				if ( iNewColor != getCollarColor() )
				{
					setCollarColor( iNewColor );

					if ( !worldObj.isRemote )
					{
						worldObj.playAuxSFX( BTWEffectManager.APPLY_DUNG_TO_WOLF_EFFECT_ID,
								MathHelper.floor_double( posX ), MathHelper.floor_double( posY ), 
								MathHelper.floor_double( posZ ), 0 );
					}

					return true;
				}
			}
		}
		else // !isTamed()
		{
			if (  playerStack.itemID == Item.bone.itemID && !isWildAndHostile() )
			{
				if ( !worldObj.isRemote )
				{
					if ( rand.nextInt( 3 ) == 0 )
					{
						setTamed( true );

						setPathToEntity( null );
						setAttackTarget( null );
						aiSit.setSitting( true );
						setEntityHealth( 20 );
						setOwner( player.username );
						playTameEffect( true );

						worldObj.setEntityState( this, (byte)7 );
					}
					else
					{
						playTameEffect( false );

						worldObj.setEntityState( this, (byte)6 );
					}
				}

				return true;
			}
		}

		return false;
	}

	private void onEat(Item food)
	{
		heal( food.getWolfHealAmount());

		int iHungerLevel = getHungerLevel();

		if ( iHungerLevel > 0 )
		{
			setHungerLevel(iHungerLevel - 1);
		}

		resetHungerCountdown();

		if ( !worldObj.isRemote )
		{
			worldObj.playAuxSFX( BTWEffectManager.WOLF_EATING_EFFECT_ID,
					MathHelper.floor_double( posX ), (int)( posY + height ),
					MathHelper.floor_double( posZ ), 0 );        	
		}
		//		Hiracho: Added
		if ( food.itemID == BTWItems.rawMysteryMeat.itemID ||food.itemID == BTWItems.cookedMysteryMeat.itemID )
		{
			onEatBreedingItem();
		}
		if ( food.itemID == Item.rottenFlesh.itemID )
		{
			onRottenFleshEaten();
		}
		else if ( food.itemID == BTWItems.chocolate.itemID )
		{
			onChocolateEaten();
		}
	}
	
	@Override
	public void onEatBreedingItem() {
		// wolves sated with fresh steve no longer hold grudges
		setAngry(false);
		
		super.onEatBreedingItem();
	}

	private void onChocolateEaten()
	{
		if ( !worldObj.isRemote )
		{
			addPotionEffect( new PotionEffect( Potion.wither.id, 40 * 20, 0 ) );

			worldObj.setEntityState(this, (byte)11);
		}
	}

	private void onRottenFleshEaten()
	{
		if (infectionCountdown < 0 )
		{
			infectionCountdown = MINIMUM_INFECTION_TIME + rand.nextInt(INFECTION_TIME_VARIANCE);
		}
	}

	public boolean isDarkEnoughToAffectShitting()
	{
		int i = MathHelper.floor_double(posX);
		int j = MathHelper.floor_double(posY);
		int k = MathHelper.floor_double(posZ);

		int lightValue = worldObj.getBlockLightValue( i, j, k );

		if ( lightValue <= 5 )
		{
			return true;
		}

		return false;
	}

	public boolean attemptToShit()
	{
		float poopVectorX = MathHelper.sin((rotationYawHead / 180F) * (float)Math.PI); 

		float poopVectorZ = -MathHelper.cos((rotationYawHead / 180F) * (float)Math.PI); 

		double shitPosX = posX + poopVectorX;
		double shitPosY = posY + 0.25D;
		double shitPosZ = posZ + poopVectorZ;

		int shitPosI = MathHelper.floor_double( shitPosX );
		int shitPosJ = MathHelper.floor_double( shitPosY );
		int shitPosK = MathHelper.floor_double( shitPosZ );

		if ( !isPathToBlockOpenToShitting(shitPosI, shitPosJ, shitPosK) )
		{
			return false;
		}

		EntityItem entityitem = (EntityItem) EntityList.createEntityOfType(EntityItem.class, worldObj, 
				shitPosX, shitPosY, shitPosZ, 
				new ItemStack( BTWItems.dung) );

		float velocityFactor = 0.05F;

		entityitem.motionX = poopVectorX * 10.0f * velocityFactor;

		entityitem.motionZ = poopVectorZ * 10.0f * velocityFactor;

		entityitem.motionY = (float)worldObj.rand.nextGaussian() * velocityFactor + 0.2F;

		entityitem.delayBeforeCanPickup = 10;

		worldObj.spawnEntityInWorld( entityitem );

		worldObj.playSoundAtEntity( this, "random.explode", 0.2F, 1.25F );

		worldObj.playSoundAtEntity(this, "mob.wolf.growl", getSoundVolume(), (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);

		// emit smoke

		for ( int counter = 0; counter < 5; counter++ )
		{
			double smokeX = posX + ( poopVectorX * 0.5f ) + ( worldObj.rand.nextDouble() * 0.25F );
			double smokeY = posY + worldObj.rand.nextDouble() * 0.5F + 0.25F;
			double smokeZ = posZ + ( poopVectorZ * 0.5f ) + ( worldObj.rand.nextDouble() * 0.25F );

			worldObj.spawnParticle( "smoke", smokeX, smokeY, smokeZ, 0.0D, 0.0D, 0.0D );
		}

		return true;
	}

	private boolean isPathToBlockOpenToShitting(int i, int j, int k)
	{
		if ( !isBlockOpenToShitting(i, j, k) )
		{
			return false;
		}

		int wolfI = MathHelper.floor_double( posX );
		int wolfK = MathHelper.floor_double( posZ );

		int deltaI = i - wolfI;
		int deltaK = k - wolfK;

		if ( deltaI != 0 && deltaK != 0 )
		{
			// we're pooping on a diagnal.  Test to make sure that we're not warping poop through blocked off corners

			if (!isBlockOpenToShitting(wolfI, j, k) && !isBlockOpenToShitting(i, j, wolfK) )
			{
				return false;
			}
		}

		return true;
	}

	private boolean isBlockOpenToShitting(int i, int j, int k)
	{
		Block block = Block.blocksList[worldObj.getBlockId( i, j, k )];

		if ( block != null && ( block == Block.waterMoving || block == Block.waterStill || block == Block.lavaMoving || block == Block.lavaStill || block == Block.fire || block.blockMaterial.isReplaceable() ||
				block == BTWBlocks.detectorLogic || block == BTWBlocks.glowingDetectorLogic ||
				block == BTWBlocks.stokedFire) )
		{
			block = null;
		}

		if ( block != null )
		{
			return false;
		}        

		return true;
	}

	private void transformToDire()
	{
		int iFXI = MathHelper.floor_double( posX );
		int iFXJ = MathHelper.floor_double( posY ) + 1;
		int iFXK = MathHelper.floor_double( posZ );

		worldObj.func_82739_e( BTWEffectManager.WOLF_CONVERSION_TO_DIRE_WOLF_EFFECT_ID, iFXI, iFXJ, iFXK, 0 );

		setDead();

		DireWolfEntity direWolf = new DireWolfEntity( worldObj );

		direWolf.setLocationAndAngles( posX, posY, posZ, rotationYaw, rotationPitch );
		direWolf.renderYawOffset = renderYawOffset;

		worldObj.spawnEntityInWorld( direWolf );
	}

	public boolean isWildAndHostile()
	{
		if ( !isTamed() )
		{
			if (isStarving() || isAngry() )
			{
				// wild wolves always hostile when starving

				return true;
			}
			else
			{
				// check if night and full moon

				int iTimeOfDay = (int)( worldObj.worldInfo.getWorldTime() % 24000L );

				if ( iTimeOfDay > 13500 && iTimeOfDay < 22500 )
				{
					int iMoonPhase = worldObj.getMoonPhase();

					if ( iMoonPhase == 0 && worldObj.worldInfo.getWorldTime() > 24000L ) // full moon, and not the first one of the game
					{
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean isWildAndHungry()
	{
		if ( !isTamed() )
		{
			if ( !isFullyFed() )
			{
				return true;
			}
		}

		return false;
	}

	public boolean isWildAndStarving()
	{
		if ( !isTamed() )
		{
			if ( isStarving() )
			{
				return true;
			}
		}

		return false;
	}

	public float getGrazeHeadVerticalOffset(float par1)
	{
		if (howlingCountdown > 0 )
		{
			float fTiltFraction = 1F;

			if (howlingCountdown < 5 )
			{
				fTiltFraction = (float) howlingCountdown / 5F;
			}
			else if (howlingCountdown > WolfHowlBehavior.HOWL_DURATION - 10 )
			{
				fTiltFraction = (float)(WolfHowlBehavior.HOWL_DURATION + 1 - howlingCountdown) / 10F;
			}

			if ( !isSitting() )
			{
				return fTiltFraction * -0.5F;
			}
			else
			{
				return fTiltFraction * -0.25F;
			}
		}

		return 0F;    	
	}

	public float getGrazeHeadRotation(float par1)
	{
		if (howlingCountdown > 0 )
		{
			float fTiltFraction = 1F;

			if (howlingCountdown < 5 )
			{
				fTiltFraction = (float) howlingCountdown / 5F;
			}
			else if (howlingCountdown > WolfHowlBehavior.HOWL_DURATION - 10 )
			{
				fTiltFraction = (float)(WolfHowlBehavior.HOWL_DURATION + 1 - howlingCountdown) / 10F;
			}    			

			return fTiltFraction * -((float)Math.PI / 5F);
		}
		else
		{
			return rotationPitch / (180F / (float)Math.PI);
		}    	
	}

	public boolean areEyesGlowing()
	{
		return isDoingHeadSpin;
	}

	public float getPossessionHeadRotation()
	{
		return possessionHeadRotation * 2F * (float)Math.PI;
	}

	private void updateHeadSpin()
	{
		if (isDoingHeadSpin)
		{
			possessionHeadRotation += 0.008F;

			if (possessionHeadRotation >= 1.0F )
			{
				possessionHeadRotation = 0F;

				isDoingHeadSpin = false;
			}
		}
	}

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public String getTexture()
	{
		if ( isTamed() )
		{
			if ( isStarving() )
			{
				return "/btwmodtex/fcWolf_tame_starving.png";
			}

			return "/mob/wolf_tame.png";
		}
		else if ( isAngry() )
		{
			return "/mob/wolf_angry.png";
		}
		else if ( isStarving() || hasAttackTarget())
		{
			return "/btwmodtex/fcWolf_wild_starving.png"; 
		}

		return texture; // intentionally bypass super method
	}

    @Override
    @Environment(EnvType.CLIENT)
    public void handleHealthUpdate( byte bUpdateType )
	{
		if ( bUpdateType == 10 )
		{
			howlingCountdown = WolfHowlBehavior.HOWL_DURATION;
		}
		else if ( bUpdateType == 11 )
		{
			addPotionEffect( new PotionEffect( Potion.wither.id, 40 * 20, 0 ) );
		}
		else
		{    	
			super.handleHealthUpdate(bUpdateType);
		}
	}

    @Override
    @Environment(EnvType.CLIENT)
    public float getTailRotation()
	{
		if ( isWildAndHostile() )
		{
			return 1.5393804F;
		}
		else if ( isTamed() )
		{
			return ( 0.55F - (float)( 20 - this.dataWatcher.getWatchableObjectInt( 18 ) ) * 
					0.02F ) * (float)Math.PI;
		}

		return (float)Math.PI / 5F;
	}
	
	public boolean onBlockDispenserConsume(BlockDispenserBlock blockDispenser, BlockDispenserTileEntity tileEntity) {
		
        worldObj.playAuxSFX( BTWEffectManager.WOLF_HURT_EFFECT_ID, (int)posX, (int)posY, (int)posZ, 0 );
        setDead();
		
		InventoryUtils.addSingleItemToInventory(tileEntity, BTWBlocks.companionCube.blockID, 0);

		for ( int tempCount = 0; tempCount < 2; tempCount++ )
		{
			blockDispenser.spitOutItem(worldObj, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, new ItemStack(Item.silk ));
		}
		
		return true;
	}
}