// FCMOD

package btw.item.items;

import btw.entity.BroadheadArrowEntity;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class CompositeBowItem extends BowItem
{
	private static int maxDamage =  576;
	
    public CompositeBowItem(int iItemID )
    {
        super( iItemID );
        
        setMaxDamage(maxDamage);
        
        setUnlocalizedName( "fcItemBowComposite" );
    }

    @Override
    public boolean canItemBeFiredAsArrow(int iItemID)
    {    	
    	return iItemID == Item.arrow.itemID || iItemID == BTWItems.rottenArrow.itemID || iItemID == BTWItems.broadheadArrow.itemID;
    }

    @Override
    public float getPullStrengthToArrowVelocityMultiplier()
    {
    	return 3.0F;
    }
    
    @Override
    protected EntityArrow createArrowEntityForItem(World world, EntityPlayer player, int iItemID, float fPullStrength)
	{
		if ( iItemID == BTWItems.broadheadArrow.itemID )
		{
			return (EntityArrow) EntityList.createEntityOfType(BroadheadArrowEntity.class, world, player, fPullStrength * getPullStrengthToArrowVelocityMultiplier());
		}
		else if ( iItemID == BTWItems.rottenArrow.itemID )
		{
	        world.playSoundAtEntity( player, "random.break", 0.8F, 0.8F + world.rand.nextFloat() * 0.4F);
	        
			if ( world.isRemote )
			{
		        float motionX = -MathHelper.sin((player.rotationYaw / 180F) * (float)Math.PI) * MathHelper.cos((player.rotationPitch / 180F) * (float)Math.PI) * fPullStrength;
		        float motionZ = MathHelper.cos((player.rotationYaw / 180F) * (float)Math.PI) * MathHelper.cos((player.rotationPitch / 180F) * (float)Math.PI) * fPullStrength;
		        float motionY = -MathHelper.sin((player.rotationPitch / 180F) * (float)Math.PI) * fPullStrength;
		        
		        for (int i = 0; i < 32; i++)
		        {
		        	// spew boat particles
		        	
		            world.spawnParticle( "iconcrack_333", player.posX, player.posY + player.getEyeHeight(), player.posZ, 
		        		motionX + (double)((float)(Math.random() * 2D - 1.0D) * 0.4F), 
		        		motionY + (double)((float)(Math.random() * 2D - 1.0D) * 0.4F), 
		        		motionZ + (double)((float)(Math.random() * 2D - 1.0D) * 0.4F) );
		        }
			}
			
			return null;
		}
		else
		{
			return super.createArrowEntityForItem(world, player, iItemID, fPullStrength);
		}
		
	}

    @Override
    protected void playerBowSound(World world, EntityPlayer player, float fPullStrength)
    {
    	world.playSoundAtEntity( player, "random.bow", 1.0F, 1.5F / (itemRand.nextFloat() * 0.4F + 1.2F) + fPullStrength * 0.5F );
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] drawIconArray = new Icon[3];

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        itemIcon = register.registerIcon( "fcItemBowComposite" );        
        
        drawIconArray[0] =  register.registerIcon( "fcItemBowComposite_pull_0" );
        drawIconArray[1] =  register.registerIcon( "fcItemBowComposite_pull_1" );
        drawIconArray[2] =  register.registerIcon( "fcItemBowComposite_pull_2" );        
    }

    @Environment(EnvType.CLIENT)
	public Icon getDrawIcon(int itemInUseDuration) {
		if (itemInUseDuration >= 18) {
			return drawIconArray[2];
		} else if (itemInUseDuration > 12) {
			return drawIconArray[1];
		} else if (itemInUseDuration > 0) {
			return drawIconArray[0];
		}
		return itemIcon;
	}
    
	@Override
	@Environment(EnvType.CLIENT)
	public Icon getAnimationIcon(EntityPlayer player) {
		ItemStack itemInUse = player.getItemInUse();

		if (itemInUse != null && itemInUse.itemID == this.itemID) {
			int timeInUse = itemInUse.getMaxItemUseDuration() - player.getItemInUseCount();

			return getDrawIcon(timeInUse);
		}
		else return itemIcon;
	}
}