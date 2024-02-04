package btw.block.tileentity.beacon;

public class AmbientBeaconEffect extends BeaconEffect{
	public final String EFFECT_NAME;
	
	public AmbientBeaconEffect(String effectName) {
		this.EFFECT_NAME = effectName;
	}
	
	@Override
	public void onUpdate(BeaconTileEntity beacon) {
	
	}
	
	@Override
	public void onPowerChange(int newPowerLevel, int oldPowerLevel, BeaconTileEntity beacon) {
		super.onPowerChange(newPowerLevel, oldPowerLevel, beacon);
		
		if (!beacon.worldObj.isRemote) {
			updateAmbientBeaconListForStateChange(newPowerLevel, oldPowerLevel, beacon);
		}
	}
	
	protected void updateAmbientBeaconListForStateChange(int newPowerLevel, int oldPowerLevel, BeaconTileEntity beacon) {
		if (newPowerLevel <= 0) {
			beacon.worldObj.getAmbientBeaconLocationList()
					.removePointAt(beacon.xCoord, beacon.yCoord, beacon.zCoord);
		}
		else if (oldPowerLevel <= 0) {
			beacon.worldObj.getAmbientBeaconLocationList()
					.addPoint(beacon.xCoord, beacon.yCoord, beacon.zCoord, newPowerLevel, (int) rangePerLevel[newPowerLevel], EFFECT_NAME);
		}
		else if (oldPowerLevel != newPowerLevel) {
			beacon.worldObj.getAmbientBeaconLocationList()
					.changeEffectLevelOfPointAt(beacon.xCoord, beacon.yCoord, beacon.zCoord, newPowerLevel, (int) rangePerLevel[newPowerLevel]);
		}
	}
}
