package net.minecraft.src;

import btw.client.render.util.RenderUtils;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Random;

public abstract class BlockHalfSlab extends Block
{
    protected final boolean isDoubleSlab;

    public BlockHalfSlab(int par1, boolean par2, Material par3Material)
    {
        super(par1, par3Material);
        this.isDoubleSlab = par2;

        if (par2)
        {
            opaqueCubeLookup[par1] = true;
            
            // FCMOD: Added
            initBlockBounds(0D, 0D, 0D, 1D, 1D, 1D);
            // END FCMOD
        }
        else
        {
            initBlockBounds(0D, 0D, 0D, 1D, 0.5D, 1D);
            
            useNeighborBrightness[par1] = true;
        }

        this.setLightOpacity(255);
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return this.isDoubleSlab;
    }

    /**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
     */
    public int onBlockPlaced(World par1World, int par2, int par3, int par4, int par5, float par6, float par7, float par8, int par9)
    {
        return this.isDoubleSlab ? par9 : (par5 != 0 && (par5 == 1 || (double)par7 <= 0.5D) ? par9 : par9 | 8);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random par1Random)
    {
        return this.isDoubleSlab ? 2 : 1;
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int damageDropped(int par1)
    {
        return par1 & 7;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return this.isDoubleSlab;
    }

    /**
     * Takes a block ID, returns true if it's the same as the ID for a stone or wooden single slab.
     */
    @Environment(EnvType.CLIENT)
    private static boolean isBlockSingleSlab(int par0)
    {
        return par0 == Block.stoneSingleSlab.blockID || par0 == Block.woodSingleSlab.blockID;
    }

    /**
     * Returns the slab block name with step type.
     */
    public abstract String getFullSlabName(int var1);

    /**
     * Get the block's damage value (for use with pick block).
     */
    public int getDamageValue(World par1World, int par2, int par3, int par4)
    {
        return super.getDamageValue(par1World, par2, par3, par4) & 7;
    }

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    @Environment(EnvType.CLIENT)
    public int idPicked(World par1World, int par2, int par3, int par4)
    {
        return isBlockSingleSlab(this.blockID) ? this.blockID : (this.blockID == Block.stoneDoubleSlab.blockID ? Block.stoneSingleSlab.blockID : (this.blockID == Block.woodDoubleSlab.blockID ? Block.woodSingleSlab.blockID : Block.stoneSingleSlab.blockID));
    }
    
	// FCMOD: Added New
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
    	AxisAlignedBB bounds;
    	
        if ( isDoubleSlab )
        {
            bounds = AxisAlignedBB.getAABBPool().getAABB( 0F, 0F, 0F, 1F, 1F, 1F );
        }
        else
        {
            if ( getIsUpsideDown(blockAccess, i, j, k) )
            {
                bounds = AxisAlignedBB.getAABBPool().getAABB( 0F, 0.5F, 0F, 1F, 1F, 1F );
            }
            else
            {
            	bounds = AxisAlignedBB.getAABBPool().getAABB( 0F, 0F, 0F, 1F, 0.5F, 1F );
            }
        }
        
        return bounds;
    }
    
    @Override
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
    	if ( !isDoubleSlab )
    	{
            boolean bIsUpsideDown = getIsUpsideDown(blockAccess, i, j, k);
            
            if ( iFacing == 0  ) 
            {
            	if ( !bIsUpsideDown )
            	{
            		return true;
            	}
            }
            else if ( iFacing == 1 )
            {
            	if ( bIsUpsideDown )
            	{
            		return true;
            	}
            }
    	}
    	
		return super.hasLargeCenterHardPointToFacing( blockAccess, i, j, k, iFacing, bIgnoreTransparency );
	}

    @Override
    public float groundCoverRestingOnVisualOffset(IBlockAccess blockAccess, int i, int j, int k)
    {
        if ( !isDoubleSlab )
        {
            if ( ( blockAccess.getBlockMetadata( i, j, k ) & 8 ) == 0 )
            {
            	return -0.5F;
            }
        }
        
    	return 0F;
    }
    
    @Override
    public boolean canGroundCoverRestOnBlock(World world, int i, int j, int k)
    {
    	return true;
    }
    
    @Override
    public boolean canMobsSpawnOn(World world, int i, int j, int k)
    {
        return blockMaterial.getMobsCanSpawnOn(world.provider.dimensionId);
    }

    @Override
    public float mobSpawnOnVerticalOffset(World world, int i, int j, int k)
    {
        if ( !isDoubleSlab && !getIsUpsideDown(world, i, j, k) )
        {
        	return -0.5F;
        }
        
    	return 0F;
    }
    
    //------------- Class Specific Methods ------------//
    
    public boolean getIsUpsideDown(IBlockAccess blockAccess, int i, int j, int k)
    {
    	return getIsUpsideDown(blockAccess.getBlockMetadata(i, j, k));
    }
    
    public boolean getIsUpsideDown(int iMetadata)
    {
    	return ( iMetadata & 8 ) > 0;
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
    	if ( isDoubleSlab )
    	{
    		return super.shouldSideBeRendered( blockAccess, iNeighborI, iNeighborJ, iNeighborK, iSide );
    	}

    	BlockPos myPos = new BlockPos(iNeighborI, iNeighborJ, iNeighborK,
                                      getOppositeFacing(iSide) );
    	
    	boolean bUpsideDown = getIsUpsideDown(blockAccess, myPos.x, myPos.y, myPos.z);
    	
    	if ( iSide < 2 )
    	{
	    	if ( iSide == 0 )
	    	{
	    		return bUpsideDown || !blockAccess.isBlockOpaqueCube( 
	    			iNeighborI, iNeighborJ, iNeighborK );
	    	}
	    	else // iSide == 1
	    	{
	    		return !bUpsideDown || !blockAccess.isBlockOpaqueCube( 
	    			iNeighborI, iNeighborJ, iNeighborK );
	    	}
    	}

        return RenderUtils.shouldRenderNeighborHalfSlabSide(blockAccess,
                                                            iNeighborI, iNeighborJ, iNeighborK, iSide, bUpsideDown);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRenderNeighborHalfSlabSide(IBlockAccess blockAccess, int i, int j, int k, int iNeighborSlabSide, boolean bNeighborUpsideDown)
    {
    	if ( isDoubleSlab )
    	{
    		return false;
    	}
    	
		return getIsUpsideDown(blockAccess, i, j, k) != bNeighborUpsideDown;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRenderNeighborFullFaceSide(IBlockAccess blockAccess, int i, int j, int k, int iNeighborSide)
    {
    	if ( isDoubleSlab )
    	{
    		return false;
    	}
    	
    	if ( iNeighborSide < 2 )
    	{
    		boolean bUpsideDown = getIsUpsideDown(blockAccess, i, j, k);
    		
    		if ( iNeighborSide == 0 )
    		{
    			return !bUpsideDown;
    		}
    		else // iNeighborSide == 1
    		{
    			return bUpsideDown;
    		}    			
    	}
    	
		return true;
    }    
	// END FCMOD
}
