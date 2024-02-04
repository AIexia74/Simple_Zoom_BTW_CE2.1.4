// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class DetectorRailBlock extends BlockDetectorRail
{
    public DetectorRailBlock(int iBlockID )
    {
    	super( iBlockID );
    	
        setPicksEffectiveOn(true);

        setCreativeTab( CreativeTabs.tabTransport );
    }
    
    @Override
    public void updateTick(World world, int i, int j, int k, Random random )
    {
        if ( !world.isRemote )
        {
            if ( isOn(world, i, j, k) )
            {
                setStateIfMinecartInteractsWithRailLocal(world, i, j, k, world.getBlockMetadata(i, j, k));
            }
        }
    }
    
    @Override
    public void onEntityCollidedWithBlock( World world, int i, int j, int k, Entity entity )
    {
        if ( !world.isRemote )
        {
            if ( !isOn(world, i, j, k) )
            {
                setStateIfMinecartInteractsWithRailLocal(world, i, j, k, world.getBlockMetadata(i, j, k));
            }
        }
    }
    
    //------------- Class Specific Methods ------------//
    
    private boolean isOn(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return isOnFromMetadata(blockAccess.getBlockMetadata(i, j, k));
    }
    
    private boolean isOnFromMetadata(int iMetadata)
    {
    	return ( iMetadata & 8 ) > 0;
    }
    
    private void setIsOn(World world, int i, int j, int k, boolean bOn)
    {
    	int iMetadata = world.getBlockMetadata( i, j, k );
    	
    	iMetadata = setIsOnInMetadata(iMetadata, bOn);
    	
    	world.setBlockMetadataWithNotify( i, j, k, iMetadata );
    	
        world.notifyBlocksOfNeighborChange( i, j - 1, k, blockID );
    }
    
    private int setIsOnInMetadata(int iMetadata, boolean bOn)
    {
    	if ( bOn )
    	{
    		iMetadata |= 8;
    	}
    	else
    	{
    		iMetadata &= (~8);
    	}
    	
    	return iMetadata;
    }
    
    private void setStateIfMinecartInteractsWithRailLocal(World world, int i, int j, int k, int iMetadata)
    {
    	// local version of parent private function
    	
        boolean bIsOn = isOnFromMetadata(iMetadata);
        
        boolean bTriggeredByCart = false;
        
        float fBoxBorder = 0.125F;
        
        List collidingMinecarts = world.getEntitiesWithinAABB( EntityMinecart.class, 
        	AxisAlignedBB.getAABBPool().getAABB( ((float)i + fBoxBorder), (double)j, ((float)k + fBoxBorder), 
        		((float)(i + 1) - fBoxBorder), ((float)(j + 1) - fBoxBorder), ((float)(k + 1) - fBoxBorder ) ) );

        if ( collidingMinecarts != null && !collidingMinecarts.isEmpty() )
        {        	
            for(int listIndex = 0; listIndex < collidingMinecarts.size(); listIndex++)
            {
                EntityMinecart minecartEntity = (EntityMinecart)collidingMinecarts.get( listIndex );

                if ( shouldPlateActivateBasedOnMinecart(world, i, j, k, minecartEntity.getMinecartType(),
                                                        minecartEntity.riddenByEntity) )
        		{
                	bTriggeredByCart = true;

                	break; // bomb out of the for loop as we've found a cart that triggers
                }
            }                
        }

        if ( bTriggeredByCart != bIsOn )
        {
        	setIsOn(world, i, j, k, bTriggeredByCart);
        }
        
        if ( bTriggeredByCart )
        {
            world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
        }
    }
    
    public boolean shouldPlateActivateBasedOnMinecart(World world, int i, int j, int k, int iMinecartType, Entity riddenByEntity)
    {
    	int iLocalBlockID = world.getBlockId( i, j, k );
    	
		if ( iLocalBlockID == BTWBlocks.woodenDetectorRail.blockID )
		{
			// wooden plates are activated by all cart types
			
			return true;
		}
		else if ( iLocalBlockID == BTWBlocks.steelDetectorRail.blockID )
		{
			// iron plates are only activated by carts containing a player
			
			if ( riddenByEntity != null )
			{
				if ( riddenByEntity instanceof EntityPlayer )
				{
					return true;
				}
			}
		}
		else if ( iLocalBlockID == Block.railDetector.blockID )
		{
			// Regular stone plates are activated by non-empty carts
			
			// 0 specifies an empty cart, 1 a crate cart, and 2 a powered cart
			
			if ( iMinecartType > 0 || riddenByEntity != null )
			{
				return true;
			}		
		}
    	
    	return false;
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconOn;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );
		
    	if ( blockID == Block.railDetector.blockID )
    	{    		
            blockIcon = register.registerIcon("detectorRail");

            iconOn = register.registerIcon("detectorRail_on");
    	}
    	else
    	{
	        blockIcon = register.registerIcon( getUnlocalizedName2() );

            iconOn = register.registerIcon(getUnlocalizedName2() + "_on");
    	}
    }

    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
    	if ( this.isOnFromMetadata(iMetadata) )
    	{
    		return iconOn;
    	}
    	
    	return blockIcon;
    }
}
