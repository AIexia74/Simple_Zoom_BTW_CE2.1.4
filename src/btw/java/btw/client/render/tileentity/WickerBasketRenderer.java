// FCMOD (client only)

package btw.client.render.tileentity;

import btw.block.BTWBlocks;
import btw.block.tileentity.WickerBasketTileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class WickerBasketRenderer extends BasketRenderer
{
    private final float storageItemMaxHeight = 0.25F;
    
    public void renderTileEntityAt(TileEntity tileEntity, double xCoord, double yCoord, double zCoord, float fPartialTickCount )
    {
    	super.renderTileEntityAt( tileEntity, xCoord, yCoord, zCoord, fPartialTickCount );
    	
    	WickerBasketTileEntity basket = (WickerBasketTileEntity)tileEntity;
    	
		renderStorageStack(basket, xCoord, yCoord, zCoord, fPartialTickCount);
    }
    
	//------------- Class Specific Methods ------------//
	
    private void renderStorageStack(WickerBasketTileEntity basket, double xCoord, double yCoord, double zCoord, float fPartialTickCount)
    {
    	ItemStack stack = basket.getStorageStack();
    	
    	if ( stack != null && basket.lidOpenRatio > 0.01F )
    	{
	    	int iMetadata = basket.worldObj.getBlockMetadata( basket.xCoord, basket.yCoord, basket.zCoord );
	    	
	        EntityItem entity = (EntityItem) EntityList.createEntityOfType(EntityItem.class, basket.worldObj, 0.0D, 0.0D, 0.0D, stack );
	        
	        entity.hoverStart = 0.0F;
	    	
	        GL11.glPushMatrix();
	        
	        float fCurrentItemHeight = storageItemMaxHeight * getCurrentOpenRatio(basket, fPartialTickCount);
	        	
	        GL11.glTranslatef( (float)xCoord + 0.5F, (float)yCoord + fCurrentItemHeight, (float)zCoord + 0.5F );        
	        
            // don't rotate items rendered as billboards (fancyGraphics test)
            
	        if ( RenderManager.instance.options.fancyGraphics )
	        {        	
	        	float fYaw = convertFacingToYaw(BTWBlocks.wickerBasket.getFacing(iMetadata));
	        	
	        	GL11.glRotatef( fYaw, 0.0F, 1.0F, 0.0F);
	        }
	
            
	        if (doesStackRenderAsBlock(stack) && stack.stackSize > 4 )
        	{
	        	// move large block stacks towards the front to avoid overlapping with lid
	        	
	        	GL11.glTranslatef( 0F, 0F, -0.15F );
        	}
	        
	        RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
	
	        GL11.glPopMatrix();
    	}
    }
    
    private float convertFacingToYaw(int iFacing)
    {
    	float fYaw = 0;
    	
    	if ( iFacing == 3 )
    	{
    		fYaw = 180F;
    	}
    	else if ( iFacing == 4 )
    	{
    		fYaw = 90F;
    	}
    	else if ( iFacing == 5 )
    	{
    		fYaw = 270F;
    	}
    	
    	return fYaw;
    }
    
    private boolean doesStackRenderAsBlock(ItemStack stack)
    {
        return stack.getItemSpriteNumber() == 0 && Block.blocksList[stack.itemID] != null && 
        	Block.blocksList[stack.itemID].doesItemRenderAsBlock(stack.getItemDamage());
    }
}
