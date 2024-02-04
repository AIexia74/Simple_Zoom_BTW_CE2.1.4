// FCMOD

package btw.entity.mob.behavior;

import btw.entity.mob.WolfEntity;
import net.minecraft.src.EntityAINearestAttackableTarget;

public class WildWolfTargetIfStarvingOrHostileBehavior extends EntityAINearestAttackableTarget
{
    private WolfEntity associatedWolf;
    
    public WildWolfTargetIfStarvingOrHostileBehavior(WolfEntity wolf, Class targetClass, float fTargetRange, int iChanceOfTargeting, boolean bCheckLineOfSight )
    {
        super( wolf, targetClass, fTargetRange, iChanceOfTargeting, bCheckLineOfSight );

        associatedWolf = wolf;
    }

    @Override
    public boolean continueExecuting()
    {
    	if ( !associatedWolf.isWildAndHostile() )
    	{
    		return false;
    	}
    	
    	return super.continueExecuting();
    }
    
    @Override
    public boolean shouldExecute()
    {
    	if ( !associatedWolf.isWildAndHostile() )
    	{
    		return false;
    	}
    	
        return super.shouldExecute();
    }
}
