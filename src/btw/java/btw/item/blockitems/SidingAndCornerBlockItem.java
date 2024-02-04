// FCMOD

package btw.item.blockitems;

import btw.block.blocks.SidingAndCornerAndDecorativeBlock;
import net.minecraft.src.Block;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class SidingAndCornerBlockItem extends ItemBlock
{
    public SidingAndCornerBlockItem(int iItemID )
    {
        super( iItemID );
        
        setMaxDamage( 0 );
        setHasSubtypes(true);
        
        setUnlocalizedName( Block.blocksList[getBlockID()].getUnlocalizedName() );
    }
    
    @Override
    public int getMetadata( int iItemDamage )
    {
        return iItemDamage;
    }
    
    @Override
    public String getUnlocalizedName( ItemStack itemstack )
    {
    	if( itemstack.getItemDamage() == SidingAndCornerAndDecorativeBlock.SUBTYPE_BENCH)
    	{
            return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("bench").toString();
    	}
    	else if( itemstack.getItemDamage() == SidingAndCornerAndDecorativeBlock.SUBTYPE_FENCE)
    	{
            return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("fence").toString();
    	}
    	else if( itemstack.getItemDamage() == 0 )
    	{
            return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("siding").toString();
    	}
    	else
    	{
            return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append("corner").toString();
    	}
    }
}
