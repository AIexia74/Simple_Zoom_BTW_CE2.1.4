package net.minecraft.src;

import btw.client.fx.BTWEffectManager;
import btw.network.packet.BTWPacketManager;
import btw.client.network.packet.handler.EntityEventPacketHandler;
import btw.world.util.WorldUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Iterator;
import java.util.List;

public abstract class EntityCreature extends EntityLiving
{
    public PathEntity pathToEntity;

    /** The Entity this EntityCreature is set to attack. */
    public Entity entityToAttack;

    /**
     * returns true if a creature has attacked recently only used for creepers and skeletons
     */
    public boolean hasAttacked = false;

    /** Used to make a creature speed up and wander away when hit. */
    public int fleeingTick = 0;

    public EntityCreature(World par1World)
    {
        super(par1World);
    }

    /**
     * Disables a mob's ability to move on its own while true.
     */
    protected boolean isMovementCeased()
    {
        return false;
    }
    
    protected void updateEntityActionState()
    {
        this.worldObj.theProfiler.startSection("ai");

        if (this.fleeingTick > 0)
        {
            --this.fleeingTick;
        }

        this.hasAttacked = this.isMovementCeased();
        float var1 = 16.0F;

        if (this.entityToAttack == null)
        {
            this.entityToAttack = this.findPlayerToAttack();

            if (this.entityToAttack != null)
            {
                this.pathToEntity = this.worldObj.getPathEntityToEntity(this, this.entityToAttack, var1, true, false, false, true);
            }
        }
        else if (this.entityToAttack.isEntityAlive())
        {
            float var2 = this.entityToAttack.getDistanceToEntity(this);

            if ( shouldContinueAttacking(var2) )
        	{
                if (this.canEntityBeSeen(this.entityToAttack))
                {
                    this.attackEntity(this.entityToAttack, var2);
                }
        	}
            else
            {
                entityToAttack = null;
            }
        }
        else
        {
            this.entityToAttack = null;
        }

        this.worldObj.theProfiler.endSection();

        if (!this.hasAttacked && this.entityToAttack != null && (this.pathToEntity == null || this.rand.nextInt(20) == 0))
        {
            this.pathToEntity = this.worldObj.getPathEntityToEntity(this, this.entityToAttack, var1, true, false, false, true);
        }
        else if (!this.hasAttacked && (this.pathToEntity == null && this.rand.nextInt(180) == 0 || this.rand.nextInt(120) == 0 || this.fleeingTick > 0))
        {
            this.updateWanderPath();
        }

        int var21 = MathHelper.floor_double(this.boundingBox.minY + 0.5D);
        boolean var3 = this.isInWater();
        boolean var4 = this.handleLavaMovement();
        this.rotationPitch = 0.0F;

        if (this.pathToEntity != null && this.rand.nextInt(100) != 0)
        {
            this.worldObj.theProfiler.startSection("followpath");
            Vec3 var5 = this.pathToEntity.getPosition(this);
            double var6 = (double)(this.width * 2.0F);

            while (var5 != null && var5.squareDistanceTo(this.posX, var5.yCoord, this.posZ) < var6 * var6)
            {
                this.pathToEntity.incrementPathIndex();

                if (this.pathToEntity.isFinished())
                {
                    var5 = null;
                    this.pathToEntity = null;
                }
                else
                {
                    var5 = this.pathToEntity.getPosition(this);
                }
            }

            this.isJumping = false;

            if (var5 != null)
            {
                double var8 = var5.xCoord - this.posX;
                double var10 = var5.zCoord - this.posZ;
                double var12 = var5.yCoord - (double)var21;
                float var14 = (float)(Math.atan2(var10, var8) * 180.0D / Math.PI) - 90.0F;
                float var15 = MathHelper.wrapAngleTo180_float(var14 - this.rotationYaw);
                this.moveForward = this.moveSpeed;

                if (var15 > 30.0F)
                {
                    var15 = 30.0F;
                }

                if (var15 < -30.0F)
                {
                    var15 = -30.0F;
                }

                this.rotationYaw += var15;

                if (this.hasAttacked && this.entityToAttack != null)
                {
                    double var16 = this.entityToAttack.posX - this.posX;
                    double var18 = this.entityToAttack.posZ - this.posZ;
                    float var20 = this.rotationYaw;
                    this.rotationYaw = (float)(Math.atan2(var18, var16) * 180.0D / Math.PI) - 90.0F;
                    var15 = (var20 - this.rotationYaw + 90.0F) * (float)Math.PI / 180.0F;
                    this.moveStrafing = -MathHelper.sin(var15) * this.moveForward * 1.0F;
                    this.moveForward = MathHelper.cos(var15) * this.moveForward * 1.0F;
                }

                if (var12 > 0.0D)
                {
                    this.isJumping = true;
                }
            }

            if (this.entityToAttack != null)
            {
                this.faceEntity(this.entityToAttack, 30.0F, 30.0F);
            }

            if (this.isCollidedHorizontally && !this.hasPath())
            {
                this.isJumping = true;
            }

            if (this.rand.nextFloat() < 0.8F && (var3 || var4))
            {
                this.isJumping = true;
            }

            this.worldObj.theProfiler.endSection();
            
            // FCMOD: Added as the lack of super call in this condition was causing spiders 
            // with targets to hang out for an exceedingly long time without despawning
            entityAge++;
            
            despawnEntity();            
            // END FCMOD
        }
        else
        {
            super.updateEntityActionState();
            this.pathToEntity = null;
        }
    }

    /**
     * Time remaining during which the Animal is sped up and flees.
     */
    protected void updateWanderPath()
    {
        this.worldObj.theProfiler.startSection("stroll");
        boolean var1 = false;
        int var2 = -1;
        int var3 = -1;
        int var4 = -1;
        float var5 = -99999.0F;

        for (int var6 = 0; var6 < 10; ++var6)
        {
            int var7 = MathHelper.floor_double(this.posX + (double)this.rand.nextInt(13) - 6.0D);
            int var8 = MathHelper.floor_double(this.posY + (double)this.rand.nextInt(7) - 3.0D);
            int var9 = MathHelper.floor_double(this.posZ + (double)this.rand.nextInt(13) - 6.0D);
            float var10 = this.getBlockPathWeight(var7, var8, var9);

            if (var10 > var5)
            {
                var5 = var10;
                var2 = var7;
                var3 = var8;
                var4 = var9;
                var1 = true;
            }
        }

        if (var1)
        {
            this.pathToEntity = this.worldObj.getEntityPathToXYZ(this, var2, var3, var4, 10.0F, true, false, false, true);
        }

        this.worldObj.theProfiler.endSection();
    }

    /**
     * Basic mob attack. Default to touch of death in EntityCreature. Overridden by each mob to define their attack.
     */
    protected void attackEntity(Entity par1Entity, float par2) {}

    /**
     * Takes a coordinate in and returns a weight to determine how likely this creature will try to path to the block.
     * Args: x, y, z
     */
    public float getBlockPathWeight(int par1, int par2, int par3)
    {
        return 0.0F;
    }

    /**
     * Finds the closest player within 16 blocks to attack, or null if this Entity isn't interested in attacking
     * (Animals, Spiders at day, peaceful PigZombies).
     */
    protected Entity findPlayerToAttack()
    {
        return null;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        int var1 = MathHelper.floor_double(this.posX);
        int var2 = MathHelper.floor_double(this.boundingBox.minY);
        int var3 = MathHelper.floor_double(this.posZ);
        return super.getCanSpawnHere() && this.getBlockPathWeight(var1, var2, var3) >= 0.0F;
    }

    /**
     * Returns true if entity has a path to follow
     */
    public boolean hasPath()
    {
        return this.pathToEntity != null;
    }

    /**
     * sets the Entities walk path in EntityCreature
     */
    public void setPathToEntity(PathEntity par1PathEntity)
    {
        this.pathToEntity = par1PathEntity;
    }

    /**
     * Returns current entities target
     */
    public Entity getEntityToAttack()
    {
        return this.entityToAttack;
    }

    /**
     * Sets the entity which is to be attacked.
     */
    public void setTarget(Entity par1Entity)
    {
        this.entityToAttack = par1Entity;
    }

    /**
     * This method returns a value to be applied directly to entity speed, this factor is less than 1 when a slowdown
     * potion effect is applied, more than 1 when a haste potion effect is applied and 2 for fleeing entities.
     */
    public float getSpeedModifier()
    {
        float var1 = super.getSpeedModifier();

        if (this.fleeingTick > 0 && !this.isAIEnabled())
        {
            var1 *= 2.0F;
        }

        return var1;
    }
    
    // FCMOD: Added New
    private static final int IS_POSSESSED_DATA_WATCHER_ID = 24;
    
    protected int possessionTimer = -1; // - 1 is not possessed
    
    @Override
    protected void entityInit()
    {
    	entityCreatureEntityInit();
    }
    
    protected void entityCreatureEntityInit()
    {
    	// Added to alow calling entityInit() directly regardless of class hierarchy
    	
        super.entityInit();
        
        if ( getCanCreatureTypeBePossessed() )
        {
        	dataWatcher.addObject(IS_POSSESSED_DATA_WATCHER_ID, new Byte((byte)0 ));
        }
    }
    
    @Override
    public void writeEntityToNBT( NBTTagCompound tag )
    {
        super.writeEntityToNBT( tag );
        
        if ( getCanCreatureTypeBePossessed() )
        {
	        tag.setInteger("fcPossessionTimer", possessionTimer);
	        
	        tag.setByte( "fcPossessionLevel", (byte) getPossessionLevel());
        }
    }

    @Override
    public void readEntityFromNBT( NBTTagCompound tag )
    {
        super.readEntityFromNBT( tag );
        
        if ( getCanCreatureTypeBePossessed() )
        {
	        if ( tag.hasKey( "fcPossessionTimer" ) )
	    	{
                possessionTimer = tag.getInteger("fcPossessionTimer");
	        
	        	if (possessionTimer >= 0 )
	        	{
	        		setPossessionLevel(1);
	        	}
	        	else
	        	{
	        		setPossessionLevel(0);
	        	}
	    	}
	        else
	        {
                possessionTimer = -1;
	        	
	    		setPossessionLevel(0);
	        }
	        
	        if ( tag.hasKey( "fcPossessionLevel" ) )
	        {
	        	setPossessionLevel(tag.getByte("fcPossessionLevel"));
	        }
        }
    }

    @Override
    protected void modSpecificOnLivingUpdate()
    {
    	super.modSpecificOnLivingUpdate();
    	
        if ( getCanCreatureTypeBePossessed() )
        {
        	handlePossession();
        }
    }

    public boolean getCanCreatureTypeBePossessed()
    {
    	return false;
    }

    public boolean getCanCreatureBePossessedFromDistance(boolean bPersistentSpirit)
    {
        return getCanCreatureTypeBePossessed() && isEntityAlive() && !isPossessed();
    }

    public boolean isPossessed()
    {
    	return getCanCreatureTypeBePossessed() && dataWatcher.getWatchableObjectByte(IS_POSSESSED_DATA_WATCHER_ID) != 0;
    }

    public boolean isFullyPossessed()
    {
    	return getCanCreatureTypeBePossessed() && dataWatcher.getWatchableObjectByte(IS_POSSESSED_DATA_WATCHER_ID) > 1;
    }
    
    public void setPossessionLevel(int iLevel)
    {    	
        if ( getCanCreatureTypeBePossessed() )
        {
	    	byte byteValue = (byte)iLevel;
	    	
	        dataWatcher.updateObject(IS_POSSESSED_DATA_WATCHER_ID, Byte.valueOf(byteValue));
        }
    }

    public int getPossessionLevel()
    {    	
        if ( getCanCreatureTypeBePossessed() )
        {
        	return (int)( dataWatcher.getWatchableObjectByte(IS_POSSESSED_DATA_WATCHER_ID) );
        }
        
        return 0;
    }

    public int getInitialPossessionChance()
    {
    	return 1000;    	
    }

    public int getTimeToFullPossession()
    {
    	return 2400 + worldObj.rand.nextInt( 2400 ); // 2 to 4 minutes    	
    }

    public void onInitialPossession()
    {
        worldObj.playSoundAtEntity( this, getDeathSound(), getSoundVolume(), (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F );
        
        worldObj.playAuxSFX( BTWEffectManager.GHAST_MOAN_EFFECT_ID,
        	MathHelper.floor_double( posX ), MathHelper.floor_double( posY ), MathHelper.floor_double( posZ ), 0 );            
    }

    public void onFullPossession()
    {
    }
    
    public void initiatePossession()
    {
		setPossessionLevel(1);

        possessionTimer = getTimeToFullPossession();
		
		onInitialPossession();
    }
    
    protected void handlePossession()
    {
    	if ( worldObj.getWorldInfo().getGameType() == EnumGameType.CREATIVE )
    	{
    		return;
    	}
    	
        if ( !worldObj.isRemote )
        {
	    	if ( !isPossessed() )
	    	{
	    		if ( worldObj.provider.dimensionId == -1 ) // is nether
	    		{
	    			if (worldObj.rand.nextInt(getInitialPossessionChance()) == 0 )
	    			{
	    				initiatePossession();
	    			}
	    		}
	    	}
	    	else if ( !isChild() ) // children can be possessed but it won't take full effect until they grow up
	    	{
	    		if (getPossessionLevel() == 1 )
	    		{
		    		possessionTimer--;
		    		
		    		if (possessionTimer < 0 )
		    		{
                        possessionTimer = 0;
		    		}
		    		
		    		if (possessionTimer == 0 )
		    		{
		    			setPossessionLevel(2);
		    			
		    			onFullPossession();
		    		}
	    		}
	    	}
        }
    }

    public boolean attemptToPossessNearbyCreature(double dRange, boolean bPersistentSpirit)
    {    	
        List nearbyCreatures = worldObj.getEntitiesWithinAABB( EntityCreature.class, boundingBox.expand( dRange, dRange, dRange ) );
        
        Iterator itemIterator = nearbyCreatures.iterator();
    	
        while ( itemIterator.hasNext() )
        {
    		EntityCreature tempCreature = (EntityCreature)itemIterator.next();
    		
	        if (tempCreature.getCanCreatureBePossessedFromDistance(bPersistentSpirit) && tempCreature != this )
	        {
	        	tempCreature.initiatePossession();
        		
        		return true;
	        }	        
        }
        
    	return false;
    }

    public static int attemptToPossessCreaturesAroundBlock(World world, int i, int j, int k, int iPossessionCount, int iCubicRange)
    {
		AxisAlignedBB possessionBox = AxisAlignedBB.getAABBPool().getAABB( 
			(double)( i - iCubicRange ), (double)( j - iCubicRange ), (double)( k - iCubicRange ),
			(double)( i + 1 + iCubicRange ), (double)( j + 1 + iCubicRange ), (double)( k + 1 + iCubicRange ) );
		
        List nearbyCreatures = world.getEntitiesWithinAABB( EntityCreature.class, possessionBox );
        
        Iterator creatureIterator = nearbyCreatures.iterator();
    	
        while ( creatureIterator.hasNext() && iPossessionCount > 0 )
        {
        	EntityCreature tempCreature = (EntityCreature)creatureIterator.next();
    		
	        if ( tempCreature.getCanCreatureBePossessedFromDistance(false) )
	        {
	        	tempCreature.initiatePossession();
	        	
	        	iPossessionCount--;        		
	        }        	        
        }    
        
        return iPossessionCount;
    }
    
    protected void attemptToPossessNearbyCreatureOnDeath()
    {
		attemptToPossessNearbyCreature(16D, false);
    }
    
    @Override
    public void onDeath( DamageSource source )
    {
    	super.onDeath( source );
    	
    	// if a possessed creature dies, the spirits attempt to possess another nearby
    	
    	if (!worldObj.isRemote && isPossessed() )
    	{
    		if ( riddenByEntity == null || !riddenByEntity.onPossessedRidingEntityDeath() )
    		{
    			attemptToPossessNearbyCreatureOnDeath();
    		}
    	}
    }
    
    protected boolean shouldContinueAttacking(float fDistanceToTarget)
    {
    	return true;
    }

    protected void transmitAttackTargetToClients()
    {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream( byteStream );
        
        try
        {
	        dataStream.writeInt( entityId );
	        dataStream.writeByte( (byte) EntityEventPacketHandler.SET_ATTACK_TARGET_EVENT_ID);

	        if ( entityToAttack != null )
	        {
	        	dataStream.writeInt( entityToAttack.entityId );
	        }
	        else
	        {
	        	dataStream.writeInt( -1 );
	        }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }        
	        
        Packet250CustomPayload packet = new Packet250CustomPayload(BTWPacketManager.CUSTOM_ENTITY_EVENT_PACKET_CHANNEL, byteStream.toByteArray() );
        
        WorldUtils.sendPacketToAllPlayersTrackingEntity((WorldServer)worldObj, this, packet);
    }
    // END FCMOD
	public boolean canSoulAffectEntity(Entity soulEntity) {
		//TODO using this method cause it checks have nothing to do with distance..rename it?
		if (getCanCreatureBePossessedFromDistance(true)) {
			initiatePossession();
			return true;
		}
		
		return false;
	}
	
}
