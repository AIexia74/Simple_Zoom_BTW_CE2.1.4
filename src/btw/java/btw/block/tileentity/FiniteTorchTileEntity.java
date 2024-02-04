// FCMOD

package btw.block.tileentity;

import btw.block.BTWBlocks;
import btw.block.blocks.FireBlock;
import btw.block.blocks.TorchBlockBase;
import btw.block.blocks.FiniteBurningTorchBlock;
import btw.block.blocks.FiniteUnlitTorchBlock;
import net.minecraft.src.Block;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;

public class FiniteTorchTileEntity extends TileEntity
{
    static private final float CHANCE_OF_FIRE_SPREAD = 0.01F;
    static private final float CHANCE_OF_GOING_OUT_FROM_RAIN = 0.01F;
    
    static public final int MAX_BURN_TIME = 24000; // full day
	static public final int SPUTTER_TIME = 30 * 20; // 30 seconds
	
	public int burnTimeCountdown = 0;
	
    public FiniteTorchTileEntity()
    {
    	super();

		burnTimeCountdown = MAX_BURN_TIME;
    }
    
    @Override
    public void readFromNBT( NBTTagCompound tag )
    {
        super.readFromNBT( tag );
        
        if( tag.hasKey( "fcBurnCounter" ) )
        {
			burnTimeCountdown = tag.getInteger("fcBurnCounter");
        }
    }
    
    @Override
    public void writeToNBT( NBTTagCompound tag)
    {
        super.writeToNBT( tag );
        
        tag.setInteger("fcBurnCounter", burnTimeCountdown);
    }
        
    @Override
    public void updateEntity()
    {
    	super.updateEntity();   

    	if ( !worldObj.isRemote )
    	{
			burnTimeCountdown--;
			
			if (burnTimeCountdown <= 0 || (worldObj.rand.nextFloat() <= CHANCE_OF_GOING_OUT_FROM_RAIN && isRainingOnTorch() ) )
			{				
				// wait until all chunks in vicinity are loaded to avoid lighting glitches
				
				if ( worldObj.doChunksNearChunkExist( xCoord, yCoord, zCoord, 32 ) )
				{
					extinguishTorch();
				}
				else
				{
					// ensure it goes out once fully loaded

					burnTimeCountdown = 0;
				}				
			}
			else 
			{
				if (burnTimeCountdown < SPUTTER_TIME)
				{
			    	int iMetadata = worldObj.getBlockMetadata( xCoord, yCoord, zCoord );
			    	
			    	if ( !FiniteBurningTorchBlock.getIsSputtering(iMetadata) )
			    	{
			    		FiniteBurningTorchBlock block = (FiniteBurningTorchBlock)(Block.blocksList[worldObj.getBlockId( xCoord, yCoord, zCoord )]);
			    		
			    		block.setIsSputtering(worldObj, xCoord, yCoord, zCoord, true);
			    	}
				}
				
	    		if (worldObj.rand.nextFloat() <= CHANCE_OF_FIRE_SPREAD)
	    		{
    				FireBlock.checkForFireSpreadAndDestructionToOneBlockLocation(worldObj, xCoord, yCoord + 1, zCoord);
	    		}
			}
    	}
    	else
    	{
	    	int iMetadata = worldObj.getBlockMetadata( xCoord, yCoord, zCoord );
	    	
	    	if ( FiniteBurningTorchBlock.getIsSputtering(iMetadata) )
	    	{ 
	    		sputter();
	    	}
    	}
    }
    
    //------------- Class Specific Methods ------------//
    
    private boolean isRainingOnTorch()
    {
    	FiniteBurningTorchBlock block = (FiniteBurningTorchBlock)(Block.blocksList[worldObj.getBlockId( xCoord, yCoord, zCoord )]);
    	
    	return block.isRainingOnTorch(worldObj, xCoord, yCoord, zCoord);
    }
    
    private void extinguishTorch()
    {
		burnTimeCountdown = 0;
    	
    	int iOldMetadata = worldObj.getBlockMetadata( xCoord, yCoord, zCoord );
    	int iOrientation = TorchBlockBase.getOrientation(iOldMetadata);
    	
    	int iNewMetadata = TorchBlockBase.setOrientation(0, iOrientation);
    	iNewMetadata = FiniteUnlitTorchBlock.setIsBurnedOut(iNewMetadata, true);
    	
		worldObj.playAuxSFX( 1004, xCoord, yCoord, zCoord, 0 );
		
    	worldObj.setBlockAndMetadataWithNotify( xCoord, yCoord, zCoord, BTWBlocks.finiteUnlitTorch.blockID, iNewMetadata );
    }
    
    private void sputter()
    {
    	int iMetadata = worldObj.getBlockMetadata( xCoord, yCoord, zCoord );
    	
        int iOrientation = TorchBlockBase.getOrientation(iMetadata);
        
        double dHorizontalOffset = 0.27D;

        double xPos = xCoord + 0.5D + ( worldObj.rand.nextDouble() * 0.1D - 0.05D );
        double yPos = yCoord + 0.92D + ( worldObj.rand.nextDouble() * 0.1D - 0.05D );
        double zPos = zCoord + 0.5D + ( worldObj.rand.nextDouble() * 0.1D - 0.05D );
        
        if ( iOrientation == 1 )
        {
        	xPos -= dHorizontalOffset;
        }
        else if ( iOrientation == 2 )
        {
        	xPos += dHorizontalOffset;
        }
        else if ( iOrientation == 3 )
        {
        	zPos -= dHorizontalOffset;        	
        }
        else if ( iOrientation == 4 )
        {
        	zPos += dHorizontalOffset;
        }
        else
        {
        	yPos -= 0.22D;
        }
        
        worldObj.spawnParticle("smoke", xPos, yPos, zPos, 0.0D, 0.0D, 0.0D);
    }
    
    //----------- Client Side Functionality -----------//
}