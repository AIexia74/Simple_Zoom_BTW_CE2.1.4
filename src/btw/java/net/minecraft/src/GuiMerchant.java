package net.minecraft.src;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@Environment(EnvType.CLIENT)
public class GuiMerchant extends GuiContainer
{
    /** Instance of IMerchant interface. */
    private IMerchant theIMerchant;
    private GuiButtonMerchant nextRecipeButtonIndex;
    private GuiButtonMerchant previousRecipeButtonIndex;
    private int currentRecipeIndex = 0;
    private String field_94082_v;
    
    // FCMOD: Added
    private int currentNumValidRecipes = 0;
    private int validRecipesScrollOffset = 0;
    // END FCMOD

    public GuiMerchant(InventoryPlayer par1, IMerchant par2, World par3World, String par4)
    {
        super(new ContainerMerchant(par1, par2, par3World));
        this.theIMerchant = par2;
        this.field_94082_v = par4 != null && par4.length() >= 1 ? par4 : StatCollector.translateToLocal("entity.Villager.name");
        // FCMOD: Added
        ySize = 239;
        // END FCMOD
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        super.initGui();
        int var1 = (this.width - this.xSize) / 2;
        int var2 = (this.height - this.ySize) / 2;
        this.buttonList.add(this.nextRecipeButtonIndex = new GuiButtonMerchant(1, var1 + 144, var2 + 118, true));
        this.buttonList.add(this.previousRecipeButtonIndex = new GuiButtonMerchant(2, var1 + 34 - 14, var2 + 118, false));
        this.nextRecipeButtonIndex.drawButton = false;
        this.previousRecipeButtonIndex.drawButton = false;
        this.nextRecipeButtonIndex.enabled = false;
        this.previousRecipeButtonIndex.enabled = false;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRenderer.drawString(this.field_94082_v, this.xSize / 2 - this.fontRenderer.getStringWidth(this.field_94082_v) / 2, 6, 4210752);
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture( "/btwmodtex/fcguitrading.png" );
        int var4 = (this.width - this.xSize) / 2;
        int var5 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.ySize);
        MerchantRecipeList var6 = this.theIMerchant.getRecipes(this.mc.thePlayer);

        if (var6 != null && !var6.isEmpty())
        {
            int var7 = this.currentRecipeIndex;
            MerchantRecipe var8 = (MerchantRecipe)var6.get(var7);

            if (var8.func_82784_g())
            {
                mc.renderEngine.bindTexture( "/btwmodtex/fcguitrading.png" );
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_LIGHTING);
                this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 21, 212, 0, 28, 21);
                this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 51, 212, 0, 28, 21);
            }
        }
        
        // FCMOD: Added
        drawXPDisplay();
        // END FCMOD
    }

    /**
     * Gets the Instance of IMerchant interface.
     */
    public IMerchant getIMerchant()
    {
        return this.theIMerchant;
    }
    
    // FCMOD: Added    
    public void drawScreen( int iMouseX, int iMouseY, float fMysteryVariable )
    {
        super.drawScreen( iMouseX, iMouseY, fMysteryVariable );
        
        MerchantRecipeList recipeList = theIMerchant.getRecipes( mc.thePlayer );
    	ContainerMerchant associatedContainer = getAssociatedContainerMerchant();
    	
        if ( recipeList != null && !recipeList.isEmpty() && associatedContainer != null )
        {
            int iGuiX = (this.width - this.xSize) / 2;
            int iGuiY = (this.height - this.ySize) / 2;
            
            GL11.glPushMatrix();
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            
            int iNumRecipes = recipeList.size();
            
            if ( iNumRecipes > 8 )
            {
            	// gui can display a max of 8 recipes
            	
            	iNumRecipes = 8;
            }
            
            ItemStack tooltipStack = null;
            
            int iOffsetY = 18;            
            
            for ( int iTempRecipeIndex = 0; iTempRecipeIndex < iNumRecipes; iTempRecipeIndex++ )
            {
                int iOffsetX = 8;
                
            	if ( iTempRecipeIndex % 2 == 1 )
            	{
                    iOffsetX = 8 + ( 18 * 5 );
            	}
            	/*
            	else if ( iTempRecipeIndex == iNumRecipes - 1 )
            	{
            		// last item in an odd row, so center it
            		
                    iOffsetX = 8 + ( ( 18 * 5 ) - ( 9 + 36 ) );
            	}
            	*/
            	
	            MerchantRecipe tempRecipe = (MerchantRecipe)recipeList.get( iTempRecipeIndex );
	            
	            GL11.glEnable(GL11.GL_LIGHTING);
	            
	            ItemStack inputStack1 = tempRecipe.getItemToBuy();
	            ItemStack inputStack2 = tempRecipe.getSecondItemToBuy();
	            ItemStack outputStack = tempRecipe.getItemToSell();
	            
	            itemRenderer.zLevel = 100.0F;
	            
	            itemRenderer.renderItemAndEffectIntoGUI( fontRenderer, mc.renderEngine, inputStack1, iGuiX + iOffsetX, iGuiY + iOffsetY );
	            itemRenderer.renderItemOverlayIntoGUI( fontRenderer, mc.renderEngine, inputStack1, iGuiX + iOffsetX, iGuiY + iOffsetY );
	
	            if (inputStack2 != null)
	            {
	                itemRenderer.renderItemAndEffectIntoGUI( fontRenderer, mc.renderEngine, inputStack2, iGuiX + iOffsetX + 18, iGuiY + iOffsetY );
	                itemRenderer.renderItemOverlayIntoGUI( fontRenderer, mc.renderEngine, inputStack2, iGuiX + iOffsetX + 18, iGuiY + iOffsetY );
	            }
	
	            itemRenderer.renderItemAndEffectIntoGUI( fontRenderer, mc.renderEngine, outputStack, iGuiX + iOffsetX + 54, iGuiY + iOffsetY );
	            itemRenderer.renderItemOverlayIntoGUI( fontRenderer, mc.renderEngine, outputStack, iGuiX + iOffsetX + 54, iGuiY + iOffsetY );
	            
	            itemRenderer.zLevel = 0.0F;
	            
	            GL11.glDisable(GL11.GL_LIGHTING);
	
	            // draw arrow between input and output on active trades
	            
	            mc.renderEngine.bindTexture( "/btwmodtex/fcguitrading.png" );
	            
	            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	            
	            if ( !tempRecipe.func_82784_g() ) // whether the recipe is expired
	            {
	                drawTexturedModalRect( iGuiX + iOffsetX + 36, iGuiY + iOffsetY, 176, 38, 16, 16 );
	                
	        		String sXPString = null;
	    			
	        		if (tempRecipe.tradeLevel < 0 )
	        		{
	        			if (-( tempRecipe.tradeLevel) == associatedContainer.associatedVillagerTradeLevel)
	        			{
	        				sXPString = "++";
	        			}
	        		}
	        		else if (tempRecipe.tradeLevel == associatedContainer.associatedVillagerTradeLevel && tempRecipe.tradeLevel < 5 && !tempRecipe.isMandatory())
	        		{
	        			sXPString = "+";
	        		}
	        		
	        		if ( sXPString != null )
	        		{
	                    int iLevelScreenX = iGuiX + iOffsetX + 45 - ( mc.fontRenderer.getStringWidth( sXPString ) / 2 );            
	                    int iLevelScreenY = iGuiY + iOffsetY + 5;
	                    
	                    // black (0) outline of text
	                    
	                    mc.fontRenderer.drawString( sXPString, iLevelScreenX + 1, iLevelScreenY, 0 );            
	                    mc.fontRenderer.drawString( sXPString, iLevelScreenX - 1, iLevelScreenY, 0 );
	                    
	                    mc.fontRenderer.drawString( sXPString, iLevelScreenX, iLevelScreenY + 1, 0 );
	                    mc.fontRenderer.drawString( sXPString, iLevelScreenX, iLevelScreenY - 1, 0 );
	                    
	                    // text itself in the same color as vanilla xp display (8453920)
	                    
	                    mc.fontRenderer.drawString( sXPString, iLevelScreenX, iLevelScreenY, 8453920 );            
	        		}
	            }
	            else
	            {	                
	                drawTexturedModalRect( iGuiX + iOffsetX + 36, iGuiY + iOffsetY, 191, 38, 16, 16 );
	            }
                
	            if ( isPointInRegion( iOffsetX, iOffsetY, 16, 16, iMouseX, iMouseY ) )
	            {
	            	tooltipStack = inputStack1;
	            }
	            else if (inputStack2 != null && this.isPointInRegion( iOffsetX + 18, iOffsetY, 16, 16, iMouseX, iMouseY))
	            {
	            	tooltipStack = inputStack2;
	            }
	            else if ( isPointInRegion( iOffsetX + 53, iOffsetY, 16, 16, iMouseX, iMouseY ) )
	            {
	            	tooltipStack = outputStack;
	            }	
	            
	            if ( iTempRecipeIndex % 2 == 1 )
	            {
	            	iOffsetY += 18;
	            }
            }
            
            if ( tooltipStack != null )
            {
            	// have to do this at the end so that recipes don't overlap it
            	
                drawItemStackTooltip( tooltipStack, iMouseX, iMouseY );
            }
            
            GL11.glPopMatrix();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
        }
    }
    
    ContainerMerchant getAssociatedContainerMerchant()
    {
    	if ( inventorySlots != null && inventorySlots instanceof ContainerMerchant )
    	{
    		return (ContainerMerchant)inventorySlots;
    	}
    	
    	return null;
    }
    
    private void drawXPDisplay()
    {
    	ContainerMerchant associatedContainer = getAssociatedContainerMerchant();
    	
    	if ( associatedContainer != null && associatedContainer.associatedVillagerTradeLevel > 0 )
    	{
            mc.renderEngine.bindTexture( "/btwmodtex/fcguitrading.png" );
            
	        int xPos = ( width - xSize ) / 2;
	        int yPos = ( height - ySize ) / 2;

	        if (associatedContainer.associatedVillagerTradeMaxXP > 0 )
	        {
	        	int iXPBarIconWidth = 151;
	        	float fXPBarScale = ((float)associatedContainer.associatedVillagerTradeXP / (float)associatedContainer.associatedVillagerTradeMaxXP);
	        	
		        if (associatedContainer.associatedVillagerTradeLevel >= 5 )
		        {
		        	fXPBarScale = 1F;
		        }
		        
		        int iScaledIconWidth = (int)( fXPBarScale * (float)iXPBarIconWidth );		        

		        if ( iScaledIconWidth > 0 )
		        {
			        drawTexturedModalRect( xPos + 12,									// screen x pos 
			        		yPos + 99, 													// screen y pos
			        		0, 															// bitmap source x
			        		251, 														// bitmap source y
			        		iScaledIconWidth,											// width
			        		5 );														// height
		        }
	        }
	        
	        // draw the experience level
	        
            String sLevelString = "" + associatedContainer.associatedVillagerTradeLevel;
            
	        if (associatedContainer.associatedVillagerTradeLevel >= 5 )
	        {
	        	sLevelString = "Max";
	        }
	        
            int iLevelScreenX = xPos + 88 - ( mc.fontRenderer.getStringWidth( sLevelString ) / 2 );            
            int iLevelScreenY = yPos + 93;
            
            // black (0) outline of text
            
            mc.fontRenderer.drawString( sLevelString, iLevelScreenX + 1, iLevelScreenY, 0 );            
            mc.fontRenderer.drawString( sLevelString, iLevelScreenX - 1, iLevelScreenY, 0 );
            
            mc.fontRenderer.drawString( sLevelString, iLevelScreenX, iLevelScreenY + 1, 0 );
            mc.fontRenderer.drawString( sLevelString, iLevelScreenX, iLevelScreenY - 1, 0 );
            
            // text itself in the same color as vanilla xp display (8453920)
            
            mc.fontRenderer.drawString( sLevelString, iLevelScreenX, iLevelScreenY, 8453920 );
    	}
    }
    
	private int isEmeraldOnlyBuyTrade(MerchantRecipe recipe)
	{
		// returns a value greater than zero (the cost in emeralds) if this is a straight purchase trade
		
		if ( recipe.getSecondItemToBuy() == null )
		{
			ItemStack firstItem = recipe.getItemToBuy();
			
			if ( firstItem != null && firstItem.itemID == Item.emerald.itemID )
			{
				return firstItem.stackSize;
			}
		}
		
		return 0;
	}
	
    public void updateScreen()
    {
    	super.updateScreen();
    	
    	int iOldCurrentRecipe = currentRecipeIndex;
    	
        MerchantRecipeList recipeList = theIMerchant.getRecipes( mc.thePlayer );
    	ContainerMerchant associatedContainer = getAssociatedContainerMerchant();

		currentNumValidRecipes = 0;
    	
        if ( recipeList != null && !recipeList.isEmpty() && associatedContainer != null )
        {    
        	InventoryMerchant merchantInventory = associatedContainer.getMerchantInventory();
        	
            ItemStack playerStack1 = merchantInventory.getStackInSlot( 0 );
            ItemStack playerStack2 = merchantInventory.getStackInSlot( 1 );
            
            if ( playerStack1 != null || playerStack2 != null )
            {
	            int iNumRecipes = recipeList.size();
	            
	            for ( int iTempRecipeIndex = 0; iTempRecipeIndex < iNumRecipes; iTempRecipeIndex++ )
	            {
	            	if ( ( playerStack1 != null && recipeList.canRecipeBeUsed( playerStack1, playerStack2, iTempRecipeIndex ) != null ) ||
	            		( playerStack2 != null && recipeList.canRecipeBeUsed( playerStack2, playerStack1, iTempRecipeIndex ) != null ) )
	            	{
            		    currentNumValidRecipes++;
            		    
			            MerchantRecipe tempRecipe = (MerchantRecipe)recipeList.get( iTempRecipeIndex );
			            
	            		int iTempEmeraldTrade = isEmeraldOnlyBuyTrade(tempRecipe);
	            		
	            		if ( iTempEmeraldTrade > 0 )
	            		{
	            			// we keep scanning through these until we find the highest value one the player's offer can purchase
	            			
	            			if (currentNumValidRecipes <= validRecipesScrollOffset + 1 )
	            			{
	            				currentRecipeIndex = iTempRecipeIndex;
	            			}
	            		}
	            		else
	            		{
							validRecipesScrollOffset = 0;
	            		    
		            		currentRecipeIndex = iTempRecipeIndex;
		            		
		            		break;
	            		}
	            	}
	            }
	            
	            if ( iOldCurrentRecipe != currentRecipeIndex )
	            {
	            	associatedContainer.setCurrentRecipeIndex(currentRecipeIndex);
	                ByteArrayOutputStream var3 = new ByteArrayOutputStream();
	                DataOutputStream var4 = new DataOutputStream(var3);
	
	                try
	                {
	                    var4.writeInt(this.currentRecipeIndex);
	                    this.mc.getNetHandler().addToSendQueue(new Packet250CustomPayload("MC|TrSel", var3.toByteArray()));
	                }
	                catch (Exception var6)
	                {
	                    var6.printStackTrace();
	                }
	            }
            }
        }
        
        if (currentNumValidRecipes <= 0 || validRecipesScrollOffset >= currentNumValidRecipes)
        {
			validRecipesScrollOffset = 0;
        }
        
        if (currentNumValidRecipes > 1 )
        {
        	nextRecipeButtonIndex.drawButton = true;
        	previousRecipeButtonIndex.drawButton = true;
        	
	        nextRecipeButtonIndex.enabled = validRecipesScrollOffset < currentNumValidRecipes - 1;
	        previousRecipeButtonIndex.enabled = validRecipesScrollOffset > 0;
        }
        else
        {
        	nextRecipeButtonIndex.drawButton = false;
        	previousRecipeButtonIndex.drawButton = false;
        	
	        nextRecipeButtonIndex.enabled = false;
	        previousRecipeButtonIndex.enabled = false;
        }
    }
    
    protected void actionPerformed( GuiButton button )
    {
        if ( button == this.nextRecipeButtonIndex )
        {
        	if (validRecipesScrollOffset < currentNumValidRecipes - 1 )
        	{
        		validRecipesScrollOffset++;
        	}
        }
        else if ( button == this.previousRecipeButtonIndex )
        {
        	if (validRecipesScrollOffset > 0 )
        	{
        		validRecipesScrollOffset--;
        	}
        }
    }
    // END FCMOD
}