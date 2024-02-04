// FCMOD

package btw.block.material;

import net.minecraft.src.MapColor;
import net.minecraft.src.Material;

public class CementMaterial extends Material
{
    public CementMaterial(MapColor mapcolor )
    {
        super(mapcolor);
        
        setReplaceable();
        setNoPushMobility();
    }
}