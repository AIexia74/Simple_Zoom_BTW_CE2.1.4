// FCMOD

package btw.item.items;

import btw.crafting.util.FurnaceBurnTime;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class BarkItem extends Item
{
	public static final int SUBTYPE_OAK = 0;
	public static final int SUBTYPE_SPRUCE = 1;
	public static final int SUBTYPE_BIRCH = 2;
	public static final int SUBTYPE_JUNGLE = 3;
	public static final int SUBTYPE_BLOOD_WOOD = 4;
	
	public static final int NUM_SUBTYPES = 5;
	
    private String[] nameExtensionsBySubtype = new String[] {"oak", "spruce", "birch", "jungle", "bloodwood" };
    
    public BarkItem(int iItemID )
    {
        super( iItemID );
        
        setHasSubtypes( true );
        setMaxDamage( 0 );
        
        setBuoyant();
        setBellowsBlowDistance(2);
    	setfurnaceburntime(FurnaceBurnTime.KINDLING);
		setFilterableProperties(FILTERABLE_SMALL | FILTERABLE_THIN);

        setUnlocalizedName( "fcItemBark" );
        
        setCreativeTab( CreativeTabs.tabMaterials );
    }
    
    @Override
    public String getUnlocalizedName( ItemStack stack )
    {
        int iSubtype = MathHelper.clamp_int(stack.getItemDamage(), 0, NUM_SUBTYPES - 1);
        
        return super.getUnlocalizedName() + "." + nameExtensionsBySubtype[iSubtype];
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconBySubtype = new Icon[NUM_SUBTYPES];
    @Environment(EnvType.CLIENT)
    private String[] iconNamesBySubtype = new String[] {"fcItemBarkOak", "fcItemBarkSpruce", "fcItemBarkBirch", "fcItemBarkJungle", "fcItemBarkBloodWood" };

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        for (int iTempSubtype = 0; iTempSubtype < iconNamesBySubtype.length; ++iTempSubtype)
        {
            iconBySubtype[iTempSubtype] = par1IconRegister.registerIcon(iconNamesBySubtype[iTempSubtype]);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamage( int iDamage )
    {
        int iIconIndex = MathHelper.clamp_int(iDamage, 0, NUM_SUBTYPES - 1);
        
        return iconBySubtype[iIconIndex];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubItems( int iItemID, CreativeTabs creativeTabs, List list )
    {
    	for (int iSubtype = 0; iSubtype < NUM_SUBTYPES; iSubtype++ )
    	{
    		list.add( new ItemStack( iItemID, 1, iSubtype ) );
    	}
    }
}
