// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;

public class NetherrackBlock extends FullBlock
{
    public NetherrackBlock(int iBlockID )
    {
        super( iBlockID, BTWBlocks.netherRockMaterial);
        
        setHardness( 0.6F );        
        setResistance( 0.4F * 5F / 3F ); // 0.4 was original hardness of netherrack.  Equation to preserve its original blast resistance.
        setPicksEffectiveOn();
        
        setStepSound( soundStoneFootstep );
        
        setCreativeTab( CreativeTabs.tabBlock );
        
        setUnlocalizedName( "hellrock" );        
    }
    
    @Override
    public float getMovementModifier(World world, int i, int j, int k)
    {
    	return 1F;
    }    
    
    @Override
    public int getEfficientToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return 2; // iron and better
    }
    
	@Override
    public void onBlockAdded( World world, int i, int j, int k )
    {
        super.onBlockAdded( world, i, j, k );
        
        if ( !world.provider.isHellWorld )
        {
        	// "2" in last param to not trigger another neighbor block notify
        	
            world.setBlock( i, j, k, BTWBlocks.fallingNetherrack.blockID, 0, 2 );
        }
    }
	
    //------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//
}
