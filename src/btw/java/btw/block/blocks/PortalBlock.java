// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;

import java.util.Random;

public class PortalBlock extends BlockPortal {
	private static final int CHANCE_OF_CHECKING_FOR_POSSESSION = 10;
	public static final int CREATURE_POSSESSION_RANGE = 16;
	
	// Sizes include frame
	private static final int MIN_PORTAL_WIDTH = 4;
	private static final int MIN_PORTAL_HEIGHT = 5;
	private static final int MAX_PORTAL_WIDTH = 23;
	private static final int MAX_PORTAL_HEIGHT = 23;
	
	public PortalBlock(int blockID) {
		super(blockID);
		
		setHardness(-1F);
		
		setLightValue(0.75F);
		
		setStepSound(soundGlassFootstep);
		
		setUnlocalizedName("portal");
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		// override to prevent pigmen spawning around portals in the overworld and to possess nearby animals and villagers
		if (rand.nextInt(CHANCE_OF_CHECKING_FOR_POSSESSION) == 0 && world.provider.isSurfaceWorld()) {
			EntityCreature.attemptToPossessCreaturesAroundBlock(world, x, y, z, 1, CREATURE_POSSESSION_RANGE);
		}
		
		// set this here due to legacy portals having potentially not set it on creation
		WorldUtils.gameProgressSetNetherBeenAccessedServerOnly();
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int neighborID) {
		//Check direction of the portal
		byte isX = 0;
		byte isZ = 1;
		
		if (world.getBlockId(x - 1, y, z) == this.blockID || world.getBlockId(x + 1, y, z) == this.blockID) {
			isX = 1;
			isZ = 0;
		}
		
		int a = 0;
		int b = 0;
		int c = 0;
		int d = 0;
		
		while (world.getBlockId(x, y - a, z) == this.blockID)
			a++;
		
		while (world.getBlockId(x, y + b, z) == this.blockID)
			b++;
		
		while (world.getBlockId(x - isX * c, y, z - isZ * c) == this.blockID)
			c++;
		
		while (world.getBlockId(x + isX * d,  y, z + isZ * d) == this.blockID)
			d++;
		
		if (world.getBlockId(x, y - a, z) != Block.obsidian.blockID ||
				world.getBlockId(x, y + b, z) != Block.obsidian.blockID ||
				world.getBlockId(x - isX * c, y, z - isZ * c) != Block.obsidian.blockID ||
				world.getBlockId(x + isX * d,  y, z + isZ * d) != Block.obsidian.blockID)
		{
			world.setBlockToAir(x, y, z);
		}
	}
	
	@Override
	public boolean tryToCreatePortal(World world, int x, int y, int z) {
		//Used to find bounds of the portal
		int xDistPos = 0;
		int xDistNeg = 0;
		int zDistPos = 0;
		int zDistNeg = 0;
		
		//Checks if there is obsidian on the same level on the x or z axis from it within the max portal width of the chosen location
		for (int i = 0; i < MAX_PORTAL_WIDTH - 1; i++) {
			if (world.getBlockId(x + i, y, z) == Block.obsidian.blockID && xDistPos == 0) {
				xDistPos = i;
			}
			if (world.getBlockId(x - i, y, z) == Block.obsidian.blockID && xDistNeg == 0) {
				xDistNeg = -i;
			}
			if (world.getBlockId(x, y, z + i) == Block.obsidian.blockID && zDistPos == 0) {
				zDistPos = i;
			}
			if (world.getBlockId(x, y, z - i) == Block.obsidian.blockID && zDistNeg == 0) {
				zDistNeg = -i;
			}
		}
		
		int xDiff = xDistPos - xDistNeg + 1;
		int zDiff = zDistPos - zDistNeg + 1;
		
		//If obsidian is too far or too close (or doesn't exist) in both x and z, stop attempt
		if ((xDiff < MIN_PORTAL_WIDTH && zDiff < MIN_PORTAL_WIDTH) ||
				(xDiff > MAX_PORTAL_WIDTH && zDiff > MAX_PORTAL_WIDTH) ||
				(xDiff > MAX_PORTAL_WIDTH && zDiff < MIN_PORTAL_WIDTH) ||
				(zDiff > MAX_PORTAL_WIDTH && xDiff < MIN_PORTAL_WIDTH))
		{
			return false;
		}
		
		int isX = 0;
		int isZ = 0;
		
		//Finds the direction with a calid portal, favoring x
		if (xDistPos != 0 && xDistNeg != 0) {
			zDistPos = 0;
			zDistNeg = 0;
			isX = 1;
		}
		else if (zDistPos != 0 && zDistNeg != 0) {
			xDistPos = 0;
			xDistNeg = 0;
			isZ = 1;
		}
		
		int yDist = 0;
		
		//Starts searching above starting position for a block of obsidian within min and max portal size
		for (int i = MIN_PORTAL_HEIGHT - 2; i < MAX_PORTAL_HEIGHT - 1; i++) {
			if (world.getBlockId(x,  y + i,  z) == Block.obsidian.blockID ) {
				yDist = i + 1;
				break;
			}
		}
		
		//If no obsidian is found above the starting location
		if (yDist == 0) {
			return false;
		}
		
		int lowerBound = xDistNeg + zDistNeg;
		int upperBound = xDistPos + zDistPos;
		
		for (int i = lowerBound; i <= upperBound; i++) {
			for (int j = -1; j < yDist; j++) {
				int id = world.getBlockId(x + isX * i, y + j, z + isZ * i);
				
				//Ignores corners
				if ((i != lowerBound && i != upperBound) || (j != -1 && j != yDist - 1)) {
					
					//If it's on the border
					if (i == lowerBound || i == upperBound || j == -1 || j == yDist - 1) {
						if (id != Block.obsidian.blockID) {
							return false;
						}
					}
					
					//Checks to make sure no blocks are in frame except air and firestarting blocks, otherwise fails
					else if (!world.isAirBlock(x + isX * i, y + j, z + isZ * i) &&
							id != Block.fire.blockID &&
							id != BTWBlocks.largeCampfire.blockID &&
							id != BTWBlocks.mediumCampfire.blockID &&
							id != BTWBlocks.smallCampfire.blockID &&
							id != BTWBlocks.unlitCampfire.blockID)
					{
						return false;
					}
				}
			}
		}
		
		//Fills frame with portal blocks
		for (int i = lowerBound + 1; i < upperBound; i++) {
			for (int j = 0; j < yDist - 1; j++) {
				world.setBlock(x + isX * i, y + j, z + isZ * i, Block.portal.blockID, 0, 2);
			}
		}
		
		WorldUtils.gameProgressSetNetherBeenAccessedServerOnly();
		return true;
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int i, int j, int k) {
		// override to deprecate parent
	}
	
	@Override
	public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int i, int j, int k) {
		float fHalfWidth;
		float fHalfDepth;
		
		if (blockAccess.getBlockId(i - 1, j, k) != blockID && blockAccess.getBlockId(i + 1, j, k) != blockID) {
			fHalfWidth = 0.125F;
			fHalfDepth = 0.5F;
		}
		else {
			fHalfWidth = 0.5F;
			fHalfDepth = 0.125F;
		}
		
		return AxisAlignedBB.getAABBPool().getAABB(0.5F - fHalfWidth, 0.0F, 0.5F - fHalfDepth, 0.5F + fHalfWidth, 1.0F, 0.5F + fHalfDepth);
	}
	
	@Override
	public ItemStack getStackRetrievedByBlockDispenser(World world, int i, int j, int k) {
		return null; // can't be picked up
	}
	
	//------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//
}