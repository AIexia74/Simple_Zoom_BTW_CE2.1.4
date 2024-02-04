// FCMOD

package btw.block.blocks;

import btw.client.fx.BTWEffectManager;
import net.minecraft.src.*;

import java.util.Random;

public class CarvedPumpkinBlock extends BlockPumpkin
{
    public CarvedPumpkinBlock(int iBlockID)
    {
    	super( iBlockID, false );
    	
    	setHardness( 1F );
    	setAxesEffectiveOn(true);
    	
    	setBuoyant();
    	
    	setStepSound( soundWoodFootstep );
    	
    	setUnlocalizedName( "pumpkin" );    	
    }
    
    @Override
    public void onBlockAdded(World world, int i, int j, int k )
    {
    	// override to prevent vanilla golem creation
    }
    
    @Override
    public boolean canPlaceBlockAt( World world, int i, int j, int k )
    {
        int iBlockID = world.getBlockId(i, j, k);
        
        return iBlockID == 0 || blocksList[iBlockID].blockMaterial.isReplaceable();
    }
    
    @Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
    {
        return 0;
    }
    
    @Override
    public void breakBlock( World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
    	super.breakBlock( world, i, j, k, iBlockID, iMetadata );
    	
    	if ( !world.isRemote )
    	{
			world.playAuxSFX( BTWEffectManager.GOURD_IMPACT_SOUND_EFFECT_ID, i, j, k, 0 );
    	}
    }
    
    @Override
	public int rotateMetadataAroundJAxis(int iMetadata, boolean bReverse)
	{
		int iDirection = iMetadata & 3;
		
		if ( bReverse )
		{
			iDirection++;
			
			if ( iDirection > 3 )
			{
				iDirection = 0;
			}
		}
		else
		{
			iDirection--;
			
			if ( iDirection < 0 )
			{
				iDirection = 3;
			}
		}		
		
		return ( iMetadata & (~3) ) | iDirection;
	}
    
    @Override
    public boolean canBeGrazedOn(IBlockAccess blockAccess, int i, int j, int k,
								 EntityAnimal animal)
    {
		return animal.canGrazeOnRoughVegetation();
    }
}