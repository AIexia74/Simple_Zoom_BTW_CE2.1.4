// FCMOD

package btw.item.blockitems;

import net.minecraft.src.Block;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class MouldingBlockItem extends ItemBlock
{
    public MouldingBlockItem(int iItemID )
    {
        super( iItemID );
        
        setMaxDamage( 0 );
        setHasSubtypes(true);
        
        setUnlocalizedName( Block.blocksList[getBlockID()].getUnlocalizedName() );
    }
    
    @Override
    public String getUnlocalizedName( ItemStack itemstack )
    {
        return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("moulding").toString();
    }
}
