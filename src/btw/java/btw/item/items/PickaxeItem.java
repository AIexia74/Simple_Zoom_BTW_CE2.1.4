// FCMOD

package btw.item.items;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

public class PickaxeItem extends ToolItem
{
    public PickaxeItem(int iItemID, EnumToolMaterial material)
    {
        super( iItemID, 2, material );
    }

    public PickaxeItem(int iItemID, EnumToolMaterial material, int iMaxUses)
    {
        super( iItemID, 2, material );
        
        setMaxDamage( iMaxUses );
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, World world, Block block, int i, int j, int k )
    {
    	int iToolLevel = toolMaterial.getHarvestLevel();
    	int iBlockToolLevel = block.getHarvestToolLevel(world, i, j, k);
    	
    	if ( iBlockToolLevel > iToolLevel )
    	{
        	return false;
    	}
    	
    	// FCTODO: Move the following to block classes like above
    	if ( block == Block.obsidian )
    	{
    		return toolMaterial.getHarvestLevel() >= 3;
    	}
    	else if ( block == Block.blockDiamond || block == Block.blockEmerald || block == Block.blockGold )
    	{
    		return toolMaterial.getHarvestLevel() >= 2;
    	}
    	else if ( block == Block.blockIron || block == Block.blockLapis )
    	{
    		return toolMaterial.getHarvestLevel() >= 1;
    	}
    	
    	return block.blockMaterial == Material.rock ||
			block.blockMaterial == Material.iron || 
			block.blockMaterial == Material.anvil ||
			block.blockMaterial == BTWBlocks.netherRockMaterial;
    }

    @Override
    public boolean isToolTypeEfficientVsBlockType(Block block)
    {
    	return block.arePicksEffectiveOn();
    }
    
    @Override
    public float getStrVsBlock( ItemStack stack, World world, Block block, int i, int j, int k )
    {
    	int iToolLevel = toolMaterial.getHarvestLevel();
    	int iBlockToolLevel = block.getEfficientToolLevel(world, i, j, k);
    	
    	if ( iBlockToolLevel > iToolLevel )
    	{
        	return 1.0F;
    	}
    	
		Material material = block.blockMaterial;
		
		if ( material == Material.iron || material == Material.rock || 
			block.blockMaterial == Material.anvil || 
			material == BTWBlocks.netherRockMaterial)
		{
			return efficiencyOnProperMaterial;
		}    		
    	
    	return super.getStrVsBlock( stack, world, block, i, j, k );
    }
    
    @Override
    public boolean isEfficientVsBlock(ItemStack stack, World world, Block block, int i, int j, int k)
    {
    	int iToolLevel = toolMaterial.getHarvestLevel();
    	int iBlockToolLevel = block.getEfficientToolLevel(world, i, j, k);
    	
    	if ( iBlockToolLevel > iToolLevel )
    	{
        	return false;
    	}    	
    	
    	return super.isEfficientVsBlock(stack, world, block, i, j, k);
    }
    
    @Override
	public float getVisualVerticalOffsetAsBlock()
    {
    	return 0.72F;
    }

    @Override
	public float getVisualHorizontalOffsetAsBlock()
    {
    	return 0.35F;
    }
    
    @Override
	public float getVisualRollOffsetAsBlock()
    {
    	return 20F;
    }
    
    @Override
	public float getBlockBoundingBoxHeight()
    {
    	return 0.65F;
    }
    
    @Override
	public float getBlockBoundingBoxWidth()
    {
    	return 1F;
    }
    
    @Override
	public void playPlacementSound(ItemStack stack, Block blockStuckIn, World world, int i, int j, int k)
    {
    	world.playSoundEffect( (float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F,
    		"random.anvil_land", 0.5F, world.rand.nextFloat() * 0.25F + 1.75F );
    }
    
    @Override
	public boolean canToolStickInBlock(ItemStack stack, Block block, World world, int i, int j, int k)
    {
		if ( block.blockMaterial == Material.glass )
		{
			return false;
		}
    	
		return super.canToolStickInBlock(stack, block, world, i, j, k);
    }
}
