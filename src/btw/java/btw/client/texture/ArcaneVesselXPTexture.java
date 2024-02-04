// FCMOD

package btw.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.MathHelper;
import net.minecraft.src.Texture;
import net.minecraft.src.TextureStitched;

import java.nio.ByteBuffer;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ArcaneVesselXPTexture extends TextureStitched
{
    ByteBuffer frameBuffer;
    
    float colorMultiplierRed;
    float colorMultiplierGreen;
    float colorMultiplierBlue;
    
    int bufferWidth;
    int bufferHeight;
    int bufferPixelSize;
    
    public ArcaneVesselXPTexture(String sName )
    {
        super( sName );
    }
    
    @Override
    public void init(Texture par1Texture, List par2List, int par3, int par4, int par5, int par6, boolean par7)
    {
    	super.init(par1Texture, par2List, par3, par4, par5, par6, par7);

        colorMultiplierRed = 0F;
        colorMultiplierGreen = 0F;
        colorMultiplierBlue = 0F;

        bufferWidth = ((Texture)this.textureList.get(0)).getWidth();
        bufferHeight = ((Texture)this.textureList.get(0)).getHeight();
        bufferPixelSize = bufferWidth * bufferHeight;

        frameBuffer = ByteBuffer.allocateDirect(bufferPixelSize * 4);
    }

    @Override
    public void updateAnimation()
    {
    	frameCounter++;
    	
    	float fRedAngle = (float)( frameCounter % 360 ) * (float)Math.PI / 180F;

        colorMultiplierRed = (MathHelper.sin(fRedAngle) * 0.5F + 0.5F ) * 0.75F + 0.25F;
    	
    	copyFrameToBufferWithColorMultiplier(frameBuffer, bufferPixelSize);

    	textureSheet.uploadByteBufferToGPU(originX, originY, frameBuffer, bufferWidth, bufferHeight);
    }
    
    private void copyFrameToBufferWithColorMultiplier(ByteBuffer m_frameBuffer, int m_iBufferPixelSize)
    {
    	ByteBuffer sourceBuffer = ((Texture)this.textureList.get(0)).getTextureData();
    	
        for ( int iPixelIndex = 0; iPixelIndex < m_iBufferPixelSize; iPixelIndex++ )
        {
        	int iSourceRed = sourceBuffer.get( iPixelIndex * 4 + 0 ) & 0xff;
        	int iSourceGreen = sourceBuffer.get( iPixelIndex * 4 + 1 ) & 0xff;
        	int iSourceBlue = sourceBuffer.get( iPixelIndex * 4 + 2 ) & 0xff;
        	
        	int iPixelRed = (int)(iSourceRed * colorMultiplierRed);
        	int iPixelGreen = (int)(iSourceGreen * colorMultiplierGreen);
        	int iPixelBlue = (int)(iSourceBlue * colorMultiplierBlue);
        	
        	int iPixelAlpha =  (int)sourceBuffer.get( iPixelIndex * 4 + 3 );
        	
        	if ( iPixelRed > 255 || iPixelGreen > 255 || iPixelBlue > 255 ||
        		iPixelRed < 0 || iPixelGreen < 0 || iPixelBlue < 0 )
        	{
        		boolean blah = true;
        	}
        	
            m_frameBuffer.put( iPixelIndex * 4 + 0, (byte)iPixelRed );
            m_frameBuffer.put( iPixelIndex * 4 + 1, (byte)iPixelGreen );
            m_frameBuffer.put( iPixelIndex * 4 + 2, (byte)iPixelBlue );
            m_frameBuffer.put( iPixelIndex * 4 + 3, (byte)iPixelAlpha );
        }    	
    }
    
    @Override
    public boolean isProcedurallyAnimated()
    {
    	return true;
    }    
}
