// FCMOD 

package btw.item.blockitems;

import btw.block.BTWBlocks;
import btw.block.tileentity.FiniteTorchTileEntity;
import btw.item.util.ItemUtils;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;

public class FiniteUnlitTorchBlockItem extends InfinteUnlitTorchBlockItem
{	
    public FiniteUnlitTorchBlockItem(int iItemID )
    {
        super( iItemID );
        
        setUnlocalizedName( "fcBlockTorchFiniteIdle" );
    }
    
    @Override
    protected ItemStack onRightClickOnIgniter(ItemStack stack, World world, EntityPlayer player)
    {
        int i = MathHelper.floor_double(player.posX);
        int j = MathHelper.floor_double(player.boundingBox.minY);
        int k = MathHelper.floor_double(player.posZ);
        
        player.playSound( "mob.ghast.fireball", 1.0F, world.rand.nextFloat() * 0.4F + 0.8F );
        
        ItemStack newStack = new ItemStack( BTWBlocks.finiteBurningTorch, 1, 0 );
        
        long iExpiryTime = WorldUtils.getOverworldTimeServerOnly() + (long) FiniteTorchTileEntity.MAX_BURN_TIME;
        
        newStack.setTagCompound( new NBTTagCompound() );
        newStack.getTagCompound().setLong( "outTime", iExpiryTime);

		stack.stackSize--;
		
        if ( stack.stackSize <= 0 )
        {
        	return newStack; 
        }
        
		ItemUtils.givePlayerStackOrEject(player, newStack, i, j, k);
		
		return stack;
    }    

	//------------- Class Specific Methods ------------//
}
