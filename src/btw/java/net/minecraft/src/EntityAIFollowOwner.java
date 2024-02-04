package net.minecraft.src;

public class EntityAIFollowOwner extends EntityAIBase
{
    private EntityTameable thePet;
    private EntityLiving theOwner;
    World theWorld;
    private float field_75336_f;
    private PathNavigate petPathfinder;
    private int field_75343_h;
    float maxDist;
    float minDist;
    private boolean field_75344_i;

    public EntityAIFollowOwner(EntityTameable par1EntityTameable, float par2, float par3, float par4)
    {
        this.thePet = par1EntityTameable;
        this.theWorld = par1EntityTameable.worldObj;
        this.field_75336_f = par2;
        this.petPathfinder = par1EntityTameable.getNavigator();
        this.minDist = par3;
        this.maxDist = par4;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLiving var1 = this.thePet.getOwner();

        if (var1 == null)
        {
            return false;
        }
        else if (this.thePet.isSitting())
        {
            return false;
        }
        else if (this.thePet.getDistanceSqToEntity(var1) < (double)(this.minDist * this.minDist))
        {
            return false;
        }
        else
        {
            this.theOwner = var1;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.petPathfinder.noPath() && this.thePet.getDistanceSqToEntity(this.theOwner) > (double)(this.maxDist * this.maxDist) && !this.thePet.isSitting();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_75343_h = 0;
        this.field_75344_i = this.thePet.getNavigator().getAvoidsWater();
        this.thePet.getNavigator().setAvoidsWater(false);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.theOwner = null;
        this.petPathfinder.clearPathEntity();
        this.thePet.getNavigator().setAvoidsWater(this.field_75344_i);
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        this.thePet.getLookHelper().setLookPositionWithEntity(this.theOwner, 10.0F, (float)this.thePet.getVerticalFaceSpeed());

        // FCMOD: Code added to prevent pets sitting in Minecarts from teleporting
        if ( thePet.ridingEntity != null )
        {
        	return;
        }
        // END FCMOD
        
        if (!this.thePet.isSitting())
        {
            if (--this.field_75343_h <= 0)
            {
                this.field_75343_h = 10;

                if (!this.petPathfinder.tryMoveToEntityLiving(this.theOwner, this.field_75336_f))
                {
                    if (this.thePet.getDistanceSqToEntity(this.theOwner) >= 144.0D)
                    {
				        handleTeleportation();
                    }
                }
            }
        }
    }
    
    // FCMOD: Added
    private void handleTeleportation()
    {
    	if ( !thePet.isAITryingToSit() )
    	{
	        // Code changed so that pets will teleport *behind* the player instead of just randomly around him
	        Float xVector = ( MathHelper.sin((theOwner.rotationYaw / 180F) * (float)Math.PI) * 4.0F );
	        Float zVector = -( MathHelper.cos((theOwner.rotationYaw / 180F) * (float)Math.PI) * 4.0F );
	
	        int var1 = MathHelper.floor_double(theOwner.posX + xVector);
	        int var2 = MathHelper.floor_double(theOwner.posZ + zVector);
	        int var3 = MathHelper.floor_double(theOwner.boundingBox.minY);
	        
	        // Changed this so that the searching starts at the center of the location
	        
	        for (int xCount = 0; xCount <= 4; xCount++)
	        {
	            for (int zCount = 0; zCount <= 4; zCount++)
	            {
	            	int xOffset = ( xCount + 1 ) >> 1;
	            	int zOffset = ( zCount + 1 ) >> 1;
	        
	        		if ( ( xCount & 1 ) == 0 )
	        		{
	        			xOffset = -xOffset;
	        		}
	        		
	        		if ( ( zOffset & 1 ) == 0 )
	        		{
	        			zOffset = -zOffset;
	        		}
	        		
	                if ( theWorld.doesBlockHaveSolidTopSurface( var1 + xOffset, var3 - 1, var2 + zOffset) && 
	            		!theWorld.isBlockNormalCube(var1 + xOffset, var3, var2 + zOffset) && !theWorld.isBlockNormalCube(var1 + xOffset, var3 + 1, var2 + zOffset))
	                {
	                    thePet.setLocationAndAngles((float)(var1 + xOffset) + 0.5F, var3, (float)(var2 + zOffset) + 0.5F, thePet.rotationYaw, thePet.rotationPitch);
	                    petPathfinder.clearPathEntity();
	                    return;
	                }
	            }
	        }
    	}
    }
    // END FCMOD
}
