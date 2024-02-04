// FCMOD

package btw.item.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemEnchantedBook;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

import java.util.List;
import java.util.Random;

public class EnchantedBookItem extends ItemEnchantedBook
{
    public EnchantedBookItem(int iItemID )
    {
        super( iItemID );
        
        setBuoyant();
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player )
    {
        if ( world.isRemote )
        {
        	player.addChatMessage( this.getUnlocalizedName() + ".languageUnfamiliar" );
        }
        
    	return itemStack;
    }
        
    @Override
    public void initializeStackOnGiveCommand(Random rand, ItemStack stack)
    {
    	// duplicates enchantment assignment as if this came from a dungeon chest (test code)
    	/*
        Enchantment assignedEnchantment = Enchantment.field_92090_c[rand.nextInt(Enchantment.field_92090_c.length)];
        
        int iEnchantLevel = MathHelper.getRandomIntegerInRange( rand, assignedEnchantment.getMinLevel(), assignedEnchantment.getMaxLevel() );
        
        func_92115_a( stack, new EnchantmentData( assignedEnchantment, iEnchantLevel ) );
        */
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void addInformation( ItemStack itemStack, EntityPlayer player, List infoList, boolean bAdvamcedToolTips )
    {    	
    	// override to prevent display of enchantment names and levels
    }
}
