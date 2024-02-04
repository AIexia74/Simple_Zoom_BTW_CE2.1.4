// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class RottenFleshBlock extends Block
{
    public final static float HARDNESS = 0.6F;
    
    public RottenFleshBlock(int iBlockID )
    {
        super( iBlockID, Material.ground );
        
        setHardness(HARDNESS);
        setBuoyancy(1F);
        
        setShovelsEffectiveOn(true);
        
        setStepSound( BTWBlocks.stepSoundSquish);
        
        setCreativeTab( CreativeTabs.tabBlock );
        
        setUnlocalizedName( "fcBlockRottenFlesh" );
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

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        blockIcon = register.registerIcon( "fcBlockRottenFlesh" );
    }	
}