// FCMOD

package btw.entity.mob.behavior;

import btw.block.blocks.GroundCoverBlock;
import net.minecraft.src.Block;
import net.minecraft.src.EntityAIBase;
import net.minecraft.src.EntityAnimal;
import btw.world.util.BlockPos;

public class GrazeBehavior extends EntityAIBase
{
    private EntityAnimal myAnimal;
    
    // used by chickens to prevent OCD pecking    
    private int grazeCooldown = 0;

    public GrazeBehavior(EntityAnimal entity )
    {
        myAnimal = entity;
        setMutexBits( 7 );
    }

    @Override
    public boolean shouldExecute()
    {
    	if (grazeCooldown > 0 )
    	{
    		grazeCooldown--;
    		
    		return false;
    	}
    	
    	if ( myAnimal.isSubjectToHunger() )
    	{
    		return myAnimal.isHungryEnoughToGraze() && myAnimal.getGrazeBlockForPos() != null;
    	}
    	else
    	{
            return myAnimal.getRNG().nextInt(myAnimal.isChild() ? 50 : 1000) == 0 &&
                   myAnimal.getGrazeBlockForPos() != null;
    	}
    }
    
    @Override
    public void startExecuting()
    {
        grazeCooldown = 10;

        myAnimal.grazeProgressCounter = myAnimal.getGrazeDuration();
        myAnimal.worldObj.setEntityState(myAnimal, (byte)10);
        myAnimal.getNavigator().clearPathEntity();
    }

    @Override
    public void resetTask()
    {
        myAnimal.grazeProgressCounter = 0;
    }

    @Override
    public boolean continueExecuting()
    {
        return myAnimal.grazeProgressCounter > 0;
    }

    @Override
    public void updateTask()
    {
        myAnimal.grazeProgressCounter = Math.max(0,
                                                 myAnimal.grazeProgressCounter - 1);

        if (myAnimal.grazeProgressCounter == 4 )
        {
            BlockPos targetPos = myAnimal.getGrazeBlockForPos();

            if ( targetPos != null )
            {
                myAnimal.onGrazeBlock(targetPos.x, targetPos.y, targetPos.z);
                
            	GroundCoverBlock.clearAnyGroundCoverRestingOnBlock(myAnimal.worldObj,
																   targetPos.x, targetPos.y, targetPos.z);
            	
            	if ( myAnimal.shouldNotifyBlockOnGraze() )
            	{
            		int iTargetBlockID = myAnimal.worldObj.getBlockId(
							targetPos.x, targetPos.y, targetPos.z);
            		
            		if ( iTargetBlockID != 0 )
            		{
		            	myAnimal.playGrazeFX(targetPos.x, targetPos.y, targetPos.z,
											 iTargetBlockID);
            			
		            	Block.blocksList[iTargetBlockID].onGrazed(myAnimal.worldObj,
																  targetPos.x, targetPos.y, targetPos.z, myAnimal);
            		}
            	}
            }            
        }
    }
    
    //------------- Class Specific Methods ------------//	
}
