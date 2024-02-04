// FCMOD

package btw.block.blocks;

import java.util.List;
import java.util.Random;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.client.fx.BTWEffectManager;
import btw.client.render.util.RenderUtils;
import btw.entity.item.BloodWoodSaplingItemEntity;
import btw.item.items.ShearsItem;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class AestheticVegetationBlock extends Block
{
    public final static int SUBTYPE_VINE_TRAP = 0;
    public final static int SUBTYPE_VINE_TRAP_TRIGGERED_BY_ENTITY = 1;
    public final static int SUBTYPE_BLOOD_WOOD_SAPLING = 2;
    public final static int SUBTYPE_BLOOD_LEAVES = 3; // deprecated
    public final static int SUBTYPE_VINE_TRAP_UPSIDE_DOWN = 4;
    public final static int SUBTYPE_VINE_TRAP_UPSIDE_DOWN_TRIGGERED_BY_ENTITY = 5;
    
    public final static int NUM_SUBTYPES = 6;
    
    private final static double VINE_TRAP_HEIGHT = (2D / 16D );

    private final static float HARDNESS = 0.2F;
    
	private final static int TICK_RATE = 10;
    
    public final static int BLOOD_WOOD_SAPLING_MIN_TRUNK_HEIGHT = 4;
    
    public AestheticVegetationBlock(int iBlockID )
    {
        super( iBlockID, Material.leaves );
        
        setHardness(HARDNESS);
        setAxesEffectiveOn();
        
        setBuoyancy(1F);
        
		setFireProperties(Flammability.EXTREME);
		
        setStepSound( soundGrassFootstep );
        
        setUnlocalizedName( "fcBlockAestheticVegetation" );
        
        setTickRandomly( true );           
		
		setCreativeTab( CreativeTabs.tabDecorations );
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
    public int onBlockPlaced(World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
		int iSubtype = iMetadata;
		
		if (iSubtype == SUBTYPE_VINE_TRAP)
		{
			if ( iFacing != 1 )
			{
				boolean bUpsideDown = true;
				
				if ( iFacing >= 2 )
				{
					if ( fClickY < 0.5F )
					{
						bUpsideDown = false;
					}
				}
				
				if ( bUpsideDown )
				{
					return SUBTYPE_VINE_TRAP_UPSIDE_DOWN;
				}
			}
		}
		
		return iMetadata;
    }
    
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iChangedBlockID )
    {
        super.onNeighborBlockChange( world, i, j, k, iChangedBlockID );
        
    	int iSubtype = getSubtype(world, i, j, k);
    	
    	if (iSubtype == SUBTYPE_BLOOD_WOOD_SAPLING)
    	{
    		validateBloodWoodSapling(world, i, j, k);
    	}
    }
    
	@Override
    public boolean canBlockStay( World world, int i, int j, int k )
    {
    	int iSubtype = getSubtype(world, i, j, k);
    	
    	if (iSubtype == SUBTYPE_BLOOD_WOOD_SAPLING)
    	{
    		return canBloodwoodSaplingStayAtLocation(world, i, j, k);
    	}
    	else
    	{
    		return super.canBlockStay( world, i, j, k );
    	}
    }
    
	@Override
	public int damageDropped( int iMetadata )
    {
    	int iSubtype = iMetadata;

    	switch ( iSubtype )
    	{
			case SUBTYPE_VINE_TRAP:
			case SUBTYPE_VINE_TRAP_TRIGGERED_BY_ENTITY:
			case SUBTYPE_VINE_TRAP_UPSIDE_DOWN:
			case SUBTYPE_VINE_TRAP_UPSIDE_DOWN_TRIGGERED_BY_ENTITY:
				
	    		iSubtype = SUBTYPE_VINE_TRAP;
	    		
	    		break;
	    		
			case SUBTYPE_BLOOD_LEAVES:

	    		iSubtype = SUBTYPE_BLOOD_WOOD_SAPLING;
	    		
	    		break;
    	}
    	
        return iSubtype; 
    }
    
	@Override
    public void dropBlockAsItemWithChance( World world, int i, int j, int k, int iMetaData, float fChance, int iFortuneModifier )
    {
		if (iMetaData == SUBTYPE_BLOOD_LEAVES)
		{
	        if ( world.isRemote )
	        {
	            return;
	        }
	        
	        int iNumDropped = world.rand.nextInt( 20 ) != 0 ? 0 : 1;
	        
	        for( int iTempCount = 0; iTempCount < iNumDropped; iTempCount++ )
	        {
	            if ( world.rand.nextFloat() > fChance )
	            {
	                continue;
	            }
	            
	            int iItemID = idDropped( iMetaData, world.rand, iFortuneModifier );
	            
	            if ( iItemID > 0 )
	            {
	                dropBlockAsItem_do( world, i, j, k, new ItemStack( iItemID, 1, damageDropped( iMetaData ) ) );
	            }
	        }
		}
		else
		{
			super.dropBlockAsItemWithChance( world, i, j, k, iMetaData, fChance, iFortuneModifier );
		}
    }
	
	@Override
    protected void dropBlockAsItem_do( World world, int i, int j, int k, ItemStack itemStack )
    {
		if ( itemStack.itemID == blockID && itemStack.getItemDamage() == SUBTYPE_BLOOD_WOOD_SAPLING)
		{		
			// special case bloodwood saplings to generate a self-planting EntityItem
			
	        if ( world.isRemote)
	        {
	            return;
	        }
	        else
	        {
	            float f = 0.7F;
	            
	            double d = (double)( world.rand.nextFloat() * f) + (double)(1F - f) * 0.5D;
	            double d1 = (double)( world.rand.nextFloat() * f) + (double)(1F - f) * 0.5D;
	            double d2 = (double)( world.rand.nextFloat() * f) + (double)(1F - f) * 0.5D;
	            
	            EntityItem entityitem = (EntityItem) EntityList.createEntityOfType(BloodWoodSaplingItemEntity.class, world, (double)i + d, (double)j + d1, (double)k + d2, itemStack );
	            
	            entityitem.delayBeforeCanPickup = 10;
	            world.spawnEntityInWorld(entityitem);
	            
	            return;
	        }
		}
		else
		{
			super.dropBlockAsItem_do( world, i, j, k, itemStack );
		}
    }
    
	@Override
    public void harvestBlock( World world, EntityPlayer entityPlayer, int i, int j, int k, int iMetaData )
    {
        if ( 
    		!world.isRemote && 
    		entityPlayer.getCurrentEquippedItem() != null && 
    		entityPlayer.getCurrentEquippedItem().getItem() instanceof ShearsItem &&
			iMetaData == SUBTYPE_BLOOD_LEAVES
		)
        {
            dropBlockAsItem_do( world, i, j, k, new ItemStack( BTWBlocks.bloodWoodLeaves, 1, 0 ) );
            
            entityPlayer.getCurrentEquippedItem().damageItem( 1, entityPlayer );
        } 
        else
        {
            super.harvestBlock( world, entityPlayer, i, j, k, iMetaData );
        }
    }
    
	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool( World world, int i, int j, int k )
    {
    	int iSubtype = getSubtype(world, i, j, k);
    	
    	if (iSubtype == SUBTYPE_VINE_TRAP ||
			iSubtype == SUBTYPE_VINE_TRAP_TRIGGERED_BY_ENTITY ||
			iSubtype == SUBTYPE_VINE_TRAP_UPSIDE_DOWN_TRIGGERED_BY_ENTITY ||
			iSubtype == SUBTYPE_VINE_TRAP_UPSIDE_DOWN ||
			iSubtype == SUBTYPE_BLOOD_WOOD_SAPLING)
    	{
			return null;
    	}
    	
    	return super.getCollisionBoundingBoxFromPool( world, i, j, k );
    }

    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
    	int iSubtype = getSubtype(blockAccess, i, j, k);
    	
    	return getBlockBoundsFromPoolBasedOnSubtype(iSubtype);
    }
    
	@Override
    public int tickRate( World world )
    {
        return TICK_RATE;
    }
    
	@Override
    public float getMovementModifier(World world, int i, int j, int k)
    {
		return 0.8F;
    }
    
	@Override
    public void onEntityCollidedWithBlock( World world, int i, int j, int k, Entity entity )
    {
    	int iSubtype = getSubtype(world, i, j, k);
    	
    	if (iSubtype == SUBTYPE_VINE_TRAP ||
			iSubtype == SUBTYPE_VINE_TRAP_UPSIDE_DOWN ||
			iSubtype == SUBTYPE_VINE_TRAP_TRIGGERED_BY_ENTITY ||
			iSubtype == SUBTYPE_VINE_TRAP_UPSIDE_DOWN_TRIGGERED_BY_ENTITY ||
			iSubtype == SUBTYPE_BLOOD_WOOD_SAPLING)
    	{
        	if (entity.isAffectedByMovementModifiers() && entity.onGround )
        	{
	            entity.motionX *= 0.8D;
	            entity.motionZ *= 0.8D;
        	}            
		}
    }
    
	@Override
    public void updateTick( World world, int i, int j, int k, Random random )
    {
    	int iSubtype = getSubtype(world, i, j, k);
    	
    	if (iSubtype == SUBTYPE_VINE_TRAP_TRIGGERED_BY_ENTITY ||
			iSubtype == SUBTYPE_VINE_TRAP_UPSIDE_DOWN_TRIGGERED_BY_ENTITY)
    	{
    		// these subtypes are no longer relevant...just revert back to normal
    		
    		setSubtype(world, i, j, k, iSubtype - 1);
    	}    			
    	else if (iSubtype == SUBTYPE_BLOOD_WOOD_SAPLING)
    	{
    		if ( validateBloodWoodSapling(world, i, j, k) )
    		{    		
	    		if ( random.nextInt( 14 ) == 0 )
	    		{
					// verify if we're in the nether
					if (world.provider.dimensionId == -1) {
						attemptToGrowBloodwoodSapling(world, i, j, k, random);
	    	        }
	    		}
    		}
    	}
    }
    
	@Override
    public boolean onBlockSawed(World world, int i, int j, int k)
    {
    	int iSubtype = getSubtype(world, i, j, k);

		if (iSubtype == SUBTYPE_BLOOD_WOOD_SAPLING)
		{
			return false;
		}
		
		return super.onBlockSawed(world, i, j, k);
    }
    
	@Override
    public boolean doesBlockHopperEject(World world, int i, int j, int k)
    {
    	int iSubtype = getSubtype(world, i, j, k);
    	
		if (iSubtype == SUBTYPE_VINE_TRAP ||
			iSubtype == SUBTYPE_VINE_TRAP_TRIGGERED_BY_ENTITY ||
			iSubtype == SUBTYPE_BLOOD_WOOD_SAPLING ||
			iSubtype == SUBTYPE_VINE_TRAP_UPSIDE_DOWN ||
			iSubtype == SUBTYPE_VINE_TRAP_UPSIDE_DOWN_TRIGGERED_BY_ENTITY)
		{
			return false;
		}
		else
		{		
			return super.doesBlockHopperEject(world, i, j, k);
		}
    }
    
	@Override
    public boolean canPlaceBlockOnSide( World world, int i, int j, int k, 
    	int iSide, ItemStack stack )
    {
		if ( stack != null && stack.getItemDamage() == 
			AestheticVegetationBlock.SUBTYPE_BLOOD_WOOD_SAPLING)
		{
			if ( !canBloodwoodSaplingStayAtLocation(world, i, j, k) )
			{
				return false;
			}
		}
		
		return super.canPlaceBlockOnSide( world, i, j, k, iSide, stack );
    }
    
    //------------- Class Specific Methods ------------//
    
    public int getSubtype(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return blockAccess.getBlockMetadata( i, j, k );
    }
    
    public void setSubtype(World world, int i, int j, int k, int iSubtype)
    {
    	world.setBlockMetadata( i, j, k, iSubtype );
    }
    
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnSubtype(int iSubtype)
    {
    	switch ( iSubtype )
    	{
			case SUBTYPE_VINE_TRAP:
			case SUBTYPE_VINE_TRAP_TRIGGERED_BY_ENTITY:
				
		       	return AxisAlignedBB.getAABBPool().getAABB(
						0D, 0D, 0D, 1D, VINE_TRAP_HEIGHT, 1D);
		    	
			case SUBTYPE_VINE_TRAP_UPSIDE_DOWN:
			case SUBTYPE_VINE_TRAP_UPSIDE_DOWN_TRIGGERED_BY_ENTITY:
				
	        	return AxisAlignedBB.getAABBPool().getAABB(
						0D, 1D - VINE_TRAP_HEIGHT, 0D, 1D, 1D, 1D);
		    	
			case SUBTYPE_BLOOD_WOOD_SAPLING:
			case SUBTYPE_BLOOD_LEAVES:
	    	default:
	    		
	        	return AxisAlignedBB.getAABBPool().getAABB(         	
	        		0D, 0D, 0D, 1D, 1D, 1D );
    	}	
    }
    
	public boolean validateBloodWoodSapling(World world, int i, int j, int k)
	{
        if ( !canBlockStay( world, i, j, k ) )
        {
            dropBlockAsItem( world, i, j, k, world.getBlockMetadata(i, j, k), 0 );
            
            world.setBlockWithNotify( i, j, k, 0 );
            
            return false;
        }
        
        return true;
	}
	
	public void attemptToGrowBloodwoodSapling(World world, int i, int j, int k, Random random)
	{
		// check to make sure there's enough room for a trunk
		
		for (int iTempJ = j + 1; iTempJ < j + BLOOD_WOOD_SAPLING_MIN_TRUNK_HEIGHT; iTempJ++ )
		{
			// test for air and against the height limit of the world
			
			if ( iTempJ >= 256 || !world.isAirBlock( i, iTempJ, k ) )
			{
				return;
			}
		}
		
		// create the basic trunk
		
		for (int iTempJ = j; iTempJ < j + BLOOD_WOOD_SAPLING_MIN_TRUNK_HEIGHT - 1; iTempJ++ )
		{
			world.setBlockAndMetadataWithNotify( i, iTempJ, k, BTWBlocks.bloodWoodLog.blockID, 0 );
 		}
		
		// create the top of the trunk
		
		BloodWoodLogBlock bloodWoodBlock = (BloodWoodLogBlock)(BTWBlocks.bloodWoodLog);
		
		int iTrunkTopJ = j + BLOOD_WOOD_SAPLING_MIN_TRUNK_HEIGHT - 1;
		
		world.setBlockAndMetadataWithNotify( i, iTrunkTopJ, k, BTWBlocks.bloodWoodLog.blockID, 1 );
		
		bloodWoodBlock.growLeaves(world, i, iTrunkTopJ, k);
		
		// grow the top of the trunk and any surrounding blocks it produces
		
		bloodWoodBlock.grow(world, i, iTrunkTopJ, k, random);
		
    	for ( int tempI = i - 1; tempI <= i + 1; tempI++ )
    	{
	    	for ( int tempJ = iTrunkTopJ; tempJ <= iTrunkTopJ + 1; tempJ++ ) // only look at the same level and above the block
	    	{
		    	for ( int tempK = k - 1; tempK <= k + 1; tempK++ )
		    	{
		    		if ( world.getBlockId( tempI, tempJ, tempK ) == BTWBlocks.bloodWoodLog.blockID )
		    		{
		    			int iGrowthDirection = bloodWoodBlock.getFacing(world, tempI, tempJ, tempK);
		    			
		    			if ( iGrowthDirection != 0 )
		    			{
			    			if ( tempI != i || tempJ != iTrunkTopJ || tempK != k )
			    			{
			    				bloodWoodBlock.grow(world, tempI, tempJ, tempK, random);
			    			}
		    			}
		    		}
		    	}
	    	}
    	}
    	
    	// whimper sound
    	
        world.playAuxSFX( BTWEffectManager.GHAST_MOAN_EFFECT_ID, i, j, k, 0 );
	}
	
	public boolean canBloodwoodSaplingStayAtLocation(World world, int i, int j, int k)
	{
		int iBlockBelowID = world.getBlockId( i, j - 1, k );
		
		if ( iBlockBelowID == Block.slowSand.blockID )
		{
			return true;
		}
		else if ( iBlockBelowID == BTWBlocks.planter.blockID )
		{
			if (((PlanterBlock) BTWBlocks.planter).getPlanterType(world, i, j - 1, k) == PlanterBlock.TYPE_SOUL_SAND)
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean attemptToAffectBlockWithSoul(World world, int x, int y, int z) {
		int iTargetSubType = world.getBlockMetadata(x, y, z);
		
		if (iTargetSubType == AestheticVegetationBlock.SUBTYPE_BLOOD_WOOD_SAPLING) {
			
			attemptToGrowBloodwoodSapling(world, x, y, z, world.rand);
			
			return true;
		}
		return false;
	}
	
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private final static int VINE_TRAP_TEXTURE_ID = 105;
    @Environment(EnvType.CLIENT)
    private final static int BLOOD_WOOD_SAPLING_TEXTURE_ID = 108;
    @Environment(EnvType.CLIENT)
    private final static int BLOOD_LEAVES_TEXTURE_ID = 109;

    @Environment(EnvType.CLIENT)
    private Icon iconVineTrap;
    @Environment(EnvType.CLIENT)
    private Icon iconSaplingBloodWood;
    @Environment(EnvType.CLIENT)
    private Icon iconLeavesBloodWood;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		Icon vineIcon = register.registerIcon( "fcBlockVineTrap" );
		
		blockIcon = vineIcon; // for hit effects

		iconVineTrap = vineIcon;
		iconSaplingBloodWood = register.registerIcon("fcBlockSaplingBloodWood");
		iconLeavesBloodWood = register.registerIcon("fcBlockLeavesBloodWood_old");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
    	int iSubtype = iMetadata;
    	
    	switch ( iSubtype )
    	{
    		case SUBTYPE_VINE_TRAP:
    		case SUBTYPE_VINE_TRAP_TRIGGERED_BY_ENTITY:
    		case SUBTYPE_VINE_TRAP_UPSIDE_DOWN:
    		case SUBTYPE_VINE_TRAP_UPSIDE_DOWN_TRIGGERED_BY_ENTITY:
    			
    			return iconVineTrap;
    			
    		case SUBTYPE_BLOOD_WOOD_SAPLING:
    			
    			return iconSaplingBloodWood;
    			
    		case SUBTYPE_BLOOD_LEAVES:
    			
    			return iconLeavesBloodWood;
    	}
    	
    	return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks( int iBlockID, CreativeTabs creativeTabs, List list )
    {
        list.add( new ItemStack(iBlockID, 1, SUBTYPE_VINE_TRAP));
        list.add( new ItemStack(iBlockID, 1, SUBTYPE_BLOOD_WOOD_SAPLING));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {    	
    	if ( iSide >= 2 )
    	{
	    	if ( blockAccess.getBlockId( iNeighborI, iNeighborJ, iNeighborK ) == blockID )
	    	{
	    		int iTargetSubtype = blockAccess.getBlockMetadata( iNeighborI, iNeighborJ, iNeighborK );
	    		
	    		if (iTargetSubtype == SUBTYPE_VINE_TRAP ||
					iTargetSubtype == SUBTYPE_VINE_TRAP_TRIGGERED_BY_ENTITY)
	    		{
	    			BlockPos sourcePos = new BlockPos( iNeighborI, iNeighborJ, iNeighborK );
	    			
	    			sourcePos.addFacingAsOffset(Block.getOppositeFacing(iSide));
	    			
	    			int iSourceSubtype = blockAccess.getBlockMetadata(sourcePos.x, sourcePos.y, sourcePos.z);
	    			
		    		if (iSourceSubtype == SUBTYPE_VINE_TRAP ||
						iSourceSubtype == SUBTYPE_VINE_TRAP_TRIGGERED_BY_ENTITY)
		    		{
		    			return false;
		    		}
	    		}
	    		else if (iTargetSubtype == SUBTYPE_VINE_TRAP_UPSIDE_DOWN ||
						 iTargetSubtype == SUBTYPE_VINE_TRAP_UPSIDE_DOWN_TRIGGERED_BY_ENTITY)
	    		{
	    			BlockPos sourcePos = new BlockPos( iNeighborI, iNeighborJ, iNeighborK );
	    			
	    			sourcePos.addFacingAsOffset(Block.getOppositeFacing(iSide));
	    			
	    			int iSourceSubtype = blockAccess.getBlockMetadata(sourcePos.x, sourcePos.y, sourcePos.z);
	    			
		    		if (iSourceSubtype == SUBTYPE_VINE_TRAP_UPSIDE_DOWN ||
						iSourceSubtype == SUBTYPE_VINE_TRAP_UPSIDE_DOWN_TRIGGERED_BY_ENTITY)
		    		{
		    			return false;
		    		}
	    		}
	    	}
    	}
    	
    	return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked( World world, int i, int j, int k )
    {
		int iMetadata = world.getBlockMetadata( i, j, k );
		
		if (iMetadata == SUBTYPE_BLOOD_LEAVES)
		{
			return BTWBlocks.bloodWoodLeaves.blockID;
		}
		
        return idDropped( iMetadata, world.rand, 0 );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getDamageValue( World world, int i, int j, int k )
    {
		// used only by pick block
		
		int iMetadata = world.getBlockMetadata( i, j, k );
		
		if (iMetadata == SUBTYPE_BLOOD_LEAVES)
		{
			return 0;
		}
		
		return super.getDamageValue( world, i, j, k );		
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	IBlockAccess blockAccess = renderBlocks.blockAccess;
    	
    	int iSubtype = getSubtype(blockAccess, i, j, k);

        renderBlocks.setRenderBounds(getBlockBoundsFromPoolBasedOnSubtype(iSubtype));
        
    	switch ( iSubtype )
    	{
			case SUBTYPE_BLOOD_WOOD_SAPLING:
				
		    	return renderBlocks.renderCrossedSquares( this, i, j, k );    	
    			
			default:
				
				return renderBlocks.renderStandardBlock( this, i, j, k );
    	}				
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
    	renderBlocks.setRenderBounds(getBlockBoundsFromPoolBasedOnSubtype(iItemDamage));
    	
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, this,
											   -0.5F, -0.5F, -0.5F, iItemDamage);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean doesItemRenderAsBlock(int iItemDamage)
    {
    	if (iItemDamage == SUBTYPE_BLOOD_WOOD_SAPLING)
    	{
    		// force bloodwood saplings to use the item renderer despite being a block
    		
    		return false;
    	}
    	else
    	{
    		return super.doesItemRenderAsBlock(iItemDamage);
    	}
    }    
}