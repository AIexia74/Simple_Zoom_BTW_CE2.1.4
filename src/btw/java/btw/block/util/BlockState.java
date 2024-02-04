package btw.block.util;

import net.minecraft.src.Block;

public final class BlockState {
	public final int id;
	public final int metadata;
	
	public BlockState(int id, int metadata) {
		this.id = id;
		this.metadata = metadata;
	}
	
	public Block getBlock() {
		return Block.blocksList[id];
	}
	
	public BlockState copy() {
		return new BlockState(this.id, this.metadata);
	}
	
	public BlockState copyWithNewMetadata(int newMetadata) {
		return new BlockState(this.id, newMetadata);
	}
}