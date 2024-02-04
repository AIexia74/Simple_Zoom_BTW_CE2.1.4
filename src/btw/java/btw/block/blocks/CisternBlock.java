// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class CisternBlock extends BlockCauldron
{
    public CisternBlock(int iBlockID )
    {
        super( iBlockID );
        
        initBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
    }
    
    @Override
    public void addCollisionBoxesToList(World world, int i, int j, int k,
                                        AxisAlignedBB intersectingBox, List list, Entity entity )
    {
    	// parent method is super complicated for no apparent reason
    	
        AxisAlignedBB tempBox = getCollisionBoundingBoxFromPool( world, i, j, k );
    	
    	tempBox.addToListIfIntersects(intersectingBox, list);
    }
    
	//------------- Class Specific Methods ------------//
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
    	return renderer.renderBlockCauldron( this, i, j, k );
    }    
}
