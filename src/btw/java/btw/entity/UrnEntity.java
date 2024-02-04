// FCMOD

package btw.entity;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticOpaqueBlock;
import btw.client.fx.BTWEffectManager;
import btw.network.packet.BTWPacketManager;
import btw.entity.mob.SnowmanEntity;
import btw.entity.mob.WitherEntityPersistent;
import btw.item.BTWItems;
import btw.world.util.BlockPos;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UrnEntity extends EntityThrowable
	implements EntityWithCustomPacket
{
	static public final double CUBIC_RANGE = 4D;
	
    public int itemShiftedIndex;
    
    public UrnEntity(World world )
    {
        super(world);

        itemShiftedIndex = 0;
    }
    
    public UrnEntity(World world, int iItemShiftedIndex )
    {
    	this( world );

        itemShiftedIndex = iItemShiftedIndex;
    }

    public UrnEntity(World world, EntityLiving throwingEntity, int iItemShiftedIndex )
    {
    	super( world, throwingEntity );

        itemShiftedIndex = iItemShiftedIndex;
    }

    public UrnEntity(World world, double d, double d1, double d2, int iItemShiftedIndex )
    {
    	super( world, d, d1, d2 );

        itemShiftedIndex = iItemShiftedIndex;
    }

	@Override
    public void writeEntityToNBT( NBTTagCompound nbttagcompound )
    {
		super.writeEntityToNBT( nbttagcompound );
		
        nbttagcompound.setInteger("m_iItemShiftedIndex", itemShiftedIndex);
    }

	@Override
    public void readEntityFromNBT( NBTTagCompound nbttagcompound )
    {
		super.readEntityFromNBT( nbttagcompound );

        itemShiftedIndex = nbttagcompound.getInteger("m_iItemShiftedIndex");
    }
	
	
	@Override
	protected void onImpact(MovingObjectPosition impactPosition) {
		setDead();
		
		if (worldObj.isRemote) {
			return;
		}
		
		if (itemShiftedIndex == BTWItems.soulUrn.itemID) {
			boolean looseSoul = true;
			
			if (impactPosition.entityHit != null) {
				impactPosition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0);
				
				if (impactPosition.entityHit instanceof EntityCreature) {
					looseSoul = !((EntityCreature) impactPosition.entityHit).canSoulAffectEntity(this);
				}
			}
			else if (attemptToCreateGolemOrWither(worldObj, impactPosition.blockX, impactPosition.blockY, impactPosition.blockZ)) {
				looseSoul = false;
			}
			else {
				Block impactBlock = Block.blocksList[worldObj.getBlockId(impactPosition.blockX, impactPosition.blockY, impactPosition.blockZ)];
				
				if (impactBlock != null && impactBlock.attemptToAffectBlockWithSoul(worldObj, impactPosition.blockX, impactPosition.blockY, impactPosition.blockZ)) {
					looseSoul = false;
				}
			}
			
			// nothing at direct impact spot. free soul
			if (looseSoul) {
				looseSoulEffects(worldObj, impactPosition.hitVec.xCoord, impactPosition.hitVec.yCoord, impactPosition.hitVec.zCoord);
			}
			
			worldObj.playAuxSFX(BTWEffectManager.SOUL_URN_SHATTER_EFFECT_ID, (int) Math.round(this.posX), (int) Math.round(this.posY),
					(int) Math.round(this.posZ), 0);
		}
	}
	
	public void looseSoulEffects(World worldObj, double xCoord, double yCoord, double zCoord) {
		AxisAlignedBB possessionBox = AxisAlignedBB.getAABBPool()
				.getAABB(xCoord - CUBIC_RANGE, yCoord - CUBIC_RANGE,
						zCoord - CUBIC_RANGE, xCoord + CUBIC_RANGE,
						yCoord + CUBIC_RANGE, zCoord + CUBIC_RANGE);

		List nearbyCreatures = worldObj.getEntitiesWithinAABB(EntityCreature.class, possessionBox);
		
		for (Object nearbyCreature : nearbyCreatures) {
			EntityCreature tempCreature = (EntityCreature) nearbyCreature;
			
			if (tempCreature.canSoulAffectEntity(this)) {
				tempCreature.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0);
				return;
			}
		}
		
		// try to enter a random block

		int range =  2; // enter blocks in smaller range in line with visual particles
		Block tempBlock;
		List blockPositions = new ArrayList();
		for (int i = (int)xCoord - range; i <= xCoord + range; i++) {
			for (int j = (int)yCoord - range; j <= yCoord + range; j++) {
				for (int k = (int)zCoord - range; k <= zCoord + range; k++) {
					blockPositions.add(new BlockPos(i, j, k));
				}
			}
		}
		Collections.shuffle(blockPositions, rand);
		
		for (Object blockPos : blockPositions) {
			BlockPos tempPos = (BlockPos) blockPos;
			if (attemptToCreateGolemOrWither(worldObj, tempPos.x, tempPos.y, tempPos.z)) {
				return;
			}
			
			tempBlock = Block.blocksList[worldObj.getBlockId(tempPos.x, tempPos.y, tempPos.z)];
			
			if (tempBlock != null && tempBlock.attemptToAffectBlockWithSoul(worldObj, tempPos.x, tempPos.y, tempPos.z)) {
				return;
			}
		}
	}
	
	//------------- FCIEntityPacketHandler ------------//

    @Override
    public int getTrackerViewDistance()
    {
    	return 64;
    }
    
    @Override
    public int getTrackerUpdateFrequency()
    {
    	return 10;
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
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream( byteStream );
        
        try
        {
	        dataStream.writeInt( BTWPacketManager.URN_SPAWN_PACKET_ID);
	        dataStream.writeInt( entityId );
	        
	        dataStream.writeInt( MathHelper.floor_double( posX * 32D ) );
	        dataStream.writeInt( MathHelper.floor_double( posY * 32D ) );
	        dataStream.writeInt( MathHelper.floor_double( posZ * 32D ) );
	        
	        dataStream.writeInt(itemShiftedIndex);
	        
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
    
    //------------- Class Specific Methods ------------//
    
    static public boolean attemptToCreateGolemOrWither(World world, int x, int y, int z)
    {
    	// check if this is part of a column with a head on top
	
		for (int j = y; j <= y + 2; j++) {
			if (isGolemHeadBlock(world, x, j, z)) {
				return attemptToCreateSnowOrIronGolem(world, x, j, z);
			}
			else if (isWitherHeadBlock(world, x, j, z)) {
				return attemptToCreateWither(world, x, j, z);
			}
			else if (!isValidBodyBlockForSnowGolem(world, x, j, z) && !isValidBodyBlockForIronGolem(world, x, j, z) &&
					!isWitherBodyBlock(world, x, j, z)) {
				break;
			}
		}
	
		// check if this is a possible "arm" of an iron golem
		if (isValidBodyBlockForIronGolem(world, x, y, z)) {
			int j = y + 1;
		
			for (int i = x - 1; i <= x + 1; i++) {
				for (int k = z - 1; k <= z + 1; k++) {
					if (isGolemHeadBlock(world, i, j, k)) {
						return attemptToCreateSnowOrIronGolem(world, i, j, k);
					}
				}
			}
		}
    	
    	return false;
    }
    
    static private boolean isGolemHeadBlock(World world, int i, int j, int k)
    {
		int iBlockID = world.getBlockId( i, j, k );
		
		return iBlockID == Block.pumpkin.blockID || 
			iBlockID == Block.pumpkinLantern.blockID;
    }
    
    static private boolean isWitherHeadBlock(World world, int i, int j, int k)
    {
		int iBlockID = world.getBlockId( i, j, k );
		
		if ( iBlockID == Block.skull.blockID )
		{
            TileEntity tileEntity = world.getBlockTileEntity( i, j, k );
            
            if ( tileEntity != null && tileEntity instanceof TileEntitySkull )
            {            	
            	return ((TileEntitySkull)tileEntity).getSkullType() == 5; // infused skull
            }
		}
		
		return false;
    }
    
    static private boolean isValidBodyBlockForSnowGolem(World world, int i, int j, int k)
    {
    	int iBlockID = world.getBlockId( i, j, k );
    	
    	return iBlockID == Block.blockSnow.blockID || 
    		iBlockID == BTWBlocks.looseSnow.blockID ||
    		iBlockID == BTWBlocks.solidSnow.blockID;
    }

    static private boolean isValidBodyBlockForIronGolem(World world, int i, int j, int k)
    {
    	int iBlockID = world.getBlockId( i, j, k );
    	
    	return iBlockID == Block.blockIron.blockID;
    }

    static private boolean isWitherBodyBlock(World world, int i, int j, int k)
    {
    	int iBlockID = world.getBlockId( i, j, k );
    	
    	if ( iBlockID == BTWBlocks.aestheticOpaque.blockID )
    	{
    		int iSubtype = world.getBlockMetadata( i, j, k );
    		
    		if ( iSubtype == AestheticOpaqueBlock.SUBTYPE_BONE)
    		{
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    /**
     * Assumes block at location has already been tested as a valid head block (pumpkin)
     */
    static private boolean attemptToCreateSnowOrIronGolem(World world, int i, int j, int k)
    {
        if (isValidBodyBlockForSnowGolem(world, i, j - 1, k) &&
            isValidBodyBlockForSnowGolem(world, i, j - 2, k) )
        {
            world.setBlock( i, j, k, 0 );
            world.setBlock( i, j - 1, k, 0 );
            world.setBlock( i, j - 2, k, 0 );
            
            world.notifyBlockChange( i, j, k, 0 );
            world.notifyBlockChange( i, j - 1, k, 0 );
            world.notifyBlockChange( i, j - 2, k, 0 );
            
            SnowmanEntity snowGolem = (SnowmanEntity) EntityList.createEntityOfType(SnowmanEntity.class, world);
            
            snowGolem.setLocationAndAngles( (double)i + 0.5D, (double)j - 1.95D, (double)k + 0.5D, 0F, 0F);
            
            world.spawnEntityInWorld( snowGolem );
                
	        world.playAuxSFX( BTWEffectManager.CREATE_SNOW_GOLEM_EFFECT_ID, i, j, k, 0 );

            return true;
        }
        else if (isValidBodyBlockForIronGolem(world, i, j - 1, k) &&
                 isValidBodyBlockForIronGolem(world, i, j - 2, k) )
        {
            boolean bIronAlongIAxis = isValidBodyBlockForIronGolem(world, i - 1, j - 1, k) &&
                                      isValidBodyBlockForIronGolem(world, i + 1, j - 1, k);
            
            boolean bIronAlongKAxis = isValidBodyBlockForIronGolem(world, i, j - 1, k - 1) &&
                                      isValidBodyBlockForIronGolem(world, i, j - 1, k + 1);

            if ( bIronAlongIAxis || bIronAlongKAxis )
            {
                world.setBlock( i, j, k, 0 );
                world.setBlock( i, j - 1, k, 0 );
                world.setBlock( i, j - 2, k, 0 );

                if ( bIronAlongIAxis )
                {
                    world.setBlock( i - 1, j - 1, k, 0 );
                    world.setBlock( i + 1, j - 1, k, 0 );
                }
                else
                {
                    world.setBlock( i, j - 1, k - 1, 0 );
                    world.setBlock( i, j - 1, k + 1, 0 );
                }

                world.notifyBlockChange( i, j, k, 0 );
                world.notifyBlockChange( i, j - 1, k, 0 );
                world.notifyBlockChange( i, j - 2, k, 0 );

                if ( bIronAlongIAxis )
                {
                    world.notifyBlockChange( i - 1, j - 1, k, 0 );
                    world.notifyBlockChange( i + 1, j - 1, k, 0 );
                }
                else
                {
                    world.notifyBlockChange( i, j - 1, k - 1, 0 );
                    world.notifyBlockChange( i, j - 1, k + 1, 0 );
                }

                EntityIronGolem ironGolem = (EntityIronGolem) EntityList.createEntityOfType(EntityIronGolem.class, world);
                
                ironGolem.setPlayerCreated( true );
                ironGolem.setLocationAndAngles( (double)i + 0.5D, (double)j - 1.95D, (double)k + 0.5D, 0F, 0F);
                
                world.spawnEntityInWorld( ironGolem );

    	        world.playAuxSFX( BTWEffectManager.CREATE_IRON_GOLEM_EFFECT_ID, i, j, k, 0 );
            }
            
            return true;
        }
        
    	return false;
    }
    
    /**
     * Assumes block at location has already been tested as a valid head block (infused skull)
     */
    static private boolean attemptToCreateWither(World world, int i, int j, int k)
    {
        if ( j >= 2 && world.provider.dimensionId == 0 )
        {
            for ( int iTempKOffset = -2; iTempKOffset <= 0; ++iTempKOffset )
            {
                if (
                        isWitherBodyBlock(world, i, j - 1, k + iTempKOffset) &&
                        isWitherBodyBlock(world, i, j - 1, k + iTempKOffset + 1) &&
                        isWitherBodyBlock(world, i, j - 2, k + iTempKOffset + 1) &&
                        isWitherBodyBlock(world, i, j - 1, k + iTempKOffset + 2) &&
                        isWitherHeadBlock(world, i, j, k + iTempKOffset) &&
                        isWitherHeadBlock(world, i, j, k + iTempKOffset + 1) &&
                        isWitherHeadBlock(world, i, j, k + iTempKOffset + 2) )
                {
                	// This flags the skulls not to drop as an item when they're destroyed
                	
                    world.SetBlockMetadataWithNotify( i, j, k + iTempKOffset, 8, 2 );
                    world.SetBlockMetadataWithNotify( i, j, k + iTempKOffset + 1, 8, 2 );
                    world.SetBlockMetadataWithNotify( i, j, k + iTempKOffset + 2, 8, 2 );
                    
                    world.setBlock( i, j, k + iTempKOffset, 0, 0, 2 );
                    world.setBlock( i, j, k + iTempKOffset + 1, 0, 0, 2 );
                    world.setBlock( i, j, k + iTempKOffset + 2, 0, 0, 2 );
                                    
                    world.setBlock( i, j - 1, k + iTempKOffset, 0, 0, 2 );
                    world.setBlock( i, j - 1, k + iTempKOffset + 1, 0, 0, 2 );
                    world.setBlock( i, j - 1, k + iTempKOffset + 2, 0, 0, 2 );
                    world.setBlock( i, j - 2, k + iTempKOffset + 1, 0, 0, 2 );

                    WitherEntityPersistent.summonWitherAtLocation(world,
                                                                  i, j, k + iTempKOffset + 1);
                    
                    world.notifyBlockChange( i, j, k + iTempKOffset, 0 );
                    world.notifyBlockChange( i, j, k + iTempKOffset + 1, 0 );
                    world.notifyBlockChange( i, j, k + iTempKOffset + 2, 0 );
                                             
                    world.notifyBlockChange( i, j - 1, k + iTempKOffset, 0 );
                    world.notifyBlockChange( i, j - 1, k + iTempKOffset + 1, 0 );
                    world.notifyBlockChange( i, j - 1, k + iTempKOffset + 2, 0 );
                    world.notifyBlockChange( i, j - 2, k + iTempKOffset + 1, 0 );
                    
                    return true;
                }
            }

            for ( int iTempIOffset = -2; iTempIOffset <= 0; ++iTempIOffset)
            {
                if (
                        isWitherBodyBlock(world, i + iTempIOffset, j - 1, k) &&
                        isWitherBodyBlock(world, i + iTempIOffset + 1, j - 1, k) &&
                        isWitherBodyBlock(world, i + iTempIOffset + 1, j - 2, k) &&
                        isWitherBodyBlock(world, i + iTempIOffset + 2, j - 1, k) &&
                        isWitherHeadBlock(world, i + iTempIOffset, j, k) &&
                        isWitherHeadBlock(world, i + iTempIOffset + 1, j, k) &&
                        isWitherHeadBlock(world, i + iTempIOffset + 2, j, k) )
                {
                	// This flags the skulls not to drop as an item when they're destroyed
                	
                    world.SetBlockMetadataWithNotify( i + iTempIOffset, j, k, 8, 2 );
                    world.SetBlockMetadataWithNotify( i + iTempIOffset + 1, j, k, 8, 2 );
                    world.SetBlockMetadataWithNotify( i + iTempIOffset + 2, j, k, 8, 2 );
                    
                    world.setBlock( i + iTempIOffset, j, k, 0, 0, 2 );
                    world.setBlock( i + iTempIOffset + 1, j, k, 0, 0, 2 );
                    world.setBlock( i + iTempIOffset + 2, j, k, 0, 0, 2 );
                    world.setBlock( i + iTempIOffset, j - 1, k, 0, 0, 2 );
                    world.setBlock( i + iTempIOffset + 1, j - 1, k, 0, 0, 2 );
                    world.setBlock( i + iTempIOffset + 2, j - 1, k, 0, 0, 2 );
                    world.setBlock( i + iTempIOffset + 1, j - 2, k, 0, 0, 2 );

                    WitherEntityPersistent.summonWitherAtLocation(world,
                    	i + iTempIOffset + 1, j, k);
                    
                    world.notifyBlockChange( i + iTempIOffset, j, k, 0 );
                    world.notifyBlockChange( i + iTempIOffset + 1, j, k, 0 );
                    world.notifyBlockChange( i + iTempIOffset + 2, j, k, 0 );
                    world.notifyBlockChange( i + iTempIOffset, j - 1, k, 0 );
                    world.notifyBlockChange( i + iTempIOffset + 1, j - 1, k, 0 );
                    world.notifyBlockChange( i + iTempIOffset + 2, j - 1, k, 0 );
                    world.notifyBlockChange( i + iTempIOffset + 1, j - 2, k, 0 );
                    
                    return true;
                }
            }
        }
        
        return false;
    }
    
	//----------- Client Side Functionality -----------//
}