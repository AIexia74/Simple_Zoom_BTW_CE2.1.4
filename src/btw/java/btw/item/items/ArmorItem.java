// FCMOD

package btw.item.items;

import net.minecraft.src.*;

public class ArmorItem extends ItemArmor
{
	private final int armorWeight;
	
    public ArmorItem(int iItemID, EnumArmorMaterial armorMaterial, int iRenderIndex, int iArmorType, int iWeight )
    {
    	super( iItemID, armorMaterial, iRenderIndex, iArmorType );

		armorWeight = iWeight;
    }
    
    @Override
    public int getWeightWhenWorn()
    {
    	return armorWeight;
    }
    
    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player )
    {
    	super.onCreated( stack, world, player );
    	
		if (player.timesCraftedThisTick == 0 && world.isRemote )
		{
			if ( getArmorMaterial() == EnumArmorMaterial.CLOTH )
			{
				player.playSound( "step.cloth", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F );
			}
			else
			{
				player.playSound( "random.anvil_use", 0.5F, world.rand.nextFloat() * 0.25F + 1.25F );
			}
		}		
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//
}
