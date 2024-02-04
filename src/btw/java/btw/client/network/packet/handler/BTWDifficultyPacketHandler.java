package btw.client.network.packet.handler;

import btw.network.packet.handler.CustomPacketHandler;
import btw.world.util.difficulty.Difficulties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.WorldClient;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

@Environment(EnvType.CLIENT)
public class BTWDifficultyPacketHandler implements CustomPacketHandler {
    @Override
    public void handleCustomPacket(Packet250CustomPayload packet) throws IOException {
        WorldClient world = Minecraft.getMinecraft().theWorld;
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));

        byte difficultyID = dataStream.readByte();
        
        world.worldInfo.setDifficulty(Difficulties.DIFFICULTY_LIST.get(difficultyID));
    }
}
