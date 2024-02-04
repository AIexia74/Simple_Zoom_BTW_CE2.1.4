// FCMOD

package btw.block.tileentity;

import btw.block.blocks.BasketBlock;
import net.minecraft.src.TileEntity;

public abstract class BasketTileEntity extends TileEntity
{
    protected static final float LID_OPEN_RATE = 0.1F;
    protected static final float LID_CLOSE_RATE = 0.2F;
    
	protected static final float MAX_KEEP_OPEN_RANGE = 8F;
	
	public float lidOpenRatio = 0F;
	public float prevLidOpenRatio = 0F;
	
	public boolean closing = false;
	
	BasketBlock blockBasket;
	
    public BasketTileEntity(BasketBlock blockBasket )
    {
    	super();

		this.blockBasket = blockBasket;
    }
    
    @Override
    public void updateEntity()
    {
    	super.updateEntity();   

		updateOpenState();
    }
    
    @Override
    public boolean receiveClientEvent( int iEventType, int iEventParam )
    {
        if ( iEventType == 1)
        {
			closing = iEventParam == 1;
        	
            return true;
        }
        
        return super.receiveClientEvent( iEventType, iEventParam );
    }
    
    //------------- Class Specific Methods ------------//
    
    public abstract void ejectContents();
    
    public void startClosingServerSide()
    {
		closing = true;
		
        worldObj.addBlockEvent( xCoord, yCoord, zCoord, getBlockType().blockID, 1, 1 );
    }
    
    private void updateOpenState()
    {
		prevLidOpenRatio = lidOpenRatio;
    	
    	if ( blockBasket.getIsOpen(worldObj, xCoord, yCoord, zCoord) )
    	{
    		if (closing)
    		{
				lidOpenRatio -= LID_CLOSE_RATE;
    			
    			if (lidOpenRatio <= 0F )
    			{
					lidOpenRatio = 0F;
    				
    				if ( !worldObj.isRemote )
    				{
    					blockBasket.setIsOpen(worldObj, xCoord, yCoord, zCoord, false);
    					
    					onFinishedClosing();
    				}
    			}
    		}
    		else
    		{
	    		if ( shouldStartClosingServerSide() )
				{
	    			startClosingServerSide();
				}
	    		else
	    		{
					lidOpenRatio += LID_OPEN_RATE;
	    			
	    			if (lidOpenRatio > 1F )
	    			{
						lidOpenRatio = 1F;
	    			}
	    		}
    		}
    	}
    	else
    	{
			closing = false;
			lidOpenRatio = 0F;
    	}
    }
    
    public abstract boolean shouldStartClosingServerSide();
    
    protected void onFinishedClosing()
    {
		worldObj.playSoundEffect( xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D,
			"step.gravel", 0.1F + ( worldObj.rand.nextFloat() * 0.1F ), 
    		1F + ( worldObj.rand.nextFloat() * 0.25F ) );		
    }
}
