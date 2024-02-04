// FCMOD

package btw.entity.mob;

import btw.client.fx.BTWEffectManager;
import btw.entity.mob.behavior.SimpleWanderBehavior;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Iterator;
import java.util.List;

public class OcelotEntity extends EntityOcelot
{
	private static final int TABBY_SKIN_ID = 22;
	
    public OcelotEntity(World world )
    {
        super( world );
        
        tasks.removeAllTasksOfClass(EntityAIWander.class);
        
        targetTasks.removeAllTasksOfClass(EntityAITargetNonTamed.class);
        
        tasks.addTask( 10, new SimpleWanderBehavior( this, 0.23F ) );
        
        targetTasks.addTask( 1, new EntityAITargetNonTamed( this, 
        	ChickenEntity.class, 14F, 750, false ) );
    }
    
    @Override
    public boolean interact( EntityPlayer player )
    {
    	boolean bWasTamed = isTamed();
    	
    	boolean bReturnValue = super.interact( player );
    	
    	if ( !worldObj.isRemote && !bWasTamed && isTamed() )
    	{
            if ( worldObj.rand.nextInt( 4 ) == 0 )
            {
                setTameSkin(TABBY_SKIN_ID); // tabby
            }
    	}
    	
    	return bReturnValue;
    }
    
    @Override
    public void checkForLooseFood()
    {    
	    if ( !worldObj.isRemote && isEntityAlive() )
	    {
	    	boolean bAte = false;
	    	
	        List itemList = worldObj.getEntitiesWithinAABB( EntityItem.class, 
	        	boundingBox.expand( 2.5D, 1D, 2.5D ) );
	        
	        Iterator itemIterator = itemList.iterator();
	
	        while ( itemIterator.hasNext() )
	        {
	    		EntityItem itemEntity = (EntityItem)itemIterator.next();
	    		
		        if ( itemEntity.delayBeforeCanPickup == 0 && !itemEntity.isDead )
		        {
		        	ItemStack itemStack = itemEntity.getEntityItem();
		        	
		        	Item item = itemStack.getItem();
		        	
		        	if ( item.itemID == Item.chickenRaw.itemID || 
		        		item.itemID == Item.fishRaw.itemID )
		        	{
			            itemEntity.setDead();
			            
			            bAte = true;				            
		        	}
		        }
	        }
	        
	        if ( bAte )
	        {
	        	worldObj.playAuxSFX( BTWEffectManager.BURP_SOUND_EFFECT_ID,
	        		MathHelper.floor_double( posX ), MathHelper.floor_double( posY ), 
	        		MathHelper.floor_double( posZ ), 0 );
	        }
	    }
    }
    
    @Override
	public void onNearbyAnimalAttacked(EntityAnimal attackedAnimal, EntityLiving attackSource)
	{
    	// wolves and ocelots don't give a shit if a nearby animal is attacked
	}
    
    @Override
	public void onNearbyPlayerStartles(EntityPlayer player)
	{
    	// ignore fire start and block place/remove attempts
	}
    
	@Override
    public boolean isAffectedByMovementModifiers()
    {
    	return false;
    }	
    
	@Override
    public void initCreature()
    {
        if ( worldObj.rand.nextInt( 7 ) == 0 )
        {
        	// spawn a couple of kittens nearby, as parent class does
        	
            for ( int iTempCount = 0; iTempCount < 2; ++iTempCount )
            {
                OcelotEntity kitten = (OcelotEntity) EntityList.createEntityOfType(OcelotEntity.class, worldObj);
                
                kitten.setLocationAndAngles( posX, posY, posZ, rotationYaw, 0F );
                kitten.setGrowingAge( -kitten.getTicksForChildToGrow());
                
                worldObj.spawnEntityInWorld( kitten );
            }
        }
    }
	
	@Override
    public OcelotEntity spawnBabyAnimal(EntityAgeable otherParent )
    {
        OcelotEntity kitten = (OcelotEntity) EntityList.createEntityOfType(OcelotEntity.class, worldObj);

        if ( isTamed() )
        {
            kitten.setOwner( getOwnerName() );
            kitten.setTamed( true );
            kitten.setTameSkin( getTameSkin() );
        }

        return kitten;
    }
    
    @Override
    public int getItemFoodValue(ItemStack stack)
    {
    	// class processes its own food values, so this prevents EntityAnimal from treating
    	// it like a herbivore.
    	
    	return 0;
    }
    
    @Override
    public boolean isTooHungryToHeal()
    {
    	// handles own healing independent of hunger
    	
    	return true; 
    }
    
    @Override
    public int getMeleeAttackStrength(Entity target)
    {
    	return 3;
    }
    
    @Override
    public boolean attackEntityAsMob( Entity target )
    {
        return meleeAttack(target); // skip over parent
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public String getTexture()
    {
    	if (getTameSkin() == TABBY_SKIN_ID)
    	{
        	return "/btwmodtex/cat_tabby.png";
    	}
    	
    	return super.getTexture();
    }
}