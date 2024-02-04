package btw.entity.mob.villager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.World;

public class FarmerVillagerEntity extends VillagerEntity {
	public FarmerVillagerEntity(World world) {
		super(world, VillagerEntity.PROFESSION_ID_FARMER);
	}

	@Override
	public int getDirtyPeasant() {
		return dataWatcher.getWatchableObjectInt(DIRTY_PEASANT_DATA_WATCHER_ID);
	}

	@Override
	public void setDirtyPeasant(int iDirtyPeasant) {
		dataWatcher.updateObject(DIRTY_PEASANT_DATA_WATCHER_ID, iDirtyPeasant);
	}

	//CLIENT ONLY
    @Override
    @Environment(EnvType.CLIENT)
    public String getTexture() {
		if (this.getDirtyPeasant() > 0)
			return "/btwmodtex/fcDirtyPeasant.png";
		return "/mob/villager/farmer.png";
	}
}