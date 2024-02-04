// FCMOD

package btw.entity;

import net.minecraft.src.*;

public class InfiniteArrowEntity extends EntityArrow
	implements EntityWithCustomPacket
{
    static final private int VEHICLE_SPAWN_PACKET_TYPE = 100;
    
    public InfiniteArrowEntity(World world )
    {
        super(world);
    }
	
    public InfiniteArrowEntity(World world, double d, double d1, double d2 )
    {
        super( world, d, d1, d2 );
   }
    
    public InfiniteArrowEntity(World world, EntityLiving entityliving, float f )
    {
    	super( world, entityliving, f );
    	
    	canBePickedUp = 2;
    }
    
    public InfiniteArrowEntity(World world, EntityLiving firingEntity, EntityLiving targetEntity, float par4, float par5 )
    {
        super( world, firingEntity, targetEntity, par4, par5 );
    }
    
    @Override
	public Item getCorrespondingItem()
	{
		return null;
	}
    
    @Override
	public boolean canHopperCollect()
	{
		return false;
	}
    
    @Override
    public void onUpdate()
    {
        super.onUpdate();
        
        if ( !isDead && inGround )
        {
        	// infinite arrows are destroyed on impact
        	
            for (int i = 0; i < 32; i++)
            {
            	// spew gold particles
            	
                worldObj.spawnParticle( "iconcrack_266", posX, posY, posZ,
	        		(double)((float)(Math.random() * 2D - 1.0D) * 0.4F), 
	        		(double)((float)(Math.random() * 2D - 1.0D) * 0.4F), 
	        		(double)((float)(Math.random() * 2D - 1.0D) * 0.4F) );
            }
            
        	setDead();
        }
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