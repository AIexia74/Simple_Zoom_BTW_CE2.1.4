// FCMOD

package btw.item.items;

import net.minecraft.src.ItemCarrotOnAStick;

public class CarrotOnAStickItem extends ItemCarrotOnAStick
{
    public CarrotOnAStickItem(int iItemID )
    {
        super( iItemID );

		setBuoyant();
        setFilterableProperties(FILTERABLE_NARROW);
		
    	setAsBasicPigFood();
        
        setUnlocalizedName( "carrotOnAStick" );
    }
    
    //------------- Class Specific Methods ------------//	
    
	//----------- Client Side Functionality -----------//
}
