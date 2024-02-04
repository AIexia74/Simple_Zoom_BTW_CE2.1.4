// FCMOD

package btw.world.structure;

import btw.entity.mob.BlazeEntity;
import btw.entity.mob.MagmaCubeEntity;
import btw.entity.mob.ZombiePigmanEntity;
import btw.entity.mob.SkeletonEntity;
import net.minecraft.src.MapGenNetherBridge;
import net.minecraft.src.SpawnListEntry;
import net.minecraft.src.StructureBoundingBox;
import net.minecraft.src.StructureStart;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class NetherBridgeMapGen extends MapGenNetherBridge
{
    protected List mobSpawnList = new ArrayList();

    public NetherBridgeMapGen()
    {
    	super();
    	
    	mobSpawnList.add(new SpawnListEntry(BlazeEntity.class, 10, 2, 3 ));
    	mobSpawnList.add(new SpawnListEntry(ZombiePigmanEntity.class, 5, 4, 4 ));
    	mobSpawnList.add(new SpawnListEntry(SkeletonEntity.class, 10, 4, 4 ));
    	mobSpawnList.add(new SpawnListEntry(MagmaCubeEntity.class, 3, 4, 4 ));
    }
    
    @Override
    public List getSpawnList()
    {
        return mobSpawnList;
    }
    
    //------------- Class Specific Methods ------------//
    
    public boolean hasStructureAtLoose(int i, int j, int k)
    {
        Iterator structureIterator = structureMap.values().iterator();

        while ( structureIterator.hasNext() )
        {
            StructureStart tempStructure = (StructureStart)structureIterator.next();
            
            StructureBoundingBox box = tempStructure.getBoundingBox();
            
            if ( tempStructure.isSizeableStructure() && box.intersectsWith( i, k, i, k ) )
            {
            	// test j value as intersection test does not
            	
            	if ( j >= box.minY && j <= box.maxY )
            	{            		
            		return true;
            	}
            }
        }

        return false;
    }
    
    public StructureStart getClosestStructureWithinRangeSq(double xPos, double zPos, double dRangeSq)
    {
    	StructureStart closestStructure = null;
    	double dClosestDistSq = dRangeSq;
    	
        Iterator structureIterator = structureMap.values().iterator();

        while ( structureIterator.hasNext() )
        {
            StructureStart tempStructure = (StructureStart)structureIterator.next();
            
            StructureBoundingBox box = tempStructure.getBoundingBox();

            double dStructXPos = box.getCenterX();
            double dStructZPos = box.getCenterZ();
            
            double dDeltaX = xPos - dStructXPos;
            double dDeltaZ = zPos - dStructZPos;
            
            double dTempDistSq = ( dDeltaX * dDeltaX ) + ( dDeltaZ * dDeltaZ );
            
            if ( dTempDistSq < dClosestDistSq )
            {
            	closestStructure = tempStructure;
            	dClosestDistSq = dTempDistSq;
            }
        }
        
        return closestStructure;
    }
    
	//----------- Client Side Functionality -----------//
}
