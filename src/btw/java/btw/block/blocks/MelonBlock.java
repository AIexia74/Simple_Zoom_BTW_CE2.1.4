// FCMOD

package btw.block.blocks;

import btw.client.fx.BTWEffectManager;
import btw.item.BTWItems;
import btw.util.CustomDamageSource;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class MelonBlock extends GourdBlock
{
    public MelonBlock(int iBlockID)
    {
        super( iBlockID );        
    }
    
    //------------- FCBlockGourd ------------//
    
    @Override
	protected Item itemToDropOnExplode()
    {
    	return BTWItems.mashedMelon;
    }
	
    @Override
	protected int itemCountToDropOnExplode()
    {
    	return 2;
    }
	
    @Override
	protected int auxFXIDOnExplode()
    {
    	return BTWEffectManager.MELON_EXPLODE_EFFECT_ID;
    }
    
	protected DamageSource getFallDamageSource()
	{
		return CustomDamageSource.damageSourceMelon;
	}
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		blockIcon = register.registerIcon( "melon_side" );

        iconTop = register.registerIcon("melon_top");
    }
}
