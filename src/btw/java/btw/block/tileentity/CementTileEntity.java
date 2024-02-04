// FCMOD

package btw.block.tileentity;

import btw.block.blocks.CementBlock;
import net.minecraft.src.*;

public class CementTileEntity extends TileEntity
	implements TileEntityDataPacketHandler
{
    private int dryTime;
    private int spreadDist;
    
	public CementTileEntity()
	{
        spreadDist = 0;
        dryTime = 0;
	}
	
    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        
        nbttagcompound.setInteger("dryTime", dryTime);
        nbttagcompound.setInteger("spreadDist", spreadDist);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        
        if( nbttagcompound.hasKey( "dryTime" ) )
        {
            dryTime = nbttagcompound.getInteger("dryTime");
        }
        else
        {
        	// if the cement doesn't have a drytime associated with it, set it to dry immediately

            dryTime = CementBlock.CEMENT_TICKS_TO_DRY;
        }
        
        if( nbttagcompound.hasKey( "spreadDist" ) )
        {
            spreadDist = nbttagcompound.getInteger("spreadDist");
        }
        else
        {
        	// if the cement doesn't have a spread associated with it, set it to not spread further

            spreadDist = CementBlock.MAX_CEMENT_SPREAD_DIST;
        }
    }
    
    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        
        nbttagcompound.setShort( "d", (short) dryTime);
        nbttagcompound.setShort( "s", (short) spreadDist);
        
        return new Packet132TileEntityData( xCoord, yCoord, zCoord, 1, nbttagcompound );
    }
    
    
    //------------- FCITileEntityDataPacketHandler ------------//
    
    @Override
    public void readNBTFromPacket( NBTTagCompound nbttagcompound )
    {
        dryTime = nbttagcompound.getShort("d");
        spreadDist = nbttagcompound.getShort("s");
        
        // force a visual update for the block since the above variables affect it
        
        worldObj.markBlockRangeForRenderUpdate( xCoord, yCoord, zCoord, xCoord, yCoord, zCoord );        
    }    
    
    //------------- Class Specific Methods ------------//
    
    public int getDryTime()
    {
    	return dryTime;
    }
    
    public void setDryTime(int iDryTime)
    {
        dryTime = iDryTime;
    	
    	// mark block to be sent to client
    	
        worldObj.markBlockForUpdate( xCoord, yCoord, zCoord );
    }
    
    public int getSpreadDist()
    {
    	return spreadDist;
    }
    
    public void setSpreadDist(int iSpreadDist)
    {
        spreadDist = iSpreadDist;
    	
    	// mark block to be sent to client
    	
        worldObj.markBlockForUpdate( xCoord, yCoord, zCoord );
    }
    
    //----------- Client Side Functionality -----------//
}