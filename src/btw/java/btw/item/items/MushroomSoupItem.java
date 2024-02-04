// FCMOD

package btw.item.items;

import net.minecraft.src.*;

public class MushroomSoupItem extends ItemFood
{
    public MushroomSoupItem(int iItemID, int iHungerHealed )
    {
        super( iItemID, iHungerHealed, 0.25F, false );        
    }
    
    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player )
    {
        super.onEaten( stack, world, player );

        ItemStack bowlStack = new ItemStack( Item.bowlEmpty );
        
        if ( !player.inventory.addItemStackToInventory( bowlStack ) )
        {
            player.dropPlayerItem( bowlStack );
        }
        
        return stack;
    }
}
