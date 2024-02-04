// FCMOD

package btw.block.blocks;

import btw.block.util.Flammability;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class MushroomCapBlock extends BlockMushroomCap
{
	protected final int mushroomType; // copy of parent variable due to private visibility
	
    public MushroomCapBlock(int iBlockID, int iMushroomType )
    {
        super( iBlockID, Material.wood, iMushroomType );

        mushroomType = iMushroomType;
        
        setHardness( 0.2F );
        
        setStepSound( soundWoodFootstep );
        
        setBuoyant();
        
		setFireProperties(Flammability.HIGH);
		
        setUnlocalizedName( "fcBlockMushroomCap" );
    }

    @Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
    {
    	if (mushroomType != 0 )
    	{
    		return BTWItems.redMushroom.itemID;
    	}
    	
		return BTWItems.brownMushroom.itemID;
    }
    
    @Override
    public boolean canMobsSpawnOn(World world, int i, int j, int k)
    {
    	// so that mobs don't appear uncontrollably in mushroom farms after a large mushroom
    	// pops up
    	
    	return false;
    }
    
	//------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked( World world, int i, int j, int k )
    {
        return idDropped( world.getBlockMetadata( i, j, k ), world.rand, 0 );
    }
}