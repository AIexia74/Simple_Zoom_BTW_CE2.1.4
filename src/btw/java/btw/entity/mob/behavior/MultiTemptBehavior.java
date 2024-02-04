// FCMOD

package btw.entity.mob.behavior;

import net.minecraft.src.EntityAIBase;
import net.minecraft.src.EntityAnimal;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

public class MultiTemptBehavior extends EntityAIBase
{
    protected EntityAnimal myAnimal;
    
    protected float moveSpeed;
    
    private EntityPlayer temptingPlayer;

    private int delayBetweenCounter;
    
    private boolean doesAnimalNormallyAvoidWater;

    public MultiTemptBehavior(EntityAnimal animal, float fMoveSpeed )
    {
        delayBetweenCounter = 0;

        myAnimal = animal;
        moveSpeed = fMoveSpeed;
        
        setMutexBits( 3 );
    }

    public boolean shouldExecute()
    {
        if (delayBetweenCounter <= 0 )
        {
	        temptingPlayer = myAnimal.worldObj.getClosestPlayerToEntity(myAnimal, 10D);
	
	        if ( temptingPlayer != null )
	        {
		        ItemStack itemstack = temptingPlayer.getCurrentEquippedItem();
		
		        if ( itemstack != null )
		        {
			        return myAnimal.isTemptingItem(itemstack);
		        }		
	        }
        }
        else
        {
            delayBetweenCounter--;
        }
        
        return false;
    }

    public void startExecuting()
    {
        doesAnimalNormallyAvoidWater = myAnimal.getNavigator().getAvoidsWater();
        myAnimal.getNavigator().setAvoidsWater(false);
    }

    public void resetTask()
    {
        temptingPlayer = null;
        myAnimal.getNavigator().clearPathEntity();
        delayBetweenCounter = 33; // note that AI is only checked every 3 ticks
        
        myAnimal.getNavigator().setAvoidsWater(doesAnimalNormallyAvoidWater);
    }

    public void updateTask()
    {
        myAnimal.getLookHelper().setLookPositionWithEntity(temptingPlayer, 30F,
                                                           myAnimal.getVerticalFaceSpeed());

        if (myAnimal.getDistanceSqToEntity(temptingPlayer) < 6.25D )
        {
            myAnimal.getNavigator().clearPathEntity();
        }
        else
        {
            myAnimal.getNavigator().tryMoveToEntityLiving(temptingPlayer, moveSpeed);
        }
    }
}
