// FCMOD

package btw.network.packet;

import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StartBlockHarvestPacket extends Packet
{
    public int posX;
    public int posY;
    public int posZ;
    
    public int face;
    
    private int miningSpeedModifier;

    public StartBlockHarvestPacket() {}

    public StartBlockHarvestPacket(int i, int j, int k, int iHitFace, float fMiningSpeedModifier )
    {
        posX = i;
        posY = j;
        posZ = k;

        face = iHitFace;

        miningSpeedModifier = (int)(fMiningSpeedModifier * 8000F );
    }

    public void readPacketData( DataInputStream stream ) throws IOException
    {
        posX = stream.readInt();
        posY = stream.read();
        posZ = stream.readInt();

        face = stream.read();

        miningSpeedModifier = stream.readShort();
    }

    public void writePacketData( DataOutputStream stream ) throws IOException
    {
        stream.writeInt(posX);
        stream.write(posY);
        stream.writeInt(posZ);
        
        stream.write(face);
        
        stream.writeShort(miningSpeedModifier);
    }

    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleStartBlockHarvest(this);
    }

    public int getPacketSize()
    {
        return 12;
    }
    
    public float getMiningSpeedModifier()
    {
    	return (float) miningSpeedModifier / 8000F;
    }
}
