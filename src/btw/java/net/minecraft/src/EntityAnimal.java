package net.minecraft.src;

import btw.client.fx.BTWEffectManager;
import btw.entity.mob.SquidEntity;
import btw.item.BTWItems;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

// FCMOD: Added
import java.util.Iterator;
// END FCMOD

public abstract class EntityAnimal extends EntityAgeable implements IAnimals
{
	/**
	 * This is representation of a counter for reproduction progress. (Note that this is different from the inLove which
	 * represent being in Love-Mode)
	 */
	protected int breeding = 0;

	public EntityAnimal(World par1World)
	{
		super(par1World);
	}

	/**
	 * main AI tick function, replaces updateEntityActionState
	 */
	protected void updateAITick()
	{
		if (this.getGrowingAge() != 0)
		{
			resetInLove();
		}

		super.updateAITick();
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		
		// stop grazing while fleeing
		//if (fleeingTick > 0) grazeProgressCounter = 0;
		
		if (this.getGrowingAge() != 0)
		{
			resetInLove();
		}

		if ( isInLove() )
		{
			setInLove( getInLove() - 1 );
			String var1 = "heart";

			if ( getInLove() % 10 == 0 )
			{
				double var2 = this.rand.nextGaussian() * 0.02D;
				double var4 = this.rand.nextGaussian() * 0.02D;
				double var6 = this.rand.nextGaussian() * 0.02D;
				this.worldObj.spawnParticle(var1, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, var2, var4, var6);
			}
		}
		else
		{
			this.breeding = 0;
		}        
	}

	/**
	 * Basic mob attack. Default to touch of death in EntityCreature. Overridden by each mob to define their attack.
	 */
	protected void attackEntity(Entity par1Entity, float par2)
	{
		if (par1Entity instanceof EntityPlayer)
		{
			if (par2 < 3.0F)
			{
				double var3 = par1Entity.posX - this.posX;
				double var5 = par1Entity.posZ - this.posZ;
				this.rotationYaw = (float)(Math.atan2(var5, var3) * 180.0D / Math.PI) - 90.0F;
				this.hasAttacked = true;
			}

			EntityPlayer var7 = (EntityPlayer)par1Entity;

			if (var7.getCurrentEquippedItem() == null || !this.isBreedingItem(var7.getCurrentEquippedItem()))
			{
				this.entityToAttack = null;
			}
		}
		else if (par1Entity instanceof EntityAnimal)
		{
			EntityAnimal var8 = (EntityAnimal)par1Entity;

			if (this.getGrowingAge() > 0 && var8.getGrowingAge() < 0)
			{
				if ((double)par2 < 2.5D)
				{
					this.hasAttacked = true;
				}
			}
			else if ( isInLove() && var8.isInLove() )
			{
				if (var8.entityToAttack == null)
				{
					var8.entityToAttack = this;
				}

				if (var8.entityToAttack == this && (double)par2 < 3.5D)
				{
					setInLove( getInLove() + 1 );
					var8.setInLove( var8.getInLove() + 1 );
					++this.breeding;

					if (this.breeding % 4 == 0)
					{
						this.worldObj.spawnParticle("heart", this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, 0.0D, 0.0D, 0.0D);
					}

					if (this.breeding == 60)
					{
						this.procreate((EntityAnimal)par1Entity);
					}
				}
				else
				{
					this.breeding = 0;
				}
			}
			else
			{
				this.breeding = 0;
				this.entityToAttack = null;
			}
		}
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
	{
		if (this.isEntityInvulnerable())
		{
			return false;
		}
		else
		{
			this.fleeingTick = 60;
			this.entityToAttack = null;
			resetInLove();
			panicNearbyAnimals(par1DamageSource);
			return super.attackEntityFrom(par1DamageSource, par2);
		}
	}

	/**
	 * Finds the closest player within 16 blocks to attack, or null if this Entity isn't interested in attacking
	 * (Animals, Spiders at day, peaceful PigZombies).
	 */
	protected Entity findPlayerToAttack()
	{
		if (this.fleeingTick > 0)
		{
			return null;
		}
		else
		{
			float var1 = 8.0F;
			List var2;
			int var3;
			EntityAnimal var4;

			if ( isInLove() )
			{
				double dClosestAnimalDistanceSq = 0.0F;
				EntityAnimal closestValidAnimal = null;

				List list = worldObj.getEntitiesWithinAABB(getClass(), boundingBox.expand( var1, var1, var1 ));

				for (int i = 0; i < list.size(); i++)
				{
					EntityAnimal entityanimal = (EntityAnimal)list.get(i);

					if (entityanimal != this && entityanimal.isInLove() )
					{
						double dDistanceSqToAnimal = getDistanceSqToEntity( entityanimal );

						if ( closestValidAnimal == null || dDistanceSqToAnimal < dClosestAnimalDistanceSq )
						{
							dClosestAnimalDistanceSq = dDistanceSqToAnimal;
							closestValidAnimal = entityanimal;
						}
					}
				}

				return closestValidAnimal;
				// END FCMOD
			}
			else if (this.getGrowingAge() == 0)
			{
				var2 = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.boundingBox.expand((double)var1, (double)var1, (double)var1));

				for (var3 = 0; var3 < var2.size(); ++var3)
				{
					EntityPlayer var5 = (EntityPlayer)var2.get(var3);

					if (var5.getCurrentEquippedItem() != null && this.isBreedingItem(var5.getCurrentEquippedItem()))
					{
						return var5;
					}
				}
			}
			else if (this.getGrowingAge() > 0)
			{
				var2 = this.worldObj.getEntitiesWithinAABB(this.getClass(), this.boundingBox.expand((double)var1, (double)var1, (double)var1));

				for (var3 = 0; var3 < var2.size(); ++var3)
				{
					var4 = (EntityAnimal)var2.get(var3);

					if (var4 != this && var4.getGrowingAge() < 0)
					{
						return var4;
					}
				}
			}

			return null;
		}
	}

	/**
	 * Checks if the entity's current position is a valid location to spawn this entity.
	 */
	public boolean getCanSpawnHere()
	{
		int var1 = MathHelper.floor_double(this.posX);
		int var2 = MathHelper.floor_double(this.boundingBox.minY);
		int var3 = MathHelper.floor_double(this.posZ);
		return this.worldObj.getBlockId(var1, var2 - 1, var3) == Block.grass.blockID && this.worldObj.getFullBlockLightValue(var1, var2, var3) > 8 && super.getCanSpawnHere();
	}

	/**
	 * Get number of ticks, at least during which the living entity will be silent.
	 */
	public int getTalkInterval()
	{
		return 120;
	}

	/**
	 * Determines if an entity can be despawned, used on idle far away entities
	 */
	protected boolean canDespawn()
	{
		return false;
	}

	/**
	 * Get the experience points the entity currently has.
	 */
	protected int getExperiencePoints(EntityPlayer par1EntityPlayer)
	{
		return 1 + this.worldObj.rand.nextInt(3);
	}

	/**
	 * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
	 * the animal type)
	 */
	public boolean isBreedingItem(ItemStack par1ItemStack)
	{
		return par1ItemStack.itemID == Item.wheat.itemID;
	}

	/**
	 * Returns if the entity is currently in 'love mode'.
	 */
	public boolean isInLove()
	{
		return getInLove() > 0;
	}

	public void resetInLove()
	{
		setInLove( 0 );
	}

	/**
	 * Returns true if the mob is currently able to mate with the specified mob.
	 */
	public boolean canMateWith(EntityAnimal par1EntityAnimal)
	{
		return par1EntityAnimal == this ? false : (par1EntityAnimal.getClass() != this.getClass() ? false : this.isInLove() && par1EntityAnimal.isInLove());
	}

	// FCMOD: Added New
	protected static final int HUNGER_LEVEL_DATA_WATCHER_ID = 21;
	protected static final int IN_LOVE_DATA_WATCHER_ID = 22;
	protected static final int WEARING_BREEDING_HARNESS_DATA_WATCHER_ID = 23;

	@Override
	protected void entityInit()
	{
		super.entityInit();

		dataWatcher.addObject(HUNGER_LEVEL_DATA_WATCHER_ID, new Byte((byte)0 ));
		dataWatcher.addObject(IN_LOVE_DATA_WATCHER_ID, new Integer(0 ));
		dataWatcher.addObject(WEARING_BREEDING_HARNESS_DATA_WATCHER_ID, Byte.valueOf((byte)0));
	}

	@Override
	public void writeEntityToNBT( NBTTagCompound tag )
	{
		super.writeEntityToNBT( tag );

		tag.setInteger( "InLove", getInLove() );
		tag.setByte( "fcHungerLvl", (byte) getHungerLevel());
		tag.setBoolean( "BreedingHarness", getWearingBreedingHarness() );
		tag.setInteger("fcHungerCnt", hungerCountdown);
		tag.setInteger("fcHealCnt", healingCountdown);
	}

	@Override
	public void readEntityFromNBT( NBTTagCompound tag )
	{
		super.readEntityFromNBT( tag );

		setInLove( tag.getInteger( "InLove" ) );

		if ( tag.hasKey( "BreedingHarness" ) )
		{
			setWearingBreedingHarness( tag.getBoolean( "BreedingHarness" ) );
		}

		if ( tag.hasKey( "fcHungerLvl" ) )
		{
			setHungerLevel(tag.getByte("fcHungerLvl"));
		}

		if ( tag.hasKey( "fcHungerCnt" ) )
		{
			hungerCountdown = tag.getInteger("fcHungerCnt");
		}
		else
		{
			resetHungerCountdown();
		}

		if ( tag.hasKey( "fcHealCnt" ) )
		{
			healingCountdown = tag.getInteger("fcHealCnt");
		}
		else
		{
			resetHealingCountdown();
		}
	}

	@Override
	protected void modSpecificOnLivingUpdate()
	{
		super.modSpecificOnLivingUpdate();

		if ( !worldObj.isRemote )
		{
			if ( isEntityAlive() )
			{
				checkForLooseFood();
				checkForIntersectingBreedingHarnesses();

				updateHealing();
				updateHungerState();
			}
		}
		else
		{
			if (grazeProgressCounter > 0 )
			{
				// update the graze counter on the client as we only get an initial 
				// notification when it starts in a healthUpdate
				grazeProgressCounter--;
			}
			// update fleeing state client-side
			if (fleeingTick > 0) fleeingTick--;
		}

		if ( isInLove() )
		{
			if ( entityToAttack != null && entityToAttack instanceof EntityAnimal )
			{
				EntityAnimal entityanimal = (EntityAnimal)entityToAttack;

				if ( !entityanimal.isInLove() )
				{
					// the targeted animal is no longer in love mode.  Reset the target so another will be selected on the following update

					entityToAttack = null;
				}
			}
		}
	}

	@Override
	public void jump()
	{
		if ( isChild() )
		{
			// jump half height if child or starving

			motionY = 0.21D;
			isAirBorne = true;    		
		}
		else
		{
			super.jump();
		}
	}

	@Override
	public void onDeath( DamageSource damageSource )
	{
		super.onDeath( damageSource );

		if ( !worldObj.isRemote && getWearingBreedingHarness() )
		{
			dropItem( BTWItems.breedingHarness.itemID, 1 );
		}        
	}

	@Override
	protected void updateEntityActionState()
	{
		super.updateEntityActionState();

		if ( getWearingBreedingHarness() )
		{
			moveStrafing = 0F;
			moveForward = 0F;
		}
	}

	@Override
	public void checkForScrollDrop()
	{
		// this is overridden to prevent potentially expensive (and useless) checks every 
		// time an animal dies, since none of them drop scrolls
	}

	@Override
	protected float getSoundPitch()
	{
		float fPitch = super.getSoundPitch();

		if ( isPossessed() )
		{
			fPitch *= 0.60F;
		}

		return fPitch;
	}

	@Override
	public void setRevengeTarget( EntityLiving targetEntity )
	{
		// override to lengthen panic time on animals

		entityLivingToAttack = targetEntity;

		if ( entityLivingToAttack != null )
		{
			revengeTimer = 300; // 15 seconds
		}
		else
		{
			revengeTimer = 0;
		}
	}

	@Override
	public float getBlockPathWeight( int i, int j, int k )
	{
		if (canGrazeOnBlock(i, j - 1, k) || canGrazeOnBlock(i, j, k) )
		{
			return 10F;
		}

		return worldObj.getNaturalLightBrightness(i, j, k) - 0.5F;
	}

	@Override
	public boolean isSecondaryTargetForSquid()
	{
		return true;
	}

	@Override
	public void onFlungBySquidTentacle(SquidEntity squid)
	{
		DamageSource squidSource = DamageSource.causeMobDamage( squid );

		attackEntityFrom( squidSource, 0 );
	}

	@Override
	public void onHeadCrabbedBySquid(SquidEntity squid)
	{
		DamageSource squidSource = DamageSource.causeMobDamage( squid );

		attackEntityFrom( squidSource, 0 );
	}

	@Override
	protected void attemptToPossessNearbyCreatureOnDeath()
	{
		attemptToPossessNearbyCreature(16D, true);
	}

	@Override
	public float getSpeedModifier()
	{
		return super.getSpeedModifier() * getHungerSpeedModifier();
	}

	@Override
	public boolean canChildGrow()
	{
		return super.canChildGrow() && !isTooHungryToGrow();
	}

	@Override
	public boolean canLoveJuiceRegenerate()
	{
		return isFullyFed();
	}

	@Override
	public int getTicksForChildToGrow()
	{
		return MiscUtils.TICKS_PER_GAME_DAY * 2;
	}

	@Override
	public boolean interact( EntityPlayer player )
	{
		return entityAnimalInteract(player);
	}

	@Override
	public void initCreature() 
	{
		initHungerWithVariance();
	}

	//------------- Class Specific Methods ------------//

	public int getInLove()
	{
		return dataWatcher.getWatchableObjectInt(IN_LOVE_DATA_WATCHER_ID);
	}

	public void setInLove( int iInLove )
	{
		dataWatcher.updateObject(IN_LOVE_DATA_WATCHER_ID, Integer.valueOf(iInLove));
	}

	public boolean getWearingBreedingHarness()
	{
		return (dataWatcher.getWatchableObjectByte(WEARING_BREEDING_HARNESS_DATA_WATCHER_ID) > 0 );
	}

	public void setWearingBreedingHarness( boolean bWearingHarness )
	{
		Byte wearing = 0;

		if ( bWearingHarness )
		{
			wearing = 1;
		}

		dataWatcher.updateObject(WEARING_BREEDING_HARNESS_DATA_WATCHER_ID, Byte.valueOf(wearing));
	}

	public void checkForIntersectingBreedingHarnesses()
	{    
		if ( getWearingBreedingHarness()  )
		{
			AxisAlignedBB tempBoundingBox = boundingBox.copy();

			tempBoundingBox.contract(0.1D, 0.1D, 0.1D );

			List collisionList = worldObj.getEntitiesWithinAABB( EntityAnimal.class, 
					tempBoundingBox );

			if ( !collisionList.isEmpty() )
			{
				for ( int listIndex = 0; listIndex < collisionList.size(); listIndex++ )
				{
					EntityAnimal entityAnimal = (EntityAnimal)collisionList.get( listIndex );;

					if ( entityAnimal != this )
					{		    		
						if ( entityAnimal.getWearingBreedingHarness() && !(entityAnimal.isLivingDead) )
						{
							attackEntityFrom( DamageSource.inWall, 1 );

							break;				            
						}
					}
				}
			}
		}
	}

	public void panicNearbyAnimals(DamageSource damageSource)
	{
		Entity attackingEntity = damageSource.getEntity();

		if ( attackingEntity != null && ( attackingEntity instanceof EntityLiving ) )
		{
			EntityLiving attackingEntityLiving = (EntityLiving)attackingEntity;

			List animalList = worldObj.getEntitiesWithinAABB( EntityAnimal.class, boundingBox.expand( 16D, 8D, 16D ) );

			Iterator itemIterator = animalList.iterator();

			while ( itemIterator.hasNext())
			{
				EntityAnimal tempAnimal = (EntityAnimal)itemIterator.next();

				if ( !(tempAnimal.isLivingDead) && tempAnimal != this && tempAnimal != attackingEntityLiving )
				{
					tempAnimal.onNearbyAnimalAttacked(this, attackingEntityLiving);
				}

			}
		}
	}

	public void onNearbyAnimalAttacked(EntityAnimal attackedAnimal, EntityLiving attackSource)
	{
		// only freak the animal out if it isn't already freaked by something else

		if ( entityLivingToAttack == null )
		{
			entityLivingToAttack = attackSource;	        
			revengeTimer = 150; // 7.5 seconds
		}
		else if ( revengeTimer < 150 )
		{
			revengeTimer = 150;
		}
	}

	public void onNearbyFireStartAttempt(EntityPlayer player)
	{
		onNearbyPlayerStartles(player);
	}

	public void onNearbyPlayerBlockAddOrRemove(EntityPlayer player)
	{
		onNearbyPlayerStartles(player);
	}

	protected void onNearbyPlayerStartles(EntityPlayer player)
	{
		// only freak the animal out if it isn't already freaked by something else

		if ( entityLivingToAttack == null )
		{
			entityLivingToAttack = player;	        
			revengeTimer = 150; // 7.5 seconds
		}
		else if ( revengeTimer < 150 )
		{
			revengeTimer = 150;
		}
	}

	protected void procreate( EntityAnimal targetMate )
	{
		// Replacement for vanilla function above

		double dChildX = posX;
		double dChildY = posY;
		double dChildZ = posZ;

		if ( getWearingBreedingHarness() )
		{
			dChildX = ( posX + targetMate.posX ) / 2.0F;
			dChildY = ( posY + targetMate.posY ) / 2.0F;
			dChildZ = ( posZ + targetMate.posZ ) / 2.0F;                	
		}

		giveBirthAtTargetLocation(targetMate, dChildX, dChildY, dChildZ);

		resetMatingStateOfBothParents(targetMate);

		spawnBirthHeartParticles();

		worldObj.playSoundAtEntity( this, getDeathSound(), getSoundVolume(), (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F );

		worldObj.playAuxSFX( BTWEffectManager.ANIMAL_BIRTHING_EFFECT_ID,
				MathHelper.floor_double( dChildX ), MathHelper.floor_double( dChildY ), MathHelper.floor_double( dChildZ ), 
				0 );            
	}

	public int getNestSize()
	{
		return 1;
	}

	protected void giveBirthAtTargetLocation(EntityAnimal targetMate, double dChildX, double dChildY, double dChildZ)
	{
		int nestSize = getNestSize();
		
		for (int nestTempCount = 0; nestTempCount < nestSize; ++nestTempCount)
		{
			EntityAgeable childEntity = createChild( targetMate );

			if ( childEntity != null )
			{
				childEntity.setGrowingAge( -getTicksForChildToGrow());

				childEntity.setLocationAndAngles( dChildX, dChildY, dChildZ, rotationYaw, rotationPitch );

				worldObj.spawnEntityInWorld( childEntity );            
			}
		}
	}

	protected void resetMatingStateOfBothParents(EntityAnimal targetMate)
	{
		setGrowingAge(getTicksToRegenerateLoveJuice());
		targetMate.setGrowingAge( targetMate.getTicksToRegenerateLoveJuice());

		resetInLove();            
		breeding = 0;
		entityToAttack = null;

		targetMate.resetInLove();            
		targetMate.entityToAttack = null;
		targetMate.breeding = 0;        
	}

	protected void spawnBirthHeartParticles()
	{
		for ( int iTempCount = 0; iTempCount < 7; ++iTempCount )
		{
			double dParticleVelX = this.rand.nextGaussian() * 0.02D;
			double dParticleVelY = this.rand.nextGaussian() * 0.02D;
			double dParticleVelZ = this.rand.nextGaussian() * 0.02D;

			worldObj.spawnParticle( "heart", 
					this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, 
					this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), 
					this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, 
					dParticleVelX, dParticleVelY, dParticleVelZ);
		}
	}

	//-------------- Hunger related functionality ------------//

	public static final int FULL_HUNGER_COUNT = ( MiscUtils.TICKS_PER_GAME_DAY);
	public static final int LEVEL_UP_HUNGER_COUNT = (FULL_HUNGER_COUNT +
													 (FULL_HUNGER_COUNT / 16 ) );

	public static final int MAX_HEALING_COUNT = ( MiscUtils.TICKS_PER_GAME_DAY);

	public int hungerCountdown = FULL_HUNGER_COUNT;
	public int healingCountdown = FULL_HUNGER_COUNT;

	public static final int BASE_GRAZE_FOOD_VALUE = 200;

	public static final int m_iDelayBetweenEatLoose = (MiscUtils.TICKS_PER_SECOND / 2 );

	public static final int DELAY_BETWEEN_EAT_LOOSE_VARIANCE =
			(MiscUtils.TICKS_PER_SECOND / 2 );

	public int eatLooseCooldownCounter = (m_iDelayBetweenEatLoose + DELAY_BETWEEN_EAT_LOOSE_VARIANCE);

	public int grazeProgressCounter = 0;

	public void initHungerWithVariance()
	{
		// prevent initially spawned animals from all eating at the same time.

		if ( isSubjectToHunger() )
		{
			hungerCountdown = FULL_HUNGER_COUNT - rand.nextInt(getGrazeHungerGain());
		}
	}

	public int getHungerLevel()
	{
		return dataWatcher.getWatchableObjectByte(HUNGER_LEVEL_DATA_WATCHER_ID);
	}

	public void setHungerLevel(int iHungerLevel)
	{
		dataWatcher.updateObject(HUNGER_LEVEL_DATA_WATCHER_ID, Byte.valueOf((byte)iHungerLevel));
	}

	public boolean isFullyFed()
	{
		return getHungerLevel() == 0;
	}

	public boolean isFamished()
	{
		return getHungerLevel() == 1;
	}

	public boolean isStarving()
	{
		return getHungerLevel() >= 2;
	}

	public void onBecomeFamished()
	{
		setHungerLevel(1);
	}

	public void onBecomeStarving()
	{
		setHungerLevel(2);
	}

	public void onStarvingCountExpired()
	{
		// max health 20 wolves, 15 cows, 10 pigs, 8 sheep, 4 chicken
		if (this.worldObj.getDifficulty().canAnimalsStarve()) {
			attackEntityFrom(DamageSource.starve, 5);
		}
	}

	public boolean isSubjectToHunger()
	{
		return false;
	}

	public void updateHungerState()
	{
		if ( isSubjectToHunger() )
		{
			if ( !isChild() )
			{
				hungerCountdown--;
			}
			else    			
			{
				// children burn more energy

				hungerCountdown -= 2;
			}

			if (hungerCountdown <= 0 )
			{
				if ( !isChild() )
				{
					if ( isFullyFed() )
					{
						onBecomeFamished();
					}
					else if ( isFamished() )
					{
						onBecomeStarving();
					}
					else // starving
					{
						onStarvingCountExpired();
					}

					resetHungerCountdown();
				}
				else 
				{ 
					// children can not survive being famished.  They'll
					// just keep taking damage once their countdown expires

					attackEntityFrom( DamageSource.starve, 1 );
				}		
			}
		}
	}

	public void resetHungerCountdown()
	{
		hungerCountdown = FULL_HUNGER_COUNT;
	}

	public void addToHungerCount(int iAddedHunger)
	{
		hungerCountdown += iAddedHunger;

		// don't level up immediately when full to prevent flickering state

		if (hungerCountdown > LEVEL_UP_HUNGER_COUNT)
		{
			int iHungerLevel = getHungerLevel();

			if ( iHungerLevel > 0 )
			{
				hungerCountdown -= FULL_HUNGER_COUNT;

				setHungerLevel(iHungerLevel - 1);
			}
		}    	
	}

	public int getGrazeHungerGain()
	{
		return BASE_GRAZE_FOOD_VALUE * getFoodValueMultiplier();
	}

	public int getFoodValueMultiplier()
	{
		return 2;
	}

	public void onGrazeBlock(int i, int j, int k)
	{
		addToHungerCount(getGrazeHungerGain());
	}

	public boolean shouldNotifyBlockOnGraze()
	{
		return true;
	}

	public void playGrazeFX(int i, int j, int k, int iBlockID)
	{
		worldObj.playAuxSFX( 2001, i, j, k, iBlockID ); 
	}

	public int getGrazeDuration()
	{
		return 40;
	}

	public boolean isHungryEnoughToGraze()
	{
		return !isFullyFed() || hungerCountdown + getGrazeHungerGain() <= FULL_HUNGER_COUNT;
	}

	public boolean isHungryEnoughToForceMoveToGraze()
	{
		return isChild() || !isFullyFed() || hungerCountdown < FULL_HUNGER_COUNT / 2;
	}

	public boolean isTooHungryToGrow()
	{
		return !isFullyFed() || hungerCountdown < (FULL_HUNGER_COUNT * 3 ) / 4;
	}

	public boolean isTooHungryToHeal()
	{
		return !isFullyFed() || hungerCountdown < (FULL_HUNGER_COUNT * 3 ) / 4;
	}

	public boolean canGrazeMycelium()
	{
		return false;
	}

	public boolean getDisruptsEarthOnGraze()
	{
		return false;
	}

	public boolean canGrazeOnRoughVegetation()
	{
		return false;
	}

	/**
	 * Returns null if no valid graze block exists at location
	 */    
	public BlockPos getGrazeBlockForPos()
	{
		BlockPos targetPos = new BlockPos( MathHelper.floor_double( posX ),
				(int)boundingBox.minY, MathHelper.floor_double( posZ ) );

		if ( canGrazeOnBlock(targetPos.x, targetPos.y, targetPos.z) )
		{
			return targetPos;
		}
		else
		{
			targetPos.y--;

			if ( canGrazeOnBlock(targetPos.x, targetPos.y, targetPos.z) )
			{
				return targetPos;
			}
		}

		return null;
	}

	public boolean shouldStayInPlaceToGraze()
	{
		return getGrazeBlockForPos() != null;
	}

	public boolean canGrazeOnBlock(int i, int j, int k)
	{
		Block block = Block.blocksList[worldObj.getBlockId( i, j, k )];

		if ( block != null )
		{
			return block.canBeGrazedOn(worldObj, i, j, k, this);
		}

		return false;
	}

	public float getHungerSpeedModifier()
	{
		if ( isStarving() )
		{
			return 0.5F;
		}
		else if ( isFamished() )
		{
			return 0.75F;
		}

		return 1F;
	}

	public boolean isTemptingItem(ItemStack stack)
	{
		return getItemFoodValue(stack) > 0 ||
			   (isBreedingItem( stack ) && isReadyToEatBreedingItem() );
	}

	public boolean isEdibleItem(ItemStack stack)
	{
		return isBreedingItem( stack ) || getItemFoodValue(stack) > 0;
	}

	public boolean isHungryEnoughToEatLooseFood()
	{
		return !isFullyFed() || hungerCountdown <= FULL_HUNGER_COUNT;
	}

	public boolean isReadyToEatBreedingItem()
	{
		return isFullyFed() && getGrowingAge() == 0 && !isInLove();
	}

	public int getItemFoodValue(ItemStack stack)
	{
		return stack.getItem().getHerbivoreFoodValue(stack.getItemDamage()) *
			   getFoodValueMultiplier();
	}

	public boolean attemptToEatItemForBreeding(ItemStack stack)
	{
		if (isBreedingItem( stack ) && isReadyToEatBreedingItem() )
		{
			onEatBreedingItem();

			return true;
		}

		return false;
	}

	public void onEatBreedingItem()
	{
		setInLove( 600 );
		entityToAttack = null;

		for( int iTempCount = 0; iTempCount < 7; iTempCount++ )
		{
			worldObj.spawnParticle( "heart", 
					posX + (double)( ( rand.nextFloat() * width * 2F ) - width ), 
					posY + 0.5D + (double)( rand.nextFloat() * height ), 
					posZ + (double)( ( rand.nextFloat() * width * 2.0F ) - width ), 
					rand.nextGaussian() * 0.02D, rand.nextGaussian() * 0.02D, 
					rand.nextGaussian() * 0.02D ); // last 3 are velocity
		}
	}

	public boolean attemptToEatItem(ItemStack stack)
	{
		int iFoodValue = getItemFoodValue(stack);

		if (attemptToEatItemForBreeding(stack) || (iFoodValue > 0 &&
												   isHungryEnoughToEatLooseFood() ) )
		{
			addToHungerCount(iFoodValue);

			worldObj.setEntityState( this, (byte)10 ); // trigger eating anim on client

			worldObj.playAuxSFX( BTWEffectManager.ANIMAL_EATING_EFFECT_ID,
					MathHelper.floor_double( posX ), (int)( posY + height ),
					MathHelper.floor_double( posZ ), 0 );

			return true;
		}

		return false;
	}

	public boolean attemptToBeHandFedItem(ItemStack stack)
	{
		return attemptToEatItem(stack);
	}

	public boolean attemptToEatLooseItem(ItemStack stack)
	{
		return attemptToEatItem(stack);
	}

	public boolean isReadyToEatLooseFood()
	{
		return isHungryEnoughToEatLooseFood() || isReadyToEatBreedingItem();
	}

	public boolean isReadyToEatLooseItem(ItemStack stack)
	{
		return (getItemFoodValue(stack) > 0 && isHungryEnoughToEatLooseFood() ) ||
			   (isBreedingItem( stack ) && isReadyToEatBreedingItem() );
	}

	public void checkForLooseFood()
	{    
		if (eatLooseCooldownCounter > 0 )
		{
			eatLooseCooldownCounter--;
		}
		else if ( isReadyToEatLooseFood() )
		{
			List<EntityItem> entityList = worldObj.getEntitiesWithinAABB( 
					EntityItem.class, AxisAlignedBB.getAABBPool().getAABB( 
							boundingBox.minX - 1.5F, boundingBox.minY - 1F, boundingBox.minZ - 1.5F, 
							boundingBox.maxX + 1.5F, boundingBox.maxY + 1F, boundingBox.maxZ + 1.5F ) );

			if ( !entityList.isEmpty() )
			{
				Iterator<EntityItem> entityIterator = entityList.iterator();

				while ( entityIterator.hasNext() )
				{
					EntityItem tempEntity = entityIterator.next();

					if ( tempEntity.delayBeforeCanPickup == 0 && tempEntity.isEntityAlive() )
					{
						ItemStack tempStack = tempEntity.getEntityItem();

						if ( attemptToEatLooseItem(tempEntity.getEntityItem()) )
						{
							tempStack.stackSize--;

							if ( tempStack.stackSize <= 0 )
							{
								tempEntity.setDead();					            
							}
							else
							{
								// slight delay on pickup so animals don't all eat from a stack
								// at once

								tempEntity.delayBeforeCanPickup = 2; 			                	
							}

							eatLooseCooldownCounter = m_iDelayBetweenEatLoose +
													  rand.nextInt(DELAY_BETWEEN_EAT_LOOSE_VARIANCE + 1);

							break;				            
						}			        		
					}
				}
			}
		}
	}

	public boolean entityAnimalInteract(EntityPlayer player)
	{
		ItemStack heldItem = player.inventory.getCurrentItem();

		if ( heldItem != null )
		{
			if ( isEdibleItem(heldItem) )
			{
				if (!worldObj.isRemote && attemptToBeHandFedItem(heldItem) )
				{
					heldItem.stackSize--;

					if ( heldItem.stackSize <= 0 )
					{
						player.inventory.setInventorySlotContents( player.inventory.currentItem, 
								null );
					}
				}

				return true;
			}
		}

		return super.interact( player );
	}

	public int getTicksToRegenerateLoveJuice()
	{
		return MiscUtils.TICKS_PER_GAME_DAY;
	}

	public float getGrazeHeadVerticalOffset(float fPartialTick)
	{
		int iGrazeDuration = getGrazeDuration();

		if (getWearingBreedingHarness() || grazeProgressCounter <= 0 )
		{
			return 0F;
		}
		else if (grazeProgressCounter >= 4 && grazeProgressCounter <= iGrazeDuration - 4 )
		{
			return 1F;
		}
		else if (grazeProgressCounter < 4 )
		{
			return ((float) grazeProgressCounter - fPartialTick ) / 4F;
		}
		else
		{
			return -((float)(grazeProgressCounter - iGrazeDuration ) - fPartialTick ) / 4F;
		}    	
	}

	public float getGrazeHeadRotation(float fPartialTick)
	{
		int iGrazeDuration = getGrazeDuration();

		if (getWearingBreedingHarness() || grazeProgressCounter <= 0 )
		{
			return rotationPitch / ( 180F / (float)Math.PI );
		}
		else if (grazeProgressCounter > 4 && grazeProgressCounter <= iGrazeDuration - 4 )
		{
			float fProgress = ((float)(grazeProgressCounter - 4 ) - fPartialTick ) /
							  (float)( iGrazeDuration - 8 );

			return ((float)Math.PI / getGrazeHeadRotationMagnitudeDivisor() ) +
					( (float)Math.PI * 7F / 100F ) * 
					MathHelper.sin(fProgress * getGrazeHeadRotationRateMultiplier());
		}
		else
		{
			return (float)Math.PI / getGrazeHeadRotationMagnitudeDivisor();
		}
	}

	public float getGrazeHeadRotationMagnitudeDivisor()
	{
		return 5F;
	}

	public float getGrazeHeadRotationRateMultiplier()
	{
		return 28.7F;
	}

	public void updateHealing()
	{
		if (isSubjectToHunger() && !isChild() )
		{
			if ( isTooHungryToHeal() )
			{
				resetHealingCountdown();
			}
			else
			{
				healingCountdown--;

				if (healingCountdown <= 0 )
				{
					heal( 1 );

					resetHealingCountdown();
				}
			}
		}
	}

	public void resetHealingCountdown()
	{
		healingCountdown = MAX_HEALING_COUNT;
	}    

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void handleHealthUpdate( byte bUpdateType )
	{
		if ( bUpdateType == 10 )
		{
			grazeProgressCounter = getGrazeDuration();
		}
		else
		{
			super.handleHealthUpdate( bUpdateType );
		}
	}
	// END FCMOD
}
