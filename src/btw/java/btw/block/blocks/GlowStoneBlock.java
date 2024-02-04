// FCMOD

package btw.block.blocks;

import net.minecraft.src.BlockGlowStone;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;

public class GlowStoneBlock extends BlockGlowStone
{
    public GlowStoneBlock(int iBlockID )
    {
    	super( iBlockID, Material.glass );
    	
	    setHardness( 0.6F );
	    setResistance( 0.5F ); // preserve vanilla resistance
	    
    	setPicksEffectiveOn();
    	
    	setLightValue( 1F );
    	
    	setStepSound( soundGlassFootstep );
    	
    	setUnlocalizedName( "lightgem" );
    }
    
    @Override
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
		return bIgnoreTransparency;
	}
}