// FCMOD

package btw.entity.mechanical.source;

import btw.block.BTWBlocks;
import btw.block.blocks.AxleBlock;
import btw.block.util.MechPowerUtils;
import net.minecraft.src.*;

public abstract class MechanicalPowerSourceEntityHorizontal extends MechanicalPowerSourceEntity
{
	// local vars
	
    public boolean alignedToX;
    
    public MechanicalPowerSourceEntityHorizontal(World world )
    {
        super( world );

		alignedToX = true;
    }
    
    public MechanicalPowerSourceEntityHorizontal(World world, double x, double y, double z, boolean bIAligned  )
    {
    	super( world, x, y, z );

		alignedToX = bIAligned;
        
        initBoundingBox();
    }
    
	@Override
    public void setDead()
    {
    	if (providingPower)
    	{        	
        	int iCenterI = MathHelper.floor_double( posX );
        	int iCenterJ = MathHelper.floor_double( posY );
        	int iCenterK = MathHelper.floor_double( posZ );
        	
        	int iCenterBlockID = worldObj.getBlockId( iCenterI, iCenterJ, iCenterK );

        	if ( iCenterBlockID == BTWBlocks.axlePowerSource.blockID )
        	{
        		// temporarily remove the center axle to prevent power-transfer problems if we're powering
        		// in both directions
        		
        		int iAxisAlignment = ( (AxleBlock) BTWBlocks.axlePowerSource).getAxisAlignment(worldObj, iCenterI, iCenterJ, iCenterK);
        		
                worldObj.setBlockWithNotify( iCenterI, iCenterJ, iCenterK, BTWBlocks.axle.blockID );
                
                ( (AxleBlock) BTWBlocks.axle).setAxisAlignment(worldObj, iCenterI, iCenterJ, iCenterK, iAxisAlignment);
        	}
    	}
    	
        super.setDead();
    }
    
    //------------- FCEntityMechPower ------------//
	
    @Override
    protected boolean validateConnectedAxles()
    {
    	// verify we still have an axle at our center
    	
    	int iCenterI = MathHelper.floor_double( posX );
    	int iCenterJ = MathHelper.floor_double( posY );
    	int iCenterK = MathHelper.floor_double( posZ );
    	
    	int iCenterBlockID = worldObj.getBlockId( iCenterI, iCenterJ, iCenterK );

    	if ( !MechPowerUtils.isBlockIDAxle(iCenterBlockID) )
    	{
    		return false;
    	}
    	
    	AxleBlock centerAxleBlock = (AxleBlock) Block.blocksList[iCenterBlockID];
    	
		// make sure the center block is still properly oriented
		
    	int iAxisAlignment = centerAxleBlock.getAxisAlignment(worldObj, iCenterI, iCenterJ, iCenterK);
    	
    	if ( iAxisAlignment == 0 || ( iAxisAlignment == 1 && alignedToX) || (iAxisAlignment == 2 && !alignedToX) )
		{
    		return false;
		}            		
    	
    	if ( !providingPower)
    	{
    		if ( iCenterBlockID == BTWBlocks.axlePowerSource.blockID )
    		{
    			worldObj.setBlockWithNotify( iCenterI, iCenterJ, iCenterK, BTWBlocks.axle.blockID );
    			
    			((AxleBlock) BTWBlocks.axle).setAxisAlignment(worldObj, iCenterI, iCenterJ, iCenterK, iAxisAlignment);
    		}
    		else if (centerAxleBlock.getPowerLevel(worldObj, iCenterI, iCenterJ, iCenterK) > 0 )
    		{
    			// we have an unpowered device on a powered axle
    			
        		return false;
    		}
    	}
    	else
    	{
    		if ( iCenterBlockID == BTWBlocks.axle.blockID )
    		{
    			// we have powered device on a unpowered axle.  Restore power (this is likely the result of a player-rotated axle or Gear Box).
    			
    			worldObj.setBlockWithNotify( iCenterI, iCenterJ, iCenterK, BTWBlocks.axlePowerSource.blockID );
    			
    			((AxleBlock) BTWBlocks.axlePowerSource).setAxisAlignment(worldObj, iCenterI, iCenterJ, iCenterK, iAxisAlignment);
    		}    		
    	}
    	
    	return true;    	
    }
    
    @Override
	public void transferPowerStateToConnectedAxles()
    {
    	int iCenterI = MathHelper.floor_double( posX );
    	int iCenterJ = MathHelper.floor_double( posY );
    	int iCenterK = MathHelper.floor_double( posZ );
    	
    	int iCenterBlockID = worldObj.getBlockId( iCenterI, iCenterJ, iCenterK );
    	
    	AxleBlock centerAxleBlock = (AxleBlock)Block.blocksList[iCenterBlockID];
    	
    	int iAxisAlignment = centerAxleBlock.getAxisAlignment(worldObj, iCenterI, iCenterJ, iCenterK);
    	
    	if (providingPower)
    	{
    		if ( iCenterBlockID == BTWBlocks.axle.blockID )
    		{
    			worldObj.setBlockWithNotify( iCenterI, iCenterJ, iCenterK, BTWBlocks.axlePowerSource.blockID );
    			
    			((AxleBlock) BTWBlocks.axlePowerSource).setAxisAlignment(worldObj, iCenterI, iCenterJ, iCenterK, iAxisAlignment);
    		}
    	}
    	else
    	{
    		if ( iCenterBlockID == BTWBlocks.axlePowerSource.blockID )
    		{
    			worldObj.setBlockWithNotify( iCenterI, iCenterJ, iCenterK, BTWBlocks.axle.blockID );
    			
    			((AxleBlock) BTWBlocks.axle).setAxisAlignment(worldObj, iCenterI, iCenterJ, iCenterK, iAxisAlignment);
    		}
    	}    	
    }
    
    //------------- Class Specific Methods ------------//

	public void initBoundingBox()
    {
        if (alignedToX)
        {
        	boundingBox.setBounds( posX - (getDepth() * 0.5F ), posY - (getHeight() * 0.5F ), posZ - (getWidth() * 0.5F ),
    			posX + (getDepth() * 0.5F ), posY + (getHeight() * 0.5F ), posZ + (getWidth() * 0.5F ));
        			
        }
        else
        {
        	boundingBox.setBounds( posX - (getWidth() * 0.5F ), posY - (getHeight() * 0.5F ), posZ - (getDepth() * 0.5F ),
    			posX + (getWidth() * 0.5F ), posY + (getHeight() * 0.5F ), posZ + (getDepth() * 0.5F ));
        }
    }

	public AxisAlignedBB getDeviceBounds()
    {	
        if (alignedToX)
        {
        	return AxisAlignedBB.getAABBPool().getAABB( 
        		posX - (getDepth() * 0.5F ), posY - (getHeight() * 0.5F ), posZ - (getWidth() * 0.5F ),
    			posX + (getDepth() * 0.5F ), posY + (getHeight() * 0.5F ), posZ + (getWidth() * 0.5F ));
        			
        }
        else
        {
        	return AxisAlignedBB.getAABBPool().getAABB( 
        		posX - (getWidth() * 0.5F ), posY - (getHeight() * 0.5F ), posZ - (getDepth() * 0.5F ),
    			posX + (getWidth() * 0.5F ), posY + (getHeight() * 0.5F ), posZ + (getDepth() * 0.5F ));
        }
    }
}
