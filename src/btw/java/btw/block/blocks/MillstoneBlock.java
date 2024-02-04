// FCMOD

package btw.block.blocks;

import btw.block.MechanicalBlock;
import btw.block.model.MillstoneModel;
import btw.block.tileentity.MillstoneTileEntity;
import btw.client.fx.BTWEffectManager;
import btw.inventory.util.InventoryUtils;
import btw.block.util.MechPowerUtils;
import btw.block.util.RayTraceUtils;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class MillstoneBlock extends BlockContainer
	implements MechanicalBlock
{
	private static final int TICK_RATE = 10;
	
	public static final int CONTENTS_NOTHING = 0;
	public static final int CONTENTS_NORMAL_GRINDING = 1;
	public static final int CONTENTS_NETHERRACK = 2;
	public static final int CONTENTS_COMPANION_CUBE = 3;
	public static final int CONTENTS_JAMMED = 4;
	
	public static final MillstoneModel model = new MillstoneModel();
	
    public MillstoneBlock(int iBlockID)
    {
	    super( iBlockID, Material.rock );
	    
        setHardness( 3.5F );
        
        setStepSound( soundStoneFootstep );
        setUnlocalizedName( "fcBlockMillStone" );
        
        setTickRandomly( true );
        
        setCreativeTab( CreativeTabs.tabRedstone );
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
    public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
    {		
		int iState = this.getCurrentGrindingType(world, i, j, k);
		
        MillstoneTileEntity tileEntity = (MillstoneTileEntity)world.getBlockTileEntity( i, j, k );
        
		if (iState == CONTENTS_NOTHING)
		{
	    	ItemStack heldStack = player.getCurrentEquippedItem();

	    	if ( heldStack != null )
	    	{
				if ( !world.isRemote )
				{
			        world.playAuxSFX( BTWEffectManager.ITEM_COLLECTION_POP_EFFECT_ID, i, j, k, 0 );
			        
					tileEntity.attemptToAddSingleItemFromStack(heldStack);
				}
				else
				{				
					heldStack.stackSize--;
				}
	    	}
		}
		else if ( !world.isRemote )
    	{
	        world.playAuxSFX( BTWEffectManager.ITEM_COLLECTION_POP_EFFECT_ID, i, j, k, 0 );
	        
			tileEntity.ejectContents(iFacing);
    	}
		
		return true;		
    }

	@Override
    public void breakBlock( World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
        InventoryUtils.ejectInventoryContents(world, i, j, k, (IInventory)world.getBlockTileEntity(i, j, k));

        super.breakBlock( world, i, j, k, iBlockID, iMetadata );
    }

	@Override
    public TileEntity createNewTileEntity( World world )
    {
        return new MillstoneTileEntity();
    }

	@Override
    public void updateTick( World world, int i, int j, int k, Random random )
    {
    	boolean bReceivingPower = isInputtingMechanicalPower(world, i, j, k);
    	boolean bOn = getIsMechanicalOn(world, i, j, k);
    	
    	if ( bOn != bReceivingPower )
    	{
    		setIsMechanicalOn(world, i, j, k, bReceivingPower);
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
    public void onNeighborBlockChange( World world, int i, int j, int k, int iBlockID )
    {
    	if (!isCurrentStateValid(world, i, j, k) &&
			!world.isUpdatePendingThisTickForBlock(i, j, k, blockID) )
    	{    		
	        world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
    	}
    }
    
    @Override
    public MovingObjectPosition collisionRayTrace( World world, int i, int j, int k, Vec3 startRay, Vec3 endRay )
    {
    	RayTraceUtils rayTrace = new RayTraceUtils( world, i, j, k, startRay, endRay );
    	
    	model.addToRayTrace(rayTrace);
    	
    	rayTrace.addBoxWithLocalCoordsToIntersectionList(model.boxBase);
		
        return rayTrace.getFirstIntersection();
    }

    @Override
	public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
		return true;
	}
    
    @Override
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
		return iFacing == 0 || bIgnoreTransparency;
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
		return iFacing < 2;
	}
	@Override
    public boolean isOutputtingMechanicalPower(World world, int i, int j, int k)
    {
    	return false;
    }
    
	@Override
	public void overpower(World world, int i, int j, int k)
	{
		breakMillStone(world, i, j, k);
	}
	
	@Override
	public boolean hasComparatorInputOverride()
    {
        return true;
    }

	@Override
    public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5)
    {
        return ((MillstoneTileEntity) par1World.getBlockTileEntity(par2, par3, par4)).stackMilling == null ? 0 : 15;
    }
	
    //------------- Class Specific Methods ------------//
    
    public boolean getIsMechanicalOn(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getIsMechanicalOn(blockAccess.getBlockMetadata(i, j, k));
	}
    
    public boolean getIsMechanicalOn(int iMetadata)
    {
    	return ( iMetadata & 1 ) > 0;    
    }
    
    public void setIsMechanicalOn(World world, int i, int j, int k, boolean bOn)
    {
    	int iMetadata = setIsMechanicalOn(world.getBlockMetadata(i, j, k), bOn);
    	
		world.setBlockMetadataWithNotify( i, j, k, iMetadata );
    }
    
    public int setIsMechanicalOn(int iMetadata, boolean bOn)
    {
    	if ( bOn )
    	{
            return iMetadata | 1;
    	}
    	
    	return iMetadata & (~1);
    }
    
    public int getCurrentGrindingType(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getCurrentGrindingType(blockAccess.getBlockMetadata(i, j, k));
    }
    
    public int getCurrentGrindingType(int iMetadata)
    {
    	return ( iMetadata & 14 ) >> 1;
    }
    
    public void setCurrentGrindingType(World world, int i, int j, int k, int iGrindingType)
    {
    	int iMetadata = setCurrentGrindingType(world.getBlockMetadata(i, j, k), iGrindingType);
    	
		world.setBlockMetadataWithClient( i, j, k, iMetadata );
    }
    
    public int setCurrentGrindingType(int iMetadata, int iGrindingType)
    {
    	iMetadata &= ~14; // filter out old state
    	
    	return iMetadata | ( iGrindingType << 1 );    	
    }
    
	private void breakMillStone(World world, int i, int j, int k)
	{
    	dropItemsIndividually(world, i, j, k, BTWItems.stone.itemID, 16, 0, 0.75F);
		
        world.playAuxSFX( BTWEffectManager.MECHANICAL_DEVICE_EXPLODE_EFFECT_ID, i, j, k, 0 );
        
		world.setBlockWithNotify( i, j, k, 0 );
	}
	
	public boolean isCurrentStateValid(World world, int i, int j, int k)
	{
    	boolean bReceivingPower = isInputtingMechanicalPower(world, i, j, k);
    	boolean bOn = getIsMechanicalOn(world, i, j, k);
    	
    	return bOn == bReceivingPower;    	
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private final Icon[] iconsBySide = new Icon[6];
    @Environment(EnvType.CLIENT)
    private final Icon[] iconsBySideFull = new Icon[6];
    @Environment(EnvType.CLIENT)
    private final Icon[] iconsBySideOn = new Icon[6];
    @Environment(EnvType.CLIENT)
    private final Icon[] iconsBySideOnFull = new Icon[6];

    @Environment(EnvType.CLIENT)
    private boolean renderingBase = false;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        blockIcon = register.registerIcon( "stone" ); // for hit effects

		iconsBySideFull[0] = iconsBySide[0] = register.registerIcon("fcBlockMillStone_bottom");

		iconsBySideFull[1] = iconsBySide[1] = register.registerIcon("fcBlockMillStone_top");

		iconsBySideOn[0] = iconsBySideOnFull[0] = register.registerIcon("fcBlockMillStone_bottom_on");
		iconsBySideOn[1] = iconsBySideOnFull[1] = register.registerIcon("fcBlockMillStone_top_on");
    		
        Icon sideIcon = register.registerIcon( "fcBlockMillStone_side" );
        Icon sideIconFull = register.registerIcon( "fcBlockMillStone_side_full" );
        
        Icon sideIconOn = register.registerIcon( "fcBlockMillStone_side_on" );
        Icon sideIconOnFull = register.registerIcon( "fcBlockMillStone_side_on_full" );
        
        for ( int iTempSide = 2; iTempSide <= 5; iTempSide++ )
        {
			iconsBySide[iTempSide] = sideIcon;
			iconsBySideFull[iTempSide] = sideIconFull;

			iconsBySideOn[iTempSide] = sideIconOn;
			iconsBySideOnFull[iTempSide] = sideIconOnFull;
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		if (renderingBase)
		{
			return iconsBySide[iSide];
		}
		else if ( getIsMechanicalOn(iMetadata) )
		{
			if (getCurrentGrindingType(iMetadata) == CONTENTS_NOTHING)
			{
				return iconsBySideOn[iSide];
			}
			else
			{
				return iconsBySideOnFull[iSide];
			}
		}
		else
		{
			if (getCurrentGrindingType(iMetadata) == CONTENTS_NOTHING)
			{
				return iconsBySide[iSide];
			}
			else
			{
				return iconsBySideFull[iSide];
			}
		}
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {
		renderingBase = true;
    	
        model.boxBase.renderAsBlock(renderBlocks, this, i, j, k);

		renderingBase = false;
    	
    	return model.renderAsBlock(renderBlocks, this, i, j, k);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
		renderingBase = true;
    	
        model.boxBase.renderAsItemBlock(renderBlocks, this, iItemDamage);

		renderingBase = false;
    	
    	model.renderAsItemBlock(renderBlocks, this, iItemDamage);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick( World world, int i, int j, int k, Random random )
    {
    	if ( getIsMechanicalOn(world, i, j, k) )
    	{
	        int iCurrentGrindingType = getCurrentGrindingType(world, i, j, k);
	        
	    	emitMillingParticles(world, i, j, k, iCurrentGrindingType, random);
	        
            if (iCurrentGrindingType == CONTENTS_JAMMED)
            {
		        world.playSound( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
		    		"minecart.base", 
		    		1.0F + ( world.rand.nextFloat() * 0.1F ),	// volume 
		    		1.25F + ( world.rand.nextFloat() * 0.1F ) );	// pitch
            }
            else if (iCurrentGrindingType != CONTENTS_NOTHING)
            {
		        world.playSound( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
		    		"minecart.base", 
		    		1.0F + ( world.rand.nextFloat() * 0.1F ),	// volume 
		    		0.75F + ( world.rand.nextFloat() * 0.1F ) );	// pitch
            }
            else if ( random.nextInt( 2 ) == 0 )
	        {
		        world.playSound( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
		    		"minecart.base", 
		    		1.5F + ( random.nextFloat() * 0.1F ),	// volume 
		    		0.5F + ( random.nextFloat() * 0.1F ) );	// pitch
	        }
	        
	        if (iCurrentGrindingType == CONTENTS_NETHERRACK)
	        {
		        if ( random.nextInt( 3 ) <= 1 )
		        {
			        world.playSound( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
		        		"mob.ghast.scream", 0.75F, random.nextFloat() * 0.4F + 0.8F );
		        }
	        }
	        else if (iCurrentGrindingType == CONTENTS_COMPANION_CUBE)
	        {
		        if ( random.nextInt( 3 ) <= 1 )
		        {
		            world.playSound( (float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, 
		        		"mob.wolf.hurt", 2F, ( random.nextFloat() - random.nextFloat() ) * 0.2F + 1F );
		        }
	        }            
    	}
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool( World world, int i, int j, int k )
    {
		AxisAlignedBB transformedBox = model.boxSelection.makeTemporaryCopy();
		
		transformedBox.offset( i, j, k );
		
		return transformedBox;		
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void clientNotificationOfMetadataChange(World world, int i, int j, int k, int iOldMetadata, int iNewMetadata)
	{
		if (!getIsMechanicalOn(iOldMetadata) && getIsMechanicalOn(iNewMetadata) )
		{
			// mech power turn on
			
			int iGrindType = getCurrentGrindingType(iNewMetadata);
			
            if (iGrindType == CONTENTS_COMPANION_CUBE)
            {                
                world.playSound( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
                		"mob.wolf.hurt", 5F, ( world.rand.nextFloat() - world.rand.nextFloat() ) * 0.2F + 1F );
            }
            
	        world.playSound( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
	    		"minecart.base", 
	    		1.5F + ( world.rand.nextFloat() * 0.1F ),	// volume 
	    		0.5F + ( world.rand.nextFloat() * 0.1F ) );	// pitch
	        
	        emitMillingParticles(world, i, j, k, iGrindType, world.rand);
		}
	}

    @Environment(EnvType.CLIENT)
    private void emitMillingParticles(World world, int i, int j, int k, int iGrindType, Random rand)
    {
    	String sParticle;
    	
    	if (iGrindType == CONTENTS_NOTHING)
    	{
    		sParticle = "smoke";
    	}
    	else if (iGrindType == CONTENTS_JAMMED)
    	{
    		sParticle = "largesmoke";
    	}
    	else
    	{
    		sParticle = "fcwhitesmoke";
    	}
    	
        for ( int iTempCount = 0; iTempCount < 5; iTempCount++ )
        {
            float smokeX = (float)i + rand.nextFloat();
            float smokeY = (float)j + rand.nextFloat() * 0.5F + 1F;
            float smokeZ = (float)k + rand.nextFloat();
            
            world.spawnParticle( sParticle, smokeX, smokeY, smokeZ, 0D, 0D, 0D );
        }
    }    
}
