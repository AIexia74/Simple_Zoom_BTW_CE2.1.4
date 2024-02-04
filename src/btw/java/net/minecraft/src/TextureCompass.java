package net.minecraft.src;

import com.prupe.mcpatcher.hd.FancyDial;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public class TextureCompass extends TextureStitched
{
    public static TextureCompass compassTexture;

    /** Current compass heading in radians */
    public double currentAngle;

    /** Speed and direction of compass rotation */
    public double angleDelta;
    
    // FCMOD: Added
    private boolean directionUpdated = false;
    // END FCMOD

    public TextureCompass()
    {
        super("compass");
        compassTexture = this;
        FancyDial.setup(this);
    }

    public void updateAnimation()
    {
    	updateInert();

        directionUpdated = false;
    }

    /**
     * Updates the compass based on the given x,z coords and camera direction
     */
    private void updateCompass(World par1World, double par2, double par4, double par6, boolean par8, boolean par9, EntityPlayer player )
    {
        double var10 = 0.0D;

        if (par1World != null && !par8)
        {
        	var10 = computeCompassAngle(par2, par4, par6, par9, player);
        }

        if (par9)
        {
            this.currentAngle = var10;
        }
        else if ( !directionUpdated)
        {
            double var17;

            for (var17 = var10 - this.currentAngle; var17 < -Math.PI; var17 += (Math.PI * 2D))
            {
                ;
            }

            while (var17 >= Math.PI)
            {
                var17 -= (Math.PI * 2D);
            }

            if (var17 < -1.0D)
            {
                var17 = -1.0D;
            }

            if (var17 > 1.0D)
            {
                var17 = 1.0D;
            }

            this.angleDelta += var17 * 0.1D;
            this.angleDelta *= 0.8D;
            this.currentAngle += this.angleDelta;
            // FCMOD: Added
            directionUpdated = true;
            // END FCMOD
        }

        if (!FancyDial.update(this, par9))
        {
        	int var18;

        	for (var18 = (int)((this.currentAngle / (Math.PI * 2D) + 1.0D) * (double)this.textureList.size()) % this.textureList.size(); var18 < 0; var18 = (var18 + this.textureList.size()) % this.textureList.size())
            {
                ;
            }

            if (var18 != this.frameCounter)
            {
                this.frameCounter = var18;
                this.textureSheet.copyFrom(this.originX, this.originY, (Texture)this.textureList.get(this.frameCounter), this.rotated);
            }
        }
    }
    
    // FCMOD: Added (client only)
    public void updateActive()
    {
        Minecraft mc = Minecraft.getMinecraft();

        if ( mc.theWorld != null && mc.thePlayer != null )
        {
            updateCompass( mc.theWorld, mc.thePlayer.posX, mc.thePlayer.posZ, 
            	mc.thePlayer.rotationYaw, false, false, mc.thePlayer );
        }
        else
        {
            updateCompass( null, 0.0D, 0.0D, 0.0D, true, false, null );
        }
    }
    
    public void updateInert()
    {
        this.frameCounter = textureList.size() / 2;
        this.textureSheet.copyFrom(this.originX, this.originY, (Texture)this.textureList.get(this.frameCounter), this.rotated);
    }
    
	private double computeCompassAngle(double dSourceX, double dSourceZ, double dSourceYaw, boolean bIsInFrame, EntityPlayer player)
	{
		double angle = Math.PI;
		
        if ( !bIsInFrame && player != null )
		{
            if ( !player.hasValidMagneticPointForLocation() )
            {
                angle = Math.random() * Math.PI * 2.0D;
            }
            else
            {
		        int iTargetI = player.getStongestMagneticPointForLocationI();
		        int iTargetK = player.getStongestMagneticPointForLocationK();
		        
		        double dDeltaX = (double)iTargetI + 0.5D - dSourceX;
		        double dDeltaZ = (double)iTargetK + 0.5D - dSourceZ;
		        
		        dSourceYaw %= 360D;
		        
		        angle = -(( dSourceYaw - 90.0D ) * Math.PI / 180.0D - Math.atan2( dDeltaZ, dDeltaX ));
            }
		}
        
		return angle;
	}
    // END FCMOD
}
