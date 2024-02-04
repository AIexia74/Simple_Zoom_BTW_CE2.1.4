// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

public class CreeperOysterBlock extends Block
{
    public final static float HARDNESS = 0.6F;
    
    public CreeperOysterBlock(int iBlockID )
    {
        super( iBlockID, Material.ground );
        
        setHardness(HARDNESS);
        setShovelsEffectiveOn(true);
        
        setBuoyancy(1F);
        
        setStepSound( BTWBlocks.stepSoundSquish);
        
        setCreativeTab( CreativeTabs.tabBlock );
        
        setUnlocalizedName( "fcBlockCreeperOysters" );
    }
    
	@Override
    public boolean doesBlockBreakSaw(World world, int i, int j, int k )
    {
		return false;
    }
	
	@Override
    public boolean canBePistonShoveled(World world, int i, int j, int k)
    {
    	return true;
    }
	
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//
}