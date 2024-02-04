// FCMOD

package btw.item.items;

import btw.crafting.util.FurnaceBurnTime;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class KnittingItem extends ProgressiveCraftingItem
{
    public KnittingItem(int iItemID )
    {
    	super( iItemID );
    	
        setBuoyant();
    	setfurnaceburntime(FurnaceBurnTime.SHAFT.burnTime +
                           ( 2 * FurnaceBurnTime.WOOL.burnTime));
    	
        setUnlocalizedName( "fcItemKnitting" );        
    }

    @Override
    protected void playCraftingFX(ItemStack stack, World world, EntityPlayer player)
    {
        player.playSound( "step.wood", 
        	0.25F + 0.25F * (float)world.rand.nextInt( 2 ), 
        	( world.rand.nextFloat() - world.rand.nextFloat() ) * 0.25F + 1.75F );
    }
    
    @Override
    public ItemStack onEaten( ItemStack stack, World world, EntityPlayer player )
    {
    	int iColorIndex = WoolItem.getClosestColorIndex(getColor(stack));
    	ItemStack woolStack = new ItemStack( BTWItems.woolKnit, 1, iColorIndex );
    	
        world.playSoundAtEntity( player, "step.cloth", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F );
        
		ItemUtils.givePlayerStackOrEject(player, woolStack);
		
        return new ItemStack( BTWItems.knittingNeedles);
    }
    
    @Override
    public boolean getCanBeFedDirectlyIntoCampfire(int iItemDamage)
    {
    	return false;
    }
    
    @Override
    public boolean getCanBeFedDirectlyIntoBrickOven(int iItemDamage)
    {
    	return false;
    }
    
    //------------- Class Specific Methods ------------//
    
    static public void setColor(ItemStack stack, int iColor)
    {
        NBTTagCompound tag = stack.getTagCompound();

        if ( tag == null )
        {
            tag = new NBTTagCompound();
            stack.setTagCompound( tag );
        }
        
        tag.setInteger( "fcColor", iColor );
        
    }
    
    static public int getColor(ItemStack stack)
    {
        NBTTagCompound tag = stack.getTagCompound();

        if ( tag != null )
        {
        	if ( tag.hasKey( "fcColor" ) )
        	{
        		return tag.getInteger( "fcColor" );
        	}
        }            
        
        return 0;
    }
    
	//------------ Client Side Functionality ----------//

    @Environment(EnvType.CLIENT)
    private Icon iconWool;

    @Override
    @Environment(EnvType.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColorFromItemStack( ItemStack stack, int iRenderPass )
    {
    	if ( iRenderPass == 0 )
    	{
    		return getColor(stack);
    	}
    	
    	return super.getColorFromItemStack( stack, iRenderPass );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
    	super.registerIcons( register );

        iconWool = register.registerIcon("fcItemKnitting_Wool");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamageForRenderPass( int iDamage, int iRenderPass )
    {
    	if ( iRenderPass == 0 )
    	{
    		return iconWool;
    	}
    	
        return itemIcon;
    }
}
