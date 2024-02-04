// FCMOD

package btw.item.items;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

public class AxeItem extends ToolItem
{
    public AxeItem(int iItemID, EnumToolMaterial material)
    {    	
        super( iItemID, 3, material );        
    }

    @Override
    public float getStrVsBlock(ItemStack toolItemStack, World world, Block block, int i, int j, int k )
    {
    	int iToolLevel = toolMaterial.getHarvestLevel();
    	int iBlockToolLevel = block.getEfficientToolLevel(world, i, j, k);
    	
    	if ( iBlockToolLevel > iToolLevel )
    	{
        	return 1.0F;
    	}
    	
    	if ( block.getIsProblemToRemove(toolItemStack, world, i, j, k) )
    	{
    		// stumps and such
    		
    		return 1.0F;
    	}

    	return super.getStrVsBlock( toolItemStack, world, block, i, j, k );
    }
    
    @Override
    public boolean canHarvestBlock( ItemStack stack, World world, Block block, int i, int j, int k )
    {
    	int iToolLevel = toolMaterial.getHarvestLevel();
    	int iBlockToolLevel = block.getHarvestToolLevel(world, i, j, k);
    	
    	if ( iBlockToolLevel > iToolLevel )
    	{
        	return false;
    	}
    	else if ( block.getIsProblemToRemove(stack, world, i, j, k) ) // stumps and such
    	{
			return false;
    	}    	
    	else if ( isToolTypeEfficientVsBlockType(block) )
    	{
    		return true;
    	}
        
        return super.canHarvestBlock( stack, world, block, i, j, k );
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
    	
    	if ( block.getIsProblemToRemove(stack, world, i, j, k) )
    	{
    		// stumps and such
    		
			return false;
    	}
    	
    	return super.isEfficientVsBlock(stack, world, block, i, j, k);
    }
    
    @Override
    public boolean isConsumedInCrafting()
    {
    	return toolMaterial.getHarvestLevel() <= 2; // wood, stone, gold & iron
    }
    
    @Override
    public boolean isDamagedInCrafting()
    {
    	return toolMaterial.getHarvestLevel() <= 2; // wood, stone, gold & iron
    }
    
    @Override
    public void onUsedInCrafting(EntityPlayer player, ItemStack outputStack)
    {
		playChopSoundOnPlayer(player);
    }
    
    @Override
    public void onBrokenInCrafting(EntityPlayer player)
    {
    	playBreakSoundOnPlayer(player);
    }

    @Override
    public boolean onBlockDestroyed( ItemStack stack, World world, int iBlockID, int i, int j, int k, EntityLiving destroyingEntityLiving )
    {
    	if ( !getIsDamagedByVegetation() )
    	{
        	// override to prevent damage to non-stone axes when harvesting leaves or other vegetation

	    	Block block = Block.blocksList[iBlockID];
	    	
	    	if ( block != null && block.blockMaterial.getAxesTreatAsVegetation() )
	    	{
    			return true;
	    	}
    	}
    
    	return super.onBlockDestroyed( stack, world, iBlockID, i, j, k, destroyingEntityLiving );
    }

    @Override
    public float getExhaustionOnUsedToHarvestBlock(int iBlockID, World world, int i, int j, int k, int iBlockMetadata)
    {
    	if ( !getConsumesHungerOnZeroHardnessVegetation() )
    	{
	    	Block block = Block.blocksList[iBlockID];
	    	
	    	if ( block != null )
	    	{
	            if ( (double)block.getBlockHardness( world, i, j, k ) == 0.0D ) // same code used to test this elsewhere
	            {
		    		if ( block.blockMaterial.getAxesTreatAsVegetation() )
		    		{
		    			return 0F;
		    		}
	            }
	    	}
    	}
    	
    	return super.getExhaustionOnUsedToHarvestBlock(iBlockID, world, i, j, k, iBlockMetadata);
    }
    
    @Override
    public boolean isToolTypeEfficientVsBlockType(Block block)
    {
    	return block.blockMaterial.getAxesEfficientOn() || block.areAxesEffectiveOn();
    }
    
    @Override
	public boolean canToolStickInBlock(ItemStack stack, Block block, World world, int i, int j, int k)
    {
		if ( block.blockMaterial == BTWBlocks.logMaterial ||
			block.blockMaterial == BTWBlocks.plankMaterial)
		{
			// ensures axe will stick in stumps and workbenches, despite not being efficient vs. them
			
			return true;
		}
    	
		return super.canToolStickInBlock(stack, block, world, i, j, k);
    }
    
    @Override
	public void playPlacementSound(ItemStack stack, Block blockStuckIn, World world, int i, int j, int k)
    {
    	world.playSoundEffect( (float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F,
    		"mob.zombie.woodbreak", 0.25F, 1.25F + ( world.rand.nextFloat() * 0.25F ) );
    }
    
    //------------- Class Specific Methods ------------//
	
    public boolean getConsumesHungerOnZeroHardnessVegetation()
    {
    	if ( this.toolMaterial.getHarvestLevel() <= 1 ) // wood, stone & gold
    	{
    		return true;
    	}
    	
    	return false;
    }
    
    public boolean getIsDamagedByVegetation()
    {
    	if ( this.toolMaterial.getHarvestLevel() <= 2 ) // wood, stone, gold, & iron
    	{
    		return true;
    	}
    	
    	return false;
    }
    
    static public void playChopSoundOnPlayer(EntityPlayer player)
    {
		if (player.timesCraftedThisTick == 0 )
		{
			// note: the playSound function for player both plays the sound locally on the client, and plays it remotely on the server without it being sent again to the same player
			
			player.playSound( "mob.zombie.wood", 0.5F, 2.5F );
		}
    }
    
    static public void playBreakSoundOnPlayer(EntityPlayer player)
    {
		// note: the playSound function for player both plays the sound locally on the client, and plays it remotely on the server without it being sent again to the same player
    	
		player.playSound( "random.break", 0.8F, 0.8F + player.worldObj.rand.nextFloat() * 0.4F );
    }
    
	//----------- Client Side Functionality -----------//
}
