// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Block;
import net.minecraft.src.Icon;

public class ButtonBlockWood extends ButtonBlock
{
    public ButtonBlockWood(int iBlockID)
    {
        super( iBlockID, true );
        
        setAxesEffectiveOn(true);
        setBuoyant();
    }

    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int iSide, int iMetadata )
    {
        return Block.planks.getBlockTextureFromSide(1);
    }
}
