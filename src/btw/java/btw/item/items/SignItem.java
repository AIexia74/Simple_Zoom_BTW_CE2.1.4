// FCMOD

package btw.item.items;

import btw.world.util.BlockPos;
import net.minecraft.src.*;

public class SignItem extends ItemSign
{
    public SignItem(int iItemID )
    {
        super( iItemID );
        
        setBuoyant();
        setIncineratedInCrucible();
        
        setUnlocalizedName( "sign" );
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ )
    {
        if ( iFacing == 0 || itemStack.stackSize == 0 ||
        	( player != null && !player.canPlayerEdit( i, j, k, iFacing, itemStack ) ) ||
        	!world.getBlockMaterial( i, j, k ).isSolid() )
        {
            return false;
        }
        
		BlockPos targetPos = new BlockPos( i, j, k );
		targetPos.addFacingAsOffset(iFacing);
		
        if ( Block.signPost.canPlaceBlockAt(world, targetPos.x, targetPos.y, targetPos.z) )
        {
            if ( iFacing == 1 )
            {
                int iYaw = MathHelper.floor_double( ( ( player.rotationYaw + 180F ) * 16F / 360F ) + 0.5D ) & 15;
                
                world.setBlock(targetPos.x, targetPos.y, targetPos.z, Block.signPost.blockID, iYaw, 3);
            }
            else
            {
            	world.setBlock(targetPos.x, targetPos.y, targetPos.z, Block.signWall.blockID, iFacing, 3);
            }

            itemStack.stackSize--;
            
            TileEntitySign tileEnt = (TileEntitySign)world.getBlockTileEntity(targetPos.x, targetPos.y, targetPos.z);

            if ( tileEnt != null )
            {
                player.displayGUIEditSign( tileEnt );
            }

            return true;
        }
        
        return false;
    }
}
