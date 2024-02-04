// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.BlockQuartz;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.RenderBlocks;

public class BlackStoneBlock extends BlockQuartz
{
    public BlackStoneBlock(int iBlockID )
    {
        super( iBlockID );
        
        setHardness( 2F );
        
        setStepSound( soundStoneFootstep );
        
        setUnlocalizedName( "quartzBlock" );
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconChiseled;
    @Environment(EnvType.CLIENT)
    private Icon iconLinesSide;
    @Environment(EnvType.CLIENT)
    private Icon iconLinesTop;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        blockIcon = register.registerIcon( "fcBlockBlackStone" );

        iconChiseled = register.registerIcon("fcBlockBlackStone_chiseled");
        iconLinesSide = register.registerIcon("fcBlockBlackStone_lines");
        iconLinesTop = register.registerIcon("fcBlockBlackStone_lines_top");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		if ( iMetadata == 1 ) // chiseled
		{
			return iconChiseled;
		}
		else if ( iMetadata == 2 ) // lines
		{
			if ( iSide <= 1 )
			{
				return iconLinesTop;
			}
			
			return iconLinesSide;
		}
		else if ( iMetadata == 3 ) // lines sideways
		{
			if ( iSide >= 4 )
			{
				return iconLinesTop;
			}
			
			return iconLinesSide;
		}
		else if ( iMetadata == 4 ) // lines sideways
		{
			if ( iSide == 2 || iSide == 3 )
			{
				return iconLinesTop;
			}
			
			return iconLinesSide;
		}
		
		return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
    	return renderer.renderBlockQuartz( this, i, j, k );
    }    
}
