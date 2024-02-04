// FCMOD

package btw.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class StokedFireTexture extends FireTexture
{
    public StokedFireTexture(String sName, int iFireAnimationIndex )
    {
        super( sName, iFireAnimationIndex );

        fireAnimationIndex = iFireAnimationIndex;
    }
    
    @Override
    public void updateAnimation()
    {
    	frameCounter = 0;

        if (fireAnimation != null )
        {
        	fireAnimation.copyStokedFireFrameToByteBuffer(frameBuffer, bufferPixelSize);
        }
    	
    	textureSheet.uploadByteBufferToGPU(originX, originY, frameBuffer, bufferWidth, bufferHeight);
    }
    
    @Override
    public boolean isProcedurallyAnimated()
    {
    	return true;
    }    
}
