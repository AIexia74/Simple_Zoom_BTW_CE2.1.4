// FCMOD

package btw.block.blocks.legacy;

import btw.block.BTWBlocks;
import btw.block.blocks.PlanksBlock;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class LegacySidingBlock extends Block
{
    public static final int NUM_SUBTYPES = 2;
    
	protected static final double SLAB_HEIGHT = 0.5D;
	
	public LegacySidingBlock(int iBlockID)
	{
        super( iBlockID, Material.wood );

        setHardness( 2F );        
        setAxesEffectiveOn();
        setPicksEffectiveOn();
        
        setBuoyancy(1F);
        
    	initBlockBounds(0D, 0D, 0D, 1D, 1D, SLAB_HEIGHT);
    	
        setStepSound( soundWoodFootstep );
        
        setUnlocalizedName( "fcBlockOmniSlab" );
	}
	
	@Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
    {
		if ( ( iMetadata & 1 ) > 0 ) // is wood
		{
			return BTWItems.woodSidingStubID;
		}
		else
		{
			return BTWBlocks.stoneSidingAndCorner.blockID;
		}		
    }

	@Override
	public int damageDropped( int iMetadata )
    {
		return 0;
    }
	
	@Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
            IBlockAccess blockAccess, int i, int j, int k)
    {
        int iFacing = getFacing(blockAccess, i, j, k);
        
        switch ( iFacing )
        {
	        case 0:
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB(
						0D, SLAB_HEIGHT, 0D,
						1D, 1D, 1D );
	        	
	        case 1:
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB(
						0.0F, 0.0F, 0.0F,
						1.0F, SLAB_HEIGHT, 1.0F);
	        	
	        case 2:
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB(
						0.0F, 0.0F, SLAB_HEIGHT,
						1.0F, 1.0F, 1.0F );
	        	
	        case 3:
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB(
						0.0F, 0.0F, 0.0F,
						1.0F, 1.0F, SLAB_HEIGHT);
	        	
	        case 4:
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB(SLAB_HEIGHT, 0.0F, 0.0F,
														   1.0F, 1.0F, 1.0F );
	        	
	        default: // 5
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB(
						0.0F, 0.0F, 0.0F, SLAB_HEIGHT, 1.0F, 1.0F);
        }    	
    }
    
	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }

	@Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }    

	@Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, 
    	float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
        return setFacing(iMetadata, iFacing);
    }
    
    @Override
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess,
												   int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
    	int iBlockFacing = getFacing(blockAccess, i, j, k);
    	
    	return iFacing == Block.getOppositeFacing(iBlockFacing);
	}
	@Override
	public int getFacing(int iMetadata)
	{
    	return ( iMetadata >> 1 );
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
    	iMetadata &= 1; // filter out the previous facing
    	
    	iMetadata |= ( iFacing << 1 );
    	
		return iMetadata;
	}
	
	@Override
	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{		
		int iFacing = getFacing(blockAccess, i, j, k);

		return iFacing != 0;
	}
	
	@Override
	public boolean canTransmitRotationVerticallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		int iFacing = getFacing(blockAccess, i, j, k);
		
		if ( iFacing > 1 )
		{
			// sideways facing blocks can transmit
			
			return true;
		}

		return false;
	}
	
	@Override
	public boolean toggleFacing(World world, int i, int j, int k, boolean bReverse)
	{		
		int iFacing = getFacing(world, i, j, k);
		
		iFacing = Block.cycleFacing(iFacing, bReverse);

		setFacing(world, i, j, k, iFacing);
		
        world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
        
        return true;
	}

	@Override
    public boolean doesBlockBreakSaw(World world, int i, int j, int k )
    {
		return !isSlabWood(world, i, j, k);
    }
    
    @Override
    public int getFurnaceBurnTime(int iItemDamage)
    {
    	if ( ( iItemDamage & 1 ) > 0 )
    	{
    		return PlanksBlock.getFurnaceBurnTimeByWoodType(0) / 2;
    	}
    	
    	return 0;
    }
    
    //------------- Class Specific Methods ------------//
    
	public boolean isSlabWood(IBlockAccess iBlockAccess, int i, int j, int k)
    {
    	return ( ( iBlockAccess.getBlockMetadata( i, j, k ) ) & 1 ) > 0;
    }
   
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconWood;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        blockIcon = register.registerIcon( "stone" );
		iconWood = register.registerIcon("wood");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
    	if ( ( iMetadata & 1 ) > 0 )
    	{
    		return iconWood;
    	}
    	
		return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked( World world, int i, int j, int k )
    {
        return idDropped( world.getBlockMetadata( i, j, k ), world.rand, 0 );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, 
    	int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(
			iNeighborI, iNeighborJ, iNeighborK, iSide);
    }
}