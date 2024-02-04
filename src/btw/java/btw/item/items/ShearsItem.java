// FCMOD

package btw.item.items;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

public class ShearsItem extends ItemShears
{
    public ShearsItem(int iItemID )
    {
    	super( iItemID );
    	
		setInfernalMaxEnchantmentCost(25);
		setInfernalMaxNumEnchants(2);
    }
    
    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, int iBlockID, int i, int j, int k, EntityLiving usingEntity )
    {
        if ( iBlockID != Block.leaves.blockID &&
        	iBlockID != BTWBlocks.bloodWoodLeaves.blockID &&
        	iBlockID != Block.web.blockID && 
        	iBlockID != BTWBlocks.web.blockID &&
        	iBlockID != Block.tallGrass.blockID && 
        	iBlockID != Block.vine.blockID && 
        	iBlockID != Block.tripWire.blockID &&
        	iBlockID != BTWBlocks.hempCrop.blockID )
        {
            return super.onBlockDestroyed(stack, world, iBlockID, i, j, k, usingEntity);
        }
        else
        {
            stack.damageItem( 1, usingEntity );
            return true;
        }
    }
    
    @Override
    public boolean canHarvestBlock( ItemStack stack, World world, Block block, int i, int j, int k )
    {
        return block.blockID == Block.web.blockID || 
        	block.blockID == BTWBlocks.web.blockID ||
        	block.blockID == Block.redstoneWire.blockID || 
        	block.blockID == Block.tripWire.blockID;
    }
    
    @Override
    public float getStrVsBlock( ItemStack stack, World world, Block block, int i, int j, int k ) 
    {
    	if ( isEfficientVsBlock(stack, world, block, i, j, k) )
		{
        	if ( block.blockID == BTWBlocks.bloodWoodLeaves.blockID ||
        		block.blockID == Block.leaves.blockID ||
        		block.blockID == Block.web.blockID || 
        		block.blockID == BTWBlocks.web.blockID )
        	{
                return 15F;
        	}
        	else
        	{
        		return 5F;
        	}
		}
    	
    	return super.getStrVsBlock( stack, world, block, i, j, k );
    }    
    
    @Override
    public boolean isEfficientVsBlock(ItemStack stack, World world, Block block, int i, int j, int k)
    {
    	if ( !block.blockMaterial.isToolNotRequired() )
    	{
    		if ( canHarvestBlock( stack, world, block, i, j, k ) )
    		{
    			return true;
    		}
    	}
    	
    	if ( block == Block.cloth || 
    		block == Block.leaves || 
    		block == BTWBlocks.bloodWoodLeaves ||
    		block == BTWBlocks.woolSlab ||
    		block == BTWBlocks.woolSlabTop ||
    		block == Block.vine ||
    		block == BTWBlocks.hempCrop)
    	{
    		return true;
    	}
    	
        return false;
    }
  
    @Override
    public boolean isDamagedInCrafting()
    {
    	return true;
    }
    
    @Override
    public void onUsedInCrafting(EntityPlayer player, ItemStack outputStack)
    {
		// note: the playSound function for player both plays the sound locally on the client, and plays it remotely on the server without it being sent again to the same player
    	
		if (player.timesCraftedThisTick == 0 )
		{
			player.playSound("mob.sheep.shear", 0.8F, 1.0F);
		}
    }
    
    @Override
    public void onBrokenInCrafting(EntityPlayer player)
    {
		// note: the playSound function for player both plays the sound locally on the client, and plays it remotely on the server without it being sent again to the same player
    	
		player.playSound("random.break", 0.8F, 0.8F + player.worldObj.rand.nextFloat() * 0.4F);
    }
    
    @Override
    public void onCreated( ItemStack stack, World world, EntityPlayer player ) 
    {
		if (player.timesCraftedThisTick == 0 && world.isRemote )
		{
			player.playSound( "random.anvil_use", 0.5F, world.rand.nextFloat() * 0.25F + 1.25F );
		}
		
    	super.onCreated( stack, world, player );
    }    
}
