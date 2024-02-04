// FCMOD

package btw.entity.mob;

import btw.BTWMod;
import btw.item.BTWItems;
import net.minecraft.src.*;

public class GhastEntity extends EntityGhast
{
    private static final long PLAYER_SWITCH_DIMENSIONS_GRACE_PERIOD = 600; // 30 seconds
    
    private static final double MAX_FIREBALL_LAUNCH_RANGE = 64D;
    private static final double MAX_FIREBALL_LAUNCH_RANGE_SQ = (MAX_FIREBALL_LAUNCH_RANGE * MAX_FIREBALL_LAUNCH_RANGE);
    
    private static final double FIREBALL_SPAWN_DIST_FROM_GHAST = 4D;

    private static final int FIREBALL_EXPLOSION_POWER = 1;
    
    private Entity entityTargeted = null;
    private int retargetCountdown = 0;
	
    public GhastEntity(World world )
    {
        super( world );
    }

    @Override
    public int getMaxHealth()
    {
        return 20;
    }
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.worldObj.isRemote) {
			updateAttackStateClient();
		}
	}
	
	private void updateAttackStateClient() {
		// Sanity checking. If state is 0, attackCounter is at most 10 on server. If state is 1, attackCounter is at least 11 on server.
		byte state = this.dataWatcher.getWatchableObjectByte(16);
		if (state == 0 && this.attackCounter > 10) {
			this.attackCounter = 10;
		} else if (state == 1 && this.attackCounter <= 10) {
			this.attackCounter = 11;
		}
		prevAttackCounter = attackCounter;
		
		if (entityTargeted == null || !entityTargeted.isEntityAlive() || retargetCountdown-- <= 0 )
		{
			entityTargeted = worldObj.getClosestVulnerablePlayerToEntity(this, 100D);
			
			// Give the player a grace period when they first entering the nether
			
			if (entityTargeted != null)
			{
				EntityPlayer targetPlayer = (EntityPlayer) entityTargeted;
				
				long lTargetChangedDimensionTime = targetPlayer.timeOfLastDimensionSwitch;
				long lWorldTime = worldObj.getWorldTime();
				
				if ( lWorldTime > lTargetChangedDimensionTime &&
						lWorldTime - lTargetChangedDimensionTime <= PLAYER_SWITCH_DIMENSIONS_GRACE_PERIOD &&
						targetPlayer != entityLivingToAttack )
				{
					entityTargeted = null;
				}
			}
			
			if (entityTargeted != null )
			{
				retargetCountdown = 20;
			}
		}
		
		if (entityTargeted != null && entityTargeted.getDistanceSqToEntity(this) < MAX_FIREBALL_LAUNCH_RANGE_SQ)
		{
			double dTargetDeltaX = entityTargeted.posX - posX;
			double dTargetDeltaZ = entityTargeted.posZ - posZ;
			
			renderYawOffset = rotationYaw = -(float)( Math.atan2( dTargetDeltaX, dTargetDeltaZ ) * 180D / Math.PI );
			
			if ( canEntityBeSeen(entityTargeted) )
			{
				attackCounter++;
				
				if ( attackCounter == 20 )
				{
					attackCounter = -40;
				}
			}
			else if ( attackCounter > 0 )
			{
				attackCounter--;
			}
		}
		else
		{
			renderYawOffset = rotationYaw = -(float)( Math.atan2( motionX, motionZ) * 180D / Math.PI );
			
			if ( attackCounter > 0 )
			{
				attackCounter--;
			}
		}
	}
    
    @Override
    protected void updateEntityActionState()
    {
        entityAge++;        
        despawnEntity();
        
        prevAttackCounter = attackCounter;
        
        double dWaypointDeltaX = waypointX - posX;
        double dWaypointDeltaY = waypointY - posY;
        double dWaypointDeltaZ = waypointZ - posZ;
        
        double dWaypointDistSq = dWaypointDeltaX * dWaypointDeltaX + dWaypointDeltaY * dWaypointDeltaY + dWaypointDeltaZ * dWaypointDeltaZ;

        if ( dWaypointDistSq < 1D || dWaypointDistSq > 3600D )
        {
            waypointX = posX + ( ( rand.nextDouble() * 2D - 1D ) * 16D );
            waypointY = posY + ( ( rand.nextDouble() * 2D - 1D ) * 16D );
            waypointZ = posZ + ( ( rand.nextDouble() * 2D - 1D ) * 16D );
        }

        if ( courseChangeCooldown-- <= 0 )
        {
            courseChangeCooldown += rand.nextInt( 5 ) + 2;
            
            double dWaypointDist = MathHelper.sqrt_double( dWaypointDistSq );

            if ( isCourseTraversable(waypointX, waypointY, waypointZ, dWaypointDist) )
            {
                motionX += dWaypointDeltaX / dWaypointDist * 0.1D;
                motionY += dWaypointDeltaY / dWaypointDist * 0.1D;
                motionZ += dWaypointDeltaZ / dWaypointDist * 0.1D;
            }
            else
            {
                waypointX = posX;
                waypointY = posY;
                waypointZ = posZ;
            }
        }

        if (entityTargeted == null || !entityTargeted.isEntityAlive() || retargetCountdown-- <= 0 )
        {
            entityTargeted = worldObj.getClosestVulnerablePlayerToEntity(this, 100D);

            // Give the player a grace period when they first entering the nether
            
            if (entityTargeted != null && entityTargeted instanceof EntityPlayer)
            {
            	EntityPlayer targetPlayer = (EntityPlayer) entityTargeted;
            	
                long lTargetChangedDimensionTime = targetPlayer.timeOfLastDimensionSwitch;
                long lWorldTime = worldObj.getWorldTime();
                
                if ( lWorldTime > lTargetChangedDimensionTime && 
                	lWorldTime - lTargetChangedDimensionTime <= PLAYER_SWITCH_DIMENSIONS_GRACE_PERIOD &&
                	targetPlayer != entityLivingToAttack )
                {
                    entityTargeted = null;
                }
            }
            
            if (entityTargeted != null )
            {
                retargetCountdown = 20;
            }
        }

        if (entityTargeted != null && entityTargeted.getDistanceSqToEntity(this) < MAX_FIREBALL_LAUNCH_RANGE_SQ)
        {
            double dTargetDeltaX = entityTargeted.posX - posX;
            double dTargetDeltaZ = entityTargeted.posZ - posZ;
            
            double dTargetDeltaY = (entityTargeted.boundingBox.minY + (double)(entityTargeted.height / 2F ) ) -
                                   ( posY + (double)( height / 2F ) );
            
            renderYawOffset = rotationYaw = -(float)( Math.atan2( dTargetDeltaX, dTargetDeltaZ ) * 180D / Math.PI );

            if ( canEntityBeSeen(entityTargeted) )
            {
                if ( attackCounter == 10 )
                {
                    worldObj.playAuxSFXAtEntity( null, 1007, (int)posX, (int)posY, (int)posZ, 0 );
                }

                attackCounter++;

                if ( attackCounter == 20 )
                {
                	fireAtTarget();
                }
            }
            else if ( attackCounter > 0 )
            {
                attackCounter--;
            }
        }
        else
        {
            renderYawOffset = rotationYaw = -(float)( Math.atan2( motionX, motionZ) * 180D / Math.PI );

            if ( attackCounter > 0 )
            {
                attackCounter--;
            }
        }

        if ( !worldObj.isRemote )
        {
        	// this updates whether the ghast's mouth is rendered as open for attack
        	
            boolean bMouthOpen = dataWatcher.getWatchableObjectByte( 16 ) != 0;
            boolean bShouldMouthBeOpen = attackCounter > 10;

            if ( bMouthOpen != bShouldMouthBeOpen )
            {
            	setMouthOpen(bShouldMouthBeOpen);
            }
        }
    }
    
    @Override
    protected double minDistFromPlayerForDespawn()
    {
    	return 144D;
    }
    
    @Override
    public void checkForScrollDrop()
    {    	
    	if ( rand.nextInt( 500 ) == 0 )
    	{
            ItemStack itemstack = new ItemStack( BTWItems.arcaneScroll, 1, Enchantment.punch.effectId );
            
            entityDropItem(itemstack, 0.0F);
    	}
    }
    
    @Override
    public int getTalkInterval()
    {
        return 80 + rand.nextInt( 480 ); // default is 80
    }
    
	@Override
    public boolean canEntityBeSeen( Entity entity )
	{
		// override to compute line of sight from the center of the ghast
		
        return worldObj.rayTraceBlocks_do_do( worldObj.getWorldVec3Pool().getVecFromPool( posX, posY + (double)height / 2D, posZ), 
        	worldObj.getWorldVec3Pool().getVecFromPool( entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ ), false, true ) == null;
	}
	
    @Override
    public boolean doesEntityApplyToSquidPossessionCap()
    {
    	return isEntityAlive() && getIsPersistent();
    }

    @Override
    protected int getDropItemId()
    {
        return Item.fireballCharge.itemID;
    }
    
    @Override
    protected void dropFewItems( boolean bKilledByPlayer, int iFortuneModifier )
    {
        int iNumDrops = rand.nextInt( 2 ) + rand.nextInt( 1 + iFortuneModifier );

        for ( int iTempCount = 0; iTempCount < iNumDrops; ++iTempCount )
        {
            dropItem( Item.ghastTear.itemID, 1 );
        }

        iNumDrops = rand.nextInt( 3 ) + rand.nextInt( 1 + iFortuneModifier );

        for ( int iTempCount = 0; iTempCount < iNumDrops; ++iTempCount )
        {
            dropItem( Item.fireballCharge.itemID, 1 );
        }
    }

    @Override
    protected float getSoundVolume()
    {
		// note that fireball launching screams and woosh are auxFX, so they aren't affected by this
		
    	if ( worldObj.provider.dimensionId == -1 )
    	{
    		return 10F;
    	}
    	
        return 3F;
    }
    
    @Override
    public boolean getCanSpawnHere()
    {
        return this.getCanSpawnHereNoPlayerDistanceRestrictions() &&
        	worldObj.getClosestPlayer( posX, posY, posZ, 64D ) == null;
    }

    @Override
    public boolean attractsLightning()
    {
    	return false;
    }
    
	//------------- Class Specific Methods ------------//
    
    private boolean isCourseTraversable(double dDestX, double dDestY, double dDestZ, double dDistToDest)
    {
        if ( dDestY >= 0 && dDestY <= worldObj.getHeight() )
        {        
	        double dDeltaXNorm = ( waypointX - posX ) / dDistToDest;
	        double dDeltaYNorm = ( waypointY - posY ) / dDistToDest;
	        double dDeltaZNorm = ( waypointZ - posZ ) / dDistToDest;
	        
	        AxisAlignedBB tempBox = boundingBox.copy();
	
	        for ( double dTempDist = 1; dTempDist < dDistToDest; dTempDist += 1D )
	        {
	            tempBox.offset( dDeltaXNorm, dDeltaYNorm, dDeltaZNorm );
	
	            if ( !worldObj.getCollidingBoundingBoxes( this, tempBox ).isEmpty() )
	            {
	                return false;
	            }            
	        }
	
	        // avoid destinations containing water or lava
	        
	        if ( worldObj.isAnyLiquid( tempBox )  )
	        {
	        	return false;
	        }
	        
	        return true;
        }
        
    	return false;
    }
    
    public boolean getCanSpawnHereNoPlayerDistanceRestrictions()
    {
        return worldObj.checkNoEntityCollision( boundingBox ) && 
    		worldObj.getCollidingBoundingBoxes( this, boundingBox ).isEmpty() &&
        	!worldObj.isAnyLiquid( boundingBox ) &&
            (this.rand.nextInt(5) == 0 || BTWMod.increaseGhastSpawns);
    }
    
    private void setMouthOpen(boolean bOpen)
    {
    	Byte tempByte = 0;
    	
    	if ( bOpen )
    	{
    		tempByte = 1;
    	}
    	
        dataWatcher.updateObject( 16, Byte.valueOf( tempByte ) );
    }    
    
	private void fireAtTarget()
	{
	    worldObj.playAuxSFXAtEntity( (EntityPlayer)null, 1008, (int)posX, (int)posY, (int)posZ, 0 );
	    
	    Vec3 ghastLookVec = getLook( 1F );
	    
		double dFireballX = posX + ghastLookVec.xCoord;
		double dFireballY = posY + (double)( height / 2F );
		double dFireballZ = posZ + ghastLookVec.zCoord;

        double dDeltaX = entityTargeted.posX - dFireballX;
        double dDeltaY = (entityTargeted.posY + entityTargeted.getEyeHeight() ) - dFireballY;
        double dDeltaZ = entityTargeted.posZ - dFireballZ;
        
	    EntityLargeFireball fireball = (EntityLargeFireball) EntityList.createEntityOfType(EntityLargeFireball.class, worldObj, this, dDeltaX, dDeltaY, dDeltaZ );
	    
	    fireball.field_92057_e = FIREBALL_EXPLOSION_POWER;
	    
	    double dDeltaLength = MathHelper.sqrt_double( dDeltaX * dDeltaX + dDeltaY * dDeltaY + dDeltaZ * dDeltaZ );
	    
	    double dUnitDeltaX = dDeltaX / dDeltaLength; 
	    double dUnitDeltaY = dDeltaY / dDeltaLength; 
	    double dUnitDeltaZ = dDeltaZ / dDeltaLength; 
	    
	    fireball.posX = dFireballX + (dUnitDeltaX * FIREBALL_SPAWN_DIST_FROM_GHAST);
	    fireball.posY = dFireballY + (dUnitDeltaY * FIREBALL_SPAWN_DIST_FROM_GHAST) - (fireball.height / 2D );
	    fireball.posZ = dFireballZ + (dUnitDeltaZ * FIREBALL_SPAWN_DIST_FROM_GHAST);
	    
	    worldObj.spawnEntityInWorld( fireball );
	    
	    attackCounter = -40;
	}

	//----------- Client Side Functionality -----------//    
}
