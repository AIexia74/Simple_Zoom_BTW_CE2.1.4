// FCMOD

package btw.item.blockitems;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class InfiniteBurningTorchBlockItem extends ItemBlock
{	
    public InfiniteBurningTorchBlockItem(int iItemID )
    {
        super( iItemID );
    }
    
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ )
    {
        if ( player.canPlayerEdit( i, j, k, iFacing, stack ) )
        {
			if ( attemptToLightBlock(stack, world, i, j, k, iFacing) )
			{
	            return true;
			}
        }
        
        return super.onItemUse(  stack, player, world, i, j, k, iFacing, fClickX, fClickY, fClickZ );
    }
    
    @Override
    public boolean getCanItemStartFireOnUse(int iItemDamage)
    {
    	return true;
    }    
    
    @Override
    public void onUpdate( ItemStack stack, World world, EntityPlayer entity, int iInventorySlot, boolean bIsHandHeldItem )
    {
    	if ( !world.isRemote && stack.stackSize > 0 )
    	{
        	if ( entity.isInWater() && entity.isInsideOfMaterial(Material.water) && !entity.capabilities.isCreativeMode )
        	{
                int iFXI = MathHelper.floor_double( entity.posX );
                int iFXJ = MathHelper.floor_double( entity.posY ) + 1;
                int iFXK = MathHelper.floor_double( entity.posZ );
                
		        world.playAuxSFX( 1004, iFXI, iFXJ, iFXK, 0 ); // fizz sound fx
		        
        		stack.itemID = BTWBlocks.infiniteUnlitTorch.blockID;
        	}
    	}
    }
	
	@Override
	public int getBlockID() {
		// Well we don't have a world here, so we have to do this the hard way.
		if (MinecraftServer.getIsServer()) {
			if (MinecraftServer.getServer().worldServers[0].getDifficulty().shouldNetherCoalTorchesStartFires()) {
				return this.blockID;
			}
			else {
				return Block.torchWood.blockID;
			}
		}
		else {
			if (Minecraft.getMinecraft().theWorld.getDifficulty().shouldNetherCoalTorchesStartFires()) {
				return this.blockID;
			}
			else {
				return Block.torchWood.blockID;
			}
		}
	}

    //------------- Class Specific Methods ------------//
    
    protected boolean attemptToLightBlock(ItemStack stack, World world, int i, int j, int k, int iFacing)
    {
    	int iTargetBlockID = world.getBlockId( i, j, k );
    	Block targetBlock = Block.blocksList[iTargetBlockID];

    	if ( targetBlock != null && targetBlock.getCanBeSetOnFireDirectlyByItem(world, i, j, k) )
		{
    		if ( !world.isRemote )
    		{
    			targetBlock.setOnFireDirectly(world, i, j, k);
    		}
    		
			return true;
		}
    	
    	return false;
    }
    	
	//----------- Client Side Functionality -----------//	

    @Override
    @Environment(EnvType.CLIENT)
    public boolean canPlaceItemBlockOnSide( World world, int i, int j, int k, int iFacing, EntityPlayer player, ItemStack itemStack )
    {
    	int iTargetBlockID = world.getBlockId( i, j, k );
    	Block targetBlock = Block.blocksList[iTargetBlockID];

    	if ( targetBlock != null && targetBlock.getCanBeSetOnFireDirectlyByItem(world, i, j, k) )
		{
    		return true;
		}
    	
        return super.canPlaceItemBlockOnSide( world, i, j, k, iFacing, player, itemStack );
    }
}
