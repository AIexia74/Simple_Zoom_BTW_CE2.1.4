// FCMOD (client only)

package btw.client.gui;

import btw.client.render.util.RenderUtils;
import btw.inventory.container.WorkbenchContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class CraftingGuiWorkbench extends GuiContainer
{
	private WorkbenchContainer container;
	
    public CraftingGuiWorkbench(InventoryPlayer inventory, World world, int i, int j, int k )
    {
        super( new WorkbenchContainer( inventory, world, i, j, k ) );

        container = (WorkbenchContainer)inventorySlots;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.crafting"), 28, 6, 4210752);
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);

        drawSecondaryOutputIndicator();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture("/gui/crafting.png");
        int var4 = (this.width - this.xSize) / 2;
        int var5 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.ySize);
    }
    
    //------------- Class Specific Methods ------------//
    
    private void drawSecondaryOutputIndicator()
    {
        IRecipe recipe = CraftingManager.getInstance().findMatchingIRecipe(
                container.craftMatrix, mc.theWorld);
        
        if ( recipe != null && recipe.hasSecondaryOutput() )
        {
	        Slot outputSlot = (Slot) container.inventorySlots.get(0);
	        
	        int iDisplayX = outputSlot.xDisplayPosition + 24;
	        int iDisplayY = outputSlot.yDisplayPosition + 5;
	        
	        RenderUtils.drawSecondaryCraftingOutputIndicator(mc, iDisplayX, iDisplayY);
        }
    }    
}
