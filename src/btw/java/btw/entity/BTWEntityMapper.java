package btw.entity;

import btw.BTWMod;
import btw.entity.item.BloodWoodSaplingItemEntity;
import btw.entity.item.FloatingItemEntity;
import btw.entity.mechanical.platform.BlockLiftedByPlatformEntity;
import btw.entity.mechanical.platform.MovingAnchorEntity;
import btw.entity.mechanical.platform.MovingPlatformEntity;
import btw.entity.mechanical.source.VerticalWindMillEntity;
import btw.entity.mechanical.source.WaterWheelEntity;
import btw.entity.mechanical.source.WindMillEntity;
import btw.entity.mob.*;
import btw.entity.mob.villager.*;
import net.minecraft.src.EntityList;

public class BTWEntityMapper {
    public static void createModEntityMappings() {
        EntityList.addMapping(WaterWheelEntity.class, "WaterWheel", BTWMod.instance.parseID("fcWaterWheelEntityID"));
        EntityList.addMapping(WindMillEntity.class, "WindMill", BTWMod.instance.parseID("fcWindMillEntityID"));
        EntityList.addMapping(MovingAnchorEntity.class, "MovingAnchor", BTWMod.instance.parseID("fcMovingAnchorEntityID"));
        EntityList.addMapping(MovingPlatformEntity.class, "MovingPlatform", BTWMod.instance.parseID("fcMovingPlatformEntityID"));
        EntityList.addMapping(BlockLiftedByPlatformEntity.class, "BlockLiftedByPlatform", BTWMod.instance.parseID("fcBlockLiftedByPlatformEntityID"));
        EntityList.addMapping(BroadheadArrowEntity.class, "BroadheadArrow", BTWMod.instance.parseID("fcBroadheadArrowEntityID"));
        EntityList.addMapping(UrnEntity.class, "Urn", BTWMod.instance.parseID("fcUrnEntityID"));
        EntityList.addMapping(DynamiteEntity.class, "Dynamite", BTWMod.instance.parseID("fcDynamiteEntityID"));
        EntityList.addMapping(MiningChargeEntity.class, "MiningCharge", BTWMod.instance.parseID("fcMiningChargeEntityID"));
        EntityList.addMapping(InfiniteArrowEntity.class, "fcInfiniteArrow", BTWMod.instance.parseID("fcInfiniteArrowEntityID"));
        EntityList.addMapping(FloatingItemEntity.class, "fcItemFloating", BTWMod.instance.parseID("fcItemFloatingEntityID"));
        EntityList.addMapping(BloodWoodSaplingItemEntity.class, "fcItemBloodWoodSapling", BTWMod.instance.parseID("fcItemBloodWoodSaplingEntityID"));
        EntityList.addMapping(CanvasEntity.class, "fcCanvas", BTWMod.instance.parseID("fcCanvasEntityID"));
        EntityList.addMapping(RottenArrowEntity.class, "fcRottenArrow", BTWMod.instance.parseID("fcRottenArrowEntityID"));
        EntityList.addMapping(VerticalWindMillEntity.class, "fcWindMillVertical", BTWMod.instance.parseID("fcEntityWindMillVerticalID"));
        EntityList.addMapping(SpiderWebEntity.class, "fcSpiderWeb", BTWMod.instance.parseID("fcEntitySpiderWebID"));
        EntityList.addMapping(DireWolfEntity.class, "fcDireWolf", BTWMod.instance.parseID("fcEntityDireWolfID"));
        EntityList.addMapping(SoulSandEntity.class, "fcEntitySoulSand", BTWMod.instance.parseID("fcEntitySoulSandID"));
        EntityList.addMapping(JungleSpiderEntity.class, "fcJungleSpider", BTWMod.instance.parseID("fcEntityJungleSpiderID"), 0x1C311D, 0x1B1911);
        EntityList.addMapping(WitherEntityPersistent.class, "fcWitherPersistent", BTWMod.instance.parseID("fcWitherPersistentID"));
        EntityList.addMapping(CorpseEyeEntity.class, "fcCorpseEye", BTWMod.instance.parseID("fcEntityCorpseEyeID"));

        //Uses legacy names from old API for compatibility
        EntityList.addMapping(FarmerVillagerEntity.class, "addonVillagerFarmer", 600, 5651507, 12422002);
        EntityList.addMapping(LibrarianVillagerEntity.class, "addonVillagerLibrarian", 601, 14342874, 16179719);
        EntityList.addMapping(PriestVillagerEntity.class, "addonVillagerPriest", 602, 8470879, 12422002);
        EntityList.addMapping(BlacksmithVillagerEntity.class, "addonVillagerBlacksmith", 603, 4802889, 12422002);
        EntityList.addMapping(ButcherVillagerEntity.class, "addonVillagerButcher", 604, 11447982, 12422002);
        //Removes generic villager egg since specific villager types have their own eggs now
        EntityList.entityEggs.remove(120);

        EntityList.replaceExistingMapping(WitherSkullEntity.class, "WitherSkull");
        EntityList.replaceExistingMapping(FallingBlockEntity.class, "FallingSand");
        EntityList.replaceExistingMapping(CreeperEntity.class, "Creeper");
        EntityList.replaceExistingMapping(SkeletonEntity.class, "Skeleton");
        EntityList.replaceExistingMapping(SpiderEntity.class, "Spider");
        EntityList.replaceExistingMapping(ZombieEntity.class, "Zombie");
        EntityList.replaceExistingMapping(SlimeEntity.class, "Slime");
        EntityList.replaceExistingMapping(GhastEntity.class, "Ghast");
        EntityList.replaceExistingMapping(ZombiePigmanEntity.class, "PigZombie");
        EntityList.replaceExistingMapping(EndermanEntity.class, "Enderman");
        EntityList.replaceExistingMapping(CaveSpiderEntity.class, "CaveSpider");
        EntityList.replaceExistingMapping(BlazeEntity.class, "Blaze");
        EntityList.replaceExistingMapping(MagmaCubeEntity.class, "LavaSlime");
        EntityList.replaceExistingMapping(WitherEntity.class, "WitherBoss");
        EntityList.replaceExistingMapping(BatEntity.class, "Bat");
        EntityList.replaceExistingMapping(WitchEntity.class, "Witch");
        EntityList.replaceExistingMapping(PigEntity.class, "Pig");
        EntityList.replaceExistingMapping(SheepEntity.class, "Sheep");
        EntityList.replaceExistingMapping(CowEntity.class, "Cow");
        EntityList.replaceExistingMapping(ChickenEntity.class, "Chicken");
        EntityList.replaceExistingMapping(SquidEntity.class, "Squid");
        EntityList.replaceExistingMapping(WolfEntity.class, "Wolf");
        EntityList.replaceExistingMapping(SnowmanEntity.class, "SnowMan");
        EntityList.replaceExistingMapping(OcelotEntity.class, "Ozelot");
        EntityList.replaceExistingMapping(VillagerEntity.class, "Villager");
    }
}
