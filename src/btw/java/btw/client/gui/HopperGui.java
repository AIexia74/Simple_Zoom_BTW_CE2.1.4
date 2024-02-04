//FCMOD

package btw.client.gui;

import btw.block.tileentity.HopperTileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import btw.inventory.container.HopperContainer;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.InventoryPlayer;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class HopperGui extends GuiContainer
{
	static final int HOPPER_GUI_HEIGHT = 193;
	static final int HOPPER_MACHINE_ICON_HEIGHT = 14;

    private HopperTileEntity associatedTileEntityHopper;

	public HopperGui(InventoryPlayer inventoryplayer, HopperTileEntity tileentityHopper )
    {
        super( new HopperContainer( inventoryplayer, tileentityHopper ) );
        
        ySize = HOPPER_GUI_HEIGHT;
        
        associatedTileEntityHopper = tileentityHopper;
    }

    @Override
    protected void drawGuiContainerForegroundLayer( int i, int j )
    {
        fontRenderer.drawString( "Hopper", 70, 6, 0x404040 );
        fontRenderer.drawString( "Inventory", 8, ( ySize - 96 ) + 2, 0x404040 );
    }

    @Override
    protected void drawGuiContainerBackgroundLayer( float f, int i, int j )
    {
    	// draw the background image
    	
        GL11.glColor4f( 1.0F, 1.0F, 1.0F, 1.0F );
        
        mc.renderEngine.bindTexture( "/btwmodtex/fcHopper.png" );
        
        int xPos = ( width - xSize ) / 2;
        int yPos = ( height - ySize ) / 2;
        
        drawTexturedModalRect( xPos, yPos, 0, 0, xSize, ySize );
        
        // draw the machine indicator

        if (associatedTileEntityHopper.mechanicalPowerIndicator > 0 )
        {
            drawTexturedModalRect( xPos + 80,									// screen x pos 
        		yPos + 18, 														// screen y pos
        		176, 															// bitmap source x
        		0, 																// bitmap source y
        		14, 															// width
                                   HOPPER_MACHINE_ICON_HEIGHT);										// height
        } 
    }    
}