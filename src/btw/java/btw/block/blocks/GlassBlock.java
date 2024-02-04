// FCMOD

package btw.block.blocks;

import net.minecraft.src.BlockGlass;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;

public class GlassBlock extends BlockGlass
{
    public GlassBlock(int iBlockID, Material material, boolean bRenderFacesNeighboringOnSameBlock )
    {
        super( iBlockID, material, bRenderFacesNeighboringOnSameBlock );
        
        setPicksEffectiveOn(true);
    }
    
    @Override
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
		return bIgnoreTransparency;
	}
    
    @Override
	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}	
	
    @Override
	public boolean canTransmitRotationHorizontallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
    @Override
	public boolean canTransmitRotationVerticallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
}
