// FCMOD

package btw.item.items;

import net.minecraft.src.Block;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class ShovelItemStone extends ShovelItem
{
    public ShovelItemStone(int iItemID )
    {
        super( iItemID, EnumToolMaterial.STONE );
        
        efficiencyOnProperMaterial /= 3;
        
        setUnlocalizedName("shovelStone");
    }
    
    @Override
    public boolean canHarvestBlock(ItemStack stack, World world, Block block, int i, int j, int k )
    {
    	// special casing clay here to avoid having to set tool levels in every block that can be harvested by shovel.  If
    	// more blocks require stone-shovel harvest, consider doing the level thing
    	
    	if ( block == Block.blockClay )
    	{
    		return true;
    	}
    	
    	// stone shovels always drop piles and disturb neighboring blocks, as if dug by hand
    	
        return false;
    }
}
