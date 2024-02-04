//FCMOD

package btw.client.gui;

import btw.block.tileentity.PulleyTileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import btw.inventory.container.PulleyContainer;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.InventoryPlayer;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class PulleyGui extends GuiContainer
{
	static final int PULLEY_GUI_HEIGHT = 174;
	static final int PULLEY_MACHINE_ICON_WIDTH = 14;
	static final int PULLEY_MACHINE_ICON_HEIGHT = 14;

    private PulleyTileEntity associatedTileEntityPulley;

	public PulleyGui(InventoryPlayer inventoryplayer, PulleyTileEntity tileEntityPulley )
    {
        super( new PulleyContainer( inventoryplayer, tileEntityPulley ) );
        
        ySize = PULLEY_GUI_HEIGHT;
        
        associatedTileEntityPulley = tileEntityPulley;
    }

    @Override
    protected void drawGuiContainerForegroundLayer( int i, int j )
    {
        fontRenderer.drawString( "Pulley", 75, 6, 0x404040 );
        fontRenderer.drawString( "Inventory", 8, ( ySize - 96 ) + 2, 0x404040 );
    }

    @Override
    protected void drawGuiContainerBackgroundLayer( float f, int i, int j )
    {
    	// draw the background image
    	
        GL11.glColor4f( 1.0F, 1.0F, 1.0F, 1.0F );
        
        mc.renderEngine.bindTexture( "/btwmodtex/fcguipulley.png" );
        
        int xPos = ( width - xSize ) / 2;
        int yPos = ( height - ySize ) / 2;
        
        drawTexturedModalRect( xPos, yPos, 0, 0, xSize, ySize );
        
        // draw the machine indicator
        
        if (associatedTileEntityPulley.mechanicalPowerIndicator > 0 )
        {
            drawTexturedModalRect( xPos + 80,                                    // screen x pos
            		yPos + 18,                                                    // screen y pos
                                   176,                                                        // bitmap source x
                                   0,                                                            // bitmap source y
                                   PULLEY_MACHINE_ICON_WIDTH,                                    // width
                                   PULLEY_MACHINE_ICON_HEIGHT);									// height
        }        
    }    
}