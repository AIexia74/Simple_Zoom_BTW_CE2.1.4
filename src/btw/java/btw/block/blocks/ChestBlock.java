// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.crafting.util.FurnaceBurnTime;
import btw.block.tileentity.ChestTileEntity;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class ChestBlock extends BlockChest
{
    public ChestBlock(int iBlockID)
    {
    	super( iBlockID, 0 );
    	
    	setBlockMaterial(BTWBlocks.plankMaterial);
    	
    	setHardness( 1.5F );    	
    	setAxesEffectiveOn();
    	
    	setBuoyant();
    	setFurnaceBurnTime(FurnaceBurnTime.WOOD_BASED_BLOCK);
    	
        initBlockBounds(0.0625F, 0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        
    	setStepSound( soundWoodFootstep );
    	
    	setUnlocalizedName( "chest" );    	
    }
    
    @Override
    public TileEntity createNewTileEntity(World world )
    {
        return new ChestTileEntity();
    }
    
	@Override
    public void setBlockBoundsBasedOnState( IBlockAccess blockAccess, int i, int j, int k )
    {
    	// override to deprecate parent
    }
	
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
        if ( blockAccess.getBlockId( i, j, k - 1 ) == blockID )
        {
        	return AxisAlignedBB.getAABBPool().getAABB( 
        		0.0625F, 0F, 0F, 
        		0.9375F, 0.875F, 0.9375F );
        }
        else if ( blockAccess.getBlockId( i, j, k + 1 ) == blockID )
        {
        	return AxisAlignedBB.getAABBPool().getAABB( 
        		0.0625F, 0.0F, 0.0625F, 
        		0.9375F, 0.875F, 1.0F );
        }
        else if ( blockAccess.getBlockId( i - 1, j, k ) == blockID )
        {
        	return AxisAlignedBB.getAABBPool().getAABB( 
        		0F, 0F, 0.0625F, 
        		0.9375F, 0.875F, 0.9375F );
        }
        else if ( blockAccess.getBlockId( i + 1, j, k ) == blockID )
        {
        	return AxisAlignedBB.getAABBPool().getAABB( 
        		0.0625F, 0F, 0.0625F, 
        		1F, 0.875F, 0.9375F );
        }
        else
        {
        	return AxisAlignedBB.getAABBPool().getAABB(
        		0.0625F, 0F, 0.0625F, 
        		0.9375F, 0.875F, 0.9375F );
        }        
    }
    
	@Override
    protected boolean canSilkHarvest( int iMetadata )
    {
        return true;
    }    
    
    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return 2; // iron or better
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, 6, 0, fChanceOfDrop);
		dropItemsIndividually(world, i, j, k, Item.stick.itemID, 2, 0, fChanceOfDrop);
		
		return true;
	}
	
	@Override
	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		// don't rotate if there's a neigboring chest
		
        if ( blockAccess.getBlockId( i - 1, j, k ) == blockID ||
        	blockAccess.getBlockId( i + 1, j, k ) == blockID ||
        	blockAccess.getBlockId( i, j, k - 1 ) == blockID ||
        	blockAccess.getBlockId( i, j, k + 1 ) == blockID )
        {
        	return false;
        }
        
        return true;
	}
	
	@Override
	public int rotateMetadataAroundJAxis(int iMetadata, boolean bReverse)
	{
		return Block.rotateFacingAroundY(iMetadata, bReverse);
	}

	@Override
    public boolean canSupportFallingBlocks(IBlockAccess blockAccess, int i, int j, int k)
    {
		// this is to prevent sand collapsing during worldgen
		
		return true;
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	return false;
    }    
}
