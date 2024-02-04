// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.crafting.util.FurnaceBurnTime;
import btw.block.model.BlockModel;
import btw.block.model.CampfireModel;
import btw.block.tileentity.CampfireTileEntity;
import btw.client.render.util.RenderUtils;
import btw.crafting.manager.CampfireCraftingManager;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import btw.util.MiscUtils;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class CampfireBlock extends BlockContainer
{
	public final int fireLevel;
	
	public static final int CAMPFIRE_FUEL_STATE_NORMAL = 0;
	public static final int CAMPFIRE_FUEL_STATE_BURNED_OUT = 1;
	public static final int CAMPFIRE_FUEL_STATE_SMOULDERING = 2;
	
    private CampfireModel modelCampfire = new CampfireModel();
    
    private BlockModel modelCollisionBase;
    private BlockModel modelCollisionWithSpit;
    
    public static CampfireBlock[] fireLevelBlockArray = new CampfireBlock[4];
    
    public static boolean campfireChangingState = false; // temporarily true when block is being changed from one block ID to another
	
    private static final float SPIT_THICKNESS = (1F / 16F );
    private static final float HALF_SPIT_THICKNESS = (SPIT_THICKNESS / 2F );
    private static final float SPIT_HEIGHT = (12F / 16F );
    private static final float SPIT_MIN_Y = (SPIT_HEIGHT - HALF_SPIT_THICKNESS);
    private static final float SPIT_MAX_Y = (SPIT_MIN_Y + SPIT_THICKNESS);
    
    private static final float SPIT_SUPPORT_WIDTH = (1F / 16F );
    private static final float HALF_SPIT_SUPPORT_WIDTH = (SPIT_SUPPORT_WIDTH / 2F );
    private static final float SPIT_SUPPORT_BORDER = (0.5F / 16F );
    
    private static final float SPIT_FORK_WIDTH = (1F / 16F );
    private static final float SPIT_FORK_HEIGHT = (3F / 16F );
    private static final float SPIT_FORK_HEIGHT_OFFSET = (1F / 16F );
    private static final float SPIT_FORK_MIN_Y = (SPIT_MIN_Y - SPIT_FORK_HEIGHT_OFFSET);
    private static final float SPIT_FORK_MAX_Y = (SPIT_FORK_MIN_Y + SPIT_FORK_HEIGHT);
    
    private static final double SPIT_COLLISION_HEIGHT = (SPIT_HEIGHT + 1.5D / 16D );
    private static final double SPIT_COLLISION_WIDTH = (3D / 16D );
    private static final double SPIT_COLLISION_HALF_WIDTH = (SPIT_COLLISION_WIDTH / 2D );
    
    public CampfireBlock(int iBlockID, int iFireLevel )
    {
        super( iBlockID, Material.circuits );

		fireLevel = iFireLevel;

		fireLevelBlockArray[iFireLevel] = this;
        
        setHardness( 0.1F );        
		
        setBuoyant();
    	setFurnaceBurnTime(4 * FurnaceBurnTime.SHAFT.burnTime);
        
        setStepSound( soundWoodFootstep );
        
        setAlwaysStartlesAnimals();
        
        setUnlocalizedName( "fcBlockCampfire" );
        
        initModels();
    }
    
	@Override
    public TileEntity createNewTileEntity( World world )
    {
        return new CampfireTileEntity();
    }

	@Override
    public void breakBlock( World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
		if ( !campfireChangingState)
		{
	        CampfireTileEntity tileEntity = (CampfireTileEntity)world.getBlockTileEntity(
	        	i, j, k );
	        
	        tileEntity.ejectContents();
	        
	        // only called when not changing state as super kills the tile entity	        
	        super.breakBlock( world, i, j, k, iBlockID, iMetadata );	        
		}        
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
		if ( getHasSpit(blockAccess, i, j, k) )
		{
        	return AxisAlignedBB.getAABBPool().getAABB(
					0D, 0D, 0D, 1D, SPIT_COLLISION_HEIGHT, 1D);
		}
		else
		{
        	return AxisAlignedBB.getAABBPool().getAABB(         	
        		0D, 0D, 0D, 1D, 0.5D, 1D );
		}
    }
	
	@Override
    protected boolean canSilkHarvest()
    {
        return false;
    }    

	@Override
    public boolean canPlaceBlockAt( World world, int i, int j, int k )
    {
        if ( !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j - 1, k, 1, true) )
		{
			return false;
		}
		
        return super.canPlaceBlockAt( world, i, j, k );
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
		
		world.notifyNearbyAnimalsOfPlayerBlockAddOrRemove((EntityPlayer) entityLiving, this, i, j, k);
	}
	
	@Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
    {
		if (fireLevel != 0 || getFuelState(iMetadata) != CAMPFIRE_FUEL_STATE_NORMAL)
		{
			return 0;
		}
		
    	return super.idDropped( iMetadata, rand, iFortuneModifier );
    }
	
	@Override
    public int tickRate( World world )
    {
        return FallingBlock.FALLING_BLOCK_TICK_RATE;
    }
    
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iBlockID )
    {    	
        if ( !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j - 1, k, 1, true) )
        {
        	// schedudle a block update to destroy rather than doing it immediately, as
        	// this can occur during the tileEntity's updateEntity() if, for example,
        	// and ice block under the campfire is melted

            world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
        }
    }
    
	@Override
    public void updateTick( World world, int i, int j, int k, Random rand )
    {
        if ( !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world,
																   i, j - 1, k, 1, true) )
        {
        	if (fireLevel > 0 )
        	{
        		world.playAuxSFX( BTWEffectManager.FIRE_FIZZ_EFFECT_ID, i, j, k, 1 );
        	}
			
            dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
            
            world.setBlockToAir( i, j, k );
        }
    }
	
	@Override
    public boolean getCanBeSetOnFireDirectly(IBlockAccess blockAccess, int i, int j, int k)
    {
		return fireLevel == 0 && getFuelState(blockAccess, i, j, k) == CAMPFIRE_FUEL_STATE_NORMAL;
    }
    
	@Override
    public boolean setOnFireDirectly(World world, int i, int j, int k)
    {
		if ( getCanBeSetOnFireDirectly(world, i, j, k) )
		{
			if ( !isRainingOnCampfire(world, i, j, k) )
			{
				changeFireLevel(world, i, j, k, 1, world.getBlockMetadata(i, j, k));
				
		        CampfireTileEntity tileEntity = (CampfireTileEntity)world.getBlockTileEntity(
		        	i, j, k );
		        
		        tileEntity.onFirstLit();
		        
	            world.playSoundEffect( i + 0.5D, j + 0.5D, k + 0.5D, 
	            	"mob.ghast.fireball", 1F, world.rand.nextFloat() * 0.4F + 0.8F );
	            
	            if ( !Block.portal.tryToCreatePortal( world, i, j, k ) )
            	{
	            	// FCTODO: A bit hacky here.  Should probably be a general way to start a 
	            	// bigger fire atop flammable blocks
	            	
	            	int iBlockBelowID = world.getBlockId( i, j - 1, k );
	            	
	            	if ( iBlockBelowID == Block.netherrack.blockID ||
	            		iBlockBelowID ==  BTWBlocks.fallingNetherrack.blockID )
	            	{
	            		world.setBlockWithNotify( i, j, k, Block.fire.blockID );
	            	}
            	}
			}
			else
			{
	            world.playAuxSFX( BTWEffectManager.FIRE_FIZZ_EFFECT_ID, i, j, k, 0 );
			}
            
            return true;
		}
		
		return false;
    }
    
	@Override
    public int getChanceOfFireSpreadingDirectlyTo(IBlockAccess blockAccess, int i, int j, int k)
    {
		if (fireLevel == 0 && getFuelState(blockAccess, i, j, k) == CAMPFIRE_FUEL_STATE_NORMAL)
		{
			return 60; // same chance as leaves and other highly flammable objects
		}
		
		return 0;
    }
    
	@Override
    public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
    {
    	ItemStack stack = player.getCurrentEquippedItem();
    	
    	if ( stack != null )
    	{
    		Item item = stack.getItem();
    		
    		if ( !getHasSpit(world, i, j, k) )
    		{
	    		if ( item == BTWItems.pointyStick)
				{
	    			setHasSpit(world, i, j, k, true);
	    			
	                CampfireTileEntity tileEntity =
	                	(CampfireTileEntity)world.getBlockTileEntity( i, j, k );
	                
	                tileEntity.setSpitStack(stack);
	                
	    			stack.stackSize--;
	    			
	    			return true;
				}
    		}
    		else
    		{
                CampfireTileEntity tileEntity =
                	(CampfireTileEntity)world.getBlockTileEntity( i, j, k );
                
                ItemStack cookStack = tileEntity.getCookStack();
                
    			if ( cookStack == null )
    			{
    				if ( isValidCookItem(stack) )
    				{
    	                ItemStack spitStack = tileEntity.getSpitStack();
    	                
    	                if ( spitStack.getItemDamage() == 0 )
    	                {
    	                	tileEntity.setCookStack(stack);
    	                }
    	                else
    	                {
    	                	// break the damaged spit when the player attempts to place an item on it
    	                	// this is to discourage early game exploits involving half damaged sticks.
    	                	
							tileEntity.setSpitStack(null);
							
							setHasSpit(world, i, j, k, false);
							
    		    			if ( !world.isRemote )
    		    			{
	    	                	ItemStack ejectStack = stack.copy();
	    	        	    	
	    	                	ejectStack.stackSize = 1;
	    	                	
	    	        			ItemUtils.ejectStackWithRandomOffset(world, i, j, k,
																	 ejectStack);
	    	        			
	    	        			ItemUtils.ejectSingleItemWithRandomOffset(world, i, j, k,
                                                                          BTWItems.sawDust.itemID, 0);
	    	        			
    		        	        world.playAuxSFX( BTWEffectManager.WOOD_BLOCK_DESTROYED_EFFECT_ID,
    		        	        	i, j, k, 0 );
    		    			}
    	                }
	    				
		    			stack.stackSize--;
		    			
		    			return true;
    				}
    			}
    			else if ( cookStack.itemID == stack.itemID && 
    				stack.stackSize < stack.getMaxStackSize() )
    			{
    	            player.worldObj.playSoundAtEntity( player, "random.pop", 0.2F, 
    	        		( ( player.rand.nextFloat() - player.rand.nextFloat() ) * 0.7F + 1F ) * 2F );
    	            
    				stack.stackSize++;
    				
    				tileEntity.setCookStack(null);
    				
    				return true;
    			}
    		}
    		
    		if (fireLevel > 0 || getFuelState(world, i, j, k) == CAMPFIRE_FUEL_STATE_SMOULDERING)
    		{	    		
    			int iItemDamage = stack.getItemDamage();
    			
	    		if ( item.getCanBeFedDirectlyIntoCampfire(iItemDamage) )
	    		{
	    			if ( !world.isRemote )
	    			{
		                CampfireTileEntity tileEntity =
		                	(CampfireTileEntity)world.getBlockTileEntity( i, j, k );
		                
		                world.playSoundEffect( i + 0.5D, j + 0.5D, k + 0.5D, "mob.ghast.fireball", 
		                	0.2F + world.rand.nextFloat() * 0.1F, 
		                	world.rand.nextFloat() * 0.25F + 1.25F );
		                
		                tileEntity.addBurnTime(item.getCampfireBurnTime(iItemDamage));
	    			}
	                
	    			stack.stackSize--;
	    			
	    			return true;
	    		}
    		}
    	}
    	else // empty hand
    	{
            CampfireTileEntity tileEntity =
            	(CampfireTileEntity)world.getBlockTileEntity( i, j, k );
            
            ItemStack cookStack = tileEntity.getCookStack();
            
			if ( cookStack != null )
			{
				ItemUtils.givePlayerStackOrEject(player, cookStack, i, j, k);
				
				tileEntity.setCookStack(null);
    			
    			return true;
			}
			else
			{
	            ItemStack spitStack = tileEntity.getSpitStack();
	            
	            if ( spitStack != null )
	            {
	            	ItemUtils.givePlayerStackOrEject(player, spitStack, i, j, k);

					tileEntity.setSpitStack(null);
					
					setHasSpit(world, i, j, k, false);
	    			
	    			return true;
	            }
			}
    	}
		
		return false;
    }
	
	@Override
    public boolean shouldDeleteTileEntityOnBlockChange(int iNewBlockID)
    {
    	for (int iTempIndex = 0; iTempIndex < fireLevelBlockArray.length; iTempIndex++ )
    	{
    		if (fireLevelBlockArray[iTempIndex].blockID == iNewBlockID )
    		{
    			return false;
    		}
    	}
    	
    	return true;
    }

	@Override
    public void onEntityCollidedWithBlock( World world, int i, int j, int k, Entity entity )
    {
		if ( !world.isRemote && entity.isEntityAlive() && (fireLevel > 0 ||
															getFuelState(world, i, j, k) == CAMPFIRE_FUEL_STATE_SMOULDERING) )
		{
			if ( entity instanceof EntityItem )
			{
				EntityItem entityItem = (EntityItem)entity;
	        	ItemStack targetStack = entityItem.getEntityItem();
				Item item = targetStack.getItem();
				int iBurnTime = item.getCampfireBurnTime(targetStack.getItemDamage());
				
				if ( iBurnTime > 0 )
				{
					iBurnTime *= targetStack.stackSize;
					
	                CampfireTileEntity tileEntity =
	                	(CampfireTileEntity)world.getBlockTileEntity( i, j, k );
	                
	                world.playSoundEffect( i + 0.5D, j + 0.5D, k + 0.5D, "mob.ghast.fireball", 
	                	world.rand.nextFloat() * 0.1F + 0.2F, 
	                	world.rand.nextFloat() * 0.25F + 1.25F );
	               
	                tileEntity.addBurnTime(iBurnTime);
	    			
	    			entity.setDead();
				}
			}
		}		
    }

	@Override
    public boolean getDoesFireDamageToEntities(World world, int i, int j, int k, Entity entity)
    {
		return fireLevel > 2 || (fireLevel == 2 && entity instanceof EntityLiving );
    }
	
    @Override
    public boolean getCanBlockLightItemOnFire(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return fireLevel > 0;
    }
    
    @Override
    public MovingObjectPosition collisionRayTrace( World world, int i, int j, int k, Vec3 startRay, Vec3 endRay )
    {
    	int iMetadata = world.getBlockMetadata( i, j, k );
    	
    	BlockModel collisionModel = modelCollisionBase;
    	
    	if ( getHasSpit(iMetadata) )
    	{
    		collisionModel = modelCollisionWithSpit;
    	}
    	
    	if ( getIsIAligned(iMetadata) )
    	{
    		collisionModel = collisionModel.makeTemporaryCopy();
    		
    		collisionModel.rotateAroundYToFacing(4);
    	}
    	
    	return collisionModel.collisionRayTrace(world, i, j, k, startRay, endRay);
    }
    
    @Override
	public void onFluidFlowIntoBlock(World world, int i, int j, int k, BlockFluid newBlock)
	{
    	if (fireLevel > 0 )
    	{
    		world.playAuxSFX( BTWEffectManager.FIRE_FIZZ_EFFECT_ID, i, j, k, 0 );
    	}
    	
    	super.onFluidFlowIntoBlock(world, i, j, k, newBlock);
	}

    @Override
    public boolean canBeCrushedByFallingEntity(World world, int i, int j, int k,
											   EntityFallingSand entity)
    {
    	return true;
    }
    
    @Override
    public void onCrushedByFallingEntity(World world, int i, int j, int k,
										 EntityFallingSand entity)
    {
    	if (!world.isRemote && fireLevel > 0 )
    	{
    		world.playAuxSFX( BTWEffectManager.FIRE_FIZZ_EFFECT_ID, i, j, k, 0 );
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
	public boolean canRotateOnTurntable(IBlockAccess iBlockAccess, int i, int j, int k)
	{
		return true;
	}
	
	@Override
	public boolean rotateAroundJAxis(World world, int i, int j, int k, boolean bReverse)
	{
		setIAligned(world, i, j, k, !getIsIAligned(world, i, j, k));
		
		return true;
	}

	@Override
	public int rotateMetadataAroundJAxis(int iMetadata, boolean bReverse)
	{
		return setIAligned(iMetadata, !getIsIAligned(iMetadata));
	}

    @Override
    public boolean canGroundCoverRestOnBlock(World world, int i, int j, int k)
    {
    	return fireLevel == 0 && world.doesBlockHaveSolidTopSurface(i, j - 1, k);
    }
    
    @Override
    public float groundCoverRestingOnVisualOffset(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return -1F;        
    }
    
	//------------- Class Specific Methods ------------//
	
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
	
	public void setHasSpit(World world, int i, int j, int k, boolean bHasSpit)
	{
		int iMetadata = setHasSpit(world.getBlockMetadata(i, j, k), bHasSpit);
		
		world.setBlockMetadataWithNotify( i, j, k, iMetadata );
	}
	
	public int setHasSpit(int iMetadata, boolean bHasSpit)
	{
		if ( bHasSpit )
		{
			iMetadata |= 2;
		}
		else
		{
			iMetadata &= (~2);
		}
		
		return iMetadata;
	}
    
	public boolean getHasSpit(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getHasSpit(blockAccess.getBlockMetadata(i, j, k));
	}
	
	public boolean getHasSpit(int iMetadata)
	{
		return ( iMetadata & 2 ) != 0;
	}
	
	public void setFuelState(World world, int i, int j, int k, int iCampfireState)
	{
		int iMetadata = setFuelState(world.getBlockMetadata(i, j, k), iCampfireState);
		
		world.setBlockMetadataWithNotify( i, j, k, iMetadata );
	}
	
	public int setFuelState(int iMetadata, int iCampfireState)
	{
		iMetadata &= ~12; // filter out old state
		
		return iMetadata | ( iCampfireState << 2 );
	}
    
	public int getFuelState(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getFuelState(blockAccess.getBlockMetadata(i, j, k));
	}
	
	public int getFuelState(int iMetadata)
	{
		return ( iMetadata & 12 ) >> 2;
	}
	
	public boolean isValidCookItem(ItemStack stack)
	{
		if (CampfireCraftingManager.instance.getRecipeResult(stack.getItem().itemID) != null )
		{
			return true;
		}
		
		return false;
	}
	
	public void extinguishFire(World world, int i, int j, int k, boolean bSmoulder)
	{
		int iMetadata = world.getBlockMetadata( i, j, k );
		
		if ( bSmoulder )
		{
			iMetadata = setFuelState(iMetadata, CAMPFIRE_FUEL_STATE_SMOULDERING);
		}
		else
		{
			iMetadata = setFuelState(iMetadata, CAMPFIRE_FUEL_STATE_BURNED_OUT);
		}
		
		changeFireLevel(world, i, j, k, 0, iMetadata);

    	if ( !world.isRemote )
    	{
			world.playAuxSFX( BTWEffectManager.FIRE_FIZZ_EFFECT_ID, i, j, k, 1 );
    	}
	}
	
	public void relightFire(World world, int i, int j, int k)
	{
		changeFireLevel(world, i, j, k, 1, setFuelState(world.getBlockMetadata(i, j, k), CAMPFIRE_FUEL_STATE_NORMAL));
	}
	
	public void stopSmouldering(World world, int i, int j, int k)
	{
		setFuelState(world, i, j, k, CAMPFIRE_FUEL_STATE_BURNED_OUT);
	}
	
	public void changeFireLevel(World world, int i, int j, int k, int iFireLevel, int iMetadata)
	{
		CampfireBlock.campfireChangingState = true;
		
        world.setBlockAndMetadataWithNotify( i, j, k, 
        	CampfireBlock.fireLevelBlockArray[iFireLevel].blockID,
        	iMetadata );
        
		CampfireBlock.campfireChangingState = false;
	}
	
    public boolean isRainingOnCampfire(World world, int i, int j, int k)
    {
    	return world.isRainingAtPos(i, j, k);
    }
    
    private void initModels()
    {
		modelCollisionBase = new BlockModel();
		modelCollisionWithSpit = new BlockModel();

        modelCollisionBase.addBox(0D, 0D, 0D, 1D, 0.5D, 1D);
        modelCollisionWithSpit.addBox(0D, 0D, 0D, 1D, 0.5D, 1D);
        
        modelCollisionWithSpit.addBox(0D, 0D, 0.5D - SPIT_COLLISION_HALF_WIDTH,
									  1D, SPIT_COLLISION_HEIGHT, 0.5D + SPIT_COLLISION_HALF_WIDTH);
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon spitIcon;
    @Environment(EnvType.CLIENT)
    private Icon spitSupportIcon;
    @Environment(EnvType.CLIENT)
    private Icon burnedIcon;
    @Environment(EnvType.CLIENT)
    private Icon embersIcon;
    @Environment(EnvType.CLIENT)
    static final double[] fireAnimationScaleArray = new double[] {0D, 0.25D, 0.5D, 0.875D };
	
	@Environment(EnvType.CLIENT)
	public int idPicked(World world, int x, int y, int z) {
		return BTWBlocks.unlitCampfire.blockID;
	}

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		blockIcon = register.registerIcon( "fcBlockCampfire" );
		spitIcon = register.registerIcon("fcBlockCampfire_spit");
		spitSupportIcon = register.registerIcon("fcBlockCampfire_support");
		burnedIcon = register.registerIcon("fcBlockCampfire_burned");
		embersIcon = register.registerIcon("fcOverlayEmbers");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, 
    	int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {    	
    	if ( renderCampfireModel(renderBlocks, i, j, k) )
    	{
    		if ( getHasSpit(renderBlocks.blockAccess, i, j, k) )
    		{
    			renderSpit(renderBlocks, i, j, k);
    		}
    		
	    	if (fireLevel > 0 && !renderBlocks.hasOverrideBlockTexture() )
	    	{
	    		renderFirePortion(renderBlocks, i, j, k);
	    	}	    	
	    	
	    	return true;     	
    	}
    	
    	return false;     	
    }


    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks renderer, int i, int j, int k,
									  boolean bFirstPassResult)
    {
    	if ( bFirstPassResult )
    	{
	    	if (fireLevel == 0 && getFuelState(renderer.blockAccess, i, j, k) == CAMPFIRE_FUEL_STATE_SMOULDERING)
			{
				renderCampfireModelEmbers(renderer, i, j, k);
			}
    	}
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
    	modelCampfire.renderAsItemBlock(renderBlocks, this, iItemDamage);
    }

    @Environment(EnvType.CLIENT)
    private boolean renderCampfireModelEmbers(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	int iMetadata = renderBlocks.blockAccess.getBlockMetadata( i, j, k );
    	BlockModel transformedModel;
    	
    	if ( !WorldUtils.isGroundCoverOnBlock(renderBlocks.blockAccess, i, j, k) )
    	{
        	transformedModel = modelCampfire.makeTemporaryCopy();
    	}
    	else
    	{
        	transformedModel = modelCampfire.modelInSnow.makeTemporaryCopy();
    	}

    	if ( getIsIAligned(iMetadata) )
    	{    		
    		transformedModel.rotateAroundYToFacing(4);
    	}
    	
		return transformedModel.renderAsBlockFullBrightWithTexture(renderBlocks, this, i, j, k, embersIcon);
    }

    @Environment(EnvType.CLIENT)
    private boolean renderCampfireModel(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	int iMetadata = renderBlocks.blockAccess.getBlockMetadata( i, j, k );
    	BlockModel transformedModel;
    	
    	if ( !WorldUtils.isGroundCoverOnBlock(renderBlocks.blockAccess, i, j, k) )
    	{
        	transformedModel = modelCampfire.makeTemporaryCopy();
    	}
    	else
    	{
        	transformedModel = modelCampfire.modelInSnow.makeTemporaryCopy();
    	}

    	if ( getIsIAligned(iMetadata) )
    	{    		
    		transformedModel.rotateAroundYToFacing(4);
    	}
    	
    	if (getFuelState(iMetadata) != CAMPFIRE_FUEL_STATE_NORMAL &&
			!renderBlocks.hasOverrideBlockTexture())
    	{
    		return transformedModel.renderAsBlockWithTexture(renderBlocks, this, i, j, k, burnedIcon);
    	}
    	else
    	{    	
    		return transformedModel.renderAsBlock(renderBlocks, this, i, j, k);
    	}
    }

    @Environment(EnvType.CLIENT)
    private void renderSpit(RenderBlocks renderBlocks, int i, int j, int k)
	{
		boolean bIAligned = getIsIAligned(renderBlocks.blockAccess, i, j, k);

		// spit
		
        RenderUtils.setRenderBoundsWithAxisAlignment(renderBlocks,
													 0F, SPIT_MIN_Y, 0.5F - HALF_SPIT_THICKNESS,
													 1F, SPIT_MAX_Y, 0.5F + HALF_SPIT_THICKNESS, bIAligned);
        
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, this, i, j, k, spitIcon);
        
        boolean bRenderSupport = true;
        
        if ( ( bIAligned && !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(
        	renderBlocks.blockAccess, i, j, k - 1, 3) ) ||
        	( !bIAligned && !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(
    		renderBlocks.blockAccess, i - 1, j, k, 5) ) )
        {
            renderSpitSupport(renderBlocks, i, j, k, SPIT_SUPPORT_BORDER);
        }
        
        if ( ( bIAligned && !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(
        	renderBlocks.blockAccess, i, j, k + 1, 2) ) ||
        	( !bIAligned && !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(
    		renderBlocks.blockAccess, i + 1, j, k, 4) ) )
        {
        	renderSpitSupport(renderBlocks, i, j, k,
							  1F - SPIT_SUPPORT_BORDER - SPIT_SUPPORT_WIDTH);
        }
	}

    @Environment(EnvType.CLIENT)
    private void renderSpitSupport(RenderBlocks renderBlocks, int i, int j, int k, float fOffset)
	{
		boolean bIAligned = getIsIAligned(renderBlocks.blockAccess, i, j, k);

        // support
        
        RenderUtils.setRenderBoundsWithAxisAlignment(renderBlocks,
													 fOffset, 0F, 0.5F - HALF_SPIT_SUPPORT_WIDTH,
													 fOffset + SPIT_SUPPORT_WIDTH, SPIT_MIN_Y, 0.5F + HALF_SPIT_SUPPORT_WIDTH, bIAligned);
        
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, this, i, j, k, spitSupportIcon);
        
        // support fork
        
        RenderUtils.setRenderBoundsWithAxisAlignment(renderBlocks,
													 fOffset, SPIT_FORK_MIN_Y, 0.5F - HALF_SPIT_SUPPORT_WIDTH + SPIT_FORK_WIDTH,
													 fOffset + SPIT_SUPPORT_WIDTH, SPIT_FORK_MAX_Y,
													 0.5F + HALF_SPIT_SUPPORT_WIDTH + SPIT_FORK_WIDTH, bIAligned);
        
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, this, i, j, k, spitSupportIcon);
        
        RenderUtils.setRenderBoundsWithAxisAlignment(renderBlocks,
													 fOffset, SPIT_FORK_MIN_Y, 0.5F - HALF_SPIT_SUPPORT_WIDTH - SPIT_FORK_WIDTH,
													 fOffset + SPIT_SUPPORT_WIDTH, SPIT_FORK_MAX_Y,
													 0.5F + HALF_SPIT_SUPPORT_WIDTH - SPIT_FORK_WIDTH, bIAligned);
        
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, this, i, j, k, spitSupportIcon);
	}

    @Environment(EnvType.CLIENT)
    private void renderFirePortion(RenderBlocks renderBlocks, int i, int j, int k)
	{
		IBlockAccess blockAccess = renderBlocks.blockAccess;
		
        Tessellator tesselator = Tessellator.instance;
        
        double dScale = fireAnimationScaleArray[fireLevel];
        
        double dI = (double)i;
        double dJ = (double)j;
        double dK = (double)k;
        
        Icon fireTexture1 = Block.fire.func_94438_c(0);
        Icon fireTexture2 = Block.fire.func_94438_c(1);
        
        if ( ( ( i + k ) & 1 ) != 0 )
        {
            fireTexture1 = Block.fire.func_94438_c(1);
            fireTexture2 = Block.fire.func_94438_c(0);
        }        

        tesselator.setColorOpaque_F( 1.0F, 1.0F, 1.0F );
        tesselator.setBrightness( getMixedBrightnessForBlock( blockAccess, i, j, k ) );
        
        double dMinU = fireTexture1.getMinU();
        double dMinV = fireTexture1.getMinV();
        double dMaxU = fireTexture1.getMaxU();
        double dMaxV = fireTexture1.getMaxV();
        
        double dFireHeight = 1.4D * dScale;
        double dHorizontalMin = 0.5D - ( 0.5D * dScale );
        double dHorizontalMax = 0.5D + ( 0.5D * dScale );
        
        double dOffset = 0.2D * dScale; 
        	
        double var18 = dI + 0.5D + dOffset;        
        double var20 = dI + 0.5D - dOffset;
        double var22 = dK + 0.5D + dOffset;
        double var24 = dK + 0.5D - dOffset;
        
        dOffset = 0.3D * dScale; 
    	
        double var26 = dI + 0.5D - dOffset;
        double var28 = dI + 0.5D + dOffset;
        double var30 = dK + 0.5D - dOffset;
        double var32 = dK + 0.5D + dOffset;
        
        tesselator.addVertexWithUV(var26, (dJ + dFireHeight), (dK + dHorizontalMax), dMaxU, dMinV);
        tesselator.addVertexWithUV(var18, (dJ + 0), (dK + dHorizontalMax), dMaxU, dMaxV);
        tesselator.addVertexWithUV(var18, (dJ + 0), (dK + dHorizontalMin), dMinU, dMaxV);
        tesselator.addVertexWithUV(var26, (dJ + dFireHeight), (dK + dHorizontalMin), dMinU, dMinV);
        
        tesselator.addVertexWithUV(var28, (dJ + dFireHeight), (dK + dHorizontalMin), dMaxU, dMinV);
        tesselator.addVertexWithUV(var20, (dJ + 0), (dK + dHorizontalMin), dMaxU, dMaxV);
        tesselator.addVertexWithUV(var20, (dJ + 0), (dK + dHorizontalMax), dMinU, dMaxV);
        tesselator.addVertexWithUV(var28, (dJ + dFireHeight), (dK + dHorizontalMax), dMinU, dMinV);
        
        dMinU = fireTexture2.getMinU();
        dMinV = fireTexture2.getMinV();
        dMaxU = fireTexture2.getMaxU();
        dMaxV = fireTexture2.getMaxV();
        
        tesselator.addVertexWithUV((dI + dHorizontalMax), (dJ + dFireHeight), var32, dMaxU, dMinV);
        tesselator.addVertexWithUV((dI + dHorizontalMax), (dJ + 0), var24, dMaxU, dMaxV);
        tesselator.addVertexWithUV((dI + dHorizontalMin), (dJ + 0), var24, dMinU, dMaxV);
        tesselator.addVertexWithUV((dI + dHorizontalMin), (dJ + dFireHeight), var32, dMinU, dMinV);
        
        tesselator.addVertexWithUV((dI + dHorizontalMin), (dJ + dFireHeight), var30, dMaxU, dMinV);
        tesselator.addVertexWithUV((dI + dHorizontalMin), (dJ + 0), var22, dMaxU, dMaxV);
        tesselator.addVertexWithUV((dI + dHorizontalMax), (dJ + 0), var22, dMinU, dMaxV);
        tesselator.addVertexWithUV((dI + dHorizontalMax), (dJ + dFireHeight), var30, dMinU, dMinV);
        
        dOffset = 0.5D * dScale;
        
        var18 = dI + 0.5D - dOffset;
        var20 = dI + 0.5D + dOffset;
        var22 = dK + 0.5D - dOffset;
        var24 = dK + 0.5D + dOffset;
        
        dOffset = 0.4D * dScale;
        
        var26 = dI + 0.5D - dOffset;
        var28 = dI + 0.5D + dOffset;
        var30 = dK + 0.5D - dOffset;
        var32 = dK + 0.5D + dOffset;
        
        tesselator.addVertexWithUV(var26, (dJ + dFireHeight), (dK + dHorizontalMin), dMinU, dMinV);
        tesselator.addVertexWithUV(var18, (dJ + 0), (dK + dHorizontalMin), dMinU, dMaxV);
        tesselator.addVertexWithUV(var18, (dJ + 0), (dK + dHorizontalMax), dMaxU, dMaxV);
        tesselator.addVertexWithUV(var26, (dJ + dFireHeight), (dK + dHorizontalMax), dMaxU, dMinV);
        
        tesselator.addVertexWithUV(var28, (dJ + dFireHeight), (dK + dHorizontalMax), dMinU, dMinV);
        tesselator.addVertexWithUV(var20, (dJ + 0), (dK + dHorizontalMax), dMinU, dMaxV);
        tesselator.addVertexWithUV(var20, (dJ + 0), (dK + dHorizontalMin), dMaxU, dMaxV);
        tesselator.addVertexWithUV(var28, (dJ + dFireHeight), (dK + dHorizontalMin), dMaxU, dMinV);
        
        dMinU = fireTexture1.getMinU();
        dMinV = fireTexture1.getMinV();        
        dMaxU = fireTexture1.getMaxU();
        dMaxV = fireTexture1.getMaxV();
        
        tesselator.addVertexWithUV((dI + dHorizontalMin), (dJ + dFireHeight), var32, dMinU, dMinV);
        tesselator.addVertexWithUV((dI + dHorizontalMin), (dJ + 0), var24, dMinU, dMaxV);
        tesselator.addVertexWithUV((dI + dHorizontalMax), (dJ + 0), var24, dMaxU, dMaxV);
        tesselator.addVertexWithUV((dI + dHorizontalMax), (dJ + dFireHeight), var32, dMaxU, dMinV);
        
        tesselator.addVertexWithUV((dI + dHorizontalMax), (dJ + dFireHeight), var30, dMinU, dMinV);
        tesselator.addVertexWithUV((dI + dHorizontalMax), (dJ + 0), var22, dMinU, dMaxV);
        tesselator.addVertexWithUV((dI + dHorizontalMin), (dJ + 0), var22, dMaxU, dMaxV);
        tesselator.addVertexWithUV((dI + dHorizontalMin), (dJ + dFireHeight), var30, dMaxU, dMinV);
	}

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick( World world, int i, int j, int k, Random rand )
    {
    	if (fireLevel > 1 )
    	{
            for (int iTempCount = 0; iTempCount < fireLevel; iTempCount++ )
            {
                double xPos = i + rand.nextFloat();
                double yPos = j + 0.5F + ( rand.nextFloat() * 0.5F );
                double zPos = k + rand.nextFloat();
                
                world.spawnParticle( "smoke", xPos, yPos, zPos, 0D, 0D, 0D );
            }
            
	        CampfireTileEntity tileEntity =
	        	(CampfireTileEntity)world.getBlockTileEntity( i, j, k );
	        
	        if ( tileEntity.getIsFoodBurning() )
	        {
	            for ( int iTempCount = 0; iTempCount < 1; ++iTempCount )
	            {
	                double xPos = i + 0.375F + rand.nextFloat() * 0.25F;
	                double yPos = j + 0.5F + rand.nextFloat() * 0.5F;
	                double zPos = k + 0.375F + rand.nextFloat() * 0.25F;
	                
	                world.spawnParticle( "largesmoke", xPos, yPos, zPos, 0D, 0D, 0D );
	            }
	        }
	        else if ( tileEntity.getIsCooking() )
	        {
	            for ( int iTempCount = 0; iTempCount < 1; ++iTempCount )
	            {
	                double xPos = i + 0.375F + rand.nextFloat() * 0.25F;
	                double yPos = j + 0.5F + rand.nextFloat() * 0.5F;
	                double zPos = k + 0.375F + rand.nextFloat() * 0.25F;
	                
	                world.spawnParticle( "fcwhitesmoke", xPos, yPos, zPos, 0D, 0D, 0D );
	            }
	        }           
    	}
    	else if (fireLevel == 1 || getFuelState(world, i, j, k) == CAMPFIRE_FUEL_STATE_SMOULDERING)
    	{
            double xPos = (double)i + 0.375D + ( rand.nextDouble() * 0.25D );
            double yPos = (double)j + 0.25D + ( rand.nextDouble() * 0.25D );
            double zPos = (double)k + 0.375D + ( rand.nextDouble() * 0.25D );
            
            world.spawnParticle( "smoke", xPos, yPos, zPos, 0D, 0D, 0D );
    	}
    	
    	if (fireLevel > 0 )
    	{
	        if ( rand.nextInt(24) == 0 )
	        {
	        	float fVolume = (fireLevel * 0.25F ) + rand.nextFloat();
	        	
	            world.playSound( i + 0.5D, j + 0.5D, k + 0.5D, "fire.fire", 
	            	fVolume, rand.nextFloat() * 0.7F + 0.3F, false );
	        }	        
    	}
    }
}