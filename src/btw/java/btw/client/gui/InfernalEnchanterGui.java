// FCMOD

package btw.client.gui;

import btw.inventory.container.InfernalEnchanterContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class InfernalEnchanterGui extends GuiContainer
{
	private static final int GUI_HEIGHT = 210;
	
    private static final int SCROLL_ICON_SCREEN_POS_X = 17;
    private static final int SCROLL_ICON_SCREEN_POS_Y = 37;
    
    private static final int SCROLL_ICON_BITMAP_POS_X = 176;
    private static final int SCROLL_ICON_BITMAP_POS_Y = 0;
    
    private static final int ITEM_ICON_SCREEN_POS_X = 17;
    private static final int ITEM_ICON_SCREEN_POS_Y = 75;
    
    private static final int ITEM_ICON_BITMAP_POS_X = 192;
    private static final int ITEM_ICON_BITMAP_POS_Y = 0;
    
    private static final int ENCHANTMENT_BUTTONS_POS_X = 60;
    private static final int ENCHANTMENT_BUTTONS_POS_Y = 17;
    private static final int ENCHANTMENT_BUTTONS_HEIGHT = 19;
    private static final int ENCHANTMENT_BUTTONS_WIDTH = 108;

    private static final int ENCHANTMENT_BUTTON_NORMAL_POS_X = 0;
    private static final int ENCHANTMENT_BUTTON_NORMAL_POS_Y = 211;

    private static final int ENCHANTMENT_BUTTON_INACTIVE_POS_X = 0;
    private static final int ENCHANTMENT_BUTTON_INACTIVE_POS_Y = 230;
    
    private static final int ENCHANTMENT_BUTTON_HIGHLIGHTED_POS_X = 108;
    private static final int ENCHANTMENT_BUTTON_HIGHLIGHTED_POS_Y = 211;
    
	private InfernalEnchanterContainer container;
	
    /*
     * world, i, j, & k are only relevant on the server
     */
    public InfernalEnchanterGui(InventoryPlayer playerInventory, World world, int i, int j, int k )
    {
        super( new InfernalEnchanterContainer( playerInventory, world, i, j, k ) );
        
        ySize = GUI_HEIGHT;

        container = (InfernalEnchanterContainer)inventorySlots;
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
    }

    @Override
    protected void mouseClicked( int iXClick, int iYClick, int par3)
    {
        super.mouseClicked( iXClick, iYClick, par3 );
        
        int iBitmapXOffset = (width - xSize) / 2;
        int iBitmapYOffset = (height - ySize) / 2;

        for (int iButtonIndex = 0; iButtonIndex < InfernalEnchanterContainer.MAX_ENCHANTMENT_POWER_LEVEL; iButtonIndex++)
        {
            int iRelativeXClick = iXClick - (iBitmapXOffset + ENCHANTMENT_BUTTONS_POS_X);
            int iRelativeYClick = iYClick - (iBitmapYOffset + ENCHANTMENT_BUTTONS_POS_Y + ENCHANTMENT_BUTTONS_HEIGHT * iButtonIndex );

            if ( iRelativeXClick >= 0 && iRelativeYClick >= 0 && iRelativeXClick < ENCHANTMENT_BUTTONS_WIDTH && iRelativeYClick <
                                                                                                                ENCHANTMENT_BUTTONS_HEIGHT)
            {
            	// Relay the click to the server, which will then handle the actual enchanting
        		mc.playerController.sendEnchantPacket(container.windowId, iButtonIndex);
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer( int i, int j )
    {
        fontRenderer.drawString( "Infernal Enchanter", 40, 5, 0x404040);
        
        fontRenderer.drawString( "Inventory", 8, ( ySize - 96 ) + 2, 0x404040 );
        
        // debug display for bookshelves

        /*
    	String bookshelfString = (new StringBuilder()).append( m_container.m_iMaxSurroundingBookshelfLevel).toString();

        fontRenderer.drawString( bookshelfString, 10, 10, 0xFF0000 );
        */
        
        // draw level numbers to the sides of the buttons
        
        for (int iTemp = 0; iTemp < container.MAX_ENCHANTMENT_POWER_LEVEL; iTemp++ )
        {
        	// get roman numeral equivalent of level
        	
        	String levelString = (new StringBuilder()).append(
        			StatCollector.translateToLocal((new StringBuilder()).append("enchantment.level.").append(iTemp + 1).toString())).toString();

            fontRenderer.drawString(levelString, ENCHANTMENT_BUTTONS_POS_X - 15, ENCHANTMENT_BUTTONS_POS_Y + (iTemp * ENCHANTMENT_BUTTONS_HEIGHT) + 6, 0x404040);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer( float par1, int iMouseX, int iMouseY )
    {
        RenderHelper.disableStandardItemLighting();
        
    	// draw background image
    	
        GL11.glColor4f( 1.0F, 1.0F, 1.0F, 1.0F );
        mc.renderEngine.bindTexture( "/btwmodtex/fcguiinfernal.png" );
        
        int xPos = ( width - xSize ) / 2;
        int yPos = ( height - ySize ) / 2;
        
        drawTexturedModalRect( xPos, yPos, 0, 0, xSize, ySize );
        
        mc.renderEngine.bindTexture( "/btwmodtex/fcguiinfernal.png" );
        
        // draw icons to indicate empty slot for scroll
        
        ItemStack scrollStack = container.tableInventory.getStackInSlot(0);
        
        if ( scrollStack == null)
        {
            drawTexturedModalRect(xPos + SCROLL_ICON_SCREEN_POS_X,                // screen x pos
                                  yPos + SCROLL_ICON_SCREEN_POS_Y,                            // screen y pos
                                  SCROLL_ICON_BITMAP_POS_X,                                    // bitmap source x
                                  SCROLL_ICON_BITMAP_POS_Y,                                    // bitmap source y
                                  16,                                                        // width
                                  16 );														// height
        }
        
        // draw icons to indicate empty slot for item
        
        ItemStack itemStack = container.tableInventory.getStackInSlot(1);
        
        if ( itemStack == null)
        {
            drawTexturedModalRect(xPos + ITEM_ICON_SCREEN_POS_X,                // screen x pos
                                  yPos + ITEM_ICON_SCREEN_POS_Y,                                // screen y pos
                                  ITEM_ICON_BITMAP_POS_X,                                        // bitmap source x
                                  ITEM_ICON_BITMAP_POS_Y,                                        // bitmap source y
                                  16,                                                        // width
                                  16 );														// height
        }
        
        // draw enchantment buttons
        
        EnchantmentNameParts.instance.setRandSeed(container.nameSeed);
        
        for (int iTempButton = 0; iTempButton < container.MAX_ENCHANTMENT_POWER_LEVEL; iTempButton ++ )
        {
            String enchantmentName = EnchantmentNameParts.instance.generateRandomEnchantName();
            
        	int iButtonEnchantmentLevel = container.currentEnchantmentLevels[iTempButton];
        	
        	boolean bPlayerCapable = iButtonEnchantmentLevel <= mc.thePlayer.experienceLevel && iButtonEnchantmentLevel <= container.maxSurroundingBookshelfLevel;
        	
            int iNameColor = 0x685e4a;
            int iLevelNumberColor = 0x80ff20;

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.renderEngine.bindTexture( "/btwmodtex/fcguiinfernal.png" );            
            
        	if ( iButtonEnchantmentLevel <= 0 || !bPlayerCapable  )
        	{
        		// disable the button
        		
	            drawTexturedModalRect(xPos + ENCHANTMENT_BUTTONS_POS_X, yPos + ENCHANTMENT_BUTTONS_POS_Y + (ENCHANTMENT_BUTTONS_HEIGHT * iTempButton ),
                                      ENCHANTMENT_BUTTON_INACTIVE_POS_X, ENCHANTMENT_BUTTON_INACTIVE_POS_Y, ENCHANTMENT_BUTTONS_WIDTH,
                                      ENCHANTMENT_BUTTONS_HEIGHT);
	            
	            iNameColor = (iNameColor & 0xfefefe) >> 1;
        		iLevelNumberColor = 0x407f10;
        	}
        	else
        	{
        		if ( isMouseOverEnchantmentButton(iTempButton, iMouseX, iMouseY) )
        		{
    	            drawTexturedModalRect(xPos + ENCHANTMENT_BUTTONS_POS_X, yPos + ENCHANTMENT_BUTTONS_POS_Y + (ENCHANTMENT_BUTTONS_HEIGHT * iTempButton ),
                                          ENCHANTMENT_BUTTON_HIGHLIGHTED_POS_X, ENCHANTMENT_BUTTON_HIGHLIGHTED_POS_Y, ENCHANTMENT_BUTTONS_WIDTH,
                                          ENCHANTMENT_BUTTONS_HEIGHT);
    	            
    	            iNameColor = 0xffff80;
        		}
        	}
        	
        	if ( iButtonEnchantmentLevel > 0 )
        	{
        		// fill in the enchantment details
        		
        		FontRenderer tempFontRenderer = mc.standardGalacticFontRenderer;
        		
        		tempFontRenderer.drawSplitString( enchantmentName, xPos + ENCHANTMENT_BUTTONS_POS_X + 2,
                                                  yPos + 2 + ENCHANTMENT_BUTTONS_POS_Y + (ENCHANTMENT_BUTTONS_HEIGHT * iTempButton ),
    				104, iNameColor );
        		
        		tempFontRenderer = mc.fontRenderer;
        		
        		String enchantmentLevelString = ( new StringBuilder() ).append( "" ).append( iButtonEnchantmentLevel ).toString();
        		
        		tempFontRenderer.drawStringWithShadow(enchantmentLevelString,
                                                      xPos + ENCHANTMENT_BUTTONS_POS_X + ENCHANTMENT_BUTTONS_WIDTH - 2 - tempFontRenderer.getStringWidth(enchantmentLevelString),
                                                      yPos + 9 + ENCHANTMENT_BUTTONS_POS_Y + (ENCHANTMENT_BUTTONS_HEIGHT * iTempButton ), iLevelNumberColor);
        	}
        }
    }
    
    private boolean isMouseOverEnchantmentButton(int iButtonIndex, int iMouseX, int iMouseY)
    {
        int iBackgroundXPos = ( width - xSize ) / 2;
        int iBackgroundYPos = ( height - ySize ) / 2;
        
        int iRelativeMouseX = iMouseX - iBackgroundXPos;
    	int iRelativeMouseY = iMouseY - iBackgroundYPos;
    	
    	int iButtonYPos = ENCHANTMENT_BUTTONS_POS_Y + (ENCHANTMENT_BUTTONS_HEIGHT * iButtonIndex );
    	
    	if (iRelativeMouseX >= ENCHANTMENT_BUTTONS_POS_X && iRelativeMouseX <= ENCHANTMENT_BUTTONS_POS_X + ENCHANTMENT_BUTTONS_WIDTH &&
			iRelativeMouseY >= iButtonYPos && iRelativeMouseY <= iButtonYPos + ENCHANTMENT_BUTTONS_HEIGHT)
    	{
    		return true;
    	}	
    	
    	return false;
    }
}