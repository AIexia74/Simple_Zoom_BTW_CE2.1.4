// FCMOD

package btw.entity.mob.behavior;

import btw.entity.mob.CreeperEntity;
import net.minecraft.src.EntityAICreeperSwell;

public class CreeperSwellBehavior extends EntityAICreeperSwell
{
    private CreeperEntity myCreeper;

    public CreeperSwellBehavior(CreeperEntity creeper )
    {
    	super( creeper );

		myCreeper = creeper;
    }

    @Override
    public boolean shouldExecute()
    {
    	if (myCreeper.getCreeperState() <= 0 && myCreeper.getNeuteredState() > 0 )
    	{
    		return false;
    	}
    	else if ( myCreeper.getIsDeterminedToExplode() )
    	{
    		return true;
    	}
    	
    	return super.shouldExecute();
    }

    @Override
    public void updateTask()
    {
    	if (myCreeper.getNeuteredState() > 0 )
    	{
    		myCreeper.setCreeperState(-1);
    	}
    	else if (!myCreeper.getIsDeterminedToExplode() &&
				 ( creeperAttackTarget == null || myCreeper.getDistanceSqToEntity(this.creeperAttackTarget) > 36D ||
				   !myCreeper.getEntitySenses().canSee(creeperAttackTarget) ) )
    	{
    		myCreeper.setCreeperState(-1);
    	}    	
        else
        {
        	myCreeper.setCreeperState(1);
        }
    }
}
