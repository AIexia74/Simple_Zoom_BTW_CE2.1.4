// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class DaylightDetectorBlock extends BlockDaylightDetector
{
    public DaylightDetectorBlock(int par1)
    {
        super( par1 );
		
        setCreativeTab( null );
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return 0;
    }

    @Override
    public void updateLightLevel(World par1World, int par2, int par3, int par4)
    {
    }

    @Override
    public boolean canProvidePower()
    {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World par1World)
    {
        return null;
    }

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int par1, int par2)
    {
        return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        blockIcon = par1IconRegister.registerIcon( "fcBlockStub" );
    }
}
