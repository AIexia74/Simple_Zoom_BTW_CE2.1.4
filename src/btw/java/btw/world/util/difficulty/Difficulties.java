package btw.world.util.difficulty;

import btw.network.packet.BTWPacketManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

public class Difficulties {
	public static final ArrayList<Difficulty> DIFFICULTY_LIST = new ArrayList<>();
	
	public static final Difficulty STANDARD = new Difficulty("standard");
	public static final Difficulty RELAXED = new RelaxedDifficulty("relaxed");
	
	public static Difficulty getDifficultyFromName(String name) {
		for (Difficulty c : DIFFICULTY_LIST) {
			if (c.NAME.equals(name)) {
				return c;
			}
		}
		
		return STANDARD;
	}
	
	public static String[] getAllDifficultyNames() {
		String[] names = new String[DIFFICULTY_LIST.size()];
		
		for (int i = 0; i < DIFFICULTY_LIST.size(); i++) {
			names[i] = DIFFICULTY_LIST.get(i).NAME;
		}
		
		return names;
	}
	
	/**
	 * Creates a packet that can be sent to the client to update the difficulty.
	 * @param mcServer The server to get the difficulty from.
	 * @return The difficulty packet.
	 */
	public static Packet createDifficultyPacket(MinecraftServer mcServer) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream(byteStream);
		
		byte difficulty = (byte) mcServer.worldServers[0].getWorldInfo().getDifficulty().ID;
		try {
			dataStream.writeByte(difficulty);
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}

		return new Packet250CustomPayload(BTWPacketManager.BTW_DIFFICULTY_PACKET_CHANNEL, byteStream.toByteArray());
	}
}
