// FCMOD

package btw.block.blocks;

import net.minecraft.src.BlockJukeBox;
import btw.crafting.util.FurnaceBurnTime;

public class JukeboxBlock extends BlockJukeBox
{
    public JukeboxBlock(int iBlockID)
    {
    	super( iBlockID );

    	setHardness( 1.5F );
    	setResistance( 10F );
    	setAxesEffectiveOn();
    	
    	setBuoyant();
    	setFurnaceBurnTime(FurnaceBurnTime.WOOD_BASED_BLOCK);
    	
    	setStepSound( soundStoneFootstep );
    	
    	setUnlocalizedName( "jukebox" );
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}
