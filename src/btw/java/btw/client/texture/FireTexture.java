// FCMOD

package btw.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Texture;
import net.minecraft.src.TextureStitched;

import java.nio.ByteBuffer;
import java.util.List;

@Environment(EnvType.CLIENT)
public class FireTexture extends TextureStitched
{
    ByteBuffer frameBuffer;
    
    int bufferWidth;
    int bufferHeight;
    int bufferPixelSize;
    
    int fireAnimationIndex;
    FireAnimation fireAnimation = null;

    public FireTexture(String sName, int iFireAnimationIndex )
    {
        super( sName );

        fireAnimationIndex = iFireAnimationIndex;
    }
    
    @Override
    public void init(Texture par1Texture, List par2List, int par3, int par4, int par5, int par6, boolean par7)
    {
    	super.init(par1Texture, par2List, par3, par4, par5, par6, par7);

        bufferWidth = ((Texture)this.textureList.get(0)).getWidth();
        bufferHeight = ((Texture)this.textureList.get(0)).getHeight();
        bufferPixelSize = bufferWidth * bufferHeight;

        frameBuffer = ByteBuffer.allocateDirect(bufferPixelSize * 4);

        fireAnimation = FireAnimation.instanceArray[fireAnimationIndex];
        
        if (fireAnimation == null )
        {
            fireAnimation = new FireAnimation(fireAnimationIndex, bufferWidth, bufferHeight);
        }
    }

    @Override
    public void updateAnimation()
    {
    	frameCounter = 0;

        if (fireAnimation != null )
        {
        	fireAnimation.copyRegularFireFrameToByteBuffer(frameBuffer, bufferPixelSize);
        }
    	
    	textureSheet.uploadByteBufferToGPU(originX, originY, frameBuffer, bufferWidth, bufferHeight);
    }
    
    @Override
    public boolean isProcedurallyAnimated()
    {
    	return true;
    }    
}
