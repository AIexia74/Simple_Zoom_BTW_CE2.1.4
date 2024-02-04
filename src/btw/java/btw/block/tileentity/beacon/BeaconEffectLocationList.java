// FCMOD

package btw.block.tileentity.beacon;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BeaconEffectLocationList
{
	public List effectLocations;
	
	public BeaconEffectLocationList()
	{
		effectLocations = new ArrayList();
	}
	
    public void loadFromNBT( NBTTagList tagList )
    {
    	effectLocations.clear();
    	
        for ( int iTempCount = 0; iTempCount < tagList.tagCount(); ++iTempCount )
        {
            NBTTagCompound tempCompound = (NBTTagCompound)tagList.tagAt( iTempCount );
            
            BeaconEffectLocation newPoint = new BeaconEffectLocation( tempCompound );
            
            effectLocations.add(newPoint);
        }
    }
    
    public NBTTagList saveToNBT()
    {
        NBTTagList tagList = new NBTTagList( "EffectLocations" );
        
    	Iterator tempIterator = effectLocations.iterator();

    	while( tempIterator.hasNext() )
    	{
            NBTTagCompound tempTagCompound = new NBTTagCompound();
            
            BeaconEffectLocation tempPoint = (BeaconEffectLocation)tempIterator.next();
    		
            tempPoint.writeToNBT(tempTagCompound);
            
            tagList.appendTag( tempTagCompound );
    	}
    	
        return tagList;
    }
    
    public void removePointAt(int iIPos, int iJPos, int iKPos)
    {
    	Iterator tempIterator = effectLocations.iterator();
    	
    	while ( tempIterator.hasNext() )
    	{
    		BeaconEffectLocation tempPoint = (BeaconEffectLocation)tempIterator.next();
    		
    		if (tempPoint.posX == iIPos && tempPoint.posZ == iKPos && tempPoint.posY == iJPos )
    		{
    			tempIterator.remove();
    			
    			return;
    		}
    	}
    }
    
    public void addPoint(int posX, int posY, int posZ, int effectLevel, int range, String effectID)
    {
    	BeaconEffectLocation newPoint = new BeaconEffectLocation( posX, posY, posZ, effectLevel, range, effectID );
        
        effectLocations.add(newPoint);
    }
    
    public void changeEffectLevelOfPointAt(int posX, int posY, int posZ, int powerLevel, int range)
    {
    	BeaconEffectLocation point = getEffectAtLocation(posX, posY, posZ);
		
		if ( point != null )
		{
			point.effectLevel = powerLevel;
			point.range = range;
		}    	
    }
    
    public BeaconEffectLocation getEffectAtLocation(int posX, int posY, int posZ)
    {
	
		for (Object effectLocation : effectLocations) {
			BeaconEffectLocation tempPoint = (BeaconEffectLocation) effectLocation;
		
			if (tempPoint.posX == posX && tempPoint.posZ == posZ && tempPoint.posY == posY) {
				return tempPoint;
			}
		}
    	
    	return null;
    }
    
    public int getMostPowerfulBeaconEffectForLocation(String effectID, int iIPos, int iKPos)
    {
    	int maxLevel = 0;
	
		for (Object effectLocation : effectLocations) {
			BeaconEffectLocation point = (BeaconEffectLocation) effectLocation;
			
			if(point.effectID == effectID) {
				if (iIPos >= point.posX - point.range && iIPos <= point.posX + point.range && iKPos >= point.posZ - point.range &&
						iKPos <= point.posZ + point.range) {
					if (point.effectLevel > maxLevel) {
						maxLevel = point.effectLevel;
					}
				}
			}
		}
    	
    	return maxLevel;
    }
}
