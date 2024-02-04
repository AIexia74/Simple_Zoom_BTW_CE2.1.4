// FCMOD

package btw.block.tileentity;

import btw.block.BTWBlocks;
import btw.block.blocks.UnfiredBrickBlock;
import net.minecraft.src.*;

public class UnfiredBrickTileEntity extends TileEntity
	implements TileEntityDataPacketHandler
{
    private static final int TIME_TO_COOK = (10 * 60 * 20 );
    private static final int RAIN_COOK_DECAY = 10;
    
	private int cookCounter = 0;
	
	private boolean isCooking = false;
	
    public UnfiredBrickTileEntity()
    {
    	super();
    }
    
    @Override
    public void readFromNBT( NBTTagCompound tag )
    {
        super.readFromNBT( tag );
        
        if( tag.hasKey( "fcCookCounter" ) )
        {
			cookCounter = tag.getInteger("fcCookCounter");
        }
    }
    
    @Override
    public void writeToNBT( NBTTagCompound tag)
    {
        super.writeToNBT( tag );
        
        tag.setInteger("fcCookCounter", cookCounter);
    }
        
    @Override
    public void updateEntity()
    {
    	super.updateEntity();   

    	if ( !worldObj.isRemote )
    	{
    		updateCooking();
    	}
    	else 
    	{    
    		if (isCooking)
    		{
				if ( worldObj.rand.nextInt( 20 ) == 0 )
				{
	                double xPos = xCoord + 0.25F + worldObj.rand.nextFloat() * 0.5F;
	                double yPos = yCoord + 0.5F + worldObj.rand.nextFloat() * 0.25F;
	                double zPos = zCoord + 0.25F + worldObj.rand.nextFloat() * 0.5F;
	                
	                worldObj.spawnParticle( "fcwhitesmoke", xPos, yPos, zPos, 0.0D, 0.0D, 0.0D );
	            }
    		}
    	}
    }
    
    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound tag = new NBTTagCompound();
        
        tag.setBoolean("x", isCooking);
        
        return new Packet132TileEntityData( xCoord, yCoord, zCoord, 1, tag );
    }
    
    //------------- FCITileEntityDataPacketHandler ------------//
    
    @Override
    public void readNBTFromPacket( NBTTagCompound tag )
    {
		isCooking = tag.getBoolean("x");

        // force a visual update for the block since the above variables affect it
        
        worldObj.markBlockRangeForRenderUpdate( xCoord, yCoord, zCoord, xCoord, yCoord, zCoord );        
    }
    
    //------------- Class Specific Methods ------------//
    
    public void updateCooking()
    {
		boolean bNewCooking;
    	int iBlockMaxNaturalLight = worldObj.getBlockNaturalLightValueMaximum(xCoord, yCoord, zCoord);
    	int iBlockCurrentNaturalLight = iBlockMaxNaturalLight - worldObj.skylightSubtracted;
    	
    	bNewCooking = iBlockCurrentNaturalLight >= 15;
    	
    	int iBlockAboveID = worldObj.getBlockId( xCoord, yCoord + 1, zCoord );
    	Block blockAbove = Block.blocksList[iBlockAboveID];
    	
    	if ( blockAbove != null && blockAbove.isGroundCover() )
    	{
    		bNewCooking = false;
    	}
    	
    	if (bNewCooking != isCooking)
    	{
			isCooking = bNewCooking;
		
    		worldObj.markBlockForUpdate( xCoord, yCoord, zCoord );
    	}
    	
    	UnfiredBrickBlock brickBlock = BTWBlocks.placedUnfiredBrick;
    	
    	if (isCooking)
    	{
    		cookCounter++;
    		
    		if (cookCounter >= TIME_TO_COOK)
    		{
    			brickBlock.onFinishedCooking(worldObj, xCoord, yCoord, zCoord);
    			
    			return;
    		}
    	}
    	else
    	{
    		if ( isRainingOnBrick(worldObj, xCoord, yCoord, zCoord) )
    		{
				cookCounter -= RAIN_COOK_DECAY;
    			
    			if (cookCounter < 0 )
    			{
					cookCounter = 0;
    			}
    		}    		
    	}
    	
    	int iDisplayedCookLevel = brickBlock.getCookLevel(worldObj, xCoord, yCoord, zCoord);
    	int iCurrentCookLevel = computeCookLevel();;
		
    	if ( iDisplayedCookLevel != iCurrentCookLevel )
    	{
    		brickBlock.setCookLevel(worldObj, xCoord, yCoord, zCoord, iCurrentCookLevel);
    	}
    }
    
    public boolean isRainingOnBrick(World world, int i, int j, int k)
    {
    	return world.isRaining() && world.isRainingAtPos(i, j, k);
    }
    
    private int computeCookLevel()
    {
    	if (cookCounter > 0 )
		{
			int iCookLevel= (int)(((float) cookCounter / (float) TIME_TO_COOK) * 7F ) + 1;
			
			return MathHelper.clamp_int( iCookLevel, 0, 7 );
		}
    	
    	return 0;
    }
    
    //----------- Client Side Functionality -----------//
}
