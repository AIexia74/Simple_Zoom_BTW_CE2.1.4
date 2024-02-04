// FCMOD (client only)

package btw.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import btw.entity.mob.PigEntity;
import net.minecraft.src.ModelPig;

@Environment(EnvType.CLIENT)
public class PigModel extends ModelPig
{
    private float headRotation;
    
    public PigModel()
    {
        this( 0F );
    }

    public PigModel(float fScaleFactor )
    {
    	// scale factor appears to be used by saddled render pass model
    	
        super( fScaleFactor );
    }
    
    public void setLivingAnimations(EntityLiving entity, float par2, float par3,
                                    float fPartialTick )
    {
        super.setLivingAnimations( entity, par2, par3, fPartialTick );

        PigEntity pig = (PigEntity)entity;
        
        if ( !pig.isChild() )
        {
        	head.rotationPointY = 12F + pig.getGrazeHeadVerticalOffset(fPartialTick) * 4F;
        }
        else
        {        	
        	head.rotationPointY = 12F + pig.getGrazeHeadVerticalOffset(fPartialTick) * 2F;
        }


        headRotation = pig.getGrazeHeadRotation(fPartialTick);
    }

    public void setRotationAngles( float par1, float par2, float par3, 
    	float par4, float par5, float par6, Entity entity )
    {
        super.setRotationAngles( par1, par2, par3, par4, par5, par6, entity );
        
        head.rotateAngleX = headRotation;
    }
    
	//------------- Class Specific Methods ------------//
}
