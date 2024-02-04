// FCMOD

package btw.block.blocks;

import btw.block.util.Flammability;
import net.minecraft.src.*;

import java.util.Random;

public class TallGrassBlock extends BlockTallGrass
{
    private static final double HALF_WIDTH = 0.4F;
    
    public TallGrassBlock(int iBlockID)
    {
    	super( iBlockID );
    	
    	setHardness( 0F );
    	
    	setBuoyant();
    	
		setFireProperties(Flammability.GRASS);
		
        initBlockBounds(0.5D - HALF_WIDTH, 0D, 0.5D - HALF_WIDTH,
                        0.5D + HALF_WIDTH, 0.8D, 0.5D + HALF_WIDTH);
        
		setStepSound( soundGrassFootstep );
		
		setUnlocalizedName( "tallgrass" );
    }
    
    @Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
    {
        return -1;
    }

    @Override
    public void updateTick(World world, int i, int j, int k, Random rand )
    {
    	int iBlockAboveMaxNaturalLight = world.getBlockNaturalLightValueMaximum(i, j + 1, k);
    	int iBlockAboveCurrentNaturalLight = iBlockAboveMaxNaturalLight - world.skylightSubtracted;
    	
        if ( iBlockAboveCurrentNaturalLight >= GrassBlock.SPREAD_LIGHT_LEVEL && world.provider.dimensionId != 1 )
        {
        	int iMetadata = world.getBlockMetadata( i, j, k );
        	
        	if ( iMetadata == 1 ) // actual tall grass as opposed to ferns, dead shrubs, etc
        	{
            	// check for grass spread onto tilled earth
            	
            	if ( rand.nextInt( 3 ) == 0 )
            	{
                    int iTargetI = i + rand.nextInt(3) - 1;
                    int iTargetJ = j - 1 + rand.nextInt(5) - 3;
                    int iTargetK = k + rand.nextInt(3) - 1;
                    
                    int iTargetBlockID = world.getBlockId( iTargetI, iTargetJ + 1, iTargetK );

                    // FCNOTE: This is legacy support, so don't worry about new tilled block types
                    if ( world.getBlockId( iTargetI, iTargetJ, iTargetK ) == 
                    	Block.tilledField.blockID )
                    {
                    	if ( world.isAirBlock( iTargetI, iTargetJ + 1, iTargetK ) )
                    	{
                        	int iTargetBlockMaxNaturalLight = world.getBlockNaturalLightValueMaximum(iTargetI, iTargetJ + 1, iTargetK);
                        	
                        	if ( iTargetBlockMaxNaturalLight >= GrassBlock.SPREAD_LIGHT_LEVEL )
                        	{                        	
                        		world.setBlockAndMetadataWithNotify( iTargetI, iTargetJ + 1, iTargetK, Block.tallGrass.blockID, 1 );
                        	}
                    	}
                	}
                }
        	}
        }
        
        super.updateTick( world, i, j, k, rand );
    }
    
    @Override
    public boolean canSpitWebReplaceBlock(World world, int i, int j, int k)
    {
    	return true;
    }
    
    @Override
    public boolean isReplaceableVegetation(World world, int i, int j, int k)
    {
    	return true;
    }
    
    @Override
    public ItemStack getStackRetrievedByBlockDispenser(World world, int i, int j, int k)
    {
    	return createStackedBlock( world.getBlockMetadata( i, j, k ) );
    }

    @Override
    public boolean canBeGrazedOn(IBlockAccess blockAccess, int i, int j, int k,
                                 EntityAnimal animal)
    {
		return blockAccess.getBlockMetadata( i, j, k ) != 0 || animal.canGrazeOnRoughVegetation();
    }

    @Override
    public int getHerbivoreItemFoodValue(int iItemDamage)
    {
    	if ( iItemDamage == 1 || iItemDamage == 2)
    	{
    		return Item.BASE_HERBIVORE_ITEM_FOOD_VALUE / 4;
    	}
    	
    	return super.getHerbivoreItemFoodValue(iItemDamage);
    }
    
	//------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//
}