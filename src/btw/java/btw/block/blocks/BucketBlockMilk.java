// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class BucketBlockMilk extends BucketBlockFull
{
    public BucketBlockMilk(int iBlockID )
    {
        super( iBlockID );
    	
    	setUnlocalizedName( "fcBlockBucketMilk" );
    }
    
	@Override
    public int idDropped( int iMetadata, Random rand, int iFortuneMod )
    {
		return Item.bucketMilk.itemID;
    }
	
	//------------- Class Specific Methods ------------//
	
	@Override
    public boolean attemptToSpillIntoBlock(World world, int i, int j, int k)
    {
        if ( ( world.isAirBlock( i, j, k ) || !world.getBlockMaterial( i, j, k ).isSolid() ) )
        {     
    		world.setBlockWithNotify( i, j, k, BTWBlocks.milkFluid.blockID );
            
            return true;
        }
        
        return false;
    }
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconContents;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

        iconContents = register.registerIcon("fcBlockBucket_milk");
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected Icon getContentsIcon()
	{
		return iconContents;
	}
}
