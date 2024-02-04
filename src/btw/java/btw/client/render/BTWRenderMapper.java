package btw.client.render;

import btw.block.tileentity.*;
import btw.client.render.entity.*;
import btw.client.render.tileentity.*;
import btw.entity.*;
import btw.entity.mechanical.platform.BlockLiftedByPlatformEntity;
import btw.entity.mechanical.platform.MovingAnchorEntity;
import btw.entity.mechanical.platform.MovingPlatformEntity;
import btw.entity.mechanical.source.VerticalWindMillEntity;
import btw.entity.mechanical.source.WaterWheelEntity;
import btw.entity.mechanical.source.WindMillEntity;
import btw.entity.mob.*;
import btw.entity.mob.villager.VillagerEntity;
import btw.entity.model.ChickenModel;
import btw.entity.model.DireWolfModel;
import btw.entity.model.PigModel;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

@Environment(EnvType.CLIENT)
public class BTWRenderMapper {
    public static void initTileEntityRenderers() {
        TileEntityRenderer.instance.addSpecialRendererForClass(CampfireTileEntity.class, new CampfireRenderer());
        TileEntityRenderer.instance.addSpecialRendererForClass(OvenTileEntity.class, new OvenRenderer());
        TileEntityRenderer.instance.addSpecialRendererForClass(PlacedToolTileEntity.class, new PlacedToolRenderer());
        TileEntityRenderer.instance.addSpecialRendererForClass(WickerBasketTileEntity.class, new WickerBasketRenderer());
        TileEntityRenderer.instance.addSpecialRendererForClass(HamperTileEntity.class, new BasketRenderer());
    }

    public static void initEntityRenderers() {
        RenderManager.addEntityRenderer(WaterWheelEntity.class, new WaterWheelRenderer());
        RenderManager.addEntityRenderer(WindMillEntity.class, new WindmillRenderer());
        RenderManager.addEntityRenderer(MovingAnchorEntity.class, new MovingAnchorRenderer());
        RenderManager.addEntityRenderer(MovingPlatformEntity.class, new MovingPlatformRenderer());
        RenderManager.addEntityRenderer(BlockLiftedByPlatformEntity.class, new BlockLiftedByPlatformRenderer());
        RenderManager.addEntityRenderer(BroadheadArrowEntity.class, new BroadheadArrowRenderer());
        RenderManager.addEntityRenderer(InfiniteArrowEntity.class, new InfiniteArrowRenderer());
        RenderManager.addEntityRenderer(UrnEntity.class, new UrnRenderer());
        RenderManager.addEntityRenderer(DynamiteEntity.class, new DynamiteRenderer());
        RenderManager.addEntityRenderer(MiningChargeEntity.class, new MiningChargeRenderer());
        RenderManager.addEntityRenderer(CanvasEntity.class, new CanvasRenderer());
        RenderManager.addEntityRenderer(VerticalWindMillEntity.class, new VerticalWindMillRenderer());
        RenderManager.addEntityRenderer(SpiderWebEntity.class, new SpiderWebRenderer());
        RenderManager.addEntityRenderer(DireWolfEntity.class, new DireWolfRenderer(new DireWolfModel(), null));
        RenderManager.addEntityRenderer(SoulSandEntity.class, new SoulSandRenderer());
        RenderManager.addEntityRenderer(JungleSpiderEntity.class, new SpiderRenderer());
        RenderManager.addEntityRenderer(WitherEntityPersistent.class, new RenderWither());
        RenderManager.addEntityRenderer(CorpseEyeEntity.class, new RenderSnowball(BTWItems.corpseEye));

        // remapped vanilla entities
        RenderManager.addEntityRenderer(SpiderEntity.class, new SpiderRenderer());
        RenderManager.addEntityRenderer(CaveSpiderEntity.class, new SpiderRenderer());
        RenderManager.addEntityRenderer(PigEntity.class, new RenderPig(new PigModel(), new PigModel(0.5F), 0.7F));
        RenderManager.addEntityRenderer(SheepEntity.class, new RenderSheep(new ModelSheep2(), new ModelSheep1(), 0.7F));
        RenderManager.addEntityRenderer(CowEntity.class, new RenderCow(new ModelCow(), 0.7F));
        RenderManager.addEntityRenderer(WolfEntity.class, new WolfRenderer(new ModelWolf(), new ModelWolf(), 0.5F));
        RenderManager.addEntityRenderer(ChickenEntity.class, new RenderChicken(new ChickenModel(), 0.3F));
        RenderManager.addEntityRenderer(OcelotEntity.class, new RenderOcelot(new ModelOcelot(), 0.4F));
        RenderManager.addEntityRenderer(CreeperEntity.class, new RenderCreeper());
        RenderManager.addEntityRenderer(EndermanEntity.class, new RenderEnderman());
        RenderManager.addEntityRenderer(SnowmanEntity.class, new RenderSnowMan());
        RenderManager.addEntityRenderer(SkeletonEntity.class, new RenderSkeleton());
        RenderManager.addEntityRenderer(WitchEntity.class, new RenderWitch());
        RenderManager.addEntityRenderer(BlazeEntity.class, new RenderBlaze());
        RenderManager.addEntityRenderer(ZombieEntity.class, new RenderZombie());
        RenderManager.addEntityRenderer(SlimeEntity.class, new RenderSlime(new ModelSlime(16), new ModelSlime(0), 0.25F));
        RenderManager.addEntityRenderer(MagmaCubeEntity.class, new RenderMagmaCube());
        RenderManager.addEntityRenderer(GhastEntity.class, new RenderGhast());
        RenderManager.addEntityRenderer(SquidEntity.class, new SquidRenderer());
        RenderManager.addEntityRenderer(VillagerEntity.class, new RenderVillager());
        RenderManager.addEntityRenderer(BatEntity.class, new RenderBat());
        RenderManager.addEntityRenderer(WitherEntity.class, new RenderWither());
        RenderManager.addEntityRenderer(WitherSkullEntity.class, new RenderWitherSkull());
        RenderManager.addEntityRenderer(FallingBlockEntity.class, new RenderFallingSand());
        RenderManager.addEntityRenderer(LightningBoltEntity.class, new LightningBoltRenderer());

        // additional vanilla remaps to cut down overhead of looking up super classes
        RenderManager.addEntityRenderer(ZombiePigmanEntity.class, new RenderZombie());
    }
}
