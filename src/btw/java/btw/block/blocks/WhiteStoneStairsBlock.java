// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Block;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;

public class WhiteStoneStairsBlock extends StairsBlock
{	
	public WhiteStoneStairsBlock(int iBlockID)
    {
    	super( iBlockID, Block.stone, 0 );
    	
        setHardness( 1.5F );
        setResistance( 10F );
        setPicksEffectiveOn();
        
        setUnlocalizedName( "fcBlockWhiteStoneStairs" );        
	}

	@Override
    public int damageDropped( int iMetadata )
    {
		return iMetadata & 8;
    }
	
	//------------- Class Specific Methods ------------//
	
	public boolean getIsCobbleFromMetadata(int iMetadata)
	{
		return ( iMetadata & 8 ) > 0;
	}
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconWhiteCobble;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
	{
		blockIcon = register.registerIcon( "fcBlockWhiteStone" );

		iconWhiteCobble = register.registerIcon("fcBlockWhiteCobble");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
	{
		if ( getIsCobbleFromMetadata(iMetadata) )
		{
			return iconWhiteCobble;
		}
		
		return blockIcon;
	}
}
