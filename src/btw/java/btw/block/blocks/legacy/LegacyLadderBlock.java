// FCMOD

package btw.block.blocks.legacy;

import btw.block.blocks.LadderBlockBase;
import net.minecraft.src.Block;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;

public class LegacyLadderBlock extends LadderBlockBase
{
    public LegacyLadderBlock(int iBlockID)
    {
        super( iBlockID );
        
        setUnlocalizedName( "ladder" );
        
        setCreativeTab( null );
    }
    
    @Override
	public boolean getPreventsFluidFlow(World world, int i, int j, int k, Block fluidBlock)
	{
    	return true;
	}
    
    @Override
    public int getFacing(int iMetadata)
    {
    	return iMetadata;
    }
    
    @Override
    public int setFacing(int iMetadata, int iFacing)
    {
    	return MathHelper.clamp_int( iFacing, 2, 5 );
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//
}
