// FCMOD

package btw.block.blocks.legacy;

import net.minecraft.src.BlockSnowBlock;
import net.minecraft.src.World;

public class LegacySnowBlock extends BlockSnowBlock
{
    public LegacySnowBlock(int iBlockID)
    {
    	super( iBlockID );
    	
    	setHardness( 0.2F );
    	setShovelsEffectiveOn();
    	
    	setBuoyant();
    	
    	setStepSound( soundSnowFootstep );
    	
    	setUnlocalizedName( "snow" );
    	
        setCreativeTab( null );    	
    }
    
    @Override
    public boolean canBePistonShoveled(World world, int i, int j, int k)
    {
    	return true;
    }
}
