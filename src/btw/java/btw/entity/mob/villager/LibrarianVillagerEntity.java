package btw.entity.mob.villager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.World;

public class LibrarianVillagerEntity extends VillagerEntity {
	public LibrarianVillagerEntity(World world) {
		super(world, VillagerEntity.PROFESSION_ID_LIBRARIAN);
	}
	
	//CLIENT ONLY
    @Environment(EnvType.CLIENT)
    public String getTexture() {
		if (this.getCurrentTradeLevel() >= 5)
            return "/btwmodtex/fcLibrarianSpecs.png";
        return "/mob/villager/librarian.png";
	}
}