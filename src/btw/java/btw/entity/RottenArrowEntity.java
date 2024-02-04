// FCMOD

package btw.entity;

import btw.item.BTWItems;
import net.minecraft.src.*;

public class RottenArrowEntity extends EntityArrow
	implements EntityWithCustomPacket
{
	private static final float DAMAGE_MULTIPLIER = 1.0F;
	
    static final private int VEHICLE_SPAWN_PACKET_TYPE = 104;
    
    public RottenArrowEntity(World world )
    {
        super( world );
    }

    public RottenArrowEntity(World world, double d, double d1, double d2 )
    {
        super( world, d, d1, d2 );
    }

    public RottenArrowEntity(World world, EntityLiving entityLiving, float f )
    {
        super( world, entityLiving, f );
    	
    	canBePickedUp = 2;
    }

    public RottenArrowEntity(World world, EntityLiving firingEntity, EntityLiving targetEntity, float par4, float par5)
    {
        super( world, firingEntity, targetEntity, par4, par5 );
    }
    
    @Override
    protected float getDamageMultiplier()
    {
    	return DAMAGE_MULTIPLIER;
    }

    @Override
	protected boolean addArrowToPlayerInv(EntityPlayer player)
	{
		return false;
	}
    
    @Override
    public void onUpdate()
    {
        super.onUpdate();
        
        if ( !isDead && inGround )
        {
        	// rotten arrows are destroyed on impact
        	
            for (int i = 0; i < 32; i++)
            {
            	// spew boat particles
            	
                worldObj.spawnParticle("iconcrack_333", posX, posY, posZ,
	        		(double)((float)(Math.random() * 2D - 1.0D) * 0.4F), 
	        		(double)((float)(Math.random() * 2D - 1.0D) * 0.4F), 
	        		(double)((float)(Math.random() * 2D - 1.0D) * 0.4F) );
            }
            
        	setDead();
        }
    }

    @Override
	public Item getCorrespondingItem()
	{
		return BTWItems.rottenArrow;
	}
    
    @Override
	public boolean canHopperCollect()
	{
		return false;
	}    
    
    //************* FCIEntityPacketHandler ************//

    @Override
    public Packet getSpawnPacketForThisEntity()
    {
		return new Packet23VehicleSpawn(this, getVehicleSpawnPacketType(), shootingEntity == null ? entityId : shootingEntity.entityId );
    }
    
    @Override
    public int getTrackerViewDistance()
    {
    	return 64;
    }
    
    @Override
    public int getTrackerUpdateFrequency()
    {
    	return 20;
    }
    
    @Override
    public boolean getTrackMotion()
    {
    	return false;
    }
    
    @Override
    public boolean shouldServerTreatAsOversized()
    {
    	return false;
    }
    
    //************* Class Specific Methods ************//

    static public int getVehicleSpawnPacketType()
    {
    	return VEHICLE_SPAWN_PACKET_TYPE;
    }
}