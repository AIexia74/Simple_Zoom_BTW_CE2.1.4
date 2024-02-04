// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.IBlockAccess;

public class OvenBlockIdle extends OvenBlock
{
    public OvenBlockIdle(int iBlockID)
    {
        super( iBlockID, false );
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
        if (isRenderingInterior) {
            // hex 999999
            return 10066329;
        }
        return super.colorMultiplier(blockAccess, x, y, z);
    }
}
