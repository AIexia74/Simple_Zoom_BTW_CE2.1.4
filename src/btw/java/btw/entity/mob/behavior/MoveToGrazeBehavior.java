// FCMOD

package btw.entity.mob.behavior;

import net.minecraft.src.EntityAIBase;
import net.minecraft.src.EntityAnimal;
import btw.world.util.BlockPos;
import btw.util.RandomPositionGenerator;

public class MoveToGrazeBehavior extends EntityAIBase
{
    private EntityAnimal myAnimal;
    
    private float moveSpeed;
    
    protected BlockPos destPos = new BlockPos();

    public MoveToGrazeBehavior(EntityAnimal entity, float fMoveSpeed )
    {
        myAnimal = entity;
        moveSpeed = fMoveSpeed;
        
        setMutexBits( 1 );
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
		if (myAnimal.isSubjectToHunger() && myAnimal.isHungryEnoughToForceMoveToGraze() )
		{    			
	        return !myAnimal.shouldStayInPlaceToGraze() &&
                   RandomPositionGenerator.findSimpleRandomTargetBlock(myAnimal,
                                                                       10, 7, destPos);
		}
    	
    	return false;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !myAnimal.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
    	myAnimal.getNavigator().tryMoveToXYZ(destPos.x, destPos.y, destPos.z, moveSpeed);
    }
}
