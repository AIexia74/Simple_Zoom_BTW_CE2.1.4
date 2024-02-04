// FCMOD

package btw.block.blocks;

import net.minecraft.src.BlockCloth;
import btw.block.util.Flammability;

public class WoolBlock extends BlockCloth
{
    public WoolBlock()
    {
        super();
        
        setHardness( 0.8F );
        
        setBuoyant();
        
		setFireProperties(Flammability.CLOTH);
		
		setStepSound( soundClothFootstep );
		
		setUnlocalizedName( "cloth" );		
    }
}
