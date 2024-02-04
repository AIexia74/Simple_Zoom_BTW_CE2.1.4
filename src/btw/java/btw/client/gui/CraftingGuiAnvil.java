// FCMOD (client only)

package btw.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.World;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class CraftingGuiAnvil extends CraftingGuiWorkbench
{
    public CraftingGuiAnvil(InventoryPlayer invPlayer, World world, int i, int j, int k )
    {
    	super( invPlayer, world, i, j, k );
    }

    @Override
    protected void drawGuiContainerBackgroundLayer( float par1, int par2, int par3 )
    {
        GL11.glColor4f( 1F, 1F, 1F, 1F);
        
        mc.renderEngine.bindTexture( "/btwmodtex/fcGuiAnvilVanilla.png" );
        
        int xPos = (width - xSize) / 2;
        int yPos = (height - ySize) / 2;
        
        drawTexturedModalRect( xPos, yPos, 0, 0, xSize, ySize);
    }
}
