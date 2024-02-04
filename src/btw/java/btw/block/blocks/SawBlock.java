// FCMOD

package btw.block.blocks;

import java.util.List;
import java.util.Random;

import btw.block.BTWBlocks;
import btw.block.MechanicalBlock;
import btw.client.fx.BTWEffectManager;
import btw.client.render.util.RenderUtils;
import btw.crafting.manager.SawCraftingManager;
import btw.item.BTWItems;
import btw.util.CustomDamageSource;
import btw.block.util.MechPowerUtils;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class SawBlock extends Block
	implements MechanicalBlock
{
	private static final int POWER_CHANGE_TICK_RATE = 10;
	
	private static final int SAW_TIME_BASE_TICK_RATE = 20;
	private static final int SAW_TIME_TICK_RATE_VARIANCE = 4;

	// This base height prevents chickens slipping through grinders, while allowing items to pass
	
	public static final float BASE_HEIGHT = 1F - (4F / 16F );

	public static final float BLADE_LENGTH = (10F / 16F );
    public static final float BLADE_HALF_LENGTH = BLADE_LENGTH * 0.5F;
    
    public static final float BLADE_WIDTH = (0.25F / 16F );
    public static final float BLADE_HALF_WIDTH = BLADE_WIDTH * 0.5F;

    public static final float BLADE_HEIGHT = 1F - BASE_HEIGHT;
    
	public SawBlock(int iBlockID)
	{
        super( iBlockID, BTWBlocks.plankMaterial);

        setHardness( 2F );
        setAxesEffectiveOn(true);
        
        setBuoyancy(1F);
        
        initBlockBounds(0F, 0F, 0F, 1F, BASE_HEIGHT, 1F);
        
		setFireProperties(5, 20);
		
        setStepSound( soundWoodFootstep );
        
        setUnlocalizedName( "fcBlockSaw" );
        
        setTickRandomly( true );

        setCreativeTab( CreativeTabs.tabRedstone );
	}
	
	@Override
    public int tickRate( World world )
    {
    	return POWER_CHANGE_TICK_RATE;
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
	}
	
	@Override
    public void onBlockAdded( World world, int i, int j, int k )
    {
        super.onBlockAdded( world, i, j, k );

        // note that we can't validate if the update is required here as the block will have
        // its facing set after being added
        
        world.scheduleBlockUpdate(i, j, k, blockID, POWER_CHANGE_TICK_RATE);
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
		// reduce the collison box slightly to avoid vanilla cardinal point weirdness
		// with floating items
				
		float fBaseHeight = BASE_HEIGHT - (1.0F / 32.0F );
		
    	return getBlockBoundsFromPoolForBaseHeight(world, i, j, k, fBaseHeight).offset(i, j, k);
    }

    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getBlockBoundsFromPoolForBaseHeight(blockAccess, i, j, k, BASE_HEIGHT);
    }
    
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iBlockID )
    {
		if ( !world.isUpdatePendingThisTickForBlock(i, j, k, blockID) )
		{
			scheduleUpdateIfRequired(world, i, j, k);
		}
    }
    
	@Override
    public void updateTick( World world, int i, int j, int k, Random rand )
    {
    	boolean bReceivingPower = isInputtingMechanicalPower(world, i, j, k);
    	boolean bOn = isBlockOn(world, i, j, k);
    	
    	if ( bOn != bReceivingPower )
    	{
	        emitSawParticles(world, i, j, k, rand);
	        
    		setBlockOn(world, i, j, k, bReceivingPower);
    		
    		if ( bReceivingPower )
    		{
		        world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
		    		"minecart.base", 
		    		1F + ( rand.nextFloat() * 0.1F ),		// volume 
		    		1.5F + ( rand.nextFloat() * 0.1F ) );	// pitch
		        
    			// the saw doesn't cut on the update in which it is powered, so check if another
    			// update is required
    			
		        scheduleUpdateIfRequired(world, i, j, k);
    		}
    		else
    		{
		        world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
		    		"minecart.base", 
		    		1F + ( rand.nextFloat() * 0.1F ),		// volume 
		    		0.75F + ( rand.nextFloat() * 0.1F ) );	// pitch
    		}
    	}
    	else if ( bOn )
    	{
    		sawBlockToFront(world, i, j, k, rand);
    	}
    }
    
	@Override
    public void randomUpdateTick(World world, int i, int j, int k, Random rand)
    {
		// verify we have a tick already scheduled to prevent jams on chunk load
		
		if ( !world.isUpdateScheduledForBlock(i, j, k, blockID) )
		{
			scheduleUpdateIfRequired(world, i, j, k);
		}
    }

	@Override
    public void onEntityCollidedWithBlock( World world, int i, int j, int k, Entity entity )
    {
		if ( world.isRemote )
		{
			return;
		}
		
    	if ( isBlockOn(world, i, j, k) )
    	{
	    	if ( entity instanceof EntityLiving )
	    	{
	    		int iFacing = getFacing(world, i, j, k);
	    		
	    		// construct bounding box from saw
	    		
	            float fHalfLength = ( 10.0F / 16.0F ) * 0.5F;
	            float fHalfWidth = ( 0.25F / 16.0F ) * 0.5F;
	            float fBlockHeight = ( 4.0F / 16.0F );
	            
	            AxisAlignedBB sawBox;
	            
	            switch ( iFacing )
	            {	    	        
	            	case 0:
	    	        	
	    	        	sawBox = AxisAlignedBB.getAABBPool().getAABB( 0.5F - fHalfLength, 0.0F, 0.5F - fHalfWidth, 
    			    		0.5F + fHalfLength, fBlockHeight, 0.5F + fHalfWidth );		        
	    		        
	    	        	break;
	    	        	
	    	        case 1:        	
	    	        	
	    	        	sawBox = AxisAlignedBB.getAABBPool().getAABB( 0.5F - fHalfLength, 1.0F - fBlockHeight, 0.5F - fHalfWidth, 
    			    		0.5F + fHalfLength, 1.0F, 0.5F + fHalfWidth );
	    		        
	    		        break;
	    		        
	    	        case 2:
	    	        	
	    	        	sawBox = AxisAlignedBB.getAABBPool().getAABB(  0.5F - fHalfLength, 0.5F - fHalfWidth, 0.0F,   
    		        		0.5F + fHalfLength, 0.5F + fHalfWidth, fBlockHeight );
	    		        
	    	        	break;
	    	        	
	    	        case 3:
	    	        	
	    	        	sawBox = AxisAlignedBB.getAABBPool().getAABB( 0.5F - fHalfLength, 0.5F - fHalfWidth, 1.0F - fBlockHeight,  
    		        		0.5F + fHalfLength, 0.5F + fHalfWidth, 1.0F );
	    			        
	    	        	break;
	    	        	
	    	        case 4:
	    	        	
	    	        	sawBox = AxisAlignedBB.getAABBPool().getAABB( 0.0F, 0.5F - fHalfWidth, 0.5F - fHalfLength, 
    			    		fBlockHeight, 0.5F + fHalfWidth, 0.5F + fHalfLength );
	    		        
	    	        	break;
	    	        	
	    	        default: // 5
	    	        	
	    	        	sawBox = AxisAlignedBB.getAABBPool().getAABB( 1.0F - fBlockHeight, 0.5F - fHalfWidth,  0.5F - fHalfLength, 
    			    		1.0F, 0.5F + fHalfWidth, 0.5F + fHalfLength );
	    			        
	    	        	break;	        	
	            }
	            
	            sawBox = sawBox.getOffsetBoundingBox( (double)i, (double)j, (double)k );
	    	        	
	            List collisionList = null;
	            
	            collisionList = world.getEntitiesWithinAABB(net.minecraft.src.EntityLiving.class, sawBox );
	            
	            if ( collisionList != null && collisionList.size() > 0 )
	            {
	            	DamageSource source = CustomDamageSource.damageSourceSaw;
	            	int iDamage = 4;
	            	
	            	BlockPos targetPos = new BlockPos( i, j, k );
	            	
	            	targetPos.addFacingAsOffset(iFacing);
	            	
	            	int iTargetBlockID = world.getBlockId(targetPos.x, targetPos.y, targetPos.z);
	            	int iTargetMetadata = world.getBlockMetadata(targetPos.x, targetPos.y, targetPos.z);
	            	
	            	if ( iTargetBlockID == BTWBlocks.aestheticOpaque.blockID &&
	            		( iTargetMetadata == AestheticOpaqueBlock.SUBTYPE_CHOPPING_BLOCK_CLEAN || iTargetMetadata == AestheticOpaqueBlock.SUBTYPE_CHOPPING_BLOCK_DIRTY) )
	            	{
	            		source = CustomDamageSource.damageSourceChoppingBlock;
	            		iDamage *= 3;
	            		
	            		if ( iTargetMetadata == AestheticOpaqueBlock.SUBTYPE_CHOPPING_BLOCK_CLEAN)
	            		{
	            			world.setBlockMetadataWithNotify(targetPos.x, targetPos.y, targetPos.z, AestheticOpaqueBlock.SUBTYPE_CHOPPING_BLOCK_DIRTY);
	            		}
	            	}	            		
	            	
		            for( int iTempListIndex = 0; iTempListIndex < collisionList.size(); iTempListIndex++ )
		            {
		                EntityLiving tempTargetEntity = (EntityLiving)collisionList.get( iTempListIndex );
		                
			            if ( tempTargetEntity.attackEntityFrom( source, iDamage ) )
			    		{
			                world.playAuxSFX( BTWEffectManager.SAW_DAMAGE_EFFECT_ID, i, j, k, iFacing );
			    		}
		            }
	            }
	    	}
    	}
    }
    
	@Override
	public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
		return iFacing != getFacing(blockAccess, i, j, k);
	}
	
	@Override
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
		return Block.getOppositeFacing(iFacing) == getFacing(blockAccess, i, j, k);
	}
	
    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return 2; // iron or better
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, BTWItems.gear.itemID, 1, 0, fChanceOfDrop);
		dropItemsIndividually(world, i, j, k, Item.stick.itemID, 2, 0, fChanceOfDrop);
		dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, 3, 0, fChanceOfDrop);
		dropItemsIndividually(world, i, j, k, Item.ingotIron.itemID, 2, 0, fChanceOfDrop);
		dropItemsIndividually(world, i, j, k, BTWItems.ironNugget.itemID, 4, 0, fChanceOfDrop);
		dropItemsIndividually(world, i, j, k, BTWItems.leatherStrap.itemID, 3, 0, fChanceOfDrop);
		
		return true;
	}
	
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
		
		return iFacing != 0;
	}
	
	@Override
	public boolean canTransmitRotationVerticallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		int iFacing = getFacing(blockAccess, i, j, k);
		
		return iFacing != 0 && iFacing!= 1;
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

	@Override
	public boolean toggleFacing(World world, int i, int j, int k, boolean bReverse)
	{		
		int iFacing = getFacing(world, i, j, k);
		
		iFacing = Block.cycleFacing(iFacing, bReverse);

		setFacing(world, i, j, k, iFacing);
		
    	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
    	
    	world.notifyBlockChange( i, j, k, blockID );
    	
    	return true;
	}
	
    @Override
    public boolean isIncineratedInCrucible()
    {
    	return false;
    }
	
    //------------- Class Specific Methods ------------//    
	
	protected boolean isCurrentPowerStateValid(World world, int i, int j, int k)
	{
    	boolean bReceivingPower = isInputtingMechanicalPower(world, i, j, k);
    	boolean bOn = isBlockOn(world, i, j, k);
    	
    	return bOn == bReceivingPower;
	}
	
    public boolean isBlockOn(IBlockAccess iBlockAccess, int i, int j, int k)
    {
    	return ( iBlockAccess.getBlockMetadata( i, j, k ) & 8 ) > 0;    
	}
    
    public void setBlockOn(World world, int i, int j, int k, boolean bOn)
    {
    	int iMetaData = world.getBlockMetadata( i, j, k ) & 7; // filter out old on state
    	
    	if ( bOn )
    	{
    		iMetaData |= 8;
    	}
    	
        world.setBlockMetadataWithNotify( i, j, k, iMetaData );
    }
    
	protected void scheduleUpdateIfRequired(World world, int i, int j, int k)
	{
		if ( !isCurrentPowerStateValid(world, i, j, k) )
		{
	        world.scheduleBlockUpdate(i, j, k, blockID, POWER_CHANGE_TICK_RATE);
		}
		else if ( isBlockOn(world, i, j, k) )
		{
			// check if we have something to cut in front of us
			
    		int iFacing = getFacing(world, i, j, k);
    		BlockPos targetPos = new BlockPos( i, j, k, iFacing );
    		
			Block targetBlock = Block.blocksList[world.getBlockId(targetPos.x, targetPos.y,
																  targetPos.z)];
			int targetMetadata = world.getBlockMetadata(targetPos.x, targetPos.y, targetPos.z);
    		
    		if ( targetBlock != null && ( targetBlock.blockMaterial.isSolid() || 
    			SawCraftingManager.instance.getRecipe(targetBlock, targetMetadata) != null ||
    			targetBlock.doesBlockDropAsItemOnSaw(world, targetPos.x, targetPos.y, targetPos.z) ) )
    		{
		        world.playSoundEffect( i + 0.5D, j + 0.5D, k + 0.5D, "minecart.base", 
		    		1.5F + ( world.rand.nextFloat() * 0.1F ),		// volume 
		    		1.9F + ( world.rand.nextFloat() * 0.1F ) );		// pitch
		        
    			world.scheduleBlockUpdate(i, j, k, blockID, SAW_TIME_BASE_TICK_RATE +
															world.rand.nextInt(SAW_TIME_TICK_RATE_VARIANCE));
    		}	    		
		}
	}
	
    public AxisAlignedBB getBlockBoundsFromPoolForBaseHeight(IBlockAccess blockAccess, int i, int j, int k,
															 float fBaseHeight)
    {
        int iFacing = getFacing(blockAccess, i, j, k);
        
        switch ( iFacing )
        {
	        case 0:
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB(	0F, 1F - fBaseHeight, 0F, 1F, 1F, 1F );
	        	
	        case 1:
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB( 0F, 0F, 0F, 1F, fBaseHeight, 1F );
	        	
	        case 2:
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB( 0F, 0F, 1F - fBaseHeight, 
            		1F, 1F, 1F );
	        	
	        case 3:
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB( 0F, 0F, 0F, 1F, 1F, fBaseHeight );
	        	
	        case 4:
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB( 1F - fBaseHeight, 0F, 0F, 1F, 1F, 1F );
	        	
	        default: // 5
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB( 0F, 0F, 0F, fBaseHeight, 1F, 1F );
        }
    }
	
    void emitSawParticles(World world, int i, int j, int k, Random random)
    {
    	// FCTODO: I don't believe this is working as it's being called on the server
		int iFacing = getFacing(world, i, j, k);
		
		// compute position of saw blade
		
        float fBladeXPos = (float)i;
        float fBladeYPos = (float)j;
        float fBladeZPos = (float)k;
        
        float fBladeXExtent = 0.0f;
        float fBladeZExtent = 0.0f;
        
        switch ( iFacing )
        {	    	        
        	case 0:
	        	
        		fBladeXPos += 0.5f;
        		fBladeZPos += 0.5f;
        		
                fBladeXExtent = 1.0f;
                
	        	break;
	        	
	        case 1:        	
	        	
        		fBladeXPos += 0.5f;
        		fBladeZPos += 0.5f;
        		
        		fBladeYPos += 1.0f;
        		
                fBladeXExtent = 1.0f;
                
		        break;
		        
	        case 2:
	        	
        		fBladeXPos += 0.5f;
        		fBladeYPos += 0.5f;
        		
                fBladeXExtent = 1.0f;
                
	        	break;
	        	
	        case 3:
	        	
        		fBladeXPos += 0.5f;
        		fBladeYPos += 0.5f;
        		
        		fBladeZPos += 1.0f;
        		
                fBladeXExtent = 1.0f;
                
	        	break;
	        	
	        case 4:
	        	
        		fBladeYPos += 0.5f;
        		fBladeZPos += 0.5f;
        		
                fBladeZExtent = 1.0f;
                
	        	break;
	        	
	        default: // 5
	        	
        		fBladeYPos += 0.5f;
        		fBladeZPos += 0.5f;
        		
        		fBladeXPos += 1.0f;
        		
                fBladeZExtent = 1.0f;
                
	        	break;	        	
        }
        
        for ( int counter = 0; counter < 5; counter++ )
        {
            float smokeX = fBladeXPos + ( ( random.nextFloat() - 0.5f ) * fBladeXExtent );
            float smokeY = fBladeYPos + ( random.nextFloat() * 0.10f );
            float smokeZ = fBladeZPos + ( ( random.nextFloat() - 0.5f ) * fBladeZExtent );
            
            world.spawnParticle( "smoke", smokeX, smokeY, smokeZ, 0.0D, 0.0D, 0.0D );
        }
    }
    
	protected void sawBlockToFront(World world, int i, int j, int k, Random random)
	{
		int iFacing = getFacing(world, i, j, k);
		BlockPos targetPos = new BlockPos( i, j, k, iFacing );
		
		if ( !world.isAirBlock(targetPos.x, targetPos.y, targetPos.z) )
		{
			if ( !handleSawingExceptionCases(world, targetPos.x, targetPos.y, targetPos.z, i, j, k, iFacing, random) )
			{
	    		Block targetBlock = Block.blocksList[world.getBlockId(targetPos.x, targetPos.y, targetPos.z)];
	    		
	    		if ( targetBlock != null )
	    		{
	    			if ( targetBlock.doesBlockBreakSaw(world, targetPos.x, targetPos.y, targetPos.z) )
	    			{
	    				breakSaw(world, i, j, k);
	    			}
	    			else if ( targetBlock.onBlockSawed(world, targetPos.x, targetPos.y, targetPos.z, i, j, k) )
	    			{    				
				        emitSawParticles(world, targetPos.x, targetPos.y, targetPos.z, random);
	    			}
	    		}
			}
		}
	}
	
	/*
	 * returns true if an exception case is processed, false otherwise
	 */
    private boolean handleSawingExceptionCases(World world, int i, int j, int k, int iSawI, int iSawJ, int iSawK, int iSawFacing, Random random)
    {
    	//TODO: remove piston exception case to be able to completely remove this method
		int iTargetBlockID = world.getBlockId( i, j, k );
		
		if ( iTargetBlockID == Block.pistonMoving.blockID )
		{
			return true;
		}
		
		return false;
    }
    
	public void breakSaw(World world, int i, int j, int k)
	{
		dropComponentItemsOnBadBreak(world, i, j, k, world.getBlockMetadata(i, j, k), 1F);
		
        world.playAuxSFX( BTWEffectManager.MECHANICAL_DEVICE_EXPLODE_EFFECT_ID, i, j, k, 0 );
        
		world.setBlockWithNotify( i, j, k, 0 );
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
    	return MechPowerUtils.isBlockPoweredByAxle(world, i, j, k, this);
    }    

	@Override
	public boolean canInputAxlePowerToFacing(World world, int i, int j, int k, int iFacing)
	{
		int iBlockFacing = getFacing(world, i, j, k);
		
		return iFacing != iBlockFacing;
	}

	@Override
    public boolean isOutputtingMechanicalPower(World world, int i, int j, int k)
    {
    	return false;
    }
    
	@Override
	public void overpower(World world, int i, int j, int k)
	{
		breakSaw(world, i, j, k);
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconFront;
    @Environment(EnvType.CLIENT)
    private Icon iconBladeOff;
    @Environment(EnvType.CLIENT)
    private Icon iconBladeOn;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

		iconFront = register.registerIcon("fcBlockSaw_front");
		iconBladeOff = register.registerIcon("fcBlockSawBlade_off");
		iconBladeOn = register.registerIcon("fcBlockSawBlade_on");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
    	// used by item render
    	
        if ( iSide == 1 )
        {
        	return iconFront;
        }
        
        return blockIcon;
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
        
        return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick( World world, int i, int j, int k, Random random )
    {
    	if ( isBlockOn(world, i, j, k) )
    	{
    		emitSawParticles(world, i, j, k, random);
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

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
    	IBlockAccess blockAccess = renderer.blockAccess;
    	
        float fHalfLength = 0.5F;
        float fHalfWidth = 0.5F;
        float fBlockHeight = BASE_HEIGHT;
        
        int iFacing = getFacing(blockAccess, i, j, k);
      
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
        renderer.renderStandardBlock( this, i, j, k );
        
        // render the blade

        fHalfLength = ( 10.0F / 16.0F ) * 0.5F;
        fHalfWidth = ( 0.25F / 16.0F ) * 0.5F;
        fBlockHeight = ( 4.0F / 16.0F );
        
        switch ( iFacing )
        {
        	// the weirdness in facing 0 and 1 is due to this weird texture inversion thing that the rendering code
        	// does.
        	
	        case 0:
	        	
		        renderer.setRenderBounds( 0.5F - fHalfLength, 0.0F, 0.5F - fHalfWidth, 
			    		0.5F + fHalfLength, 0.999F/*fBlockHeight*/, 0.5F + fHalfWidth );		        
		        
		    	renderer.setUVRotateEast(3);
		    	renderer.setUVRotateWest(3);
		    	
		    	renderer.setUVRotateSouth(1);
		    	renderer.setUVRotateNorth(2);
		    	
		    	renderer.setUVRotateBottom(3);
		    	
	        	break;
	        	
	        case 1:        	
	        	
		        renderer.setRenderBounds( 0.5F - fHalfLength, 0.001F/*1.0F - fBlockHeight*/, 0.5F - fHalfWidth, 
			    		0.5F + fHalfLength, 1.0F, 0.5F + fHalfWidth );
		        
		    	renderer.setUVRotateSouth(2);
		    	renderer.setUVRotateNorth(1);
		    	
		        break;
		        
	        case 2:
	        	
		        renderer.setRenderBounds(  0.5F - fHalfLength, 0.5F - fHalfWidth, 0.0F,   
		        		0.5F + fHalfLength, 0.5F + fHalfWidth, fBlockHeight );
		        
		    	renderer.setUVRotateSouth(3);
		    	renderer.setUVRotateNorth(4);
		    	
		    	renderer.setUVRotateEast(3); // top
		    	renderer.setUVRotateWest(3); // bottom
		    	
	        	break;
	        	
	        case 3:
	        	
		        renderer.setRenderBounds( 0.5F - fHalfLength, 0.5F - fHalfWidth, 1.0F - fBlockHeight,  
		        		0.5F + fHalfLength, 0.5F + fHalfWidth, 1.0F );
			        
		    	renderer.setUVRotateSouth(4);
		    	renderer.setUVRotateNorth(3);
		    	
		    	renderer.setUVRotateTop(3);
		    	renderer.setUVRotateBottom(3);
		    	
	        	break;
	        	
	        case 4:
	        	
		        renderer.setRenderBounds( 0.0F, 0.5F - fHalfWidth, 0.5F - fHalfLength, 
			    		fBlockHeight, 0.5F + fHalfWidth, 0.5F + fHalfLength );
		        
		    	renderer.setUVRotateEast(4);
		    	renderer.setUVRotateWest(3);
		    	
		    	renderer.setUVRotateTop(2);
		    	renderer.setUVRotateBottom(1);
		    	
		    	renderer.setUVRotateNorth(3);
		    	renderer.setUVRotateSouth(4);
		    	
	        	break;
	        	
	        default: // 5
	        	
		        renderer.setRenderBounds( 1.0F - fBlockHeight, 0.5F - fHalfWidth,  0.5F - fHalfLength, 
			    		1.0F, 0.5F + fHalfWidth, 0.5F + fHalfLength );
			        
		    	renderer.setUVRotateEast(3);
		    	renderer.setUVRotateWest(4);
		    	
		    	renderer.setUVRotateTop(1);
		    	renderer.setUVRotateBottom(2);
		    	
		    	renderer.setUVRotateSouth(4);
		    	renderer.setUVRotateNorth(3);
		    	
	        	break;	        	
        }

        
        renderer.setRenderAllFaces(true);
        
        Icon bladeIcon = iconBladeOff;
        
        if ( isBlockOn(blockAccess, i, j, k) )
        {
        	bladeIcon = iconBladeOn;
        }
        
        RenderUtils.renderStandardBlockWithTexture(renderer, this, i, j, k, bladeIcon);
        
        renderer.setRenderAllFaces(false);

		renderer.clearUVRotation();
		
        return true;        
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
        renderBlocks.setRenderBounds(0.0F, 0.0F, 0.0F,
									 1.0F, BASE_HEIGHT, 1.0F);
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, this, -0.5F, -0.5F, -0.5F, 1);
        
        // render blade
        
        renderBlocks.setRenderBounds(0.5F - BLADE_HALF_LENGTH, 0.001F, 0.5F - BLADE_HALF_WIDTH,
									 0.5F + BLADE_HALF_LENGTH, 1.0F, 0.5F + BLADE_HALF_WIDTH);
        
        RenderUtils.renderInvBlockWithTexture(renderBlocks, this, -0.5F, -0.5F, -0.5F, iconBladeOff);
   }    
}