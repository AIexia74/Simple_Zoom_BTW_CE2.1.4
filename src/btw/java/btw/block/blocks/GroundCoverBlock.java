// FCMOD

package btw.block.blocks;

import btw.BTWMod;
import btw.client.render.util.RenderUtils;
import btw.block.util.RayTraceUtils;
import btw.world.util.BlockPos;
import com.prupe.mcpatcher.mal.block.RenderBlocksUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class GroundCoverBlock extends Block {
	public static final float VISUAL_HEIGHT = 0.125F;
	
	protected GroundCoverBlock(int iBlockID, Material material) {
		super(iBlockID, material);
		
		initBlockBounds(0F, 0F, 0F, 1F, 0.125F, 1F);
		
		setHardness(0.1F);
		setShovelsEffectiveOn();
		
		setBuoyant();
		
		setLightOpacity(0);
		
		setCreativeTab(CreativeTabs.tabDecorations);
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, int i, int j, int k) {
		int iBlockBelowID = world.getBlockId(i, j - 1, k);
		Block blockBelow = Block.blocksList[iBlockBelowID];
		
		if (blockBelow != null) {
			return blockBelow.canGroundCoverRestOnBlock(world, i, j - 1, k);
		}
		
		return false;
	}
	
	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, int iNeighborBlockID) {
		if (!canPlaceBlockAt(world, i, j, k)) {
			world.setBlockToAir(i, j, k);
		}
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, int i, int j, int k, Vec3 startRay, Vec3 endRay) {
		float fVisualOffset = 0F;
		
		int iBlockBelowID = world.getBlockId(i, j - 1, k);
		Block blockBelow = Block.blocksList[iBlockBelowID];
		
		if (blockBelow != null) {
			fVisualOffset = blockBelow.groundCoverRestingOnVisualOffset(world, i, j - 1, k);
		}
		
		RayTraceUtils rayTrace = new RayTraceUtils(world, i, j, k, startRay, endRay);
		
		rayTrace.addBoxWithLocalCoordsToIntersectionList(0D, fVisualOffset, 0D, 1D, VISUAL_HEIGHT + fVisualOffset, 1D);
		
		return rayTrace.getFirstIntersection();
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		return null;
	}
	
	@Override
	public boolean isGroundCover() {
		return true;
	}
	
	//------------- Class Specific Methods ------------//
	
	public static void clearAnyGroundCoverRestingOnBlock(World world, int i, int j, int k) {
		Block blockAbove = Block.blocksList[world.getBlockId(i, j + 1, k)];
		
		if (blockAbove != null) {
			if (blockAbove.isGroundCover()) {
				world.setBlockToAir(i, j + 1, k);
			}
			else if (blockAbove.groundCoverRestingOnVisualOffset(world, i, j + 1, k) < -0.99F) {
				Block block2Above = blockAbove = Block.blocksList[world.getBlockId(i, j + 2, k)];
				
				if (block2Above != null && block2Above.isGroundCover()) {
					world.setBlockToAir(i, j + 2, k);
				}
			}
		}
	}
	
	public static boolean isGroundCoverRestingOnBlock(World world, int i, int j, int k) {
		Block blockAbove = Block.blocksList[world.getBlockId(i, j + 1, k)];
		
		if (blockAbove != null) {
			if (blockAbove.isGroundCover()) {
				return true;
			}
			else if (blockAbove.groundCoverRestingOnVisualOffset(world, i, j + 1, k) < -0.99F) {
				Block block2Above = blockAbove = Block.blocksList[world.getBlockId(i, j + 2, k)];
				
				if (block2Above != null && block2Above.isGroundCover()) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	//----------- Client Side Functionality -----------//
	
	@Override
	@Environment(EnvType.CLIENT)
	public void clientBreakBlock(World world, int i, int j, int k, int iBlockID, int iMetadata) {
		// mark blocks up to 2 below for render update due to render offset
		
		world.markBlockRangeForRenderUpdate(i, j - 1, k, i, j - 2, k);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
		float fVisualOffset = 0F;
		
		int iBlockBelowID = world.getBlockId(i, j - 1, k);
		Block blockBelow = Block.blocksList[iBlockBelowID];
		
		if (blockBelow != null) {
			fVisualOffset = blockBelow.groundCoverRestingOnVisualOffset(world, i, j - 1, k);
		}
		
		return getBlockBoundsFromPoolBasedOnState(world, i, j, k).offset(i, j + fVisualOffset, k);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean renderBlock(RenderBlocks renderBlocks, int x, int y, int z) {
		IBlockAccess blockAccess = renderBlocks.blockAccess;
		
		// test to prevent momentary display above partial blocks that have just been destroyed
		
		if (blockAccess.getBlockId(x, y - 1, z) != 0) {
			float fVisualOffset = 0F;
			
			int iBlockBelowID = blockAccess.getBlockId(x, y - 1, z);
			Block blockBelow = Block.blocksList[iBlockBelowID];
			
			int iBlockHeight = BTWMod.enableSnowRework ? (blockAccess.getBlockMetadata(x, y, z) & 7) + 1 : 1;
			
			if (blockBelow != null) {
				fVisualOffset = blockBelow.groundCoverRestingOnVisualOffset(blockAccess, x, y - 1, z);
				
				if (fVisualOffset < 0.0F) {
					y -= 1;
					
					fVisualOffset += 1F;
				}
			}
			
			float fHeight = VISUAL_HEIGHT * iBlockHeight;
			
			renderBlocks.setRenderBounds(0F, fVisualOffset, 0F, 1F, fHeight + fVisualOffset, 1F);
			
			RenderUtils.renderStandardBlockWithTexture(renderBlocks, this, x, y, z, blockIcon);
		}
		
		return true;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide) {
		if (iSide >= 2) {
			// check for fully grown groth to side to eliminate a large number of faces
			
			if (blockAccess.getBlockId(iNeighborI, iNeighborJ, iNeighborK) == blockID) {
				BlockPos thisBlockPos = new BlockPos(iNeighborI, iNeighborJ, iNeighborK, Block.getOppositeFacing(iSide));
				
				// veryify we aren't a block that is visually offset by testing if the block we think we're in actually contains snow
				
				if (blockAccess.getBlockId(thisBlockPos.x, thisBlockPos.y, thisBlockPos.z) == blockID) {
					if (blockAccess.isBlockOpaqueCube(iNeighborI, iNeighborJ, iNeighborK)) {
						return false;
					}
					else
						//hiracho added check to see if same id has diff metadata, snow bigger metadata=higher snow and needs sides TODO move to snow?
					if (blockAccess.getBlockMetadata(thisBlockPos.x, thisBlockPos.y, thisBlockPos.z) <=
							blockAccess.getBlockMetadata(iNeighborI, iNeighborJ, iNeighborK)) {
						int iBlockBelowID = blockAccess.getBlockId(iNeighborI, iNeighborJ - 1, iNeighborK);
						
						if (iBlockBelowID != 0 &&
								blocksList[iBlockBelowID].groundCoverRestingOnVisualOffset(blockAccess, iNeighborI, iNeighborJ - 1, iNeighborK) > -0.01F) {
							return false;
						}
					}
				}
			}
			
			return true;
		}
		else if (iSide == 1) {
			return true;
		}
		
		return super.shouldSideBeRendered(blockAccess, iNeighborI, iNeighborJ, iNeighborK, iSide);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public boolean shouldRenderNeighborFullFaceSide(IBlockAccess blockAccess, int i, int j, int k, int iNeighborSide) {
		if (iNeighborSide == 1) {
			int iBlockBelowID = blockAccess.getBlockId(i, j - 1, k);
			
			if (iBlockBelowID != 0 && blocksList[iBlockBelowID].groundCoverRestingOnVisualOffset(blockAccess, i, j - 1, k) > -VISUAL_HEIGHT) {
				return false;
			}
		}
		
		return true;
	}
}