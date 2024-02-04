// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.tileentity.beacon.BeaconTileEntity;
import btw.block.tileentity.EnderChestTileEntity;
import btw.inventory.util.InventoryUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class EnderChestBlock extends BlockEnderChest
{
    public EnderChestBlock(int iBlockID)
    {
    	super( iBlockID );
    	
        initBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
    }
    
    @Override
    public TileEntity createNewTileEntity(World world )
    {
        return new EnderChestTileEntity();
    }
    
    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
    {
    	if ( !world.isRemote )
    	{
        	if ( !world.isBlockNormalCube( i, j + 1, k ) )
        	{
                EnderChestTileEntity tileEntity = (EnderChestTileEntity)world.getBlockTileEntity( i, j, k );
                
                if ( tileEntity != null )
                {
	                InventoryEnderChest chestInventory = null;
	                
		    		int iAntennaLevel = computeLevelOfEnderChestsAntenna(world, i, j, k);
		    		
		    		if ( iAntennaLevel == 0 )
		    		{
		    			// local inventory
		    			
		    			chestInventory = tileEntity.getLocalEnderChestInventory();
		    		}
		    		else if ( iAntennaLevel == 1 )
		    		{
		    			// dimension specific low power communal inventory
		    			
		    			chestInventory = world.getLocalLowPowerEnderChestInventory();
		    		}
		    		else if ( iAntennaLevel == 2 )
		    		{
		    			// dimension specific communal inventory
		    			
		    			chestInventory = world.getLocalEnderChestInventory();
		    		}
		    		else if ( iAntennaLevel == 3 )
		    		{
		    			// global communal inventory
		    			
                		chestInventory = MinecraftServer.getServer().worldServers[0].worldInfo.getGlobalEnderChestInventory();
		    		}
		    		else
		    		{
		    			// global private inventory
		    			
		                chestInventory = player.getInventoryEnderChest();
		    		}
		    		
		    		if ( chestInventory != null )
		    		{
                        chestInventory.setAssociatedChest( tileEntity );
                        
                        player.displayGUIChest( chestInventory );
                        
                        if ( iAntennaLevel >= 1 )
                        {
            	            world.playSoundEffect((double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, "mob.endermen.stare", 
            	            	0.25F * (float)iAntennaLevel, world.rand.nextFloat() * 0.4F + 1.2F );
                        }                        
		    		}
                }
        	}    		
    	}
        
        return true;        
    }
    
	@Override
    public void breakBlock( World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
		if ( !world.isRemote )
		{
	        EnderChestTileEntity tileEntity = (EnderChestTileEntity)world.getBlockTileEntity( i, j, k );
	        
	        if ( tileEntity != null )
	        {
	        	InventoryEnderChest chestInventory = tileEntity.getLocalEnderChestInventory();
	        	
	        	if ( chestInventory != null )
	        	{	        	
	        		InventoryUtils.ejectInventoryContents(world, i, j, k, chestInventory);
	        	}
	        }
		}

        super.breakBlock( world, i, j, k, iBlockID, iMetadata );
    }
	
    //------------- Class Specific Methods -------------//
    
    private int computeLevelOfEnderChestsAntenna(World world, int i, int j, int k)
    {
    	if ( world.getBlockId( i, j - 1, k ) == BTWBlocks.aestheticOpaque.blockID &&
    		world.getBlockMetadata( i, j - 1, k ) == AestheticOpaqueBlock.SUBTYPE_ENDER_BLOCK)
    	{
    		return getLevelOfAntennaBeaconBlockIsPartOf(world, i, j - 1, k);
    	}
    	
    	return 0;
    }
    
    private int getLevelOfAntennaBeaconBlockIsPartOf(World world, int i, int j, int k)
    {
    	// Note: This is pretty brute force.  There's probably a more efficient alogorithm for it, but it only occurs on player interaction
    	
    	// scan from the top down so that we can return on the first valid beacon found (since further ones will be lower power)
    	// the following creates an inverted pyramid with its point on the block in question, thus any active beacons of the appropriate level
    	// found *must* be associated with this block and be an antenna block pyramid 
    	
    	for ( int iTempLevel = 4; iTempLevel >= 1; iTempLevel-- )
    	{
    		int iTempJ = j + iTempLevel;
    		
	    	for ( int iTempI = i - iTempLevel; iTempI <= i + iTempLevel; iTempI++ )
	    	{
		    	for ( int iTempK = k - iTempLevel; iTempK <= k + iTempLevel; iTempK++ )
		    	{
		    		if ( world.getBlockId( iTempI, iTempJ, iTempK ) == Block.beacon.blockID )
		    		{
		                BeaconTileEntity tileEntity = (BeaconTileEntity)world.getBlockTileEntity( iTempI, iTempJ, iTempK );
		            	
		                if ( tileEntity != null )
		                {
		                	if ( tileEntity.getLevels() >= iTempLevel )
		                	{
		                		return iTempLevel;
		                	}
		                }
		    		}
		    	}
	    	}
    	}
    	
    	return 0;
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	return false;
    }    
}
