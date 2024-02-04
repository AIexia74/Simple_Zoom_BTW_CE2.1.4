// FCMOD

package btw.entity.util;

import net.minecraft.src.Entity;

public class ClosestEntityInfo
{
	public double sourcePosX;
	public double sourcePosY;
	public double sourcePosZ;
	
	public double closestDistanceSq;
	public Entity closestEntity;
	
	public ClosestEntitySelectionCriteria criteria;
	
	public int chunkEntityListMinVerticalIndex;
	public int chunkEntityListMaxVerticalIndex;
	
	public ClosestEntityInfo
	( 
		double dSourcePosX, 
		double dSourcePosY, 
		double dSourcePosZ, 
		double dClosestDistanceSq, 
		Entity closestEntity, 
		ClosestEntitySelectionCriteria criteria,
		int iChunkEntityListMinVerticalIndex, 
		int iChunkEntityListMaxVerticalIndex
	)
	{
		sourcePosX = dSourcePosX;
		sourcePosY = dSourcePosY;
		sourcePosZ = dSourcePosZ;
		closestDistanceSq = dClosestDistanceSq;
		this.closestEntity = closestEntity;
		this.criteria = criteria;
		chunkEntityListMinVerticalIndex = iChunkEntityListMinVerticalIndex;
		chunkEntityListMaxVerticalIndex = iChunkEntityListMaxVerticalIndex;
	}
}
