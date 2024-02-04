// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.World;

import java.util.Random;

public class BucketBlockChocolateMilk extends BucketBlockFull
{
    public BucketBlockChocolateMilk(int iBlockID )
    {
        super( iBlockID );
    	
    	setUnlocalizedName( "fcBlockBucketMilkChocolate" );
    }
    
	@Override
    public int idDropped( int iMetadata, Random rand, int iFortuneMod )
    {
		return BTWItems.milkChocolateBucket.itemID;
    }
	
	//------------- Class Specific Methods ------------//
	
	@Override
    public boolean attemptToSpillIntoBlock(World world, int i, int j, int k)
    {
        if ( ( world.isAirBlock( i, j, k ) || !world.getBlockMaterial( i, j, k ).isSolid() ) )
        {     
    		world.setBlockWithNotify( i, j, k, BTWBlocks.chocolateMilkFluid.blockID );
            
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

        iconContents = register.registerIcon("fcBlockBucket_chocolate");
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected Icon getContentsIcon()
	{
		return iconContents;
	}
}
