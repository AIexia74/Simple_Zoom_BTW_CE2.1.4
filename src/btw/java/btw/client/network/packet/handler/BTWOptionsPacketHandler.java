package btw.client.network.packet.handler;

import btw.BTWMod;
import btw.network.packet.handler.CustomPacketHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.WorldClient;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

@Environment(EnvType.CLIENT)
public class BTWOptionsPacketHandler implements CustomPacketHandler {
    @Override
    public void handleCustomPacket(Packet250CustomPayload packet) throws IOException {
        WorldClient world = Minecraft.getMinecraft().theWorld;
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));

        byte bHardcoreBuoy = dataStream.readByte();

        BTWMod.serverEnableHardcoreBuoy = bHardcoreBuoy != 0;

        BTWMod.serverHardcorePlayerNamesLevel = dataStream.readByte();

        if (BTWMod.serverHardcorePlayerNamesLevel < 0 || BTWMod.serverHardcorePlayerNamesLevel > 2) {
            BTWMod.serverHardcorePlayerNamesLevel = 0;
        }
    }
}
