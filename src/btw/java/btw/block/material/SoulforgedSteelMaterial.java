// FCMOD

package btw.block.material;

import net.minecraft.src.MapColor;
import net.minecraft.src.Material;

public class SoulforgedSteelMaterial extends Material
{
    public SoulforgedSteelMaterial(MapColor mapColor )
    {
        super( mapColor );
        setImmovableMobility(); // same as obsidian
        setRequiresTool(); // can only be harvested if the tool in question is specifically coded for it
    }
}
