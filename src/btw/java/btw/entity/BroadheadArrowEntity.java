// FCMOD

package btw.entity;

import btw.item.BTWItems;
import net.minecraft.src.*;

public class BroadheadArrowEntity extends EntityArrow
	implements EntityWithCustomPacket
{
	private static final float BROADHEAD_DAMAGE_MULTIPLIER = 1.5F;
	
    static final private int VEHICLE_SPAWN_PACKET_TYPE = 101;
    
    public BroadheadArrowEntity(World world )
    {
        super( world );
    }

    public BroadheadArrowEntity(World world, double d, double d1, double d2 )
    {
        super( world, d, d1, d2 );
    }

    public BroadheadArrowEntity(World world, EntityLiving entityLiving, float f )
    {
        super( world, entityLiving, f );
    }

    @Override
    protected float getDamageMultiplier()
    {
    	return BROADHEAD_DAMAGE_MULTIPLIER;
    }

    @Override
	public Item getCorrespondingItem()
	{
		return BTWItems.broadheadArrow;
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