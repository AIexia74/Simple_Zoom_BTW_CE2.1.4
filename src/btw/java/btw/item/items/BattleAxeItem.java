// FCMOD

package btw.item.items;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

public class BattleAxeItem extends AxeItem
{
    private final int weaponDamage;
    
    public BattleAxeItem(int i)
    {
        super( i, EnumToolMaterial.SOULFORGED_STEEL );

        weaponDamage = 4 + toolMaterial.getDamageVsEntity();
        
        setUnlocalizedName( "fcItemAxeBattle" );        
    }

    @Override
    public EnumAction getItemUseAction(ItemStack itemstack)
    {
        return EnumAction.block;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack itemstack)
    {
        return 72000;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer)
    {
    	if (!entityplayer.isUsingSpecialKey())
    	{
            entityplayer.setItemInUse( itemstack, getMaxItemUseDuration( itemstack ) );
    	}  
        return itemstack;
    }

    @Override
    public int getDamageVsEntity( Entity entity )
    {
        return weaponDamage;
    }

    @Override
    public boolean hitEntity( ItemStack stack, EntityLiving defendingEntity, EntityLiving attackingEntity )
    {
        stack.damageItem( 1, attackingEntity );
        
        return true;
    }

    @Override
    public float getStrVsBlock( ItemStack stack, World world, Block block, int i, int j, int k)
    {
        if ( block.blockID == Block.web.blockID || block.blockID == BTWBlocks.web.blockID )
    	{
    		return 15F;
    	}
    	
    	return super.getStrVsBlock( stack, world, block, i, j, k );
    }

    @Override
    public boolean canHarvestBlock( ItemStack stack, World world, Block block, int i, int j, int k )
    {
        if ( block.blockID == Block.web.blockID || block.blockID == BTWBlocks.web.blockID )
        {
        	return true;
        }
        
    	return super.canHarvestBlock( stack, world, block, i, j, k );
    }

    @Override
    public boolean isEfficientVsBlock(ItemStack stack, World world, Block block, int i, int j, int k)
    {
        if ( block.blockID == Block.web.blockID || block.blockID == BTWBlocks.web.blockID )
    	{
    		return true;
    	}
    	
    	return super.isEfficientVsBlock(stack, world, block, i, j, k);
    }    
    
    @Override
    public boolean isEnchantmentApplicable(Enchantment enchantment)
    {
    	if ( enchantment.type == EnumEnchantmentType.weapon )
    	{
    		return true;
    	}
    	
    	return super.isEnchantmentApplicable(enchantment);
    }

    @Override
    public boolean getCanBePlacedAsBlock()
    {
    	return true;
    }    
    
    //------------- Class Specific Methods ------------//
    
	//------------ Client Side Functionality ----------//
}