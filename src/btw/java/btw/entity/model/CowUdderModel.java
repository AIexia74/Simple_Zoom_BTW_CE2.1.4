// FCMOD

package btw.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.ModelQuadruped;
import net.minecraft.src.ModelRenderer;

@Environment(EnvType.CLIENT)
public class CowUdderModel extends ModelQuadruped
{
    public CowUdderModel()
    {
        super(12, 0.0F);
        
        body = new ModelRenderer(this, 18, 4);
        
        body.setRotationPoint(0.0F, 5.0F, 2.0F);
        body.setTextureOffset(50, 0);
        
        body.addBox(-2.0F, 2.0F, -11.0F, 4, 6, 3 );
    }
}
