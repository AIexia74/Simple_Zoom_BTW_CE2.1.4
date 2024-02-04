// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Icon;
import net.minecraft.src.World;

import java.util.Random;

public class BucketBlockCement extends BucketBlockFull
{
    public BucketBlockCement(int iBlockID )
    {
        super( iBlockID );
    	
    	setUnlocalizedName( "fcItemBucketCement" );
    }
    
	@Override
    public int idDropped( int iMetadata, Random rand, int iFortuneMod )
    {
		return BTWItems.cementBucket.itemID;
    }
	
	//------------- Class Specific Methods ------------//
	
	@Override
    public boolean attemptToSpillIntoBlock(World world, int i, int j, int k)
    {
        if ( ( world.isAirBlock( i, j, k ) || !world.getBlockMaterial( i, j, k ).isSolid() ) )
        {            
	    	world.playSoundEffect( i, j, k, "mob.ghast.moan", 
				0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
	    	
	    	if ( world.isAirBlock( i, j - 1, k ) || !world.getBlockMaterial( i, j - 1, k ).isSolid() )
	    	{
	    		// if the block below the target is empty, place there instead so the player can
	    		// pour over ledges without destroying the bucket block
	    		
	    		j--;
	    	}
	    	
    		world.setBlockWithNotify( i, j, k, BTWBlocks.cement.blockID );
            
            return true;
        }
        
        return false;
    }
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    protected Icon getContentsIcon()
	{
		return BTWBlocks.cement.blockIcon;
	}
}
