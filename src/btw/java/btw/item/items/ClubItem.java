// FCMOD

package btw.item.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class ClubItem extends Item
{
    private int weaponDamage;

    public static final int WEAPON_DAMAGE_BONE = 4;
    public static final int DURABILITY_BONE = 20;
    
    public ClubItem(int iItemID, int iDurability, int iWeaponDamage, String sName )
    {
        super( iItemID );
        
        maxStackSize = 1;
        
        setMaxDamage( iDurability );

        weaponDamage = iWeaponDamage;
        
        setBuoyant();
        setIncineratedInCrucible();
        
        setUnlocalizedName( sName );
        
        setCreativeTab( CreativeTabs.tabCombat );
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLiving defendingEntity, EntityLiving attackingEntity )
    {
        stack.damageItem( 1, attackingEntity );
        
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, int iBlockID, int i, int j, int k, EntityLiving usingEntity )
    {
        if ( Block.blocksList[iBlockID].getBlockHardness( world, i, j, k ) > 0F )
        {
            stack.damageItem( 2, usingEntity );
        }

        return true;
    }

    @Override
    public int getDamageVsEntity(Entity par1Entity)
    {
        return weaponDamage;
    }

    @Override
    public void onCreated( ItemStack stack, World world, EntityPlayer player ) 
    {
		if (player.timesCraftedThisTick == 0 && world.isRemote )
		{
			player.playSound( "mob.zombie.woodbreak", 0.1F, 1.25F + ( world.rand.nextFloat() * 0.25F ) );
		}
		
    	super.onCreated( stack, world, player );
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }
}
