// FCMOD

package btw.block.tileentity.beacon;

public class BeaconEffectDescriptor {
	public final int BLOCK_METADATA;
	public final BeaconEffect EFFECT;
	
	public BeaconEffectDescriptor(int blockMetadata, BeaconEffect effect) {
		this.BLOCK_METADATA = blockMetadata;
		this.EFFECT = effect;
	}
}
