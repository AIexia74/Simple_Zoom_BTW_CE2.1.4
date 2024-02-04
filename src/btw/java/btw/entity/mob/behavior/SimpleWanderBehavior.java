// FCMOD: A simplified version of EntityAIWander that uses int destinations to avoid multiple
// typecasts, and which ignores the entities "home" position entirely, since most creatures
// don't even have one.

package btw.entity.mob.behavior;

import net.minecraft.src.EntityAIBase;
import net.minecraft.src.EntityCreature;
import btw.world.util.BlockPos;
import btw.util.RandomPositionGenerator;

public class SimpleWanderBehavior extends EntityAIBase
{
    private EntityCreature myEntity;
    
    private float moveSpeed;
    
    protected BlockPos destPos = new BlockPos();

    public SimpleWanderBehavior(EntityCreature entity, float fMoveSpeed )
    {
        myEntity = entity;
        moveSpeed = fMoveSpeed;
        
        setMutexBits( 1 );
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (myEntity.getRNG().nextInt(120) == 0 &&
            RandomPositionGenerator.findSimpleRandomTargetBlock(myEntity,
                                                                10, 7, destPos) )
        {
            return true;
        }
        
        return false;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !myEntity.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
    	myEntity.getNavigator().tryMoveToXYZ(destPos.x, destPos.y, destPos.z, moveSpeed);
    }
}
