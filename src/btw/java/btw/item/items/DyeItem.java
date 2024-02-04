// FCMOD

package btw.item.items;

import btw.entity.mob.SheepEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class DyeItem extends ItemDye
{
    public DyeItem(int iItemID )
    {
        super( iItemID );
        
        setBellowsBlowDistance(2);
        
        setUnlocalizedName( "dyePowder" );
    }
    
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ )
    {
        if ( itemStack.getItemDamage() == 15 ) // bone meal
        {        	
        	if ( applyBoneMeal(world, i, j, k) )
        	{
                itemStack.stackSize--;
                
                return true;
        	}
        }

        return false;
    }

    @Override
    // client
    public boolean itemInteractionForEntity( ItemStack stack, EntityLiving entity )
    // server
    //public boolean useItemOnEntity( ItemStack stack, EntityLiving entity )
    {
        if ( entity instanceof SheepEntity)
        {
            SheepEntity sheep = (SheepEntity)entity;
            int i = BlockCloth.getBlockFromDye(stack.getItemDamage());

            if (!sheep.getSheared() && sheep.getFleeceColor() != i)
            {
            	sheep.setSuperficialFleeceColor(i);
            	
                stack.stackSize--;
            }

            return true;
        }
        
        return false;
    }
    
    @Override
    public int getFilterableProperties(ItemStack stack)
    {
    	if ( stack.getItemDamage() == 0 )
    	{
    		// ink sack
    		
    		return FILTERABLE_SMALL;
    	}
    	
		return FILTERABLE_FINE;
    }
    
    //------------- Class Specific Methods ------------//

    private boolean applyBoneMeal(World world, int i, int j, int k)
    {    	
        Block targetBlock = Block.blocksList[world.getBlockId( i, j, k )];
        
        if ( targetBlock != null && 
        	targetBlock.attemptToApplyFertilizerTo(world, i, j, k) )
        {
            return true;
        }
        
    	return false;
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon cocoaPowderIcon;

    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister par1IconRegister )
    {
    	super.registerIcons( par1IconRegister );

        cocoaPowderIcon = par1IconRegister.registerIcon("fcItemCocoaPowder");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamage( int iDamage )
    {
    	if ( iDamage == 3 ) // cocoa powder
    	{
    		return cocoaPowderIcon;
    	}
    	else
    	{
    		return super.getIconFromDamage( iDamage );
    	}
    }    
}