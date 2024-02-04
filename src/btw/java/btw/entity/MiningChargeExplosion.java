// FCMOD

package btw.entity;

import btw.client.fx.BTWEffectManager;
import btw.world.util.BlockPos;
import net.minecraft.src.*;

import java.util.List;

public class MiningChargeExplosion
{
	public final static float EXPLOSION_STRENGTH = 20.0F;
	
    private World worldObj;
    public double explosionX;
    public double explosionY;
    public double explosionZ;
    public int facing;
    
    public MiningChargeExplosion(World world, double dXPos, double dYPos, double dZPos, int iFacing )
    {
        worldObj = world;
        
        explosionX = dXPos;
        explosionY = dYPos;
        explosionZ = dZPos;

        facing = iFacing;
    }

    public void doExplosion()
    {
        damageEntities();
        
        destroyBlocks();
        
        worldObj.playAuxSFX( BTWEffectManager.MINING_CHARGE_EXPLOSION_EFFECT_ID,
    		MathHelper.floor_double( explosionX ),
    		MathHelper.floor_double( explosionY ), 
    		MathHelper.floor_double( explosionZ ), 0 );
    }
    
    private void destroyBlocks()
    {
        int iISource = MathHelper.floor_double( explosionX );
        int iJSource = MathHelper.floor_double( explosionY );
        int iKSource = MathHelper.floor_double( explosionZ );
        
        int iSourceBlockID = worldObj.getBlockId( iISource, iJSource, iKSource);
        
        if ( iSourceBlockID > 0 )
        {                        
            if ((Block.blocksList[iSourceBlockID]).getExplosionResistance( null, worldObj, iISource, iJSource, iKSource ) >= EXPLOSION_STRENGTH)
            {
            	// we are in a block that's too tough to destroy.  Abort.
            	
            	return;
            }
        }
        
        // offset the blast so that it is centered on the block to which we are attached
        
        BlockPos targetPos = new BlockPos( iISource, iJSource, iKSource );
        targetPos.addFacingAsOffset(facing);
        
        int iTargetBlockID = worldObj.getBlockId(targetPos.x, targetPos.y, targetPos.z);
        
        if ( iTargetBlockID > 0 )
        {                        
            if ((Block.blocksList[iTargetBlockID]).getExplosionResistance(null, worldObj, targetPos.x, targetPos.y, targetPos.z) >= EXPLOSION_STRENGTH)
            {
            	// we are attached to a block that's too tough to destroy.  Center the blast on the charge's
            	// position
            	
            	targetPos = new BlockPos( iISource, iJSource, iKSource );
            }
        }
        
        destroyCentralBlocks(targetPos.x, targetPos.y, targetPos.z);
        
        // resolve the extra block of penetration towards our facing.
        
        targetPos.addFacingAsOffset(facing);
        
        iTargetBlockID = worldObj.getBlockId(targetPos.x, targetPos.y, targetPos.z);

        if ( iTargetBlockID > 0 )
        {                        
            if ((Block.blocksList[iTargetBlockID]).getExplosionResistance(null, worldObj, targetPos.x, targetPos.y, targetPos.z) >= EXPLOSION_STRENGTH)
            {
            	// the block between the source and extra block is too tough, abort
            	
            	return;
            }
        }
        
        targetPos.addFacingAsOffset(facing);
        
        iTargetBlockID = worldObj.getBlockId(targetPos.x, targetPos.y, targetPos.z);

        if ( iTargetBlockID > 0 )
        {                        
            if ((Block.blocksList[iTargetBlockID]).getExplosionResistance(null, worldObj, targetPos.x, targetPos.y, targetPos.z) < EXPLOSION_STRENGTH)
            {
            	destroyBlock(targetPos.x, targetPos.y, targetPos.z);
            }
        }
    }
    
    private void destroyCentralBlocks(int iICenter, int iJCenter, int iKCenter)
    {
        for ( int iTempI = iICenter - 1; iTempI <= iICenter + 1; iTempI++ )
        {
            for ( int iTempJ = iJCenter - 1; iTempJ <= iJCenter + 1; iTempJ++ )
            {
                for ( int iTempK = iKCenter - 1; iTempK <= iKCenter + 1; iTempK++ )
                {
                    int iTempBlockID = worldObj.getBlockId( iTempI, iTempJ, iTempK );
                    
                    if ( iTempBlockID > 0 )
                    {                        
                        if ((Block.blocksList[iTempBlockID]).getExplosionResistance( null, worldObj, iTempI, iTempJ, iTempK ) < EXPLOSION_STRENGTH)
	                    {
	                    	destroyBlock(iTempI, iTempJ, iTempK);
	                    }
                    }
                }
            }
        }        
    }

    private void damageEntities()
    {
    	float explosionSize = 6.0F;
    	
        int k = MathHelper.floor_double(explosionX - (double)explosionSize - 1.0D);
        int i1 = MathHelper.floor_double(explosionX + (double)explosionSize + 1.0D);
        int k1 = MathHelper.floor_double(explosionY - (double)explosionSize - 1.0D);
        int l1 = MathHelper.floor_double(explosionY + (double)explosionSize + 1.0D);
        int i2 = MathHelper.floor_double(explosionZ - (double)explosionSize - 1.0D);
        int j2 = MathHelper.floor_double(explosionZ + (double)explosionSize + 1.0D);
        
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(null, AxisAlignedBB.getAABBPool().getAABB(k, k1, i2, i1, l1, j2));
        
        Vec3 vec3d = Vec3.createVectorHelper(explosionX, explosionY, explosionZ);
        
        for(int k2 = 0; k2 < list.size(); k2++)
        {
            Entity entity = (Entity)list.get(k2);
            double d4 = entity.getDistance(explosionX, explosionY, explosionZ) / (double)explosionSize;
            if(d4 <= 1.0D)
            {
                double d6 = entity.posX - explosionX;
                double d8 = entity.posY - explosionY;
                double d10 = entity.posZ - explosionZ;
                double d11 = MathHelper.sqrt_double(d6 * d6 + d8 * d8 + d10 * d10);
                d6 /= d11;
                d8 /= d11;
                d10 /= d11;
                double d12 = worldObj.getBlockDensity(vec3d, entity.boundingBox);
                double d13 = (1.0D - d4) * d12;
                
                if ( entity instanceof EntityItem )
                {
                	EntityItem entityItem = (EntityItem)entity;
                	
                	int iItemID = entityItem.getEntityItem().itemID;

                	
                	if ( iItemID != Item.coal.itemID &&
            			iItemID != Item.redstone.itemID &&
            			iItemID != Item.dyePowder.itemID &&
            			iItemID != Item.flint.itemID &&
            			iItemID != Block.oreGold.blockID &&
            			iItemID != Block.oreIron.blockID )
                	{
                		int iItemDamage = (int)(((d13 * d13 + d13) / 2D) * 8D * (double)explosionSize + 1.0D);
                		
                		if ( iItemDamage > 2 )
                		{
                			iItemDamage = 3;
                		}
                		
                    	entity.attackEntityFrom( DamageSource.setExplosionSource( null ), iItemDamage );
                	}
                }
                else                	
                {                		
                	entity.attackEntityFrom( DamageSource.setExplosionSource( null ), (int)(((d13 * d13 + d13) / 2D) * 8D * (double)explosionSize + 1.0D));
                }
                
            	if ( !( entity instanceof MiningChargeEntity) )
            	{
	                double d14 = d13;
	                entity.motionX += d6 * d14;
	                entity.motionY += d8 * d14;
	                entity.motionZ += d10 * d14;
            	}
            }
        }
    }
    
    private void destroyBlock(int x, int y, int z)
    {
        int blockID = worldObj.getBlockId(x, y, z);
        int meta = worldObj.getBlockMetadata(x, y, z);
        
        if (blockID > 0) {
        	Block destroyedBlock = Block.blocksList[blockID];
        	
        	destroyedBlock.dropItemsOnDestroyedByMiningCharge(worldObj, x, y, z, meta);
        	destroyedBlock.onBlockDestroyedByMiningCharge(worldObj, x, y, z);
            
            worldObj.setBlockWithNotify(x, y, z, 0);

            destroyedBlock.postBlockDestroyedByMiningCharge(worldObj, x, y, z);
        }
    }    
}
