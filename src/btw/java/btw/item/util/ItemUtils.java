// FCMOD

package btw.item.util;

import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;

public class ItemUtils
{
    static public void ejectStackWithRandomOffset(World world, int i, int j, int k, ItemStack stack)
    {
        float xOffset = world.rand.nextFloat() * 0.7F + 0.15F;
        float yOffset = world.rand.nextFloat() * 0.2F + 0.1F;
        float zOffset = world.rand.nextFloat() * 0.7F + 0.15F;
        
        ejectStackWithRandomVelocity(world, (float)i + xOffset, (float)j + yOffset, (float)k + zOffset, stack);
    }
    
    static public void ejectSingleItemWithRandomOffset(World world, int i, int j, int k, int iShiftedItemIndex, int iDamage)
    {
		ItemStack itemStack = new ItemStack( iShiftedItemIndex, 1, iDamage );
		
		ejectStackWithRandomOffset(world, i, j, k, itemStack);
    }
    
    static public void ejectStackWithRandomVelocity(World world, double xPos, double yPos, double zPos, ItemStack stack)
    {
        EntityItem entityitem =
        		(EntityItem) EntityList.createEntityOfType(EntityItem.class, world, xPos, yPos, zPos,
		stack );

        float velocityFactor = 0.05F;

        entityitem.motionX = (float)world.rand.nextGaussian() * velocityFactor;
        entityitem.motionY = (float)world.rand.nextGaussian() * velocityFactor + 0.2F;
        entityitem.motionZ = (float)world.rand.nextGaussian() * velocityFactor;
        
        entityitem.delayBeforeCanPickup = 10;
        
        world.spawnEntityInWorld( entityitem );
    }
    
    static public void ejectSingleItemWithRandomVelocity(World world, float xPos, float yPos, float zPos, int iShiftedItemIndex, int iDamage)
    {
		ItemStack itemStack = new ItemStack( iShiftedItemIndex, 1, iDamage );
		
		ItemUtils.ejectStackWithRandomVelocity(world, xPos, yPos, zPos, itemStack);
    }
    
    static public void dropStackAsIfBlockHarvested(World world, int i, int j, int k, ItemStack stack)
    {
    	// copied from dropBlockAsItemWithChance() in Block.java
    	
        float f1 = 0.7F;
        
        double d = (double)(world.rand.nextFloat() * f1) + (double)(1.0F - f1) * 0.5D;
        double d1 = (double)(world.rand.nextFloat() * f1) + (double)(1.0F - f1) * 0.5D;
        double d2 = (double)(world.rand.nextFloat() * f1) + (double)(1.0F - f1) * 0.5D;
        
        EntityItem entityitem = (EntityItem) EntityList.createEntityOfType(EntityItem.class, world, (double)i + d, (double)j + d1, (double)k + d2, stack );
        
        entityitem.delayBeforeCanPickup = 10;
        
        world.spawnEntityInWorld( entityitem );
    }
    
    static public void dropSingleItemAsIfBlockHarvested(World world, int i, int j, int k, int iShiftedItemIndex, int iDamage)
    {
		ItemStack itemStack = new ItemStack( iShiftedItemIndex, 1, iDamage );
		
		ItemUtils.dropStackAsIfBlockHarvested(world, i, j, k, itemStack);
    }
    
    static public void ejectStackAroundBlock(World world, int i, int j, int k, ItemStack stack)
    {
    	int iTempFacing = world.rand.nextInt( 6 );
    	BlockPos targetPos = new BlockPos( i, j, k );
    	BlockPos tempPos = new BlockPos();
    	
    	for ( int iTempFacingCount = 0; iTempFacingCount < 6; iTempFacingCount++ )
    	{    		
        	tempPos.set(i, j, k);
        	tempPos.addFacingAsOffset(iTempFacing);
        	
        	if ( WorldUtils.isReplaceableBlock(world, tempPos.x, tempPos.y, tempPos.z) )
        	{
        		targetPos.set(tempPos);
        		
        		break;
        	}
        	
    		iTempFacing++;
    		
    		if ( iTempFacing  >= 6 )
    		{    			
    			iTempFacing = 0;
    		}
    	}
    		
    	dropStackAsIfBlockHarvested(world, targetPos.x, targetPos.y, targetPos.z, stack);
	}
    
    static public void ejectStackFromBlockTowardsFacing(World world, int i, int j, int k, ItemStack stack, int iFacing)
    {
    	Vec3 ejectPos = Vec3.createVectorHelper( 
    		world.rand.nextDouble() * 0.7D + 0.15D, 
    		1.2D + world.rand.nextDouble() * 0.1D, 
    		world.rand.nextDouble() * 0.7D + 0.15D );
    	
    	ejectPos.tiltAsBlockPosToFacingAlongJ(iFacing);
    	
        EntityItem entity = (EntityItem) EntityList.createEntityOfType(EntityItem.class, world, i + ejectPos.xCoord, j + ejectPos.yCoord, k + ejectPos.zCoord, stack );

        if ( iFacing < 2 )
        {
            entity.motionX = world.rand.nextDouble() * 0.1D - 0.05D;
            entity.motionZ = world.rand.nextDouble() * 0.1D - 0.05D;

            if ( iFacing == 0 )
            {
            	entity.motionY = 0D;
            }
            else
            {
            	entity.motionY = 0.2D;
            }
        }
        else
        {
        	Vec3 ejectVel = Vec3.createVectorHelper( world.rand.nextDouble() * 0.1D - 0.05D, 
        		0.2D, world.rand.nextDouble() * -0.05D - 0.05D );
        	
        	ejectVel.rotateAsVectorAroundJToFacing(iFacing);
        	
            entity.motionX = ejectVel.xCoord;
            entity.motionY = ejectVel.yCoord;
            entity.motionZ = ejectVel.zCoord;
        }
        
        entity.delayBeforeCanPickup = 10;
        
        world.spawnEntityInWorld( entity );        
	}
    
	static public void givePlayerStackOrEjectFromTowardsFacing(EntityPlayer player, ItemStack stack, int i, int j, int k, int iFacing)
	{
		if ( player.inventory.addItemStackToInventory( stack ) )
		{
            player.worldObj.playSoundAtEntity( player, "random.pop", 0.2F, 
        		((player.rand.nextFloat() - player.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
		}
		else if ( !player.worldObj.isRemote )			
		{			
			ItemUtils.ejectStackFromBlockTowardsFacing(player.worldObj, i, j, k, stack, iFacing);
		}
	}
	
	static public void givePlayerStackOrEjectFavorEmptyHand(EntityPlayer player, ItemStack stack, int i, int j, int k)
	{
		if ( player.addStackToCurrentHeldStackIfEmpty(stack) )
		{
            player.worldObj.playSoundAtEntity( player, "random.pop", 0.2F, 
        		((player.rand.nextFloat() - player.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
		}
		else
		{
			givePlayerStackOrEject(player, stack, i, j, k);
		}		
	}
	
	static public void givePlayerStackOrEject(EntityPlayer player, ItemStack stack, int i, int j, int k)
	{
		if ( player.inventory.addItemStackToInventory( stack ) )
		{
            player.worldObj.playSoundAtEntity( player, "random.pop", 0.2F, 
        		((player.rand.nextFloat() - player.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
		}
		else if ( !player.worldObj.isRemote )			
		{			
			ItemUtils.ejectStackWithRandomOffset(player.worldObj, i, j, k, stack);
		}
	}
	
	static public void givePlayerStackOrEject(EntityPlayer player, ItemStack stack)
	{
		if ( player.inventory.addItemStackToInventory( stack ) )
		{
            player.worldObj.playSoundAtEntity( player, "random.pop", 0.2F, 
        		((player.rand.nextFloat() - player.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
		}
		else if ( !player.worldObj.isRemote )			
		{			
			ItemUtils.ejectStackWithRandomVelocity(player.worldObj, player.posX, player.posY, player.posZ, stack);
		}
	}	
}
