// FCMOD

package btw.item.items;

import net.minecraft.src.Block;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class HoeItem extends ToolItem
{
    protected EnumToolMaterial theToolMaterial;

    public HoeItem(int iItemID, EnumToolMaterial material )
    {
        super( iItemID, 1, material );        
        
        if ( material.getHarvestLevel() <= 2 ) // iron or worse
        {
        	efficiencyOnProperMaterial /= 8;
        }
        else
        {
        	efficiencyOnProperMaterial /= 4;
        }
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, World world, Block block, int i, int j, int k )
    {
    	// hoes don't harvest, they only convert
    	
    	return false;
    }
    
    @Override
    public boolean isToolTypeEfficientVsBlockType(Block block)
    {
    	return block.areHoesEffectiveOn();
    }

    @Override
    public boolean canToolStickInBlock(ItemStack stack, Block block, World world, int i, int j, int k)
    {
		return super.canToolStickInBlock(stack, block, world, i, j, k) ||
               block.areShovelsEffectiveOn();
    }
    
    @Override
	public float getVisualVerticalOffsetAsBlock()
    {
    	return 0.80F;
    }    
    
    @Override
	public float getBlockBoundingBoxHeight()
    {
    	return 0.79F;
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
