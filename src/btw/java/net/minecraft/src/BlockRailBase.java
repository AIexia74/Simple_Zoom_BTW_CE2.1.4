package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Random;

public abstract class BlockRailBase extends Block
{
    /** Power related rails have this field at true. */
    protected final boolean isPowered;

    /**
     * Returns true if the block at the coordinates of world passed is a valid rail block (current is rail, powered or
     * detector).
     */
    public static final boolean isRailBlockAt(World par0World, int par1, int par2, int par3)
    {
        return isRailBlock(par0World.getBlockId(par1, par2, par3));
    }

    /**
     * Return true if the parameter is a blockID for a valid rail block (current is rail, powered or detector).
     */
    public static final boolean isRailBlock(int par0)
    {
    	return Block.blocksList[par0] instanceof BlockRailBase;
    }

    protected BlockRailBase(int par1, boolean par2)
    {
        super(par1, Material.circuits);
        this.isPowered = par2;
        initBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        this.setCreativeTab(CreativeTabs.tabTransport);
    }

    /**
     * Returns true if the block is power related rail.
     */
    public boolean isPowered()
    {
        return this.isPowered;
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 9;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random par1Random)
    {
        return 1;
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        return par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4);
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        if (!par1World.isRemote)
        {
            this.refreshTrackShape(par1World, par2, par3, par4, true);

            if (this.isPowered)
            {
                this.onNeighborBlockChange(par1World, par2, par3, par4, this.blockID);
            }
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        if (!par1World.isRemote)
        {
            int var6 = par1World.getBlockMetadata(par2, par3, par4);
            int var7 = var6;

            if (this.isPowered)
            {
                var7 = var6 & 7;
            }

            boolean var8 = false;

            if (!par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4))
            {
                var8 = true;
            }

            if (var7 == 2 && !par1World.doesBlockHaveSolidTopSurface(par2 + 1, par3, par4))
            {
                var8 = true;
            }

            if (var7 == 3 && !par1World.doesBlockHaveSolidTopSurface(par2 - 1, par3, par4))
            {
                var8 = true;
            }

            if (var7 == 4 && !par1World.doesBlockHaveSolidTopSurface(par2, par3, par4 - 1))
            {
                var8 = true;
            }

            if (var7 == 5 && !par1World.doesBlockHaveSolidTopSurface(par2, par3, par4 + 1))
            {
                var8 = true;
            }

            if (var8)
            {
            	// FCMOD: Code added to prevent item duplication since multiple notifications can occur at once
            	if ( par1World.getBlockId( par2, par3, par4 ) != blockID )
            	{
            		return;
            	}
            	// END FCMOD
                this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
                par1World.setBlockToAir(par2, par3, par4);
            }
            else
            {
                this.func_94358_a(par1World, par2, par3, par4, var6, var7, par5);
            }
        }
    }

    protected void func_94358_a(World par1World, int par2, int par3, int par4, int par5, int par6, int par7) {}

    /**
     * Completely recalculates the track shape based on neighboring tracks
     */
    protected void refreshTrackShape(World par1World, int par2, int par3, int par4, boolean par5)
    {
        if (!par1World.isRemote)
        {
            (new BlockBaseRailLogic(this, par1World, par2, par3, par4)).func_94511_a(par1World.isBlockIndirectlyGettingPowered(par2, par3, par4), par5);
        }
    }

    /**
     * Returns the mobility information of the block, 0 = free, 1 = can't push but can move over, 2 = total immobility
     * and stop pistons
     */
    public int getMobilityFlag()
    {
        return 0;
    }

    /**
     * ejects contained items into the world, and notifies neighbours of an update, as appropriate
     */
    public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        int var7 = par6;

        if (this.isPowered)
        {
            var7 = par6 & 7;
        }

        super.breakBlock(par1World, par2, par3, par4, par5, par6);

        if (var7 == 2 || var7 == 3 || var7 == 4 || var7 == 5)
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, par5);
        }

        if (this.isPowered)
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4, par5);
            par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, par5);
        }
    }
    
	// FCMOD: Added New
	@Override
	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
	@Override
	public int rotateMetadataAroundJAxis(int iMetadata, boolean bReverse)
	{
		int iDirection = iMetadata;
		
		if ( isPowered() ) // this should never be true
		{
			iDirection &= 7;
		}
		
		if ( iDirection == 0 )
		{
			iDirection = 1;
		}
		else if ( iDirection == 1 )
		{
			iDirection = 0;
		}
		// sloped rails
		else if ( iDirection == 2 )
		{
		}
		else if ( iDirection == 3 )
		{
		}
		else if ( iDirection == 4 )
		{
		}
		else if ( iDirection == 5 )
		{
		}
		// curved rails
		else if ( iDirection == 6 )
		{
			if ( bReverse )
			{
				iDirection = 7;
			}
			else
			{
				iDirection = 9;
			}
		}
		else if ( iDirection == 7 )
		{
			if ( bReverse )
			{
				iDirection = 8;
			}
			else
			{
				iDirection = 6;
			}
		}
		else if ( iDirection == 8 )
		{
			if ( bReverse )
			{
				iDirection = 9;
			}
			else
			{
				iDirection = 7;
			}
		}
		else if ( iDirection == 9 )
		{
			if ( bReverse )
			{
				iDirection = 6;
			}
			else
			{
				iDirection = 8;
			}
		}
		
		if ( isPowered() ) // this should never be true
		{
			iMetadata = (iMetadata & 8) | iDirection;
		}
		else
		{
			iMetadata = iDirection;
		}
		
		return iMetadata;
	}
	
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
    	IBlockAccess blockAccess, int i, int j, int k)
    {
        int iDirection = blockAccess.getBlockMetadata( i, j, k );

        if ( iDirection >= 2 && iDirection <= 5 )
        {
        	return AxisAlignedBB.getAABBPool().getAABB(         	
        		0D, 0D, 0D, 1D, 0.625D, 1D );
        }
        else
        {
        	return AxisAlignedBB.getAABBPool().getAABB(         	
        		0D, 0D, 0D, 1D, 0.125F, 1D );
        }
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
    	return renderer.renderBlockMinecartTrack( this, i, j, k );
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, 
    	int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(
			iNeighborI, iNeighborJ, iNeighborK, iSide);
    }	
	// END FCMOD
}
