// FCMOD

package btw.block.tileentity;

import net.minecraft.src.NBTTagCompound;

public interface TileEntityDataPacketHandler
{
    public void readNBTFromPacket( NBTTagCompound nbttagcompound );
}