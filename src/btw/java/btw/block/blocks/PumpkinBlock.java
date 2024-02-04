// FCMOD

package btw.block.blocks;

import btw.client.fx.BTWEffectManager;
import btw.util.CustomDamageSource;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class PumpkinBlock extends GourdBlock
{
    public PumpkinBlock(int iBlockID, boolean bStub)
    {
        super( iBlockID );
        
        setHardness(1.0F);
        
        setStepSound(soundWoodFootstep);
        
        setUnlocalizedName("fcBlockPumpkinFresh");    
    }

    //------------- FCBlockGourd ------------//
    
    @Override
	protected Item itemToDropOnExplode()
    {
    	return Item.pumpkinSeeds;
    }
	
    @Override
	protected int itemCountToDropOnExplode()
    {
    	return 4;
    }
	
    @Override
	protected int auxFXIDOnExplode()
    {
    	return BTWEffectManager.PUMPKIN_EXPLODE_EFFECT_ID;
    }
    
	protected DamageSource getFallDamageSource()
	{
		return CustomDamageSource.damageSourcePumpkin;
	}
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		blockIcon = register.registerIcon( "pumpkin_side" );

        iconTop = register.registerIcon("pumpkin_top");
    }
}