package btw.block.tileentity.beacon;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticOpaqueEarthBlock;
import btw.client.fx.BTWEffectManager;
import net.minecraft.src.Block;


public class SpawnAnchorBeaconEffect extends BeaconEffect {
	
	private static final int SOULFORGED_BEACON_BLIGHT_SPREAD_FREQUENCY = 10000;
	public static final int[] soulforgedBeaconBlightSpreadRange = new int[]{0, 8, 16, 32, 64};
	
	@Override
	public void onUpdate(BeaconTileEntity beacon) {
		if(!beacon.worldObj.isRemote) {
			checkForBlightSpread(beacon);
			checkPlayerSpawnedAtBeacon(beacon);
		}
		else {
			playSoundEffect(beacon);
		}
	}
	
	public void checkPlayerSpawnedAtBeacon(BeaconTileEntity beacon){
		if (beacon.playerRespawnedAtBeacon) {
			beacon.playerRespawnedAtBeacon = false;
			
			beacon.worldObj.playAuxSFX(BTWEffectManager.GHAST_SCREAM_EFFECT_ID, beacon.xCoord, beacon.yCoord, beacon.zCoord, 1);
			
			beacon.worldObj.playSoundEffect(beacon.xCoord + 0.5D, beacon.yCoord + 0.5D, beacon.zCoord + 0.5D,
					"mob.wither.spawn", 1.0F + (beacon.worldObj.rand.nextFloat() * 0.1F),  // volume
					1.0F + (beacon.worldObj.rand.nextFloat() * 0.1F)); // pitch
		}
	}
	
	@Override
	public void onPowerOn(BeaconTileEntity beacon) {
		super.onPowerOn(beacon);
		if(beacon.worldObj.isRemote) {
			beacon.worldObj.playSound(beacon.xCoord + 0.5D, beacon.yCoord + 0.5D, beacon.zCoord + 0.5D,
					"mob.ghast.scream", 1.0F, // volume
					beacon.worldObj.rand.nextFloat() * 0.4F + 0.25F); // pitch
		}
	}
	
	public void playSoundEffect(BeaconTileEntity beacon) {
		// moans from SFS pyramid
		int level = beacon.getLevels();
		
		if (beacon.worldObj.rand.nextInt(60) <= level) {
			int iLevelOfSound = beacon.worldObj.rand.nextInt(level) + 1;
			
			int offsetX = beacon.worldObj.rand.nextInt(level * 2 + 1) - level;
			int offsetZ = beacon.worldObj.rand.nextInt(level * 2 + 1) - level;
			
			beacon.worldObj.playSound(beacon.xCoord + offsetX, beacon.yCoord - level, beacon.zCoord + offsetZ, "mob.ghast.moan",
					0.5F, beacon.worldObj.rand.nextFloat() * 0.4F + 0.25F);
		}
	}
	
	private void checkForBlightSpread(BeaconTileEntity beacon) {
		if (beacon.worldObj.rand.nextInt(SOULFORGED_BEACON_BLIGHT_SPREAD_FREQUENCY) == 0) {
			int level = beacon.getLevels();
			
			int range = soulforgedBeaconBlightSpreadRange[level];
			
			int x = beacon.xCoord + beacon.worldObj.rand.nextInt(range * 2 + 1) - range;
			int y = beacon.worldObj.rand.nextInt(256);
			int z = beacon.zCoord + beacon.worldObj.rand.nextInt(range * 2 + 1) - range;
			
			int blockID = beacon.worldObj.getBlockId(x, y, z);
			
			if (blockID == Block.grass.blockID) {
				beacon.worldObj.setBlockAndMetadataWithNotify(x, y, z, BTWBlocks.aestheticEarth.blockID, AestheticOpaqueEarthBlock.SUBTYPE_BLIGHT_LEVEL_0);
			}
		}
	}
	
	@Override
	protected int getMagneticFieldLevel(int powerlevel ) {

		return powerlevel * 2;
	}
}
