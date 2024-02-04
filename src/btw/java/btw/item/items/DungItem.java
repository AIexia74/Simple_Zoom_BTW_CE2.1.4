// FCMOD

package btw.item.items;

import btw.entity.mob.SheepEntity;
import net.minecraft.src.*;

public class DungItem extends Item
{
    public DungItem(int iItemID )
    {
    	super( iItemID );
    	
    	setBuoyant();
    	setIncineratedInCrucible();
		setFilterableProperties(FILTERABLE_SMALL);
    	
    	setUnlocalizedName( "fcItemDung" );
    	
    	setCreativeTab( CreativeTabs.tabMaterials );
    }
    
    @Override
    public boolean itemInteractionForEntity(ItemStack itemstack, EntityLiving entity )
    //public boolean useItemOnEntity( ItemStack itemStack, EntityLiving entity )
    {
        if ( entity instanceof SheepEntity)
        {
            entity.attackEntityFrom( DamageSource.generic, 0 );
            
            return true;
        }
        
        return false;
    }
}