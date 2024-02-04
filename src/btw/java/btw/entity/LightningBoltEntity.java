// FCMOD

package btw.entity;

import btw.block.BTWBlocks;
import btw.block.blocks.FireBlock;
import btw.block.blocks.LogBlock;
import btw.client.fx.BTWEffectManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Iterator;

public class LightningBoltEntity extends EntityWeatherEffect
{
	private static final int MAX_TRUNK_DETECTION_PENETRATION = 6;
	private static final int MAX_TRUNK_PENETRATION = 6;

	private int lightningState;
	private int durationCountdown;

	public long renderSeed = 0L;

	public LightningBoltEntity(World world, double dPosX, double dPosY, double dPosZ )
	{
		super( world );

		setLocationAndAngles( dPosX, dPosY, dPosZ, 0F, 0F );

		lightningState = 2;
		durationCountdown = rand.nextInt(3) + 1;

		renderSeed = rand.nextLong();

		if (!world.isRemote && !isStrikingLightningRod() && world.doChunksNearChunkExist(
				MathHelper.floor_double( dPosX ), MathHelper.floor_double( dPosY ),
				MathHelper.floor_double( dPosZ ), 10) )
		{
			int iStrikeI = MathHelper.floor_double(dPosX);
			int iStrikeJ = (int)dPosY;
			int iStrikeK = MathHelper.floor_double(dPosZ);

			onStrikeBlock(world, iStrikeI, iStrikeJ, iStrikeK);

			if ( !isStrikingLightningRod() )
			{
				for ( int iTempCount = 0; iTempCount < 4; ++iTempCount )
				{
					int iTempI = iStrikeI + rand.nextInt( 3 ) - 1;
					int iTempJ = iStrikeJ + rand.nextInt( 3 ) - 1;
					int iTempK = iStrikeK + rand.nextInt( 3 ) - 1;

					if (FireBlock.canFireReplaceBlock(world, iTempI, iTempJ, iTempK) &&
                        Block.fire.canPlaceBlockAt( world, iTempI, iTempJ, iTempK ) )
					{
						if (world.getGameRules().getGameRuleBooleanValue("doFireTick") && world.getDifficulty().shouldLightningStartFires()) {
							world.setBlock( iTempI, iTempJ, iTempK, Block.fire.blockID );
						}
					}
				}
			}
		}
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		lightningState--;

		if (lightningState == 1 )
		{
			worldObj.func_82739_e( BTWEffectManager.LIGHTNING_STRIKE_EFFECT_ID,
					MathHelper.floor_double( posX ), (int)posY - 1, MathHelper.floor_double( posZ ), 0 );

			if ( !isStrikingLightningRod() )
			{
				double dRange = 3D;

				List<Entity> entityList = worldObj.getEntitiesWithinAABBExcludingEntity( this, 
						AxisAlignedBB.getAABBPool().getAABB( 
								posX - dRange, posY, posZ - dRange, 
								posX + dRange, posY + 6.0D + dRange, posZ + dRange ) );

				Iterator<Entity> entityIterator = entityList.iterator();

				while ( entityIterator.hasNext() )
				{
					Entity tempEntity = entityIterator.next();

					tempEntity.onStruckByLightning(this);
				}	            
			}
		}
		else if (lightningState < 0 )
		{
			if (durationCountdown == 0 )
			{
				setDead();
			}
			else if (lightningState < -rand.nextInt(10) )
			{
				durationCountdown--;

				lightningState = 1;
				renderSeed = rand.nextLong();

				if (!worldObj.isRemote && !isStrikingLightningRod() &&
					worldObj.doChunksNearChunkExist( MathHelper.floor_double( posX ),
								MathHelper.floor_double( posY ), MathHelper.floor_double( posZ ), 10 ) )
				{
					int iTempI = MathHelper.floor_double( posX );
					int iTempJ = (int)posY;
					int iTempK = MathHelper.floor_double( posZ );

					if (FireBlock.canFireReplaceBlock(worldObj, iTempI, iTempJ, iTempK) &&
                        Block.fire.canPlaceBlockAt( worldObj, iTempI, iTempJ, iTempK ) )
					{
						if (worldObj.getGameRules().getGameRuleBooleanValue("doFireTick") && worldObj.getDifficulty().shouldLightningStartFires()) {
							worldObj.setBlock( iTempI, iTempJ, iTempK, Block.fire.blockID );
						}
					}
				}
			}
		}

		if (lightningState >= 0 && worldObj.isRemote )
		{
			// handles flashes lighting up world during lightning strike

			worldObj.lastLightningBolt = 2;
		}
	}

	@Override
	protected void entityInit() {}

	@Override
	protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {}

	@Override
	protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {}

	//------------- Class Specific Methods ------------//

	private boolean isStrikingLightningRod()
	{
		int i = MathHelper.floor_double( posX );
		int j = MathHelper.floor_double( posY );
		int k = MathHelper.floor_double( posZ );

		return worldObj.getBlockId( i, j - 1, k ) == BTWBlocks.lightningRod.blockID;
	}

	private void onStrikeBlock(World world, int i, int j, int k) {
		if (world.getGameRules().getGameRuleBooleanValue("doFireTick") && world.getDifficulty().shouldLightningStartFires()) {
			if (hasHitTreeTrunk(world, i, j, k)) {
				burnTreeTrunk(world, i, j, k);
			}
			else if (world.getBlockId(i, j - 1, k) == Block.leaves.blockID) {
				// we've likely hit the top of a tree, but missed the trunk.  Take a few more tries at it.
				
				for (int iTempCount = 0; iTempCount < 5; iTempCount++) {
					int iTempI = i + world.rand.nextInt(5) - 2;
					int iTempK = k + world.rand.nextInt(5) - 2;
					
					if (FireBlock.canFireReplaceBlock(world, iTempI, j, iTempK) && hasHitTreeTrunk(world, iTempI, j, iTempK)) {
						burnTreeTrunk(world, iTempI, j, iTempK);
						
						break;
					}
				}
			}
			
			Block blockBelow = Block.blocksList[world.getBlockId(i, j - 1, k)];
			
			if (blockBelow != null) {
				blockBelow.onStruckByLightning(world, i, j - 1, k);
			}
			
			if (FireBlock.canFireReplaceBlock(world, i, j, k) && Block.fire.canPlaceBlockAt(world, i, j, k)) {
				world.setBlock(i, j, k, Block.fire.blockID);
			}
		}
	}

	private boolean hasHitTreeTrunk(World world, int i, int j, int k)
	{
		int m_iMinJ = j - MAX_TRUNK_DETECTION_PENETRATION;

		for ( j--; j >= m_iMinJ; j-- )
		{
			int iTempBlockID = world.getBlockId( i, j, k );

			if ( iTempBlockID == Block.wood.blockID )
			{
				// test for jungle wood, which doesn't convert due to massive destruction it would cause

				return ( world.getBlockMetadata( i, j, k ) & 3 ) != 3;  
			}
			else if ( iTempBlockID != Block.leaves.blockID )
			{
				return false;
			}
		}

		return false;
	}

	private boolean burnTreeTrunk(World world, int i, int j, int k)
	{
		int m_iMinJ = j - MAX_TRUNK_PENETRATION;

		LogBlock logBlock = (LogBlock)Block.wood;

		for ( j--; j >= m_iMinJ; j-- )
		{
			int iTempBlockID = world.getBlockId( i, j, k );

			if ( iTempBlockID == Block.wood.blockID )
			{
				// test for jungle wood, which doesn't convert due to massive destruction it would cause

				if ( ( world.getBlockMetadata( i, j, k ) & 3 ) != 3 )
				{
					logBlock.convertToSmouldering(world, i, j, k);
				}
				else
				{
					break;
				}
			}
			else if ( iTempBlockID == Block.leaves.blockID )
			{
				if ( Block.fire.canPlaceBlockAt( world, i, j, k ) )
				{
					world.setBlock( i, j, k, Block.fire.blockID );
				}
				else
				{
					world.setBlockToAir( i, j, k );
				}
			}
			else
			{
				break;
			}
		}

		return false;
	}

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean isInRangeToRenderVec3D( Vec3 vec )
	{
		return lightningState >= 0;
	}
}
