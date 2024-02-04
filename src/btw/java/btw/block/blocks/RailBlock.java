// FCMOD

package btw.block.blocks;

import net.minecraft.src.BlockRail;

public class RailBlock extends BlockRail
{
	public RailBlock(int iBlockID )
	{
		super( iBlockID );
		
		setHardness( 0.7F );
		setPicksEffectiveOn();
		
		setStepSound( soundMetalFootstep );
		
		setUnlocalizedName( "rail" );		
	}
}
