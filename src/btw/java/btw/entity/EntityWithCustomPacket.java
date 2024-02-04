// FCMOD

package btw.entity;

import btw.client.network.packet.handler.CustomEntityPacketHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Packet;

public interface EntityWithCustomPacket {
	/**
	 * @return Custom packet containing information for this entity to send from the server to the client
	 */
    Packet getSpawnPacketForThisEntity();
    
    int getTrackerViewDistance();
    
    int getTrackerUpdateFrequency();

    boolean getTrackMotion();

    /**
     * Partially disables server-side visibility tests for interacting with an entity
     */
    boolean shouldServerTreatAsOversized();
}