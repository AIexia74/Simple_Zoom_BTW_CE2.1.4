package btw.network.packet;

import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TimerSpeedPacket extends Packet {
	public float timerSpeed;
	
	public TimerSpeedPacket() {}
	
	public TimerSpeedPacket(float timerSpeed) {
		this.timerSpeed = timerSpeed;
	}
	
	@Override
	public void readPacketData(DataInputStream var1) throws IOException {
		timerSpeed = var1.readFloat();
	}
	
	@Override
	public void writePacketData(DataOutputStream var1) throws IOException {
		var1.writeFloat(timerSpeed);
	}
	
	@Override
	public void processPacket(NetHandler var1) {
		var1.handleTimerSpeed(this);
	}
	
	@Override
	public int getPacketSize() {
		return 4;
	}
}
