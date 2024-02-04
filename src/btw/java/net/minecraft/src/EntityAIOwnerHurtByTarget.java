package net.minecraft.src;

public class EntityAIOwnerHurtByTarget extends EntityAITarget
{
    EntityTameable theDefendingTameable;
    EntityLiving theOwnerAttacker;

    public EntityAIOwnerHurtByTarget(EntityTameable par1EntityTameable)
    {
        super(par1EntityTameable, 32.0F, false);
        this.theDefendingTameable = par1EntityTameable;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	// FCMOD: Code added to check if sitting before attacking    	
    	if ( theDefendingTameable.isSitting() )
    	{
    		return false;
    	}
    	// END FCMOD
        if (!this.theDefendingTameable.isTamed())
        {
            return false;
        }
        else
        {
            EntityLiving var1 = this.theDefendingTameable.getOwner();

            if (var1 == null)
            {
                return false;
            }
            else
            {
                this.theOwnerAttacker = var1.getAITarget();
                return this.isSuitableTarget(this.theOwnerAttacker, false);
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.theOwnerAttacker);
        super.startExecuting();
    }

    // FCMOD: Inherited function added
    @Override
    public boolean continueExecuting()
    {
    	if ( theDefendingTameable.isSitting() )
    	{
    		return false;
    	}
    	
    	return super.continueExecuting();
    }
    // END FCMOD
}
