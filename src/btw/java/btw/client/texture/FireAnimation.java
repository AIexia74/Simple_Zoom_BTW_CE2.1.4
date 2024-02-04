// FCMOD

package btw.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.nio.ByteBuffer;

@Environment(EnvType.CLIENT)
public class FireAnimation
{
	public static FireAnimation[] instanceArray = new FireAnimation[] {null, null };
	
	public int width;
	public int height;
	
	public int textureHeight;
	
	public int size;
	
    protected float previousIntensities[];
    protected float currentIntensities[];
    
    private float intensityDecayFactor;
    private float intensityDecayFactorTop;
    private double distanceFromCenterIntensityModifier;
    private int centerRow;

    public static final float COLOR_SHIFT_SEPARATOR_BLUE_TO_WHITE = 0.39F;
    public static final float COLOR_SHIFT_SEPARATOR_WHITE_TO_RED = 0.66F;

    public static final float INVISIBLE_PIXEL_THRESHOLD_TOP = 0.87F;
    public static final float INVISIBLE_PIXEL_THRESHOLD_BOTTOM = 0.001F;
    
	public FireAnimation(int iInstanceIndex, int iTextureWidth, int iTextureHeight )
	{
        width = iTextureWidth;
        height = iTextureHeight * 2;

        textureHeight = iTextureHeight;

        size = width * height;

        previousIntensities = new float[size];
        currentIntensities = new float[size];
        
        for (int iTempIndex = 0; iTempIndex < size; iTempIndex++ )
        {
            previousIntensities[iTempIndex] = 0F;
            currentIntensities[iTempIndex] = 0F;
        }

        intensityDecayFactor = 1F + (0.08F * (16F / (float) textureHeight) );
        intensityDecayFactorTop = 1F + (0.07F * (16F / (float) textureHeight) );
        distanceFromCenterIntensityModifier = 0.123D * (16D / (double) width);

        centerRow = width / 2;

        instanceArray[iInstanceIndex] = this;
	}
	
    public void update()
    {
    	driftFireUpwards();
    	
    	generateNewBottomRow();

        previousIntensities = currentIntensities;
    }
    
    private void generateNewBottomRow()
    {
        for (int i = 0; i < width; i++ )
        {
        	double dDistFromCenter = (double)(centerRow - Math.abs(i - (centerRow - 1 )) );
        	
        	double dBaseIntensity = ( Math.random() * Math.random() * Math.random() * 4D ) + 
        		( Math.random() * 0.10000000149011612D ) + 0.2D;
        	
        	double dDistanceFromCenterModifier = dDistFromCenter * distanceFromCenterIntensityModifier;
        	
        	dDistanceFromCenterModifier = ( dDistanceFromCenterModifier * dDistanceFromCenterModifier );

            currentIntensities[i + ((height - 1 ) * width)] =
            	(float)( dBaseIntensity + dDistanceFromCenterModifier );
       }        
    }
    
    private void driftFireUpwards()
    {
    	// copy all rows except the bottom from the one below, and slowly decrease intensity
    	
        for (int i = 0; i < width; i++ )
        {
            for (int j = 0; j < height - 1; j++ )
            {
                int iTotalWeight = 18; // primary pixel weight
                
                float fNewIntensity = previousIntensities[i + (j + 1 ) * width] * (float)iTotalWeight;

                // modify the intensity by the intensity of the pixels around this one, using a weighted average
                
                for ( int iTempI = i - 1; iTempI <= i + 1; iTempI++ )
                {
                    for ( int iTempJ = j; iTempJ <= j + 1; iTempJ++ )
                    {
                        if ( iTempI >= 0 && iTempJ >= 0 && iTempI < width && iTempJ < height)
                        {
                            fNewIntensity += previousIntensities[iTempI + iTempJ * width];
                        }

                        iTotalWeight++;
                    }
                }

                if (j < textureHeight)
                {
                	fNewIntensity /= ((float)iTotalWeight * intensityDecayFactorTop);

                }
                else
                {
                	fNewIntensity /= ((float)iTotalWeight * intensityDecayFactor);
                }

                currentIntensities[i + j * width] = fNewIntensity;
            }
        }
    }
    
    public void copyRegularFireFrameToByteBuffer(ByteBuffer m_frameBuffer, int m_iBufferPixelSize)
    {
        for ( int iPixelIndex = 0; iPixelIndex < m_iBufferPixelSize; iPixelIndex++ )
        {
            float iPixelIntensity = currentIntensities[iPixelIndex];

            if ( iPixelIntensity > 1.0F )
            {
                iPixelIntensity = 1.0F;
            }
            else if ( iPixelIntensity < 0.0F )
            {
                iPixelIntensity = 0.0F;
            }

            float fColorMultiplier = 1.0F -  iPixelIntensity;
            
            int iRed = 0;
            int iGreen = 0;
            int iBlue = 0;
            
            char cAlpha = '\377';
            
            if (fColorMultiplier > INVISIBLE_PIXEL_THRESHOLD_TOP || fColorMultiplier < INVISIBLE_PIXEL_THRESHOLD_BOTTOM)
            {
                cAlpha = '\0';
            }
            else if (fColorMultiplier < COLOR_SHIFT_SEPARATOR_BLUE_TO_WHITE)
            {
            	float fFactor = fColorMultiplier / COLOR_SHIFT_SEPARATOR_BLUE_TO_WHITE;
            	
            	float fFactorSquared = fFactor * fFactor;
            	
	            iRed = (int)( fFactorSquared * 255F );
	            iGreen = (int)( fFactorSquared * 255F );	            
	            iBlue = (int)( fFactor * 100F ) + 155;            
            }
            else if (fColorMultiplier < COLOR_SHIFT_SEPARATOR_WHITE_TO_RED)
            {
            	iRed = 255;
            	iGreen = 255;
            	iBlue = 255;
            }
            else
            {   
	            float fDelta = 1.0F - ((fColorMultiplier - COLOR_SHIFT_SEPARATOR_WHITE_TO_RED) /
                                       (1.0F - COLOR_SHIFT_SEPARATOR_WHITE_TO_RED) );

	            iRed = (int)( fDelta * 120F ) + 135;
	            
	            float fDeltaSquared = fDelta * fDelta;
	            
	            iGreen = (int)( fDeltaSquared * 225F ) + 30;
	            
	            float fBlueMultiplier = fDeltaSquared * fDeltaSquared;
	            fBlueMultiplier *= fBlueMultiplier;
	            
	            iBlue = (int)( fBlueMultiplier * 255F );            
            }
            
            m_frameBuffer.put( iPixelIndex * 4 + 0, (byte)iRed );
            m_frameBuffer.put( iPixelIndex * 4 + 1, (byte)iGreen );
            m_frameBuffer.put( iPixelIndex * 4 + 2, (byte)iBlue );
            m_frameBuffer.put( iPixelIndex * 4 + 3, (byte)cAlpha );
        }
    }
    
    public void copyStokedFireFrameToByteBuffer(ByteBuffer m_frameBuffer, int m_iBufferPixelSize)
    {
        for ( int iPixelIndex = 0; iPixelIndex < m_iBufferPixelSize; iPixelIndex++ )
        {
            float iPixelIntensity = currentIntensities[iPixelIndex + (textureHeight * width)];

            if ( iPixelIntensity > 1.0F )
            {
                iPixelIntensity = 1.0F;
            }
            else if ( iPixelIntensity < 0.0F )
            {
                iPixelIntensity = 0.0F;
            }

            float fColorMultiplier = 1.0F -  iPixelIntensity;
            
            int iRed = 0;
            int iGreen = 0;
            int iBlue = 0;
            
            char cAlpha = '\377';
            
            if (fColorMultiplier > INVISIBLE_PIXEL_THRESHOLD_TOP || fColorMultiplier < INVISIBLE_PIXEL_THRESHOLD_BOTTOM)
            {
                cAlpha = '\0';
            }
            else if (fColorMultiplier < COLOR_SHIFT_SEPARATOR_BLUE_TO_WHITE)
            {
            	float fFactor = fColorMultiplier / COLOR_SHIFT_SEPARATOR_BLUE_TO_WHITE;
            	
            	float fFactorSquared = fFactor * fFactor;
            	
	            iRed = (int)( fFactorSquared * 255F );
	            iGreen = (int)( fFactorSquared * 255F );	            
	            iBlue = (int)( fFactor * 100F ) + 155;            
            }
            else if (fColorMultiplier < COLOR_SHIFT_SEPARATOR_WHITE_TO_RED)
            {
            	iRed = 255;
            	iGreen = 255;
            	iBlue = 255;
            }
            else
            {   
	            float fDelta = 1.0F - ((fColorMultiplier - COLOR_SHIFT_SEPARATOR_WHITE_TO_RED) /
                                       (1.0F - COLOR_SHIFT_SEPARATOR_WHITE_TO_RED) );

	            iRed = (int)( fDelta * 120F ) + 135;
	            
	            float fDeltaSquared = fDelta * fDelta;
	            
	            iGreen = (int)( fDeltaSquared * 225F ) + 30;
	            
	            float fBlueMultiplier = fDeltaSquared * fDeltaSquared;
	            fBlueMultiplier *= fBlueMultiplier;
	            
	            iBlue = (int)( fBlueMultiplier * 255F );            
            }
            
            m_frameBuffer.put( iPixelIndex * 4 + 0, (byte)iRed );
            m_frameBuffer.put( iPixelIndex * 4 + 1, (byte)iGreen );
            m_frameBuffer.put( iPixelIndex * 4 + 2, (byte)iBlue );
            m_frameBuffer.put( iPixelIndex * 4 + 3, (byte)cAlpha );
        }
    }
    
    static public void updateInstances()
    {
    	for ( int iTempIndex = 0; iTempIndex < 2; iTempIndex++ )
    	{
    		if (instanceArray[iTempIndex] != null )
    		{
    			instanceArray[iTempIndex].update();
    		}
    	}
    }    
}