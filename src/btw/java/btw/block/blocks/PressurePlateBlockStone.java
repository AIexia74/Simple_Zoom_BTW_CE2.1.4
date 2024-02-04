// FCMOD

package btw.block.blocks;

import net.minecraft.src.EnumMobType;
import net.minecraft.src.Material;

public class PressurePlateBlockStone extends PressurePlateBlock
{
    public PressurePlateBlockStone(int iBlockID)
    {
    	super( iBlockID, "stone", Material.rock, EnumMobType.mobs );
    	
    	setHardness( 1.5F );
    	
    	setStepSound( soundStoneFootstep );
    	
    	setUnlocalizedName( "pressurePlate" );
    }
}
