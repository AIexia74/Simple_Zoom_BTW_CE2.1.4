package btw.block.tileentity.beacon;

public class MagneticPointBeaconEffect extends BeaconEffect {
	
	@Override
	public void onUpdate(BeaconTileEntity tileEntity) {
	}
	
	@Override
	protected int getMagneticFieldLevel(int powerlevel ) {
		return powerlevel * 2;
	}
}
