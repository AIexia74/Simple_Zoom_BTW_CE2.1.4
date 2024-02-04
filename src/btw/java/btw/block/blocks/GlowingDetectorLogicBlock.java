// FCMOD

package btw.block.blocks;

import btw.client.render.util.RenderUtils;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class GlowingDetectorLogicBlock extends DetectorLogicBlock
{
    private final static float LIT_FACE_THICKNESS = 0.01F;
	
    public GlowingDetectorLogicBlock(int iBlockID )
    {
        super( iBlockID );  
        
        setLightValue( 1F );     
        
        setUnlocalizedName( "fcBlockDetectorLogicGlowing" );
    }
    
    //------------- FCBlockDetectorLogic ------------//

	@Override
	protected void removeSelf(World world, int i, int j, int k)
	{
		// this function exists as the regular block shouldn't notify the client when it is removed, but the glowing variety should 
		
    	world.setBlock( i, j, k, 0, 0, 2 );        	
	}
    
    //------------- Class Specific Methods ------------//

	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public int getMixedBrightnessForBlock(IBlockAccess iblockaccess, int i, int j, int k)
	{
		return 0xF00F0;
	}

    @Override
    @Environment(EnvType.CLIENT)
    public int getRenderBlockPass()
    {
        return 1;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, 
    	int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(
			iNeighborI, iNeighborJ, iNeighborK, iSide);
    }

    @Environment(EnvType.CLIENT)
    public void setRenderBoundsToRenderLitFace(RenderBlocks renderBlocks, int iFacing)
	{
		switch ( iFacing )
		{
			case 0:
				
				renderBlocks.setRenderBounds(0.0F, 0.0F, 0.0F,
											 1.0F, LIT_FACE_THICKNESS, 1.0F);
		    	
		    	break;
		    	
			case 1:
				
				renderBlocks.setRenderBounds(0.0F, 1.0F - LIT_FACE_THICKNESS, 0.0F,
											 1.0F, 1.0F, 1.0F );
		    	
		    	break;
		    	
			case 2:
				
				renderBlocks.setRenderBounds(0.0F, 0.0F, 0.0F,
											 1.0F, 1.0F, LIT_FACE_THICKNESS);
		    	
		    	break;
		    	
			case 3:
				
				renderBlocks.setRenderBounds( 0.0F, 0.0F, 1.0F - LIT_FACE_THICKNESS,
	    			1.0F, 1.0F, 1.0F );
		    	
		    	break;
		    	
			case 4:
				
				renderBlocks.setRenderBounds(0.0F, 0.0F, 0.0F, LIT_FACE_THICKNESS, 1.0F, 1.0F);
		    	
		    	break;
		    	
			default: // 5
				
				renderBlocks.setRenderBounds(1.0F - LIT_FACE_THICKNESS, 0.0F, 0.0F,
											 1.0F, 1.0F, 1.0F );
		    	
		    	break;
		    	
		}
    	
	}

    @Environment(EnvType.CLIENT)
    public boolean shouldVisiblyProjectToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing)
	{	
    	BlockPos targetPos = new BlockPos( i, j, k, iFacing );
    	
    	if ( iFacing == 0 )
    	{
    		return WorldUtils.doesBlockHaveSolidTopSurface(blockAccess, targetPos.x, targetPos.y, targetPos.z);
    	}
    	else
    	{
    		return blockAccess.isBlockNormalCube(targetPos.x, targetPos.y, targetPos.z);
    	}    	
	}

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
    	IBlockAccess blockAccess = renderer.blockAccess;
    	
		for ( int iTempFacing = 0; iTempFacing <= 5; iTempFacing++ )
		{
			if ( shouldVisiblyProjectToFacing(blockAccess, i, j, k, iTempFacing) )
			{
				setRenderBoundsToRenderLitFace(renderer, iTempFacing);
				
		        RenderUtils.renderBlockFullBrightWithTexture(renderer, blockAccess, i, j, k, blockIcon);
			}				
		}
		
    	if (LOGIC_DEBUG_DISPLAY)
    	{
    		return renderDetectorLogicDebug(renderer, blockAccess, i, j, k, this);
    	}
    	else
    	{    	
    		return true;
    	}
    }    
}