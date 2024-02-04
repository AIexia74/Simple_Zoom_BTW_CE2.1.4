package net.minecraft.src;

import com.prupe.mcpatcher.cc.ColorizeBlock;
import com.prupe.mcpatcher.cc.Colorizer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EntitySplashFX extends EntityRainFX
{
    public EntitySplashFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        super(par1World, par2, par4, par6);
        this.particleGravity = 0.04F;
        this.nextTextureIndexX();

        if (par10 == 0.0D && (par8 != 0.0D || par12 != 0.0D))
        {
            this.motionX = par8;
            this.motionY = par10 + 0.1D;
            this.motionZ = par12;
        }
        
        if (ColorizeBlock.computeWaterColor(false, (int)this.posX, (int)this.posY, (int)this.posZ))
        {
            this.particleRed = Colorizer.setColor[0];
            this.particleGreen = Colorizer.setColor[1];
            this.particleBlue = Colorizer.setColor[2];
        }
    }
}
