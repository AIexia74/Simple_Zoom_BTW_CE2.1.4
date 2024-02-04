// FCMOD

package btw.block.blocks;

import net.minecraft.src.BlockObsidian;

public class ObsidianBlock extends BlockObsidian
{
    public ObsidianBlock(int iBlockID )
    {
        super( iBlockID );
        
        setHardness( 50F );
        setResistance( 2000F );
        
        setStepSound( soundStoneFootstep );
        
        setUnlocalizedName( "obsidian" );
    }

	@Override
    public int getMobilityFlag()
    {
        return 2; // cannot be pushed
    }
}
