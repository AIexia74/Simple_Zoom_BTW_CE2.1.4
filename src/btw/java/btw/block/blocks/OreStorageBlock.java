// FCMOD

package btw.block.blocks;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Material;

public class OreStorageBlock extends FullBlock
{
    public OreStorageBlock(int iBlockID )
    {
        super( iBlockID, Material.iron );
        
        setPicksEffectiveOn();
        
        setCreativeTab( CreativeTabs.tabBlock );
    }
    
	//----------- Client Side Functionality -----------//
}
