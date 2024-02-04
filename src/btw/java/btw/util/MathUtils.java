// FCMOD

package btw.util;

/**
 * Similar to MatherHelper, fills in a few additional functions
 */
public class MathUtils
{
    public static double clampDouble(double dValue, double dBottom, double dTop)
    {
    	if ( dValue < dBottom )
    	{
    		return dBottom;
    	}
    	else if ( dValue > dTop )
    	{
    		return dTop;
    	}
    	
    	return dValue;
    }
    
    public static double clampDoubleTop(double dValue, double dTop)
    {
    	if ( dValue > dTop )
    	{
    		return dTop;
    	}
    	
    	return dValue;
    }
    
    public static double clampDoubleBottom(double dValue, double dBottom)
    {
    	if ( dValue < dBottom )
    	{
    		return dBottom;
    	}
    	
    	return dValue;
    }
    
    public static double absDouble(double dValue)
    {
    	if ( dValue >= 0D )
    	{
    		return dValue;
    	}
    	
    	return -dValue;
    }
}
