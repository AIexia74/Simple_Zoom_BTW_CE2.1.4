package btw.world.util;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.WorldInfo;
import net.minecraft.src.WorldServer;

public interface WorldData {
	/**
	 * Saves local (per-dimension) world data to the given NBT tag
	 * @param world
	 * @param tag
	 */
	void saveWorldDataToNBT(WorldServer world, NBTTagCompound tag);
	
	/**
	 * Loads local (per-dimension) world data from the given NBT tag
	 * @param world
	 * @param tag
	 */
	void loadWorldDataFromNBT(WorldServer world, NBTTagCompound tag);

	/**
	 * Loads global world data from the given NBT tag
	 * @param info
	 * @param tag
	 */
	void saveGlobalDataToNBT(WorldInfo info, NBTTagCompound tag);

	/**
	 * Loads global world data from the given NBT tag
	 * @param tag
	 */
	void loadGlobalDataFromNBT(WorldInfo info, NBTTagCompound tag);
	
	/**
	 * Called on world creation, and should be used to set up any data needed on initial load
	 */
	default void createDefaultGlobalData(WorldInfo info) {}
	
	/**
	 * Copies data from one WorldInfo object to another
	 * @param oldInfo
	 * @param newInfo
	 */
	void copyGlobalData(WorldInfo oldInfo, WorldInfo newInfo);
	
	/**
	 * Only used for local data. Global data is stored in level.dat
	 * @return The name of the file to be stored for each dimension containing the local world data
	 */
	String getFilename();
}
