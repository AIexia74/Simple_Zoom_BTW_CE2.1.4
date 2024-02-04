// FCMOD

package btw.entity.mob;

import btw.client.fx.BTWEffectManager;
import btw.entity.LightningBoltEntity;
import btw.entity.mob.behavior.CreeperSwellBehavior;
import btw.entity.mob.behavior.SimpleWanderBehavior;
import btw.item.BTWItems;
import btw.item.items.ShearsItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class CreeperEntity extends EntityCreeper
{
    private static final int NEUTERED_STATE_DATA_WATCHER_ID = 25;

    private boolean determinedToExplode = false;

    public CreeperEntity(World world )
    {
    	super( world );
    	
        tasks.removeAllTasksOfClass(EntityAICreeperSwell.class);
        tasks.removeAllTasksOfClass(EntityAIWander.class);
        
        tasks.addTask( 2, new CreeperSwellBehavior( this ) );
        tasks.addTask( 5, new SimpleWanderBehavior( this, 0.2F ) );
    }
    
    @Override
    protected void fall(float par1)
    {
    	// Parent function skipped to get rid of decreased fuse time when falling on the player
    	
        entityLivingFall(par1);
    }
    
    @Override
    protected void entityInit()
    {
        super.entityInit();
        
        dataWatcher.addObject(NEUTERED_STATE_DATA_WATCHER_ID, new Byte((byte)0 ));
    }
    
    @Override
    public void writeEntityToNBT( NBTTagCompound tag )
    {
        super.writeEntityToNBT( tag );

        tag.setByte( "fcNeuteredState", (byte) getNeuteredState());
    }

    @Override
    public void readEntityFromNBT( NBTTagCompound tag )
    {
        super.readEntityFromNBT(tag);
        
        if ( tag.hasKey( "fcNeuteredState" ) )
        {
        	setNeuteredState((int)tag.getByte("fcNeuteredState"));
        }        
    }
    
    @Override
    public void onDeath( DamageSource source )
    {
    	if (getNeuteredState() == 0 )
    	{
    		super.onDeath(source);
    	}
    	else
    	{
    		// neutered creepers skip over parent method so as not to drop records
    		
    		entityLivingOnDeath(source);
    	}
    }

    @Override
    protected int getDropItemId()
    {
        return BTWItems.nitre.itemID;
    }
    
    @Override
    protected void dropHead()
    {
        entityDropItem(new ItemStack( Item.skull.itemID, 1, 4 ), 0.0F);
    }
    
    @Override
    protected void dropFewItems( boolean bKilledByPlayer, int iFortuneModifier )
    {
        super.dropFewItems( bKilledByPlayer, iFortuneModifier );

        if (getNeuteredState() == 0 )
        {
	        if ( ( rand.nextInt( 3 ) == 0 || rand.nextInt( 1 + iFortuneModifier ) > 0 ) )
	        {
	            dropItem( BTWItems.creeperOysters.itemID, 1 );
	        }
        }
    }
    
    @Override
    public boolean interact( EntityPlayer player )
    {
        ItemStack playersCurrentItem = player.inventory.getCurrentItem();

        if ( playersCurrentItem != null && playersCurrentItem.getItem() instanceof ShearsItem && getNeuteredState() == 0 )
        {
            if ( !worldObj.isRemote )
            {
                setNeuteredState(1);
                
                EntityItem oysterItem = entityDropItem( new ItemStack( BTWItems.creeperOysters, 1 ), 0.25F);
                
                oysterItem.motionY += (double)(rand.nextFloat() * 0.025F);
                oysterItem.motionX += (double)((rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
                oysterItem.motionZ += (double)((rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
                
                int i = MathHelper.floor_double( posX );
                int j = MathHelper.floor_double( posY );
                int k = MathHelper.floor_double( posZ );
                
		        worldObj.playAuxSFX( BTWEffectManager.CREEPER_SNIP_EFFECT_ID, i, j, k, 0 );
            }

            playersCurrentItem.damageItem( 10, player );

            if ( playersCurrentItem.stackSize <= 0 )
            {
            	player.inventory.mainInventory[player.inventory.currentItem] = null;
            }            
            
            return true;
        }

        return super.interact(player);
    }

    @Override
    public int getTalkInterval()
    {
        return 120;
    }
    
    @Override
    public void playLivingSound()
    {
		if (getNeuteredState() > 0 )
		{
	        String var1 = this.getLivingSound();
	
	        if (var1 != null)
	        {
	            this.playSound(var1, 0.25F, this.getSoundPitch() + 0.25F);
	        }
		}
		else
		{
			super.playLivingSound();
		}
    }
    
    @Override
    protected String getLivingSound()
    {
		if (getNeuteredState() > 0 )
		{
			return "mob.creeper.say";
		}
		
		return super.getLivingSound();
    }
    
    @Override
    public void onKickedByCow(CowEntity cow)
	{
        determinedToExplode = true;
	}
    
    @Override
    public void checkForScrollDrop()
    {    	
    	if ( rand.nextInt( 1000 ) == 0 )
    	{
            ItemStack itemstack = new ItemStack( BTWItems.arcaneScroll, 1, Enchantment.blastProtection.effectId );
            
            entityDropItem(itemstack, 0.0F);
    	}
    }
    
    @Override
    public void onStruckByLightning(LightningBoltEntity entityBolt)
    {
        // create charged creeper
    	// intentionally not calling super method so that creeper isn't damaged by strike.
        
        dataWatcher.updateObject( 17, Byte.valueOf( (byte)1 ) );
    }
    
    //------------- Class Specific Methods ------------//
	
    public boolean getIsDeterminedToExplode()
    {
    	return determinedToExplode;
    }
    
    public int getNeuteredState()
    {
        return (int)( dataWatcher.getWatchableObjectByte(NEUTERED_STATE_DATA_WATCHER_ID) );
    }
    
    public void setNeuteredState(int iNeuteredState)
    {
        dataWatcher.updateObject(NEUTERED_STATE_DATA_WATCHER_ID, Byte.valueOf((byte)iNeuteredState));
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public String getTexture()
    {
		if (getNeuteredState() > 0 )
		{
			return "/btwmodtex/fcCreeperDepressed.png";
		}
		
		return super.getTexture();		
    }
}