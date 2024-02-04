package btw.block.tileentity.beacon;

public class CompanionBeaconEffect extends AmbientBeaconEffect{
	public static int companionStrength = 0;
	
	public CompanionBeaconEffect() {
		super("Companion");
	}
	
	@Override
	public void onUpdate(BeaconTileEntity beacon) {
		super.onUpdate(beacon);
		
		if( beacon.updatedPowerState) {
			updateCompanionStrengthUpwards(beacon);
		}
	}
	
	private void updateCompanionStrengthUpwards(BeaconTileEntity beacon) {
		int power = beacon.getLevels();
		if (power > companionStrength) {
			companionStrength = power;
		}
	}
	
	private void setCompanionStrength(int strength) {
			companionStrength = strength;
	}
	
	@Override
	public void onPowerOn(BeaconTileEntity beacon) {
		super.onPowerOn(beacon);
		
		updateCompanionStrengthUpwards(beacon);
		
		if(beacon.worldObj.isRemote) {
			beacon.worldObj.playSound(beacon.xCoord + 0.5D, beacon.yCoord + 0.5D, beacon.zCoord + 0.5D,
					"mob.wolf.howl1", 1.2F + (beacon.worldObj.rand.nextFloat() * 0.2F), // volume
					1.0F - (beacon.worldObj.rand.nextFloat() * 0.2F));    // pitch
		}
	}
	
	@Override
	public void onPowerChange(int newPowerLevel, int oldPowerLevel, BeaconTileEntity beacon) {
		super.onPowerChange(newPowerLevel, oldPowerLevel, beacon);
		
		// its okay to just set it to the new powerlevel, with multiple beacons, updatetick will set things right soon enough
		setCompanionStrength(beacon.getLevels());
	}
	
	@Override
	public void onPowerOff(BeaconTileEntity beacon) {
		super.onPowerOff(beacon);
		
		setCompanionStrength(0);
	}
}
