// FCMOD

package btw.item.items;

import net.minecraft.src.*;

public class EggItem extends ThrowableItem
{
    public EggItem(int iItemID )
    {
    	super( iItemID );

        maxStackSize = 16;
        
    	setIncineratedInCrucible();
    	setFilterableProperties(FILTERABLE_SMALL);
    	
    	setUnlocalizedName( "egg" );
    	
        setCreativeTab( CreativeTabs.tabFood );
    }
    
    @Override
    protected void spawnThrownEntity(ItemStack stack, World world,
                                     EntityPlayer player)
    {
        world.spawnEntityInWorld( EntityList.createEntityOfType(EntityEgg.class, world, player ) );
    }
    
    @Override
    protected EntityThrowable getEntityFiredByByBlockDispenser(World world,
                                                               double dXPos, double dYPos, double dZPos)
    {
    	return (EntityThrowable) EntityList.createEntityOfType(EntityEgg.class, world, dXPos, dYPos, dZPos );
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//
}
