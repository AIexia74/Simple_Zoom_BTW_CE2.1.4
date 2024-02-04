// FCMOD

package btw.client.gui;

import btw.client.render.util.RenderUtils;
import btw.inventory.container.SoulforgeContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class CraftingGuiSoulforge extends GuiContainer
{
	private SoulforgeContainer container;
	
	static final int M_I_GUI_HEIGHT = 184;
	
    /*
     * world, i, j, & k are only relevant on the server
     */
    public CraftingGuiSoulforge(InventoryPlayer inventoryplayer, World world, int i, int j, int k )
    {
        super( new SoulforgeContainer( inventoryplayer, world, i, j, k ) );

        container = (SoulforgeContainer)inventorySlots;
        
        ySize = M_I_GUI_HEIGHT;
    }

    @Override
    protected void drawGuiContainerForegroundLayer( int i, int j )
    {
        fontRenderer.drawString( "Soulforge", 22, 6, 0x404040 );
        fontRenderer.drawString( "Inventory", 8, (ySize - 96) + 2, 0x404040 );

        drawSecondaryOutputIndicator();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer( float f, int i, int j )
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture( "/btwmodtex/fcguianvil.png" );
        
        int xPos = (width - xSize) / 2;
        int yPos = (height - ySize) / 2;
        
        drawTexturedModalRect( xPos, yPos, 0, 0, xSize, ySize);
    }
    
    //------------- Class Specific Methods ------------//
    
    private void drawSecondaryOutputIndicator()
    {
        IRecipe recipe = CraftingManager.getInstance().findMatchingIRecipe(
                container.craftMatrix, mc.theWorld);
        
        if ( recipe != null && recipe.hasSecondaryOutput() )
        {
	        Slot outputSlot = (Slot) container.inventorySlots.get(0);
	        
	        int iDisplayX = outputSlot.xDisplayPosition + 26;
	        int iDisplayY = outputSlot.yDisplayPosition + 5;
	        
	        RenderUtils.drawSecondaryCraftingOutputIndicator(mc, iDisplayX, iDisplayY);
        }
    }    
}