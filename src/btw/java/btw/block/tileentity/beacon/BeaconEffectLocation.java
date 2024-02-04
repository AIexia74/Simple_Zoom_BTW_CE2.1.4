// FCMOD

package btw.block.tileentity.beacon;

import net.minecraft.src.NBTTagCompound;

public class BeaconEffectLocation
{
	public int posX;
	public int posY;
	public int posZ;
	public int effectLevel;
	public int range;
	String effectID;
	
	public BeaconEffectLocation()
	{
		posX = 0;
		posY = 0;
		posZ = 0;
		effectLevel = 0;
		range = 0;
	}
	
	public BeaconEffectLocation(int posX, int posY, int posZ, int effectLevel, int range, String effectID)
	{
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.effectLevel = effectLevel;
		this.range = range;
		this.effectID = effectID;
	}
	
	public BeaconEffectLocation(NBTTagCompound tagCompound )
	{
		loadFromNBT(tagCompound);
	}
	
	public void loadFromNBT(NBTTagCompound tagCompound)
	{
		posX = tagCompound.getInteger("IPos");
		posY = tagCompound.getShort("JPos");
		posZ = tagCompound.getInteger("KPos");
		effectLevel = tagCompound.getByte("Lvl");
		range = tagCompound.getInteger("Rng");
		if (tagCompound.getString("effectID") != null) {
			effectID = tagCompound.getString("effectID");
		}
		// for compatibility with worlds before this change
		else effectID = BeaconTileEntity.LOOTING_EFFECT.EFFECT_NAME;
	}
	
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound)
    {
    	tagCompound.setInteger("IPos", posX);
    	tagCompound.setShort( "JPos", (short) posY);
    	tagCompound.setInteger("KPos", posZ);
		tagCompound.setByte( "Lvl", (byte) effectLevel);
    	tagCompound.setInteger("Rng", range);
		tagCompound.setString("effectID", effectID);
		
        return tagCompound;
    }    
}