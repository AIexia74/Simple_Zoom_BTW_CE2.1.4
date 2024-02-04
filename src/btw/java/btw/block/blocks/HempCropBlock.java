// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.item.BTWItems;
import btw.item.items.ShearsItem;
import btw.item.util.ItemUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class HempCropBlock extends BlockCrops
{
	static private final double COLLISION_BOX_WIDTH = 0.4F;
	static private final double COLLISION_BOX_HALF_WIDTH = (COLLISION_BOX_WIDTH / 2F );

	static private final float BASE_GROWTH_CHANCE = 0.1F;

    public HempCropBlock(int iBlockID)
    {
        super( iBlockID );

        setHardness( 0.2F );
        setAxesEffectiveOn(true);
        
        setBuoyant();
		setFireProperties(Flammability.CROPS);
		
		initBlockBounds(0.5D - COLLISION_BOX_HALF_WIDTH, 0D, 0.5D - COLLISION_BOX_HALF_WIDTH,
                        0.5D + COLLISION_BOX_HALF_WIDTH, 1D, 0.5D + COLLISION_BOX_HALF_WIDTH);
		
        setUnlocalizedName( "fcBlockHemp" );
    }
    
	@Override
    public int idDropped( int iMetadata, Random random, int iFortuneModifier )
    {
        if ( iMetadata >= 7 )
        {
            return getCropItem();
        }
        
        return 0;
    }

    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
            IBlockAccess blockAccess, int i, int j, int k)
    {
        int iGrowthLevel = blockAccess.getBlockMetadata( i, j, k );
        iGrowthLevel = MathHelper.clamp_int( iGrowthLevel, 0, 7 );
        
        double dBoundsHeight = ( 1 + iGrowthLevel ) / 8D;        
        double dHalfWidth = COLLISION_BOX_HALF_WIDTH;
        
    	int iWeedsGrowthLevel = getWeedsGrowthLevel(blockAccess, i, j, k);
    	
    	if ( iWeedsGrowthLevel > 0 )
    	{
    		dBoundsHeight = Math.max( dBoundsHeight, 
    			WeedsBlock.getWeedsBoundsHeight(iWeedsGrowthLevel));
    		
    		dHalfWidth = WeedsBlock.WEEDS_BOUNDS_HALF_WIDTH;
    	}
    	
    	return AxisAlignedBB.getAABBPool().getAABB(         	
    		0.5D - dHalfWidth, 0D, 0.5D - dHalfWidth, 
    		0.5D + dHalfWidth, dBoundsHeight, 0.5D + dHalfWidth );
    }
	
	@Override
    public boolean doesBlockDropAsItemOnSaw(World world, int i, int j, int k)
    {
		return true;
    }
    
    @Override
    public boolean canBlockStay( World world, int i, int j, int k )
    {
        return super.canBlockStay( world, i, j, k ) || 
        	( world.getBlockId( i, j - 1, k ) == blockID && !getIsTopBlock(world, i, j - 1, k) );
    }
    
	@Override
    protected void attemptToGrow(World world, int i, int j, int k, Random rand)
    {
        if(!getIsTopBlock(world, i, j, k) &&
           getWeedsGrowthLevel(world, i, j, k) == 0 &&
           ( world.getBlockLightValue( i, j, k ) >= 15 ||
    		world.getBlockId( i, j + 1, k ) == BTWBlocks.lightBlockOn.blockID ||
    		world.getBlockId( i, j + 2, k ) == BTWBlocks.lightBlockOn.blockID ) )
        {
            Block blockBelow = Block.blocksList[world.getBlockId( i, j - 1, k )];            
        	
            if ( blockBelow != null && 
            	blockBelow.isBlockHydratedForPlantGrowthOn(world, i, j - 1, k) )
            {
                // only the base of the plants grows, and only does if its on hydrated soil
        		
                int iMetadata = world.getBlockMetadata( i, j, k );
                
	            if (getGrowthLevel(world, i, j, k) < 7 )
	            {
	            	float fChanceOfGrowth = getBaseGrowthChance(world, i, j, k) *
                                            blockBelow.getPlantGrowthOnMultiplier(world, i, j - 1, k, this);
	            	
	                if ( rand.nextFloat() <= fChanceOfGrowth )
	                {
		            	incrementGrowthLevel(world, i, j, k);
	                }
	            }
	            else if ( world.isAirBlock( i, j + 1, k ) )
	            {
	            	// check for growth of top block
	            	
	            	float fChanceOfGrowth = (getBaseGrowthChance(world, i, j, k) / 4F ) *
                                            blockBelow.getPlantGrowthOnMultiplier(world, i, j - 1, k, this);
	            	
	                if ( rand.nextFloat() <= fChanceOfGrowth )
                    {
                		// top of the plant
	                	
	                	int iNewMetadata = setIsTopBlock(0, true);
                		
                		world.setBlockAndMetadataWithNotify( i, j + 1, k, blockID, iNewMetadata );
                    	
                		blockBelow.notifyOfFullStagePlantGrowthOn(world, i, j - 1, k, this);
                    }
            	}
            }
        }
    }

	@Override
    public void dropSeeds(World world, int i, int j, int k, int iMetadata,
                          float fChance, int iFortuneModifier)
    {
        if (getIsTopBlock(iMetadata) && world.rand.nextInt(100) < 50 )
        {
        	ItemUtils.dropStackAsIfBlockHarvested(world, i, j, k,
                                                  new ItemStack( getSeedItem(), 1, 0 ));
        }
    }
    
    @Override
    protected int getSeedItem()
    {
        return BTWItems.hempSeeds.itemID;
    }

    @Override
    protected int getCropItem()
    {
        return BTWItems.hemp.itemID;
    }
    
	@Override
    public float getBaseGrowthChance(World world, int i, int j, int k)
    {
    	return 0.1F;
    }
    
	@Override
    public void harvestBlock( World world, EntityPlayer player, int i, int j, int k, int iMetadata )
    {
		super.harvestBlock( world, player, i, j, k, iMetadata );
		
        if( !world.isRemote )
        {
        	// kill the plant below if not harvested by shears
        	
        	if ( player.getCurrentEquippedItem() == null ||
        		!(player.getCurrentEquippedItem().getItem() instanceof ShearsItem))
        	{
        		if ( world.getBlockId( i, j - 1, k ) == blockID )
        		{
        			dropBlockAsItem( world, i, j - 1, k, 
        				world.getBlockMetadata( i, j - 1, k ), 0 );
        			
        			world.setBlockToAir( i, j - 1, k );
        		}
        	}
        }
    }
	
    //------------- Class Specific Methods ------------//

    protected boolean getIsTopBlock(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getIsTopBlock(blockAccess.getBlockMetadata(i, j, k));
    }
    
    protected boolean getIsTopBlock(int iMetadata)
    {
    	return ( iMetadata & 8 ) != 0;
    }
    
    protected void setIsTopBlock(World world, int i, int j, int k, boolean bTop)
    {
    	int iMetadata = setIsTopBlock(world.getBlockMetadata(i, j, k), bTop);
    	
    	world.setBlockMetadataWithNotify( i, j, k, iMetadata );
    }
    
    protected int setIsTopBlock(int iMetadata, boolean bTop)
    {
    	if ( bTop )
    	{
    		iMetadata |= 8;
    	}
    	else
    	{
    		iMetadata &= (~8);
    	}
    	
    	return iMetadata;    	
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconBottomByGrowthArray = new Icon[8];
    @Environment(EnvType.CLIENT)
    private Icon iconTop;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        iconTop = register.registerIcon("fcBlockHemp_top");
		
		blockIcon = iconTop; // for hit effects

        iconBottomByGrowthArray[0] = register.registerIcon("fcBlockHemp_bottom_00");
        iconBottomByGrowthArray[1] = register.registerIcon("fcBlockHemp_bottom_01");
        iconBottomByGrowthArray[2] = register.registerIcon("fcBlockHemp_bottom_02");
        iconBottomByGrowthArray[3] = register.registerIcon("fcBlockHemp_bottom_03");
        iconBottomByGrowthArray[4] = register.registerIcon("fcBlockHemp_bottom_04");
        iconBottomByGrowthArray[5] = register.registerIcon("fcBlockHemp_bottom_05");
        iconBottomByGrowthArray[6] = register.registerIcon("fcBlockHemp_bottom_06");
        iconBottomByGrowthArray[7] = register.registerIcon("fcBlockHemp_bottom_07");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
        if ( getIsTopBlock(iMetadata) )
        {
        	return iconTop;
        }
        
    	return iconBottomByGrowthArray[iMetadata];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
    	renderer.renderCrossedSquares( this, i, j, k );
 
    	if ( !getIsTopBlock(renderer.blockAccess, i, j, k) )
		{
    		BTWBlocks.weeds.renderWeeds(this, renderer, i, j, k);
		}
		
    	return true;
    }    
}