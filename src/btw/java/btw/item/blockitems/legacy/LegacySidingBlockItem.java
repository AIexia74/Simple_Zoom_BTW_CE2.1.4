// FCMOD 

package btw.item.blockitems.legacy;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Icon;
import net.minecraft.src.ItemBlock;

public class LegacySidingBlockItem extends ItemBlock
{
    public LegacySidingBlockItem(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
        setUnlocalizedName("fcBlockOmniSlab");
    }

    @Override
    public int getMetadata( int i )
    {
        return i;
    }

    @Override
    public float getBuoyancy(int iItemDamage)
    {
    	if ( iItemDamage == 0 ) // stone siding
    	{
    		return -1.0F;
    	}
    	else
    	{
    		return super.getBuoyancy(iItemDamage);
    	}
    }

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamage(int i )
    {
        return BTWBlocks.legacyStoneAndOakSiding.getIcon( 2, i );
    }
}
