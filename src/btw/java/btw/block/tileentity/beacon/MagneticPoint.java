// FCMOD

package btw.block.tileentity.beacon;

import net.minecraft.src.NBTTagCompound;

public class MagneticPoint
{
	static private double[] fieldStrengthMultipliersByLevel = new double[] {0D, 1D, 8D, 27D, 64D, 125D, 216D, 343D, 4096D };
	static private double[] maxRangeSquaredForLevelWithNoise = new double[] {0D, 100D, 400D, 1600D, 6400, 25600, 102400, 409600, Double.POSITIVE_INFINITY };
		
	public int posX;
	public int posY;
	public int posZ;
	public int fieldLevel;
	
	public MagneticPoint()
	{
		posX = 0;
		posY = 0;
		posZ = 0;
		fieldLevel = 0;
	}
	
	public MagneticPoint(int iIPos, int iJPos, int iKPos, int iFieldLevel )
	{
		posX = iIPos;
		posY = iJPos;
		posZ = iKPos;
		fieldLevel = iFieldLevel;
	}
	
	public MagneticPoint(NBTTagCompound tagCompound )
	{
		loadFromNBT(tagCompound);
	}
	
	public void loadFromNBT(NBTTagCompound tagCompound)
	{
		posX = tagCompound.getInteger("IPos");
		posY = tagCompound.getShort("JPos");
		posZ = tagCompound.getInteger("KPos");
		fieldLevel = tagCompound.getByte("Lvl");
	}
	
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound)
    {
    	tagCompound.setInteger("IPos", posX);
    	tagCompound.setShort( "JPos", (short) posY);
    	tagCompound.setInteger("KPos", posZ);
		tagCompound.setByte( "Lvl", (byte) fieldLevel);

        return tagCompound;
    }
    
    public double getFieldStrengthRelativeToPosition(double dRelativeX, double dRelativeZ)
    {
    	double dDeltaX = (double) posX - dRelativeX;
    	double dDeltaZ = (double) posZ - dRelativeZ;
    	
    	double dDistanceSq = dDeltaX * dDeltaX + dDeltaZ * dDeltaZ;
    	
    	return fieldStrengthMultipliersByLevel[fieldLevel] / dDistanceSq;
    }
    
    public double getFieldStrengthRelativeToPositionWithBackgroundNoise(double dRelativeX, double dRelativeZ)
    {
    	double dDeltaX = (double) posX - dRelativeX;
    	double dDeltaZ = (double) posZ - dRelativeZ;
    	
    	double dDistanceSq = dDeltaX * dDeltaX + dDeltaZ * dDeltaZ;
    	
    	if (dDistanceSq <= maxRangeSquaredForLevelWithNoise[fieldLevel] )
    	{    	
    		return fieldStrengthMultipliersByLevel[fieldLevel] / dDistanceSq;
    	}
    	else
    	{
    		return -1D;
    	}
    }
}