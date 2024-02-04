// FCMOD

package btw.block.blocks;

import net.minecraft.src.*;

import java.util.Random;

public class LavaBlockStationary extends BlockStationary
{
	public LavaBlockStationary(int iBlockID, Material material)
	{
		super( iBlockID, material );
	}

	@Override
	public boolean canPathThroughBlock(IBlockAccess blockAccess, int i, int j, int k, Entity entity, PathFinder pathFinder)
	{
		return entity.handleLavaMovement();
	}

	@Override
	public int getWeightOnPathBlocked(IBlockAccess blockAccess, int i, int j, int k)
	{
		return -2;
	}

	@Override
	public boolean getDoesFireDamageToEntities(World world, int i, int j, int k)
	{
		return true;
	}

	@Override
	public boolean getCanBlockLightItemOnFire(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}

	@Override
	public void updateTick( World world, int i, int j, int k, Random rand )
	{
		if (world.getGameRules().getGameRuleBooleanValue("doFireTick")) {
			checkForDirectSetOnFireToNeighborsInContact(world, i, j, k);

			checkForStartingFiresAroundLava(world, i, j, k, rand);
		}
	}

	//------------- Class Specific Methods ------------//    

	private boolean canBlockMaterialBurn(World world, int i, int j, int k)
	{
		return world.getBlockMaterial( i, j, k ).getCanBurn();
	}

	private void checkForDirectSetOnFireToNeighborsInContact(World world, int i, int j, int k)
	{
		checkForDirectSetOnFire(world, i, j - 1, k);

		checkForDirectSetOnFire(world, i - 1, j, k);
		checkForDirectSetOnFire(world, i + 1, j, k);

		checkForDirectSetOnFire(world, i, j, k - 1);
		checkForDirectSetOnFire(world, i, j, k + 1);
	}

	private void checkForDirectSetOnFire(World world, int i, int j, int k)
	{
		Block tempBlock = Block.blocksList[world.getBlockId( i, j, k )];

		if ( tempBlock != null && tempBlock.getCanBeSetOnFireDirectly(world, i, j, k) )
		{
			tempBlock.setOnFireDirectly(world, i, j, k);
		}
	}

	private void checkForStartingFiresAroundLava(World world, int i, int j, int k, Random rand)
	{
		// the below duplicates fire starting ability of lava in BlockStationary.updateTick(), 
		// and adds ability for blocks to be set on fire directly

		int iNumAttempts = rand.nextInt(3);

		if ( iNumAttempts != 0 )
		{
			for ( int iTempCount = 0; iTempCount < iNumAttempts; ++iTempCount )
			{
				i += rand.nextInt(3) - 1;
				++j;
				k += rand.nextInt(3) - 1;

				int iTempBlockID = world.getBlockId( i, j, k );
				Block tempBlock = Block.blocksList[iTempBlockID];

				if ( tempBlock == null || tempBlock.isAirBlock() )
				{
					if (canBlockMaterialBurn(world, i - 1, j, k) || canBlockMaterialBurn(world, i + 1, j, k) ||
						canBlockMaterialBurn(world, i, j, k - 1) || canBlockMaterialBurn(world, i, j, k + 1) ||
						canBlockMaterialBurn(world, i, j - 1, k) || canBlockMaterialBurn(world, i, j + 1, k) )
					{
						world.setBlock(i, j, k, Block.fire.blockID);

						return;
					}
				}
				else if ( tempBlock.getCanBeSetOnFireDirectly(world, i, j, k) )
				{
					tempBlock.setOnFireDirectly(world, i, j, k);

					break;

				}
				else if ( tempBlock.blockMaterial.blocksMovement() )
				{
					return;
				}
			}
		}
		else
		{
			// I think this is actually a bug with vanilla, as it seems to start fires on the same layer as the first in the above,
			// but its intent seams to be to start fires on the same level as the lava block.  Won't fix as I don't
			// want to burn people's builds.

			// it may be this code is intended to just up the chances of blocks immediately neigboring burning

			int iSourceI = i;
			int iSourceK = k;

			for (int var9 = 0; var9 < 3; ++var9)
			{
				i = iSourceI + rand.nextInt(3) - 1;
				k = iSourceK + rand.nextInt(3) - 1;

				if (world.isAirBlock(i, j + 1, k) && this.canBlockMaterialBurn(world, i, j, k))
				{
					world.setBlock(i, j + 1, k, Block.fire.blockID);
				}
				else
				{
					Block tempBlock= Block.blocksList[world.getBlockId( i, j, k )];

					if ( tempBlock != null && tempBlock.getCanBeSetOnFireDirectly(world, i, j, k) )
					{
						tempBlock.setOnFireDirectly(world, i, j, k);
					}
				}
			}
		}        
	}

	//----------- Client Side Functionality -----------//
}  
