// FCMOD

package btw.entity.mob;

import btw.entity.InfiniteArrowEntity;
import btw.entity.RottenArrowEntity;
import btw.entity.mob.behavior.SkeletonArrowAttackBehavior;
import btw.entity.mob.behavior.SimpleWanderBehavior;
import btw.item.BTWItems;
import net.minecraft.src.*;

public class SkeletonEntity extends EntitySkeleton
{
    private SkeletonArrowAttackBehavior aiRangedAttack;
    private EntityAIAttackOnCollide aiMeleeAttack;
    
    private static final float ENCHANTMENT_PROBABILITY = (0.05F / 4F );
    private static final float ARMOR_ENCHANTMENT_PROBABILITY = 0.05F;
    private static final float PICK_UP_LOOT_PROBABILITY = 0.15F;
    
    public SkeletonEntity(World world )
    {
        super( world );
        
        tasks.removeAllTasksOfClass(EntityAIWander.class);
        
        tasks.addTask( 5, new SimpleWanderBehavior( this, moveSpeed ) );
    }
    
    @Override
    protected void entityInit()
    {
        super.entityInit();
        
        // must initialize these AI variables here as they're referred to in the 
        // super constructor when setCombatTask() is called

        aiRangedAttack = new SkeletonArrowAttackBehavior(this, 0.25F, 60, 15F );
        aiMeleeAttack = new EntityAIAttackOnCollide(this, EntityPlayer.class, 0.31F, false );
    }
    
    @Override
    public void preInitCreature()
    {
        if ( worldObj.provider.dimensionId == -1 && getRNG().nextInt( 5 ) > 0 )
        {
            setSkeletonType( 1 );
        }
    }
    
    @Override
    public void initCreature()
    {
        if ( getSkeletonType() == 1 )
        {
            setCurrentItemOrArmor( 0, new ItemStack( Item.swordStone ) );
        }
        else
        {
            addRandomArmor();
            func_82162_bC();
        }

        setCombatTask();
        
        setCanPickUpLoot(rand.nextFloat() < PICK_UP_LOOT_PROBABILITY);
    }
    
    @Override
    public void spawnerInitCreature()
    {
    	// spawner skeletons have no bows, and can not be wither skels
    	
        setCombatTask();
        
        setCanPickUpLoot(rand.nextFloat() < PICK_UP_LOOT_PROBABILITY);
        
        func_82162_bC();
    }
    
    @Override
    public void onLivingUpdate()
    {
		checkForCatchFireInSun();

        if ( worldObj.isRemote && getSkeletonType() == 1 )
        {
            setSize( 0.72F, 2.34F );
        }

        entityMobOnLivingUpdate();
    }
    
    @Override
    protected int getDropItemId()
    {
    	// this is not actually used since dropFewItems() sets its own skeleton drops,
    	// but overriding parent arrow drop for clarity
    	
        return 0;
    }
    
    @Override
    protected void dropFewItems( boolean bKilledByPlayer, int iLootingModifier )
    {
    	if ( getSkeletonType() == 0 && worldObj.provider.dimensionId != -1 )
        {
        	if ( getHeldItem() != null && getHeldItem().itemID == Item.bow.itemID )
        	{
	            int iNumArrows = rand.nextInt( 3 + iLootingModifier );
	
	            for ( int iTempCount = 0; iTempCount < iNumArrows; iTempCount++ )
	            {
            		dropItem( BTWItems.rottenArrow.itemID, 1 );
	            }
        	}
        }

        int iNumBones = rand.nextInt( 3 + iLootingModifier );

        for ( int iTempCount = 0; iTempCount < iNumBones; iTempCount++ )
        {
            dropItem( Item.bone.itemID, 1 );
        }
    }

    @Override
    protected void dropRareDrop( int iBonusDrop )
    {
    	// override wither skull drop in parent
    }
    
    @Override
    public void attackEntityWithRangedAttack( EntityLiving target, float fDamageModifier )
    {
    	EntityArrow arrow = null;
    	
    	if ( worldObj.provider.dimensionId == -1 ) // the nether
    	{
    		arrow = (EntityArrow) EntityList.createEntityOfType(InfiniteArrowEntity.class, worldObj, this, target, 1.6F, 12F );
    	}
    	else
    	{
    		arrow = (EntityArrow) EntityList.createEntityOfType(RottenArrowEntity.class, worldObj, this, target, 1.6F, 12F );
    	}
    	
        int iPowerLevel = EnchantmentHelper.getEnchantmentLevel(
        	Enchantment.power.effectId, getHeldItem() );
        
        if ( iPowerLevel > 0 )
        {
            arrow.setDamage( arrow.getDamage() + (double)iPowerLevel * 0.5D + 0.5D );
        }

        int iPunchLevel = EnchantmentHelper.getEnchantmentLevel(
        	Enchantment.punch.effectId, getHeldItem() );
        
        if ( iPunchLevel > 0 )
        {
            arrow.setKnockbackStrength( iPunchLevel );
        }

        int iFlameLevel = EnchantmentHelper.getEnchantmentLevel(
        	Enchantment.flame.effectId, getHeldItem() );

        if ( iFlameLevel > 0 || getSkeletonType() == 1 || 
        	( isBurning() && rand.nextFloat() < 0.3F ) )
        {
            arrow.setFire( 100 );
        }

        playSound( "random.bow", 1F, 1F / ( getRNG().nextFloat() * 0.4F + 0.8F ) );
        
        worldObj.spawnEntityInWorld( arrow );
    }
    
    @Override
    public void setCombatTask()
    {
        tasks.removeTask(aiMeleeAttack);
        tasks.removeTask(aiRangedAttack);
        
        ItemStack heldStack = getHeldItem();

        if ( heldStack != null && heldStack.itemID == Item.bow.itemID )
        {
            tasks.addTask(4, aiRangedAttack);
        }
        else
        {
            tasks.addTask(4, aiMeleeAttack);
        }
    }

    @Override
    protected boolean isValidLightLevel()
    {
    	if ( worldObj.provider.dimensionId == -1 )
    	{
            return true;
    	}
    	
    	return super.isValidLightLevel();
    }
    
    @Override
    public void checkForScrollDrop()
    {    	
    	if ( getSkeletonType() == 0 )
    	{
	    	if ( rand.nextInt( 1000 ) == 0 )
	    	{
	    		ItemStack itemstack = null;
	    		
	    		if ( worldObj.provider.dimensionId == -1 ) // the nether
	    		{
	    			itemstack = new ItemStack( BTWItems.arcaneScroll, 1, Enchantment.infinity.effectId );
	    		}
	    		else
	    		{
	    			itemstack = new ItemStack( BTWItems.arcaneScroll, 1, Enchantment.projectileProtection.effectId );
	    		}
	            
	            entityDropItem(itemstack, 0.0F);
	    	}
    	}
    }

    @Override
    protected void dropHead()
    {
        if ( getSkeletonType() == 1 )
        {
            entityDropItem( new ItemStack(Item.skull.itemID, 1, 1 ), 0.0F );
        }
        else
        {
            entityDropItem( new ItemStack( Item.skull.itemID, 1, 0 ), 0.0F );
        }
    }
    
    @Override
    protected void func_82162_bC()
    {
    	// function adds random enchantments to equipment. Overriding to reduce 
    	// spawn frequency of magic bows, increase their drop rate, and unify 
    	// enchant power accross difficulty  
    	
        if ( getHeldItem() != null && rand.nextFloat() < ENCHANTMENT_PROBABILITY)
        {
            EnchantmentHelper.addRandomEnchantment( rand, getHeldItem(), 7 * rand.nextInt(6));
            
            equipmentDropChances[0] = 0.99F;
        }

        for (int var1 = 0; var1 < 4; ++var1)
        {
            ItemStack var2 = this.getCurrentArmor(var1);

            if ( var2 != null && rand.nextFloat() < ARMOR_ENCHANTMENT_PROBABILITY)
            {
                EnchantmentHelper.addRandomEnchantment( rand, var2, 7 * rand.nextInt( 6 ) );
            }
        }        
    }
    
    @Override
    public boolean attackEntityFrom( DamageSource damageSource, int iDamageAmount )
    {
    	// prevent skeletons taking cactus damage
    	
    	if ( damageSource != DamageSource.cactus )
    	{
    		return super.attackEntityFrom( damageSource, iDamageAmount );
    	}
    	
    	return false;
    }
}