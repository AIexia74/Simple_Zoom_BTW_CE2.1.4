// FCMOD

package btw.item.items;

import btw.world.util.BlockPos;
import net.minecraft.src.*;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public abstract class FireStarterItem extends Item
{
	private final float exhaustionPerUse;
	
    public FireStarterItem(int iItemID, int iMaxUses, float fExhaustionPerUse )
    {
        super( iItemID );
        
        maxStackSize = 1;
        setMaxDamage( iMaxUses );
		exhaustionPerUse = fExhaustionPerUse;
    	
        setCreativeTab(CreativeTabs.tabTools);
    }
    
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ )
    {
        if ( player.canPlayerEdit( i, j, k, iFacing, stack ) )
        {
    		performUseEffects(player);
    		
        	if ( !world.isRemote )
        	{        	    
                notifyNearbyAnimalsOfAttempt(player);
                
                if ( checkChanceOfStart(stack, world.rand) )
                {
        			attemptToLightBlock(stack, world, i, j, k, iFacing);
        		}        		
        	}
	
			player.addExhaustion(exhaustionPerUse * world.getDifficulty().getHungerIntensiveActionCostMultiplier());
            
            stack.damageItem( 1, player );
            
            return true;
        }
        
        return false;
    }

    @Override
    public boolean getCanItemStartFireOnUse(int iItemDamage)
    {
    	return true;
    }    
    
    //------------- Class Specific Methods ------------//
    
    protected abstract boolean checkChanceOfStart(ItemStack stack, Random rand);
    
    protected void performUseEffects(EntityPlayer player)
    {
    }
    
    protected boolean attemptToLightBlock(ItemStack stack, World world, int i, int j, int k, int iFacing)
    {
    	int iTargetBlockID = world.getBlockId( i, j, k );
    	Block targetBlock = Block.blocksList[iTargetBlockID];

    	if ( targetBlock != null && targetBlock.getCanBeSetOnFireDirectlyByItem(world, i, j, k) )
		{
    		if ( targetBlock.setOnFireDirectly(world, i, j, k) )
    		{            	            
    			return true;
    		}
		}
    	
    	return false;
    }
    
    protected boolean attemptToLightBlockRegardlessOfFlamability(ItemStack stack, World world, int i, int j, int k, int iFacing)
    {
    	// old way of setting absolutely anything on fire with a fire block.  Not used at present, but here for reference
    	
    	int iTargetBlockID = world.getBlockId( i, j, k );
    	Block targetBlock = Block.blocksList[iTargetBlockID];

    	BlockPos targetPos = new BlockPos( i, j, k );
    	
    	if ( targetBlock == null || !targetBlock.getCanBeSetOnFireDirectlyByItem(world, targetPos.x, targetPos.y, targetPos.z) )
    	{
    		targetPos.addFacingAsOffset(iFacing);
        	targetBlock = Block.blocksList[world.getBlockId(targetPos.x, targetPos.y, targetPos.z)];
        	iTargetBlockID = world.getBlockId(targetPos.x, targetPos.y, targetPos.z);
    	}
    	
    	if ( targetBlock != null && targetBlock.getCanBeSetOnFireDirectlyByItem(world, targetPos.x, targetPos.y, targetPos.z) )
		{
    		if ( targetBlock.setOnFireDirectly(world, targetPos.x, targetPos.y, targetPos.z) )
    		{            	            
    			return true;
    		}
		}
    	else if ( world.isAirBlock(targetPos.x, targetPos.y, targetPos.z) )
        {
            world.setBlockWithNotify(targetPos.x, targetPos.y, targetPos.z, Block.fire.blockID);
            
            world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, "mob.ghast.fireball", 1.0F, world.rand.nextFloat() * 0.4F + 0.8F );
            
			return true;
        }
    	
    	return false;
    }
    
	public void notifyNearbyAnimalsOfAttempt(EntityPlayer player)
	{
        List animalList = player.worldObj.getEntitiesWithinAABB( EntityAnimal.class, player.boundingBox.expand( 6, 6, 6 ) );
	        
        Iterator itemIterator = animalList.iterator();

        while ( itemIterator.hasNext())
        {
    		EntityAnimal tempAnimal = (EntityAnimal)itemIterator.next();
    		
	        if ( !tempAnimal.isDead )
	        {
	        	tempAnimal.onNearbyFireStartAttempt(player);
	        }	        
        }
	}
    
	//----------- Client Side Functionality -----------//
}
