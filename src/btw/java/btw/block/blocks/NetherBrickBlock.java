// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

import java.util.Random;

public class NetherBrickBlock extends Block
{
    public NetherBrickBlock(int iBlockID )
    {
        super( iBlockID, BTWBlocks.netherRockMaterial);
        
        setHardness( 2F );
        setResistance( 10F );
        
        setPicksEffectiveOn();
        
        setStepSound( soundStoneFootstep );
        
        setUnlocalizedName( "netherBrick" );
        
        setCreativeTab( CreativeTabs.tabBlock );
    }
    
    @Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
    {
        return BTWBlocks.looseNetherBrick.blockID;
    }
    
    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata)
    {
        dropBlockAsItem( world, i, j, k, iMetadata, 0 );
    }
    
	@Override
	public boolean hasMortar(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//    
}