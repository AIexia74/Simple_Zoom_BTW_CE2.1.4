// FCMOD (client only)

package btw.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import btw.entity.mob.ChickenEntity;
import net.minecraft.src.ModelChicken;

@Environment(EnvType.CLIENT)
public class ChickenModel extends ModelChicken
{
    private float headRotation;
    
    public ChickenModel()
    {
        super();
    }

    @Override    
    public void setLivingAnimations(EntityLiving entity, float par2, float par3,
                                    float fPartialTick )
    {
        super.setLivingAnimations( entity, par2, par3, fPartialTick );

        ChickenEntity chicken = (ChickenEntity)entity;
        
        if ( !chicken.isChild() )
        {
        	head.rotationPointY = 15F + chicken.getGrazeHeadVerticalOffset(fPartialTick) * 3F;
        }
        else
        {        	
        	head.rotationPointY = 15F + chicken.getGrazeHeadVerticalOffset(fPartialTick) * 1.5F;
        }
        	
        bill.rotationPointY = chin.rotationPointY = head.rotationPointY;

        headRotation = chicken.getGrazeHeadRotation(fPartialTick);
    }

    @Override    
    public void setRotationAngles( float par1, float par2, float par3, 
    	float par4, float par5, float par6, Entity entity )
    {
        super.setRotationAngles( par1, par2, par3, par4, par5, par6, entity );
        
        head.rotateAngleX = bill.rotateAngleX = chin.rotateAngleX = headRotation;
    }
    
	//------------- Class Specific Methods ------------//
}
