// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.MechanicalBlock;
import btw.client.fx.BTWEffectManager;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import btw.block.util.MechPowerUtils;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class BellowsBlock extends Block
	implements MechanicalBlock
{
	private static final int BELLOWS_TICK_RATE = 35;// 37 until 4.B00 b
	
	public static final float BELLOWS_CONTRACTED_HEIGHT = 1F - (5F / 16F );
	
	private static final double BLOW_ITEM_STRENGTH = 0.2D;
	private static final double PARTICLE_SPEED = 0.1F;
	
	public BellowsBlock(int iBlockID)
	{
        super( iBlockID, Material.wood );

        setHardness( 2F );
        setAxesEffectiveOn(true);
        
        setBuoyancy(1F);
        
    	initBlockBounds(0F, 0F, 0F, 1F, BELLOWS_CONTRACTED_HEIGHT, 1F);
    	
        setStepSound( soundWoodFootstep );
        
        setUnlocalizedName( "fcBlockBellows" );
        
        setTickRandomly( true );
        
        setCreativeTab( CreativeTabs.tabRedstone );
	}
	
	@Override
    public int tickRate( World world )
    {
    	return BELLOWS_TICK_RATE;
    }
    
	@Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
    	if ( iFacing < 2 )
    	{
    		iFacing = 2;
    	}
    	
        return setFacing(iMetadata, iFacing);
    }
    
	@Override
	public void onBlockPlacedBy( World world, int i, int j, int k, EntityLiving entityLiving, ItemStack stack )
	{
		int iFacing = MiscUtils.convertOrientationToFlatBlockFacingReversed(entityLiving);
		
		setFacing(world, i, j, k, iFacing);
	}
	
	@Override
    public void onBlockAdded( World world, int i, int j, int k )
    {
        super.onBlockAdded( world, i, j, k );
        
    	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
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
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
    	if ( isBlockMechanicalOn(blockAccess, i, j, k) )
    	{
        	return AxisAlignedBB.getAABBPool().getAABB(
					0F, 0F, 0F, 1F, BELLOWS_CONTRACTED_HEIGHT, 1F);
    	}
    	else
    	{
        	return AxisAlignedBB.getAABBPool().getAABB(         	
        		0F, 0F, 0F, 1F, 1F, 1F );
    	}
    }
    
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iNeighborBlockID )
    {
    	boolean bUpdateAlreadyScheduled = world.isUpdateScheduledForBlock(i, j, k, blockID);
    	
    	if ( !bUpdateAlreadyScheduled )
    	{
    		if (!isCurrentStateValid(world, i, j, k) &&
				!world.isUpdatePendingThisTickForBlock(i, j, k, blockID) )
    		{
    			world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
    			setIsContinuousMechanicalStateChange(world, i, j, k, true);
    		}
    	}
    	else
    	{
    		boolean bContinuousChange = isContinuousMechanicalStateChange(world, i, j, k);
    		
    		if ( bContinuousChange )
    		{
    			if ( isCurrentStateValid(world, i, j, k) )
    			{
        			// mechanical power has again changed state when we were already scheduled for an update
        			
        			setIsContinuousMechanicalStateChange(world, i, j, k, false);
        		}
    		}
    	}    	
    }
    
	@Override
    public void updateTick( World world, int i, int j, int k, Random random )
    {
    	boolean bReceivingMechanicalPower = isInputtingMechanicalPower(world, i, j, k);
    	boolean bMechanicalOn = isBlockMechanicalOn(world, i, j, k);
    	
    	boolean bContinuousChange = isContinuousMechanicalStateChange(world, i, j, k);
    	
    	if ( bMechanicalOn != bReceivingMechanicalPower )
    	{
    		if ( bContinuousChange )
    		{
	    		setIsContinuousMechanicalStateChange(world, i, j, k, false);
	    		setBlockMechanicalOn(world, i, j, k, bReceivingMechanicalPower);
	    		
		        if ( bReceivingMechanicalPower )
		        {
		        	blow(world, i, j, k);
		        }
		        else
		        {
		        	liftCollidingEntities(world, i, j, k);
		        }		        
    		}
    		else
    		{
    			// the Bellows is in the wrong state for the power it's receiving, but the power flow was interrupted during the transition
    			// schedule another block update to check again later
    			
    			world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );    			
	    		setIsContinuousMechanicalStateChange(world, i, j, k, true);
    		}
    	}
    	else
    	{
    		if ( bContinuousChange )
    		{
	    		setIsContinuousMechanicalStateChange(world, i, j, k, false);
    		}
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
    			setIsContinuousMechanicalStateChange(world, i, j, k, true);
			}
		}
    }
    
	@Override
	public int getFacing(int iMetadata)
	{
    	return ( iMetadata & 3 ) + 2;
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
    	iMetadata &= (~3);	// filter out old facing
    	
    	// this block only has 4 valid facings
    	
    	if ( iFacing >= 2 )
    	{
    		iFacing -= 2;
    	}
    	else
    	{
    		iFacing = 0;
    	}
    	
    	iMetadata |= iFacing;
    	
        return iMetadata;
	}
	
	@Override
	public boolean canRotateOnTurntable(IBlockAccess iBlockAccess, int i, int j, int k)
	{
		return true;
	}
	
	@Override
	public boolean rotateAroundJAxis(World world, int i, int j, int k, boolean bReverse)
	{
		if ( super.rotateAroundJAxis(world, i, j, k, bReverse) )
		{
	    	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
	    	
	    	MechPowerUtils.destroyHorizontallyAttachedAxles(world, i, j, k);
	    	
	        return true;
		}
		
		return false;
	}

    //------------- FCIBlockMechanical -------------//
    
	@Override
    public boolean canOutputMechanicalPower()
    {
    	return false;
    }

	@Override
    public boolean canInputMechanicalPower()
    {
    	return true;
    }

	@Override
    public boolean isInputtingMechanicalPower(World world, int i, int j, int k)
    {
    	return MechPowerUtils.isBlockPoweredByAxle(world, i, j, k, this) ||
			   MechPowerUtils.isBlockPoweredByHandCrank(world, i, j, k);
    }
	
	@Override
	public boolean canInputAxlePowerToFacing(World world, int i, int j, int k, int iFacing)
	{
		int iBlockFacing = getFacing(world, i, j, k);
		
		return iFacing != iBlockFacing && iFacing != 1;
	}

	@Override
    public boolean isOutputtingMechanicalPower(World world, int i, int j, int k)
    {
    	return false;
    }
    
	@Override
	public void overpower(World world, int i, int j, int k)
	{
		breakBellows(world, i, j, k);
	}
	
	//------------- Class Specific Methods ------------//
    
    public boolean isBlockMechanicalOn(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getIsBlockMechanicalOnFromMetadata(blockAccess.getBlockMetadata(i, j, k));
	}
    
    public void setBlockMechanicalOn(World world, int i, int j, int k, boolean bOn)
    {
    	int iMetadata = world.getBlockMetadata( i, j, k ) & (~4); // filter out old on state
    	
    	if ( bOn )
    	{
    		iMetadata |= 4;
    	}
    	
        world.setBlockMetadataWithNotify( i, j, k, iMetadata );
    }
    
    public boolean getIsBlockMechanicalOnFromMetadata(int iMetadata)
    {
    	return ( iMetadata & 4 ) > 0;
    }
    
    public boolean isContinuousMechanicalStateChange(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return ( blockAccess.getBlockMetadata( i, j, k ) & 8 ) > 0;    
	}
    
    public void setIsContinuousMechanicalStateChange(World world, int i, int j, int k, boolean bContinuous)
    {
    	int iMetadata = world.getBlockMetadata( i, j, k ) & (~8); // filter out old on state
    	
    	if ( bContinuous )
    	{
    		iMetadata |= 8;
    	}
    	
    	// no notify here as this is strictly internal state
    	
        world.setBlockMetadata( i, j, k, iMetadata );
    }
    
	public boolean isCurrentStateValid(World world, int i, int j, int k)
	{
    	boolean bReceivingMechanicalPower = isInputtingMechanicalPower(world, i, j, k);
    	boolean bMechanicalOn = isBlockMechanicalOn(world, i, j, k);
    	
		if ( bReceivingMechanicalPower != bMechanicalOn )
		{
			return false;
		}
		
		return true;
	}
	
	private void blow(World world, int i, int j, int k)
	{
		//EmitBellowsParticles( world, i, j, k, world.rand );
		
		stokeFiresInFront(world, i, j, k);
		
		blowLightItemsInFront(world, i, j, k);
	}
	
	private void stokeFiresInFront(World world, int i, int j, int k)
	{
		int iFacing = getFacing(world, i, j, k);
		int iFacingSide1 = Block.rotateFacingAroundY(iFacing, false);
		int iFacingSide2 = Block.rotateFacingAroundY(iFacing, true);
		
		BlockPos tempTargetPos = new BlockPos( i, j, k );
		
		// stoke fire
		
		for ( int iTempCount = 0; iTempCount < 3; iTempCount++ )
		{
			tempTargetPos.addFacingAsOffset(iFacing);
			
			int tempBlockID = world.getBlockId(tempTargetPos.x, tempTargetPos.y, tempTargetPos.z);
			
			if ( tempBlockID == Block.fire.blockID || tempBlockID == BTWBlocks.stokedFire.blockID )
			{
				stokeFire(world, tempTargetPos.x, tempTargetPos.y, tempTargetPos.z);
			}
			else if ( !world.isAirBlock(tempTargetPos.x, tempTargetPos.y, tempTargetPos.z) )
			{
				// line of sight to the next fire is blocked so drop out
				
				break;
			}
			
			// stoke one side
				
			BlockPos tempSidePos1 =  new BlockPos(tempTargetPos.x, tempTargetPos.y, tempTargetPos.z);
			
			tempSidePos1.addFacingAsOffset(iFacingSide1);
			
			tempBlockID = world.getBlockId(tempSidePos1.x, tempSidePos1.y, tempSidePos1.z);
			
			if ( tempBlockID == Block.fire.blockID || tempBlockID == BTWBlocks.stokedFire.blockID )
			{
				stokeFire(world, tempSidePos1.x, tempSidePos1.y, tempSidePos1.z);
			}
			
			// then the other
			
			BlockPos tempSidePos2 =  new BlockPos(tempTargetPos.x, tempTargetPos.y, tempTargetPos.z);
			
			tempSidePos2.addFacingAsOffset(iFacingSide2);
			
			tempBlockID = world.getBlockId(tempSidePos2.x, tempSidePos2.y, tempSidePos2.z);
			
			if ( tempBlockID == Block.fire.blockID || tempBlockID == BTWBlocks.stokedFire.blockID )
			{
				stokeFire(world, tempSidePos2.x, tempSidePos2.y, tempSidePos2.z);
			}
		}		
	}
	
	private void blowLightItemsInFront(World world, int i, int j, int k)
	{
		int iFacing = getFacing(world, i, j, k);
		
		BlockPos targetPos = new BlockPos( i, j, k );
			
		targetPos.addFacingAsOffset(iFacing);
		
        List collisionList = null;
        
    	int iBlowRange = computeBlowRange(world, i, j, k);

    	if ( iBlowRange > 0 )
    	{
	        AxisAlignedBB blowBox = createBlowBoundingBox(world, i, j, k, iBlowRange);
	        
	        if ( blowBox != null )
	        {        
		        // check for items within the blow zone       
		        
		        collisionList = world.getEntitiesWithinAABB( EntityItem.class, blowBox );
		
		    	if ( collisionList != null && collisionList.size() > 0 )
		    	{
		    		Vec3 blowVector = MiscUtils.convertBlockFacingToVector(iFacing);
		    		
		    		blowVector.xCoord *= BLOW_ITEM_STRENGTH;
		    		blowVector.yCoord *= BLOW_ITEM_STRENGTH;
		    		blowVector.zCoord *= BLOW_ITEM_STRENGTH;
		    		
		            for ( int listIndex = 0; listIndex < collisionList.size(); listIndex++ )
		            {
			    		EntityItem targetEntityItem = (EntityItem)collisionList.get( listIndex );
			    		
				        if ( !targetEntityItem.isDead )
				        {
				        	ItemStack stack = targetEntityItem.getEntityItem();
				        	int iItemBlowDistance = stack.getItem().getBellowsBlowDistance(stack.getItemDamage());
				        	
				        	if ( iItemBlowDistance > 0 && ( iItemBlowDistance >= iBlowRange ||
															isEntityWithinBlowRange(world, i, j, k, iItemBlowDistance, targetEntityItem) ) )
		        			{
				        		targetEntityItem.motionX += blowVector.xCoord;
				        		targetEntityItem.motionY += blowVector.yCoord;
				        		targetEntityItem.motionZ += blowVector.zCoord;
		        			}		        
				        }		        
		            }
		    	}
	        }
    	}
	}
	
	private boolean isEntityWithinBlowRange(World world, int i, int j, int k, int iBlowRange, Entity entity)
	{
		// FCTODO: Optimize this, it's brute force right now
		
        AxisAlignedBB blowBox = createBlowBoundingBox(world, i, j, k, iBlowRange);
        
        return blowBox.intersectsWith( entity.boundingBox );        
	}
	
    private AxisAlignedBB createBlowBoundingBox(World world, int i, int j, int k, int iBlowRange)
    {
    	AxisAlignedBB blowBox = null;
    	
    	if ( iBlowRange > 0 )
    	{
    		int iFacing = getFacing(world, i, j, k);
    		
    		BlockPos targetPos = new BlockPos( i, j, k );
			
    		targetPos.addFacingAsOffset(iFacing);
    		
    		blowBox = AxisAlignedBB.getAABBPool().getAABB((float)targetPos.x, (float)targetPos.y, (float)targetPos.z,
														  (float)(targetPos.x + 1), (float)(targetPos.y + 1), (float)(targetPos.z + 1));
    		
    		if ( iBlowRange > 1 )
    		{
	    		Vec3 blowVector = MiscUtils.convertBlockFacingToVector(iFacing);
	    		double dMultiplier = (double)( iBlowRange - 1 );
	    		
    			blowVector.xCoord *= dMultiplier;
    			blowVector.yCoord *= dMultiplier;
    			blowVector.zCoord *= dMultiplier;
	    		
    			blowBox = blowBox.addCoord( blowVector.xCoord, blowVector.yCoord, blowVector.zCoord );
    		}    		
    	}
    	
    	return blowBox;
    }

	private int computeBlowRange(World world, int i, int j, int k)
	{
		int iBlowRange = 0;
		int iFacing = getFacing(world, i, j, k);
		
		BlockPos targetPos = new BlockPos( i, j, k );
		
		for ( int iTempRange = 0; iTempRange < 3; iTempRange++ )
		{
			targetPos.addFacingAsOffset(iFacing);
			
			if ( canBlowThroughBlock(world, targetPos.x, targetPos.y, targetPos.z) )
			{
				iBlowRange++;
			}
			else
			{
				break;
			}
		}
		
		return iBlowRange;
	}
	
	private boolean canBlowThroughBlock(World world, int i, int j, int k)
	{
		if ( !world.isAirBlock( i, j, k ) )
		{
			int iBlockID = world.getBlockId( i, j, k );
			
			if ( iBlockID != Block.fire.blockID && iBlockID != BTWBlocks.stokedFire.blockID && iBlockID != Block.trapdoor.blockID )
			{
				if ( Block.blocksList[iBlockID].getCollisionBoundingBoxFromPool( world, i, j, k ) != null )
				{
					return false;
				}
			}
		}
		
		return true;
	}	
	
	private void stokeFire(World world, int i, int j, int k)
	{
		if ( world.getBlockId( i, j - 1, k ) == BTWBlocks.hibachi.blockID )
		{
			if ( world.getBlockId( i, j, k ) == BTWBlocks.stokedFire.blockID )
			{
				// reset the stoked fire's state to keep it going
				
				world.setBlockMetadata( i, j, k, 0 );
			}
			else
			{
				world.setBlockWithNotify( i, j, k, BTWBlocks.stokedFire.blockID );
			}
			
			// put regular fire above the stoked
			
			if ( world.isAirBlock( i, j + 1, k ) )
			{
	            world.setBlockWithNotify( i, j + 1, k, fire.blockID );
			}
		}
		else
		{
			// we don't have a hibachi below, extinguish the fire
			
			world.setBlockWithNotify( i, j, k, 0 );
		}
	}
	
	private void liftCollidingEntities(World world, int i, int j, int k)
	{
		List list = world.getEntitiesWithinAABBExcludingEntity( null, 
			AxisAlignedBB.getAABBPool().getAABB(
					(float)i, (float)j + BELLOWS_CONTRACTED_HEIGHT, (float)k,
					(float)( i + 1), (float)(j + 1), (float)(k + 1) ) );
		
		float extendedMaxY = (float)(j + 1);
            
        if ( list != null && list.size() > 0 )
        {
            for(int j1 = 0; j1 < list.size(); j1++)
            {
                Entity tempEntity = (Entity)list.get(j1);
   
                if( !tempEntity.isDead && ( tempEntity.canBePushed() || ( tempEntity instanceof EntityItem ) ) )
                {
                	double tempEntityMinY = tempEntity.boundingBox.minY;
                	
                	if ( tempEntityMinY < extendedMaxY )
                	{
        	    		double entityYOffset = extendedMaxY - tempEntityMinY;
        	    		
        	    		tempEntity.setPosition( tempEntity.posX, tempEntity.posY + entityYOffset, tempEntity.posZ );	    		
                	}
                }
            }
        }
	}
	
	public void breakBellows(World world, int i, int j, int k)
	{
		// drop wood siding
		
		for ( int iTemp = 0; iTemp < 2; iTemp++ )
		{
			ItemUtils.ejectSingleItemWithRandomOffset(world, i, j, k, BTWItems.woodSidingStubID, 0);
		}
		
		// drop gears
		
		for ( int iTemp = 0; iTemp < 1; iTemp++ )
		{
			ItemUtils.ejectSingleItemWithRandomOffset(world, i, j, k, BTWItems.gear.itemID, 0);
		}
		
		// drop leather straps
		
		for ( int iTemp = 0; iTemp < 2; iTemp++ )
		{
			ItemStack itemStack = new ItemStack( BTWItems.tannedLeather.itemID, 4, 0 );
			
			ItemUtils.ejectStackWithRandomOffset(world, i, j, k, itemStack);
		}
		
        world.playAuxSFX(BTWEffectManager.MECHANICAL_DEVICE_EXPLODE_EFFECT_ID, i, j, k, 0 );
        
		world.setBlockWithNotify( i, j, k, 0 );
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
        Icon sideIcon = register.registerIcon( "fcBlockBellows_side" );
        
        blockIcon = sideIcon; // for hit effects

		iconBySideArray[0] = register.registerIcon("fcBlockBellows_bottom");
		iconBySideArray[1] = register.registerIcon("fcBlockBellows_top");

		iconBySideArray[2] = sideIcon;
		iconBySideArray[3] = sideIcon;
		iconBySideArray[4] = sideIcon;
		iconBySideArray[5] = sideIcon;

		iconFront = register.registerIcon("fcBlockBellows_front");
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
        
        if ( iSide == iFacing )
        {
			return iconFront;
        }
        
		return iconBySideArray[iSide];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void clientNotificationOfMetadataChange(World world, int i, int j, int k, int iOldMetadata, int iNewMetadata)
	{
		if (!getIsBlockMechanicalOnFromMetadata(iOldMetadata) && getIsBlockMechanicalOnFromMetadata(iNewMetadata) )
		{
			// bellows power turn on
			
			blowLightItemsInFront(world, i, j, k);
			
            world.playSound( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, "mob.cow.say4", 
            		0.25F, world.rand.nextFloat() * 0.4F + 2F );	            
	        
            int iFacing = getFacing(iNewMetadata);
            
	        emitBellowsParticles(world, i, j, k, iFacing, world.rand);
		}
		else if (getIsBlockMechanicalOnFromMetadata(iOldMetadata) && !getIsBlockMechanicalOnFromMetadata(iNewMetadata) )
		{
			// bellows power turn off
			
        	liftCollidingEntities(world, i, j, k);
        	
            world.playSound( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, "mob.cow.say2", 
            		1.0F, world.rand.nextFloat() * 0.4F + 2F );
		}
	}

    @Environment(EnvType.CLIENT)
    private void emitBellowsParticles(World world, int i, int j, int k, int iFacing, Random random)
    {
		BlockPos targetPos = new BlockPos( i, j, k );
			
		targetPos.addFacingAsOffset(iFacing);
		
		Vec3 blowVector = MiscUtils.convertBlockFacingToVector(iFacing);
		
		blowVector.xCoord *= PARTICLE_SPEED;
		blowVector.yCoord *= PARTICLE_SPEED;
		blowVector.zCoord *= PARTICLE_SPEED;
		
        for ( int counter = 0; counter < 10; counter++ )
        {
            float smokeX = (float)targetPos.x + random.nextFloat();
            float smokeY = (float)targetPos.y + random.nextFloat() * 0.5F;
            float smokeZ = (float)targetPos.z + random.nextFloat();
            
            world.spawnParticle( "smoke", smokeX, smokeY, smokeZ, 
        		blowVector.xCoord + ( random.nextFloat() * 0.10F ) - 0.05F, 
        		blowVector.yCoord + ( random.nextFloat() * 0.10F ) - 0.05F, 
        		blowVector.zCoord + ( random.nextFloat() * 0.10F ) - 0.05F );
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