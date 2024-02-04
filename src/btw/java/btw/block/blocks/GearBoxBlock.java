// FCMOD

package btw.block.blocks;

import btw.BTWMod;
import btw.block.BTWBlocks;
import btw.block.util.Flammability;
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

public class GearBoxBlock extends Block
	implements MechanicalBlock
{
	static public final int TICK_RATE = 10;
	
	static private final int TURN_ON_TICK_RATE = 10;
	static private final int TURN_OFF_TICK_RATE = 9;
	
	public GearBoxBlock(int iBlockID)
	{
        super( iBlockID, BTWBlocks.plankMaterial);

        setHardness( 2F );
        
        setAxesEffectiveOn(true);
        
        setBuoyant();
		setFireProperties(Flammability.PLANKS);
        
        setStepSound( soundWoodFootstep );
        
        setTickRandomly( true );

        setUnlocalizedName( "fcBlockGearBox" );
        
        setCreativeTab( CreativeTabs.tabRedstone );
    }
	
	@Override
    public int tickRate( World world )
    {
    	return TICK_RATE;
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
        
    	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
    }

	@Override
    public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
    {
    	if ( player.getCurrentEquippedItem() == null && 
    		!MechPowerUtils.doesBlockHaveAnyFacingAxles(world, i, j, k) )
    	{
	        if ( !world.isRemote )
	        {
	        	toggleFacing(world, i, j, k, false);
	        	
	            MiscUtils.playPlaceSoundForBlock(world, i, j, k);
	        }
	        
	        return true;
    	}
    	
		return false;
    }
	
	@Override
    public void updateTick( World world, int i, int j, int k, Random rand )
    {
    	boolean bMechPowered = isInputtingMechanicalPower(world, i, j, k);
    	
    	updateMechPoweredState(world, i, j, k, bMechPowered);
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
    public void onNeighborBlockChange( World world, int i, int j, int k, int iBlockID )
    {
		// we have to verify that an update isn't pending as this may be called
		// by another block being updated, while an update is waiting to be processed
		// already this tick
		
		if (!isCurrentStateValid(world, i, j, k) &&
			!world.isUpdateScheduledForBlock(i, j, k, blockID) &&
			!world.isUpdatePendingThisTickForBlock(i, j, k, blockID) )
		{
	    	if (!BTWMod.disableGearBoxPowerDrain &&
				isGearBoxOn(world, i, j, k) )
			{
		    	// a Gear Box turns off slightly quicker than it turns on so that 
	    		// pulses of power bleed off with distance
		  
    			world.scheduleBlockUpdate(i, j, k, blockID, TURN_OFF_TICK_RATE);
			}
	    	else
	    	{		
	    		world.scheduleBlockUpdate(i, j, k, blockID, TURN_ON_TICK_RATE);
	    	}
		}
    }
	
	@Override
    public int getMechanicalPowerLevelProvidedToAxleAtFacing(World world, int i, int j, int k, int iFacing)
    {
		if (isGearBoxOn(world, i, j, k) &&
			getFacing(world, i, j, k) != iFacing )
		{
			return 4;
		}
		
    	return 0;
    }
	
    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return 2; // iron or better
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, Item.stick.itemID, 2, 0, fChanceOfDrop);
		dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, 3, 0, fChanceOfDrop);
		dropItemsIndividually(world, i, j, k, BTWItems.gear.itemID, 2, 0, fChanceOfDrop);
		
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
    	iMetadata &= 8; // filter out any old alignment
    	
    	iMetadata |= iFacing;
    	
		return iMetadata;
	}
	
	@Override
	public boolean rotateAroundJAxis(World world, int i, int j, int k, boolean bReverse)
	{
		int iFacing = getFacing(world, i, j, k);

		int iNewFacing = Block.rotateFacingAroundY(iFacing, bReverse);
		
		if ( iNewFacing != iFacing )
		{
	    	if ( isGearBoxOn(world, i, j, k) )
	    	{
	    		setGearBoxOn(world, i, j, k, false);
	    	}
	    	
			setFacing(world, i, j, k, iNewFacing);
			
	        world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
	        
	    	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
	    	
	    	MechPowerUtils.destroyHorizontallyAttachedAxles(world, i, j, k);
	    	
	    	return true;
    	}
		
		return false;
	}
	
	@Override
	public boolean toggleFacing(World world, int i, int j, int k, boolean bReverse)
	{		
    	if ( isGearBoxOn(world, i, j, k) )
    	{
    		setGearBoxOn(world, i, j, k, false);
    	}
    	
		int iFacing = getFacing(world, i, j, k);
		
		iFacing = Block.cycleFacing(iFacing, bReverse);

		setFacing(world, i, j, k, iFacing);
		
        world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
        
    	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
    	
    	world.notifyBlockChange( i, j, k, blockID );
    	
    	return true;    	
	}
	
    //------------- FCIBlockMechanical -------------//
    
	@Override
    public boolean canOutputMechanicalPower()
    {
    	return true;
    }

	@Override
    public boolean canInputMechanicalPower()
    {
    	return true;
    }

	@Override
    public boolean isInputtingMechanicalPower(World world, int i, int j, int k)
    {
    	return MechPowerUtils.isBlockPoweredByAxleToSide(world, i, j, k, getFacing(world, i, j, k));
    }    

	@Override
	public boolean canInputAxlePowerToFacing(World world, int i, int j, int k, int iFacing)
	{
		int iBlockFacing = getFacing(world, i, j, k);
		
		return iFacing == iBlockFacing;
	}

	@Override
    public boolean isOutputtingMechanicalPower(World world, int i, int j, int k)
    {
    	return isGearBoxOn(world, i, j, k);
    }
    
	@Override
	public void overpower(World world, int i, int j, int k)
	{
    	if ( isGearBoxOn(world, i, j, k) )
    	{
    		// an overpowered gearbox that doesn't have its gears disengaged, is destroyed
    		
    		breakGearBox(world, i, j, k);
    	}
	}
	
    //------------- Class Specific Methods ------------//
    
	protected void updateMechPoweredState(World world, int i, int j, int k, boolean bShouldBePowered)
	{
    	if (isGearBoxOn(world, i, j, k) != bShouldBePowered )
    	{
    		setGearBoxOn(world, i, j, k, bShouldBePowered);
    	}    	
	}
    
	protected boolean isCurrentStateValid(World world, int i, int j, int k)
	{
    	return isGearBoxOn(world, i, j, k) == isInputtingMechanicalPower(world, i, j, k);
	}
	
    public boolean isGearBoxOn(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return isGearBoxOn(blockAccess.getBlockMetadata(i, j, k));
    }
    
    public boolean isGearBoxOn(int iMetadata)
    {
    	return ( iMetadata & 8 ) > 0;
    }
    
    public int setGearBoxOn(int iMetadata, boolean bOn)
    {
    	iMetadata &= 7; // filter out any old on state
    	
    	if ( bOn )
    	{
    		iMetadata |= 8;
    	}
    	
    	return iMetadata;
    
    }
    
    public void setGearBoxOn(World world, int i, int j, int k, boolean bOn)
    {
    	int iMetadata = setGearBoxOn(world.getBlockMetadata(i, j, k), bOn);
    	
        world.setBlockMetadataWithNotify( i, j, k, iMetadata );
    }    
	
	public void breakGearBox(World world, int i, int j, int k)
	{
		dropComponentItemsOnBadBreak(world, i, j, k, world.getBlockMetadata(i, j, k), 1F);
		
        world.playAuxSFX( BTWEffectManager.MECHANICAL_DEVICE_EXPLODE_EFFECT_ID, i, j, k, 0 );
        
		world.setBlockWithNotify( i, j, k, 0 );
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconInput;
    @Environment(EnvType.CLIENT)
    private Icon iconOutput;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

		iconInput = register.registerIcon(getUnlocalizedName2() + "_input");
		iconOutput = register.registerIcon(getUnlocalizedName2() + "_output");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
    	// used by item render
		
        if ( iSide == 3 )
        {
        	return iconInput;
        }
        
        return blockIcon;    	
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture( IBlockAccess blockAccess, int i, int j, int k, int iSide )
    {
        int iFacing =  getFacing(blockAccess, i, j, k);
        
        if ( iSide == iFacing )
        {
        	return iconInput;
        }
        else
        {
        	BlockPos sideBlockPos = new BlockPos( i, j, k );
        	
        	sideBlockPos.addFacingAsOffset(iSide);
        	
        	if (blockAccess.getBlockId(sideBlockPos.x, sideBlockPos.y, sideBlockPos.z) ==
				BTWBlocks.axle.blockID )
        	{
        		if ( ( (AxleBlock) BTWBlocks.axle).isAxleOrientedTowardsFacing(
						blockAccess, sideBlockPos.x, sideBlockPos.y, sideBlockPos.z, iSide) )
        		{
        			return iconOutput;
        		}
        	}
        	
            if ( iSide == Block.getOppositeFacing(iFacing) )
            {
        		for ( int iTempFacing = 0; iTempFacing <= 5; iTempFacing++ )
        		{
        			if ( iTempFacing != iFacing && MechPowerUtils.doesBlockHaveFacingAxleToSide(
    					blockAccess, i, j, k, iTempFacing) )
        			{
        				// if the box has no output axles connected, the
        				// side opposite the input displays the output texture 
        				// to help with orienting the box
        				
            			return blockIcon;
        			}
        		}
        		
    			return iconOutput;
            }
        }
        
        return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick( World world, int i, int j, int k, Random random )
    {
    	if ( isGearBoxOn(world, i, j, k) )
    	{
    		emitGearBoxParticles(world, i, j, k, random);
    	}
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void clientNotificationOfMetadataChange(World world, int i, int j, int k, int iOldMetadata, int iNewMetadata)
	{
		if (!isGearBoxOn(iOldMetadata) && isGearBoxOn(iNewMetadata) )
		{
			// gear box mech power turn on
			
			world.playSound( i + 0.5D, j + 0.5D, k + 0.5D, 
	    		"random.chestopen", 0.25F, world.rand.nextFloat() * 0.25F + 0.25F);
	        
	        emitGearBoxParticles(world, i, j, k, world.rand);
		}
		
		// Render update all blocks at a distance surrounding the gear box that could be powered 
		// by it so that axles change their visual state appropriately
		
        world.markBlockRangeForRenderUpdate( i - 3, j - 3, k - 3, i + 3, j + 3, k + 3 );
	}

    @Override
    @Environment(EnvType.CLIENT)
    public void clientBreakBlock(World world, int i, int j, int k, int iBlockID, int iMetadata)
	{
        world.markBlockRangeForRenderUpdate( i - 3, j - 3, k - 3, i + 3, j + 3, k + 3 );
	}

    @Override
    @Environment(EnvType.CLIENT)
    public void clientBlockAdded(World world, int i, int j, int k)
	{
        world.markBlockRangeForRenderUpdate( i - 3, j - 3, k - 3, i + 3, j + 3, k + 3 );
	}

    @Environment(EnvType.CLIENT)
    private void emitGearBoxParticles(World world, int i, int j, int k, Random random)
    {
        for ( int iTempCount = 0; iTempCount < 5; iTempCount++ )
        {
            float smokeX = (float)i + random.nextFloat();
            float smokeY = (float)j + random.nextFloat() * 0.5F + 1.0F;
            float smokeZ = (float)k + random.nextFloat();
            
            world.spawnParticle( "smoke", smokeX, smokeY, smokeZ, 0D, 0D, 0D );
        }
    }
}