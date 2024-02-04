package btw.network.packet;

import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerSyncPacket extends Packet {
	
	public String playerName;
	
	public PlayerSyncPacket(String playername){
		this.playerName=playername;
	}
	
	public PlayerSyncPacket(){
	
	}
	
	@Override
	public void readPacketData(DataInputStream inputStream) throws IOException {
		playerName = readString(inputStream, 16);
	}
	
	@Override
	public void writePacketData(DataOutputStream outputStream) throws IOException {
		writeString(playerName, outputStream);
	}
	
	@Override
	public void processPacket(NetHandler netHandler) {
		netHandler.handlePlayerSync(this);
	}
	
	@Override
	public int getPacketSize() {
		return 2 + playerName.length()*2;
	}
}
