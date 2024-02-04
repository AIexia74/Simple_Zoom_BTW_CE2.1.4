//FCMOD

package btw.client.gui;

import btw.inventory.container.HamperContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class HamperGui extends GuiContainer
{
	private static final int HAMPER_GUI_HEIGHT = 149;

    private IInventory hamperInventory;

	public HamperGui(InventoryPlayer playerInventory, IInventory hamperInventory )
    {
        super( new HamperContainer( playerInventory, hamperInventory ) );
        
        ySize = HAMPER_GUI_HEIGHT;

        this.hamperInventory = hamperInventory;
    }

    @Override
    protected void drawGuiContainerForegroundLayer( int i, int j )
    {
        String windowName = StatCollector.translateToLocal(hamperInventory.getInvName());
        
        fontRenderer.drawString( windowName, xSize / 2 - fontRenderer.getStringWidth( windowName ) / 2, 6, 0x404040 );
        
        fontRenderer.drawString( StatCollector.translateToLocal( "container.inventory" ), 8, ( ySize - 96 ) + 2, 0x404040 );
    }

    @Override
    protected void drawGuiContainerBackgroundLayer( float f, int i, int j )
    {
    	// draw the background image
    	
        GL11.glColor4f( 1.0F, 1.0F, 1.0F, 1.0F );
        
        mc.renderEngine.bindTexture( "/btwmodtex/fcGuiInv4.png" );
        
        int xPos = ( width - xSize ) / 2;
        int yPos = ( height - ySize ) / 2;
        
        drawTexturedModalRect( xPos, yPos, 0, 0, xSize, ySize );        
    }    
}