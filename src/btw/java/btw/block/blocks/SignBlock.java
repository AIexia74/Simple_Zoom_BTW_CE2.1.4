// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class SignBlock extends BlockSign
{
	protected final boolean freeStanding;
	
    public SignBlock(int iBlockID, boolean bFreeStanding)
    {
    	super( iBlockID, TileEntitySign.class, bFreeStanding );

        freeStanding = bFreeStanding;
    	
    	setHardness( 1F );
    	
    	setBuoyant();
    	
        initBlockBounds(0.25F, 0.0F, 0.25, 0.75F, 1F, 0.75F);
        
        setStepSound( soundWoodFootstep );
        
        setUnlocalizedName( "sign" );
        
        disableStats();
    }
    
    @Override
    public boolean doesBlockHopperEject(World world, int i, int j, int k)
    {
    	return false;
    }
    
    @Override
	public boolean getPreventsFluidFlow(World world, int i, int j, int k, Block fluidBlock)
	{
    	return true;
	}
    
	@Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int i, int j, int k )
    {
    	// override to deprecate parent
    }
	
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
        if ( !freeStanding)
        {
            int var5 = blockAccess.getBlockMetadata( i, j, k );
            
            float var6 = 0.28125F;
            float var7 = 0.78125F;
            float var8 = 0.0F;
            float var9 = 1.0F;
            float var10 = 0.125F;
            
            if ( var5 == 2 )
            {
            	return AxisAlignedBB.getAABBPool().getAABB(         	
            		var8, var6, 1.0F - var10, var9, var7, 1.0F );
            }
            else if ( var5 == 3 )
            {
            	return AxisAlignedBB.getAABBPool().getAABB(         	
            		var8, var6, 0.0F, var9, var7, var10 );
            }
            else if ( var5 == 4 )
            {
            	return AxisAlignedBB.getAABBPool().getAABB(         	
            		1.0F - var10, var6, var8, 1.0F, var7, var9 );
            }
            else if ( var5 == 5 )
            {
            	return AxisAlignedBB.getAABBPool().getAABB(         	
            		0.0F, var6, var8, var10, var7, var9 );
            }
            
        	return AxisAlignedBB.getAABBPool().getAABB(         	
        		0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F );
        }
        
        return super.getBlockBoundsFromPoolBasedOnState(blockAccess, i, j, k);
    }
	
	//------------- Class Specific Methods ------------//
	
	public boolean isFreeStanding() {
		return this.freeStanding;
	}
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {
    	return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool( World world, int i, int j, int k )
    {
    	return getBlockBoundsFromPoolBasedOnState(world, i, j, k).offset(i, j, k);
    }
	
	public String getSignTexture() {
		return "/item/sign.png";
	}
}
