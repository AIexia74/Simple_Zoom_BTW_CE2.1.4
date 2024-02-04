// FCMOD

package btw.block.blocks.legacy;

import btw.block.BTWBlocks;
import btw.block.blocks.FarmlandBlockBase;
import net.minecraft.src.Block;
import net.minecraft.src.Material;
import net.minecraft.src.World;

public abstract class LegacyFarmlandBlockBase extends FarmlandBlockBase
{
    protected LegacyFarmlandBlockBase(int iBlockID )
    {
    	super( iBlockID );    	
    }
    
    @Override
    public void onNeighborBlockChange(World world, int i, int j, int k, int iNeighborBlockID )
    {
        super.onNeighborBlockChange( world, i, j, k, iNeighborBlockID );
        
        Block blockAbove = Block.blocksList[world.getBlockId( i, j + 1, k )];
        
        Material material = world.getBlockMaterial( i, j + 1, k );

        if ( blockAbove != null )
        {
	        if ( blockAbove.blockMaterial.isSolid() )
	        {
	            world.setBlockWithNotify( i, j, k, BTWBlocks.looseDirt.blockID );
	        }
	        else if ( blockAbove.getConvertsLegacySoil(world, i, j + 1, k) )
	        {
	        	// the new mod crop types (like wheat) convert legacy soil when planted
	        	
	        	convertToNewSoil(world, i, j, k);
	        }
        }
    }

    @Override
    protected boolean isHydrated(int iMetadata)
    {
    	// stores decreasing levels of hydration from 7 to 1
    	
    	return iMetadata > 0;
    }
    
    @Override
    protected int setFullyHydrated(int iMetadata)
    {
    	return iMetadata | 7;
    }
    
	@Override
	protected void dryIncrementally(World world, int i, int j, int k)
	{
        int iMetadata = world.getBlockMetadata( i, j, k );

        world.setBlockMetadataWithNotify( i, j, k, iMetadata - 1 );
	}

    //------------- Class Specific Methods ------------//
	
	protected abstract void convertToNewSoil(World world, int i, int j, int k);
    
	//----------- Client Side Functionality -----------//
}