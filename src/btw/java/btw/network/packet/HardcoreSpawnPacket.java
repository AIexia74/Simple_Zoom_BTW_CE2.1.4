package btw.network.packet;

import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class HardcoreSpawnPacket extends Packet {
	public long timeOfLastSpawnAssignment;
	
	public HardcoreSpawnPacket() {}
	
	public HardcoreSpawnPacket(long timeOfLastSpawnAssignment) {
		this.timeOfLastSpawnAssignment = timeOfLastSpawnAssignment;
	}
	
	@Override
	public void readPacketData(DataInputStream dataInputStream) throws IOException {
		this.timeOfLastSpawnAssignment = dataInputStream.readLong();
	}
	
	@Override
	public void writePacketData(DataOutputStream dataOutputStream) throws IOException {
		dataOutputStream.writeLong(this.timeOfLastSpawnAssignment);
	}
	
	@Override
	public void processPacket(NetHandler netHandler) {
		netHandler.handleHardcoreSpawnSync(this);
	}
	
	@Override
	public int getPacketSize() {
		return 8;
	}
}
