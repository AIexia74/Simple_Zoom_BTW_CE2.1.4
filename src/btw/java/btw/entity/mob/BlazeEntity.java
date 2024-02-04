// FCMOD

package btw.entity.mob;

import btw.item.BTWItems;
import net.minecraft.src.*;

public class BlazeEntity extends EntityBlaze
{
    public BlazeEntity(World world )
    {
        super( world );
    }
    
    @Override
    protected void dropFewItems( boolean bKilledByPlayer, int iLootingModifier )
    {
    	// treat as always killed by player to override vanilla behavior of only dropping rods
    	// when killed by player
    	
    	super.dropFewItems( true, iLootingModifier );
    }
    
    @Override
    public void checkForScrollDrop()
    {    	
    	if ( rand.nextInt( 500 ) == 0 )
    	{
            ItemStack itemstack = new ItemStack( BTWItems.arcaneScroll, 1,
            	Enchantment.flame.effectId );
            
            entityDropItem( itemstack, 0F );
    	}
    }
}
