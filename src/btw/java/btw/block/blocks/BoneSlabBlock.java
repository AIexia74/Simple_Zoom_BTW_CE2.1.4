// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class BoneSlabBlock extends SlabBlock
{
    public BoneSlabBlock(int iBlockID )
    {
        super( iBlockID, BTWBlocks.miscMaterial);
        
        setHardness( 2F ); 
        setPicksEffectiveOn(true);
        setBuoyancy(1.0F);
        
        setStepSound( Block.soundGravelFootstep );
        
        setCreativeTab( CreativeTabs.tabBlock );
        
        setUnlocalizedName( "fcBlockBoneSlab" );        
    }
    
	@Override
    public boolean doesBlockBreakSaw(World world, int i, int j, int k )
    {
		return false;
    }
    
	@Override
	public int getCombinedBlockID(int iMetadata)
	{
		return BTWBlocks.aestheticOpaque.blockID;
	}
	
	@Override
	public int getCombinedMetadata(int iMetadata)
	{
		return AestheticOpaqueBlock.SUBTYPE_BONE;
	}
	
	@Override
    public boolean canBePistonShoveled(World world, int i, int j, int k)
    {
    	return true;
    }
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconBoneSide;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        blockIcon = register.registerIcon( "fcBlockBoneSlab_top" );
        iconBoneSide = register.registerIcon("fcBlockBoneSlab_side");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		if ( iSide >= 2 )
		{
			return iconBoneSide;
		}
		
    	return blockIcon;
    }
}
