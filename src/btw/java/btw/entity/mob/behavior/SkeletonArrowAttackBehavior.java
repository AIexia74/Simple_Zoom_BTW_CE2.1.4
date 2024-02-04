// FCMOD

package btw.entity.mob.behavior;

import net.minecraft.src.EntityAIBase;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.IRangedAttackMob;

public class SkeletonArrowAttackBehavior extends EntityAIBase
{
    private final EntityLiving entityOwner;
    private final IRangedAttackMob entityRangedAttackOwner;

    private EntityLiving entityAttackTarget;

    private int attackCooldownCounter;
    private float entityMoveSpeed;
    
    private int canSeeTargetCounter;
    private int minRangedAttackTime;

    private int attackInterval;
    
    private double attackRange;
    private double attackRangeSq;

	public SkeletonArrowAttackBehavior(IRangedAttackMob rangedAttackMob, float fMoveSpeed, int iAttackInterval, float fAttackRange)
	{
        canSeeTargetCounter = 0;

        entityRangedAttackOwner = rangedAttackMob;
        entityOwner = (EntityLiving)rangedAttackMob;
        entityMoveSpeed = fMoveSpeed;
        attackInterval = iAttackInterval;
        attackCooldownCounter = iAttackInterval >> 1;
        attackRange = fAttackRange;
        attackRangeSq = fAttackRange * fAttackRange;
        
        setMutexBits(3);
    }

    public boolean shouldExecute()
    {
        EntityLiving target = entityOwner.getAttackTarget();

        if ( target == null )
        {
            return false;
        }
        else
        {
            entityAttackTarget = target;
            
            return true;
        }
    }

    public boolean continueExecuting()
    {
        return this.shouldExecute() || !entityOwner.getNavigator().noPath();
    }

    public void resetTask()
    {
        entityAttackTarget = null;
        canSeeTargetCounter = 0;
        attackCooldownCounter = attackInterval;
    }

    public void updateTask()
    {
        double dDistSqToTarget = entityOwner.getDistanceSq(entityAttackTarget.posX, entityAttackTarget.boundingBox.minY, entityAttackTarget.posZ);
        
        boolean bCanSeeTarget = entityOwner.getEntitySenses().canSee(entityAttackTarget);

        if ( bCanSeeTarget )
        {
            ++canSeeTargetCounter;
        }
        else
        {
            canSeeTargetCounter = 0;
        }

        if ( dDistSqToTarget <= (double) attackRangeSq && canSeeTargetCounter >= 20 )
        {
            entityOwner.getNavigator().clearPathEntity();
        }
        else
        {
            entityOwner.getNavigator().tryMoveToEntityLiving(entityAttackTarget, entityMoveSpeed);
        }

        entityOwner.getLookHelper().setLookPositionWithEntity(entityAttackTarget, 30.0F, 30.0F);
        
        if (attackCooldownCounter > 1 )
        {
        	attackCooldownCounter--;
        }
        else
        {
            if ( dDistSqToTarget <= (double) attackRangeSq && bCanSeeTarget )
            {
            	entityRangedAttackOwner.attackEntityWithRangedAttack(entityAttackTarget, 1F);
                attackCooldownCounter = attackInterval;
            }
        }   
    }
}
