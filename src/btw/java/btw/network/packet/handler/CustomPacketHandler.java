package btw.network.packet.handler;

import net.minecraft.src.Packet250CustomPayload;

import java.io.IOException;

public interface CustomPacketHandler {
    void handleCustomPacket(Packet250CustomPayload packet) throws IOException;
}
