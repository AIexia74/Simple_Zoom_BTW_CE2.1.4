// FCMOD

package btw.entity.mob.behavior;

import net.minecraft.src.EntityAIBase;
import net.minecraft.src.EntityCreature;
import net.minecraft.src.RandomPositionGenerator;
import net.minecraft.src.Vec3;

public class PanicOnHeadCrabBehavior extends EntityAIBase
{
	private EntityCreature owningEntity;
	private float moveSpeed;
	
    private double randPosX;
    private double randPosY;
    private double randPosZ;
    
    public PanicOnHeadCrabBehavior(EntityCreature entity, float fMoveSpeed )
    {
        owningEntity = entity;

        moveSpeed = fMoveSpeed;
        
        setMutexBits( 1 );
    }
    
    public boolean shouldExecute()
    {
    	if ( owningEntity.hasHeadCrabbedSquid() )
        {
            Vec3 randPos = RandomPositionGenerator.findRandomTarget(owningEntity, 5, 4);

            if ( randPos != null )
            {
                randPosX = randPos.xCoord;
                randPosY = randPos.yCoord;
                randPosZ = randPos.zCoord;
                
                return true;
            }
        }
    	
    	return false;
    }

    public void startExecuting()
    {
        owningEntity.getNavigator().tryMoveToXYZ(randPosX, randPosY, randPosZ, moveSpeed);
    }

    public boolean continueExecuting()
    {
        return !owningEntity.getNavigator().noPath() && owningEntity.hasHeadCrabbedSquid();
    }
}
