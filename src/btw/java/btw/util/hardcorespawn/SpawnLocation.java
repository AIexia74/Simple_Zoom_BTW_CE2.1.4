// FCMOD

package btw.util.hardcorespawn;

import net.minecraft.src.NBTTagCompound;

public class SpawnLocation
{
	public int posX;
	public int posY;
	public int posZ;
	public long spawnTime;
	
	public SpawnLocation()
	{
		posX = 0;
		posY = 0;
		posZ = 0;
		spawnTime = 0;
	}
	
	public SpawnLocation(int iIPos, int iJPos, int iKPos, long iSpawnTime )
	{
		posX = iIPos;
		posY = iJPos;
		posZ = iKPos;
		spawnTime = iSpawnTime;
	}
	
	public SpawnLocation(NBTTagCompound tagCompound )
	{
		loadFromNBT(tagCompound);
	}
	
	public void loadFromNBT(NBTTagCompound tagCompound)
	{
		posX = tagCompound.getInteger("IPos");
		posY = tagCompound.getShort("JPos");
		posZ = tagCompound.getInteger("KPos");
		spawnTime = tagCompound.getLong("SpawnTime");
	}
	
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound)
    {
    	tagCompound.setInteger("IPos", posX);
    	tagCompound.setShort( "JPos", (short) posY);
    	tagCompound.setInteger("KPos", posZ);
    	tagCompound.setLong("SpawnTime", spawnTime);

        return tagCompound;
    }
}