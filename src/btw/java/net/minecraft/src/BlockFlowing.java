package net.minecraft.src;

import btw.block.BTWBlocks;
import btw.world.util.WorldUtils;

import java.util.Random;

public class BlockFlowing extends BlockFluid
{
    /**
     * Number of horizontally adjacent liquid source blocks. Diagonal doesn't count. Only source blocks of the same
     * liquid as the block using the field are counted.
     */
    int numAdjacentSources = 0;

    /**
     * Indicates whether the flow direction is optimal. Each array index corresponds to one of the four cardinal
     * directions.
     */
    boolean[] isOptimalFlowDirection = new boolean[4];

    /**
     * The estimated cost to flow in a given direction from the current point. Each array index corresponds to one of
     * the four cardinal directions.
     */
    int[] flowCost = new int[4];

    protected BlockFlowing(int par1, Material par2Material)
    {
        super(par1, par2Material);
    }

    /**
     * Updates the flow for the BlockFlowing object.
     */
    private void updateFlow(World par1World, int par2, int par3, int par4)
    {
        int var5 = par1World.getBlockMetadata(par2, par3, par4);
        par1World.setBlock(par2, par3, par4, this.blockID + 1, var5, 2);
    }

    public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return this.blockMaterial != Material.lava;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        int var6 = this.getFlowDecay(par1World, par2, par3, par4);
        byte var7 = 1;

        if (this.blockMaterial == Material.lava && !par1World.provider.isHellWorld)
        {
            var7 = 2;
        }

        boolean var8 = true;
        int var10;

        if (var6 > 0)
        {
            byte var9 = -100;
            this.numAdjacentSources = 0;
            int var12 = this.getSmallestFlowDecay(par1World, par2 - 1, par3, par4, var9);
            var12 = this.getSmallestFlowDecay(par1World, par2 + 1, par3, par4, var12);
            var12 = this.getSmallestFlowDecay(par1World, par2, par3, par4 - 1, var12);
            var12 = this.getSmallestFlowDecay(par1World, par2, par3, par4 + 1, var12);
            
            // FCMOD: Code added
        	var12 = getSmallestFlowDecayFromCustomSources(par1World, par2, par3, par4, var12);
        	int iTickRate = tickRate(par1World);
            // END FCMOD
        	
            var10 = var12 + var7;

            if (var10 >= 8 || var12 < 0)
            {
                var10 = -1;
            }

            if (this.getFlowDecay(par1World, par2, par3 + 1, par4) >= 0)
            {
                int var11 = this.getFlowDecay(par1World, par2, par3 + 1, par4);

                if (var11 >= 8)
                {
                    var10 = var11;
                }
                else
                {
                    var10 = var11 + 8;
                }
            }

            if (this.numAdjacentSources >= 2 && this.blockMaterial == Material.water)
            {
                if (par1World.getBlockMaterial(par2, par3 - 1, par4).isSolid())
                {
                    var10 = 0;
                }
                else if (par1World.getBlockMaterial(par2, par3 - 1, par4) == this.blockMaterial && par1World.getBlockMetadata(par2, par3 - 1, par4) == 0)
                {
                    var10 = 0;
                }
            }

            if ( this.blockMaterial == Material.lava && var6 < 8 && var10 < 8 && var10 > var6 )
            {
            	iTickRate += iTickRate * ( 1 + par5Random.nextInt( 4 ) );
            }

            if (var10 == var6)
            {
                if (var8)
                {
                    this.updateFlow(par1World, par2, par3, par4);
                }
            }
            else
            {
                var6 = var10;

                if (var10 < 0)
                {
                    par1World.setBlockToAir(par2, par3, par4);
                }
                else
                {
                    par1World.setBlockMetadataWithNotify(par2, par3, par4, var10, 2);
                    par1World.scheduleBlockUpdate( par2, par3, par4, this.blockID, iTickRate );
                    par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
                }
            }
        }
        else
        {
            this.updateFlow(par1World, par2, par3, par4);
        }

        if (this.liquidCanDisplaceBlock(par1World, par2, par3 - 1, par4))
        {
            if (this.blockMaterial == Material.lava && par1World.getBlockMaterial(par2, par3 - 1, par4) == Material.water)
            {
                par1World.setBlock( par2, par3 - 1, par4, BTWBlocks.lavaPillow.blockID );
                this.triggerLavaMixEffects(par1World, par2, par3 - 1, par4);
                return;
            }

            if (var6 >= 8)
            {
                this.flowIntoBlock(par1World, par2, par3 - 1, par4, var6);
            }
            else
            {
                this.flowIntoBlock(par1World, par2, par3 - 1, par4, var6 + 8);
            }
        }
        else if (var6 >= 0 && (var6 == 0 || this.blockBlocksFlow(par1World, par2, par3 - 1, par4)))
        {
            boolean[] var13 = this.getOptimalFlowDirections(par1World, par2, par3, par4);
            var10 = var6 + var7;

            if (var6 >= 8)
            {
                var10 = 1;
            }

            if (var10 >= 8)
            {
                return;
            }

            if (var13[0])
            {
                this.flowIntoBlock(par1World, par2 - 1, par3, par4, var10);
            }

            if (var13[1])
            {
                this.flowIntoBlock(par1World, par2 + 1, par3, par4, var10);
            }

            if (var13[2])
            {
                this.flowIntoBlock(par1World, par2, par3, par4 - 1, var10);
            }

            if (var13[3])
            {
                this.flowIntoBlock(par1World, par2, par3, par4 + 1, var10);
            }
        }
    }

    /**
     * flowIntoBlock(World world, int x, int y, int z, int newFlowDecay) - Flows into the block at the coordinates and
     * changes the block type to the liquid.
     */
    private void flowIntoBlock(World par1World, int par2, int par3, int par4, int par5)
    {
        if (this.liquidCanDisplaceBlock(par1World, par2, par3, par4))
        {
            int var6 = par1World.getBlockId(par2, par3, par4);

            if (var6 > 0)
            {
                if (this.blockMaterial == Material.lava)
                {
                    this.triggerLavaMixEffects(par1World, par2, par3, par4);
                }
                else
                {
                	Block.blocksList[var6].onFluidFlowIntoBlock(par1World, par2, par3, par4, this);
                }
            }

            par1World.setBlock(par2, par3, par4, this.blockID, par5, 3);
        }
    }

    /**
     * calculateFlowCost(World world, int x, int y, int z, int accumulatedCost, int previousDirectionOfFlow) - Used to
     * determine the path of least resistance, this method returns the lowest possible flow cost for the direction of
     * flow indicated. Each necessary horizontal flow adds to the flow cost.
     */
    private int calculateFlowCost(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        int var7 = 1000;

        for (int var8 = 0; var8 < 4; ++var8)
        {
            if ((var8 != 0 || par6 != 1) && (var8 != 1 || par6 != 0) && (var8 != 2 || par6 != 3) && (var8 != 3 || par6 != 2))
            {
                int var9 = par2;
                int var11 = par4;

                if (var8 == 0)
                {
                    var9 = par2 - 1;
                }

                if (var8 == 1)
                {
                    ++var9;
                }

                if (var8 == 2)
                {
                    var11 = par4 - 1;
                }

                if (var8 == 3)
                {
                    ++var11;
                }

                if (!this.blockBlocksFlow(par1World, var9, par3, var11) && (par1World.getBlockMaterial(var9, par3, var11) != this.blockMaterial || par1World.getBlockMetadata(var9, par3, var11) != 0))
                {
                    if (!this.blockBlocksFlow(par1World, var9, par3 - 1, var11))
                    {
                        return par5;
                    }

                    if (par5 < 4)
                    {
                        int var12 = this.calculateFlowCost(par1World, var9, par3, var11, par5 + 1, var8);

                        if (var12 < var7)
                        {
                            var7 = var12;
                        }
                    }
                }
            }
        }

        return var7;
    }

    /**
     * Returns a boolean array indicating which flow directions are optimal based on each direction's calculated flow
     * cost. Each array index corresponds to one of the four cardinal directions. A value of true indicates the
     * direction is optimal.
     */
    private boolean[] getOptimalFlowDirections(World par1World, int par2, int par3, int par4)
    {
        int var5;
        int var6;

        for (var5 = 0; var5 < 4; ++var5)
        {
            this.flowCost[var5] = 1000;
            var6 = par2;
            int var8 = par4;

            if (var5 == 0)
            {
                var6 = par2 - 1;
            }

            if (var5 == 1)
            {
                ++var6;
            }

            if (var5 == 2)
            {
                var8 = par4 - 1;
            }

            if (var5 == 3)
            {
                ++var8;
            }

            if (!this.blockBlocksFlow(par1World, var6, par3, var8) && (par1World.getBlockMaterial(var6, par3, var8) != this.blockMaterial || par1World.getBlockMetadata(var6, par3, var8) != 0))
            {
                if (this.blockBlocksFlow(par1World, var6, par3 - 1, var8))
                {
                    this.flowCost[var5] = this.calculateFlowCost(par1World, var6, par3, var8, 1, var5);
                }
                else
                {
                    this.flowCost[var5] = 0;
                }
            }
        }

        var5 = this.flowCost[0];

        for (var6 = 1; var6 < 4; ++var6)
        {
            if (this.flowCost[var6] < var5)
            {
                var5 = this.flowCost[var6];
            }
        }

        for (var6 = 0; var6 < 4; ++var6)
        {
            this.isOptimalFlowDirection[var6] = this.flowCost[var6] == var5;
        }

        return this.isOptimalFlowDirection;
    }

    /**
     * getSmallestFlowDecay(World world, intx, int y, int z, int currentSmallestFlowDecay) - Looks up the flow decay at
     * the coordinates given and returns the smaller of this value or the provided currentSmallestFlowDecay. If one
     * value is valid and the other isn't, the valid value will be returned. Valid values are >= 0. Flow decay is the
     * amount that a liquid has dissipated. 0 indicates a source block.
     */
    protected int getSmallestFlowDecay(World par1World, int par2, int par3, int par4, int par5)
    {
        int var6 = this.getFlowDecay(par1World, par2, par3, par4);

        if (var6 < 0)
        {
            return par5;
        }
        else
        {
            if (var6 == 0)
            {
                ++this.numAdjacentSources;
            }

            if (var6 >= 8)
            {
                var6 = 0;
            }

            return par5 >= 0 && var6 >= par5 ? par5 : var6;
        }
    }

    /**
     * Returns true if the block at the coordinates can be displaced by the liquid.
     */
    private boolean liquidCanDisplaceBlock(World par1World, int par2, int par3, int par4)
    {
        Material var5 = par1World.getBlockMaterial(par2, par3, par4);
        return var5 == this.blockMaterial ? false : (var5 == Material.lava ? false : !this.blockBlocksFlow(par1World, par2, par3, par4));
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        super.onBlockAdded(par1World, par2, par3, par4);

        if (par1World.getBlockId(par2, par3, par4) == this.blockID)
        {
            par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World));
        }
    }

    public boolean func_82506_l()
    {
        return false;
    }
    
    // FCMOD: Added New   
    @Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iNeighborBlockID )
    {
    	super.onNeighborBlockChange( world, i, j, k, iNeighborBlockID );
    	
    	// these blocks normally have an update scheduled, and randomly get ticked to 
    	// ensure they don't get stalled on chunk load, but can sit there for extend periods
    	// until that happens, so this ensures player interaction will get them going again
    	
    	if ( !world.isUpdatePendingThisTickForBlock(i, j, k, blockID) )
    	{
    		world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
    	}
    }

	private int getSmallestFlowDecayFromCustomSources(World world, int i, int j, int k, int iSmallestFlowDecay)
	{
		// note that this function doesn't update numAdjacentSources as that is used for new source block creation
		
        if ( iSmallestFlowDecay != 0 )
        {
        	for ( int iFacing = 0; iFacing < 6; iFacing++ )
        	{
        		int iTargetDecay = WorldUtils.isValidSourceForFluidBlockToFacing(world, i, j, k, iFacing);
        		
				if ( iTargetDecay == 0 )
				{
					iSmallestFlowDecay = 0;
					
					break;
				}
				
				if ( iTargetDecay > 0 )
				{
					if ( iSmallestFlowDecay < 0 || iTargetDecay < iSmallestFlowDecay )
					{
						iSmallestFlowDecay = iTargetDecay;
					}
				}
        	}
        }
        
		return iSmallestFlowDecay;
	}
	
    public boolean blockBlocksFlow( World world, int i, int j, int k )
    {
        Block block = blocksList[world.getBlockId( i, j, k )];
        
        return block != null && block.getPreventsFluidFlow(world, i, j, k, this);
    }
	// END FCMOD
}
