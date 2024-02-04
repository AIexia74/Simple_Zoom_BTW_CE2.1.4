// FCMOD

package btw.item.items;

import btw.block.BTWBlocks;
import btw.entity.mob.ZombiePigmanEntity;
import btw.world.util.BlockPos;
import net.minecraft.src.*;

import java.util.List;

public class NetherGrothSporesItem extends Item
{
    public NetherGrothSporesItem(int iItemID )
    {
    	super( iItemID );
    	
        setMaxDamage( 0 );
        setHasSubtypes( false );
        
        setBuoyant();
        
    	setUnlocalizedName( "fcItemSporesNetherGroth" );
    	
    	setCreativeTab( CreativeTabs.tabMaterials );
    }
    
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ )
    {
		// tests copied over from itemReed to be on the safe side
        if ( player != null && !player.canPlayerEdit( i, j, k, iFacing, itemStack ) )
        {
            return false;
        }
        
        if ( itemStack.stackSize == 0 )
        {
            return false;
        }
        
		BlockPos targetPos = new BlockPos( i, j, k );
		targetPos.addFacingAsOffset(iFacing);

        WorldChunkManager worldchunkmanager = world.getWorldChunkManager();
        
        if(worldchunkmanager != null)
        {
            BiomeGenBase biomegenbase = worldchunkmanager.getBiomeGenAt(i, k);
            
            if( biomegenbase instanceof BiomeGenHell )
            {
				int iBlockID = BTWBlocks.netherGroth.blockID;
				int iMetadata = 0;
				
		        if ( world.canPlaceEntityOnSide(iBlockID, targetPos.x, targetPos.y, targetPos.z,
												false, iFacing, player, itemStack ) )
		        {
		        	if ( !world.isRemote )
		        	{
		                iMetadata = Block.blocksList[iBlockID].onBlockPlaced(world, targetPos.x, targetPos.y, targetPos.z, iFacing, fClickX, fClickY, fClickZ, iMetadata);
		                
		            	iMetadata = Block.blocksList[iBlockID].preBlockPlacedBy(world, targetPos.x, targetPos.y, targetPos.z, iMetadata, player);
		            	
			            if ( world.setBlockAndMetadataWithNotify(targetPos.x, targetPos.y,
																 targetPos.z, iBlockID, iMetadata) )
			            {
			                Block block = Block.blocksList[iBlockID];
			                
			                if (world.getBlockId(targetPos.x, targetPos.y, targetPos.z) == iBlockID )
			                {
			                    Block.blocksList[iBlockID].onBlockPlacedBy(world, targetPos.x, targetPos.y,
																		   targetPos.z, player, itemStack);
			                    
			                    Block.blocksList[iBlockID].onPostBlockPlaced(world, targetPos.x, targetPos.y,
																			 targetPos.z, iMetadata);
			                }
			                
			                world.playSoundEffect((float)targetPos.x + 0.5F, (float)targetPos.y + 0.5F,
												  (float)targetPos.z + 0.5F, block.stepSound.getPlaceSound(),
			            		(block.stepSound.getPlaceVolume() + 1.0F) / 2.0F, block.stepSound.getPlacePitch() * 0.8F);
			                
	    		            angerPigmen(world, player);
	    		            
			            }
		        	}
		            
	                itemStack.stackSize--;	                
		            
		        	return true;        	
		        }
            }
		}
            
    	return false;
    }
    
    //************* Class Specific Methods ************//
	
    private void angerPigmen(World world, EntityPlayer entityPlayer)
    {
        List list = world.getEntitiesWithinAABB( ZombiePigmanEntity.class,
        	entityPlayer.boundingBox.expand( 32D, 32D, 32D ) );
        
        for( int tempIndex = 0; tempIndex < list.size(); tempIndex++ )
        {
            Entity targetEntity = (Entity)list.get( tempIndex );
            
            targetEntity.attackEntityFrom( DamageSource.causePlayerDamage( entityPlayer ), 0 );
            
        }
    }
}
