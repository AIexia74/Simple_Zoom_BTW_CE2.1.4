// FCMOD

package btw.item.items;

import btw.block.BTWBlocks;
import net.minecraft.src.Block;

public class DoorItemWood extends DoorItem
{
    public DoorItemWood(int iITemID )
    {
        super( iITemID );

        setBuoyant();
        setIncineratedInCrucible();
        
        setUnlocalizedName( "doorWood" );
    }

    @Override
    public Block getDoorBlock()
    {
    	return BTWBlocks.woodenDoor;
    }
    
    //------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//
}
