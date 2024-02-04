// FCMOD 

package btw.item.blockitems.legacy;

import btw.block.BTWBlocks;
import btw.block.blocks.legacy.LegacyCornerBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Icon;
import net.minecraft.src.ItemBlock;

public class LegacyCornerBlockItem extends ItemBlock
{
    public LegacyCornerBlockItem(int iItemID )
    {
        super( iItemID );
        
        setMaxDamage( 0 );
        setHasSubtypes(true);
        
        setUnlocalizedName( "fcCorner" );
    }

    @Override
    public int getMetadata( int iDamage )
    {
		return iDamage;
    }
    
    @Override    
    public float getBuoyancy(int iItemDamage)
    {
    	if ( iItemDamage > 0 ) // stone corner
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
    public Icon getIconFromDamage(int iDamage )
    {
    	if ( iDamage > 0 )
    	{
    		return BTWBlocks.legacyStoneAndOakCorner.blockIcon;
    	}
    	else
    	{
    		return ((LegacyCornerBlock) BTWBlocks.legacyStoneAndOakCorner).iconWood;
    	}
    }
}