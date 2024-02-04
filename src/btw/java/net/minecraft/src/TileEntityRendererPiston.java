package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class TileEntityRendererPiston extends TileEntitySpecialRenderer
{
    /** instance of RenderBlocks used to draw the piston base and extension. */
    private RenderBlocks blockRenderer;

    public void renderPiston(TileEntityPiston tePiston, double par2, double par4, double par6, float par8)
    {
        Block var9 = Block.blocksList[tePiston.getStoredBlockID()];

        if (var9 != null && tePiston.getProgress(par8) < 1.0F)
        {
            Tessellator var10 = Tessellator.instance;
            this.bindTextureByName("/terrain.png");
            RenderHelper.disableStandardItemLighting();
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_CULL_FACE);

            if (Minecraft.isAmbientOcclusionEnabled())
            {
                GL11.glShadeModel(GL11.GL_SMOOTH);
            }
            else
            {
                GL11.glShadeModel(GL11.GL_FLAT);
            }

            var10.startDrawingQuads();
            var10.setTranslation((double)((float)par2 - (float)tePiston.xCoord + tePiston.getOffsetX(par8)), (double)((float)par4 - (float)tePiston.yCoord + tePiston.getOffsetY(par8)), (double)((float)par6 - (float)tePiston.zCoord + tePiston.getOffsetZ(par8)));
            var10.setColorOpaque(1, 1, 1);

            if (var9 == Block.pistonExtension && tePiston.getProgress(par8) < 0.5F)
            {
                this.blockRenderer.renderPistonExtensionAllFaces(var9, tePiston.xCoord, tePiston.yCoord, tePiston.zCoord, false);
            }
            else if (tePiston.shouldRenderHead() && !tePiston.isExtending())
            {
                Block.pistonExtension.setHeadTexture(((BlockPistonBase)var9).getPistonExtensionTexture());
                this.blockRenderer.renderPistonExtensionAllFaces(Block.pistonExtension, tePiston.xCoord, tePiston.yCoord, tePiston.zCoord, tePiston.getProgress(par8) < 0.5F);
                Block.pistonExtension.clearHeadTexture();
                var10.setTranslation((double)((float)par2 - (float)tePiston.xCoord), (double)((float)par4 - (float)tePiston.yCoord), (double)((float)par6 - (float)tePiston.zCoord));
                this.blockRenderer.renderPistonBaseAllFaces(var9, tePiston.xCoord, tePiston.yCoord, tePiston.zCoord);
            }
            else
            {
            	// FCNOTE: I get the impression that under rare circumstances metadata can get out of sync
            	// resulting in blocks attempting to render with erroneous data, which
            	// would explain what's happening here: 
            	// http://www.sargunster.com/btwforum/viewtopic.php?f=7&t=9630
            	if ( tePiston.getBlockMetadata() ==
            		tePiston.worldObj.getBlockMetadata(
        			tePiston.xCoord,
        			tePiston.yCoord,
        			tePiston.zCoord ) )
    			{
	            	var9.currentBlockRenderer = blockRenderer;
	            	
	        		var9.renderBlockMovedByPiston(blockRenderer, tePiston.xCoord,
                            tePiston.yCoord, tePiston.zCoord);
                    
                    if(tePiston.cachedTileEntity != null && tileEntityRenderer.hasSpecialRenderer(tePiston.cachedTileEntity)) {
                        var10.draw();
                        
                        RenderHelper.enableStandardItemLighting();
                        
                        int var3 = tileEntityRenderer.worldObj.getLightBrightnessForSkyBlocks(tePiston.xCoord, tePiston.yCoord, tePiston.zCoord, 0);
                        int var4 = var3 % 65536;
                        int var5 = var3 / 65536;
                        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var4, (float)var5);
                    
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    
                        double renderX = par2  + tePiston.getOffsetX(par8);
                        double renderY = par4  + tePiston.getOffsetY(par8);
                        double renderZ = par6  + tePiston.getOffsetZ(par8);
                    
                        tileEntityRenderer.renderTileEntityAt(tePiston.cachedTileEntity, renderX, renderY, renderZ, 1.0f);
                    
                        var10.startDrawingQuads();
                    }
    			}
            }

            var10.setTranslation(0.0D, 0.0D, 0.0D);
            var10.draw();
            RenderHelper.enableStandardItemLighting();
        }
    }

    /**
     * Called when the ingame world being rendered changes (e.g. on world -> nether travel) due to using one renderer
     * per tile entity type, rather than instance
     */
    public void onWorldChange(World par1World)
    {
        this.blockRenderer = new RenderBlocks(par1World);
    }

    public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
    {
        this.renderPiston((TileEntityPiston)par1TileEntity, par2, par4, par6, par8);
    }
}
