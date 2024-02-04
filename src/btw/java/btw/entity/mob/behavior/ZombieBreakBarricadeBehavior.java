// FCMOD

package btw.entity.mob.behavior;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

public class ZombieBreakBarricadeBehavior extends EntityAIBase
{
    private EntityLiving associatedEntity;
    
    private int doorPosX;
    private int doorPosY;
    private int doorPosZ;
    
    private Block targetBlock;
    
    private int breakingTime;
    
    private int field_75358_j = -1;

    public ZombieBreakBarricadeBehavior(EntityLiving par1EntityLiving)
    {
        associatedEntity = par1EntityLiving;
    }

    @Override
    public boolean shouldExecute()
    {
        if ( associatedEntity.isCollidedHorizontally )
        {
            PathNavigate pathNavigate = associatedEntity.getNavigator();
            PathEntity path = pathNavigate.getPath();

            if ( path != null && !path.isFinished() && pathNavigate.getCanBreakDoors() )
            {
                for ( int iTempPathIndex = 0; iTempPathIndex < Math.min( path.getCurrentPathIndex() + 2, path.getCurrentPathLength() ); ++iTempPathIndex )
                {
                    PathPoint tempPathPoint = path.getPathPointFromIndex( iTempPathIndex );
                    
                    if (associatedEntity.getDistanceSq(tempPathPoint.xCoord, associatedEntity.posY, tempPathPoint.zCoord) <= 2.25D )
                    {
                        doorPosX = tempPathPoint.xCoord;
                        doorPosY = tempPathPoint.yCoord + 1;
                        doorPosZ = tempPathPoint.zCoord;

                        targetBlock = shouldBreakBarricadeAtPos(associatedEntity.worldObj, doorPosX, doorPosY, doorPosZ);

                        if (targetBlock == null )
                        {
                            doorPosY = tempPathPoint.yCoord;

                            targetBlock = shouldBreakBarricadeAtPos(associatedEntity.worldObj, doorPosX, doorPosY, doorPosZ);

                            if (targetBlock == null )
                            {
                                doorPosY = MathHelper.floor_double(associatedEntity.posY + 1);

                                targetBlock = shouldBreakBarricadeAtPos(associatedEntity.worldObj, doorPosX, doorPosY, doorPosZ);
                            	
                                if (targetBlock == null )
                                {
                                    doorPosY = MathHelper.floor_double(associatedEntity.posY);

                                    targetBlock = shouldBreakBarricadeAtPos(associatedEntity.worldObj, doorPosX, doorPosY, doorPosZ);
                                }
                            }
                        }
                        
                        
                        if (targetBlock != null )
                        {
                            return true;
                        }                        
                        
                    }
                    else
                    {
                    	break;
                    }
                }

                doorPosX = MathHelper.floor_double(associatedEntity.posX);
                doorPosY = MathHelper.floor_double(associatedEntity.posY + 1.0D);
                doorPosZ = MathHelper.floor_double(associatedEntity.posZ);

                targetBlock = shouldBreakBarricadeAtPos(associatedEntity.worldObj, doorPosX, doorPosY, doorPosZ);
                
                if (targetBlock == null )
                {
                    doorPosY = MathHelper.floor_double(associatedEntity.posY);

                    targetBlock = shouldBreakBarricadeAtPos(associatedEntity.worldObj, doorPosX, doorPosY, doorPosZ);
                }
                
                if (targetBlock != null )
                {
                	return true;
                }
            }
        }
        
        return false;
    }

    @Override
    public void startExecuting()
    {
        breakingTime = 0;
    }

    @Override
    public boolean continueExecuting()
    {
    	if ( breakingTime > 240 || associatedEntity.worldObj.getBlockId(doorPosX, doorPosY, doorPosZ) != targetBlock.blockID )
    	{
    		return false;
    	}
    	
        double dDistSqToDoor = associatedEntity.getDistanceSq((double) doorPosX, (double) doorPosY, (double) doorPosZ);
        
        return dDistSqToDoor < 4.0D;
    }

    @Override
    public void resetTask()
    {
        super.resetTask();
        
        associatedEntity.worldObj.destroyBlockInWorldPartially(associatedEntity.entityId, doorPosX, doorPosY, doorPosZ, -1);
    }

    @Override
    public void updateTask()
    {
        if (associatedEntity.getRNG().nextInt(20) == 0 )
        {
            associatedEntity.worldObj.playAuxSFX(1010, doorPosX, doorPosY, doorPosZ, 0);
        }

        ++breakingTime;
        
        int iModifiedBreakTime = (int)( (float)breakingTime / 240.0F * 10.0F );

        if ( iModifiedBreakTime != field_75358_j )
        {
            associatedEntity.worldObj.destroyBlockInWorldPartially(associatedEntity.entityId, doorPosX, doorPosY, doorPosZ, iModifiedBreakTime);
            field_75358_j = iModifiedBreakTime;
        }

        if ( breakingTime == 240 )
        {
        	int iMetadata = associatedEntity.worldObj.getBlockMetadata(doorPosX, doorPosY, doorPosZ);
        	
            associatedEntity.worldObj.setBlockToAir(doorPosX, doorPosY, doorPosZ);
            
            if (targetBlock.blockID != Block.doorWood.blockID && targetBlock.blockID != BTWBlocks.woodenDoor.blockID )
            {
            	targetBlock.dropBlockAsItem(associatedEntity.worldObj, doorPosX, doorPosY, doorPosZ, iMetadata, 0);
            }
            
            associatedEntity.worldObj.playAuxSFX(1012, doorPosX, doorPosY, doorPosZ, 0);
            associatedEntity.worldObj.playAuxSFX(2001, doorPosX, doorPosY, doorPosZ, targetBlock.blockID);
        }
    }
    
    //------------- Class Specific Methods ------------//
    
    private Block shouldBreakBarricadeAtPos(World world, int i, int j, int k)
    {
        int iBlockID = world.getBlockId( i, j, k );

        if ( iBlockID != 0 )
        {
        	Block block = Block.blocksList[iBlockID];
        	
        	if ( block.isBreakableBarricade(world, i, j, k) /*&& !block.IsBreakableBarricadeOpen( world, i, j, k )*/  )
        	{
        		return block;
        	}        	
        }
        
        return null;
    }
}
