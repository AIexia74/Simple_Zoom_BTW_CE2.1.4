// FCMOD

package btw.block.blocks;

import btw.item.BTWItems;
import net.minecraft.src.*;

import java.util.List;

public class MossyCobblestoneBlock extends Block
{
    public MossyCobblestoneBlock(int iBlockID )
    {
        super( iBlockID, Material.rock );
        
        setHardness( 2F );
        setResistance( 10 );
        setPicksEffectiveOn();
        
        setStepSound( soundStoneFootstep );
        
        setUnlocalizedName( "stoneMoss" );
        
        setCreativeTab( CreativeTabs.tabBlock );
    }
    
    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k)
    {
		return 2; // iron or higher
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, BTWItems.stone.itemID, 6, iMetadata, fChanceOfDrop);

		
		return true;
	}
	
	@Override
	public int damageDropped(int metadata) {
		return getStrata(metadata);
	}
	
    //------------- Class Specific Methods ------------//    
	
    public int getStrata( IBlockAccess blockAccess, int i, int j, int k )
    {
		return getStrata( blockAccess.getBlockMetadata( i, j, k ) );
    }
    
    public int getStrata( int iMetadata )
    {
    	return iMetadata & 3;
    }
    
	//----------- Client Side Functionality -----------//
    private Icon iconByMetadataArray[] = new Icon[3];
    
    @Override
	public void getSubBlocks( int iBlockID, CreativeTabs creativeTabs, List list )
    {
        list.add( new ItemStack( iBlockID, 1, 0 ) );
        list.add( new ItemStack( iBlockID, 1, 1 ) );
        list.add( new ItemStack( iBlockID, 1, 2 ) );
    }

	@Override
    public int getDamageValue(World world, int x, int y, int z) {
		// used only by pick block
		return world.getBlockMetadata(x, y, z);
    }
    
	@Override
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

        iconByMetadataArray[0] = blockIcon;
        iconByMetadataArray[1] = register.registerIcon("stoneMoss_1");
        iconByMetadataArray[2] = register.registerIcon("stoneMoss_2");

    }
	
	@Override
    public Icon getIcon( int iSide, int iMetadata )
    {
        return iconByMetadataArray[iMetadata];
    }
}
