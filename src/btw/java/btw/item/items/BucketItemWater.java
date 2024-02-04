// FCMOD

package btw.item.items;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import net.minecraft.src.Block;
import btw.util.MiscUtils;
import net.minecraft.src.World;

public class BucketItemWater extends BucketItemFull
{
    public BucketItemWater(int iItemID )
    {
    	super( iItemID );
    	
    	setUnlocalizedName( "bucketWater" );
	}
    
    @Override
    public int getBlockID()
    {
        return BTWBlocks.placedWaterBucket.blockID;
    }

    @Override
	protected boolean attemptPlaceContentsAtLocation(World world, int i, int j, int k)
	{
        if ( ( world.isAirBlock( i, j, k ) || !world.getBlockMaterial( i, j, k ).isSolid() ) )
        {           
        	if ( !world.isRemote )
        	{
	            if( world.provider.isHellWorld )
	            {
	        		world.playAuxSFX( BTWEffectManager.WATER_EVAPORATION_EFFECT_ID, i, j, k, 0 );
	            } 
	            else
	            {
	            	int iTargetBlockID = world.getBlockId( i, j, k );
	            	int iTargetMetadata = world.getBlockMetadata( i, j, k );
	            	
	        		if ( iTargetBlockID == Block.lavaMoving.blockID ||
	        				iTargetBlockID == Block.lavaStill.blockID )
	        		{
		        		world.playAuxSFX( BTWEffectManager.WATER_EVAPORATION_EFFECT_ID, i, j, k, 0 );
	                    
            			if ( iTargetMetadata == 0 )
            			{
                			world.setBlockWithNotify( i, j, k, Block.obsidian.blockID );
            			}
            			else
            			{
                			world.setBlockWithNotify( i, j, k, 
                				BTWBlocks.lavaPillow.blockID );
            			}
	        		}
	        		else
	        		{        			
	        			// do not replace existing source blocks
	        			
	            		if ( ( iTargetBlockID != Block.waterMoving.blockID && 
	        				iTargetBlockID != Block.waterStill.blockID ) ||
	        				iTargetMetadata != 0 )
	        			{
	        				if ( world.provider.dimensionId == 1 )
	        				{
	        					// place water source block in the end dimension
	        					
	                			world.setBlockWithNotify( i, j, k, Block.waterMoving.blockID );
	        				}
	        				else
	        				{        					
	        					MiscUtils.placeNonPersistentWater(world, i, j, k);
	        				}
	        			}
	        		}
	            }
        	}
            
            return true;
        }
        
        return false;
	}
	
	//------------- Class Specific Methods ------------//

	//----------- Client Side Functionality -----------//
}