// FCMOD

package btw.entity.mob.behavior;

import btw.entity.mob.WolfEntity;
import net.minecraft.src.EntityAINearestAttackableTarget;

public class WildWolfTargetIfStarvingBehavior extends EntityAINearestAttackableTarget
{
    private WolfEntity associatedWolf;
    
    public WildWolfTargetIfStarvingBehavior(WolfEntity wolf, Class targetClass, float fTargetRange, int iChanceOfTargeting, boolean bCheckLineOfSight )
    {
        super( wolf, targetClass, fTargetRange, iChanceOfTargeting, bCheckLineOfSight );

        associatedWolf = wolf;
    }

    @Override
    public boolean continueExecuting()
    {
    	if ( !associatedWolf.isWildAndStarving() )
    	{
    		return false;
    	}
    	
    	return super.continueExecuting();
    }
    
    @Override
    public boolean shouldExecute()
    {
    	if ( !associatedWolf.isWildAndStarving() )
    	{
    		return false;
    	}
    	
        return super.shouldExecute();
    }
}