// FCMOD

package btw.client.gui;

import btw.block.tileentity.CookingVesselTileEntity;
import btw.inventory.BTWContainers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import btw.inventory.container.CookingVesselContainer;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.InventoryPlayer;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class CookingVesselGui extends GuiContainer
{
	static final int GUI_HEIGHT = 193;
	static final int FIRE_ICON_HEIGHT = 12;

    private CookingVesselTileEntity associatedTileEntity;
    private int containerID;

	public CookingVesselGui(InventoryPlayer inventoryplayer, CookingVesselTileEntity tileEntity, int iContainerID )
    {
        super( new CookingVesselContainer( inventoryplayer, tileEntity ) );
        
        ySize = GUI_HEIGHT;

        associatedTileEntity = tileEntity;
        containerID = iContainerID;
    }

    @Override
    protected void drawGuiContainerForegroundLayer( int i, int j )
    {
    	if (containerID == BTWContainers.crucibleContainerID)
    	{
            fontRenderer.drawString( "Crucible", 66, 6, 0x404040 );    		
    	}
    	else
    	{
    		fontRenderer.drawString( "Cauldron", 66, 6, 0x404040 );
    	}
    	
        fontRenderer.drawString( "Inventory", 8, ( ySize - 96 ) + 2, 0x404040 );
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {
    	// draw the background image
    	
        GL11.glColor4f( 1.0F, 1.0F, 1.0F, 1.0F );
        
        mc.renderEngine.bindTexture( "/btwmodtex/fccauldron.png" );
        
        int xPos = ( width - xSize ) / 2;
        int yPos = ( height - ySize ) / 2;
        
        drawTexturedModalRect( xPos, yPos, 0, 0, xSize, ySize );
        
        // draw the cooking indicator
        
        if ( associatedTileEntity.isCooking() )
        {
            int scaledIconHeight = associatedTileEntity.getCookProgressScaled(FIRE_ICON_HEIGHT);
            
            drawTexturedModalRect( xPos + 81,                                    // screen x pos
                                   yPos + 19 + FIRE_ICON_HEIGHT - scaledIconHeight,    // screen y pos
                                   176,                                                        // bitmap source x
                                   FIRE_ICON_HEIGHT - scaledIconHeight,                // bitmap source y
                                   14,                                                        // width
            		scaledIconHeight + 2);										// height
        }        
    }    
}