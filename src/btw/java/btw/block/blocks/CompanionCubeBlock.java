// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.client.fx.BTWEffectManager;
import btw.item.items.SwordItem;
import btw.item.util.ItemUtils;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class CompanionCubeBlock extends Block
{
    public final static int NUM_SUBTYPES = 16;
    
    public CompanionCubeBlock(int iBlockID )
    {
        super( iBlockID, Material.cloth );
        
        setHardness( 0.4F );
        
        setBuoyancy(1F);
        
		setFireProperties(Flammability.CLOTH);
		
        setStepSound( Block.soundClothFootstep );

        setUnlocalizedName( "fcBlockCompanionCube" );        
        
        setCreativeTab( CreativeTabs.tabBlock );
    }

	@Override
    public int onBlockPlaced(World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
		if ( !getIsSlabFromMetadata(iMetadata) )
		{
			return setFacing(iMetadata, Block.getOppositeFacing(iFacing));
		}
		else
		{
	        if ( iFacing == 0 || iFacing != 1 && (double)fClickY > 0.5D )
	        {
	        	return setFacing(iMetadata, 1);
	        }
		}
		
		return iMetadata;
    }
	
	@Override
    public int damageDropped( int iMetaData )
    {
    	if ( ( iMetaData & 8 ) > 0 ) // is slab
    	{
    		return 1;
    	}
    	
        return 0; 
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
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
    	if ( getIsSlab(blockAccess, i, j, k) )
    	{
    		if ( !getIsUpsideDownSlab(blockAccess, i, j, k) )
    		{
            	return AxisAlignedBB.getAABBPool().getAABB( 0F, 0F, 0F, 1F, 0.5F, 1F );
    		}
    		else
    		{
            	return AxisAlignedBB.getAABBPool().getAABB( 0F, 0.5F, 0F, 1F, 1F, 1F );
    		}
    	}
    	else
    	{
        	return AxisAlignedBB.getAABBPool().getAABB( 0F, 0F, 0F, 1F, 1F, 1F );
    	}
    }
    
	@Override
	public void onBlockPlacedBy( World world, int i, int j, int k, EntityLiving entityliving, ItemStack stack )
	{
    	if ( !getIsSlab(world, i, j, k) )
		{
    		spawnHearts(world, i, j, k);
    		
			int iFacing = MiscUtils.convertPlacingEntityOrientationToBlockFacingReversed(entityliving);
			
	        setFacing(world, i, j, k, iFacing);
		}
	}
	
	@Override
    public void breakBlock( World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
    	if ( !getIsSlab(world, i, j, k) )
		{
	        world.playSoundEffect( (float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, "mob.wolf.whine", 
	        		0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
		}
    }
    
	@Override
    public void onDestroyedByFire(World world, int i, int j, int k, int iFireAge, boolean bForcedFireSpread)
    {
    	if ( !getIsSlab(world, i, j, k) )
		{
	        world.playAuxSFX( BTWEffectManager.COMPANION_CUBE_DEATH_EFFECT_ID, i, j, k, 0 );
		}
    	
		super.onDestroyedByFire(world, i, j, k, iFireAge, bForcedFireSpread);
    }
    
	@Override
    public boolean doesBlockBreakSaw(World world, int i, int j, int k )
    {
    	return false;
    }

	@Override
    public boolean onBlockSawed(World world, int i, int j, int k, int iSawPosI, int iSawPosJ, int iSawPosK)
    {
		int iSawFacing = ((SawBlock) BTWBlocks.saw).getFacing(world, iSawPosI, iSawPosJ, iSawPosK);
		
		if ( !getIsSlab(world, i, j, k) )
		{
			if ( iSawFacing == 0 || iSawFacing == 1 )
			{
				// if the saw is facing up or down, eject two seperate halves as items
				
				for ( int iTempCount = 0; iTempCount < 2; iTempCount++ )
				{
					ItemUtils.ejectSingleItemWithRandomOffset(world, i, j, k,
                                                              BTWBlocks.companionCube.blockID, 1);
				}
				
		    	world.setBlockWithNotify( i, j, k, 0 );
			}
			else
			{
				// if the saw ir oriented towards the sides, it only chops off the top
				// and leaves a half-cube in its place
				
				ItemUtils.ejectSingleItemWithRandomOffset(world, i, j, k,
                                                          BTWBlocks.companionCube.blockID, 1);
				
				setIsSlab(world, i, j, k, true);
				setFacing(world, i, j, k, 0);
				
				world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );				
			}
			
			BlockPos bloodPos = new BlockPos( i, j, k );
			
			bloodPos.addFacingAsOffset(Block.getOppositeFacing(iSawFacing));

			// FCTODO: Replace these particles don't currently work
	        // EmitBloodParticles( world, bloodPos.i, bloodPos.j, bloodPos.k, world.rand );
			
            world.playSoundEffect( (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 
            		"mob.wolf.hurt", 5.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);            
		}
		else
		{
			if ( iSawFacing == 0 || iSawFacing == 1 )
			{
				ItemUtils.ejectSingleItemWithRandomOffset(world, i, j, k,
                                                          BTWBlocks.companionCube.blockID, 1);
				
		    	world.setBlockWithNotify( i, j, k, 0 );
			}
			else
			{
				return false;
			}
		}
		
		return true;
    }
	
    @Override
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
		if ( getIsSlab(blockAccess, i, j, k) )
		{
			if ( getIsUpsideDownSlab(blockAccess, i, j, k) )
			{
				return iFacing == 1;
			}
			else
			{
				return iFacing == 0;
			}
		}
		
		return true;
	}
    
	@Override
	public int getFacing(int iMetadata)
	{
		return iMetadata & ~8;
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
		iMetadata &= 8; // filter out old state
		
		iMetadata |= iFacing;
		
		return iMetadata;
	}
	
	@Override
	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return !getIsSlab(blockAccess, i, j, k);
	}
	
	@Override
	public boolean canTransmitRotationHorizontallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return !getIsSlab(blockAccess, i, j, k);
	}
	
	@Override
	public boolean canTransmitRotationVerticallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return !getIsSlab(blockAccess, i, j, k);
	}
	
	@Override
	public boolean rotateAroundJAxis(World world, int i, int j, int k, boolean bReverse)
	{
		if ( !getIsSlab(world, i, j, k) )
		{
			if ( super.rotateAroundJAxis(world, i, j, k, bReverse) )
			{
		        if ( world.rand.nextInt( 12 ) == 0 )
		        {
			        world.playSoundEffect( (float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, "mob.wolf.whine", 
			        		0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F );
		        }
				
				return true;
			}
		}
		
		return false;
	}

	@Override
    public boolean isNormalCube(IBlockAccess blockAccess, int i, int j, int k)
    {
		return !getIsSlab(blockAccess, i, j, k);
    }
    
    //------------- Class Specific Methods ------------//
    
    public boolean getIsSlab(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getIsSlabFromMetadata(blockAccess.getBlockMetadata(i, j, k));
    }
    
    public void setIsSlab(World world, int i, int j, int k, boolean bState)
    {
    	int iMetadata = world.getBlockMetadata( i, j, k ) & ~8;	// filter out old state
    	
    	if ( bState )
    	{
    		iMetadata |= 8;
    	}
    	
        world.setBlockMetadataWithNotify( i, j, k, iMetadata );
    }
    
    public boolean getIsSlabFromMetadata(int iMetadata)
    {
    	return ( iMetadata & 8 ) > 0;
    }
    
    public boolean getIsUpsideDownSlab(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getIsUpsideDownSlabFromMetadata(blockAccess.getBlockMetadata(i, j, k));
    }
    
    public boolean getIsUpsideDownSlabFromMetadata(int iMetadata)
    {
    	if ( getIsSlabFromMetadata(iMetadata) )
    	{
    		return getFacing(iMetadata) == 1;
    	}
    	
    	return false;    			
    }

	public static void spawnHearts(World world, int i, int j, int k)
    {
        String s = "heart";
        
        for( int tempCount = 0; tempCount < 7; tempCount++)
        {
            double d = world.rand.nextGaussian() * 0.02D;
            double d1 = world.rand.nextGaussian() * 0.02D;
            double d2 = world.rand.nextGaussian() * 0.02D;
            
            world.spawnParticle( s, 
        		((double) i) + (double)(world.rand.nextFloat()), 
        		(double)( j + 1 ) + (double)(world.rand.nextFloat()), 
				((double) k) + (double)(world.rand.nextFloat()), 
				d, d1, d2);
        }
    }    
    
    @Override
	public boolean canToolStickInBlockSpecialCase(World world, int x, int y, int z, Item toolOrSword) {
		if (toolOrSword instanceof SwordItem) {
			if (!world.isRemote) {
				if (!getIsSlab(world, x, y, z)) {
					world.playSoundEffect((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F, "mob.wolf.whine", 0.5F,
							2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
				}
			}

			return true;
		} 
		else {
			return false;
		}
	}
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconFront;
    @Environment(EnvType.CLIENT)
    private Icon iconGuts;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

		iconFront = register.registerIcon("fcBlockCompanionCube_front");
		iconGuts = register.registerIcon("fcBlockCompanionCube_guts");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
    	// used by item render
    	
        if ( iMetadata > 0 )
    	{
    		if ( iSide == 1 )
    		{
    			return iconGuts;
    		}
    	}
        else if ( iSide == 4 ) // FCTODO: not sure why this is 5 and not 3 like all other blocks
        {
            return iconFront;
        }        
    	
        return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture( IBlockAccess blockAccess, int i, int j, int k, int iSide )
    {
        if ( getIsSlab(blockAccess, i, j, k) )
        {
    		if ( !getIsUpsideDownSlab(blockAccess, i, j, k) )
    		{
	        	if ( iSide == 1 )
	        	{
	    			return iconGuts;
	        	}
    		}
    		else
    		{
	        	if ( iSide == 0 )
	        	{
	    			return iconGuts;
	        	}
    		}
        }
        else if (iSide == getFacing(blockAccess, i, j, k) )
        {
            return iconFront;
        }
        
        return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks( int iBlockID, CreativeTabs creativeTabs, List list )
    {
        list.add( new ItemStack( iBlockID, 1, 0 ) );
        list.add( new ItemStack( iBlockID, 1, 8 ) );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getBlockBoundsFromPoolForItemRender(int iItemDamage)
    {
    	if ( iItemDamage > 0 )
    	{
        	return AxisAlignedBB.getAABBPool().getAABB( 0F, 0F, 0F, 1F, 0.5F, 1F );
    	}
    	
    	return super.getBlockBoundsFromPoolForItemRender(iItemDamage);
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
	