// FCMOD

package btw.item.items;

import net.minecraft.src.Block;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class ShovelItem extends ToolItem
{
    public ShovelItem(int iItemID, EnumToolMaterial material )
    {
        super( iItemID, 1, material );
    }

    protected ShovelItem(int iItemID, EnumToolMaterial material, int iMaxUses )
    {
        super( iItemID, 1, material );
        
        setMaxDamage( iMaxUses );
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, World world, Block block, int i, int j, int k )
    {
        return isToolTypeEfficientVsBlockType(block);
    }
    
    @Override
    public boolean isToolTypeEfficientVsBlockType(Block block)
    {
    	return block.areShovelsEffectiveOn();
    }
    
    @Override
	public float getVisualVerticalOffsetAsBlock()
    {
    	return 0.70F;
    }    
    
    @Override
	public float getVisualRollOffsetAsBlock()
    {
    	return -15F;
    }
}
