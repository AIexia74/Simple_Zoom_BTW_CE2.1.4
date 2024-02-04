package btw.block.tileentity.beacon;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Potion;
import net.minecraft.src.PotionEffect;

import java.util.Iterator;

public class NauseaBeaconEffect extends BeaconEffect {
	
	@Override
	public void onUpdate(BeaconTileEntity beacon) {
		if (!beacon.worldObj.isRemote) {
			applyDungCloudToPlayersInRange(beacon);
		}
	}
	
	private void applyDungCloudToPlayersInRange(BeaconTileEntity beacon) {
		double range = rangePerLevel[beacon.getLevels()];
		
		Iterator playerIterator = beacon.worldObj.playerEntities.iterator();
		
		while (playerIterator.hasNext()) {
			EntityPlayer player = (EntityPlayer) playerIterator.next();
			
			double deltaX = Math.abs(beacon.xCoord - player.posX);
			
			if (deltaX <= range) {
				double deltaZ = Math.abs(beacon.zCoord - player.posZ);
				
				if (deltaZ <= range) {
					if (!player.isDead && !player.capabilities.isCreativeMode && !player.isWearingFullSuitSoulforgedArmor()) {
						player.addPotionEffect(new PotionEffect(Potion.confusion.getId(), EFFECT_DURATION, 0, true));
						player.addPotionEffect(new PotionEffect(Potion.poison.getId(), EFFECT_DURATION, 0, true));
					}
				}
			}
		}
	}
}
