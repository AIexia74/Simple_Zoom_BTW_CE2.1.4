// FCMOD

package btw.block.blocks;

import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Material;
import net.minecraft.src.World;

public class LooseBrickBlock extends MortarReceiverBlock
{
    public LooseBrickBlock(int iBlockID )
    {
        super( iBlockID, Material.rock );
        
        setHardness( 1F ); // setHardness( 2F ); regular brick
        setResistance( 5F ); // setResistance( 10F ); regular brick        
        setPicksEffectiveOn();
        setChiselsEffectiveOn();
        
        setStepSound( Block.soundStoneFootstep );
        
        setUnlocalizedName( "fcBlockBrickLoose" );        
        
		setCreativeTab( CreativeTabs.tabBlock );
    }
	
    @Override
    public boolean onMortarApplied(World world, int i, int j, int k)
    {
		world.setBlockWithNotify( i, j, k, Block.brick.blockID );
		
		return true;
    }
    
    //------------- Class Specific Methods ------------//    
    
	//----------- Client Side Functionality -----------//    
}