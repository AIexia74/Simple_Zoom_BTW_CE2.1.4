// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class StubBlock extends Block
{
    private final IBehaviorDispenseItem dropperDefaultBehaviour = new BehaviorDefaultDispenseItem();

    public StubBlock(int par1)
    {
        super( par1, Material.rock );
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        blockIcon = register.registerIcon("fcBlockStub");
    }
}
