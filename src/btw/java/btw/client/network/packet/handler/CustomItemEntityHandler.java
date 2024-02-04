package btw.client.network.packet.handler;

import btw.network.packet.BTWPacketManager;
import btw.entity.item.BloodWoodSaplingItemEntity;
import btw.entity.item.FloatingItemEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.io.DataInputStream;
import java.io.IOException;

@Environment(EnvType.CLIENT)
public class CustomItemEntityHandler implements CustomEntityPacketHandler.EntityPacketHandlerEntry {
    private int entityType;

    public CustomItemEntityHandler(int entityType) {
        this.entityType = entityType;
    }

    @Override
    public Entity handleEntitySpawn(WorldClient world, DataInputStream dataStream, Packet250CustomPayload packet) throws IOException {
        double x = ((double) (dataStream.readInt())) / 32D;
        double y = ((double) (dataStream.readInt())) / 32D;
        double z = ((double) (dataStream.readInt())) / 32D;

        int itemID = dataStream.readInt();
        int stackSize = dataStream.readInt();
        int itemDamage = dataStream.readInt();

        double motionX = ((double) (dataStream.readByte())) * 128D;
        double motionY = ((double) (dataStream.readByte())) * 128D;
        double motionZ = ((double) (dataStream.readByte())) * 128D;

        Entity entityToSpawn;

        // TODO: make this more generic
        if (entityType == BTWPacketManager.BLOOD_WOOD_SAPLING_ITEM_SPAWN_PACKET_ID) {
            entityToSpawn = EntityList.createEntityOfType(BloodWoodSaplingItemEntity.class, world, x, y, z, new ItemStack(itemID, stackSize, itemDamage));
        }
        else {
            entityToSpawn = EntityList.createEntityOfType(FloatingItemEntity.class, world, x, y, z, new ItemStack(itemID, stackSize, itemDamage));
        }

        entityToSpawn.motionX = motionX;
        entityToSpawn.motionY = motionY;
        entityToSpawn.motionZ = motionZ;

        entityToSpawn.serverPosX = (int) (x * 32D);
        entityToSpawn.serverPosY = (int) (y * 32D);
        entityToSpawn.serverPosZ = (int) (z * 32D);

        return entityToSpawn;
    }
}
