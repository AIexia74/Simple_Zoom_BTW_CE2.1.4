// FCMOD

package btw.item;

import btw.crafting.util.FurnaceBurnTime;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class ItemTemplate extends Item {
    public ItemTemplate(int itemID) {
        super(itemID);

        setNonBuoyant();
        setBellowsBlowDistance(0);
        setNotIncineratedInCrucible();
        setfurnaceburntime(FurnaceBurnTime.NONE);
        setFilterableProperties(FILTERABLE_NO_PROPERTIES);

        setCreativeTab(CreativeTabs.tabMisc);

        setUnlocalizedName("fcItemTemplate");
    }

    //------------- Class Specific Methods ------------//

    //------------ Client Side Functionality ----------//
}
