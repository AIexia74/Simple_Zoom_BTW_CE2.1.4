// FCMOD

package btw.item.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class MapItem extends ItemMap
{
    public MapItem(int iItemID)
    {
    	super( iItemID );
    	
    	setBuoyant();
		setBellowsBlowDistance(3);
		setFilterableProperties(FILTERABLE_THIN);
		
		setUnlocalizedName( "map" );
    }
    
    @Override
    public void updateMapData(World world, Entity entity, MapData mapData )
    {
        if ( world.provider.dimensionId == mapData.dimension && entity instanceof EntityPlayer)
        {
        	// Note: This function only controls adding new data to the map.  It has nothing to do with existing data, and thus is not called if
        	// the map is on a frame.
        	
            if ( mapData.isEntityLocationVisibleOnMap(entity) )
            {
            	super.updateMapData( world, entity, mapData );
            }
        }
    }

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void addInformation( ItemStack stack, EntityPlayer player, List infoList, boolean bAdvancedTips )
    {
        MapData var5 = this.getMapData(stack, player.worldObj);

        if (var5 != null)
        {
            infoList.add( "Scale: x" + ( 1 << var5.scale ) );
        }
    }
}