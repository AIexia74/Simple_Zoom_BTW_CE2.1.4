package btw.block.tileentity.beacon;

public class DecorativeBeaconEffect extends BeaconEffect {
	
	@Override
	public void onUpdate(BeaconTileEntity tileEntity) {
	}
	
	@Override
	public void onPowerChange(int newPowerLevel, int oldPowerLevel, BeaconTileEntity beacon) {
		// no magnetic pole for glass beacon
	}
}
