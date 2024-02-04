package btw.entity.mob.villager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.World;

public class PriestVillagerEntity extends VillagerEntity {
	public PriestVillagerEntity(World world) {
		super(world, VillagerEntity.PROFESSION_ID_PRIEST);
		
		registerEffectForLevelUp(VillagerEntity.PROFESSION_ID_PRIEST, 5, new TradeEffect() {
				@Override
				public void playEffect(VillagerEntity villager) {
					villager.worldObj.playSoundAtEntity(villager, "mob.enderdragon.growl", 1.0F, 0.5F);
					villager.worldObj.playSoundAtEntity(villager, "ambient.weather.thunder", 1.0F, rand.nextFloat() * 0.4F + 0.8F);
					villager.worldObj.playSoundAtEntity(villager, "random.levelup", 0.75F + (rand.nextFloat() * 0.25F), 0.5F);
				}
			});
	}
	
	@Override
	protected void spawnCustomParticles() {
		if (this.getCurrentTradeLevel() >= 5) { // top level priest
			// enderman particles

			worldObj.spawnParticle("portal", 
					posX + (rand.nextDouble() - 0.5D) * width, 
					posY + rand.nextDouble() * height - 0.25D, 
					posZ + (rand.nextDouble() - 0.5D) * width, 
					(rand.nextDouble() - 0.5D) * 2D, 
					-rand.nextDouble(), 
					(rand.nextDouble() - 0.5D) * 2D);
		}
	}
	
	//CLIENT ONLY
    @Environment(EnvType.CLIENT)
    public String getTexture() {
		if (this.getCurrentTradeLevel() >= 5)
            return "/btwmodtex/fcPriestLvl.png";
        return "/mob/villager/priest.png";
	}
}