// FCMOD

package btw.item.items;

import btw.client.fx.BTWEffectManager;
import btw.entity.SoulSandEntity;
import btw.util.hardcorespawn.SpawnLocation;
import btw.util.hardcorespawn.SpawnLocationList;
import net.minecraft.src.*;

public class SoulSandPileItem extends Item
{
    public SoulSandPileItem(int iItemID )
    {
        super( iItemID );
        
        setBellowsBlowDistance(1);
		setFilterableProperties(FILTERABLE_FINE);
        
        setUnlocalizedName( "fcItemPileSoulSand" );
        
        setCreativeTab( CreativeTabs.tabMaterials );
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if ( !world.isRemote )
        {
        	boolean bHasTarget = false;
        	
        	double dTargetXPos = player.posX;
        	double dTargetZPos = player.posZ;
        	
        	if ( world.provider.dimensionId == 0 )
        	{
        		SpawnLocationList spawnList = world.getSpawnLocationList();
        		
        		SpawnLocation closestSpawnLoc = spawnList.getClosestSpawnLocationForPosition(player.posX, player.posZ);
        		
                if ( closestSpawnLoc != null )
                {
                	dTargetXPos = (double)closestSpawnLoc.posX;
                	dTargetZPos = (double)closestSpawnLoc.posZ;
                	
                	bHasTarget = true;
                }                
        	}
        	else if ( world.provider.dimensionId == -1 )
        	{
        		IChunkProvider provider = world.getChunkProvider();
        		
        		if ( provider != null && provider instanceof ChunkProviderServer )
        		{
        			ChunkProviderServer serverProvider = (ChunkProviderServer)provider;
        			
        			provider = serverProvider.getCurrentProvider();
        			
        			if ( provider != null && provider instanceof ChunkProviderHell )
        			{
        				ChunkProviderHell hellProvider = (ChunkProviderHell)provider;
        				
        				StructureStart closestFortress = hellProvider.genNetherBridge.getClosestStructureWithinRangeSq(player.posX, player.posZ, 90000); // 300 block range
        				
        				if ( closestFortress != null )
        				{
                        	dTargetXPos = (double)closestFortress.boundingBox.getCenterX();
                        	dTargetZPos = (double)closestFortress.boundingBox.getCenterZ();
                        	
                        	bHasTarget = true;
        				}        				
        			}
        		}
        	}
        	
            SoulSandEntity sandEntity = (SoulSandEntity) EntityList.createEntityOfType(SoulSandEntity.class, world, player.posX, player.posY + 2.0D - (double)player.yOffset, player.posZ );

        	sandEntity.moveTowards(dTargetXPos, dTargetZPos);
                
            world.spawnEntityInWorld( sandEntity );

            if ( bHasTarget )
            {
	        	world.playAuxSFX( BTWEffectManager.GHAST_MOAN_EFFECT_ID,
	        		(int)Math.round( sandEntity.posX ), (int)Math.round( sandEntity.posY ), (int)Math.round( sandEntity.posZ), 0 );
            }
                
            if (!player.capabilities.isCreativeMode)
            {
                --stack.stackSize;
            }
        }

        return stack;
    }
}
