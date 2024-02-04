// FCMOD

package btw.item.items;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

public class ChiselItem extends ToolItem
{	
    protected ChiselItem(int iItemID, EnumToolMaterial toolMaterial, int iNumUses )
    {
        super( iItemID, 1, toolMaterial );
        
        setMaxDamage( iNumUses );
        
        damageVsEntity = 1; // chisels don't do any more damage to mobs than any regular (non-tool) item
    }
    
    @Override
    public boolean hitEntity(ItemStack stack, EntityLiving defendingEntity, EntityLiving attackingEntity )
    {
    	// chisels don't lose durability when used to hit entities, as they don't do any extra damage
    	
        return false;
    }    
    
    @Override
    public boolean canHarvestBlock(ItemStack stack, World world, Block block, int i, int j, int k )
    {
    	// chisels can't normally harvest blocks whole unless the block is explicitly set for it (like with webs)
    	
    	if ( block.canChiselsHarvest() )
    	{
	    	return toolMaterial.getHarvestLevel() >= block.getHarvestToolLevel(world, i, j, k);
    	}
    	
    	return false;
    }
    
    @Override
    public boolean isEfficientVsBlock(ItemStack stack, World world, Block block, int i, int j, int k)
    {
    	if ( block.getIsProblemToRemove(stack, world, i, j, k) )
    	{
    		// stumps and such
    		
    		return false;
    	}

    	int iToolLevel = toolMaterial.getHarvestLevel();
    	int iBlockToolLevel = block.getEfficientToolLevel(world, i, j, k);
    	
    	if ( iBlockToolLevel > iToolLevel )
    	{
        	return false;
    	}
    	
    	return block.arechiselseffectiveon(world, i, j, k);
    }
    
    @Override
    public int getItemEnchantability()
    {
    	// prevent chisels being enchanted like other tools
    	
        return 0;
    }
    
    @Override
    public boolean isToolTypeEfficientVsBlockType(Block block)
    {
    	return block.arechiselseffectiveon();
    }
    
    @Override
    public boolean getCanBePlacedAsBlock()
    {
    	return false;
    }
    
    @Override
    public void playPlacementSound(ItemStack stack, Block blockStuckIn, World world, int i, int j, int k)
    {
    	if ( ((ToolItem)pickaxeIron).isToolTypeEfficientVsBlockType(blockStuckIn) )
    	{
	    	world.playSoundEffect( (float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F,
	    		"random.anvil_land", 0.5F, world.rand.nextFloat() * 0.25F + 1.75F );
    	}
    	else if (((ToolItem)axeIron).isToolTypeEfficientVsBlockType(blockStuckIn) ||
    		blockStuckIn.blockMaterial == BTWBlocks.logMaterial ||
    		blockStuckIn.blockMaterial == BTWBlocks.plankMaterial)
    	{
        	world.playSoundEffect( (float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F,
        		"mob.zombie.woodbreak", 0.25F, 1.25F + ( world.rand.nextFloat() * 0.25F ) );
    	}
    	else
    	{
    		super.playPlacementSound(stack, blockStuckIn, world, i, j, k);
    	}
    }
    
    @Override
	public float getVisualVerticalOffsetAsBlock()
    {
    	return 0.45F;
    }
    
    @Override
	public float getBlockBoundingBoxHeight()
    {
    	return 0.3F;
    }
    
    public float getBlockBoundingBoxWidth()
    {
    	return 0.5F;
    }
}
