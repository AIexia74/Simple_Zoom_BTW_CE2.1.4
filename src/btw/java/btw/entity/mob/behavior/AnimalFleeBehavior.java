// FCMOD

package btw.entity.mob.behavior;

import net.minecraft.src.*;

public class AnimalFleeBehavior extends EntityAIBase
{
    private EntityAnimal theAnimal;
    
    private float speed;
    
    private double targetPosX;
    private double targetPosY;
    private double targetPosZ;

    public AnimalFleeBehavior(EntityAnimal animal, float fSpeed )
    {
        this.theAnimal = animal;
        this.speed = fSpeed;
        
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	Vec3 targetVec = null;
    	
    	if ( theAnimal.isBurning() )
    	{
            targetVec = RandomPositionGenerator.findRandomTarget(theAnimal, 5, 4);
    	}
    	else if (theAnimal.getAITarget() != null )
        {
    		targetVec = RandomPositionGenerator.findRandomTargetBlockAwayFrom(theAnimal, 5, 4,
                                                                              theAnimal.worldObj.getWorldVec3Pool().getVecFromPool(theAnimal.getAITarget().posX, theAnimal.getAITarget().posY, theAnimal.getAITarget().posZ));
        }
    	
    	if ( targetVec != null )
    	{
            this.targetPosX = targetVec.xCoord;
            this.targetPosY = targetVec.yCoord;
            this.targetPosZ = targetVec.zCoord;
            
            return true;
    	}
        
        return false;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.theAnimal.getNavigator().tryMoveToXYZ(this.targetPosX, this.targetPosY, this.targetPosZ, this.speed);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
    	if (!this.theAnimal.getNavigator().noPath() && theAnimal.getAITarget() != null )
    	{
    		EntityLiving aiTarget = theAnimal.getAITarget();
    		
    		if ( aiTarget == null )
    		{    			
    			// this is the case if the animal is burning and just running about in panic
    			
    			return true;
    		}
    		else
    		{
    			double dDistanceSqToTarget = theAnimal.getDistanceSq(targetPosX, targetPosY, targetPosZ);

    			// choose another target if we're already close to our chosen one
    			
    			if ( dDistanceSqToTarget > 4D )
    			{
		    		// only continue running in this direction if the enemy is closer to the target destination than we are to the enemy
    				// (Are we on the other side of the target destination from the enemy?)
		    		// this prevents animals running in circles when they overshoot their target.
	    			
	    			double dDistanceSqToAttacker = theAnimal.getDistanceSqToEntity(aiTarget);
	    			double dDistanceSqAttackerToTarget = aiTarget.getDistanceSq(targetPosX, targetPosY, targetPosZ);
	    			
	    			if ( dDistanceSqToAttacker < dDistanceSqAttackerToTarget )
	    			{
	    				return true;
	    			}
    			}
    		}
    	}
    	
        return false;
    }
}
