// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.entity.MiningChargeEntity;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class MiningChargeBlock extends Block
{
	public final static double BOUNDING_BOX_HEIGHT = 0.5D;
	
	private final static int TICK_RATE = 1;
	
    public MiningChargeBlock(int iBlockID )
    {
        super( iBlockID, Material.tnt );
        
        setHardness( 0F );
        
		setFireProperties(Flammability.EXPLOSIVES);
		
    	initBlockBounds(0D, 0D, 0D, 1D, BOUNDING_BOX_HEIGHT, 1D);
    	
        setStepSound( soundGrassFootstep );
        
        setUnlocalizedName( "fcBlockMiningCharge" );
        
        setTickRandomly( true );        
		
		setCreativeTab( CreativeTabs.tabRedstone );
    }
    
	@Override
    public int tickRate( World world )
    {
        return TICK_RATE;
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
    public int quantityDropped( Random random )
    {
    	// this is to override standard drop behavior since the block may or may not ignite
    	// depending on how it is destroyed
		
        return 0;
    }
    
	@Override
    public boolean canPlaceBlockAt( World world, int i, int j, int k )
    {
		for ( int iTempFacing = 0; iTempFacing <= 5; iTempFacing++ )
		{
			if ( this.isValidAnchorToFacing(world, i, j, k, iTempFacing) )
			{
				return true;
			}
		}
		
        return false;
    }
    
	@Override
    public void onBlockAdded( World world, int i, int j, int k )
    {
    	super.onBlockAdded( world, i, j, k );
    	
    	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) ); 
    }
    
	@Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
        iFacing = Block.getOppositeFacing(iFacing);

        if ( !isValidAnchorToFacing(world, i, j, k, iFacing) )
        {
    		iFacing = 0;
    	
    		for ( int iTempFacing = 0; iTempFacing <= 5; iTempFacing++ )
    		{
    			if ( this.isValidAnchorToFacing(world, i, j, k, iTempFacing) )
    			{
    				iFacing = iTempFacing;
    				
    				break;
    			}
    		}    		
        }
        
    	return setFacing(iMetadata, iFacing);
    }
    
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
        int iFacing = getFacing(blockAccess, i, j, k);
        
        switch ( iFacing )
        {
	        case 0:
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB(
						0D, 0D, 0D, 1D, BOUNDING_BOX_HEIGHT, 1D);
	        	
	        case 1:
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB(
						0D, BOUNDING_BOX_HEIGHT, 0D, 1D, 1D, 1D);
	        	
	        case 2:
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB(
						0D, 0D, 0D, 1D, 1D, BOUNDING_BOX_HEIGHT);
	        	
	        case 3:
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB(
						0D, 0D, BOUNDING_BOX_HEIGHT, 1D, 1D, 1D);
	        	
	        case 4:
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB(
						0D, 0D, 0D, BOUNDING_BOX_HEIGHT, 1D, 1D);
	        	
	        default: // 5
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB(BOUNDING_BOX_HEIGHT, 0D, 0D, 1D, 1D, 1D);
        }    	
    }
    
	@Override
    public void onNeighborBlockChange(World world, int i, int j, int k, int iNeighborBlockID )
    {
    	if ( !world.isUpdatePendingThisTickForBlock(i, j, k, blockID) )
    	{
    		world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
    	}
    }

	@Override
    public void onBlockDestroyedByExplosion( World world, int i, int j, int k, Explosion explosion )
    {
		if ( world.isRemote )
		{
			return;
		}
		
    	int iFacing = getFacing(world, i, j, k);
    	
    	MiningChargeEntity entityMiningCharge = createPrimedEntity(world, i, j, k, iFacing);
    	
    	entityMiningCharge.fuse = 1;
    }

	@Override
    public void onBlockDestroyedByPlayer( World world, int i, int j, int k, int iMetaData )
    {
        if ( world.isRemote )
        {
            return;
        }
        
        dropBlockAsItem_do( world, i, j, k, new ItemStack( 
    		BTWBlocks.miningCharge.blockID, 1, 0 ) );
    }

	@Override
    public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
    {
    	ItemStack playerEquippedItem = player.getCurrentEquippedItem();
    	
        if ( playerEquippedItem != null && 
    		playerEquippedItem.itemID == Item.flintAndSteel.itemID)
        {
        	if ( !world.isRemote )
        	{
	        	int iMetaData = world.getBlockMetadata( i, j, k );
	        	
	        	world.setBlockWithNotify( i, j, k, 0 );
	        	
	        	createPrimedEntity(world, i, j, k, iMetaData);
        	}
        	
        	playerEquippedItem.damageItem( 1, player );
            
            return true;
        }
        else
        {        
        	return super.onBlockActivated( world, i, j, k, player, iFacing, fXClick, fYClick, fZClick );
        }
    }
    
	@Override
    public void updateTick( World world, int i, int j, int k, Random random )
    {
        if( isGettingRedstonePower(world, i, j, k) )
        {
        	int iMetaData = world.getBlockMetadata( i, j, k );
        	
            world.setBlockWithNotify( i, j, k, 0 );
            
            createPrimedEntity(world, i, j, k, iMetaData);
        }        
        else if ( !isValidAnchorToFacing(world, i, j, k, getFacing(world, i, j, k)) )
		{
            world.setBlockWithNotify( i, j, k, 0 );
            
            dropBlockAsItem_do( world, i, j, k, new ItemStack( 
            		BTWBlocks.miningCharge.blockID, 1, 0 ) );
		}
    }
    
	@Override
    public void onDestroyedByFire(World world, int i, int j, int k, int iFireAge, boolean bForcedFireSpread)
    {
		createPrimedEntity(world, i, j, k, world.getBlockMetadata(i, j, k));
		
		super.onDestroyedByFire(world, i, j, k, iFireAge, bForcedFireSpread);
    }
	
    //------------- FCIBlock ------------//
    
	@Override
	public int getFacing(int iMetadata)
	{
    	return iMetadata & 7;
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
    	iMetadata &= (~7);	// filter out old facing
    	
    	iMetadata |= iFacing;
    	
		return iMetadata;
	}
	
	@Override
	public boolean canRotateOnTurntable(IBlockAccess iBlockAccess, int i, int j, int k)
	{
		int iFacing = getFacing(iBlockAccess, i, j, k);
		
		if ( iFacing != 1  )
		{
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean rotateAroundJAxis(World world, int i, int j, int k, boolean bReverse)
	{
		if ( super.rotateAroundJAxis(world, i, j, k, bReverse) )
		{
	    	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
	    	
	    	return true;
		}
		
		return false;
	}

	@Override
	public boolean toggleFacing(World world, int i, int j, int k, boolean bReverse)
	{		
		int iFacing = getFacing(world, i, j, k);
		
		iFacing = Block.cycleFacing(iFacing, bReverse);

		setFacing(world, i, j, k, iFacing);
		
    	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
    	
    	return true;
	}
	
	//------------- Class Specific Methods ------------//
    
    public boolean isGettingRedstonePower(World world, int i, int j, int k)
    {
    	return world.isBlockIndirectlyGettingPowered( i, j, k );
    }
    
    public boolean isValidAnchorToFacing(World world, int i, int j, int k, int iFacing)
    {
        BlockPos anchorBlockPos = new BlockPos( i, j, k, iFacing );

        return WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world,
																	anchorBlockPos.x, anchorBlockPos.y, anchorBlockPos.z, getOppositeFacing(iFacing), true);
    }
    
    static public MiningChargeEntity createPrimedEntity(World world, int i, int j, int k, int iMetaData)
    {
        MiningChargeEntity entityMiningCharge = (MiningChargeEntity) EntityList.createEntityOfType(MiningChargeEntity.class,
    		world, i, j, k, BTWBlocks.miningCharge.getFacing(iMetaData));
            
        world.spawnEntityInWorld( entityMiningCharge );
        world.playSoundAtEntity( entityMiningCharge, "random.fuse", 1.0F, 1.0F );
        
        return entityMiningCharge;
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconBottom;
    @Environment(EnvType.CLIENT)
    private Icon iconTop;
    @Environment(EnvType.CLIENT)
    private Icon iconSide;
    @Environment(EnvType.CLIENT)
    private Icon iconSideFlipped;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		iconBottom = register.registerIcon("fcBlockMiningCharge_bottom");
		iconTop = register.registerIcon("fcBlockMiningCharge_top");
		iconSide = register.registerIcon("fcBlockMiningCharge_side");
		iconSideFlipped = register.registerIcon("fcBlockMiningCharge_side_vert");
        
        blockIcon = iconSide; // for hit effects
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
    	// for inv render
    	
    	if ( iSide <= 3 )
    	{
    		return iconSideFlipped;
    	}
    	else if ( iSide == 4 )
    	{
    		return iconTop;
    	}
    	else // 5
    	{
    		return iconBottom;
    	}
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture( IBlockAccess blockAccess, int i, int j, int k, int iSide )
    {
    	int iFacing = getFacing(blockAccess, i, j, k);
    	
    	if ( iFacing <= 1 )
    	{
        	if ( iSide <= 3 )
        	{
        		return iconSideFlipped;
        	}
        	else if ( iSide == 4 )
        	{
        		return iconTop;
        	}
        	else // 5
        	{
        		return iconBottom;
        	}        
    	}
    	else
    	{
        	if ( iSide == 0 )
        	{
        		return iconBottom;
    		}
        	else if ( iSide == 1 )
        	{
        		return iconTop;
        	}
        	else
        	{        	
        		return iconSide;
        	}
    	}    	
    }

    @Environment(EnvType.CLIENT)
    public Icon getBlockTextureFromMetadataCustom( int iSide, int iMetadata )
    {
    	// used by entity render
    	
    	int iFacing = getFacing(iMetadata);
    	
    	if ( iFacing <= 1 )
    	{
        	if ( iSide <= 3 )
        	{
        		return iconSideFlipped;
        	}
        	else if ( iSide == 4 )
        	{
        		return iconTop;
        	}
        	else // 5
        	{
        		return iconBottom;
        	}        
    	}
    	else
    	{
        	if ( iSide == 0 )
        	{
        		return iconBottom;
    		}
        	else if ( iSide == 1 )
        	{
        		return iconTop;
        	}
        	else
        	{        	
        		return iconSide;
        	}
    	}    	
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, 
    	int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(
			iNeighborI, iNeighborJ, iNeighborK, iSide);
    }
}