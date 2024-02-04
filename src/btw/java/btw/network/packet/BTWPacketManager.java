package btw.network.packet;

import btw.BTWMod;
import btw.client.network.packet.handler.*;
import btw.entity.*;
import btw.entity.mechanical.platform.BlockLiftedByPlatformEntity;
import btw.entity.mechanical.source.VerticalWindMillEntity;
import btw.entity.mechanical.source.WaterWheelEntity;
import btw.entity.mechanical.source.WindMillEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityList;
import net.minecraft.src.Packet;

public class BTWPacketManager {
    public static final String SPAWN_CUSTOM_ENTITY_PACKET_CHANNEL = "BTW|SE";
    public static final String CUSTOM_ENTITY_EVENT_PACKET_CHANNEL = "BTW|EV";
    public static final String BTW_OPTIONS_PACKET_CHANNEL = "BTW|OP";
    public static final String BTW_DIFFICULTY_PACKET_CHANNEL = "BTW|DF";
    public static final String CUSTOM_INTERFACE_PACKET_CHANNEL = "BTW|OI";

    public static final int CANVAS_SPAWN_PACKET_ID = 0;
    public static final int WIND_MILL_SPAWN_PACKET_ID = 1;
    public static final int WATER_WHEEL_SPAWN_PACKET_ID = 2;
    public static final int MINING_CHARGE_SPAWN_PACKET_ID = 3;
    public static final int BLOOD_WOOD_SAPLING_ITEM_SPAWN_PACKET_ID = 4;
    public static final int FLOATING_ITEM_SPAWN_PACKET_ID = 5;
    public static final int DYNAMITE_SPAWN_PACKET_ID = 6;
    public static final int URN_SPAWN_PACKET_ID = 7;
    public static final int BLOCK_LIFTED_BY_PLATFORM_SPAWN_PACKET_ID = 8;
    public static final int VERTICAL_WIND_MILL_SPAWN_PACKET_ID = 9;
    public static final int SPIDER_WEB_SPAWN_PACKET_ID = 10;
    public static final int SOUL_SAND_SPAWN_PACKET_ID = 11;

    public static void initPacketInfo() {
        if (!MinecraftServer.getIsServer()) {
            BTWMod.instance.registerPacketHandler(SPAWN_CUSTOM_ENTITY_PACKET_CHANNEL, new CustomEntityPacketHandler());
            BTWMod.instance.registerPacketHandler(CUSTOM_ENTITY_EVENT_PACKET_CHANNEL, new EntityEventPacketHandler());
            BTWMod.instance.registerPacketHandler(BTW_OPTIONS_PACKET_CHANNEL, new BTWOptionsPacketHandler());
            BTWMod.instance.registerPacketHandler(BTW_DIFFICULTY_PACKET_CHANNEL, new BTWDifficultyPacketHandler());
            BTWMod.instance.registerPacketHandler(CUSTOM_INTERFACE_PACKET_CHANNEL, new GuiPacketHandler());
            
            initEntitySpawnEntries();
        }

        Packet.addIdClassMapping(166, false, true, StartBlockHarvestPacket.class);
        Packet.addIdClassMapping(21,true, true, PlayerSyncPacket.class);
        Packet.addIdClassMapping(167, true, false, TimerSpeedPacket.class);
        Packet.addIdClassMapping(168, true, false, HardcoreSpawnPacket.class);
    }

    // TODO: Maybe figure out some way to make this more OOP-friendly
    // For now registering handlers is better than the old monolithic switch statement
    @Environment(EnvType.CLIENT)
    private static void initEntitySpawnEntries() {
        CustomEntityPacketHandler.entryMap.put(CANVAS_SPAWN_PACKET_ID, (world, dataStream, packet) -> {
            int x = dataStream.readInt();
            int y = dataStream.readInt();
            int z = dataStream.readInt();
            int direction = dataStream.readInt();
            int artNum = dataStream.readInt();

            return EntityList.createEntityOfType(CanvasEntity.class, world, x, y, z, direction, artNum);
        });

        CustomEntityPacketHandler.entryMap.put(WIND_MILL_SPAWN_PACKET_ID, (world, dataStream, packet) -> {
            double x = dataStream.readInt() / 32D;
            double y = dataStream.readInt() / 32D;
            double z = dataStream.readInt() / 32D;

            boolean isXAligned = (dataStream.readByte()) > 0;

            WindMillEntity entityWindMill = (WindMillEntity) EntityList.createEntityOfType(WindMillEntity.class, world, x, y, z, isXAligned);

            entityWindMill.setRotationSpeedScaled(dataStream.readInt());

            entityWindMill.setBladeColor(0, dataStream.readByte());
            entityWindMill.setBladeColor(1, dataStream.readByte());
            entityWindMill.setBladeColor(2, dataStream.readByte());
            entityWindMill.setBladeColor(3, dataStream.readByte());

            return entityWindMill;
        });

        CustomEntityPacketHandler.entryMap.put(WATER_WHEEL_SPAWN_PACKET_ID, (world, dataStream, packet) -> {
            double x = dataStream.readInt() / 32D;
            double y = dataStream.readInt() / 32D;
            double z = dataStream.readInt() / 32D;

            boolean isXAligned = (dataStream.readByte()) > 0;

            WaterWheelEntity entityWaterWheel = (WaterWheelEntity) EntityList.createEntityOfType(WaterWheelEntity.class, world, x, y, z, isXAligned);

            entityWaterWheel.setRotationSpeedScaled(dataStream.readInt());

            return entityWaterWheel;
        });

        CustomEntityPacketHandler.entryMap.put(MINING_CHARGE_SPAWN_PACKET_ID, (world, dataStream, packet) -> {
            double x = dataStream.readInt() / 32D;
            double y = dataStream.readInt() / 32D;
            double z = dataStream.readInt() / 32D;

            int facing = dataStream.readByte();
            int fuse = dataStream.readByte();

            boolean isAttachedToBlock = (dataStream.readByte()) > 0;

            return EntityList.createEntityOfType(MiningChargeEntity.class, world, x, y, z, facing, fuse, isAttachedToBlock);
        });

        CustomEntityPacketHandler.entryMap.put(BLOOD_WOOD_SAPLING_ITEM_SPAWN_PACKET_ID, new CustomItemEntityHandler(BLOOD_WOOD_SAPLING_ITEM_SPAWN_PACKET_ID));

        CustomEntityPacketHandler.entryMap.put(FLOATING_ITEM_SPAWN_PACKET_ID, new CustomItemEntityHandler(FLOATING_ITEM_SPAWN_PACKET_ID));

        CustomEntityPacketHandler.entryMap.put(DYNAMITE_SPAWN_PACKET_ID, (world, dataStream, packet) -> {
            double x = ((double) (dataStream.readInt())) / 32D;
            double y = ((double) (dataStream.readInt())) / 32D;
            double z = ((double) (dataStream.readInt())) / 32D;

            int itemID = dataStream.readInt();
            int fuse = dataStream.readInt();

            double motionX = ((double) (dataStream.readByte())) * 128D;
            double motionY = ((double) (dataStream.readByte())) * 128D;
            double motionZ = ((double) (dataStream.readByte())) * 128D;

            DynamiteEntity dynamite = (DynamiteEntity) EntityList.createEntityOfType(DynamiteEntity.class, world, x, y, z, itemID);

            dynamite.fuse = fuse;

            dynamite.motionX = motionX;
            dynamite.motionY = motionY;
            dynamite.motionZ = motionZ;

            dynamite.serverPosX = (int) (x * 32D);
            dynamite.serverPosY = (int) (y * 32D);
            dynamite.serverPosZ = (int) (z * 32D);

            return dynamite;
        });

        CustomEntityPacketHandler.entryMap.put(URN_SPAWN_PACKET_ID, (world, dataStream, packet) -> {
            double x = ((double) (dataStream.readInt())) / 32D;
            double y = ((double) (dataStream.readInt())) / 32D;
            double z = ((double) (dataStream.readInt())) / 32D;

            int itemID = dataStream.readInt();

            double motionX = ((double) (dataStream.readByte())) * 128D;
            double motionY = ((double) (dataStream.readByte())) * 128D;
            double motionZ = ((double) (dataStream.readByte())) * 128D;

            Entity entityToSpawn = EntityList.createEntityOfType(UrnEntity.class, world, x, y, z, itemID);

            entityToSpawn.motionX = motionX;
            entityToSpawn.motionY = motionY;
            entityToSpawn.motionZ = motionZ;

            entityToSpawn.serverPosX = (int) (x * 32D);
            entityToSpawn.serverPosY = (int) (y * 32D);
            entityToSpawn.serverPosZ = (int) (z * 32D);

            return entityToSpawn;
        });

        CustomEntityPacketHandler.entryMap.put(BLOCK_LIFTED_BY_PLATFORM_SPAWN_PACKET_ID, (world, dataStream, packet) -> {
            double x = ((double) (dataStream.readInt())) / 32D;
            double y = ((double) (dataStream.readInt())) / 32D;
            double z = ((double) (dataStream.readInt())) / 32D;

            int blockID = dataStream.readInt();
            int metadata = dataStream.readInt();

            return EntityList.createEntityOfType(BlockLiftedByPlatformEntity.class, world, x, y, z, blockID, metadata);
        });

        CustomEntityPacketHandler.entryMap.put(VERTICAL_WIND_MILL_SPAWN_PACKET_ID, (world, dataStream, packet) -> {
            double x = ((double) (dataStream.readInt())) / 32D;
            double y = ((double) (dataStream.readInt())) / 32D;
            double z = ((double) (dataStream.readInt())) / 32D;

            VerticalWindMillEntity entityWindMill = (VerticalWindMillEntity) EntityList.createEntityOfType(VerticalWindMillEntity.class, world, x, y, z);

            entityWindMill.setRotationSpeedScaled(dataStream.readInt());

            entityWindMill.setBladeColor(0, dataStream.readByte());
            entityWindMill.setBladeColor(1, dataStream.readByte());
            entityWindMill.setBladeColor(2, dataStream.readByte());
            entityWindMill.setBladeColor(3, dataStream.readByte());
            entityWindMill.setBladeColor(4, dataStream.readByte());
            entityWindMill.setBladeColor(5, dataStream.readByte());
            entityWindMill.setBladeColor(6, dataStream.readByte());
            entityWindMill.setBladeColor(7, dataStream.readByte());

            return entityWindMill;
        });

        CustomEntityPacketHandler.entryMap.put(SPIDER_WEB_SPAWN_PACKET_ID, (world, dataStream, packet) -> {
            double x = dataStream.readInt() / 32D;
            double y = dataStream.readInt() / 32D;
            double z = dataStream.readInt() / 32D;

            double motionX = dataStream.readByte() * 128D;
            double motionY = dataStream.readByte() * 128D;
            double motionZ = dataStream.readByte() * 128D;

            Entity entityToSpawn = EntityList.createEntityOfType(SpiderWebEntity.class, world, x, y, z);

            entityToSpawn.motionX = motionX;
            entityToSpawn.motionY = motionY;
            entityToSpawn.motionZ = motionZ;

            entityToSpawn.serverPosX = (int) (x * 32D);
            entityToSpawn.serverPosY = (int) (y * 32D);
            entityToSpawn.serverPosZ = (int) (z * 32D);

            world.playSound(x, y, z, "mob.slime.attack", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 0.6F);

            return entityToSpawn;
        });

        CustomEntityPacketHandler.entryMap.put(SOUL_SAND_SPAWN_PACKET_ID, (world, dataStream, packet) -> {
            double x = dataStream.readInt() / 32D;
            double y = dataStream.readInt() / 32D;
            double z = dataStream.readInt() / 32D;

            double motionX = dataStream.readByte() * 128D;
            double motionY = dataStream.readByte() * 128D;
            double motionZ = dataStream.readByte() * 128D;

            Entity entityToSpawn = EntityList.createEntityOfType(SoulSandEntity.class, world, x, y, z);

            entityToSpawn.motionX = motionX;
            entityToSpawn.motionY = motionY;
            entityToSpawn.motionZ = motionZ;

            entityToSpawn.serverPosX = (int) (x * 32D);
            entityToSpawn.serverPosY = (int) (y * 32D);
            entityToSpawn.serverPosZ = (int) (z * 32D);

            world.playSound(x, y, z, "dig.sand", 1.0F, 1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F);

            return entityToSpawn;
        });
    }
}
