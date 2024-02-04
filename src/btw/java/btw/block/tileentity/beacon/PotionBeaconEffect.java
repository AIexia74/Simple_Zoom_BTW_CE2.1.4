package btw.block.tileentity.beacon;

public class PotionBeaconEffect extends BeaconEffect {
	private final int POTION_ID;
	private final boolean SCALES_WITH_LEVEL;
	
	public PotionBeaconEffect(int potionID, boolean scalesWithLevel) {
		this.POTION_ID = potionID;
		this.SCALES_WITH_LEVEL = scalesWithLevel;
	}
	
	@Override
	public void onUpdate(BeaconTileEntity beacon) {
		if (!beacon.worldObj.isRemote) {
			if (SCALES_WITH_LEVEL) {
				applyPotionEffectToPlayersInRange(this.POTION_ID, beacon.getLevels() - 1, beacon);
			}
			else {
				applyPotionEffectToPlayersInRange(this.POTION_ID, 0, beacon);
			}
		}
	}
}
