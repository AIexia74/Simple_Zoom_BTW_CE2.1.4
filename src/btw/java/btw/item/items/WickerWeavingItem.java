// FCMOD

package btw.item.items;

import btw.crafting.util.FurnaceBurnTime;
import btw.item.BTWItems;
import net.minecraft.src.*;

public class WickerWeavingItem extends ProgressiveCraftingItem
{
	// takes half as long as other progressive crafting	
	static public final int WICKER_WEAVING_MAX_DAMAGE = (60 * 20 / PROGRESS_TIME_INTERVAL);
	
    public WickerWeavingItem(int iItemID )
    {
    	super( iItemID );
    	
        setBuoyant();
        setBellowsBlowDistance(2);
    	setIncineratedInCrucible();
    	setfurnaceburntime(FurnaceBurnTime.WICKER_PIECE);
        setFilterableProperties(Item.FILTERABLE_THIN);
    	
        setUnlocalizedName( "fcItemWickerWeaving" );        
    }
    
    @Override
    protected void playCraftingFX(ItemStack stack, World world, EntityPlayer player)
    {
        player.playSound( "step.grass", 
        	0.25F + 0.25F * (float)world.rand.nextInt( 2 ), 
        	( world.rand.nextFloat() - world.rand.nextFloat() ) * 0.25F + 1.75F );
    }
    
    @Override
    public ItemStack onEaten( ItemStack stack, World world, EntityPlayer player )
    {
        world.playSoundAtEntity( player, "step.grass", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F );
        
        return new ItemStack( BTWItems.wickerPane, 1, 0 );
    }
    
    @Override
    public void onCreated( ItemStack stack, World world, EntityPlayer player ) 
    {
		if (player.timesCraftedThisTick == 0 && world.isRemote )
		{
			player.playSound( "step.grass", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F );
		}
		
    	super.onCreated( stack, world, player );
    }
    
    @Override
    public boolean getCanBeFedDirectlyIntoCampfire(int iItemDamage)
    {
    	return false;
    }
    
    @Override
    public boolean getCanBeFedDirectlyIntoBrickOven(int iItemDamage)
    {
    	return false;
    }
    
    @Override
    protected int getProgressiveCraftingMaxDamage()
    {
    	return WICKER_WEAVING_MAX_DAMAGE;
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
