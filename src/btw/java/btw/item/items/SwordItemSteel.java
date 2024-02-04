// FCMOD

package btw.item.items;

import net.minecraft.src.EnumToolMaterial;

public class SwordItemSteel extends SwordItem
{
    public SwordItemSteel(int i )
    {
        super( i, EnumToolMaterial.SOULFORGED_STEEL );
        
        setUnlocalizedName( "fcItemSwordRefined" );
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}