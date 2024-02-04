// FCMOD

package btw.item.blockitems;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Icon;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class LeavesBlockItem extends ItemBlock
{
    public LeavesBlockItem(int iItemID )
    {
        super( iItemID );
        
        setMaxDamage( 0 );
        setHasSubtypes( true );
    }

    @Override
    public int getMetadata( int iItemDamage )
    {
        return iItemDamage | 4;
    }

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamage(int iItemDamage )
    {
        return BTWBlocks.bloodWoodLeaves.getIcon( 0, iItemDamage );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColorFromItemStack(ItemStack itemStack, int iLayer )
    {
    	return 0xD81F1F;
    }
}
