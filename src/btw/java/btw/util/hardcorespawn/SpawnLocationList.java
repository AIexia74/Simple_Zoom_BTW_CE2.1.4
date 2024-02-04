// FCMOD

package btw.util.hardcorespawn;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SpawnLocationList
{
	public List spawnLocations;
	
	public SpawnLocationList()
	{
		spawnLocations = new ArrayList();
	}
	
    public void loadFromNBT( NBTTagList tagList )
    {
    	spawnLocations.clear();
    	
        for ( int iTempCount = 0; iTempCount < tagList.tagCount(); ++iTempCount )
        {
            NBTTagCompound tempCompound = (NBTTagCompound)tagList.tagAt( iTempCount );
            
            SpawnLocation newPoint = new SpawnLocation( tempCompound );
            
            spawnLocations.add(newPoint);
        }
    }
    
    public NBTTagList saveToNBT()
    {
        NBTTagList tagList = new NBTTagList( "SpawnLocations" );
        
    	Iterator tempIterator = spawnLocations.iterator();

    	while( tempIterator.hasNext() )
    	{
            NBTTagCompound tempTagCompound = new NBTTagCompound();
            
            SpawnLocation tempPoint = (SpawnLocation)tempIterator.next();
    		
            tempPoint.writeToNBT(tempTagCompound);
            
            tagList.appendTag( tempTagCompound );
    	}
    	
        return tagList;
    }
    
    public void addPoint(int iIPos, int iJPos, int iKPos, long lSpawnTime)
    {
    	SpawnLocation newPoint = new SpawnLocation( iIPos, iJPos, iKPos, lSpawnTime );
        
        spawnLocations.add(newPoint);
    }
    
    public SpawnLocation getClosestSpawnLocationForPosition(double dXPos, double dZPos)
    {
    	SpawnLocation closestLocation = null;
    	double dClosestDistSq = 0;
    	
    	Iterator tempIterator = spawnLocations.iterator();
    	
    	while ( tempIterator.hasNext() )
    	{
    		SpawnLocation tempPoint = (SpawnLocation)tempIterator.next();
    		
    		double dDeltaI = (double)tempPoint.posX - dXPos;
    		double dDeltaK = (double)tempPoint.posZ - dZPos;
    		
    		double dTempDistSq = ( dDeltaI * dDeltaI ) + ( dDeltaK * dDeltaK );
    		
    		if ( closestLocation == null || dTempDistSq < dClosestDistSq )
    		{
    			closestLocation = tempPoint;
    			dClosestDistSq = dTempDistSq;
    		}    		
    	}
    	
    	return closestLocation;
    }
    
    public SpawnLocation getMostRecentSpawnLocation()
    {
    	SpawnLocation mostRecent = null;
    	
    	Iterator tempIterator = spawnLocations.iterator();
    	
    	while ( tempIterator.hasNext() )
    	{
    		SpawnLocation tempPoint = (SpawnLocation)tempIterator.next();
    		
    		if ( mostRecent == null || tempPoint.spawnTime > mostRecent.spawnTime)
    		{
    			mostRecent = tempPoint;
    		}    		
    	}
    	
    	return mostRecent;
    }
    
    public boolean doesListContainPoint(int iIPos, int iJPos, int iKPos, long lSpawnTime)
    {
    	Iterator tempIterator = spawnLocations.iterator();
    	
    	while ( tempIterator.hasNext() )
    	{
    		SpawnLocation tempPoint = (SpawnLocation)tempIterator.next();
    		
    		if (tempPoint.posX == iIPos && tempPoint.posZ == iKPos && tempPoint.posY == iJPos && tempPoint.spawnTime == lSpawnTime )
    		{
    			return true;
    		}
    	}    	
    	
    	return false;
    }
    
    public void addPointIfNotAlreadyPresent(int iIPos, int iJPos, int iKPos, long lSpawnTime)
    {
    	if ( !doesListContainPoint(iIPos, iJPos, iKPos, lSpawnTime) )
		{
    		addPoint(iIPos, iJPos, iKPos, lSpawnTime);
		}    	
    }    
}