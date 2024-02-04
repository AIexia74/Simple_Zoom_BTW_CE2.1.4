package btw.util;

import btw.block.BTWBlocks;
import btw.world.util.BlockPos;
import btw.block.util.BlockState;
import net.minecraft.src.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Schematic {
	public int sizeX;
	public int sizeY;
	public int sizeZ;
	
	private Map<Integer, BlockState> blockMap;
	private List<NBTTagCompound> entities;
	private List<NBTTagCompound> tileEntities;
	
	private Schematic() {}
	
	public Schematic(NBTTagCompound tag) {
		this.loadFromNBT(tag);
	}
	
	// ------ NBT Processing ------ //
	
	public void loadFromNBT(NBTTagCompound tag) {
		this.sizeX = tag.getShort("Width");
		this.sizeY = tag.getShort("Height");
		this.sizeZ = tag.getShort("Length");
		
		byte[] blockIDs = tag.getByteArray("Blocks");
		byte[] blockIDAdd = tag.getByteArray("AddBlocks");
		byte[] metadata = tag.getByteArray("Data");
		
		processBlockData(blockIDs, blockIDAdd, metadata);
		
		NBTTagCompound entityTag = tag.getCompoundTag("Entities");
		processEntityData(entityTag);
		
		NBTTagCompound tileEntityTag = tag.getCompoundTag("TileEntities");
		processTileEntityData(tileEntityTag);
	}
	
	protected void processBlockData(byte[] blockIDs, byte[] blockIDsAdd, byte[] metadata) {
		int size = blockIDs.length;
		
		int[] blockIDsFull = new int[size];
		
		for (int i = 0; i < size; i++) {
			byte blockIDAdd = blockIDsAdd[i / 2];
			
			//Even indexes go in the high nibble
			//Odd indexes go in the low nibble
			if (i % 2 == 0) {
				blockIDAdd = (byte) (blockIDAdd >> 4);
			}
			else {
				blockIDAdd = (byte) (blockIDAdd & 15);
			}
			
			blockIDsFull[i] = blockIDAdd << 8 + blockIDs[i];
		}
		
		for (int i = 0; i < size; i++) {
			this.blockMap.put(i, new BlockState(blockIDsFull[i], metadata[i]));
		}
	}
	
	protected void putBlockAtCoords(int x, int y, int z, BlockState blockState) {
		this.blockMap.put(getIndexForCoords(x, y, z), blockState);
	}
	
	protected void processEntityData(NBTTagCompound tag) {
		Collection entityTags = tag.getTags();
		
		Iterator it = entityTags.iterator();
		
		while (it.hasNext()) {
			NBTTagCompound entity = (NBTTagCompound) it.next();
			
			this.entities.add(entity);
		}
	}
	
	protected void processTileEntityData(NBTTagCompound tag) {
		Collection tileEntityTags = tag.getTags();
		
		Iterator it = tileEntityTags.iterator();
		
		while (it.hasNext()) {
			NBTTagCompound tileEntity = (NBTTagCompound) it.next();
			
			this.tileEntities.add(tileEntity);
		}
	}
	
	// ------ World Generation ------ //
	
	public void addSchematicToWorld(World world, int x, int y, int z) {
		for (int index : blockMap.keySet()) {
			BlockPos pos = getCoordsForIndex(index);
			
			int i = x + pos.x;
			int j = y + pos.y;
			int k = z + pos.z;
			
			BlockState blockState = blockMap.get(index);
			
			int id = blockState.id;
			int metadata = blockState.metadata;
			
			//Air blocks should be ignored
			if (id == 0) {
				continue;
			}
			//Structure voids set block to air
			else if (id == BTWBlocks.structureVoid.blockID) {
				id = 0;
			}
			
			world.setBlockAndMetadata(i, j, k, id, metadata);
		}
		
		for (NBTTagCompound tag : this.entities) {
			EntityList.createEntityFromNBT(tag, world);
		}
		
		for (NBTTagCompound tag : this.tileEntities) {
			TileEntity.createAndLoadEntity(tag);
		}
	}
	
	// ------ Utility ------ //
	
	public Schematic copy() {
		Schematic schematic = new Schematic();
		
		schematic.sizeX = this.sizeX;
		schematic.sizeY = this.sizeY;
		schematic.sizeZ = this.sizeZ;
		
		Map<Integer, BlockState> newBlockMap = new HashMap();
		
		for (Map.Entry<Integer, BlockState> entry : this.blockMap.entrySet()) {
			newBlockMap.put(entry.getKey(), entry.getValue());
		}
		
		schematic.blockMap = newBlockMap;
		Collections.copy(schematic.entities, this.entities);
		Collections.copy(schematic.tileEntities, this.tileEntities);
		
		return schematic;
	}
	
	public void rotate(boolean clockwise) {
		this.rotate(clockwise, 1);
	}
	
	public void rotate(boolean clockwise, int count) {
		// TODO
	}
	
	public void mirror(boolean useXPlane) {
		// TODO
	}
	
	protected int getIndexForCoords(int x, int y, int z) {
		return (y * sizeZ + z) * sizeX + x;
	}
	
	protected BlockPos getCoordsForIndex(int index) {
		int x = index % sizeX;
		index /= sizeX;
		
		int z = index % sizeZ;
		index /= sizeZ;
		
		int y = index;
		
		return new BlockPos(x, y, z);
	}
	
	// ------ Getters ------ //
	
	public BlockState getBlockAtCoords(int x, int y, int z) {
		return this.blockMap.get(getIndexForCoords(x, y, z));
	}

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public int getSizeZ() {
		return sizeZ;
	}
}