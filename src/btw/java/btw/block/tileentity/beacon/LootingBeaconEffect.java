package btw.block.tileentity.beacon;

import btw.BTWMod;

public class LootingBeaconEffect extends AmbientBeaconEffect {
	public LootingBeaconEffect() {
		super("Looting");
	}
	
	@Override
	public void onUpdate(BeaconTileEntity beacon) {
		if (!beacon.worldObj.isRemote) {
			applyPotionEffectToPlayersInRange(BTWMod.potionLooting.getId(), beacon.getLevels() - 1, beacon);
		}
	}
}