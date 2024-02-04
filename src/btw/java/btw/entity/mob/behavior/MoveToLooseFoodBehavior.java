// FCMOD

package btw.entity.mob.behavior;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.EntityAIBase;
import net.minecraft.src.EntityAnimal;
import net.minecraft.src.EntityItem;

import java.util.Iterator;
import java.util.List;

public class MoveToLooseFoodBehavior extends EntityAIBase
{
    protected EntityAnimal myAnimal;
    
    protected float moveSpeed;
    
    private EntityItem temptingItem = null;

    private static final int UPDATES_BETWEEN_CHECKS = 20; // updates are only every 3 ticks
    	
    private int delayBetweenChecksCount = UPDATES_BETWEEN_CHECKS;
    private boolean failedToPath = false;
    
    public MoveToLooseFoodBehavior(EntityAnimal animal, float fMoveSpeed )
    {
        myAnimal = animal;
        moveSpeed = fMoveSpeed;
        
        setMutexBits( 3 );
    }

    @Override
    public boolean shouldExecute()
    {
    	boolean bReturnValue = false;
    	    	
        if (delayBetweenChecksCount <= 0 )
        {
        	// slight variance to avoid all animals syncing up

            delayBetweenChecksCount = UPDATES_BETWEEN_CHECKS +
                                      myAnimal.rand.nextInt(3) - 1;
        	
        	if ( myAnimal.isReadyToEatLooseFood() )
        	{
			    List<EntityItem> entityList = myAnimal.worldObj.getEntitiesWithinAABB(
			    	EntityItem.class, AxisAlignedBB.getAABBPool().getAABB(
                                myAnimal.posX - 10F, myAnimal.posY - 7F, myAnimal.posZ - 10F,
                                myAnimal.posX + 10F, myAnimal.posY + 7F, myAnimal.posZ + 10F));
			    
			    if ( !entityList.isEmpty() )
			    {
	        		double dClosestDistSq = 0D;
	        		temptingItem = null;
	        		
			    	Iterator<EntityItem> entityIterator = entityList.iterator();

			    	while ( entityIterator.hasNext() )
		            {
			    		EntityItem tempEntity = entityIterator.next();
			    		
				        if (tempEntity.isEntityAlive() &&
                            myAnimal.isReadyToEatLooseItem(tempEntity.getEntityItem()) )
				        {
			        		double dTempDistSq = myAnimal.getDistanceSqToEntity(
			        			tempEntity);
			        		
			        		if ( temptingItem == null || dTempDistSq < dClosestDistSq )
			        		{
			        			temptingItem = tempEntity;
			        			
			        			dClosestDistSq = dTempDistSq;
				        		
				        		bReturnValue = true;
			        		}
			    		}
		            }
		        }
        	}
        }
        else
        {
        	delayBetweenChecksCount--;
        }
        
        return bReturnValue;
    }

    @Override
    public boolean continueExecuting()
    {
    	return !failedToPath && temptingItem != null && temptingItem.isEntityAlive() &&
               myAnimal.isReadyToEatLooseItem(temptingItem.getEntityItem());
    }
    
    public void updateTask()
    {
        myAnimal.getLookHelper().setLookPositionWithEntity(temptingItem, 30F,
                                                           myAnimal.getVerticalFaceSpeed());
        
        if ( isWithinEatBox() )
        {
        	// just hang out in the eat box
        	
            myAnimal.getNavigator().clearPathEntity();
        }
        else if ( !myAnimal.getNavigator().tryMoveToEntity(temptingItem, moveSpeed) )
        {
            failedToPath = true;
        }
    }

    @Override
    public void resetTask()
    {
        temptingItem = null;
        failedToPath = false;
        myAnimal.getNavigator().clearPathEntity();
    }
    
    public boolean isWithinEatBox()
    {
    	// slightly smaller than the actual eat box to ensure we move within it
    	
    	AxisAlignedBB eatBox = AxisAlignedBB.getAABBPool().getAABB(
                myAnimal.boundingBox.minX - 1.45F,
                myAnimal.boundingBox.minY - 0.95F,
                myAnimal.boundingBox.minZ - 1.45F,
                myAnimal.boundingBox.maxX + 1.45F,
                myAnimal.boundingBox.maxY + 0.95F,
                myAnimal.boundingBox.maxZ + 1.45F);
    	
    	return eatBox.intersectsWith( temptingItem.boundingBox );
    }
}
