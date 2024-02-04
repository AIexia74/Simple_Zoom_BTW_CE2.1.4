package net.minecraft.src;

import java.util.Iterator;
import java.util.List;

public class EntityAIHurtByTarget extends EntityAITarget
{
    private boolean nearbyEntitiesOfSameTypeAttack;

    /** The PathNavigate of our entity. */
    // FCNOTE: This is actually the target entity
    EntityLiving entityPathNavigate;

    public EntityAIHurtByTarget(EntityLiving par1EntityLiving, boolean par2)
    {
        super(par1EntityLiving, 16.0F, false);
        this.nearbyEntitiesOfSameTypeAttack = par2;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return this.isSuitableTarget(this.taskOwner.getAITarget(), false);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return taskOwner.getAITarget() != null &&
    		taskOwner.getAITarget().isEntityAlive() && 
        	taskOwner.getAITarget() == entityPathNavigate && 
        	taskOwner.getAttackTarget() != null &&
        	taskOwner.getAttackTarget() == entityPathNavigate;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.taskOwner.getAITarget());
        this.entityPathNavigate = this.taskOwner.getAITarget();

        if (this.nearbyEntitiesOfSameTypeAttack)
        {
            List var1 = this.taskOwner.worldObj.getEntitiesWithinAABB(this.taskOwner.getClass(), AxisAlignedBB.getAABBPool().getAABB(this.taskOwner.posX, this.taskOwner.posY, this.taskOwner.posZ, this.taskOwner.posX + 1.0D, this.taskOwner.posY + 1.0D, this.taskOwner.posZ + 1.0D).expand((double)this.targetDistance, 10.0D, (double)this.targetDistance));
            Iterator var2 = var1.iterator();

            while (var2.hasNext())
            {
                EntityLiving var3 = (EntityLiving)var2.next();

                if (this.taskOwner != var3 && var3.getAttackTarget() == null && var3.getAITarget() == null )
                {
                	var3.setRevengeTarget( taskOwner.getAITarget() );
                }
            }
        }

        super.startExecuting();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
    	if ( taskOwner.getAttackTarget() != null && entityPathNavigate == taskOwner.getAttackTarget() )
    	{
            taskOwner.setAttackTarget((EntityLiving)null);
    	}
    	
    	if ( taskOwner.getAITarget() != null && entityPathNavigate == taskOwner.getAITarget() )
    	{
    		taskOwner.setRevengeTarget( null );
    	}
    }
}
