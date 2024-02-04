package net.minecraft.src;

import btw.block.blocks.PlantsBlock;

import java.util.Random;

public class BlockFlower extends PlantsBlock
{
	/**
	 * FCNOTE: This class would be better called BlockPlants, as it acts as a common base class
	 * for vegetation and crops.
	 */
    protected BlockFlower(int par1, Material par2Material)
    {
        super(par1, par2Material);
        this.setTickRandomly(true);
        float var3 = 0.2F;
        initBlockBounds(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, var3 * 3.0F, 0.5F + var3);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    protected BlockFlower(int par1)
    {
        this(par1, Material.plants);
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
        this.checkFlowerChange(par1World, par2, par3, par4);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        this.checkFlowerChange(par1World, par2, par3, par4);
    }

    protected final void checkFlowerChange(World par1World, int par2, int par3, int par4)
    {
        if (!this.canBlockStay(par1World, par2, par3, par4))
        {
            this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
            par1World.setBlockToAir(par2, par3, par4);
        }
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
        return 1;
    }
    
    // FCMOD: Added New
    @Override
    public boolean canBlockStay( World world, int i, int j, int k )
    {
        return ( world.getFullBlockLightValue( i, j, k ) >= 8 || 
        	world.canBlockSeeTheSky( i, j, k ) ) && super.canBlockStay( world, i, j, k ); 
    }
    
    @Override
    public boolean canBeGrazedOn(IBlockAccess blockAccess, int i, int j, int k,
                                 EntityAnimal animal)
    {
		return animal.canGrazeOnRoughVegetation();
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//
    // END FCMOD
}
