// FCMOD

package btw.entity.model;

import btw.util.status.BTWStatusCategory;
import btw.util.status.StatusEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ModelBiped;

@Environment(EnvType.CLIENT)
public class PlayerArmorModel extends ModelBiped
{
    public PlayerArmorModel()
    {
    	super();
    }

    public PlayerArmorModel(float par1)
    {
    	super( par1 );
    }

    public PlayerArmorModel(float par1, float par2, int par3, int par4)
    {
    	super( par1, par2, par3, par4 );
    }
    
    @Override
    public void render(Entity entity, float par2, float par3, float par4, float par5, float par6, float par7 )
    {
    	EntityPlayer player = (EntityPlayer)entity;
    	
    	int iFatPenalty = player.getStatusForCategory(BTWStatusCategory.FAT).map(StatusEffect::getLevel).orElse(0);
    	
    	if ( iFatPenalty == 0 )
    	{
    		super.render( entity, par2, par3, par4, par5, par6, par7 );
    	}
    	else 
    	{
            setRotationAngles( par2, par3, par4, par5, par6, par7, entity );

            bipedHead.render( par7 );
            
    		if ( iFatPenalty == 1 )
        	{
                bipedBody.renderWithScaleToBaseModel(par7, 1.0625F, 1.0F, 1.4F);
        	}
        	else if ( iFatPenalty == 2 )
        	{
                bipedBody.renderWithScaleToBaseModel(par7, 1.125F, 1.0F, 1.8F);
        	}
        	else if ( iFatPenalty == 3 )
        	{
                bipedBody.renderWithScaleToBaseModel(par7, 1.1875F, 1.0F, 2.2F);
        	}
        	else // 4
        	{
                bipedBody.renderWithScaleToBaseModel(par7, 1.25F, 1.0F, 2.6F);
        	}
    		
            bipedRightArm.render( par7 );
            bipedLeftArm.render( par7 );
            bipedRightLeg.render( par7 );
            bipedLeftLeg.render( par7 );
            bipedHeadwear.render( par7 );
    	}    	
    }
}
