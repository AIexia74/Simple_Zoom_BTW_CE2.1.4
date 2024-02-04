// FCMOD

package btw.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Entity;
import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelRenderer;

@Environment(EnvType.CLIENT)
public class SquidTentacleModel extends ModelBase
{
    public ModelRenderer modelRenderer;
    
    public SquidTentacleModel()
    {
        modelRenderer = new ModelRenderer(this, 48, 0 );
        
        modelRenderer.addBox(-1.0F, 0F, -1.0F, 2, 16, 2);
        
        modelRenderer.setRotationPoint(0.0F, 7.6F, 0.0F);
    }
    
    public void render(Entity entity, float fPreScaleX, float fPreScaleY, float fPreScaleZ, float fYaw, float fPitch, float fBaseScale )
    {
        this.setRotationAngles( 0F, 0F, 0F, fYaw, fPitch, fBaseScale, entity );
        
        modelRenderer.renderWithScaleToBaseModel(fBaseScale, fPreScaleX, fPreScaleY, fPreScaleZ);
    }
    
    public void setRotationAngles( float par1, float par2, float par3, float fYaw, float fPitch, float fBaseScale, Entity entity )
    {
        super.setRotationAngles( par1, par2, par3, fYaw, fPitch, fBaseScale, entity );

        modelRenderer.rotateAngleY = fYaw / (180F / (float)Math.PI);
        modelRenderer.rotateAngleX = fPitch / (180F / (float)Math.PI);
    }
}
