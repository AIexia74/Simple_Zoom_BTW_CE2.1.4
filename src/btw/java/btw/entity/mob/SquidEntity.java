// FCMOD

package btw.entity.mob;

import btw.client.fx.BTWEffectManager;
import btw.inventory.util.InventoryUtils;
import btw.entity.util.ClosestEntitySelectionCriteria;
import btw.item.BTWItems;
import btw.network.packet.BTWPacketManager;
import btw.client.network.packet.handler.EntityEventPacketHandler;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Iterator;
import java.util.List;

public class SquidEntity extends EntityWaterMob
{
    public static final float BRIGHTNESS_AGGRESSION_THRESHOLD = 0.1F;
    
	private static final float SAFE_ATTACK_DEPTH = 0.5F;
	private static final int SAFE_ATTACK_DEPTH_TEST_MAXIMUM = ((int) SAFE_ATTACK_DEPTH + 1 );
	
	private static final float SAFE_PASSIVE_DEPTH = 3F;
	private static final int SAFE_PASSIVE_DEPTH_TEST_MAXIMUM = ((int) SAFE_PASSIVE_DEPTH + 1 );
	
	private static final double AGGRESSION_RANGE = 16D;
	private static final int CHANCE_OF_LOSING_ATTACK_TARGET_IN_LIGHT = 400;
	
	private static final int TENTACLE_ATTACK_TICKS_TO_COOLDOWN = 100;
	private static final double TENTACLE_ATTACK_RANGE = 6D;
    public static final int TENTACLE_ATTACK_DURATION = 20;
	private static final double TENTACLE_ATTACK_TIP_COLLISION_WIDTH = 0.2D;
	private static final double TENTACLE_ATTACK_TIP_COLLISION_HALF_WIDTH = (TENTACLE_ATTACK_TIP_COLLISION_WIDTH / 2D );
    
    private int tentacleAttackCooldownTimer = TENTACLE_ATTACK_TICKS_TO_COOLDOWN;
    
    public int tentacleAttackInProgressCounter = -1;
    private double tentacleAttackTargetX = 0D;
    private double tentacleAttackTargetY = 0D;
    private double tentacleAttackTargetZ = 0D;
    
    private static final int HEAD_CRAB_DAMAGE_INITIAL_DELAY = 40;
    private static final int HEAD_CRAB_DAMAGE_PERIOD = 40;
    
    private int headCrabDamageCounter = HEAD_CRAB_DAMAGE_INITIAL_DELAY;
    
    public float squidPitch = 0F;
    public float prevSquidPitch = 0F;
    
    public float squidYaw = 0F;
    public float prevSquidYaw = 0F;
    private float squidYawSpeed = 0F;
    
    public float tentacleAngle = 0F;
    public float prevTentacleAngle = 0F;
    private float tentacleAnimProgress = 0F;
    private float prevTentacleAnimProgress = 0F;
    private float tentacleAnimSpeed = 0F;
    
    private float randomMotionSpeed = 0F;
    private float randomMotionVecX = 0F;
    private float randomMotionVecY = 0F;
    private float randomMotionVecZ = 0F;
    
    private Entity entityToNotReCrab = null; // tracking variable to prevent squid immediately reattaching when knocked off
    private int reCrabEntityCountdown = 0;
    private static final int RE_CRAB_ENTITY_TICKS = 5;
    
	private static final float POSSESSED_LEAP_DEPTH = 0.5F;
	
    private static final int POSSESSED_LEAP_COUNTDOWN_DURATION = (10 * 20 );
    private static final int POSSESSED_LEAP_PROPULSION_DURATION = 10;
    
    private int possessedLeapCountdown = 0;
    private int possessedLeapPropulsionCountdown = 0;
    
    private final float possessedLeapGhastConversionChance = 0.25F;
    private float possessedLeapGhastConversionDiceRoll = 1F;
    
    private static final int SQUID_POSSESSION_MAX_COUNT = 50;
    
    public SquidEntity(World world )
    {
        super( world );
        
        texture = "/mob/squid.png";
        
        setSize( 0.95F, 0.95F );

        tentacleAnimSpeed = 1F / (rand.nextFloat() + 1F ) * 0.2F;
    }
    
    @Override
    public int getMaxHealth()
    {
    	return 20;
    }
    
    @Override
    protected String getLivingSound()
    {
        return null;
    }
    
    @Override
    protected String getHurtSound()
    {
        return null;
    }
    
    protected String getDeathSound()
    {
        return null;
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.4F;
    }

    @Override
    protected int getDropItemId()
    {
        return 0;
    }
    
    @Override
    protected void dropFewItems( boolean bKilledByPlayer, int iLootingModifier )
    {
        int iNumInkSacks = this.rand.nextInt( 3 + iLootingModifier ) + 1;

        for ( int iTempInkSack = 0; iTempInkSack < iNumInkSacks; ++iTempInkSack )
        {
            entityDropItem( new ItemStack( Item.dyePowder, 1, 0 ), 0F );
        }
        
        if ( rand.nextInt( 8 ) - iLootingModifier <= 0 )
        {
            dropItem( BTWItems.mysteriousGland.itemID, 1 );
        }
    }
    
    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        prevSquidPitch = squidPitch;
        prevSquidYaw = squidYaw;

        prevTentacleAnimProgress = tentacleAnimProgress;
        prevTentacleAngle = tentacleAngle;
        
        updateTentacleAttack();

        if ( !isEntityAlive() )
        {
        	// dead squids tell no tales 
        	
        	if ( !worldObj.isRemote )
        	{
                this.motionX = 0.0D;
                if (this.isInWater())
                {
                	this.motionY -= 0.02D;
                	this.motionY *= 0.8D;
                }
                else
                {
                	this.motionY -= 0.08D;
                	this.motionY *= 0.9800000190734863D;
                }
                this.motionZ = 0.0D;
        	}
            
        	return;
        }

        tentacleAnimProgress += this.tentacleAnimSpeed;
        
        if (this.tentacleAnimProgress > ((float)Math.PI * 2F))
        {
            this.tentacleAnimProgress -= ((float)Math.PI * 2F);

            if (this.rand.nextInt(10) == 0)
            {
                this.tentacleAnimSpeed = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
            }
        }
        
    	if ( ridingEntity != null && !ridingEntity.isEntityAlive() )
		{
    		mountEntity( null );
    		
    		if ( !worldObj.isRemote )
    		{
	        	worldObj.playAuxSFX( BTWEffectManager.BURP_SOUND_EFFECT_ID,
	        		MathHelper.floor_double( posX ), MathHelper.floor_double( posY ), MathHelper.floor_double( posZ ), 0 );
    		}
		}
        		
        if ( !inWater && getAir() % 100 == 0 )
        {
	        if (isPossessed() || isHeadCrab() || isBeingRainedOn() )
	        {
	    	    setAir( 300 ); // don't suffocate out of water
	        }
        }
        	
        if ( isHeadCrab() )
        {
        	updateHeadCrab();
        	
            return;
        }
        
        if (this.isInWater()) // FCTODO: This test is likely what's screwing up client/server motion as it calls material acceleration
        {
            float var1;

            if (this.tentacleAnimProgress < (float)Math.PI)
            {
                var1 = this.tentacleAnimProgress / (float)Math.PI;
                this.tentacleAngle = MathHelper.sin(var1 * var1 * (float)Math.PI) * (float)Math.PI * 0.25F;

                if ((double)var1 > 0.75D)
                {
                    this.randomMotionSpeed = 1.0F;
                    this.squidYawSpeed = 1.0F;
                }
                else
                {
                    this.squidYawSpeed *= 0.8F;
                }
            }
            else
            {
                this.tentacleAngle = 0.0F;
                this.randomMotionSpeed *= 0.9F;
                this.squidYawSpeed *= 0.99F;
            }
        	
            if (!this.worldObj.isRemote)
            {
                this.motionX = (double)(this.randomMotionVecX * this.randomMotionSpeed);
                this.motionY = (double)(this.randomMotionVecY * this.randomMotionSpeed);
                this.motionZ = (double)(this.randomMotionVecZ * this.randomMotionSpeed);
                
                if (possessedLeapPropulsionCountdown > 0 )
                {
                    motionY = 1F;
                }
            }            

        	if (possessedLeapPropulsionCountdown > 0 )
        	{
        		possessedLeapPropulsionCountdown--;
        	}
        	
    		if (tentacleAttackInProgressCounter >= 0 )
    		{
    			orientToTentacleAttackPoint();
    		}
    		else if ( entityToAttack != null )
    		{
    			orientToEntity(entityToAttack);
    		}
    		else
    		{
    			orientToMotion();
    		}
        }
        else
        {
        	// squid is out of water

            possessedLeapPropulsionCountdown = 0;
        	
            this.tentacleAngle = MathHelper.abs(MathHelper.sin(this.tentacleAnimProgress)) * (float)Math.PI * 0.25F;

            if (!this.worldObj.isRemote)
            {
                this.motionX = 0.0D;
                this.motionY -= 0.08D;
                this.motionY *= 0.9800000190734863D;
                this.motionZ = 0.0D;
            }

    		if (tentacleAttackInProgressCounter >= 0 )
    		{
    			orientToTentacleAttackPoint();
    		}
    		else if ( motionY > 0.5F )
    		{
                squidPitch = 0F;
    		}
    		else
    		{
                squidPitch = (float)((double) squidPitch + (double)(-90.0F - squidPitch) * 0.02D);
    		}    		
        }        
    }
    
    @Override
    public void moveEntityWithHeading( float par1, float par2 )
    {
        moveEntity( motionX, motionY, motionZ );
    }
    
    @Override
    protected void updateEntityActionState()
    {
    	// only called on server
    	
        tentacleAttackCooldownTimer--;
        
        checkForHeadCrab();
        
        if ( isHeadCrab() )
        {
        	updateHeadCrabActionState();
        	
        	return;
        }
        
        float fNaturalLightLevel = getBrightness( 1F );
        
        boolean bIsInDarkness = fNaturalLightLevel < BRIGHTNESS_AGGRESSION_THRESHOLD;

        if ( !worldObj.isDaytime() )
        {
        	bIsInDarkness = true;
        }
        
    	if ( entityToAttack == null  )
    	{
    		if ( bIsInDarkness )
    		{
    			Entity targetEntity = findClosestValidAttackTargetWithinRange(AGGRESSION_RANGE);
	    		
	    		if ( targetEntity != null )
	    		{
	    			setTarget( targetEntity );
	    		}
    		}
    	}
    	else 
    	{
    		if ( bIsInDarkness || rand.nextInt(CHANCE_OF_LOSING_ATTACK_TARGET_IN_LIGHT) != 0 )
    		{
    			// FCOTOD: Clean up this test
	    		if (!entityToAttack.isValidOngoingAttackTargetForSquid() ||
                    getDistanceToEntity( entityToAttack ) > AGGRESSION_RANGE ||
                    (
                            worldObj.isDaytime() && entityToAttack.getBrightness( 1F ) > BRIGHTNESS_AGGRESSION_THRESHOLD &&
                            rand.nextInt(CHANCE_OF_LOSING_ATTACK_TARGET_IN_LIGHT) == 0
    				) 
				)
	    		{
	    			setTarget( null );
	    		}
    		}
    		else
    		{
    			setTarget( null );
    		}
    	}
    	
    	if ( entityToAttack != null )
    	{
    		double dDeltaX = entityToAttack.posX - posX; 
    		double dDeltaY = ( entityToAttack.posY + entityToAttack.getEyeHeight() ) - ( posY + ( height / 2 ) ); 
    		double dDeltaZ = entityToAttack.posZ - posZ;
    		
    		double dDistSqToTarget = dDeltaX * dDeltaX + dDeltaY * dDeltaY + dDeltaZ * dDeltaZ;

    		if ( dDistSqToTarget > ( 0.5D * 0.5D ) )
    		{
    			double dDistToTarget = MathHelper.sqrt_double( dDistSqToTarget );
    			
    			double dUnitVectorToTargetX = dDeltaX / dDistToTarget;
    			double dUnitVectorToTargetY = dDeltaY / dDistToTarget;
    			double dUnitVectorToTargetZ = dDeltaZ / dDistToTarget;

                randomMotionVecX = (float)(dUnitVectorToTargetX * 0.4D );
                randomMotionVecY = (float)(dUnitVectorToTargetY * 0.4D );
                randomMotionVecZ = (float)(dUnitVectorToTargetZ * 0.4D );
	    		
	    		if ( !isFullyPossessed() )
	    		{
		    		// prevent squid from leaping out of the water
		    		
	    			float fDepth = getDepthBeneathSurface(SAFE_ATTACK_DEPTH_TEST_MAXIMUM);
	    			
	    			if (fDepth < SAFE_ATTACK_DEPTH)
	    			{
	    				if (randomMotionVecY > -0.1F )
	    				{
                            randomMotionVecY = -0.1F;
	    				}
	    			}
	    			else if (randomMotionVecY > 0F )
		    		{
		    			float fDeltaSafeDepth = fDepth - SAFE_ATTACK_DEPTH;
		    			
		    			if (randomMotionVecY > fDeltaSafeDepth )
		    			{
                            randomMotionVecY = fDeltaSafeDepth;
		    			}
		    		}
	    		}
	    		
	    		if (inWater && ( !entityToAttack.inWater || entityToAttack.ridingEntity != null ) &&
                    tentacleAttackInProgressCounter < 0 && tentacleAttackCooldownTimer <= 0 && rand.nextInt(20) == 0 )
	    		{
	    			attemptTentacleAttackOnTarget();
	    		}	    			
    		}
    		else
    		{
                randomMotionVecX = randomMotionVecY = randomMotionVecZ = 0.0F;
    		}    		
    	}        
    	else if ( rand.nextInt( 50 ) == 0 || !inWater || (randomMotionVecX == 0.0F && randomMotionVecY == 0.0F && randomMotionVecZ == 0.0F ) )
        {
            float fFlatHeading = rand.nextFloat() * (float)Math.PI * 2.0F;

            randomMotionVecZ = MathHelper.sin(fFlatHeading) * 0.2F;
            randomMotionVecX = MathHelper.cos(fFlatHeading) * 0.2F;
            
			float fDepth = getDepthBeneathSurface(SAFE_PASSIVE_DEPTH_TEST_MAXIMUM);

			if (isFullyPossessed() && inWater )
			{
				// possessed squid always move towards the surface

                randomMotionVecY = 0.1F;
				
		    	if (fDepth < POSSESSED_LEAP_DEPTH && possessedLeapCountdown <= 0)
		    	{	
		    		possessedLeap();
		    	}
			}				
			else if (fDepth >= SAFE_PASSIVE_DEPTH)
			{
	    		if (fNaturalLightLevel < BRIGHTNESS_AGGRESSION_THRESHOLD)
	    		{    		
	    			int iSkylightSubtracted = worldObj.skylightSubtracted;
	    			
	    			if ( !worldObj.isDaytime() )
	    			{
		    			// go up in darkness if the surface is dark

                        randomMotionVecY = 0.1F;
	    			}
	    			else
	    			{
	    				// random motion tending towards downwards if the surface is light

                        randomMotionVecY = (rand.nextFloat() * 0.15F ) - 0.1F;
	    				
	    			}
	    		}
	    		else
	    		{
	    			// move down in light

                    randomMotionVecY = -0.1F;
	    		}
			}
			else
			{
				// move down if not at sufficient depth

                randomMotionVecY = -0.1F;
			}
        }

        entityAge++;        
        despawnEntity();
    }
    
    @Override
    protected double minDistFromPlayerForDespawn()
    {
    	return 144D;
    }
    
    @Override
    protected boolean canDespawn()
    {
    	return !isHeadCrab();
    }
    
    @Override
    public boolean getCanSpawnHere()
    {
        int i = MathHelper.floor_double( posX );
        int j = MathHelper.floor_double( posY );
        int k = MathHelper.floor_double( posZ );

        if ( !isBlockSurroundedByWater(i, j, k)
    		&& !isBlockSurroundedByWater(i + 1, j, k)
    		&& !isBlockSurroundedByWater(i - 1, j, k)
    		&& !isBlockSurroundedByWater(i, j + 1, k)
    		&& !isBlockSurroundedByWater(i, j - 1, k)
    		&& !isBlockSurroundedByWater(i, j, k + 1)
    		&& !isBlockSurroundedByWater(i, j, k - 1) )
		{
        	return false;
        }
        
        int iLightLevel = this.worldObj.getBlockLightValue( i, j, k );
        
        if ( iLightLevel > 1 )
        {
        	return false;
        }

        return super.getCanSpawnHere();
    }
    
    @Override
    public boolean attackEntityFrom( DamageSource damageSource, int iDamageAmount )
    {
    	if ( this.isHeadCrab() )
    	{
            if ( damageSource == DamageSource.inWall )
            {
            	return false;
            }
    	}
    	else
    	{
    		if (isPossessed() && damageSource == DamageSource.fall )
    		{
	        	return false;
    		}
    		
    		if ( super.attackEntityFrom( damageSource, iDamageAmount ) )
    		{
    			if ( !worldObj.isRemote )
    			{
	                Entity attackingEntity = damageSource.getEntity();
	
	                if ( attackingEntity != null && attackingEntity != this )
	                {
                		setTarget( attackingEntity );
	                }
    			}
                
    			return true;
    		}
    		
    		return false;
    	}
    	
    	return super.attackEntityFrom( damageSource, iDamageAmount );
    }
    
    @Override
    protected void playStepSound(int par1, int par2, int par3, int par4)
    {
    	// override to prevent squids playing sounds when on top of blocks.
    }
    
    @Override
    public void checkForScrollDrop()
    {    	
    	if ( rand.nextInt( 250 ) == 0 )
    	{
            ItemStack itemstack = new ItemStack( BTWItems.arcaneScroll, 1, Enchantment.respiration.effectId );
            
            entityDropItem(itemstack, 0.0F);
    	}
    }
    
    @Override
    public AxisAlignedBB getVisualBoundingBox()
    {
    	if (tentacleAttackInProgressCounter >= 0 )
    	{
    		double dExpandByAmount = TENTACLE_ATTACK_RANGE + 0.25D;
    		
    		return boundingBox.expand( dExpandByAmount, dExpandByAmount, dExpandByAmount );
    	}
    	
		return boundingBox;
    }
    
    @Override
    public void setTarget(Entity targetEntity )
    {
    	if ( !worldObj.isRemote && targetEntity != entityToAttack )
    	{
            entityToAttack = targetEntity;
            
            transmitAttackTargetToClients();
    	}
    	else
    	{
            entityToAttack = targetEntity;
    	}    	
    }

    @Override
    public boolean getCanCreatureTypeBePossessed()
    {
    	return true;
    }
    
    @Override
    public boolean getCanCreatureBePossessedFromDistance(boolean bPersistentSpirit)
    {
    	// limit number squid that can be possessed from non-persistent sources (like  portals) to prevent potential performace problems
    	// in the case of persistent sources, a persistent entity has died to create the spirit, so there shouldn't be a problem
    	// with the number of entities created
    	
    	return bPersistentSpirit || worldObj.getNumEntitiesThatApplyToSquidPossessionCap() < SQUID_POSSESSION_MAX_COUNT;
    }
    
    @Override
	public boolean onPossessedRidingEntityDeath()
	{
    	if ( isEntityAlive() && !isPossessed() )
    	{
    		initiatePossession();
    		
    		return true;
    	}
    	
		return false;
	}
	
    @Override
    public void initiatePossession()
    {
    	super.initiatePossession();
    	
    	setPersistent(true);
    }
    
    @Override
    protected void handlePossession()
    {
        super.handlePossession();
        
    	// test code for immediate full possession
        /*
        if ( !worldObj.isRemote )
        {
	    	if ( IsPossessed() )
	    	{
	    		if ( GetPossessionLevel() == 1 )
	    		{
	    			m_iPossessionTimer = 0;
	    			
	    			SetPossessionLevel( 2 );
		    			
	    			OnFullPossession();	    			
	    		}
	    	}
        }
        */
    	
        if (possessedLeapCountdown > 0 )
        {
        	possessedLeapCountdown--;
        }
        
        if (!worldObj.isRemote && isFullyPossessed() )
        {
			if ( ridingEntity == null && !inWater && !onGround )
			{
				if (possessedLeapGhastConversionDiceRoll <= possessedLeapGhastConversionChance &&
					motionY <= 0F ) // negative motion to wait for apex of leap
				{					
			        GhastEntity ghast = (GhastEntity) EntityList.createEntityOfType(GhastEntity.class, worldObj);
			        ghast.setLocationAndAngles( posX, posY, posZ, rotationYaw, 0F );
			        
					if ( worldObj.checkNoEntityCollision( ghast.boundingBox, this ) && 
						worldObj.getCollidingBoundingBoxes( this, ghast.boundingBox ).isEmpty() && 
						!worldObj.isAnyLiquid( ghast.boundingBox ) )
					{
				        worldObj.playAuxSFX( BTWEffectManager.POSSESSED_SQUID_TRANSFORMATION_EFFECT_ID,
				    		MathHelper.floor_double( posX ), MathHelper.floor_double( posY ), MathHelper.floor_double( posZ ), 
				    		0 );
				        
				        this.setDead();
				        
				        ghast.setPersistent(true);
				        
				        worldObj.spawnEntityInWorld( ghast );    			        
					}
				}
			}
			else
			{
				if ( !inWater || motionY <= 0F )
				{
					// reset the dice roll until the next leap occurs

                    possessedLeapGhastConversionDiceRoll = 1F;
				}
			}
        }        
    }
    
    @Override
    public boolean doesEntityApplyToSquidPossessionCap()
    {
    	return isEntityAlive() && getIsPersistent();
    }
    
    @Override
    public boolean isValidZombieSecondaryTarget(EntityZombie zombie)
    {
    	return !inWater && ridingEntity == null && zombie.riddenByEntity == null;
    }
    
    @Override
    public boolean attractsLightning()
    {
    	return false;
    }

    @Override
    public float getEyeHeight()
    {
        return height * 0.5F;
    }

    //------------- Class Specific Methods ------------//
    
	private void updateHeadCrabActionState()
	{
		// only called on server
		
		Entity sharedTarget = ridingEntity.getHeadCrabSharedAttackTarget();
		
		if ( sharedTarget == this )
		{
			sharedTarget = null;
		}
		
		setTarget( sharedTarget );
		
		if ( entityToAttack != null )
		{
    		if (tentacleAttackInProgressCounter < 0 && tentacleAttackCooldownTimer <= 0 && rand.nextInt(20) == 0 )
    		{
    			attemptTentacleAttackOnTarget();
    		}	    			
		}		
    	
    	if (isFullyPossessed() && possessedLeapCountdown <= 0 && !inWater && rand.nextInt(100) == 0  )
    	{	
    		mountEntity( null );
    		
    		possessedLeap();
    	}
	}
	
    private void orientToMotion()
    {
        float fMotionVectorFlatLength = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        
        renderYawOffset = interpolateAngle(renderYawOffset, -((float)Math.atan2(this.motionX, this.motionZ)) * 180.0F / (float)Math.PI, 1F);
        rotationYaw = renderYawOffset;

        squidPitch += (-((float)Math.atan2((double)fMotionVectorFlatLength, motionY)) * 180.0F / (float)Math.PI - squidPitch) * 0.1F;

        squidYaw += (float)Math.PI * squidYawSpeed * 1.5F; // this is actually the squid's roll
    }
    
    private void orientToEntity(Entity entity)
    {
		double dDeltaX = entity.posX - posX; 
		double dDeltaY = entity.posY + entity.getEyeHeight() - ( posY + ( height / 2 ) ); 
		double dDeltaZ = entity.posZ - posZ;
		
        double dFlatDist = MathHelper.sqrt_double( dDeltaX * dDeltaX + dDeltaZ * dDeltaZ );
        
        renderYawOffset = interpolateAngle(renderYawOffset, -((float)Math.atan2(dDeltaX, dDeltaZ)) * 180.0F / (float)Math.PI, 1F);
        rotationYaw = renderYawOffset;

        squidPitch = interpolateAngle(squidPitch, -(float)(Math.atan2(dFlatDist, dDeltaY) * 180.0F / Math.PI ), 10F);

        squidYaw += (float)Math.PI * squidYawSpeed * 1.5F; // this is actually the squid's roll
    }
    
    private void orientToTentacleAttackPoint()
    {
		double dDeltaX = tentacleAttackTargetX - posX;
		double dDeltaY = tentacleAttackTargetY - (posY + (height / 2 ) );
		double dDeltaZ = tentacleAttackTargetZ - posZ;
		
        double dFlatDist = MathHelper.sqrt_double( dDeltaX * dDeltaX + dDeltaZ * dDeltaZ );
        
        renderYawOffset = interpolateAngle(renderYawOffset, -((float)Math.atan2(dDeltaX, dDeltaZ)) * 180.0F / (float)Math.PI, 50F);
        rotationYaw = renderYawOffset;

        squidPitch = interpolateAngle(squidPitch, -(float)(Math.atan2(dFlatDist, dDeltaY) * 180.0F / Math.PI - 150D ), 50F);

        squidYaw = interpolateAngle(squidYaw, 0, 50F); // this is actually the squid's roll
    }
    
    private Entity findClosestValidAttackTargetWithinRange(double dRange)
    {
        Entity targetEntity = null;
        double dClosestDistSq = dRange * dRange; 

        for ( int iPlayerCount = 0; iPlayerCount < worldObj.playerEntities.size(); ++iPlayerCount )
        {
            EntityPlayer tempPlayer = (EntityPlayer)worldObj.playerEntities.get( iPlayerCount );

            if ( !tempPlayer.capabilities.disableDamage && tempPlayer.isEntityAlive() )
            {
        		double dDeltaX = tempPlayer.posX - posX; 
        		double dDeltaY = tempPlayer.posY - posY; 
        		double dDeltaZ = tempPlayer.posZ - posZ;
        		
        		double dDistSq  = dDeltaX * dDeltaX + dDeltaY * dDeltaY + dDeltaZ * dDeltaZ;
	
				if (dDistSq < dClosestDistSq
						&& (!worldObj.isDaytime() || tempPlayer.getBrightness(1F) < BRIGHTNESS_AGGRESSION_THRESHOLD)
						&& (tempPlayer.inWater || (canEntityBeSeen(tempPlayer) && this.worldObj.getDifficulty().shouldSquidsAttackDryPlayers()))
						&& (tempPlayer.ridingEntity == null || this.worldObj.getDifficulty().shouldSquidsAttackDryPlayers()))
        		{
        			targetEntity = tempPlayer;
        			dClosestDistSq = dDistSq; 
        		}
            }
        }
        
        // stagger secondary scans to only occur every 32 ticks for performance
        
        if ( ( ( worldObj.worldInfo.getWorldTime() + entityId ) & 31 ) == 0 && targetEntity == null  )
        {
        	targetEntity = worldObj.getClosestEntityMatchingCriteriaWithinRange(
        		posX, posY, posZ, dRange, ClosestEntitySelectionCriteria.secondarySquidTarget);
        }
        
        return targetEntity;
    }
    
    private void checkForHeadCrab()
    {
    	if ( isEntityAlive() )
    	{
			if ( ridingEntity == null )
			{		        
				if ( motionY < 0.5F )
				{
			    	if (reCrabEntityCountdown > 0 )
			    	{
			    		reCrabEntityCountdown--;
			    	}
			    	else
			    	{
                        entityToNotReCrab = null;
			    	}
			    	
					EntityLiving target = getValidHeadCrabTargetInRange();
					
					if ( target != null )
					{
						mountEntity( target );
						
			            playSound( "mob.slime.attack", 1F, ( rand.nextFloat() - 
			            	rand.nextFloat() ) * 0.2F + 1F );

                        headCrabDamageCounter = HEAD_CRAB_DAMAGE_INITIAL_DELAY;
			            
			    		target.onHeadCrabbedBySquid(this);
					}
				}
			}
			else
			{
                entityToNotReCrab = ridingEntity; // tracking variable to prevent squid immediately reattaching when knocked off
                reCrabEntityCountdown = RE_CRAB_ENTITY_TICKS;
			}
    	}
    }
    
    private EntityLiving getValidHeadCrabTargetInRange()
    {
    	double dRange = 0.25D;
    	
    	if ( !isInWater() )
    	{
    		dRange = 0.5D;
    	}
    	
        List<EntityLiving> entityList = worldObj.getEntitiesWithinAABB( EntityLiving.class, 
        	boundingBox.expand( dRange, dRange, dRange ) );
        
        Iterator<EntityLiving> entityIterator = entityList.iterator();

        while ( entityIterator.hasNext() )
        {
    		EntityLiving tempEntity = entityIterator.next();
    		
            if (tempEntity.getCanBeHeadCrabbed(isInWater()) &&
                tempEntity != entityToNotReCrab && canEntityBeSeen(tempEntity) )
	        {
            	return tempEntity;
	        }		        
        }
        
        return null;        
    }
    
	private void updateHeadCrab()
	{
        tentacleAnimSpeed = 0.2F;

        squidPitch = 0;
		
	    // same tentacle movement as out of water
	    
	    float fSinTentacle = MathHelper.sin(tentacleAnimProgress);
	    
	    this.tentacleAngle = MathHelper.abs(MathHelper.sin(fSinTentacle)) * (float)Math.PI * 0.25F;
	    
	    if ( !worldObj.isRemote )
	    {
	    	headCrabDamageCounter--;
	    	
	    	if (headCrabDamageCounter <= 0 )
	    	{
	    		if ( !ridingEntity.isImmuneToHeadCrabDamage() )
	    		{
		    		DamageSource squidSource = DamageSource.causeMobDamage( this );
		    		
		    		squidSource.setDamageBypassesArmor();
		    
		    		ridingEntity.attackEntityFrom( squidSource, 1 );
	    		}

                headCrabDamageCounter = HEAD_CRAB_DAMAGE_PERIOD;
	    	}
	    	
	    	// knock the player out of boats, minecarts, or whatever else he might be riding
	    	
	    	if ( ridingEntity.ridingEntity != null )
	    	{
	    		ridingEntity.mountEntity( ridingEntity.ridingEntity );
	    		
	            if ( ridingEntity.ridingEntity != null)
	            {
	                ridingEntity.ridingEntity.riddenByEntity = null;
	                ridingEntity.ridingEntity = null;
	            }
	    	}
	    }
	    else
	    {
	    	// check if the tentacles have reversed direction
	    	
	        float fPrevSinTentacle = MathHelper.sin(prevTentacleAnimProgress);
	        
	    	if ( ( fPrevSinTentacle <= 0 && fSinTentacle > 0 ) || ( fPrevSinTentacle > 0 && fSinTentacle <= 0 ) )
	    	{
	    		if ( !ridingEntity.isImmuneToHeadCrabDamage() )
	    		{
		            worldObj.playSound( posX, posY, posZ, "random.eat", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 0.8F);
		            worldObj.playSound( posX, posY, posZ, "mob.slime.big", 0.5F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 0.7F);
	    		}
	    		else
	    		{
	    			worldObj.playSound( posX, posY, posZ, "mob.slime.big", 0.025F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 0.7F);
	    		}
	    		
	    	}
	    	
	    	if ( ridingEntity instanceof EntityLiving )
	    	{
	    		EntityLiving ridingCreature = (EntityLiving)ridingEntity;

                squidYaw = -ridingCreature.rotationYawHead;
                prevSquidYaw = -ridingCreature.prevRotationYawHead;
	    		
	            renderYawOffset = 0;
	            rotationYaw = 0;
	    	}		        	
	    }
	}
	
    private void attemptTentacleAttackOnTarget()
    {
		double dDeltaX = entityToAttack.posX - posX; 
		double dDeltaY = ( entityToAttack.posY + ( entityToAttack.height / 2F ) ) - ( posY + ( height / 2F ) ); 
		double dDeltaZ = entityToAttack.posZ - posZ;
		
		double dDistSqToTarget = dDeltaX * dDeltaX + dDeltaY * dDeltaY + dDeltaZ * dDeltaZ;
		
		if ( dDistSqToTarget < (TENTACLE_ATTACK_RANGE * TENTACLE_ATTACK_RANGE) )
		{
			double dDistToTarget;
			
			if ( !canEntityCenterOfMassBeSeen(entityToAttack) )
			{
				if ( canEntityBeSeen( entityToAttack ) )
				{
					dDeltaY = ( entityToAttack.posY + entityToAttack.getEyeHeight() ) - ( posY + ( height / 2F ) );
					
					dDistSqToTarget = dDeltaX * dDeltaX + dDeltaY * dDeltaY + dDeltaZ * dDeltaZ;
				}
				else
				{
					return;
				}				
			}
			
			dDistToTarget = MathHelper.sqrt_double( dDistSqToTarget );
			
			double dUnitVectorToTargetX = dDeltaX / dDistToTarget;
			double dUnitVectorToTargetY = dDeltaY / dDistToTarget;
			double dUnitVectorToTargetZ = dDeltaZ / dDistToTarget;
			
			launchTentacleAttackInDirection(dUnitVectorToTargetX, dUnitVectorToTargetY, dUnitVectorToTargetZ);
		}
    }
	
    private void launchTentacleAttackInDirection(double dUnitVectorToTargetX, double dUnitVectorToTargetY, double dUnitVectorToTargetZ)
    {
        tentacleAttackInProgressCounter = 0;
        tentacleAttackCooldownTimer = TENTACLE_ATTACK_TICKS_TO_COOLDOWN;

        tentacleAttackTargetX = posX + (dUnitVectorToTargetX * TENTACLE_ATTACK_RANGE);
        tentacleAttackTargetY = posY + (height / 2F ) + (dUnitVectorToTargetY * TENTACLE_ATTACK_RANGE);
        tentacleAttackTargetZ = posZ + (dUnitVectorToTargetZ * TENTACLE_ATTACK_RANGE);
		
		transmitTentacleAttackToClients();
    }
    
    private void transmitTentacleAttackToClients()
    {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream( byteStream );
        
        try
        {
	        dataStream.writeInt( entityId );
	        dataStream.writeByte( (byte) EntityEventPacketHandler.SQUID_TENTACLE_ATTACK_EVENT_ID);
	        
	        dataStream.writeInt( MathHelper.floor_double(tentacleAttackTargetX * 32D));
	        dataStream.writeInt( MathHelper.floor_double(tentacleAttackTargetY * 32D));
	        dataStream.writeInt( MathHelper.floor_double(tentacleAttackTargetZ * 32D));
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }        
	        
        Packet250CustomPayload packet = new Packet250CustomPayload(BTWPacketManager.CUSTOM_ENTITY_EVENT_PACKET_CHANNEL, byteStream.toByteArray() );
        
        WorldUtils.sendPacketToAllPlayersTrackingEntity((WorldServer)worldObj, this, packet);
    }
    
    public void onClientNotifiedOfTentacleAttack(double dTargetX, double dTargetY, double dTargetZ)
    {
        tentacleAttackInProgressCounter = 0;

        tentacleAttackTargetX = dTargetX;
        tentacleAttackTargetY = dTargetY;
        tentacleAttackTargetZ = dTargetZ;
		
        worldObj.playSound( posX, posY, posZ, "random.bow", 1F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 0.5F);
        worldObj.playSound( posX, posY, posZ, "mob.slime.big", 1F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 0.5F);
        
        if ( inWater )
        {
	        for ( int iParticleCount = 0; iParticleCount < 150; iParticleCount++ )
	        {
	            this.worldObj.spawnParticle( "bubble", 
	            	posX + ( rand.nextDouble() * 2F ) - 1F, 
	            	posY + rand.nextDouble(),  
	            	posZ + ( rand.nextDouble() * 2F ) - 1F, 
	            	0D, 0D, 0D);
	        }
	        
	        for ( int iParticleCount = 0; iParticleCount < 10; iParticleCount++ )
	        {
	            this.worldObj.spawnParticle( "splash", 
	            	posX + ( rand.nextDouble() * 2F ) - 1F, 
	            	posY + height,  
	            	posZ + ( rand.nextDouble() * 2F ) - 1F, 
	            	0D, 0D, 0D);
	        }
	        
	        worldObj.playSound( posX, posY, posZ, "liquid.splash", 1F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
        }        
    }

    private void updateTentacleAttack()
    {
    	if (tentacleAttackInProgressCounter >= 0 )
    	{
    		tentacleAttackInProgressCounter++;
    		
    		if (tentacleAttackInProgressCounter >= TENTACLE_ATTACK_DURATION)
    		{
                tentacleAttackInProgressCounter = -1;
    		}
    		else
    		{
    			// tip is only dangerous on forward motion
    			
    			if (tentacleAttackInProgressCounter <= (TENTACLE_ATTACK_DURATION >> 1 ) )
    			{
    				Vec3 tentacleTip = computeTentacleAttackTip((float) tentacleAttackInProgressCounter);
    				
    				AxisAlignedBB tipBox = AxisAlignedBB.getAABBPool().getAABB(
                            tentacleTip.xCoord - TENTACLE_ATTACK_TIP_COLLISION_HALF_WIDTH,
                            tentacleTip.yCoord - TENTACLE_ATTACK_TIP_COLLISION_HALF_WIDTH,
                            tentacleTip.zCoord - TENTACLE_ATTACK_TIP_COLLISION_HALF_WIDTH,
                            tentacleTip.xCoord + TENTACLE_ATTACK_TIP_COLLISION_HALF_WIDTH,
                            tentacleTip.yCoord + TENTACLE_ATTACK_TIP_COLLISION_HALF_WIDTH,
                            tentacleTip.zCoord + TENTACLE_ATTACK_TIP_COLLISION_HALF_WIDTH);
    				
    		        List potentialCollisionList = worldObj.getEntitiesWithinAABB( EntityLiving.class, tipBox );
    		        
    		        if ( !potentialCollisionList.isEmpty() )
    		        {
	    		        Iterator collisionIterator = potentialCollisionList.iterator();
	    		        
	    		        while ( collisionIterator.hasNext() )
	    		        {
	    		        	EntityLiving tempEntity = (EntityLiving)collisionIterator.next();
	    		        	
	    		        	if ( !(tempEntity instanceof SquidEntity) && tempEntity != ridingEntity )
	    		        	{
	    		        		retractTentacleAttackOnCollision();
	    		        		
	    		        		if ( !worldObj.isRemote )
	    		        		{
	    		        			tentacleAttackFlingTarget(tempEntity, true);
	    		        		}
	    		        		
	    		        		break;
	    		        	}
	    		        }
    		        }
    			}
    		}
    	}
    }
    
    private void retractTentacleAttackOnCollision()
    {
    	int iTurningPoint = (TENTACLE_ATTACK_DURATION >> 1 );
    	
		if (tentacleAttackInProgressCounter < iTurningPoint )
		{
            tentacleAttackInProgressCounter = iTurningPoint + (iTurningPoint - tentacleAttackInProgressCounter);
		}
    }
    
    public Vec3 computeTentacleAttackTip(float fAttackProgressTick)
    {
    	double dAttackProgressSin = getAttackProgressSin(fAttackProgressTick);
    	
    	double dDeltaX = tentacleAttackTargetX - posX;
    	double dDeltaY = tentacleAttackTargetY - (posY + (height / 2F ) );
    	double dDeltaZ = tentacleAttackTargetZ - posZ;
    	
    	double dTipOffsetX = dDeltaX * dAttackProgressSin;
    	double dTipOffsetY = dDeltaY * dAttackProgressSin;
    	double dTipOffsetZ = dDeltaZ * dAttackProgressSin;
    	
    	return Vec3.createVectorHelper( posX + dTipOffsetX, posY + ( height / 2F ) + dTipOffsetY, posZ + dTipOffsetZ );
    }
    
    public double getAttackProgressSin(float fAttackProgressTick)
    {
    	double dAttackProgress = fAttackProgressTick / (float) TENTACLE_ATTACK_DURATION;
    	
    	return MathHelper.sin( (float)( dAttackProgress * Math.PI ) );
    }
    
    private void tentacleAttackFlingTarget(Entity targetEntity, boolean bPrimary)
    {
        Entity secondaryTargetEntity = null;
        
        if ( targetEntity.ridingEntity != null )
        {
        	secondaryTargetEntity = targetEntity.ridingEntity;
        	
        	targetEntity.mountEntity( null );
        }
        
		if ( bPrimary )
		{
            int iFXI = MathHelper.floor_double( targetEntity.posX );
            int iFXJ = MathHelper.floor_double( targetEntity.posY ) + 1;
            int iFXK = MathHelper.floor_double( targetEntity.posZ );
            
	        worldObj.playAuxSFX( BTWEffectManager.SQUID_TENTACLE_FLING_EFFECT_ID, iFXI, iFXJ, iFXK, 0 );
		}
		
        double dVelocityX = targetEntity.motionX;
        double dVelocityZ = targetEntity.motionZ;
        
		double dDeltaX = targetEntity.posX - posX; 
		double dDeltaZ = targetEntity.posZ - posZ;
		
		double dFlatDistToTargetSq = dDeltaX * dDeltaX + dDeltaZ * dDeltaZ;

		if ( dFlatDistToTargetSq > 0.1D )
		{
			double dFlatDistToTarget = MathHelper.sqrt_double( dFlatDistToTargetSq );
			
			dVelocityX += (float)( -dDeltaX / dFlatDistToTarget ) * 1.0F;
			dVelocityZ += (float)( -dDeltaZ / dFlatDistToTarget ) * 1.0F;
		}
		
		if ( targetEntity instanceof EntityPlayer && ((EntityPlayer)targetEntity).isBlocking() )
		{
			EntityPlayer blockingPlayer = (EntityPlayer)targetEntity;
			
	        ItemStack blockItemStack = blockingPlayer.inventory.mainInventory[blockingPlayer.inventory.currentItem];
	        
	        if ( blockItemStack != null )
	        {
				ItemStack flingStack = new ItemStack( blockItemStack.itemID, blockItemStack.stackSize, blockItemStack.getItemDamage() );
				
				InventoryUtils.copyEnchantments(flingStack, blockItemStack);
				
		        double dItemXPos = targetEntity.posX;
		        double dItemYPos = targetEntity.posY + 1D;
		        double dItemZPos = targetEntity.posZ;
		    	
		        EntityItem entityitem = (EntityItem) EntityList.createEntityOfType(EntityItem.class, worldObj, dItemXPos, dItemYPos, dItemZPos, flingStack );

		        double dVelocityY = targetEntity.motionY + 0.5D;
		        
		        entityitem.motionX = dVelocityX;
		        entityitem.motionY = dVelocityY;
		        entityitem.motionZ = dVelocityZ;
		        
		        entityitem.delayBeforeCanPickup = 10;
		        
		        worldObj.spawnEntityInWorld(entityitem);
		        
		        blockItemStack.stackSize = 0;
	        }			
		}
		else
		{
			targetEntity.isAirBorne = true;

	        double dVelocityY = targetEntity.motionY + 0.75D;
	        
	        dVelocityX *= ( rand.nextDouble() * 0.2D ) + 0.9;
	        dVelocityZ *= ( rand.nextDouble() * 0.2D ) + 0.9;
	        
			targetEntity.motionX = dVelocityX;
			targetEntity.motionY = dVelocityY;
			targetEntity.motionZ = dVelocityZ;
			
			capFlingMotionOfEntity(targetEntity);
	    	
			targetEntity.setBeenAttacked();
		}
		
		targetEntity.onFlungBySquidTentacle(this);
		
		if ( secondaryTargetEntity != null )
		{
			tentacleAttackFlingTarget(secondaryTargetEntity, false);
		}		
    }
    
	private void capFlingMotionOfEntity(Entity targetEntity)
	{
		if ( targetEntity.motionY > 0.75D )
		{
			targetEntity.motionY = 0.75D;
		}
		
		if ( targetEntity.motionX > 1D )
		{
			targetEntity.motionX = 1D;
		}
		else if ( targetEntity.motionX < -1D )
		{
			targetEntity.motionX = -1D;
		}
		
		if ( targetEntity.motionZ > 1D )
		{
			targetEntity.motionZ = 1D;
		}
		else if ( targetEntity.motionZ < -1D )
		{
			targetEntity.motionZ = -1D;
		}
	}
	
    public boolean isHeadCrab()
    {
    	return ridingEntity != null && ridingEntity instanceof EntityLiving;
    }
    
    private boolean isBlockSurroundedByWater(int i, int j, int k)
    {    	
        for ( int iTempJ = j - 1; iTempJ <= j + 1; iTempJ++ )
        {
	        for ( int iTempI = i - 1; iTempI <= i + 1; iTempI++ )
	        {
                for ( int iTempK = k - 1; iTempK <= k + 1; iTempK++ )
                {
			        int iTempBlockID = worldObj.getBlockId( iTempI, iTempJ, iTempK );
			        
			        if ( iTempBlockID != Block.waterMoving.blockID && iTempBlockID != Block.waterStill.blockID )
			        {
			        	return false;
			        }
                }
            }
        }
        
    	return true;
    }

    public float getDepthBeneathSurface(float fMaxDepthToConsider)
    {
    	// returns -1 if not in water, and 0 if in water but not at center pos
    	
    	float fDepth = -1F;
    	
        int iPosI = MathHelper.floor_double( posX );
        int iPosJ = (int)posY;
        int iPosK = MathHelper.floor_double( posZ );
        
        int iTempBlockID = worldObj.getBlockId( iPosI, iPosJ, iPosK );
        int iTempBlockAboveID = worldObj.getBlockId( iPosI, iPosJ + 1, iPosK );
        
        if ( iTempBlockID == Block.waterStill.blockID || iTempBlockID == Block.waterMoving.blockID || 
        	iTempBlockAboveID == Block.waterStill.blockID || iTempBlockAboveID == Block.waterMoving.blockID )
        {
    		fDepth = 0F;
    		
        	fDepth += posY - (float)iPosJ;
        	
        	for ( int iJOffset = 1; fDepth < fMaxDepthToConsider; iJOffset++ )
        	{
        		iTempBlockID = worldObj.getBlockId( iPosI, iPosJ + iJOffset, iPosK );
        		
                if ( iTempBlockID == Block.waterStill.blockID || iTempBlockID == Block.waterMoving.blockID )
                {
                    fDepth += 1F;
                }
                else
                {
                	if ( fDepth == 0F && posY > 32 )
                	{
                		break;
                	}
                	
                	break;
                }                    
        	}
        }
    	
    	return fDepth;
    }
    
    private float interpolateAngle(float fStartAngle, float fDestAngle, float fMaxIncrement)
    {
        float fDelta = MathHelper.wrapAngleTo180_float(fDestAngle - fStartAngle);

        if ( fDelta > fMaxIncrement )
        {
            fDelta = fMaxIncrement;
        }
        else if ( fDelta < -fMaxIncrement )
        {
            fDelta = -fMaxIncrement;
        }

        return fStartAngle + fDelta;
    }
    
    private void possessedLeap()
    {
		motionY = 1F;
		
		isAirBorne = true;

        possessedLeapCountdown = POSSESSED_LEAP_COUNTDOWN_DURATION;

        possessedLeapGhastConversionDiceRoll = rand.nextFloat();
		
		if ( inWater )
		{
            possessedLeapPropulsionCountdown = POSSESSED_LEAP_PROPULSION_DURATION;
			
            playSound( "liquid.splash", 1F, rand.nextFloat() * 0.1F + 0.5F );
		}
		else
		{
            possessedLeapPropulsionCountdown = 0;
			
			playSound( "mob.slime.big", 1F, rand.nextFloat() * 0.1F + 0.5F );
		}
    }

	private AxisAlignedBB getGhastConversionCollisionBoxFromPool()
	{
		double dWidthOffset = width / 16F; 
			
        return AxisAlignedBB.getAABBPool().getAABB( 
        	boundingBox.minX + dWidthOffset, boundingBox.maxY, boundingBox.minZ + dWidthOffset, 
        	boundingBox.maxX - dWidthOffset, boundingBox.maxY + 0.1F, boundingBox.maxZ - dWidthOffset );
	}    
}
