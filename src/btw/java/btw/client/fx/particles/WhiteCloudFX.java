// FCMOD

package btw.client.fx.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.World;

/**
 *  This is a version of white smoke that doesn't drift upwards over time
 */
@Environment(EnvType.CLIENT)
public class WhiteCloudFX extends WhiteSmokeFX
{
    public WhiteCloudFX(World world, double fXPos, double fYPos, double fZPos, double fXVel, double fYVel, double fZVel )
    {
    	super( world, fXPos, fYPos, fZPos, fXVel, fYVel, fZVel );    	
    }
    
    public void onUpdate()
    {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        if (particleAge++ >= particleMaxAge)
        {
            setDead();
        }

        setParticleTextureIndex(7 - (particleAge * 8) / particleMaxAge);
        moveEntity(motionX, motionY, motionZ);

        motionX *= 0.95999997854232788D;
        motionY *= 0.95999997854232788D;
        motionZ *= 0.95999997854232788D;

        if (onGround)
        {
            motionX *= 0.69999998807907104D;
            motionZ *= 0.69999998807907104D;
        }
    }
}
