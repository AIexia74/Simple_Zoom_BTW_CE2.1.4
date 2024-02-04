// FCMOD 

package btw.item.blockitems;

import btw.block.tileentity.FiniteTorchTileEntity;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class FiniteBurningTorchBlockItem extends InfiniteBurningTorchBlockItem
{	
    static private final float CHANCE_OF_GOING_OUT_FROM_RAIN = 0.0025F;
    
    static public final int MAX_DAMAGE = 32; // Setting this low helps reduce SMP updates
    
	static public final float DAMAGE_TO_BURN_TIME_RATIO = (float) MAX_DAMAGE / (float) FiniteTorchTileEntity.MAX_BURN_TIME;
    static public final int SPUTTER_DAMAGE = MAX_DAMAGE - (int)(DAMAGE_TO_BURN_TIME_RATIO * (float) FiniteTorchTileEntity.SPUTTER_TIME);
    
    public FiniteBurningTorchBlockItem(int iItemID )
    {
        super( iItemID );
        
        maxStackSize = 1;
        
        setMaxDamage(MAX_DAMAGE);
        
        setUnlocalizedName( "fcBlockTorchFiniteBurning" );
    }
    
    @Override
    public void onUpdate(ItemStack stack, World world, EntityPlayer entity, int iInventorySlot, boolean bIsHandHeldItem )
    {
    	if ( !world.isRemote )
    	{
	        if ( stack.stackSize > 0 && stack.hasTagCompound() && stack.getTagCompound().hasKey( "outTime" ) && !entity.capabilities.isCreativeMode)
	        {
	        	long lExpiryTime = stack.getTagCompound().getLong( "outTime" );
	        	
	        	int iCountdown = (int)( lExpiryTime - WorldUtils.getOverworldTimeServerOnly() );
	        	
	        	if ( iCountdown <= 0 || iCountdown > FiniteTorchTileEntity.MAX_BURN_TIME)
	        	{
	        		// extinguish the torch
	        		
	                int iFXI = MathHelper.floor_double( entity.posX );
	                int iFXJ = MathHelper.floor_double( entity.posY ) + 1;
	                int iFXK = MathHelper.floor_double( entity.posZ );
	                
			        world.playAuxSFX( 1004, iFXI, iFXJ, iFXK, 0 ); // fizz sound fx
	        		
	        		stack.stackSize--;       		        		
	        		
	                if ( stack.stackSize <= 0 )
	                {
	                	entity.inventory.mainInventory[iInventorySlot] = null;
	                }        		
	        	}
	        	else if ( ( ( entity.isInWater() && entity.isInsideOfMaterial(Material.water) ) || 
	        		(entity.isBeingRainedOn() && entity.worldObj.rand.nextFloat() <= CHANCE_OF_GOING_OUT_FROM_RAIN) ) )
	        	{
	                int iFXI = MathHelper.floor_double( entity.posX );
	                int iFXJ = MathHelper.floor_double( entity.posY ) + 1;
	                int iFXK = MathHelper.floor_double( entity.posZ );
	                
			        world.playAuxSFX( 1004, iFXI, iFXJ, iFXK, 0 ); // fizz sound fx
			        
	        		stack.stackSize--;       		        		
	        		
	                if ( stack.stackSize <= 0 )
	                {
	                	entity.inventory.mainInventory[iInventorySlot] = null;
	                }        		
	        	}
	        	else
	        	{
	        		int iNewItemDamage = (int)(DAMAGE_TO_BURN_TIME_RATIO * (float)(FiniteTorchTileEntity.MAX_BURN_TIME - iCountdown ) );
	        		
	        		// the below has a minimum of 1 damage to ensure damage bar is initially displayed
	        		iNewItemDamage = MathHelper.clamp_int(iNewItemDamage, 1, MAX_DAMAGE - 1);
	        		
	        		if ( iNewItemDamage != stack.getItemDamage() )
	        		{
	        			stack.setItemDamage( iNewItemDamage );
	        		}
	        	}
	        }
    	}
    }

    @Override
    public boolean ignoreDamageWhenComparingDuringUse()
    {
    	return true;
    }
	
	@Override
	public int getBlockID() {
		return this.blockID;
	}
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconSputtering;

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamage( int iDamage )
    {
    	if (iDamage >= SPUTTER_DAMAGE)
    	{
    		return iconSputtering;
    	}
    	
    	return super.getIconFromDamage( iDamage );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

		iconSputtering = register.registerIcon("fcBlockTorchFiniteSputtering");
    }	
}
