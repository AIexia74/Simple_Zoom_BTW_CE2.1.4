package btw.block.tileentity.beacon;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.PotionEffect;

public abstract class BeaconEffect {
	
	public static final int EFFECT_DURATION = 180;
	
	public static final double[] rangePerLevel = new double[]{0D, 20D, 40D, 80D, 160D};
	
	/**
	 * Only gets called when beaconEffectClass != null, isOn() and getLevels() > 0
	 */
	public abstract void onUpdate(BeaconTileEntity beacon);
	
	public void onPowerOn(BeaconTileEntity beacon) {
		if(beacon.worldObj.isRemote) {
			beacon.worldObj.playSound(beacon.xCoord + 0.5D, beacon.yCoord + 0.5D, beacon.zCoord + 0.5D,
					"mob.wither.spawn", 1.0F + (beacon.worldObj.rand.nextFloat() * 0.1F), // volume
					1.0F + (beacon.worldObj.rand.nextFloat() * 0.1F));    // pitch
		}
	}
	
	public void onPowerOff(BeaconTileEntity beacon) {
		if (beacon.worldObj.isRemote) {
			beacon.worldObj.playSound(beacon.xCoord + 0.5D, beacon.yCoord + 0.5D, beacon.zCoord + 0.5D,
					"mob.wither.death", 1.0F + (beacon.worldObj.rand.nextFloat() * 0.1F), // volume
					1.0F + (beacon.worldObj.rand.nextFloat() * 0.1F));    // pitch
		}
	}
	
	public void onPowerChange(int newPowerLevel, int oldPowerLevel, BeaconTileEntity beacon) {
		if (!beacon.worldObj.isRemote) {
			// check for changes in state which will affect the magnetic point list
			updateGlobalMagneticFieldListForStateChange(newPowerLevel, oldPowerLevel, beacon);
		}
	}
	
	protected void applyPotionEffectToPlayersInRange(int effectID, int effectLevel, BeaconTileEntity beacon) {
		if(beacon.updatedPowerState) {
			double range = rangePerLevel[beacon.getLevels()];
			
			for (Object o : beacon.worldObj.playerEntities) {
				EntityPlayer player = (EntityPlayer) o;
				
				double deltaX = Math.abs(beacon.xCoord - player.posX);
				
				if (deltaX <= range) {
					double deltaZ = Math.abs(beacon.zCoord - player.posZ);
					
					if (deltaZ <= range) {
						if (!player.isDead) {
							player.addPotionEffect(new PotionEffect(effectID, EFFECT_DURATION, effectLevel, true));
						}
					}
				}
			}
		}
	}
	
	protected void updateGlobalMagneticFieldListForStateChange(int newPowerLevel, int oldPowerLevel, BeaconTileEntity beacon) {
		if (newPowerLevel <= 0) {
			beacon.worldObj.getMagneticPointList().removePointAt(beacon.xCoord, beacon.yCoord, beacon.zCoord);
		}
		else if (oldPowerLevel <= 0) {
			beacon.worldObj.getMagneticPointList().addPoint(beacon.xCoord, beacon.yCoord, beacon.zCoord, getMagneticFieldLevel(newPowerLevel));
		}
		else if (oldPowerLevel != newPowerLevel) {
			beacon.worldObj.getMagneticPointList().changePowerLevelOfPointAt(beacon.xCoord, beacon.yCoord, beacon.zCoord, getMagneticFieldLevel(newPowerLevel));
		}
	}
	
	protected int getMagneticFieldLevel(int powerLevel) {
		return powerLevel;
	}
	
}
