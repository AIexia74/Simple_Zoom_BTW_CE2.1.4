// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.tileentity.UnfiredBrickTileEntity;
import btw.client.render.util.RenderUtils;
import btw.item.BTWItems;
import btw.util.MiscUtils;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class UnfiredBrickBlock extends BlockContainer
{
	public static final float BRICK_HEIGHT = (4F / 16F );
	public static final float BRICK_WIDTH = (6F / 16F );
	public static final float BRICK_HALF_WIDTH = (BRICK_WIDTH / 2F );
	public static final float BRICK_LENGTH = (12F / 16F );
	public static final float BRICK_HALF_LENGTH = (BRICK_LENGTH / 2F );

    public UnfiredBrickBlock(int iBlockID )
    {
        super( iBlockID, Material.circuits );
        
        setHardness( 0F );
        setShovelsEffectiveOn(true);
        
        setStepSound( BTWBlocks.stepSoundSquish);
        
        setUnlocalizedName( "fcBlockUnfiredBrick" );
    }
    
	@Override
    public TileEntity createNewTileEntity(World world )
    {
        return new UnfiredBrickTileEntity();
    }

	@Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
        return setIAligned(iMetadata, isFacingIAligned(iFacing));
    }
    
	@Override
	public void onBlockPlacedBy( World world, int i, int j, int k, EntityLiving entityLiving, ItemStack stack )
	{
		int iFacing = MiscUtils.convertOrientationToFlatBlockFacingReversed(entityLiving);
		
		setIAligned(world, i, j, k, isFacingIAligned(iFacing));
	}	
    
	@Override
    public int idDropped( int iMetadata, Random random, int iFortuneModifier )
    {
		return Item.clay.itemID;
    }
	
	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }

	@Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool( World world, int i, int j, int k )
	{
		return null;
	}
	
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
		if ( getIsIAligned(blockAccess, i, j, k) )
		{
        	return AxisAlignedBB.getAABBPool().getAABB(
					(0.5F - BRICK_HALF_LENGTH), 0D, (0.5F - BRICK_HALF_WIDTH),
					(0.5F + BRICK_HALF_LENGTH), BRICK_HEIGHT, (0.5F + BRICK_HALF_WIDTH));
		}
		
    	return AxisAlignedBB.getAABBPool().getAABB(
				(0.5F - BRICK_HALF_WIDTH), 0D, (0.5F - BRICK_HALF_LENGTH),
				(0.5F + BRICK_HALF_WIDTH), BRICK_HEIGHT, (0.5F + BRICK_HALF_LENGTH));
    }
    
	@Override
    public boolean canPlaceBlockAt( World world, int i, int j, int k )
    {
		// don't allow drying bricks on leaves as it makes for really lame drying racks in trees 
		
		return WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j - 1, k, 1, true) &&
			world.getBlockId( i, j - 1, k ) != Block.leaves.blockID;
    }
    
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iBlockID )
    {
    	if ( !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j - 1, k, 1, true) )
    	{
            dropBlockAsItem(world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
            world.setBlockWithNotify(i, j, k, 0);
    	}
    }

    @Override
    public boolean canGroundCoverRestOnBlock(World world, int i, int j, int k)
    {
    	return world.doesBlockHaveSolidTopSurface( i, j - 1, k );
    }
    
    @Override
    public float groundCoverRestingOnVisualOffset(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return -1F;        
    }
    
	@Override
    public void onEntityCollidedWithBlock( World world, int i, int j, int k, Entity entity )
    {
		if ( !world.isRemote && !entity.isDead && entity instanceof EntityLiving && !( entity instanceof EntityAmbientCreature ) )
		{
			// note that this part can occasionally get slightly fooled by having both a bat and other living entity colliding with the larger block,
			// but only the bat (EntityAmbientCreature above) within the brick itself.  That's fine.
			
			List collisionList = world.getEntitiesWithinAABB(EntityLiving.class, getVisualBB(world, i, j, k));
			
	    	if ( collisionList != null && collisionList.size() > 0 )
	    	{			
		        world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
		        	stepSound.getStepSound(), ( stepSound.getStepVolume() + 1.0F ) / 2.0F, stepSound.getStepPitch() * 0.8F );
		        
	            dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
	            
	            world.setBlockWithNotify( i, j, k, 0 );
	    	}
		}
    }
	
	@Override
	public int getFacing(int iMetadata)
	{
		if ( getIsIAligned(iMetadata) )
		{
			return 4;
		}
		
		return 2;
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
		return setIAligned(iMetadata, isFacingIAligned(iFacing));
	}
	
	@Override
	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
	@Override
	public int rotateMetadataAroundJAxis(int iMetadata, boolean bReverse)
	{
		return setIAligned(iMetadata, !getIsIAligned(iMetadata));
	}
    
	//------------- Class Specific Methods ------------//
	
	public void onFinishedCooking(World world, int i, int j, int k)
	{
		int iMetadata = world.getBlockMetadata( i, j, k ) & 1; // preserve orientation
		
		world.setBlockAndMetadataWithNotify( i, j, k, BTWBlocks.placedBrick.blockID, iMetadata );
	}
	
	public void setIAligned(World world, int i, int j, int k, boolean bIAligned)
	{
		int iMetadata = setIAligned(world.getBlockMetadata(i, j, k), bIAligned);
		
		world.setBlockMetadataWithNotify( i, j, k, iMetadata );
	}
	
	public int setIAligned(int iMetadata, boolean bIAligned)
	{
		if ( bIAligned )
		{
			iMetadata |= 1;
		}
		else
		{
			iMetadata &= (~1);
		}
		
		return iMetadata;
	}
	
	public boolean getIsIAligned(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getIsIAligned(blockAccess.getBlockMetadata(i, j, k));
	}
	
	public boolean getIsIAligned(int iMetadata)
	{
		return ( iMetadata & 1 ) != 0;
	}
	
	public boolean isFacingIAligned(int iFacing)
	{
		return iFacing >= 4;
	}
	
	public void setCookLevel(World world, int i, int j, int k, int iCookLevel)
	{
		int iMetadata = setCookLevel(world.getBlockMetadata(i, j, k), iCookLevel);
		
		world.setBlockMetadataWithNotify( i, j, k, iMetadata );
	}
	
	public int setCookLevel(int iMetadata, int iCookLevel)
	{
		iMetadata &= 1; // filter out old state
		
		iMetadata |= iCookLevel << 1;
	
		return iMetadata;
	}
    
	public int getCookLevel(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getCookLevel(blockAccess.getBlockMetadata(i, j, k));
	}
	
	public int getCookLevel(int iMetadata)
	{
		return iMetadata >> 1;
	}
	
    public AxisAlignedBB getVisualBB(IBlockAccess blockAccess, int i, int j, int k)
    {
		if ( getIsIAligned(blockAccess, i, j, k) )
        {
	    	return AxisAlignedBB.getAABBPool().getAABB(
	    		i + (0.5F - BRICK_HALF_LENGTH), j, k + (0.5F - BRICK_HALF_WIDTH),
        		i + (0.5F + BRICK_HALF_LENGTH), j + BRICK_HEIGHT, k + (0.5F + BRICK_HALF_WIDTH));
        }
        else
        {
	    	return AxisAlignedBB.getAABBPool().getAABB(
	    		i + (0.5F - BRICK_HALF_WIDTH), j, k + (0.5F - BRICK_HALF_LENGTH),
        		i + (0.5F + BRICK_HALF_WIDTH), j + BRICK_HEIGHT, k + (0.5F + BRICK_HALF_LENGTH));
        }    	
    }
	
	//------ Client Only Functionality ------//

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked(World world, int x, int y, int z) {
		return BTWItems.unfiredBrick.itemID;
	}

    @Environment(EnvType.CLIENT)
    private Icon[] cookIcons;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

		cookIcons = new Icon[7];

        for ( int iTempIndex = 0; iTempIndex < 7; iTempIndex++ )
        {
			cookIcons[iTempIndex] = register.registerIcon("fcOverlayUnfiredBrick_" + (iTempIndex + 1 ));
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		if ( iSide == 0 )
		{
			return RenderUtils.shouldRenderNeighborFullFaceSide(blockAccess,
                                                                iNeighborI, iNeighborJ, iNeighborK, iSide);
		}
		
		return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks renderBlocks, int i, int j, int k, boolean bFirstPassResult)
    {
    	if ( bFirstPassResult )
    	{
        	IBlockAccess blockAccess = renderBlocks.blockAccess;
        	
	    	int iCookLevel = getCookLevel(blockAccess, i, j, k);
	    	
    		int iBlockBelowID = blockAccess.getBlockId( i, j - 1, k );
    		
    		if ( iBlockBelowID == BTWBlocks.kiln.blockID )
    		{
    			int iKilnCookLevel =
                        BTWBlocks.kiln.getCookCounter(blockAccess, i, j - 1, k) / 2;
    			
    			if ( iKilnCookLevel > iCookLevel )
    			{
    				iCookLevel = iKilnCookLevel;
    			}
    		}
    		
    		if ( iCookLevel > 0 && iCookLevel <= 7 )
    		{
        		renderBlockWithTexture(renderBlocks, i, j, k, cookIcons[iCookLevel - 1]);
    		}
    	}
    }
}