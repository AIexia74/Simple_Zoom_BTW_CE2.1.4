// FCMOD

package btw.entity.util;

import net.minecraft.src.Entity;

public class ClosestEntitySelectionCriteria
{
	public static ClosestEntitySelectionCriteria secondarySquidTarget = new ClosestEntitySelectionCriteriaSquidSecondaryTarget();
	
    public void processEntity(ClosestEntityInfo closestEntityInfo, Entity entity)
    {
    	double dDeltaX = entity.posX - closestEntityInfo.sourcePosX;
    	double dDeltaY = entity.posY - closestEntityInfo.sourcePosY;
    	double dDeltaZ = entity.posZ - closestEntityInfo.sourcePosZ;
    	
    	double dDistSq = ( dDeltaX * dDeltaX ) + ( dDeltaY * dDeltaY ) + ( dDeltaZ * dDeltaZ );
    	
    	if ( dDistSq < closestEntityInfo.closestDistanceSq)
    	{
    		closestEntityInfo.closestEntity = entity;
    		closestEntityInfo.closestDistanceSq = dDistSq;
    	}
	}
}
