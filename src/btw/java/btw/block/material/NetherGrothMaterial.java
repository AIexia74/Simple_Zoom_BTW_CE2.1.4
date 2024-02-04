// FCMOD

package btw.block.material;

import net.minecraft.src.MapColor;
import net.minecraft.src.Material;

public class NetherGrothMaterial extends Material
{
    public NetherGrothMaterial(MapColor mapColor )
    {
        super( mapColor );
        setNoPushMobility(); // breaks on push by piston
    }

    public boolean blocksMovement()
    {
        // so that fluid can overwrite block

        return false;
    }
}