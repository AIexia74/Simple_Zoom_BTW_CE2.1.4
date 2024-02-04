// FCMOD

package btw.block.blocks;

import btw.BTWMod;
import btw.block.BTWBlocks;
import btw.block.tileentity.dispenser.BlockDispenserTileEntity;
import btw.client.fx.BTWEffectManager;
import btw.inventory.BTWContainers;
import btw.inventory.container.BlockDispenserContainer;
import btw.inventory.util.InventoryUtils;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class BlockDispenserBlock extends BlockContainer
{
	private static final int BLOCK_DISPENSER_TICK_RATE = 4;

    public BlockDispenserBlock(int iBlockID)
    {
        super( iBlockID, Material.rock );

        setHardness( 3.5F );
        
        setTickRandomly( true );
        
        setStepSound( Block.soundStoneFootstep );
        
        setUnlocalizedName( "fcBlockBlockDispenser" );
        
        setCreativeTab( CreativeTabs.tabRedstone );
    }

	@Override
    public int tickRate( World world )
    {
        return BLOCK_DISPENSER_TICK_RATE;
    }

	@Override
    public int idDropped( int i, Random random, int iFortuneModifier )
    {
        return BTWBlocks.blockDispenser.blockID;
    }

	@Override
    public void onBlockAdded( World world, int i, int j, int k ) 
	{
        world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );		
	}

	@Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
        return setFacing(iMetadata, Block.getOppositeFacing(iFacing));
    }
    
	@Override
	public void onBlockPlacedBy( World world, int i, int j, int k, EntityLiving entityLiving, ItemStack stack )
	{
		int iFacing = MiscUtils.convertPlacingEntityOrientationToBlockFacingReversed(entityLiving);
		
		setFacing(world, i, j, k, iFacing);
		
        world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
	}
	
	@Override
    public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
    {
        if ( !world.isRemote )
        {
            BlockDispenserTileEntity tileEntity = (BlockDispenserTileEntity)world.getBlockTileEntity( i, j, k );
            
        	if ( player instanceof EntityPlayerMP ) // should always be true
        	{
        		BlockDispenserContainer container = new BlockDispenserContainer( player.inventory, tileEntity );

        		BTWMod.serverOpenCustomInterface( (EntityPlayerMP)player, container, BTWContainers.blockDispenserContainerID);
        	}
        }
        
        return true;
    }

	@Override
    public TileEntity createNewTileEntity( World world )
    {
        return new BlockDispenserTileEntity();
    }
    
	@Override
    public void breakBlock( World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
        InventoryUtils.ejectInventoryContents(world, i, j, k, (IInventory)world.getBlockTileEntity(i, j, k));

        super.breakBlock( world, i, j, k, iBlockID, iMetadata );
    }

	@Override
    public void updateTick( World world, int i, int j, int k, Random random )
    {
    	validateBlockDispenser(world, i, j, k);
    	
    	boolean bIsPowered = isReceivingRedstonePower(world, i, j, k);
    	
    	if ( bIsPowered )
    	{
    		if ( !isRedstoneOn(world, i, j, k) )
    		{
    			setRedstoneOn(world, i, j, k, true);
    			
	        	dispenseBlockOrItem(world, i, j, k);
    		}
    	}
    	else
    	{
    		if ( isRedstoneOn(world, i, j, k) )
    		{
    			setRedstoneOn(world, i, j, k, false);
    			
	        	consumeFacingBlock(world, i, j, k);
    		}
    	}    	
    }
    
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iChangedBlockID )
    {
    	if (!isCurrentStateValid(world, i, j, k) &&
			!world.isUpdatePendingThisTickForBlock(i, j, k, blockID) )
    	{
    		world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
    	}
    }
    
	@Override
    public void randomUpdateTick(World world, int i, int j, int k, Random rand)
    {
		if ( !isCurrentStateValid(world, i, j, k) )
		{
			// verify we have a tick already scheduled to prevent jams on chunk load
			
			if ( !world.isUpdateScheduledForBlock(i, j, k, blockID) )
			{
		        world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );        
			}
		}
    }
	
	@Override
	public int getFacing(int iMetadata)
	{
    	return ( iMetadata & ~8 );
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
    	iMetadata &= 8;	// filter out old state
    	
    	iMetadata |= iFacing;
    	
        return iMetadata;
	}
	
	@Override
	public boolean rotateAroundJAxis(World world, int i, int j, int k, boolean bReverse)
	{
		if ( super.rotateAroundJAxis(world, i, j, k, bReverse) )
		{
			world.markBlockForUpdate( i, j, k );
			
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
		
        world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
        
        return true;
	}

	@Override
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

	@Override
    public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5)
    {
        return Container.calcRedstoneFromInventory(((IInventory) par1World.getBlockTileEntity(par2, par3, par4)));
    }
	
    //------------- Class Specific Methods ------------//
    
	public boolean isCurrentStateValid(World world, int i, int j, int k)
	{
		return isRedstoneOn(world, i, j, k) == isReceivingRedstonePower(world, i, j, k);
	}
	
    public boolean isRedstoneOn(World world, int i, int j, int k)
    {
        int iMetaData = world.getBlockMetadata( i, j, k );
        
        return ( ( iMetaData & 8 ) > 0 );
    }
    
    private void setRedstoneOn(World world, int i, int j, int k, boolean bOn)
    {
        int iMetaData = world.getBlockMetadata( i, j, k );
        
        if ( bOn )
        {
        	iMetaData = iMetaData | 8;
        }
        else
        {
        	iMetaData = iMetaData & (~8);
        }
        
        world.setBlockMetadataWithNotify( i, j, k, iMetaData );        
    }
    
    private boolean isReceivingRedstonePower(World world, int i, int j, int k)
    {
    	return ( world.isBlockGettingPowered( i, j, k ) || world.isBlockGettingPowered( i, j + 1, k ) );
    }
    
    private boolean addBlockToInventory(World world, int i, int j, int k,
										Block targetBlock, BlockPos targetPos)
    {
    	validateBlockDispenser(world, i, j, k);
    	
		ItemStack stack = targetBlock.getStackRetrievedByBlockDispenser(world,
																		targetPos.x, targetPos.y, targetPos.z);

		if ( stack != null )
		{
	        BlockDispenserTileEntity tileEntityDispenser =
	        	(BlockDispenserTileEntity)world.getBlockTileEntity( i, j, k );
	        
	        int iInitialSize = stack.stackSize;
	        
	        boolean bWholeStackAdded = InventoryUtils.addItemStackToInventory(
	        	tileEntityDispenser, stack);

	        // partial stack swallows count for the BD, with the excess items being lost
	        
			return bWholeStackAdded || stack.stackSize < iInitialSize;
		}
		
		return false;
    }
    
	private boolean consumeEntityAtTargetLoc(World world, int i, int j, int k, int targeti, int targetj, int targetk) {
		
		validateBlockDispenser(world, i, j, k);

		List list = null;

		list = world.getEntitiesWithinAABB(Entity.class,
				AxisAlignedBB.getAABBPool().getAABB((double) targeti, (double) targetj, (double) targetk,
						(double) (targeti + 1), (double) (targetj + 1), (double) (targetk + 1)));

		if (list != null && list.size() > 0) {
			
			BlockDispenserTileEntity tileEentityDispenser = (BlockDispenserTileEntity) world.getBlockTileEntity(i, j, k);

			for (int listIndex = 0; listIndex < list.size(); listIndex++) {
				
				Entity targetEntity = (Entity) list.get(listIndex);

				if (!targetEntity.isDead) {

					if (targetEntity.onBlockDispenserConsume(this, tileEentityDispenser)) {
						return true; // false means keep checking other entities, so no return
					}
				}
			}
		}

		return false;
	}
    
    private void consumeFacingBlock(World world, int i, int j, int k)
    {
        int iFacingDirection = getFacing(world, i, j, k);
        
        BlockPos targetPos = new BlockPos( i, j, k );
        
        targetPos.addFacingAsOffset(iFacingDirection);
        
        if ( !consumeEntityAtTargetLoc(world, i, j, k, targetPos.x, targetPos.y, targetPos.z) )
        {    	
	    	if ( !world.isAirBlock(targetPos.x, targetPos.y, targetPos.z) )
	    	{
		    	int iTargetBlockID = world.getBlockId(targetPos.x, targetPos.y, targetPos.z);
		    	
	    		Block targetBlock = Block.blocksList[iTargetBlockID];
	    		
	    		if ( targetBlock != null )
	    		{
    				int iTargetMetadata = world.getBlockMetadata(targetPos.x, targetPos.y, targetPos.z);
    				
					if (addBlockToInventory(world, i, j, k, targetBlock, targetPos) ||
						targetBlock.isBlockDestroyedByBlockDispenser(iTargetMetadata) )
    				{
						targetBlock.onRemovedByBlockDispenser(world,
															  targetPos.x, targetPos.y, targetPos.z);
    				}
	    		}
	    	}
        }
    }

    @Override
    public void onRemovedByBlockDispenser(World world, int i, int j, int k)
    {
		// destroy our inventory when swallowd by another BD to prevent exploits
		
        BlockDispenserTileEntity tileEntity =
        	(BlockDispenserTileEntity)world.getBlockTileEntity(
    		i, j, k );
        
        InventoryUtils.clearInventoryContents(tileEntity);
        
        super.onRemovedByBlockDispenser(world, i, j, k);
    }
    
    private boolean dispenseBlockOrItem(World world, int i, int j, int k)
    {
    	validateBlockDispenser(world, i, j, k);
    	
        int iFacing = getFacing(world, i, j, k);
        
        BlockPos targetPos = new BlockPos( i, j, k, iFacing );
        
        Block targetBlock = Block.blocksList[world.getBlockId(
				targetPos.x, targetPos.y, targetPos.z)];
        
        if ( targetBlock == null || targetBlock.blockMaterial.isReplaceable() || 
        	!targetBlock.blockMaterial.isSolid() )
        {
	        BlockDispenserTileEntity tileEntityBlockDispenser =
	        	(BlockDispenserTileEntity)world.getBlockTileEntity(i, j, k);
	        
	        ItemStack itemstack = tileEntityBlockDispenser.getCurrentItemToDispense();
	
	        if ( itemstack != null )
	        {
            	Block newBlock = null;
            	int iNewBlockMetadata = -1;
            	
            	if ( itemstack.getItem().onItemUsedByBlockDispenser(
            		itemstack, world, i, j, k, iFacing) )
    	        {
    		        world.playAuxSFX( BTWEffectManager.BLOCK_DISPENSER_SMOKE_EFFECT_ID,
    		        	i, j, k, iFacing );
    		        
    		        tileEntityBlockDispenser.onDispenseCurrentSlot();
    		        
    		        return true;
    	        }
	        }	        
        }
        
        world.playAuxSFX( BTWEffectManager.LOW_PITCH_CLICK_EFFECT_ID, i, j, k, 0 );

        return false;
    }
    
    // this function is necessary because of the conversion from using TileEntityBlockDispenser
    // to a custom tile entity for the block dispenser.  Returns false if the block isn't valid,
    // after it performs a conversion to it being a valid one
    private boolean validateBlockDispenser(World world, int i, int j, int k)
    {
    	TileEntity oldTileEntity = world.getBlockTileEntity( i, j, k );
    	
    	if ( !( oldTileEntity instanceof BlockDispenserTileEntity) )
    	{
			BlockDispenserTileEntity newTileEntity = new BlockDispenserTileEntity();
			
    		// the following condition should always be the case, but just to be safe...
    		
    		if ( oldTileEntity instanceof TileEntityDispenser )
    		{
    			// copy the inventory contents of the old entity into the new one
    			TileEntityDispenser oldTileEntityDispenser = (TileEntityDispenser)oldTileEntity;
    			int iOldInventorySize = oldTileEntityDispenser.getSizeInventory();
    			int iNewInventorySize = newTileEntity.getSizeInventory();
    			
    		    for ( int tempSlot = 0; tempSlot < iOldInventorySize && tempSlot < iNewInventorySize; tempSlot++ )
    		    {
    		    	ItemStack tempStack = oldTileEntityDispenser.getStackInSlot( tempSlot );
    		    	
    		    	if ( tempStack != null )
    		    	{
    		    		newTileEntity.setInventorySlotContents( tempSlot, tempStack.copy() );
    		    	}
    		    }
    		}

			world.setBlockTileEntity( i, j, k, newTileEntity );
			
    		return false;
    	}
    	
    	return true;
    }
    
    public void spitOutItem(World world, int i, int j, int k, ItemStack itemstack)
    {
        int iFacing = getFacing(world, i, j, k);
        BlockPos offsetPos = new BlockPos( 0, 0, 0, iFacing );
        
        double dXPos = i + (offsetPos.x * 0.5D ) + 0.5D;
        double dYPos = j + offsetPos.y + 0.2D;
        double dZPos = k + (offsetPos.z * 0.5D ) + 0.5D;

    	double dYHeading;
    	
    	if ( iFacing > 2 )
    	{
    		// slight upwards trajectory when fired sideways
    		
    		dYHeading = 0.1D;
    	}
    	else
    	{
    		dYHeading = offsetPos.y;
    	}
    	
        EntityItem entityitem = (EntityItem) EntityList.createEntityOfType(EntityItem.class, world, dXPos, dYPos, dZPos, itemstack );
        
        double dRandVel = world.rand.nextDouble() * 0.1D + 0.2D;
        
        entityitem.motionX = offsetPos.x * dRandVel;
        entityitem.motionY = dYHeading * dRandVel + 0.2D;
        entityitem.motionZ = offsetPos.z * dRandVel;
        
        entityitem.motionX += world.rand.nextGaussian() * 0.0075D * 6D;
        entityitem.motionY += world.rand.nextGaussian() * 0.0075D * 6D;
        entityitem.motionZ += world.rand.nextGaussian() * 0.0075D * 6D;
        
        world.spawnEntityInWorld( entityitem );
    }
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconBySideArray = new Icon[6];
    @Environment(EnvType.CLIENT)
    private Icon iconFront;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        Icon topIcon = register.registerIcon( "fcBlockBlockDispenser_top" );
        
        blockIcon = topIcon; // for hit effects

		iconFront = register.registerIcon("fcBlockBlockDispenser_front");

		iconBySideArray[0] = register.registerIcon("fcBlockBlockDispenser_bottom");
		iconBySideArray[1] = topIcon;
        
        Icon sideIcon = register.registerIcon( "fcBlockBlockDispenser_side" );

		iconBySideArray[2] = sideIcon;
		iconBySideArray[3] = sideIcon;
		iconBySideArray[4] = sideIcon;
		iconBySideArray[5] = sideIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		// item render
		
		if ( iSide == 3 )
		{
			return iconFront;
		}
		
		return iconBySideArray[iSide];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture( IBlockAccess blockAccess, int i, int j, int k, int iSide )
	{
		int iFacing = getFacing(blockAccess, i, j, k);
		
		if ( iFacing == iSide )
		{
			return iconFront;
		}
		
		return iconBySideArray[iSide];
	}
}