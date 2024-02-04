package net.minecraft.src;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Random;

public class BlockCrops extends BlockFlower
{
    @Environment(EnvType.CLIENT)
    private Icon[] iconArray;

    protected BlockCrops(int par1)
    {
        super(par1);
        this.setTickRandomly(true);
        float var2 = 0.5F;
        initBlockBounds(0.5F - var2, 0.0F, 0.5F - var2, 0.5F + var2, 0.25F, 0.5F + var2);
        this.setCreativeTab((CreativeTabs)null);
        this.setHardness(0.0F);
        this.setStepSound(soundGrassFootstep);
        this.disableStats();
    }

    /**
     * Apply bonemeal to the crops.
     */
    public void fertilize(World par1World, int par2, int par3, int par4)
    {
        int var5 = par1World.getBlockMetadata(par2, par3, par4) + MathHelper.getRandomIntegerInRange(par1World.rand, 2, 5);

        if (var5 > 7)
        {
            var5 = 7;
        }

        par1World.setBlockMetadataWithNotify(par2, par3, par4, var5, 2);
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int par1, int par2)
    {
        if (par2 < 0 || par2 > 7)
        {
            par2 = 7;
        }

        return this.iconArray[par2];
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 6;
    }

    /**
     * Generate a seed ItemStack for this crop.
     */
    protected int getSeedItem()
    {
        return Item.seeds.itemID;
    }

    /**
     * Generate a crop produce ItemStack for this crop.
     */
    protected int getCropItem()
    {
        return Item.wheat.itemID;
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropBlockAsItemWithChance(World par1World, int par2, int par3, int par4, int par5, float par6, int par7)
    {
        super.dropBlockAsItemWithChance(par1World, par2, par3, par4, par5, par6, 0);

        if (!par1World.isRemote)
        {
            if (par5 >= 7)
            {
            	dropSeeds(par1World, par2, par3, par4, par5, par6, par7);
            }
        }
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random par1Random)
    {
        return 1;
    }

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    @Environment(EnvType.CLIENT)
    public int idPicked(World par1World, int par2, int par3, int par4)
    {
        return this.getSeedItem();
    }

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.iconArray = new Icon[8];

        for (int var2 = 0; var2 < this.iconArray.length; ++var2)
        {
            this.iconArray[var2] = par1IconRegister.registerIcon("crops_" + var2);
        }
    }

    // FCMOD: Added New    
    @Override
    public int idDropped( int iMetadata, Random random, int iFortuneModifier )
    {
    	if ( iMetadata == 7 )
    	{
    		return getCropItem();
    	}
    	
        return 0;
    }
    
    @Override
    public boolean canBeGrazedOn(IBlockAccess blockAccess, int i, int j, int k,
                                 EntityAnimal animal)
    {
		return true;
    }
    
    @Override
    public void onGrazed(World world, int i, int j, int k, EntityAnimal animal)
    {
    	// drop the block as an item so that animals can get the base graze value + eat
    	// any tasties that drop
    	
        dropBlockAsItem( world, i, j, k, world.getBlockMetadata( i, j, k ), 0 );
        
        super.onGrazed(world, i, j, k, animal);
    }
    
    @Override
    public void updateTick( World world, int i, int j, int k, Random rand )
    {
        super.updateTick( world, i, j, k, rand );

    	// make sure parent update didn't destroy the block, and prevent crops growing in the end
    	// note that CanGrowOnBlock() has already been confirmed in parent method    	
    	
        if ( world.provider.dimensionId != 1 && 
        	world.getBlockId( i, j, k ) == blockID )
        {
        	attemptToGrow(world, i, j, k, rand);
        }
    }

    @Override
    protected boolean canGrowOnBlock(World world, int i, int j, int k)
    {
    	Block blockOn = Block.blocksList[world.getBlockId( i, j, k )];
    	
    	return blockOn != null && blockOn.canDomesticatedCropsGrowOnBlock(world, i, j, k);
    }
    
	@Override
	public boolean canWeedsGrowInBlock(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
    //------------- Class Specific Methods ------------//
    
    protected void attemptToGrow(World world, int i, int j, int k, Random rand)
    {
    	if (getWeedsGrowthLevel(world, i, j, k) == 0 &&
            getGrowthLevel(world, i, j, k) < 7 &&
	    	world.getBlockLightValue( i, j + 1, k ) >= 9 )
	    {
	        Block blockBelow = Block.blocksList[world.getBlockId( i, j - 1, k )];
	        
	        if ( blockBelow != null && 
	        	blockBelow.isBlockHydratedForPlantGrowthOn(world, i, j - 1, k) )
	        {
	    		float fGrowthChance = getBaseGrowthChance(world, i, j, k) *
                                      blockBelow.getPlantGrowthOnMultiplier(world, i, j - 1, k, this);
	    		
	            if ( rand.nextFloat() <= fGrowthChance )
	            {
	            	incrementGrowthLevel(world, i, j, k);
	            }
	        }
	    }
    }
    
    public void dropSeeds(World world, int i, int j, int k, int iMetadata,
                          float fChance, int iFortuneModifier)
    {
        dropBlockAsItem_do(world, i, j, k, new ItemStack( getSeedItem(), 1, 0 ) );
        
        if ( world.rand.nextInt( 16 ) - iFortuneModifier < 4 )
        {
            dropBlockAsItem_do(world, i, j, k, new ItemStack( getSeedItem(), 1, 0 ) );
        }
    }
    
    public float getBaseGrowthChance(World world, int i, int j, int k)
    {
    	return 0.05F;
    }
    
    protected void incrementGrowthLevel(World world, int i, int j, int k)
    {
    	int iGrowthLevel = getGrowthLevel(world, i, j, k) + 1;
    	
        setGrowthLevel(world, i, j, k, iGrowthLevel);
        
        if ( iGrowthLevel == 7 )
        {
        	Block blockBelow = Block.blocksList[world.getBlockId( i, j - 1, k )];
        	
        	if ( blockBelow != null )
        	{
        		blockBelow.notifyOfFullStagePlantGrowthOn(world, i, j - 1, k, this);
        	}
        }
    }
    
    protected int getGrowthLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getGrowthLevel(blockAccess.getBlockMetadata(i, j, k));
    }
    
    protected int getGrowthLevel(int iMetadata)
    {
    	return iMetadata & 7;
    }
    
    protected void setGrowthLevel(World world, int i, int j, int k, int iLevel)
    {
    	int iMetadata = world.getBlockMetadata( i, j, k ) & (~7); // filter out old level   	
    	
    	world.setBlockMetadataWithNotify( i, j, k, iMetadata | iLevel );
    }
    
    protected void setGrowthLevelNoNotify(World world, int i, int j, int k, int iLevel)
    {
    	int iMetadata = world.getBlockMetadata( i, j, k ) & (~7); // filter out old level   	
    	
    	world.setBlockMetadata( i, j, k, iMetadata | iLevel );
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
    	renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
    		renderer.blockAccess, i, j, k) );
        
    	renderer.renderBlockCrops( this, i, j, k );
    	
    	BTWBlocks.weeds.renderWeeds(this, renderer, i, j, k);

		return true;
    }    
    // END FCMOD
}
