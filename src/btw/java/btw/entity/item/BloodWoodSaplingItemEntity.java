// FCMOD

package btw.entity.item;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticVegetationBlock;
import btw.block.blocks.PlanterBlock;
import btw.network.packet.BTWPacketManager;
import btw.entity.EntityWithCustomPacket;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class BloodWoodSaplingItemEntity extends EntityItem
	implements EntityWithCustomPacket
{
    public BloodWoodSaplingItemEntity(World world, double dPosX, double dPosY, double dPosZ, ItemStack itemStack )
    {
    	super( world, dPosX, dPosY, dPosZ, itemStack );
    	
        isImmuneToFire = true;
    }

    public BloodWoodSaplingItemEntity(World world )
    {
        super( world );
        
        isImmuneToFire = true;
    }
    
    public void onUpdate()
    {
    	super.onUpdate();
    	
    	if ( !isDead && !worldObj.isRemote )
    	{
            if ( onGround )
            {
            	int i = MathHelper.floor_double( posX );
            	int iBlockBelowJ = MathHelper.floor_double( boundingBox.minY - 0.1F );
            	int k = MathHelper.floor_double( posZ );
            	
            	int iBlockBelowID = worldObj.getBlockId( i, iBlockBelowJ, k );            	

                checkForBloodWoodPlant(i, iBlockBelowJ, k);
            }
    	}
    }
    
    //************* FCIEntityPacketHandler ************//

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
    	return true;
    }
    
    @Override
    public boolean shouldServerTreatAsOversized()
    {
    	return false;
    }
    
    @Override
    public Packet getSpawnPacketForThisEntity()
    {    	
    	// FCTODO: Move this up into a parent class with the Floating Item code
    	
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream( byteStream );
        
        try
        {
	        dataStream.writeInt( BTWPacketManager.BLOOD_WOOD_SAPLING_ITEM_SPAWN_PACKET_ID);
	        dataStream.writeInt( entityId );
	        
	        dataStream.writeInt( MathHelper.floor_double( posX * 32D ) );
	        dataStream.writeInt( MathHelper.floor_double( posY * 32D ) );
	        dataStream.writeInt( MathHelper.floor_double( posZ * 32D ) );
	        
	        dataStream.writeInt( getEntityItem().itemID );
	        dataStream.writeInt( getEntityItem().stackSize );
	        dataStream.writeInt( getEntityItem().getItemDamage() );
	        
	        dataStream.writeByte( (byte)(int)( motionX * 128D ) );
	        dataStream.writeByte( (byte)(int)( motionY * 128D ) );
	        dataStream.writeByte( (byte)(int)( motionZ * 128D ) );	        		
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }        
	        
    	return new Packet250CustomPayload(BTWPacketManager.SPAWN_CUSTOM_ENTITY_PACKET_CHANNEL, byteStream.toByteArray() );
    }
    
    //************* Class Specific Methods ************//

    public void checkForBloodWoodPlant(int i, int j, int k)
    {
    	Block blockAbove = Block.blocksList[worldObj.getBlockId( i, j + 1, k )];
    	
        if ( blockAbove == null || blockAbove.isAirBlock() || blockAbove.isGroundCover() )
    	{
        	int iBlockID = worldObj.getBlockId( i, j, k );

        	if ( iBlockID == Block.slowSand.blockID || ( iBlockID == BTWBlocks.planter.blockID &&
                                                         ((PlanterBlock)(BTWBlocks.planter)).getPlanterType(worldObj, i, j, k) == PlanterBlock.TYPE_SOUL_SAND) )
        	{        		
    			worldObj.setBlockAndMetadataWithNotify( i, j + 1, k,
                        BTWBlocks.aestheticVegetation.blockID,
					AestheticVegetationBlock.SUBTYPE_BLOOD_WOOD_SAPLING);
    			
    			getEntityItem().stackSize--;
    			
    			if ( getEntityItem().stackSize <= 0 )
    			{
    				setDead();
    			}
        	}
    	}
    }
}