// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class VanillaSlabBlock extends BlockStep
{
	private static final int STEP_TYPE_STONE = 0;
	private static final int STEP_TYPE_SANDSTONE = 1;
	private static final int STEP_TYPE_WOOD = 2;
	private static final int STEP_TYPE_COBBLE = 3;
	private static final int STEP_TYPE_BRICK = 4;
	private static final int STEP_TYPE_STONE_BRICK = 5;
	private static final int STEP_TYPE_NETHER_BRICK = 6;
	private static final int STEP_TYPE_NETHER_QUARTZ = 7;
	
    public VanillaSlabBlock(int iBlockID, boolean bIsDoubleSlab )
    {
        super( iBlockID, bIsDoubleSlab );
    }
    
    @Override
    public int idDropped(int metadata, Random random, int fortune) {
    	if(metadata == STEP_TYPE_STONE) {
    		return BTWBlocks.stoneSlab.blockID;
    	}
    	else if(metadata == STEP_TYPE_COBBLE) {
    		return BTWBlocks.cobblestoneSlab.blockID;
    	}
    	else if(metadata == STEP_TYPE_STONE_BRICK) {
    		return BTWBlocks.stoneBrickSlab.blockID;
    	}
    	else return super.idDropped(metadata, random, fortune);
    }
    
    @Override
    public int damageDropped(int metadata) {
    	if(metadata == STEP_TYPE_STONE || metadata == STEP_TYPE_COBBLE || metadata == STEP_TYPE_STONE_BRICK) {
    		return 0;
    	}
       	else return super.damageDropped(metadata);
    }
	
    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	int iType = getBlockType(blockAccess, i, j, k);
    		
		if (iType == STEP_TYPE_SANDSTONE)
		{
			return 3; // diamond or better
		}
		else if (iType == STEP_TYPE_COBBLE || iType == STEP_TYPE_BRICK ||
				 iType == STEP_TYPE_STONE_BRICK || iType == STEP_TYPE_NETHER_BRICK)
		{
    		return 1000; // always convert, never harvest
		}
		
		return super.getHarvestToolLevel(blockAccess, i, j, k);
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		int iType = getBlockType(iMetadata);
		
		if (iType == STEP_TYPE_SANDSTONE)
		{
			if ( isDoubleSlab )
			{
				dropItemsIndividually(world, i, j, k, BTWItems.sandPile.itemID, 16, 0, fChanceOfDrop);
			}
			else
			{
				dropItemsIndividually(world, i, j, k, BTWItems.sandPile.itemID, 8, 0, fChanceOfDrop);
			}
			
			return true;
		}
		else if (iType == STEP_TYPE_COBBLE)
		{
			if ( isDoubleSlab )
			{
				dropItemsIndividually(world, i, j, k, BTWBlocks.looseCobblestone.blockID, 1, 0, fChanceOfDrop);
			}
			else
			{
				dropItemsIndividually(world, i, j, k, BTWBlocks.looseCobblestoneSlab.blockID, 1, 0, fChanceOfDrop);
			}
			
			return true;
		}
		else if (iType == STEP_TYPE_BRICK)
		{
			if ( isDoubleSlab )
			{
				dropItemsIndividually(world, i, j, k, BTWBlocks.looseBrick.blockID, 1, 0, fChanceOfDrop);
			}
			else
			{
				dropItemsIndividually(world, i, j, k, BTWBlocks.looseBrickSlab.blockID, 1, 0, fChanceOfDrop);
			}
			
			return true;
		}		
		else if (iType == STEP_TYPE_STONE_BRICK)
		{
			if ( isDoubleSlab )
			{
				dropItemsIndividually(world, i, j, k, BTWBlocks.looseStoneBrick.blockID, 1, 0, fChanceOfDrop);
			}
			else
			{
				dropItemsIndividually(world, i, j, k, BTWBlocks.looseStoneBrickSlab.blockID, 1, 0, fChanceOfDrop);
			}
			
			return true;
		}
		else if (iType == STEP_TYPE_NETHER_BRICK)
		{
			if ( isDoubleSlab )
			{
				dropItemsIndividually(world, i, j, k, BTWBlocks.looseNetherBrick.blockID, 1, 0, fChanceOfDrop);
			}
			else
			{
				dropItemsIndividually(world, i, j, k, BTWBlocks.looseNetherBrickSlab.blockID, 1, 0, fChanceOfDrop);
			}
			
			return true;
		}
		
		return false;
	}
	
    @Override
	public boolean hasContactPointToFullFace(IBlockAccess blockAccess, int i, int j, int k, int iFacing)
	{
    	if ( !isDoubleSlab  && iFacing < 2 )
    	{
        	boolean bIsUpsideDown = getIsUpsideDown(blockAccess, i, j, k);
        	
        	return bIsUpsideDown == ( iFacing == 1 );
    	}    	
    		
		return true;
	}
	
    @Override
	public boolean hasContactPointToSlabSideFace(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIsSlabUpsideDown)
	{
		return isDoubleSlab || bIsSlabUpsideDown == getIsUpsideDown(blockAccess, i, j, k);
	}
	
	@Override
	public boolean hasMortar(IBlockAccess blockAccess, int i, int j, int k)
	{
		int iType = getBlockType(blockAccess, i, j, k);
		
		return iType == STEP_TYPE_COBBLE || iType == STEP_TYPE_BRICK ||
			   iType == STEP_TYPE_STONE_BRICK || iType == STEP_TYPE_NETHER_BRICK;
	}
	
    @Override
    public boolean canMobsSpawnOn(World world, int i, int j, int k)
    {
    	int iType = getBlockType(world, i, j, k);
		
		if (iType == STEP_TYPE_WOOD)
		{
			return false;
		}
		else if (iType == STEP_TYPE_NETHER_BRICK)
		{
			return true;
		}
		
		return super.canMobsSpawnOn(world, i, j, k);
    }

	//------------- Class Specific Methods ------------//    

	public int getBlockType(int iMetadata)
	{
		return iMetadata & 7;
	}
	
	public int getBlockType(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getBlockType(blockAccess.getBlockMetadata(i, j, k));
	}
	
	//----------- Client Side Functionality -----------//
	
	@Override
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List) {
        if (par1 != Block.stoneDoubleSlab.blockID) {
        	 par3List.add(new ItemStack(par1, 1, 1));
        	 par3List.add(new ItemStack(par1, 1, 4));
        	 par3List.add(new ItemStack(par1, 1, 6));
        	 par3List.add(new ItemStack(par1, 1, 7));
        }
    }
}