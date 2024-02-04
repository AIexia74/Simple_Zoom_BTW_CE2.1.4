// FCMOD

package btw.client.gui;

import btw.block.tileentity.dispenser.BlockDispenserTileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import btw.inventory.container.BlockDispenserContainer;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.InventoryPlayer;
import org.lwjgl.opengl.GL11;

// copied wholesale from GuiDispenser with only slight modifications
@Environment(EnvType.CLIENT)
public class BlockDispenserGui extends GuiContainer
{
	static final int SELECTION_ICON_HEIGHT = 20;
	static final int GUI_HEIGHT = 182;

    private BlockDispenserTileEntity associatedTileEntityBlockDispenser;

    public BlockDispenserGui(InventoryPlayer inventoryplayer, BlockDispenserTileEntity tileentitydispenser )
    {
        super(new BlockDispenserContainer(inventoryplayer, tileentitydispenser));
        
        associatedTileEntityBlockDispenser = tileentitydispenser;
        
        ySize = GUI_HEIGHT;
    }

    @Override
    protected void drawGuiContainerForegroundLayer( int i, int j )
    {
        fontRenderer.drawString("Block Dispenser", 48, 6, 0x404040);
        fontRenderer.drawString("Inventory", 8, (ySize - 94) + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer( float f, int i, int j )
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture( "/btwmodtex/fcguiblockdisp.png" );
        
        int xPos = (width - xSize) / 2;
        int yPos = (height - ySize) / 2;
        drawTexturedModalRect( xPos, yPos, 0, 0, xSize, ySize);
        
        // draw the selection rectangle
        
        int iXOffset = (associatedTileEntityBlockDispenser.nextSlotIndexToDispense % 4 ) * 18;
        int iYOffset = (associatedTileEntityBlockDispenser.nextSlotIndexToDispense / 4 ) * 18;
        
        drawTexturedModalRect( xPos + 51 + iXOffset,                    // screen x pos
    		yPos + 15 + iYOffset,                                        // screen y pos
                               176,                                                        // bitmap source x
                               0,                                                            // bitmap source y
                               SELECTION_ICON_HEIGHT,                                        // width
                               SELECTION_ICON_HEIGHT);										// height
    }
}