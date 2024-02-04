// FCMOD

package btw.item.items;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticVegetationBlock;
import btw.block.blocks.NetherGrothBlock;
import btw.client.fx.BTWEffectManager;
import btw.entity.UrnEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class SoulUrnItem extends ThrowableItem
{
    public SoulUrnItem(int iItemID )
    {
    	super( iItemID );
    	
        maxStackSize = 16;
        
        setBuoyant();
        
    	setUnlocalizedName( "fcItemUrnSoul" );
    	
    	setCreativeTab( CreativeTabs.tabMaterials );
    }

	@Override
	public boolean isMultiUsePerClick()	{
		return false;
	}
	
	@Override
    protected void spawnThrownEntity(ItemStack stack, World world,
                                     EntityPlayer player)
    {
        world.spawnEntityInWorld( EntityList.createEntityOfType(UrnEntity.class, world, player, itemID ) );
    }
    
    @Override
    protected EntityThrowable getEntityFiredByByBlockDispenser(World world,
                                                               double dXPos, double dYPos, double dZPos)
    {
    	return (EntityThrowable) EntityList.createEntityOfType(UrnEntity.class, world, dXPos, dYPos, dZPos, itemID );
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean hasEffect( ItemStack itemStack )
    {
		return true;
    }
}
