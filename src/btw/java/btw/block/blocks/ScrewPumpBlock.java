// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.block.FluidSource;
import btw.block.MechanicalBlock;
import btw.block.util.MechPowerUtils;
import btw.client.fx.BTWEffectManager;
import btw.item.BTWItems;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class ScrewPumpBlock extends Block
	implements MechanicalBlock, FluidSource
{
	static public final int TICK_RATE = 20;
	
    public ScrewPumpBlock(int iBlockID )
    {
        super( iBlockID, BTWBlocks.plankMaterial);

        setHardness( 2F );
        setResistance( 5F );
    	
        setAxesEffectiveOn(true);
        setBuoyancy(1F);

		setFireProperties(Flammability.PLANKS);
        
        setStepSound( soundWoodFootstep );
        setUnlocalizedName( "fcBlockScrewPump" );
        
        setTickRandomly( true );
        
	    setCreativeTab( CreativeTabs.tabRedstone );
    }
    
	@Override
    public int tickRate( World world )
    {
    	return TICK_RATE;
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
    public void onNeighborBlockChange( World world, int i, int j, int k, int iBlockID )
    {
		if ( !world.isUpdatePendingThisTickForBlock(i, j, k, blockID) )
		{
			world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
		}
    }
	
	@Override
    public void updateTick( World world, int i, int j, int k, Random random )
    {
		boolean bIsJammed = isJammed(world, i, j, k);
		
		if ( bIsJammed )
		{
    		BlockPos sourcePos = new BlockPos( i, j, k );
    		
    		sourcePos.addFacingAsOffset(getFacing(world, i, j, k));
    		
    		int iSourceBlockID = world.getBlockId(sourcePos.x, sourcePos.y, sourcePos.z);
    		
    		if ( iSourceBlockID != Block.waterMoving.blockID && iSourceBlockID != Block.waterStill.blockID )
    		{
    			// there is no longer any water at our input, so clear the jam
    			
    			setIsJammed(world, i, j, k, false);
    		}
		}		
		
    	boolean bReceivingPower = isInputtingMechanicalPower(world, i, j, k);
    	boolean bOn = isMechanicalOn(world, i, j, k);
    	
    	if ( bReceivingPower != bOn )
    	{
    		setMechanicalOn(world, i, j, k, bReceivingPower);
    		
	        world.markBlockForUpdate( i, j, k );
	        
			if ( isPumpingWater(world, i, j, k) )
			{
    			// we just turned on, schedule another update to start pumping 
				// (to give the impression the water has time to travel up)
    			
    			world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );    			
    		}
			
			if ( !bReceivingPower )
			{
				// clear any jams if we're turned off
				
				if ( isJammed(world, i, j, k) )
				{
	    			setIsJammed(world, i, j, k, false);
				}				
			}
    	}
    	else
    	{
    		if ( bOn )
    		{
    			if ( isPumpingWater(world, i, j, k) )
    			{
    				boolean bSourceValidated = false;
    				
    	    		int iTargetBlockID = world.getBlockId( i, j + 1, k );
    	    		
    	    		if ( iTargetBlockID == Block.waterMoving.blockID || iTargetBlockID == Block.waterStill.blockID )
    	    		{
    	    			if ( onNeighborChangeShortPumpSourceCheck(world, i, j, k) )
    	    			{
	    	    			int iTargetHeight = world.getBlockMetadata( i, j + 1, k );
	    	    			
	    	    			if ( iTargetHeight > 1 && iTargetHeight < 8 )
	    	    			{
	    	    				// gradually increase the fluid height until it maxes at 1
	    	    				
	        	    			world.setBlockAndMetadataWithNotify( i, j + 1, k, Block.waterMoving.blockID, iTargetHeight - 1 );
	        	    			
	        	    			// schedule another update to increase it further
	        	    			
	        	    			world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
	    	    			}
    	    			}
    	    			else
    	    			{
	    					setIsJammed(world, i, j, k, true);
    	    			}
    	    		}
    	    		else
    	    		{
    	    			// FCTODO: Break blocks here that water normally destroys
    	    			
    	    			if ( world.isAirBlock( i, j + 1, k ) )
    					{
    	    				if ( startPumpSourceCheck(world, i, j, k) )
    	    				{    	    				
		    	    			// start the water off at min height
		    	    			
		    	    			world.setBlockAndMetadataWithNotify( i, j + 1, k, Block.waterMoving.blockID, 7 );
		    	    			
		    	    			// schedule another update to increase it further
		    	    			
		    	    			world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
    	    				}
    	    				else
    	    				{
    	    					setIsJammed(world, i, j, k, true);
    	    				}
    					}
    	    		}
    			}
        		else
        		{
    	    		int iTargetBlockID = world.getBlockId( i, j + 1, k );
    	    		
    	    		if ( iTargetBlockID == Block.waterMoving.blockID || iTargetBlockID == Block.waterStill.blockID )
    	    		{
    	    			// if there is water above us, notify it that we are no longer pumping
    	    			
    	    			Block.blocksList[iTargetBlockID].onNeighborBlockChange( world, i, j + 1, k, blockID );
    	    		}
        		}
    		}
    	}
    }
	
	@Override
    public void randomUpdateTick(World world, int i, int j, int k, Random rand)
    {
    	boolean bWasJammed = isJammed(world, i, j, k);
    	boolean bIsJammed = bWasJammed;
    	boolean bMechanicalOn = isMechanicalOn(world, i, j, k);
    	boolean bReceivingPower = isInputtingMechanicalPower(world, i, j, k);
    	
    	if ( bReceivingPower != bMechanicalOn )
    	{
			// verify we have a tick already scheduled to prevent jams on chunk load
			
			if ( !world.isUpdateScheduledForBlock(i, j, k, blockID) )
			{
				world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
				
				return;
			}
    	}
    	
    	if ( bMechanicalOn )
    	{
			BlockPos sourcePos = new BlockPos( i, j, k );
			
			sourcePos.addFacingAsOffset(getFacing(world, i, j, k));
			
			int iSourceBlockID = world.getBlockId(sourcePos.x, sourcePos.y, sourcePos.z);
			
			if ( iSourceBlockID != Block.waterMoving.blockID && iSourceBlockID != Block.waterStill.blockID )
			{
				// there is no longer any water at our input, so clear any jams
		
				bIsJammed = false;
			}
			else
			{
				int iDistanceToCheck = getRandomDistanceForSourceCheck(rand);
				
				bIsJammed = !MiscUtils.doesWaterHaveValidSource(world, sourcePos.x, sourcePos.y, sourcePos.z, iDistanceToCheck);
				
		    	if ( !bIsJammed && bWasJammed )
		    	{
					// schedule an update to start pumping again

	    			world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
		    	}		    		
			}
    	}
    	else
    	{
    		bIsJammed = false;
    	}
    	
    	if ( bWasJammed != bIsJammed )
    	{
			setIsJammed(world, i, j, k, bIsJammed);
    	}    	
    }
    
    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return 2; // iron or better
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, BTWItems.screw.itemID, 1, 0, fChanceOfDrop);
		dropItemsIndividually(world, i, j, k, Item.stick.itemID, 4, 0, fChanceOfDrop);
		dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, 4, 0, fChanceOfDrop);
		
		return true;
	}
	
	@Override
	public int getFacing(int iMetadata)
	{
    	return ( iMetadata & 3 ) + 2;
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
    	iMetadata &= ~3; // filter out old facing
    	
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
    public boolean isIncineratedInCrucible()
    {
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
    	return MechPowerUtils.isBlockPoweredByAxleToSide(world, i, j, k, 0);
    }    

	public boolean canInputAxlePowerToFacing(World world, int i, int j, int k, int iFacing)
	{
		return iFacing == 0;
	}
	
	@Override
    public boolean isOutputtingMechanicalPower(World world, int i, int j, int k)
    {
    	return false;
    }
    
	@Override
	public void overpower(World world, int i, int j, int k)
	{
		breakScrewPump(world, i, j, k);
	}	
	
    //------------- FCIBlockFluidSource ------------//
	
	@Override
	public int isSourceToFluidBlockAtFacing(World world, int i, int j, int k, int iFacing)
	{
		if ( iFacing == 1 )
		{
			if ( isPumpingWater(world, i, j, k) )
			{
	    		int iTargetBlockID = world.getBlockId( i, j + 1, k );
	    		
	    		if ( iTargetBlockID == Block.waterMoving.blockID || iTargetBlockID == Block.waterStill.blockID )
	    		{
	    			int iSourceHeight = 0;
	    			
	    			int iTargetHeight = world.getBlockMetadata( i, j + 1, k );
	    			
	    			if ( iTargetHeight > 0 && iTargetHeight < 8 )
	    			{
	    				// return the fluid blocks height + 1 so that the pump can gradually increase the height of the water block above it
	    				
	    				iSourceHeight = iTargetHeight - 1; 
	    			}	    			
	    			
	    			return iSourceHeight;
	    		}	    		
			}
		}
		
		return -1;
	}
	
	//------------- Class Specific Methods ------------//
    
    public boolean isMechanicalOn(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return ( blockAccess.getBlockMetadata( i, j, k ) & 4 ) > 0;    
	}
    
    public void setMechanicalOn(World world, int i, int j, int k, boolean bOn)
    {
    	int iMetadata = world.getBlockMetadata( i, j, k ) & (~4); // filter out old on state
    	
    	if ( bOn )
    	{
    		iMetadata |= 4;
    	}
    	
        world.setBlockMetadataWithNotify( i, j, k, iMetadata );
    }
    
    public boolean isJammed(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return ( blockAccess.getBlockMetadata( i, j, k ) & 8 ) > 0;    
	}
    
    public void setIsJammed(World world, int i, int j, int k, boolean bJammed)
    {
    	int iMetadata = world.getBlockMetadata( i, j, k ) & (~8); // filter out old on state
    	
    	if ( bJammed )
    	{
    		iMetadata |= 8;
    	}
    	
        world.setBlockMetadataWithNotify( i, j, k, iMetadata );
    }
    
    public boolean isPumpingWater(World world, int i, int j, int k)
    {
    	if (isMechanicalOn(world, i, j, k) && !isJammed(world, i, j, k) )
    	{
    		BlockPos sourcePos = new BlockPos( i, j, k );
    		
    		sourcePos.addFacingAsOffset(getFacing(world, i, j, k));
    		
    		int iSourceBlockID = world.getBlockId(sourcePos.x, sourcePos.y, sourcePos.z);
    		
    		if ( iSourceBlockID == Block.waterMoving.blockID || iSourceBlockID == Block.waterStill.blockID )
    		{
    			return true;
    		}    		
    	}
    	
    	return false;
    }
    
    private boolean startPumpSourceCheck(World world, int i, int j, int k)
    {
    	// initial source check to prevent any dickery with getting pumps started with temporary water
    	
		BlockPos sourcePos = new BlockPos( i, j, k );
		
		sourcePos.addFacingAsOffset(getFacing(world, i, j, k));
		
		int iSourceBlockID = world.getBlockId(sourcePos.x, sourcePos.y, sourcePos.z);
		
		if ( iSourceBlockID == Block.waterMoving.blockID || iSourceBlockID == Block.waterStill.blockID )
		{
			int iDistanceToCheck = 128;
			
			return MiscUtils.doesWaterHaveValidSource(world, sourcePos.x, sourcePos.y, sourcePos.z, iDistanceToCheck);
		}
		
    	return false;
    }
    
    private boolean onNeighborChangeShortPumpSourceCheck(World world, int i, int j, int k)
    {
    	// this test just checks for an immediate infinite loop with the pump itself
    	
		BlockPos sourcePos = new BlockPos( i, j, k );
		
		sourcePos.addFacingAsOffset(getFacing(world, i, j, k));
		
		int iSourceBlockID = world.getBlockId(sourcePos.x, sourcePos.y, sourcePos.z);
		
		if ( iSourceBlockID == Block.waterMoving.blockID || iSourceBlockID == Block.waterStill.blockID )
		{
			int iDistanceToCheck = 4;
			
			return MiscUtils.doesWaterHaveValidSource(world, sourcePos.x, sourcePos.y, sourcePos.z, iDistanceToCheck);
		}
		
    	return false;
    }
    
    private int getRandomDistanceForSourceCheck(Random rand)
    {
		// Select random distance here, favoring the lower end to save on performance
		
		int iDistanceToCheck = 32;
		int iRandomFactor = rand.nextInt( 32 );
		
		if ( iRandomFactor == 0 )
		{
			// this is the maximum distance at which the user could conceivably construct 
			// an efficient infinite water loop (world height * 8 )
			
			iDistanceToCheck = 512;
		}
		else if ( iRandomFactor <= 2 )
		{
			iDistanceToCheck = 256;
		}
		else if ( iRandomFactor <= 6 )
		{
			iDistanceToCheck = 128;
		}
		else if ( iRandomFactor <= 14 )
		{
			iDistanceToCheck = 64;
		}
		
		return iDistanceToCheck;		
    }
    
	private void breakScrewPump(World world, int i, int j, int k)
	{
		dropComponentItemsOnBadBreak(world, i, j, k, world.getBlockMetadata(i, j, k), 1F);
		
        world.playAuxSFX( BTWEffectManager.MECHANICAL_DEVICE_EXPLODE_EFFECT_ID, i, j, k, 0 );
        
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
        Icon sideIcon = register.registerIcon( "fcBlockScrewPump_side" );
        
        blockIcon = sideIcon; // for hit effects        

		iconFront = register.registerIcon("fcBlockScrewPump_front");

		iconBySideArray[0] = register.registerIcon("fcBlockScrewPump_bottom");
		iconBySideArray[1] = register.registerIcon("fcBlockScrewPump_top");

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
		
		return iconBySideArray[iSide];    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick( World world, int i, int j, int k, Random random )
    {
    	if ( isMechanicalOn(world, i, j, k) )
    	{
    		emitPoweredParticles(world, i, j, k, random);
    	}
    }

    @Environment(EnvType.CLIENT)
    public void emitPoweredParticles(World world, int i, int j, int k, Random random)
	{
		int iBlockAboveID = world.getBlockId( i, j + 1, k );
		
		if ( iBlockAboveID == Block.waterMoving.blockID || iBlockAboveID == Block.waterStill.blockID )
		{
	        for ( int counter = 0; counter < 5; counter++ )
	        {
	            float smokeX = (float)i + random.nextFloat();
	            float smokeY = (float)j + random.nextFloat() * 0.10F + 1.0F;
	            float smokeZ = (float)k + random.nextFloat();
	            
	            world.spawnParticle( "bubble", smokeX, smokeY, smokeZ, 0.0D, 0.0D, 0.0D );
	        }
		}
		else
		{
	        for ( int counter = 0; counter < 5; counter++ )
	        {
	            float smokeX = (float)i + random.nextFloat();
	            float smokeY = (float)j + random.nextFloat() * 0.5F + 1.0F;
	            float smokeZ = (float)k + random.nextFloat();
	            
	            world.spawnParticle( "smoke", smokeX, smokeY, smokeZ, 0.0D, 0.0D, 0.0D );
	        }
		}
	}
}