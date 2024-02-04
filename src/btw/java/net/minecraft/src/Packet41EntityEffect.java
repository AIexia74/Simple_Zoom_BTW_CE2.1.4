package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet41EntityEffect extends Packet
{
    public int entityId;
    public byte effectId;

    /** The effect's amplifier. */
    public byte effectAmplifier;
    public short duration;
    // FCMOD: Code added
    public byte isAmbient;
    // END FCMOD

    public Packet41EntityEffect() {}

    public Packet41EntityEffect(int par1, PotionEffect par2PotionEffect)
    {
        this.entityId = par1;
        this.effectId = (byte)(par2PotionEffect.getPotionID() & 255);
        this.effectAmplifier = (byte)(par2PotionEffect.getAmplifier() & 255);

        if (par2PotionEffect.getDuration() > 32767)
        {
            this.duration = 32767;
        }
        else
        {
            this.duration = (short)par2PotionEffect.getDuration();
        }

        // FCMOD: Code added
        isAmbient = 0;
        if ( par2PotionEffect.getIsAmbient() )
        {
            isAmbient = 1;
        }
        // END FCMOD
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.entityId = par1DataInputStream.readInt();
        this.effectId = par1DataInputStream.readByte();
        this.effectAmplifier = par1DataInputStream.readByte();
        this.duration = par1DataInputStream.readShort();
        // FCMOD: Code added
        isAmbient = par1DataInputStream.readByte();
        // END FCMOD
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.entityId);
        par1DataOutputStream.writeByte(this.effectId);
        par1DataOutputStream.writeByte(this.effectAmplifier);
        par1DataOutputStream.writeShort(this.duration);
        // FCMOD: Code added
        par1DataOutputStream.writeByte(isAmbient);
        // END FCMOD
    }

    /**
     * Returns true if duration is at maximum, false otherwise.
     */
    @Environment(EnvType.CLIENT)
    public boolean isDurationMax()
    {
        return this.duration == 32767;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleEntityEffect(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
    	return 9;
    }

    /**
     * only false for the abstract Packet class, all real packets return true
     */
    public boolean isRealPacket()
    {
        return true;
    }

    /**
     * eg return packet30entity.entityId == entityId; WARNING : will throw if you compare a packet to a different packet
     * class
     */
    public boolean containsSameEntityIDAs(Packet par1Packet)
    {
        Packet41EntityEffect var2 = (Packet41EntityEffect)par1Packet;
        return var2.entityId == this.entityId && var2.effectId == this.effectId;
    }
}