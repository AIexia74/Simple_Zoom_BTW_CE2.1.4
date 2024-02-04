// FCMOD

package btw.entity.mob;

import btw.item.BTWItems;
import net.minecraft.src.*;

public class BatEntity extends EntityBat
{
    public BatEntity(World world )
    {
        super( world );
    }
    
    @Override
    public void checkForScrollDrop()
    {    	
    	if ( rand.nextInt( 250 ) == 0 )
    	{
            ItemStack itemstack = new ItemStack( BTWItems.arcaneScroll, 1,
            	Enchantment.featherFalling.effectId );
            
            entityDropItem( itemstack, 0F );
    	}
    }
    
    @Override
    protected void dropFewItems( boolean bPlayerKilled, int iFortuneLevel )
    {
    	int iNumDrop = 1;
    	
    	if ( rand.nextInt( 4 ) - iFortuneLevel <= 0 )
    	{
    		iNumDrop = 2;
    	}
    	
        for ( int iTempCount = 0; iTempCount < iNumDrop; iTempCount++ )
        {
            dropItem( BTWItems.batWing.itemID, 1 );
        }    	
    }
    
    @Override
    public boolean attractsLightning()
    {
    	return false;
    }
}