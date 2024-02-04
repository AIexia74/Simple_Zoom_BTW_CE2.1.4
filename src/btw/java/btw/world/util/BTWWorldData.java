package btw.world.util;

import btw.world.util.difficulty.Difficulties;
import btw.world.util.difficulty.Difficulty;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.WorldInfo;
import net.minecraft.src.WorldServer;

public class BTWWorldData implements WorldData {
	@Override
	public void saveWorldDataToNBT(WorldServer world, NBTTagCompound tag) {
		if (world.getMagneticPointList() != null) {
			tag.setTag("FCMagneticPoints", world.getMagneticPointList().saveToNBT());
		}
		
		if (world.getLocalEnderChestInventory() != null) {
			tag.setTag("FCEnderItems", world.getLocalEnderChestInventory().saveInventoryToNBT());
		}
		
		if (world.getLocalLowPowerEnderChestInventory() != null) {
			tag.setTag("FCLPEnderItems", world.getLocalLowPowerEnderChestInventory().saveInventoryToNBT());
		}
		
		if (world.getAmbientBeaconLocationList() != null) {
			tag.setTag("FCLootingBeacons", world.getAmbientBeaconLocationList().saveToNBT());
		}
		
		if (world.getSpawnLocationList() != null) {
			tag.setTag("FCSpawnLocations", world.getSpawnLocationList().saveToNBT());
		}
	}
	
	@Override
	public void loadWorldDataFromNBT(WorldServer world, NBTTagCompound tag) {
		if (tag.hasKey("FCMagneticPoints")) {
			NBTTagList nbttaglist1 = tag.getTagList("FCMagneticPoints");
			world.getMagneticPointList().loadFromNBT(nbttaglist1);
		}
		
		if (tag.hasKey("FCEnderItems")) {
			NBTTagList itemList = tag.getTagList("FCEnderItems");
			world.getLocalEnderChestInventory().loadInventoryFromNBT(itemList);
		}
		
		if (tag.hasKey("FCLPEnderItems")) {
			NBTTagList itemList = tag.getTagList("FCLPEnderItems");
			world.getLocalLowPowerEnderChestInventory().loadInventoryFromNBT(itemList);
		}
		
		if (tag.hasKey("FCLootingBeacons")) {
			NBTTagList nbttaglist1 = tag.getTagList("FCLootingBeacons");
			world.getAmbientBeaconLocationList().loadFromNBT(nbttaglist1);
		}
		
		if (tag.hasKey("FCSpawnLocations")) {
			NBTTagList nbttaglist1 = tag.getTagList("FCSpawnLocations");
			world.getSpawnLocationList().loadFromNBT(nbttaglist1);
		}
	}
	
	@Override
	public void saveGlobalDataToNBT(WorldInfo info, NBTTagCompound tag) {
		if (info.getGlobalEnderChestInventory() != null) {
			tag.setTag("FCEnderItems", info.getGlobalEnderChestInventory().saveInventoryToNBT());
		}
		
		tag.setBoolean("FCNetherAccessed", info.hasNetherBeenAccessed());
		tag.setBoolean("FCWitherSummoned", info.hasWitherBeenSummoned());
		tag.setBoolean("FCEndAccessed", info.hasEndDimensionBeenAccessed());
		
		tag.setString("Difficulty", info.getDifficulty().NAME);
	}
	
	@Override
	public void loadGlobalDataFromNBT(WorldInfo info, NBTTagCompound tag) {
		if (tag.hasKey("FCEnderItems")) {
			NBTTagList nbttaglist1 = tag.getTagList("FCEnderItems");
			
			info.getGlobalEnderChestInventory().loadInventoryFromNBT(nbttaglist1);
		}
		
		if (tag.hasKey("FCNetherAccessed")) {
			info.setNetherBeenAccessed(tag.getBoolean("FCNetherAccessed"));
		}
		
		if (tag.hasKey("FCWitherSummoned")) {
			info.setWitherHasBeenSummoned(tag.getBoolean("FCWitherSummoned"));
		}
		
		if (tag.hasKey("FCEndAccessed")) {
			info.setEndDimensionHasBeenAccessed(tag.getBoolean("FCEndAccessed"));
		}
		
		if (tag.hasKey("Difficulty")) {
			Difficulty difficulty = Difficulties.getDifficultyFromName(tag.getString("Difficulty"));
			
			if (difficulty == null) {
				difficulty = Difficulties.STANDARD;
			}
			
			info.setDifficulty(difficulty);
		}
		else {
			info.setDifficulty(Difficulties.STANDARD);
		}
		
		info.previouslyRaining = info.isRaining();
		info.previouslyThundering = info.isThundering();
	}
	
	@Override
	public void createDefaultGlobalData(WorldInfo info) {
		info.setNetherBeenAccessed(false);
		info.setWitherHasBeenSummoned(false);
		info.setEndDimensionHasBeenAccessed(false);
		
		//info.setDifficulty(Difficulties.STANDARD);
	}
	
	@Override
	public void copyGlobalData(WorldInfo newInfo, WorldInfo oldInfo) {
		if (oldInfo.getGlobalEnderChestInventory() != null) {
			newInfo.setGlobalEnderChestInventory(oldInfo.getGlobalEnderChestInventory());
		}
		
		newInfo.setNetherBeenAccessed(oldInfo.hasNetherBeenAccessed());
		newInfo.setWitherHasBeenSummoned(oldInfo.hasWitherBeenSummoned());
		newInfo.setEndDimensionHasBeenAccessed(oldInfo.hasEndDimensionBeenAccessed());
		
		newInfo.setDifficulty(oldInfo.getDifficulty());
		
		newInfo.previouslyRaining = oldInfo.isRaining();
		newInfo.previouslyThundering = oldInfo.isThundering();
	}
	
	@Override
	public String getFilename() {
		return "FCWorld";
	}
}