// FCMOD

package btw.item.items;

import btw.crafting.util.FurnaceBurnTime;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;

import java.util.Random;

public class FireStarterItemPrimitive extends FireStarterItem
{
	private final float baseChance;
    private final float maxChance;
    private final float chanceIncreasePerUse;
    
    private static final float CHANCE_DECAY_PER_TICK = 0.00025F;
    private static final long DELAY_BEFORE_DECAY = (2 * 20 ); // two seconds
    
    public FireStarterItemPrimitive(int iItemID, int iMaxUses, float fExhaustionPerUse, float fBaseChance, float fMaxChance, float fChanceIncreasePerUse )
    {
        super( iItemID, iMaxUses, fExhaustionPerUse );

		baseChance = fBaseChance;
		maxChance = fMaxChance;
		chanceIncreasePerUse = fChanceIncreasePerUse;
        
        setBuoyant();
    	setfurnaceburntime(FurnaceBurnTime.SHAFT);
    }
    
    @Override
    protected boolean checkChanceOfStart(ItemStack stack, Random rand)
    {
    	boolean bReturnValue = false;
    	
        float fChance = stack.getAccumulatedChance(baseChance);
        long lCurrentTime = WorldUtils.getOverworldTimeServerOnly();
        long lLastTime = stack.getTimeOfLastUse();
        
        if ( lLastTime > 0 )
        {
        	if ( lCurrentTime > lLastTime )
        	{
        		long lDecayTime = ( lCurrentTime - lLastTime ) - DELAY_BEFORE_DECAY;
        		
        		if ( lDecayTime > 0 )
        		{
	        		fChance -= (float)lDecayTime * CHANCE_DECAY_PER_TICK;
	        		
	        		if (fChance < baseChance)
	        		{
	        			fChance = baseChance;
	        		}
        		}
        	}
        	else if ( lCurrentTime < lLastTime )
        	{
        		// do not reset chance if currentTime is the same as last time, in case use attempts
        		// stack up on the server
        		
        		fChance = baseChance;
        	}
        }
        
		if ( rand.nextFloat() <= fChance )
		{
			bReturnValue = true;
		}
		
		fChance += chanceIncreasePerUse;
		
		if (fChance > maxChance)
		{
			fChance = maxChance;
		}
		
		stack.setAccumulatedChance(fChance);
		stack.setTimeOfLastUse(lCurrentTime);
		
		return bReturnValue;
    }
    
    @Override
    protected void performUseEffects(EntityPlayer player)
    {
        player.playSound( "random.eat", 0.5F + 0.5F * (float)player.rand.nextInt(2), ( player.rand.nextFloat() * 0.25F ) + 1.75F );
        
        if ( player.worldObj.isRemote )
        {
	        for (int var3 = 0; var3 < 5; ++var3)
	        {
		        Vec3 var4 = player.worldObj.getWorldVec3Pool().getVecFromPool( ( (double)player.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D );
		        
		        var4.rotateAroundX( -player.rotationPitch * (float)Math.PI / 180.0F );
		        var4.rotateAroundY( -player.rotationYaw * (float)Math.PI / 180.0F );
		        
		        Vec3 var5 = player.worldObj.getWorldVec3Pool().getVecFromPool( ( (double)player.rand.nextFloat() - 0.5D) * 0.3D, (double)(-player.rand.nextFloat()) * 0.6D - 0.3D, 0.6D );
		        
		        var5.rotateAroundX( -player.rotationPitch * (float)Math.PI / 180.0F);
		        var5.rotateAroundY( -player.rotationYaw * (float)Math.PI / 180.0F);
		        
		        var5 = var5.addVector( player.posX, player.posY + (double)player.getEyeHeight(), player.posZ);
		        
		        player.worldObj.spawnParticle( "iconcrack_" + this.itemID, var5.xCoord, var5.yCoord, var5.zCoord, var4.xCoord, var4.yCoord + 0.05D, var4.zCoord);
	        }
        }
    }
	
    @Override
    protected boolean attemptToLightBlock(ItemStack stack, World world, int i, int j, int k, int iFacing)
    {
    	if ( super.attemptToLightBlock(stack, world, i, j, k, iFacing) )
    	{
    		stack.setAccumulatedChance(baseChance);

    		return true;
    	}
    	
    	return false;
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//
}
