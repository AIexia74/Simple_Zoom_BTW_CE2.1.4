// FCMOD: Client only

package btw.client.render.tileentity;

import btw.block.tileentity.OvenTileEntity;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class OvenRenderer extends TileEntitySpecialRenderer
{
	private static final double COOKING_ITEM_VISUAL_OFFSET = 0.25F;
	
    public void renderTileEntityAt(TileEntity tileEntity, double xCoord, double yCoord, double zCoord, float fPartialTickCount )
    {
    	OvenTileEntity furnace = (OvenTileEntity)tileEntity;
    	
    	ItemStack cookStack = furnace.getCookStack();
    	
    	if ( cookStack != null )
    	{
    		renderCookStack(furnace, xCoord, yCoord, zCoord, cookStack, fPartialTickCount);
    	}
    }
    
    private void renderCookStack(OvenTileEntity furnace, double xCoord, double yCoord, double zCoord, ItemStack stack, float fPartialTickCount)
    {
    	int iMetadata = furnace.worldObj.getBlockMetadata( furnace.xCoord, furnace.yCoord, furnace.zCoord );
    	
        EntityItem entityItem = (EntityItem) EntityList.createEntityOfType(EntityItem.class, furnace.worldObj, 0.0D, 0.0D, 0.0D, stack );
        entityItem.getEntityItem().stackSize = 1;
        entityItem.hoverStart = 0.0F;
    	
        GL11.glPushMatrix();
        GL11.glTranslatef( (float)xCoord + 0.5F, (float)yCoord + ( 6.5F / 16F ), (float)zCoord + 0.5F );        
        
    	GL11.glScalef( 0.7F, 0.7F, 0.7F );
        
    	int iFacing = iMetadata & 7;
		Vec3 vOffset = Vec3.createVectorHelper( 0.0D, 0.0D, 0.0D );
    	float fYaw = 0F;
    	
    	if ( iFacing == 2 )
    	{
    		fYaw = 0F;
    		vOffset.zCoord = -COOKING_ITEM_VISUAL_OFFSET;
    	}
    	else if ( iFacing == 3 )
    	{
    		fYaw = 180F;
    		vOffset.zCoord = COOKING_ITEM_VISUAL_OFFSET;
    	}
    	else if ( iFacing == 4 )
    	{
    		fYaw = 90F;
    		vOffset.xCoord = -COOKING_ITEM_VISUAL_OFFSET;
    	}
    	else if ( iFacing == 5 )
    	{
    		fYaw = 270F;
    		vOffset.xCoord = COOKING_ITEM_VISUAL_OFFSET;
    	}
        	
        // move the item towards the front
        
    	GL11.glTranslatef( (float)vOffset.xCoord, (float)vOffset.yCoord, 
    		(float)vOffset.zCoord );
    	
        if ( RenderManager.instance.options.fancyGraphics )
        {        	
            // don't rotate items rendered as billboards
            
        	GL11.glRotatef( fYaw, 0.0F, 1.0F, 0.0F);
        }
        
    	// furnaces get item lighting from block in front
    	
    	int iBrightness = getItemRenderBrightnessForBlockToFacing(furnace, iFacing);
    	
        int var11 = iBrightness % 65536;
        int var12 = iBrightness / 65536;
        
        OpenGlHelper.setLightmapTextureCoords( OpenGlHelper.lightmapTexUnit, (float)var11 / 1F, 
        	(float)var12 / 1F);
        
        GL11.glColor4f( 1F, 1F, 1F, 1F );
        
        RenderManager.instance.renderEntityWithPosYaw(entityItem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);

        GL11.glPopMatrix();        
    }

    protected int getItemRenderBrightnessForBlockToFacing(OvenTileEntity furnace,
														  int iFacing)
    {
    	BlockPos targetPos = new BlockPos(
    		furnace.xCoord, furnace.yCoord, furnace.zCoord, iFacing );
	
	    if ( furnace.worldObj.blockExists(targetPos.x, targetPos.y, targetPos.z) )
	    {
	        return furnace.worldObj.getLightBrightnessForSkyBlocks(
					targetPos.x, targetPos.y, targetPos.z, 0);
	    }
	    
        return 0;
    }
}
