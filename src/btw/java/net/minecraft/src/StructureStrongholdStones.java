package net.minecraft.src;

import btw.block.BTWBlocks;

import java.util.Random;

class StructureStrongholdStones extends StructurePieceBlockSelector
{
    private StructureStrongholdStones() {}

    /**
     * picks Block Ids and Metadata (Silverfish)
     */
    public void selectBlocks(Random par1Random, int par2, int par3, int par4, boolean par5)
    {
        if (par5)
        {
            this.selectedBlockId = Block.stoneBrick.blockID;
            float var6 = par1Random.nextFloat();

            if (var6 < 0.2F)
            {
                this.selectedBlockMetaData = 2;
            }
            else if (var6 < 0.5F)
            {
                this.selectedBlockMetaData = 1;
            }
            else if (var6 < 0.52F)
            {
            	this.selectedBlockId = BTWBlocks.infestedStoneBrick.blockID;
                this.selectedBlockMetaData = 0;
            }
            else if (var6 < 0.535F)
            {
            	this.selectedBlockId = BTWBlocks.infestedMossyStoneBrick.blockID;
                this.selectedBlockMetaData = 0;
            }
            else if (var6 < 0.55F)
            {
            	this.selectedBlockId = BTWBlocks.infestedCrackedStoneBrick.blockID;
                this.selectedBlockMetaData = 0;
            }
            else
            {
                this.selectedBlockMetaData = 0;
            }
        }
        else
        {
            this.selectedBlockId = 0;
            this.selectedBlockMetaData = 0;
        }
    }

    StructureStrongholdStones(StructureStrongholdPieceWeight2 par1StructureStrongholdPieceWeight2)
    {
        this();
    }
}
