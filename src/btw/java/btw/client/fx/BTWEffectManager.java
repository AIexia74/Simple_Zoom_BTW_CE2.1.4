package btw.client.fx;

import btw.block.BTWBlocks;
import btw.block.blocks.CompanionCubeBlock;
import btw.item.BTWItems;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

@Environment(EnvType.CLIENT)
public class BTWEffectManager {
    public static final int ANIMAL_BIRTHING_EFFECT_ID = 2222;
    public static final int SAW_DAMAGE_EFFECT_ID = 2223;
    public static final int NETHER_GROTH_SPORES_EFFECT_ID = 2224;
    public static final int GHAST_SCREAM_EFFECT_ID = 2225;
    public static final int BURP_SOUND_EFFECT_ID = 2226;
    public static final int FIRE_FIZZ_EFFECT_ID = 2227;
    public static final int GHAST_MOAN_EFFECT_ID = 2228;
    public static final int MINING_CHARGE_EXPLOSION_EFFECT_ID = 2229;
    public static final int HOPPER_EJECT_XP_EFFECT_ID = 2230;
    public static final int ITEM_COLLECTION_POP_EFFECT_ID = 2231;
    public static final int XP_EJECT_POP_EFFECT_ID = 2232;
    public static final int HOPPER_CLOSE_EFFECT_ID = 2233;
    public static final int REDSTONE_CLICK_EFFECT_ID = 2234;
    public static final int MECHANICAL_DEVICE_EXPLODE_EFFECT_ID = 2235;
    public static final int BLOCK_PLACE_EFFECT_ID = 2236;
    public static final int DYNAMITE_FUSE_EFFECT_ID = 2237;
    public static final int LOW_PITCH_CLICK_EFFECT_ID = 2238;
    public static final int WOLF_HURT_EFFECT_ID = 2239;
    public static final int CHICKEN_HURT_EFFECT_ID = 2240;
    public static final int BLOCK_DISPENSER_SMOKE_EFFECT_ID = 2241;
    public static final int COMPANION_CUBE_DEATH_EFFECT_ID = 2242;
    public static final int POSSESSED_CHICKEN_EXPLOSION_EFFECT_ID = 2243;
    public static final int ENDERMAN_COLLECT_BLOCK_EFFECT_ID = 2244;
    public static final int ENDERMAN_CONVERT_BLOCK_EFFECT_ID = 2245;
    public static final int ENDERMAN_PLACE_BLOCK_EFFECT_ID = 2246;
    public static final int ENDERMAN_CHANGE_DIMENSION_EFFECT_ID = 2247;
    public static final int SOUL_URN_SHATTER_EFFECT_ID = 2248;
    public static final int MELON_EXPLODE_EFFECT_ID = 2249;
    public static final int PUMPKIN_EXPLODE_EFFECT_ID = 2250;
    public static final int GOURD_IMPACT_SOUND_EFFECT_ID = 2251;
    public static final int DESTROY_BLOCK_RESPECT_PARTICLE_SETTINGS_EFFECT_ID = 2252;
    public static final int COW_REGEN_MILK_EFFECT_ID = 2253;
    public static final int COW_MILKING_EFFECT_ID = 2254;
    public static final int COW_CONVERSION_TO_MOOSHROOM_EFFECT_ID = 2255;
    public static final int WOLF_HOWL_EFFECT_ID = 2256;
    public static final int WOLF_CONVERSION_TO_DIRE_WOLF_EFFECT_ID = 2257;
    public static final int CREEPER_SNIP_EFFECT_ID = 2258;
    public static final int POSSESSED_PIG_TRANSFORMATION_EFFECT_ID = 2259;
    public static final int POSSESSED_VILLAGER_TRANSFORMATION_EFFECT_ID = 2260;
    public static final int SHEEP_REGROW_WOOL_EFFECT_ID = 2261;
    public static final int SQUID_TENTACLE_FLING_EFFECT_ID = 2262;
    public static final int CREATE_SNOW_GOLEM_EFFECT_ID = 2263;
    public static final int CREATE_IRON_GOLEM_EFFECT_ID = 2264;
    public static final int TOSS_THE_MILK_EFFECT_ID = 2265;
    public static final int APPLY_DUNG_TO_WOLF_EFFECT_ID = 2266;
    public static final int REMOVE_STUMP_EFFECT_ID = 2267;
    public static final int SHAFT_RIPPED_OFF_EFFECT_ID = 2268;
    public static final int STONE_RIPPED_OFF_EFFECT_ID = 2269;
    public static final int GRAVEL_RIPPED_OFF_EFFECT_ID = 2270;
    public static final int WOOD_BLOCK_DESTROYED_EFFECT_ID = 2271;
    public static final int BLOCK_DESTROYED_WITH_IMPROPER_TOOL_EFFECT_ID = 2272;
    public static final int POSSESSED_SQUID_TRANSFORMATION_EFFECT_ID = 2273;
    public static final int APPLY_MORTAR_EFFECT_ID = 2274;
    public static final int LOOSE_BLOCK_STUCK_TO_MORTAR_EFFECT_ID = 2275;
    public static final int SMOLDERING_LOG_FALL_EFFECT_ID = 2276;
    public static final int SMOLDERING_LOG_EXPLOSION_EFFECT_ID = 2277;
    public static final int WATER_EVAPORATION_EFFECT_ID = 2278;
    public static final int CREATE_WITHER_EFFECT_ID = 2279;
    public static final int LIGHTNING_STRIKE_EFFECT_ID = 2280;
    public static final int FLAMING_NETHERRACK_FALL_EFFECT_ID = 2281;
    public static final int CACTUS_EXPLOSION_EFFECT_ID = 2282;
    public static final int ANIMAL_EATING_EFFECT_ID = 2283;
    public static final int WOLF_EATING_EFFECT_ID = 2284;
    public static final int FAILED_EATING_EFFECT_ID = 2285;

    public static void initEffects() {
        EffectHandler.effectMap.put(ANIMAL_BIRTHING_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.playSound(x, y, z, "mob.slime.attack", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);

            for (int counter = 0; counter < 10; counter++) {
                double bloodX = x + world.rand.nextDouble();
                double bloodY = y + 1.0D + world.rand.nextDouble();
                double bloodZ = z + world.rand.nextDouble();

                world.spawnParticle("reddust", bloodX, bloodY, bloodZ, 0.0D, 0.0D, 0.0D);
            }

            for (int i = 0; i < 10; i++) {
                double bloodX = x - 0.5D + world.rand.nextDouble();
                double bloodY = y + world.rand.nextDouble();
                double bloodZ = z - 0.5D + world.rand.nextDouble();

                world.spawnParticle("dripLava", bloodX, bloodY, bloodZ, 0.0D, 0.0D, 0.0D);
            }
        });

        EffectHandler.effectMap.put(SAW_DAMAGE_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.playSound(x, y, z,
                    "minecart.base",
                    1.00F + (world.rand.nextFloat() * 0.1F),        // volume
                    2.0F + (world.rand.nextFloat() * 0.1F));    // pitch

            int facing = data;

            // emit blood particles

            BlockPos targetPos = new BlockPos((int) x, (int) y, (int) z, facing);

            for (int i = 0; i < 10; i++) {
                float smokeX = (float) targetPos.x + world.rand.nextFloat();
                float smokeY = (float) targetPos.y + world.rand.nextFloat();
                float smokeZ = (float) targetPos.z + world.rand.nextFloat();

                world.spawnParticle("reddust", smokeX, smokeY, smokeZ, 0.0D, 0.0D, 0.0D);
            }
        });

        EffectHandler.effectMap.put(NETHER_GROTH_SPORES_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.playSound(x, y, z, "random.fuse", 2.0F, world.rand.nextFloat() * 0.4F + 1.5F);

            for (int i = 0; i < 10; i++) {
                world.spawnParticle("hugeexplosion",
                        x + world.rand.nextDouble() * 10.0D - 5D,
                        y + world.rand.nextDouble() * 10.0D - 5D,
                        z + world.rand.nextDouble() * 10.0D - 5D,
                        0.0D, 0.0D, 0.0D);
            }
        });

        EffectHandler.effectMap.put(GHAST_SCREAM_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            float screamPitch = world.rand.nextFloat() * 0.4F + 0.8F;

            if (data == 1) {
                // low pitch used by Soulforged Steel
                screamPitch = world.rand.nextFloat() * 0.4F + 0.25F;
            }

            world.playSound(x, y, z, "mob.ghast.scream", 1.0F, screamPitch);
        });

        EffectHandler.effectMap.put(BURP_SOUND_EFFECT_ID, (mcInstance, world, player, x, y, z, data) ->
                world.playSound(x, y, z, "random.burp",1.0F, world.rand.nextFloat() * 0.4F + 0.7F)
        );

        EffectHandler.effectMap.put(FIRE_FIZZ_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            float fizzVolume = 0.5F;
            float fizzPitch = 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F;

            if (data == 1) {
                fizzVolume = 0.1F;
                fizzPitch = 1F + +(world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F;
            }

            world.playSound(x, y, z, "random.fizz", fizzVolume, fizzPitch);
        });

        EffectHandler.effectMap.put(GHAST_MOAN_EFFECT_ID, (mcInstance, world, player, x, y, z, data) ->
                world.playSound(x, y, z, "mob.ghast.moan",0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F)
        );

        EffectHandler.effectMap.put(MINING_CHARGE_EXPLOSION_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.playSound(x, y, z, "random.explode", 4F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
            world.spawnParticle("hugeexplosion", x, y, z, 0.0D, 0.0D, 0.0D);
        });

        EffectHandler.effectMap.put(HOPPER_EJECT_XP_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.playSound(x, y, z, "liquid.lavapop", 0.5F + world.rand.nextFloat() * 0.25F, 0.5F + world.rand.nextFloat() * 0.25F);

            for (int i = 0; i < 4; i++) {
                world.spawnParticle("slime", x, y - 0.6, z, 0.0D, 0.0D, 0.0D);
            }
        });

        EffectHandler.effectMap.put(ITEM_COLLECTION_POP_EFFECT_ID, (mcInstance, world, player, x, y, z, data) ->
                world.playSound(x, y, z, "random.pop", 0.25F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F)
        );

        EffectHandler.effectMap.put(XP_EJECT_POP_EFFECT_ID, (mcInstance, world, player, x, y, z, data) ->
                world.playSound(x, y, z, "liquid.lavapop", 0.5F + world.rand.nextFloat() * 0.25F, 0.5F + world.rand.nextFloat() * 0.25F)
        );

        EffectHandler.effectMap.put(HOPPER_CLOSE_EFFECT_ID, (mcInstance, world, player, x, y, z, data) ->
                world.playSound(x, y, z, "mob.irongolem.walk", 1.0F, 1.25F)
        );

        EffectHandler.effectMap.put(REDSTONE_CLICK_EFFECT_ID, (mcInstance, world, player, x, y, z, data) ->
                world.playSound(x, y, z, "random.click", 0.75F, 2.0F)
        );

        EffectHandler.effectMap.put(MECHANICAL_DEVICE_EXPLODE_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.playSound(x, y, z, "mob.zombie.woodbreak", 0.5F, 0.60F + (world.rand.nextFloat() * 0.25F));
            world.spawnParticle("explode", x, y, z, 0D, 0D, 0D);

            for (int i = 0; i < 20; i++) {
                double smokeX = x + world.rand.nextDouble() - 0.5D;
                double smokeY = y + world.rand.nextDouble() - 0.5D;
                double smokeZ = z + world.rand.nextDouble() - 0.5D;

                double smokeVelX = (smokeX - x) * 0.33D;
                double smokeVelY = (smokeY - y) * 0.33D;
                double smokeVelZ = (smokeZ - z) * 0.33D;

                world.spawnParticle("smoke", smokeX, smokeY, smokeZ, smokeVelX, smokeVelY, smokeVelZ);
            }
        });

        EffectHandler.effectMap.put(BLOCK_PLACE_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            int blockID = data;
            Block block = Block.blocksList[blockID];

            if (block != null) {
                world.playSound(x, y, z,
                        block.stepSound.getPlaceSound(),
                        (block.stepSound.getPlaceVolume() + 1.0F) / 2.0F,
                        block.stepSound.getPlacePitch() * 0.8F);
            }
        });

        EffectHandler.effectMap.put(DYNAMITE_FUSE_EFFECT_ID, (mcInstance, world, player, x, y, z, data) ->
                world.playSound(x, y, z, "random.fuse", 1.0F, 1.0F)
        );

        EffectHandler.effectMap.put(LOW_PITCH_CLICK_EFFECT_ID, (mcInstance, world, player, x, y, z, data) ->
                world.playSound(x, y, z, "random.click", 0.10F, 0.5F)
        );

        EffectHandler.effectMap.put(WOLF_HURT_EFFECT_ID, (mcInstance, world, player, x, y, z, data) ->
                world.playSound(x, y, z, "mob.wolf.hurt", 0.4F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F)
        );

        EffectHandler.effectMap.put(CHICKEN_HURT_EFFECT_ID, (mcInstance, world, player, x, y, z, data) ->
                world.playSound(x, y, z, "mob.chicken.hurt", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F)
        );

        EffectHandler.effectMap.put(BLOCK_DISPENSER_SMOKE_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            int facing = data;

            BlockPos targetDeltaPos = new BlockPos(0, 0, 0, facing);

            double ejectX = x + targetDeltaPos.x * 0.6D;
            double ejectY = y + targetDeltaPos.y * 0.6D;
            double ejectZ = z + targetDeltaPos.z * 0.6D;

            for (int i = 0; i < 10; i++) {
                double d4 = world.rand.nextDouble() * 0.2D + 0.01D;

                double smokeX = ejectX + targetDeltaPos.x * 0.01D + (world.rand.nextDouble() - 0.5D) * targetDeltaPos.x * 0.5D;
                double smokeY = ejectY + targetDeltaPos.y * 0.01D + (world.rand.nextDouble() - 0.5D) * 0.5D;
                double smokeZ = ejectZ + targetDeltaPos.z * 0.01D + (world.rand.nextDouble() - 0.5D) * targetDeltaPos.z * 0.5D;

                double smokeVelX = targetDeltaPos.x * d4 + world.rand.nextGaussian() * 0.01D;
                double smokeVelY = targetDeltaPos.y * d4 - 0.03D + world.rand.nextGaussian() * 0.01D;
                double smokeVelZ = targetDeltaPos.z * d4 + world.rand.nextGaussian() * 0.01D;

                world.spawnParticle("smoke", smokeX, smokeY, smokeZ, smokeVelX, smokeVelY, smokeVelZ);
            }
        });

        EffectHandler.effectMap.put(COMPANION_CUBE_DEATH_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            CompanionCubeBlock.spawnHearts(world, (int) x, (int) y, (int) z);
            world.playSound(x, y, z, "mob.wolf.whine", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
        });

        EffectHandler.effectMap.put(POSSESSED_CHICKEN_EXPLOSION_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.playSound(x, y, z, "random.explode", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);
            world.playSound(x, y, z, "mob.chicken.hurt", 2.0F, world.rand.nextFloat() * 0.4F + 1.2F);

            for (int i = 0; i < 10; i++) {
                double bloodX = x + world.rand.nextDouble();
                double bloodY = y + 1.0D + world.rand.nextDouble();
                double bloodZ = z + world.rand.nextDouble();

                world.spawnParticle("reddust", bloodX, bloodY, bloodZ, 0.0D, 0.0D, 0.0D);
            }

            for (int i = 0; i < 10; i++) {
                double bloodX = x - 0.5D + world.rand.nextDouble();
                double bloodY = y + world.rand.nextDouble() * 0.5F;
                double bloodZ = z - 0.5D + world.rand.nextDouble();

                world.spawnParticle("dripLava", bloodX, bloodY, bloodZ, 0.0D, 0.0D, 0.0D);
            }

            for (int i = 0; i < 300; i++) {
                double bloodX = x + world.rand.nextDouble() - 0.5D;
                double bloodY = y - 1.0D;// + world.rand.nextDouble() * 0.25D;
                double bloodZ = z + world.rand.nextDouble() - 0.5D;

                double bloodVelX = (world.rand.nextDouble() - 0.5D) * 0.5D;
                double bloodVelY = 0.2D + world.rand.nextDouble() * 0.6D;
                double bloodVelZ = (world.rand.nextDouble() - 0.5D) * 0.5D;

                // 331 = redstone dust based particles
                world.spawnParticle("iconcrack_331", bloodX, bloodY, bloodZ, bloodVelX, bloodVelY, bloodVelZ);
            }

            for (int i = 0; i < 25; i++) {
                double boneX = x + world.rand.nextDouble() - 0.5D;
                double boneY = y - 1.0D;// + world.rand.nextDouble() * 0.25D;
                double boneZ = z + world.rand.nextDouble() - 0.5D;

                double boneVelX = (world.rand.nextDouble() - 0.5D) * 0.5D;
                double boneVelY = 0.2D + world.rand.nextDouble() * 0.6D;
                double boneVelZ = (world.rand.nextDouble() - 0.5D) * 0.5D;

                // 352 = bone based particles
                world.spawnParticle("iconcrack_352", boneX, boneY, boneZ, boneVelX, boneVelY, boneVelZ);
            }
        });

        EffectHandler.effectMap.put(ENDERMAN_COLLECT_BLOCK_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            int blockID = data & 0xfff;
            int metadata = data >> 12 & 0xff;

            Block block = Block.blocksList[blockID];

            if (block != null) {
                world.playSound(x, y, z, block.stepSound.getBreakSound(), (block.stepSound.getBreakVolume() + 1.0F) / 2.0F, block.stepSound.getBreakPitch() * 0.8F);
                mcInstance.effectRenderer.addBlockDestroyEffects((int) x, (int) y, (int) z, blockID, metadata);
            }
        });

        EffectHandler.effectMap.put(ENDERMAN_CONVERT_BLOCK_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            int blockID = data & 0xfff;
            int metadata = data >> 12 & 0xff;
            Block block = Block.blocksList[blockID];

            if (block != null) {
                world.playSound(x, y, z, block.stepSound.getBreakSound(), (block.stepSound.getBreakVolume() + 1.0F) / 2.0F, block.stepSound.getBreakPitch() * 0.8F);
                mcInstance.effectRenderer.addBlockDestroyEffects((int) x, (int) y, (int) z, blockID, metadata);
            }

            for (int i = 0; i < 25; i++) {
                double particleX = x + (world.rand.nextDouble() - 0.5D) * 1.5D;
                double particleY = y + (world.rand.nextDouble() - 0.5D);
                double particleZ = z + (world.rand.nextDouble() - 0.5D) * 1.5D;

                world.spawnParticle("mobSpell", particleX, particleY, particleZ, 0D, 0D, 0D);
            }

            world.playSound(x, y, z, "mob.endermen.portal", 1.0F, 1.0F);
        });

        EffectHandler.effectMap.put(ENDERMAN_PLACE_BLOCK_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            int blockID = data & 0xfff;
            Block block = Block.blocksList[blockID];

            if (block != null) {
                world.playSound(x, y, z, block.stepSound.getStepSound(), (block.stepSound.getStepVolume() + 1.0F) / 2.0F, block.stepSound.getStepPitch() * 0.8F);
            }

            world.playSound(x, y, z, "mob.endermen.hit", 1.0F, 1.0F);
        });

        EffectHandler.effectMap.put(ENDERMAN_CHANGE_DIMENSION_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.spawnParticle("largeexplode", x, y, z, 0.0D, 0.0D, 0.0D);
            world.playSound(x, y, z, "ambient.weather.thunder", 3.0F, world.rand.nextFloat() * 0.4F + 0.8F);
        });

        EffectHandler.effectMap.put(SOUL_URN_SHATTER_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            for (int i = 0; i < 8; i++) {
                world.spawnParticle("snowballpoof", x, y, z, 0.0D, 0.0D, 0.0D);
            }

            world.playSound(x, y, z, "random.glass", 1.0F, 1.2F / (world.rand.nextFloat() * 0.2F + 0.9F));
            world.playSound(x, y, z, "mob.ghast.scream", 0.2F, world.rand.nextFloat() * 0.2F + 0.5F);

            for (int i = 0; i < 100; i++) {
                double particleX = x + world.rand.nextDouble() * 3D - 1.5D;
                double particleY = y + world.rand.nextDouble() * 3D - 1.5D;
                double particleZ = z + world.rand.nextDouble() * 3D - 1.5D;

                world.spawnParticle("mobSpell", particleX, particleY, particleZ, 0, 0, 0);
            }
        });

        EffectHandler.effectMap.put(MELON_EXPLODE_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            // 360 = melon slice based particles
            String particle = "iconcrack_360";

            for (int i = 0; i < 150; i++) {
                double particleX = x + world.rand.nextDouble() - 0.5D;
                double particleY = y - 0.45D;
                double particleZ = z + world.rand.nextDouble() - 0.5D;

                double particleVelX = (world.rand.nextDouble() - 0.5D) * 0.5D;
                double particleVelY = world.rand.nextDouble() * 0.7D;
                double particleVelZ = (world.rand.nextDouble() - 0.5D) * 0.5D;

                world.spawnParticle(particle, particleX, particleY, particleZ, particleVelX, particleVelY, particleVelZ);
            }

            world.playSound(x, y, z, "mob.zombie.wood", 0.2F, 0.60F + (world.rand.nextFloat() * 0.25F));
            world.playSound(x, y, z, "mob.slime.attack", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 0.6F);
        });

        EffectHandler.effectMap.put(PUMPKIN_EXPLODE_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            String particle = "iconcrack_" + BTWItems.cookedCarrot.itemID;

            for (int i = 0; i < 150; i++) {
                double particleX = x + world.rand.nextDouble() - 0.5D;
                double particleY = y - 0.45D;
                double particleZ = z + world.rand.nextDouble() - 0.5D;

                double particleVelX = (world.rand.nextDouble() - 0.5D) * 0.5D;
                double particleVelY = world.rand.nextDouble() * 0.7D;
                double particleVelZ = (world.rand.nextDouble() - 0.5D) * 0.5D;

                world.spawnParticle(particle, particleX, particleY, particleZ, particleVelX, particleVelY, particleVelZ);
            }

            world.playSound(x, y, z, "mob.zombie.wood", 0.2F, 0.60F + (world.rand.nextFloat() * 0.25F));
            world.playSound(x, y, z, "mob.slime.attack", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 0.6F);
        });

        EffectHandler.effectMap.put(GOURD_IMPACT_SOUND_EFFECT_ID, (mcInstance, world, player, x, y, z, data) ->
                world.playSound(x, y, z, "mob.zombie.wood", 0.1F, 0.40F + (world.rand.nextFloat() * 0.25F))
        );

        EffectHandler.effectMap.put(DESTROY_BLOCK_RESPECT_PARTICLE_SETTINGS_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            // regular block destroy does not respect particle setting options.  This effect produces the same results but won't
            // overload on particles in automated systems if particles are turned down
            int blockID = data & 0xfff;
            int metadata = data >> 12 & 0xff;

            Block block = Block.blocksList[blockID];

            if (block != null) {
                world.playSound(x, y, z, block.stepSound.getBreakSound(), (block.stepSound.getBreakVolume() + 1.0F) / 2.0F, block.stepSound.getBreakPitch() * 0.8F);

                if (mcInstance.gameSettings.particleSetting <= 1) {
                    for (int i = 0; i < 4; ++i) {
                        for (int j = 0; j < 4; ++j) {
                            for (int k = 0; k < 4; ++k) {
                                if (mcInstance.gameSettings.particleSetting == 0 || world.rand.nextInt(3) == 0) {
                                    double particleX = i + (i + 0.5D) / 4D;
                                    double particleY = j + (j + 0.5D) / 4D;
                                    double particleZ = k + (k + 0.5D) / 4D;

                                    EntityDiggingFX digEffect = (EntityDiggingFX) EntityList.createEntityOfType(EntityDiggingFX.class, world,
                                            particleX, particleY, particleZ,
                                            particleX - i - 0.5D, particleY - j - 0.5D, particleZ - k - 0.5D,
                                            block, 0, metadata, mcInstance.renderEngine);

                                    digEffect.applyRenderColor(metadata);
                                    mcInstance.effectRenderer.addEffect(digEffect);
                                }
                            }
                        }
                    }
                }
            }
        });

        EffectHandler.effectMap.put(COW_REGEN_MILK_EFFECT_ID, (mcInstance, world, player, x, y, z, data) ->
                world.playSound(x, y, z, "mob.slime.attack", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 0.6F)
        );

        EffectHandler.effectMap.put(COW_MILKING_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.playSound(x, y, z, "mob.slime.attack", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 0.6F);
            String milkParticle = "iconcrack_332"; // snowball

            for (int i = 0; i < 50; i++) {
                double particleX = x + world.rand.nextDouble() - 0.5D;
                double particleY = y - 0.45D;
                double particleZ = z + world.rand.nextDouble() - 0.5D;

                double particleVelX = (world.rand.nextDouble() - 0.5D) * 0.5D;
                double particleVelY = world.rand.nextDouble() * 0.25D;
                double particleVelZ = (world.rand.nextDouble() - 0.5D) * 0.5D;

                world.spawnParticle(milkParticle, particleX, particleY, particleZ, particleVelX, particleVelY, particleVelZ);
            }
        });

        EffectHandler.effectMap.put(COW_CONVERSION_TO_MOOSHROOM_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.spawnParticle("largeexplode", x, y, z, 0.0D, 0.0D, 0.0D);
            world.playSound(x, y, z, "mob.slime.attack", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);

            float hurtPitch = (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F;

            if (data > 0) {
                // child pitch
                hurtPitch += 0.5F;
            }

            world.playSound(x, y, z, "mob.cow.hurt", 1.0F, hurtPitch);
        });

        EffectHandler.effectMap.put(WOLF_HOWL_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            float soundVolume;
            float soundPitch;

            if (data > 0) {
                // dire howl
                soundVolume = 10F;
                soundPitch = (world.rand.nextFloat() - world.rand.nextFloat()) * 0.05F + 0.55F;
            } else {
                // regular wolf howl
                soundVolume = 8.5F;
                soundPitch = (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1F;
            }

            EntityPlayer localPlayer = mcInstance.thePlayer;

            if (localPlayer != null) {
                if (localPlayer.posY < 64) {
                    float volumeMultiplier = (float) (localPlayer.posY / 64D);

                    soundVolume *= volumeMultiplier;

                    if (soundVolume < 1F) {
                        soundVolume = 1F;
                    }
                }
            }

            world.playSound(x, y, z, "mob.wolf.howl", soundVolume, soundPitch);
        });

        EffectHandler.effectMap.put(WOLF_CONVERSION_TO_DIRE_WOLF_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.spawnParticle("largeexplode", x, y, z, 0.0D, 0.0D, 0.0D);
            world.playSound(x, y, z, "mob.slime.attack", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);
            world.playSound(x, y, z, "mob.wolf.growl", 8.5F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.05F + 0.55F);
        });

        EffectHandler.effectMap.put(CREEPER_SNIP_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.playSound(x, y, z, "mob.sheep.shear", 1.0F, 1.0F);
            world.playSound(x, y, z, "mob.slime.attack", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.1F + 0.7F);

            String creeperSnipParticle = "iconcrack_332"; // snowball

            for (int i = 0; i < 50; i++) {
                double particleX = x + world.rand.nextDouble() - 0.5D;
                double particleY = y - 0.45D;
                double particleZ = z + world.rand.nextDouble() - 0.5D;

                double particleVelX = (world.rand.nextDouble() - 0.5D) * 0.5D;
                double particleVelY = world.rand.nextDouble() * 0.25D;
                double particleVelZ = (world.rand.nextDouble() - 0.5D) * 0.5D;

                // 360 = melon slice based particles
                world.spawnParticle(creeperSnipParticle, particleX, particleY, particleZ, particleVelX, particleVelY, particleVelZ);
            }
        });

        EffectHandler.effectMap.put(POSSESSED_PIG_TRANSFORMATION_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.playSound(x, y, z, "mob.pig.death", 2.0F, world.rand.nextFloat() * 0.4F + 1.2F);
            world.playSound(x, y, z, "mob.zombiepig.zpigangry", 2.0F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F) * 1.8F);
            world.playSound(x, y, z, "mob.slime.attack", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);
            world.spawnParticle("largeexplode", x, y, z, 0.0D, 0.0D, 0.0D);

            for (int i = 0; i < 50; i++) {
                double particleX = x + world.rand.nextDouble() - 0.5D;
                double particleY = y - 1.0D;// + world.rand.nextDouble() * 0.25D;
                double particleZ = z + world.rand.nextDouble() - 0.5D;

                double particleVelX = (world.rand.nextDouble() - 0.5D) * 0.5D;
                double particleVelY = 0.2D + world.rand.nextDouble() * 0.6D;
                double particleVelZ = (world.rand.nextDouble() - 0.5D) * 0.5D;

                // 319 = raw pork based particles
                world.spawnParticle("iconcrack-319", particleX, particleY, particleZ, particleVelX, particleVelY, particleVelZ);
            }
        });

        EffectHandler.effectMap.put(POSSESSED_VILLAGER_TRANSFORMATION_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.playSound(x, y, z, "ambient.weather.thunder", 3.0F, world.rand.nextFloat() * 0.4F + 0.8F);
            world.playSound(x, y, z, "mob.ghast.affectionate scream", 2.0F, 0.5F + world.rand.nextFloat() * 0.25F);
            world.spawnParticle("largeexplode", x, y, z, 0.0D, 0.0D, 0.0D);

            // basically a duplicate of the witch particle effect
            for (int i = 0; i < world.rand.nextInt(35) + 10; ++i) {
                world.spawnParticle("witchMagic",
                        x + world.rand.nextGaussian() * 0.125D,
                        y + 2.0D + world.rand.nextGaussian() * 0.125D,
                        z + world.rand.nextGaussian() * 0.125D,
                        0.0D, 0.0D, 0.0D);
            }
        });

        EffectHandler.effectMap.put(SHEEP_REGROW_WOOL_EFFECT_ID, (mcInstance, world, player, x, y, z, data) ->
                world.playSound(x, y, z, "step.cloth", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F)
        );

        EffectHandler.effectMap.put(SQUID_TENTACLE_FLING_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.playSound(x, y, z, "mob.slime.attack", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 0.6F);

            if (mcInstance.gameSettings.particleSetting <= 1) {
                int blockID = Block.waterStill.blockID;
                int metadata = 0;

                Block block = Block.blocksList[blockID];

                for (int i = 0; i < 4; ++i) {
                    for (int j = 0; j < 4; ++j) {
                        for (int k = 0; k < 4; ++k) {
                            if (mcInstance.gameSettings.particleSetting == 0 || world.rand.nextInt(3) == 0) {
                                double particleX = i + (i + 0.5D) / 4D;
                                double particleY = j + (j + 0.5D) / 4D;
                                double particleZ = k + (k + 0.5D) / 4D;

                                EntityDiggingFX digEffect = (EntityDiggingFX) EntityList.createEntityOfType(EntityDiggingFX.class, world,
                                        particleX, particleY, particleZ,
                                        particleX - i - 0.5D, particleY - j - 0.5D, particleZ - k - 0.5D,
                                        block, 0, metadata, mcInstance.renderEngine);

                                digEffect.applyRenderColor(metadata);
                                mcInstance.effectRenderer.addEffect(digEffect);
                            }
                        }
                    }
                }
            }
        });

        EffectHandler.effectMap.put(CREATE_SNOW_GOLEM_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            for (int i = 0; i < 120; ++i) {
                world.spawnParticle("snowshovel",
                        (int) x + world.rand.nextDouble(), ((int) y - 2) + world.rand.nextDouble() * 2.5D, (int) z + world.rand.nextDouble(),
                        0.0D, 0.0D, 0.0D);
            }

            for (int i = 0; i < 8; i++) {
                world.spawnParticle("snowballpoof", x, y, z, 0.0D, 0.0D, 0.0D);
            }

            world.playSound(x, y, z, "random.glass", 1.0F, 1.2F / (world.rand.nextFloat() * 0.2F + 0.9F));
            world.playSound(x, y, z, "mob.enderdragon.growl", 0.25F, world.rand.nextFloat() * 0.2F + 1.8F);

            for (int i = 0; i < 100; i++) {
                double particleX = x + world.rand.nextDouble() * 3D - 1.5D;
                double particleY = y + world.rand.nextDouble() * 3D - 1.5D;
                double particleZ = z + world.rand.nextDouble() * 3D - 1.5D;

                world.spawnParticle("mobSpell", particleX, particleY, particleZ, 0, 0, 0);
            }
        });

        EffectHandler.effectMap.put(CREATE_IRON_GOLEM_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            for (int i = 0; i < 120; ++i) {
                world.spawnParticle("snowballpoof",
                        (int) x + world.rand.nextDouble(), ((int) y - 2) + world.rand.nextDouble() * 2.5D, (int) z + world.rand.nextDouble(),
                        0.0D, 0.0D, 0.0D);
            }

            for (int i = 0; i < 8; i++) {
                world.spawnParticle("snowballpoof", x, y, z, 0.0D, 0.0D, 0.0D);
            }

            world.playSound(x, y, z, "random.glass", 1.0F, 1.2F / (world.rand.nextFloat() * 0.2F + 0.9F));
            world.playSound(x, y, z, "mob.irongolem.death", 1.0F, world.rand.nextFloat() * 0.2F + 0.5F);
            world.playSound(x, y, z, "mob.enderdragon.growl", 0.5F, world.rand.nextFloat() * 0.2F + 1.5F);

            for (int i = 0; i < 100; i++) {
                double particleX = x + world.rand.nextDouble() * 3D - 1.5D;
                double particleY = y + world.rand.nextDouble() * 3D - 1.5D;
                double particleZ = z + world.rand.nextDouble() * 3D - 1.5D;

                world.spawnParticle("mobSpell", particleX, particleY, particleZ, 0, 0, 0);
            }
        });
        
        EffectHandler.effectMap.put(TOSS_THE_MILK_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            for (int i = 0; i < 120; ++i) {
                world.spawnParticle("snowballpoof",
                        (int) x + world.rand.nextDouble(), ((int) y - 2) + world.rand.nextDouble() * 2.5D, (int) z + world.rand.nextDouble(),
                        0.0D, 0.0D, 0.0D);
            }

            float soundPitch = 2.0F;

            if (data > 0) {
                soundPitch = 1.2F;
            }
            
            world.playSound(x, y, z, "mob.slime.attack", 0.5F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.1F + 0.6F);
            world.playSound(x, y, z, "random.classic_hurt", 0.25F, soundPitch);
        });
        
        EffectHandler.effectMap.put(APPLY_DUNG_TO_WOLF_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.playSound(x, y, z, "mob.wolf.whine", 0.5F, 1.5F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.4F);

            for (int i = 0; i < 15; i++) {
                double particleX = x + world.rand.nextDouble() - 0.5D;
                double particleY = y - 0.5D + world.rand.nextDouble() * 0.25D;
                double particleZ = z + world.rand.nextDouble() - 0.5D;

                double particleVelX = (world.rand.nextDouble() - 0.5D) * 0.25D;
                double particleVelY = 0.1D + world.rand.nextDouble() * 0.1D;
                double particleVelZ = (world.rand.nextDouble() - 0.5D) * 0.25D;

                // 319 = raw pork based particles
                world.spawnParticle("iconcrack_491", particleX, particleY, particleZ, particleVelX, particleVelY, particleVelZ);
            }

            world.playSound(x, y, z, "mob.slime.attack", 0.5F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.1F + 0.8F);
        });
        
        EffectHandler.effectMap.put(REMOVE_STUMP_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.playSound(x, y, z, "mob.slime.attack", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 0.6F);
            world.spawnParticle("largeexplode", x, y, z, 0.0D, 0.0D, 0.0D);

            for (int i = 0; i < 20; i++) {
                double smokeX = x + world.rand.nextDouble() - 0.5D;
                double smokeY = y + world.rand.nextDouble() - 0.5D;
                double smokeZ = z + world.rand.nextDouble() - 0.5D;

                double smokeVelX = (smokeX - x) * 0.33D;
                double smokeVelY = (smokeY - y) * 0.33D;
                double smokeVelZ = (smokeZ - z) * 0.33D;

                world.spawnParticle("smoke", smokeX, smokeY, smokeZ, smokeVelX, smokeVelY, smokeVelZ);
            }
        });
        
        EffectHandler.effectMap.put(SHAFT_RIPPED_OFF_EFFECT_ID, (mcInstance, world, player, x, y, z, data) ->
                world.playSound(x, y, z, "mob.zombie.woodbreak", 0.25F, 1.0F + (world.rand.nextFloat() * 0.25F))
        );
        
        EffectHandler.effectMap.put(STONE_RIPPED_OFF_EFFECT_ID, (mcInstance, world, player, x, y, z, data) ->
                world.playSound(x, y, z, "random.anvil_land", 0.5F, world.rand.nextFloat() * 0.25F + 1.75F)
        );
        
        EffectHandler.effectMap.put(GRAVEL_RIPPED_OFF_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.playSound(x, y, z, "random.anvil_land", 0.25F, world.rand.nextFloat() * 0.25F + 1.5F);
            world.playSound(x, y, z, "step.gravel", 1F, world.rand.nextFloat() * 0.25F + 1F);
        });
        
        EffectHandler.effectMap.put(WOOD_BLOCK_DESTROYED_EFFECT_ID, (mcInstance, world, player, x, y, z, data) ->
                world.playSound(x, y, z, "mob.zombie.woodbreak", 0.25F, 1.0F + (world.rand.nextFloat() * 0.25F))
        );
        
        EffectHandler.effectMap.put(BLOCK_DESTROYED_WITH_IMPROPER_TOOL_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            int blockID = data & 0xfff;
            Block block = Block.blocksList[blockID];

            if (block != null) {
                int metadata = data >> 12 & 0xff;

                // TODO: Make this more generic and extendable
                if (block.blockMaterial == BTWBlocks.plankMaterial || block.blockMaterial == BTWBlocks.logMaterial) {
                    world.playSound(x, y, z, "mob.zombie.woodbreak", 0.25F, 1.0F + (world.rand.nextFloat() * 0.25F));
                } else if (block.blockMaterial == Material.anvil) {
                    world.playSound(x, y, z, "random.anvil_land", 1F, world.rand.nextFloat() * 0.25F + 0.75F);
                }
            }
        });
        
        EffectHandler.effectMap.put(POSSESSED_SQUID_TRANSFORMATION_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.playSound(x, y, z, "mob.slime.attack", 1F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);
            world.playSound(x, y, z, "mob.ghast.scream", 10F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1F);

            for (int i = 0; i < 10; i++) {
                double explodeX = x + (world.rand.nextDouble() - 0.5D) * 4D;
                double explodeY = y + (world.rand.nextDouble() - 0.5D) * 4D;
                double explodeZ = z + (world.rand.nextDouble() - 0.5D) * 4D;

                world.spawnParticle("largeexplode", explodeX, explodeY, explodeZ, 0.0D, 0.0D, 0.0D);
            }
        });

        EffectHandler.effectMap.put(APPLY_MORTAR_EFFECT_ID, (mcInstance, world, player, x, y, z, data) ->
                world.playSound(x, y, z, "mob.slime.attack", 0.70F + world.rand.nextFloat() * 0.1F, 0.85F + world.rand.nextFloat() * 0.1F)
        );

        EffectHandler.effectMap.put(LOOSE_BLOCK_STUCK_TO_MORTAR_EFFECT_ID, (mcInstance, world, player, x, y, z, data) ->
                world.playSound(x, y, z, "mob.slime.attack", 0.15F + world.rand.nextFloat() * 0.1F, 0.6F + world.rand.nextFloat() * 0.1F)
        );

        EffectHandler.effectMap.put(SMOLDERING_LOG_FALL_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.playSound(x, y, z, "mob.zombie.woodbreak", 1.25F, 0.5F + world.rand.nextFloat() * 0.1F);
            world.playSound(x, y, z, "mob.ghast.fireball", 1F, 0.5F + world.rand.nextFloat() * 0.1F);
        });

        EffectHandler.effectMap.put(SMOLDERING_LOG_EXPLOSION_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.playSound(x, y, z, "mob.zombie.wood", 1.25F, 0.5F + world.rand.nextFloat() * 0.1F);
            world.spawnParticle("largeexplode", x, y, z, 0D, 0D, 0D);

            for (int i = 0; i < 10; i++) {
                world.spawnParticle("fccinders", x, y, z, 0D, 0D, 0D);
            }
        });

        EffectHandler.effectMap.put(WATER_EVAPORATION_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.playSound(x, y, z, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

            for (int i = 0; i < 8; i++) {
                world.spawnParticle("largesmoke",
                        x + world.rand.nextDouble() - 0.5D,
                        y + world.rand.nextDouble() - 0.5D,
                        z + world.rand.nextDouble() - 0.5D,
                        0D, 0D, 0D);
            }
        });

        EffectHandler.effectMap.put(CREATE_WITHER_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            for (int i = 0; i < 120; ++i) {
                world.spawnParticle("snowballpoof",
                        (int) x + world.rand.nextDouble(), ((int) y - 2) + world.rand.nextDouble() * 2.5D, (int) z + world.rand.nextDouble(),
                        0.0D, 0.0D, 0.0D);
            }

            for (int i = 0; i < 8; i++) {
                world.spawnParticle("snowballpoof", x, y, z, 0.0D, 0.0D, 0.0D);
            }

            world.playSound(x, y, z, "random.glass", 1.0F, 1.2F / (world.rand.nextFloat() * 0.2F + 0.9F));
            world.playSound(x, y, z, "mob.wither.death", 1.0F, world.rand.nextFloat() * 0.2F + 0.5F);
            world.playSound(x, y, z, "mob.enderdragon.growl", 0.5F, world.rand.nextFloat() * 0.2F + 1.5F);

            for (int i = 0; i < 100; i++) {
                double particleX = x + world.rand.nextDouble() * 3D - 1.5D;
                double particleY = y + world.rand.nextDouble() * 3D - 1.5D;
                double particleZ = z + world.rand.nextDouble() * 3D - 1.5D;

                world.spawnParticle("mobSpell", particleX, particleY, particleZ, 0, 0, 0);
            }
        });

        EffectHandler.effectMap.put(LIGHTNING_STRIKE_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.spawnParticle("largeexplode", x, y, z, 0D, 0D, 0D);
            world.playSound(x, y, z, "random.explode", 4F, 0.5F + world.rand.nextFloat() * 0.2F);
            world.playSound(x, y, z, "ambient.weather.thunder", 10000F, 0.8F + world.rand.nextFloat() * 0.2F);
        });

        EffectHandler.effectMap.put(FLAMING_NETHERRACK_FALL_EFFECT_ID, (mcInstance, world, player, x, y, z, data) ->
                world.playSound(x, y, z, "mob.ghast.fireball", 0.1F, 0.75F + world.rand.nextFloat() * 0.1F)
        );

        EffectHandler.effectMap.put(CACTUS_EXPLOSION_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            for (int i = 0; i < 150; i++) {
                double particleX = x + world.rand.nextDouble() - 0.5D;
                double particleY = y - 0.45D;
                double particleZ = z + world.rand.nextDouble() - 0.5D;

                double particleVelX = (world.rand.nextDouble() - 0.5D) * 0.5D;
                double particleVelY = world.rand.nextDouble() * 0.7D;
                double particleVelZ = (world.rand.nextDouble() - 0.5D) * 0.5D;

                // 338 = reed based particles
                world.spawnParticle("iconcrack_338", particleX, particleY, particleZ, particleVelX, particleVelY, particleVelZ);
            }
        });

        EffectHandler.effectMap.put(ANIMAL_EATING_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.playSound(x, y, z, "random.eat", 0.75F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 0.6F);

            for (int i = 0; i < 25; i++) {
                double particleX = x + world.rand.nextDouble() - 0.5D;
                double particleY = y;
                double particleZ = z + world.rand.nextDouble() - 0.5D;

                double particleVelX = (world.rand.nextDouble() - 0.5D) * 0.25D;
                double particleVelY = world.rand.nextDouble() * 0.35D;
                double particleVelZ = (world.rand.nextDouble() - 0.5D) * 0.25D;

                // 361 = pumpkin seeds
                world.spawnParticle("iconcrack_361", particleX, particleY, particleZ, particleVelX, particleVelY, particleVelZ);
            }
        });

        EffectHandler.effectMap.put(WOLF_EATING_EFFECT_ID, (mcInstance, world, player, x, y, z, data) -> {
            world.playSound(x, y, z, "random.burp", 1.0F, world.rand.nextFloat() * 0.4F + 0.7F);

            for (int i = 0; i < 25; i++) {
                double particleX = x + world.rand.nextDouble() - 0.5D;
                double particleY = y;
                double particleZ = z + world.rand.nextDouble() - 0.5D;

                double particleVelX = (world.rand.nextDouble() - 0.5D) * 0.25D;
                double particleVelY = world.rand.nextDouble() * 0.35D;
                double particleVelZ = (world.rand.nextDouble() - 0.5D) * 0.25D;

                // 281 = bowl based particles
                world.spawnParticle("iconcrack_281", particleX, particleY, particleZ, particleVelX, particleVelY, particleVelZ);
            }
        });

        EffectHandler.effectMap.put(FAILED_EATING_EFFECT_ID, (mcInstance, world, player, x, y, z, data) ->
                world.playSound(x, y, z, "random.burp", 0.25F, world.rand.nextFloat() * 0.3F + 1F)
        );
    }
}
