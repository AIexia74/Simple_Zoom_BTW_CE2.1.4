// FCMOD

package btw.item.items;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class SoupItem extends FoodItem
{
    public SoupItem(int iItemID, int iHungerHealed, float fSaturationModifier, boolean bWolfMeat, String sItemName )
    {
        super( iItemID, iHungerHealed, fSaturationModifier, bWolfMeat, sItemName );        
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
