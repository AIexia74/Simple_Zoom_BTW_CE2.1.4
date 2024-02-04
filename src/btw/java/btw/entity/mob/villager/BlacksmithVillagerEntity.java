package btw.entity.mob.villager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.World;

public class BlacksmithVillagerEntity extends VillagerEntity {
	public BlacksmithVillagerEntity(World world) {
		super(world, VillagerEntity.PROFESSION_ID_BLACKSMITH);
	}
	
	//CLIENT ONLY
    @Environment(EnvType.CLIENT)
    public String getTexture() {
        return "/mob/villager/smith.png";
	}
}