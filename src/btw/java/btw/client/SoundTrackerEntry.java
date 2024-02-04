// FCMOD

package btw.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SoundTrackerEntry
{
	public String name;
	public float posX;
	public float posY;
	public float posZ;
	public float maxRangeSq;
	
	public SoundTrackerEntry(String sName, float fXPos, float fYPos, float fZPos, float fMaxRange )
	{
		name = sName;
		posX = fXPos;
		posY = fYPos;
		posZ = fZPos;
		maxRangeSq = fMaxRange * fMaxRange;
	}
}