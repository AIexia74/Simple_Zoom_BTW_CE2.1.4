// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.model.BlockModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class CharredStumpBlock extends Block
{
    public final static float HARDNESS = 3F; // 6 on regular stump
    
    private BlockModel blockModelsNarrowOneSide[];
    
    public CharredStumpBlock(int iBlockID )
    {
        super( iBlockID, BTWBlocks.logMaterial);
        
        setHardness(HARDNESS);
        
		setAxesEffectiveOn();
		setChiselsEffectiveOn();
        
        setBuoyant();
        
        initModels();
        
        Block.useNeighborBrightness[iBlockID] = true;
        
        setLightOpacity( 8 );
        
        setStepSound( soundGravelFootstep );
        
        setUnlocalizedName( "fcBlockStumpCharred" );
    }    
    
    @Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
    {
        return 0;
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
    public MovingObjectPosition collisionRayTrace(World world, int i, int j, int k, Vec3 startRay, Vec3 endRay )
    {
    	return getCurrentModelForBlock(world, i, j, k).collisionRayTrace(world, i, j, k, startRay, endRay);
    }
    
    @Override
	public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
		return iFacing <= 1;
	}
    
    @Override
    public boolean canConvertBlock(ItemStack stack, World world, int i, int j, int k)
    {
    	return true;
    }
    
    @Override
    public boolean convertBlock(ItemStack stack, World world, int i, int j, int k, int iFromSide)
    {
    	int iOldMetadata = world.getBlockMetadata( i, j, k );
    	int iDamageLevel = getDamageLevel(iOldMetadata);
    	
    	if ( iDamageLevel < 3 )
    	{
    		iDamageLevel++;

    		setDamageLevel(world, i, j, k, iDamageLevel);
    		
    		return true;
    	}
        
    	return false;
    }
    
    @Override
    public boolean getIsProblemToRemove(ItemStack toolStack, IBlockAccess blockAccess, int i, int j, int k)
    {
    	return true;
    }
    
    @Override
    public boolean getDoesStumpRemoverWorkOnBlock(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return true;
    }
	
    @Override
    public boolean getCanBlockBeIncinerated(World world, int i, int j, int k)
    {
    	return false;
    }
    
    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
		return 1000; // always convert, never harvest
    }
    
    //------------- Class Specific Methods ------------//
    
    private final static float RIM_WIDTH = (1F / 16F );
    
    private final static float LAYER_HEIGHT = (2F / 16F );
    private final static float FIRST_LAYER_HEIGHT = (3F / 16F );
    private final static float LAYER_WIDTH_GAP = (1F / 16F );
    
    private BlockModel tempCurrentModel;
    
    protected void initModels()
    {
        blockModelsNarrowOneSide = new BlockModel[4];

        // center colum
        
        for ( int iTempIndex = 0; iTempIndex < 4; iTempIndex++ )
        {
        	BlockModel tempNarrowOneSide = blockModelsNarrowOneSide[iTempIndex] = new BlockModel();
        	
            float fCenterColumnWidthGap = RIM_WIDTH + (LAYER_WIDTH_GAP * iTempIndex );
            float fCenterColumnHeightGap = 0F;
            
            if ( iTempIndex > 0 )
            {
            	fCenterColumnHeightGap = FIRST_LAYER_HEIGHT + (LAYER_HEIGHT * (iTempIndex - 1 ) );
            }

            tempNarrowOneSide.addBox(fCenterColumnWidthGap, fCenterColumnHeightGap, fCenterColumnWidthGap,
            	1F - fCenterColumnWidthGap, 1F, 1F - fCenterColumnWidthGap);
        }
        
        // first layer
        
        for ( int iTempIndex = 1; iTempIndex < 4; iTempIndex++ )
        {
	        blockModelsNarrowOneSide[iTempIndex].addBox(RIM_WIDTH, 0, RIM_WIDTH, 1F - RIM_WIDTH, FIRST_LAYER_HEIGHT, 1F - RIM_WIDTH);
        }
        
        // second layer 
        
        float fWidthGap = RIM_WIDTH + LAYER_WIDTH_GAP;
        float fHeightGap = FIRST_LAYER_HEIGHT;
        
        for ( int iTempIndex = 2; iTempIndex < 4; iTempIndex++ )
        {
	        blockModelsNarrowOneSide[iTempIndex].addBox(fWidthGap, fHeightGap, fWidthGap, 1F - fWidthGap, fHeightGap + LAYER_HEIGHT, 1F - fWidthGap);
        }
        
    	// third layer
        
        fWidthGap = RIM_WIDTH + (LAYER_WIDTH_GAP * 2 );
        fHeightGap = FIRST_LAYER_HEIGHT + LAYER_HEIGHT;
        
        blockModelsNarrowOneSide[3].addBox(fWidthGap, fHeightGap, fWidthGap, 1F - fWidthGap, fHeightGap + LAYER_HEIGHT, 1F - fWidthGap);
    }
    
    public void setDamageLevel(World world, int i, int j, int k, int iDamageLevel)
    {
    	int iMetadata = setDamageLevel(world.getBlockMetadata(i, j, k), iDamageLevel);
    	
		world.setBlockMetadataWithNotify(  i, j, k, iMetadata );
    }
    
    public int setDamageLevel(int iMetadata, int iDamageLevel)
    {
    	iMetadata &= ~3;
    	
    	return iMetadata | iDamageLevel;
    }
    
    public int getDamageLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getDamageLevel(blockAccess.getBlockMetadata(i, j, k));
    }
    
    public int getDamageLevel(int iMetadata)
    {
    	return iMetadata & 3;
    }
    
    public BlockModel getCurrentModelForBlock(IBlockAccess blockAccess, int i, int j, int k)
    {
    	int iDamageLevel = getDamageLevel(blockAccess, i, j, k);
    	
		return blockModelsNarrowOneSide[iDamageLevel];
    }

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	return getCurrentModelForBlock(renderBlocks.blockAccess, i, j, k).renderAsBlock(
    		renderBlocks, this, i, j, k);
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
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
    	blockModelsNarrowOneSide[iItemDamage].renderAsItemBlock(renderBlocks, this, iItemDamage);
    }
}  
