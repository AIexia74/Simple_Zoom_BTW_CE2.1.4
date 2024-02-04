// FCMOD

package btw.block.blocks;

import btw.block.tileentity.beacon.BeaconEffect;
import btw.block.tileentity.beacon.BeaconTileEntity;
import btw.client.fx.BTWEffectManager;
import btw.inventory.util.InventoryUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class BeaconBlock extends BlockBeacon
{
	private static final long RESPAWN_COOLDOWN_DURATION = 60;
	
    public BeaconBlock(int iBlockID)
    {
    	super( iBlockID );
    	
    	setPicksEffectiveOn(true);
    }
    
    @Override
    public TileEntity createNewTileEntity(World world )
    {
        return new BeaconTileEntity();
    }
    
	@Override
    public void breakBlock( World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
		BeaconTileEntity tileEntity = (BeaconTileEntity)world.getBlockTileEntity( i, j, k );
		
		if ( tileEntity != null )
		{
			InventoryUtils.ejectInventoryContents(world, i, j, k, tileEntity);
			
			if ( tileEntity.getLevels() > 0 )
			{
				// play power down sound
				
		        world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
		    		"mob.wither.death", 
		    		1.0F + ( world.rand.nextFloat() * 0.1F ),	// volume 
		    		1.0F + ( world.rand.nextFloat() * 0.1F ) );	// pitch
			}
			
			// update the block's power state so that any associated global effects destroyed
			
			tileEntity.setPowerState(false, 0, null);
		}

        super.breakBlock( world, i, j, k, iBlockID, iMetadata );
    }
	
	@Override
    public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
    {
		// override to disable the beacon's interface since power is now determined based on material
		
    	BeaconTileEntity beaconEnt = (BeaconTileEntity)world.getBlockTileEntity( i, j, k );
    	
    	if ( beaconEnt != null )
    	{
    		BeaconEffect beaconEffect = beaconEnt.getActiveEffect();
    		
    		if ( beaconEffect == BeaconTileEntity.SPAWN_ANCHOR_EFFECT)
    		{
    			int iBeaconPowerLevel = beaconEnt.getLevels();
    			
    			if ( iBeaconPowerLevel > 0 )
    			{
    				if ( !world.isRemote )
    				{
    					if ( world.getWorldTime() < player.respawnAssignmentCooldownTimer ||
    						world.getWorldTime() - player.respawnAssignmentCooldownTimer > RESPAWN_COOLDOWN_DURATION)
    					{    					
    						player.addChatMessage( this.getUnlocalizedName() + ".playerBound" );
	    					
	    					ChunkCoordinates newSpawnPos = new ChunkCoordinates( i, j, k );
	    					
	    		            player.setSpawnChunk( newSpawnPos, false, world.provider.dimensionId );
	    		            
	    		            player.respawnAssignmentCooldownTimer = world.getWorldTime();
	    		            
			                world.playAuxSFX( BTWEffectManager.GHAST_SCREAM_EFFECT_ID, i, j, k, 1 );
			                
					        world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
					    		"mob.wither.spawn", 
					    		1.0F + ( world.rand.nextFloat() * 0.1F ),	// volume 
					    		1.0F + ( world.rand.nextFloat() * 0.1F ) );	// pitch		        
   					}
					}
    				
					return true;
    			}
    		}    			
    	}
    	
		return false;
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int i, int j, int k, int iSide )
    {
    	if ( iSide != 0 )
    	{
    		return true;
    	}
    	
        return !blockAccess.isBlockOpaqueCube( i, j, k );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
    	return renderer.renderBlockBeacon(this, i, j, k);
    }    
}