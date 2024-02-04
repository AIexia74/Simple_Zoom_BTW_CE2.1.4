// FCMOD

package btw.block.tileentity.beacon;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MagneticPointList
{
	public List magneticPoints;
	
	public MagneticPointList()
	{
		magneticPoints = new ArrayList();
	}
	
    public void loadFromNBT( NBTTagList tagList )
    {
    	magneticPoints.clear();
    	
        for ( int iTempCount = 0; iTempCount < tagList.tagCount(); ++iTempCount )
        {
            NBTTagCompound tempCompound = (NBTTagCompound)tagList.tagAt( iTempCount );
            
            MagneticPoint newPoint = new MagneticPoint( tempCompound );
            
            magneticPoints.add(newPoint);
        }
    }
    
    public NBTTagList saveToNBT()
    {
        NBTTagList tagList = new NBTTagList( "MagneticPoints" );
        
    	Iterator tempIterator = magneticPoints.iterator();

    	while( tempIterator.hasNext() )
    	{
            NBTTagCompound tempTagCompound = new NBTTagCompound();
            
    		MagneticPoint tempPoint = (MagneticPoint)tempIterator.next();
    		
            tempPoint.writeToNBT(tempTagCompound);
            
            tagList.appendTag( tempTagCompound );
    	}
    	
        return tagList;
    }
    
    public void removePointAt(int iIPos, int iJPos, int iKPos)
    {
    	Iterator tempIterator = magneticPoints.iterator();
    	
    	while ( tempIterator.hasNext() )
    	{
    		MagneticPoint tempPoint = (MagneticPoint)tempIterator.next();
    		
    		if (tempPoint.posX == iIPos && tempPoint.posZ == iKPos && tempPoint.posY == iJPos )
    		{
    			tempIterator.remove();
    			
    			return;
    		}
    	}
    }
    
    public void addPoint(int iIPos, int iJPos, int iKPos, int iPowerLevel)
    {
        MagneticPoint newPoint = new MagneticPoint( iIPos, iJPos, iKPos, iPowerLevel );
        
        magneticPoints.add(newPoint);
    }
    
    public void changePowerLevelOfPointAt(int iIPos, int iJPos, int iKPos, int iPowerLevel)
    {
		MagneticPoint point = getMagneticPointAtLocation(iIPos, iJPos, iKPos);
		
		if ( point != null )
		{
			point.fieldLevel = iPowerLevel;
		}    	
    }
    
    public MagneticPoint getMagneticPointAtLocation(int iIPos, int iJPos, int iKPos)
    {    	
    	Iterator tempIterator = magneticPoints.iterator();
    	
    	while ( tempIterator.hasNext() )
    	{
    		MagneticPoint tempPoint = (MagneticPoint)tempIterator.next();
    		
    		if (tempPoint.posX == iIPos && tempPoint.posZ == iKPos && tempPoint.posY == iJPos )
    		{
    			return tempPoint;
    		}
    	}
    	
    	return null;
    }
}
