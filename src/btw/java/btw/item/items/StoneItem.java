//FCMOD

package btw.item.items;

import net.minecraft.src.*;

import java.util.List;

public class StoneItem extends Item {

	public StoneItem(int iItemID ) {
		super( iItemID );

		setMaxDamage( 0 );
		setHasSubtypes( true );
		setFilterableProperties(Item.FILTERABLE_SMALL);
		setCreativeTab( CreativeTabs.tabMaterials );
	}

	//------------- Class Specific Methods ------------//

	//------------ Client Side Functionality ----------//

	private Icon[] iconByMetadataArray = new Icon[3];
	
	@Override
	public Icon getIconFromDamage(int metadata) {
		int var2 = MathHelper.clamp_int(metadata, 0, 2);
		
		return this.iconByMetadataArray[var2];
	}

	@Override
	public void registerIcons(IconRegister register) {
		super.registerIcons(register);
		
		iconByMetadataArray[0] = itemIcon;		
		iconByMetadataArray[1] = register.registerIcon( "fcItemStone_1" );
		iconByMetadataArray[2] = register.registerIcon( "fcItemStone_2" );
	}
	
	@Override
	public void getSubItems(int par1, CreativeTabs tab, List list) {
		list.add(new ItemStack(par1, 1, 0));
		list.add(new ItemStack(par1, 1, 1));
        list.add(new ItemStack(par1, 1, 2));
	}
}
