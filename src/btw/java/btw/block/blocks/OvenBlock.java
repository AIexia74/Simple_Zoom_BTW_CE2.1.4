// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.model.BlockModel;
import btw.block.model.OvenModel;
import btw.block.tileentity.OvenTileEntity;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class OvenBlock extends FurnaceBlock
{
    protected final BlockModel modelBlockInterior = new OvenModel();
    
    protected final float clickYTopPortion = (6F / 16F );
    protected final float clickYBottomPortion = (6F / 16F );
    	
    protected OvenBlock(int iBlockID, boolean bIsLit )
    {
        super( iBlockID, bIsLit );
        
        setPicksEffectiveOn();
        
        setHardness( 2F );
        setResistance( 3.33F ); // need to override resistance set in parent
        
        setUnlocalizedName( "fcBlockFurnaceBrick" );        
    }
    
    @Override
    public TileEntity createNewTileEntity(World world )
    {
        return new OvenTileEntity();
    }

	@Override
    public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
    {
        int iMetadata = world.getBlockMetadata( i, j, k );
		int iBlockFacing = iMetadata & 7;
		
		if ( iBlockFacing != iFacing )
		{
			// block is only accessible from front
			
			return false;
		}

    	ItemStack heldStack = player.getCurrentEquippedItem();
        OvenTileEntity tileEntity = (OvenTileEntity)world.getBlockTileEntity( i, j, k );
        ItemStack cookStack = tileEntity.getCookStack();
    	
        if (fYClick > clickYTopPortion)
        {
        	if ( cookStack != null )
        	{
				tileEntity.givePlayerCookStack(player, iFacing);
				
				return true;
        	}
        	else
        	{
				if ( heldStack != null && isValidCookItem(heldStack) )
				{
					if ( !world.isRemote )
					{
						tileEntity.addCookStack(new ItemStack(heldStack.itemID, 1, heldStack.getItemDamage() ));
					}
    				
	    			heldStack.stackSize--;
	    			
	    			return true;
				}
        	}
        }
        else if (fYClick < clickYBottomPortion && heldStack != null )
        {
        	// handle fuel here
        	
    		Item item = heldStack.getItem();    		
			int iItemDamage = heldStack.getItemDamage();
			
    		if ( item.getCanBeFedDirectlyIntoBrickOven(iItemDamage) )
    		{
    			if ( !world.isRemote )
    			{
    				int iItemsConsumed = tileEntity.attemptToAddFuel(heldStack);
    				
	                if ( iItemsConsumed > 0 )
	                {
	                	if ( isActive )
	                	{
			                world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
			                	"mob.ghast.fireball", 0.2F + world.rand.nextFloat() * 0.1F, world.rand.nextFloat() * 0.25F + 1.25F );
	                	}
	                	else
	                	{
	        	            world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
	                    		"random.pop", 0.25F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
	                	}
		                
	        			heldStack.stackSize -= iItemsConsumed;	        			
	                }
    			}
                
    			return true;
    		}
        }
        
		return false;
    }
	
    @Override
    public int quantityDropped( Random rand )
    {
        return 4 + rand.nextInt( 6 );
    }

    @Override
    public int idDropped( int iMetaData, Random random, int iFortuneModifier )
    {
        return Item.brick.itemID;
    }
    
    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata)
    {
        dropBlockAsItem( world, i, j, k, iMetadata, 0 );
    }
    
	@Override
    public boolean canPlaceBlockAt( World world, int i, int j, int k )
    {
		if ( !WorldUtils.doesBlockHaveSolidTopSurface(world, i, j - 1, k) )
		{
			return false;
		}
		
        return super.canPlaceBlockAt( world, i, j, k );
    }
	
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iBlockID )
    {    	
        if ( !WorldUtils.doesBlockHaveSolidTopSurface(world, i, j - 1, k) )
        {
            dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
            
            world.setBlockWithNotify( i, j, k, 0 );
        }
    }
    
    @Override
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
    	int iBlockFacing = blockAccess.getBlockMetadata( i, j, k ) & 7;
    	
    	return iBlockFacing != iFacing;
	}

    @Override
    public void updateFurnaceBlockState( boolean bBurning, World world, int i, int j, int k, boolean bHasContents )
    {
        int iMetadata = world.getBlockMetadata( i, j, k );
        TileEntity tileEntity = world.getBlockTileEntity( i, j, k );
        
        keepFurnaceInventory = true;

        if ( bBurning )
        {
            world.setBlock( i, j, k, BTWBlocks.burningOven.blockID );
        }
        else
        {
            world.setBlock( i, j, k, BTWBlocks.idleOven.blockID );
        }

        keepFurnaceInventory = false;
        
        if ( !bHasContents )
        {
        	iMetadata = iMetadata & 7;
        }
        else
        {
        	iMetadata = iMetadata | 8;
        }
        
        world.SetBlockMetadataWithNotify( i, j, k, iMetadata, 2 );

        if ( tileEntity != null )
        {
            tileEntity.validate();
            world.setBlockTileEntity( i, j, k, tileEntity );
        }
    }

	@Override
    public boolean getCanBeSetOnFireDirectly(IBlockAccess blockAccess, int i, int j, int k)
    {
		if ( !isActive )
		{
	        OvenTileEntity tileEntity = (OvenTileEntity)blockAccess.getBlockTileEntity( i, j, k );
	        
	        // uses the visual fuel level rather than the actualy fuel level so this will work on the client
	        
	        if (tileEntity.getVisualFuelLevel() > 0 )
	        {
	        	return true;
	        }
		}
		
    	return false;
    }
    
	@Override
    public boolean setOnFireDirectly(World world, int i, int j, int k)
    {
		if ( !isActive )
		{
	        OvenTileEntity tileEntity = (OvenTileEntity)world.getBlockTileEntity( i, j, k );
	        
	        if ( tileEntity.attemptToLight() )
	        {
	            world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
	            	"mob.ghast.fireball", 1.0F, world.rand.nextFloat() * 0.4F + 0.8F );
	            
	            return true;
	        }	            
		}
		
		return false;
    }
    
	@Override
    public int getChanceOfFireSpreadingDirectlyTo(IBlockAccess blockAccess, int i, int j, int k)
    {
		if ( !isActive )
		{
	        OvenTileEntity tileEntity = (OvenTileEntity)blockAccess.getBlockTileEntity( i, j, k );
	        
	        if ( tileEntity.hasValidFuel() )
	        {
				return 60; // same chance as leaves and other highly flammable objects
	        }
		}
		
		return 0;
    }

	@Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
	
    @Override
	protected int iddroppedsilktouch()
	{
		return BTWBlocks.idleOven.blockID;
	}
	
    @Override
    public boolean getIsBlockWarm(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return isActive;
    }
    
    @Override
    public boolean doesBlockHopperInsert(World world, int i, int j, int k)
    {
    	return true;
    }
    
	//------------- Class Specific Methods ------------//

	public boolean isValidCookItem(ItemStack stack)
	{
		if ( FurnaceRecipes.smelting().getSmeltingResult( stack.getItem().itemID ) != null )
		{
			return true;
		}
		
		return false;
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] fuelOverlays;
    @Environment(EnvType.CLIENT)
    private Icon currentFuelOverlay = null;
    @Environment(EnvType.CLIENT)
    private Icon blankOverlay;

    @Environment(EnvType.CLIENT)
    protected boolean isRenderingInterior = false;
    @Environment(EnvType.CLIENT)
    private int interiorBrightness = 0;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        blockIcon = register.registerIcon( "fcBlockFurnaceBrick_side" );
        furnaceIconTop = register.registerIcon( "fcBlockFurnaceBrick_top" );
        
        if ( isActive )
        {
        	furnaceIconFront = register.registerIcon( "fcBlockFurnaceBrick_front_lit" );
        }
        else
        {
        	furnaceIconFront = register.registerIcon( "fcBlockFurnaceBrick_front" );
        }

		fuelOverlays = new Icon[9];

        for ( int iTempIndex = 0; iTempIndex < 9; ++iTempIndex )
        {
			fuelOverlays[iTempIndex] = register.registerIcon("fcOverlayFurnaceFuel_" + ( iTempIndex ));
        }

		blankOverlay = register.registerIcon("fcOverlayBlank");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked( World world, int i, int j, int k )
    {
        return BTWBlocks.idleOven.blockID;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		int iFacing = iMetadata & 7; 
		
		if ( iFacing < 2 || iFacing > 5 )
		{
			// have to assign default value due to item render having metadata of 0
			
			iFacing = 3;
		}
		
		if (currentFuelOverlay == null )
		{
			if ( iFacing == iSide )
			{
				return furnaceIconFront;
			}
			else if ( iSide < 2 )
			{
				return furnaceIconTop;
			}
			
			return blockIcon;
		}
		else
		{
			if ( iFacing == iSide )
			{
				return currentFuelOverlay;
			}
			
			return blankOverlay;
		}
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
    	if (isRenderingInterior)
    	{
			BlockPos myPos = new BlockPos( iNeighborI, iNeighborJ, iNeighborK, Block.getOppositeFacing(iSide) );
			
			int iFacing = blockAccess.getBlockMetadata(myPos.x, myPos.y, myPos.z) & 7;
			
			return iSide != Block.getOppositeFacing(iFacing);
    	}
    	
		return super.shouldSideBeRendered( blockAccess, iNeighborI, iNeighborJ, iNeighborK, iSide );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
    	renderer.renderStandardBlock( this, i, j, k );
    	
    	int iFacing = renderer.blockAccess.getBlockMetadata( i, j, k ) & 7;
    	
    	BlockModel transformedModel = modelBlockInterior.makeTemporaryCopy();
    	
    	transformedModel.rotateAroundYToFacing(iFacing);

    	BlockPos interiorFacesPos = new BlockPos( i, j, k, iFacing );

		interiorBrightness = getMixedBrightnessForBlock(renderer.blockAccess,
														interiorFacesPos.x, interiorFacesPos.y, interiorFacesPos.z);
    	
    	renderer.setOverrideBlockTexture( blockIcon );
		isRenderingInterior = true;
    	
        boolean bReturnValue = transformedModel.renderAsBlockWithColorMultiplier(renderer, this, i, j, k);

		isRenderingInterior = false;
        renderer.clearOverrideBlockTexture();
        
    	return bReturnValue;     	
    }
	
    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks renderer, int i, int j, int k, boolean bFirstPassResult)
    {
    	if ( bFirstPassResult ) {
			TileEntity tileEntity = renderer.blockAccess.getBlockTileEntity(i, j, k);
			if (tileEntity instanceof OvenTileEntity) {
				int iFuelLevel = ((OvenTileEntity) tileEntity).getVisualFuelLevel();
				
				if (iFuelLevel > 0) {
					iFuelLevel = MathHelper.clamp_int(iFuelLevel - 2, 0, 8);
					currentFuelOverlay = fuelOverlays[iFuelLevel];
					
					renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(renderer.blockAccess, i, j, k));
					
					renderer.renderStandardBlock(this, i, j, k);
					
					currentFuelOverlay = null;
				}
			}
		}
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getMixedBrightnessForBlock(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
    	if (isRenderingInterior)
    	{
    		return interiorBrightness;
    	}
    	
    	return super.getMixedBrightnessForBlock( par1IBlockAccess, par2, par3, par4 );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlockWithTexture(RenderBlocks renderer, int i, int j, int k, Icon texture)
    {
    	// necessary to render harvest cracks as if this is a regular block due to non-standard render above
    	
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
    	renderer.setOverrideBlockTexture( texture );
    	
        boolean bReturnValue = renderer.renderStandardBlock( this, i, j, k );
        
        renderer.clearOverrideBlockTexture();
        
        return bReturnValue;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
    	renderBlocks.renderBlockAsItemVanilla( this, iItemDamage, fBrightness );
    	
    	BlockModel transformedModel = modelBlockInterior.makeTemporaryCopy();
    	
    	transformedModel.rotateAroundYToFacing(3);
    	
    	transformedModel.renderAsItemBlock(renderBlocks, this, iItemDamage);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick( World world, int i, int j, int k, Random rand )
    {
        if ( isActive )
        {
            OvenTileEntity tileEntity = (OvenTileEntity)world.getBlockTileEntity( i, j, k );
            int iFuelLevel = tileEntity.getVisualFuelLevel();
            
            if ( iFuelLevel == 1 )
            {
	            int iFacing = world.getBlockMetadata( i, j, k ) & 7;
	            
	            float fX = (float)i + 0.5F;
	            float fY = (float)j + 0.0F + rand.nextFloat() * 6.0F / 16.0F;
	            float fZ = (float)k + 0.5F;
	            
	            float fFacingOffset = 0.52F;
	            float fRandOffset = rand.nextFloat() * 0.6F - 0.3F;
	
	            if ( iFacing == 4 )
	            {
	                world.spawnParticle( "largesmoke", fX - fFacingOffset, fY, fZ + fRandOffset, 0.0D, 0.0D, 0.0D );
	            }
	            else if ( iFacing == 5 )
	            {
	                world.spawnParticle( "largesmoke", fX + fFacingOffset, fY, fZ + fRandOffset, 0.0D, 0.0D, 0.0D );
	            }
	            else if ( iFacing == 2 )
	            {
	                world.spawnParticle( "largesmoke", fX + fRandOffset, fY, fZ - fFacingOffset, 0.0D, 0.0D, 0.0D );
	            }
	            else if ( iFacing == 3 )
	            {
	                world.spawnParticle( "largesmoke", fX + fRandOffset, fY, fZ + fFacingOffset, 0.0D, 0.0D, 0.0D );
	            }
            }
            
            ItemStack cookStack = tileEntity.getCookStack();
            
        	if ( cookStack != null && isValidCookItem(cookStack) )
	        {
	            for ( int iTempCount = 0; iTempCount < 1; ++iTempCount )
	            {
	                float fX = i + 0.375F + rand.nextFloat() * 0.25F;
	                float fY = j + 0.45F + rand.nextFloat() * 0.1F;
	                float fZ = k + 0.375F + rand.nextFloat() * 0.25F;
	                
	                world.spawnParticle( "fcwhitecloud", fX, fY, fZ, 0D, 0D, 0D );
	            }
	        }           
        }
        
        super.randomDisplayTick( world, i, j, k, rand );
    }
	@Override
	@Environment(EnvType.CLIENT)
	public void renderBlockMovedByPiston(RenderBlocks renderBlocks, int i, int j, int k) {
		// override to bypass rendering of all faces normally done with blocks moved by pistons
		renderBlock(renderBlocks, i, j, k);
	}
	
}
