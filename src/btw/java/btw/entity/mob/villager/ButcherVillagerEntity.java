package btw.entity.mob.villager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.World;

public class ButcherVillagerEntity extends VillagerEntity {
	public ButcherVillagerEntity(World world) {
		super(world, VillagerEntity.PROFESSION_ID_BUTCHER);
	}
	
	//CLIENT ONLY
    @Environment(EnvType.CLIENT)
    public String getTexture() {
		if (this.getCurrentTradeLevel() >= 4)
            return "/btwmodtex/fcButcherLvl.png";
        return "/mob/villager/butcher.png";
	}
}