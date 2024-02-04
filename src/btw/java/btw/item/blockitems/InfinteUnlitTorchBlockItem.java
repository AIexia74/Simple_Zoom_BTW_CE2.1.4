// FCMOD 

package btw.item.blockitems;

import btw.block.BTWBlocks;
import btw.util.MiscUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class InfinteUnlitTorchBlockItem extends ItemBlock
{	
    public InfinteUnlitTorchBlockItem(int iItemID )
    {
        super( iItemID );
        
        setUnlocalizedName( "fcBlockTorchIdle" );
    }
    
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ )
    {
    	if ( isPlayerClickingOnIgniter(stack, world, player) )
		{
    		// override normal block place behavior so it goes to onItemRightClick() below
    		
    		return false;
		}
        
        return super.onItemUse(  stack, player, world, i, j, k, iFacing, fClickX, fClickY, fClickZ );
    }
    
    @Override
    public boolean getCanItemBeSetOnFireOnUse(int iItemDamage)
    {
    	return true;
    }
    
    @Override
    public ItemStack onItemRightClick( ItemStack stack, World world, EntityPlayer player )
    {
    	// even though this is an ItemBlock, this function is called if the player doesn't click on a solid block
    	// which happens here if you click on a big lake of lava
    	
    	if ( isPlayerClickingOnIgniter(stack, world, player) )
    	{
    		return onRightClickOnIgniter(stack, world, player);
    	}
    		
    	return super.onItemRightClick( stack, world, player );
    }    

	//------------- Class Specific Methods ------------//
	
    protected ItemStack onRightClickOnIgniter(ItemStack stack, World world, EntityPlayer player)
    {
        int i = MathHelper.floor_double(player.posX);
        int j = MathHelper.floor_double(player.boundingBox.minY);
        int k = MathHelper.floor_double(player.posZ);
        
        player.playSound( "mob.ghast.fireball", 1.0F, world.rand.nextFloat() * 0.4F + 0.8F );
        
        ItemStack newStack = new ItemStack( BTWBlocks.infiniteBurningTorch, stack.stackSize, 0 );
        
    	return newStack; 
    }    

    protected boolean isPlayerClickingOnIgniter(ItemStack stack, World world, EntityPlayer player)
    {
    	return isPlayerClickingOnSolidIgniter(stack, world, player) || isPlayerClickingOnLavaOrFire(stack, world, player);
    }
    
    private boolean isPlayerClickingOnSolidIgniter(ItemStack stack, World world, EntityPlayer player)
    {
        MovingObjectPosition pos = getMovingObjectPositionFromPlayer( world, player, true );

        if ( pos != null )
        {
            if ( pos.typeOfHit == EnumMovingObjectType.TILE )
            {
            	Block targetBlock = Block.blocksList[world.getBlockId( pos.blockX, pos.blockY, pos.blockZ )];
            	
        		if ( targetBlock != null && targetBlock.getCanBlockLightItemOnFire(world, pos.blockX, pos.blockY, pos.blockZ) )
                {
                	return true;
                }
            }
        }
        
    	return false;
    }
    
    private boolean isPlayerClickingOnLavaOrFire(ItemStack stack, World world, EntityPlayer player)
    {
        MovingObjectPosition pos = MiscUtils.getMovingObjectPositionFromPlayerHitWaterAndLavaAndFire(world, player, true);

        if ( pos != null )
        {
            if ( pos.typeOfHit == EnumMovingObjectType.TILE )
            {
                Material material = world.getBlockMaterial( pos.blockX, pos.blockY, pos.blockZ );
                
                if ( material == Material.lava || material == Material.fire )
                {
                	return true;
                }
            }
        }
        
    	return false;
    }
    
    //----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean canPlaceItemBlockOnSide( World world, int i, int j, int k, int iFacing, EntityPlayer player, ItemStack stack )
    {
    	if ( isPlayerClickingOnIgniter(stack, world, player) )
		{
    		return true;
		}
    	
        return super.canPlaceItemBlockOnSide( world, i, j, k, iFacing, player, stack );
    }
}
