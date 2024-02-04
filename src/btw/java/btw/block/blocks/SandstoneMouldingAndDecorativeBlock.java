// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Block;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.Material;

public class SandstoneMouldingAndDecorativeBlock extends MouldingAndDecorativeWallBlock
{
	public SandstoneMouldingAndDecorativeBlock(int iBlockID, int iMatchingCornerBlockID)
	{
		super( iBlockID, Material.rock, "fcBlockDecorativeSandstone_top", "fcBlockColumnSandstone_side", iMatchingCornerBlockID,
			0.8F, 1.34F, Block.soundStoneFootstep, "fcSandstoneMoulding" );
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconBySideArray = new Icon[6];

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

        iconBySideArray[0] = register.registerIcon("fcBlockDecorativeSandstone_bottom");
        iconBySideArray[1] = register.registerIcon("fcBlockDecorativeSandstone_top");
        
        Icon sideIcon = register.registerIcon( "fcBlockDecorativeSandstone_side" );

        iconBySideArray[2] = sideIcon;
        iconBySideArray[3] = sideIcon;
        iconBySideArray[4] = sideIcon;
        iconBySideArray[5] = sideIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		if (iMetadata != SUBTYPE_COLUMN)
		{
			return iconBySideArray[iSide];
		}
		
		return super.getIcon( iSide, iMetadata );
    }	
}