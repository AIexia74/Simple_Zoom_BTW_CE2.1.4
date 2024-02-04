package btw.client.network.packet.handler;

import btw.inventory.BTWContainers;
import btw.network.packet.handler.CustomPacketHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.WorldClient;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

@Environment(EnvType.CLIENT)
public class GuiPacketHandler implements CustomPacketHandler {
    @Override
    public void handleCustomPacket(Packet250CustomPayload packet) throws IOException {
        Minecraft mcInstance = Minecraft.getMinecraft();

        WorldClient world = mcInstance.theWorld;
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));

        int windowID = dataStream.readInt();
        int containerID = dataStream.readInt();

        EntityClientPlayerMP player = mcInstance.thePlayer;

        GuiContainer gui = BTWContainers.getAssociatedGui(player, containerID);

        if (gui != null) {
            mcInstance.displayGuiScreen(gui);

            player.openContainer.windowId = windowID;
        }
    }
}
