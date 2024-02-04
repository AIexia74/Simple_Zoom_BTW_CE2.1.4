// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public abstract class FarmlandBlockBase extends Block
{
    protected FarmlandBlockBase(int iBlockID )
    {
    	super( iBlockID, Material.ground );
    	
    	setHardness( 0.6F );
    	setShovelsEffectiveOn(true);
    	
        initBlockBounds(0F, 0F, 0F, 1F, 0.9375F, 1F);
        
        setLightOpacity( 255 );
        useNeighborBrightness[iBlockID] = true;
    	
        setTickRandomly( true );
        
        setStepSound( soundGravelFootstep );
    }
    
	@Override
    public void updateTick(World world, int i, int j, int k, Random rand )
    {
        if (hasIrrigatingBlocks(world, i, j, k) || world.isRainingAtPos(i, j + 1, k) )
        {
        	setFullyHydrated(world, i, j, k);
        }
        else if ( isHydrated(world, i, j, k) )
    	{
        	dryIncrementally(world, i, j, k);
    	}
        else
        {
        	checkForSoilReversion(world, i, j, k);
        }
    }
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k )
	{		
		// full block bounds for entity collisions
		
	    return AxisAlignedBB.getAABBPool().getAABB( 0D, 0D, 0D, 1D, 1D, 1D ).offset( i, j, k );
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
    public void onFallenUpon(World world, int i, int j, int k, Entity entity, float fFallDist )
    {
		// min fall dist of 0.75 (previously 0.5) so that the player can safely 
		// step off slabs without potentially ruining crops
		
        if ( !world.isRemote && world.rand.nextFloat() < fFallDist - 0.75F )
        {
            world.setBlockWithNotify( i, j, k, BTWBlocks.looseDirt.blockID );
        }
    }

    @Override
    public float getMovementModifier(World world, int i, int j, int k)
    {
    	return 0.8F;
    }    
    
    @Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
    {
        return BTWBlocks.looseDirt.blockID;
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k,
                                                int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, BTWItems.dirtPile.itemID,
                              6, 0, fChanceOfDrop);
		
		return true;
	}
	
	@Override
    public boolean canBePistonShoveled(World world, int i, int j, int k)
    {
    	return true;
    }
	
    @Override
	public void onVegetationAboveGrazed(World world, int i, int j, int k, EntityAnimal animal)
	{
        if ( animal.getDisruptsEarthOnGraze() )
        {
        	world.setBlockWithNotify( i, j, k, BTWBlocks.looseDirt.blockID );
        	
        	notifyNeighborsBlockDisrupted(world, i, j, k);
        }
	}
    
	@Override
    public boolean canDomesticatedCropsGrowOnBlock(World world, int i, int j, int k)
    {
    	return true;
    }
    
	@Override
    public boolean canWildVegetationGrowOnBlock(World world, int i, int j, int k)
    {
    	return true;
    }
	
	@Override
	public boolean isBlockHydratedForPlantGrowthOn(World world, int i, int j, int k)
	{
    	return isHydrated(world, i, j, k);
	}
	
	@Override
    public void onEntityCollidedWithBlock( World world, int i, int j, int k, Entity entity )
    {
		if (!world.isRemote && !isFertilized(world, i, j, k) &&
            entity.isEntityAlive() && entity instanceof EntityItem )
		{
			EntityItem entityItem = (EntityItem)entity;
			ItemStack stack = entityItem.getEntityItem();			
			
			if ( stack.itemID == Item.dyePowder.itemID && stack.getItemDamage() == 15 ) // bone meal
			{
				stack.stackSize--;
				
				if ( stack.stackSize <= 0 )
				{
					entityItem.setDead();
				}
				
				setFertilized(world, i, j, k);
            	
	            world.playSoundEffect( i + 0.5D, j + 0.5D, k + 0.5D, "random.pop", 0.25F, 
            		( ( world.rand.nextFloat() - world.rand.nextFloat() ) * 0.7F + 1F ) * 2F );
			}
		}
    }
    
    @Override
    public float groundCoverRestingOnVisualOffset(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return -( 1F / 16F );        
    }
    
	@Override
	public boolean attemptToApplyFertilizerTo(World world, int i, int j, int k)
	{
		if ( !isFertilized(world, i, j, k) )
		{
			setFertilized(world, i, j, k);
			
			return true;
		}
		
		return false;
	}
	
	@Override
    public boolean getCanBlightSpreadToBlock(World world, int i, int j, int k, int iBlightLevel)
    {
    	return iBlightLevel >= 1;
    }
    
    //------------- Class Specific Methods ------------//
    
    protected boolean isHydrated(World world, int i, int j, int k)
    {
    	return isHydrated(world.getBlockMetadata(i, j, k));
    }
    
    protected abstract boolean isHydrated(int iMetadata);
    
    protected void setFullyHydrated(World world, int i, int j, int k)
    {
    	int iMetadata = setFullyHydrated(world.getBlockMetadata(i, j, k));
    	
    	world.setBlockMetadataWithNotify( i, j, k, iMetadata );
    }
    
    protected abstract int setFullyHydrated(int iMetadata);
    
	protected abstract void dryIncrementally(World world, int i, int j, int k);
	
    protected abstract boolean isFertilized(IBlockAccess blockAccess, int i, int j, int k);
    
    protected void setFertilized(World world, int i, int j, int k)
    {
    }
    
    protected int getHorizontalHydrationRange(World world, int i, int j, int k)
    {
    	return 4;
    }
    
    protected boolean hasIrrigatingBlocks(World world, int i, int j, int k)
    {
    	int iHorizontalRange = getHorizontalHydrationRange(world, i, j, k);
    	
        for ( int iTempI = i - iHorizontalRange; iTempI <= i + iHorizontalRange; iTempI++ )
        {
            for ( int iTempJ = j; iTempJ <= j + 1; iTempJ++ )
            {
                for ( int iTempK = k - iHorizontalRange; iTempK <= k + iHorizontalRange; iTempK++ )
                {
                    if ( world.getBlockMaterial( iTempI, iTempJ, iTempK ) == Material.water )
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }
    
    protected boolean doesBlockAbovePreventSoilReversion(World world, int i, int j, int k)
    {
    	// this approach sucks, but given this is a deprecated functionality that won't
    	// apply to additional block types, just leave it alone.
    	
        int iBlockAboveID = world.getBlockId( i, j + 1, k );

        if ( iBlockAboveID == Block.crops.blockID || 
        	iBlockAboveID == Block.melonStem.blockID || 
        	iBlockAboveID == Block.pumpkinStem.blockID || 
        	iBlockAboveID == Block.potato.blockID || 
        	iBlockAboveID == Block.carrot.blockID )
        {
            return true;
        }
        
        return false;
    }

	protected void checkForSoilReversion(World world, int i, int j, int k)
	{
		if ( !doesBlockAbovePreventSoilReversion(world, i, j, k) )
		{
			world.setBlockWithNotify( i, j, k, BTWBlocks.looseDirt.blockID );
		}
	}

	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    protected Icon iconTopWet;
    @Environment(EnvType.CLIENT)
    protected Icon iconTopDry;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		blockIcon = register.registerIcon( "dirt" );

        iconTopWet = register.registerIcon("farmland_wet");
        iconTopDry = register.registerIcon("farmland_dry");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		if ( iSide == 1 )
		{
			if ( isHydrated(iMetadata) )
			{
				return iconTopWet;
			}				
			
			return iconTopDry;
		}
		
        return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked( World world, int i, int j, int k )
    {
        return BTWBlocks.farmland.blockID;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int i, int j, int k, int iSide )
    {
    	return iSide == 1 || super.shouldSideBeRendered( blockAccess, i, j, k, iSide );
    }    
}

