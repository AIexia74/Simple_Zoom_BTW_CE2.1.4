// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.World;

public class LooseNetherBrickBlock extends MortarReceiverBlock
{
    public LooseNetherBrickBlock(int iBlockID )
    {
        super( iBlockID, BTWBlocks.netherRockMaterial);
        
        setHardness( 1F ); // setHardness( 2F ); regular nether brick
        setResistance( 5F ); // setResistance( 10F ); regular nether brick
        
        setPicksEffectiveOn();
        
        setStepSound( soundStoneFootstep );
        
        setUnlocalizedName( "fcBlockNetherBrickLoose" );        
        
		setCreativeTab( CreativeTabs.tabBlock );
    }
    
    @Override
    public boolean onMortarApplied(World world, int i, int j, int k)
    {
		world.setBlockWithNotify( i, j, k, Block.netherBrick.blockID );
		
		return true;
    }
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//    
}