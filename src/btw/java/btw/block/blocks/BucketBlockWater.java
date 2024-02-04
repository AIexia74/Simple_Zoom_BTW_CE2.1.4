// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class BucketBlockWater extends BucketBlockFull
{
    public BucketBlockWater(int iBlockID )
    {
        super( iBlockID );
    	
    	setUnlocalizedName( "bucketWater" );
    }
    
	@Override
    public int idDropped( int iMetadata, Random rand, int iFortuneMod )
    {
		return Item.bucketWater.itemID;
    }
	
	//------------- Class Specific Methods ------------//
	
	@Override
    public boolean attemptToSpillIntoBlock(World world, int i, int j, int k)
    {
        if ( ( world.isAirBlock( i, j, k ) || !world.getBlockMaterial( i, j, k ).isSolid() ) )
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
        		            world.setBlockAndMetadataWithNotify( i, j, k, 
        		            	Block.waterMoving.blockID, 6 );        					
        				}
        			}
        		}
            }
            
        	return true;
        }
        
    	return false;
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconWater;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );
		
		// have to use non-transparent version of the water texture, as otherwise it'll render
		// with alpha when pushed by a piston, but not in block form

		iconWater = register.registerIcon("fcBlockBucket_water");
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected Icon getContentsIcon()
	{
		return iconWater;
	}
}
