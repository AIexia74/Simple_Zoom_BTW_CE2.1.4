package btw.client.network.packet.handler;

import btw.network.packet.handler.CustomPacketHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class CustomEntityPacketHandler implements CustomPacketHandler {
    public static Map<Integer, EntityPacketHandlerEntry> entryMap = new HashMap<>();

    @Override
    public void handleCustomPacket(Packet250CustomPayload packet) throws IOException {
            WorldClient world = Minecraft.getMinecraft().theWorld;
            DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));

            Entity entityToSpawn = null;

            int entityType = dataStream.readInt();
            int entityId = dataStream.readInt();

            if (entryMap.get(entityType) != null) {
                entityToSpawn = entryMap.get(entityType).handleEntitySpawn(world, dataStream, packet);
            }

            if (entityToSpawn != null) {
                world.addEntityToWorld(entityId, entityToSpawn);
            }
    }

    public interface EntityPacketHandlerEntry {
        Entity handleEntitySpawn(WorldClient world, DataInputStream dataStream, Packet250CustomPayload packet) throws IOException;
    }
}
