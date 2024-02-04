// FCMOD

package btw.entity.model;

import btw.entity.mob.DireWolfEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

@Environment(EnvType.CLIENT)
public class DireWolfModel extends ModelBase
{
    public ModelRenderer modelHeadMain;
    public ModelRenderer modelBody;
    public ModelRenderer modelLeg1;
    public ModelRenderer modelLeg2;
    public ModelRenderer modelLeg3;
    public ModelRenderer modelLeg4;
    ModelRenderer modelTail;
    ModelRenderer modelMane;

    private float headRotation;
    
    public DireWolfModel()
    {
        float var1 = 0.0F;
        float var2 = 13.5F;
        
        this.modelHeadMain = new ModelRenderer(this, 0, 0);
        this.modelHeadMain.addBox(-3.0F, -3.0F, -2.0F, 6, 6, 4, var1);
        this.modelHeadMain.setRotationPoint(-1.0F, var2, -7.0F);
        this.modelBody = new ModelRenderer(this, 18, 14);
        this.modelBody.addBox(-4.0F, -2.0F, -3.0F, 6, 9, 6, var1);
        this.modelBody.setRotationPoint(0.0F, 14.0F, 2.0F);
        this.modelMane = new ModelRenderer(this, 21, 0);
        this.modelMane.addBox(-4.0F, -3.0F, -3.0F, 8, 6, 7, var1);
        this.modelMane.setRotationPoint(-1.0F, 14.0F, 2.0F);
        this.modelLeg1 = new ModelRenderer(this, 0, 18);
        this.modelLeg1.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, var1);
        this.modelLeg1.setRotationPoint(-2.5F, 16.0F, 7.0F);
        this.modelLeg2 = new ModelRenderer(this, 0, 18);
        this.modelLeg2.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, var1);
        this.modelLeg2.setRotationPoint(0.5F, 16.0F, 7.0F);
        this.modelLeg3 = new ModelRenderer(this, 0, 18);
        this.modelLeg3.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, var1);
        this.modelLeg3.setRotationPoint(-2.5F, 16.0F, -4.0F);
        this.modelLeg4 = new ModelRenderer(this, 0, 18);
        this.modelLeg4.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, var1);
        this.modelLeg4.setRotationPoint(0.5F, 16.0F, -4.0F);
        this.modelTail = new ModelRenderer(this, 9, 18);
        this.modelTail.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, var1);
        this.modelTail.setRotationPoint(-1.0F, 12.0F, 8.0F);
        
        this.modelHeadMain.setTextureOffset(16, 14).addBox(-3.0F, -5.0F, 0.0F, 2, 2, 1, var1);
        this.modelHeadMain.setTextureOffset(16, 14).addBox(1.0F, -5.0F, 0.0F, 2, 2, 1, var1);
        this.modelHeadMain.setTextureOffset(0, 10).addBox(-1.5F, 0.0F, -5.0F, 3, 3, 4, var1);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        super.render(par1Entity, par2, par3, par4, par5, par6, par7);
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);

        this.modelHeadMain.renderWithRotation(par7);
        this.modelBody.render(par7);
        this.modelLeg1.render(par7);
        this.modelLeg2.render(par7);
        this.modelLeg3.render(par7);
        this.modelLeg4.render(par7);
        this.modelTail.renderWithRotation(par7);
        this.modelMane.render(par7);
    }

    /**
     * Used for easily adding entity-dependent animations. The second and third float params here are the same second
     * and third as in the setRotationAngles method.
     */
    public void setLivingAnimations(EntityLiving par1EntityLiving, float par2, float par3, float par4)
    {
        DireWolfEntity var5 = (DireWolfEntity)par1EntityLiving;

        this.modelTail.rotateAngleY = 0.0F;

        this.modelBody.setRotationPoint(0.0F, 14.0F, 2.0F);
        this.modelBody.rotateAngleX = ((float)Math.PI / 2F);
        this.modelMane.setRotationPoint(-1.0F, 14.0F, -3.0F);
        this.modelMane.rotateAngleX = this.modelBody.rotateAngleX;
        this.modelTail.setRotationPoint(-1.0F, 12.0F, 8.0F);
        this.modelLeg1.setRotationPoint(-2.5F, 16.0F, 7.0F);
        this.modelLeg2.setRotationPoint(0.5F, 16.0F, 7.0F);
        this.modelLeg3.setRotationPoint(-2.5F, 16.0F, -4.0F);
        this.modelLeg4.setRotationPoint(0.5F, 16.0F, -4.0F);
        this.modelLeg1.rotateAngleX = MathHelper.cos(par2 * 0.6662F) * 1.4F * par3;
        this.modelLeg2.rotateAngleX = MathHelper.cos(par2 * 0.6662F + (float)Math.PI) * 1.4F * par3;
        this.modelLeg3.rotateAngleX = MathHelper.cos(par2 * 0.6662F + (float)Math.PI) * 1.4F * par3;
        this.modelLeg4.rotateAngleX = MathHelper.cos(par2 * 0.6662F) * 1.4F * par3;

        modelHeadMain.rotationPointY = 13.5F + ((DireWolfEntity)par1EntityLiving).getHeadRotationPointOffset(par4) * 5.0F;
        headRotation = ((DireWolfEntity)par1EntityLiving).getHeadRotation(par4);
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {
        super.setRotationAngles(par1, par2, par3, par4, par5, par6, par7Entity);
        this.modelHeadMain.rotateAngleX = par5 / (180F / (float)Math.PI);
        this.modelHeadMain.rotateAngleY = par4 / (180F / (float)Math.PI);
        this.modelTail.rotateAngleX = par3;

        modelHeadMain.rotateAngleX = headRotation;
    }    
}
