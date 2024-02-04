package net.minecraft.src;

import btw.block.BTWBlocks;

import java.util.List;
import java.util.Random;

public class ComponentVillageField2 extends ComponentVillage
{
    private int averageGroundLevel = -1;

    /** Second crop type for this field. */
    private int secondaryCrop;

    public ComponentVillageField2(ComponentVillageStartPiece par1ComponentVillageStartPiece, int par2, Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5)
    {
        super(par1ComponentVillageStartPiece, par2);
        this.coordBaseMode = par5;
        this.boundingBox = par4StructureBoundingBox;
        this.secondaryCrop = this.pickRandomCrop(par3Random);
        this.secondaryCrop = this.pickRandomCrop(par3Random);
    }

    /**
     * Returns a crop type to be planted on this field.
     */
    private int pickRandomCrop(Random par1Random)
    {
        switch (par1Random.nextInt(5))
        {
            case 0:
                return Block.carrot.blockID;

            case 1:
                return Block.potato.blockID;

            default:
                return BTWBlocks.wheatCrop.blockID;
        }
    }

    public static ComponentVillageField2 func_74902_a(ComponentVillageStartPiece par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        StructureBoundingBox var8 = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 7, 4, 9, par6);
        return canVillageGoDeeper(var8) && StructureComponent.findIntersecting(par1List, var8) == null ? new ComponentVillageField2(par0ComponentVillageStartPiece, par7, par2Random, var8, par6) : null;
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...
     */
    public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
    {
    	// FCMOD: Added
        secondaryCrop = startPiece.getSecondaryCropBlockID(par1World);
    	
        //Uses world rand to not disturb random number generator
        if (secondaryCrop == BTWBlocks.carrotCrop.blockID && par1World.rand.nextBoolean()) {
        	secondaryCrop = BTWBlocks.floweringCarrotCrop.blockID;
        }
        // END FCMOD
        
        if (this.averageGroundLevel < 0)
        {
            this.averageGroundLevel = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);

            if (this.averageGroundLevel < 0)
            {
                return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 4 - 1, 0);
        }

        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 1, 0, 6, 4, 8, 0, 0, false);
    	int iAbandonmentLevel = startPiece.getAbandonmentLevel(par1World);
    	if ( iAbandonmentLevel <= 1 )
    	{
            fillWithBlocks(par1World, par3StructureBoundingBox, 1, 0, 1, 2, 0, 7, 
            	BTWBlocks.farmland.blockID,
            	BTWBlocks.farmland.blockID, false );
            
            fillWithBlocks(par1World, par3StructureBoundingBox, 4, 0, 1, 5, 0, 7, 
            	BTWBlocks.farmland.blockID,
            	BTWBlocks.farmland.blockID, false );
    	}
    	else
    	{
            fillWithBlocks(par1World, par3StructureBoundingBox, 1, 0, 1, 2, 0, 7, 
            	BTWBlocks.looseDirt.blockID,
            	BTWBlocks.looseDirt.blockID, false );
            
            fillWithBlocks(par1World, par3StructureBoundingBox, 4, 0, 1, 5, 0, 7, 
            	BTWBlocks.looseDirt.blockID,
            	BTWBlocks.looseDirt.blockID, false );
    	}
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 0, 0, 8, Block.wood.blockID, Block.wood.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 6, 0, 0, 6, 0, 8, Block.wood.blockID, Block.wood.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 0, 0, 5, 0, 0, Block.wood.blockID, Block.wood.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 0, 8, 5, 0, 8, Block.wood.blockID, Block.wood.blockID, false);
        // FCMOD: Added
    	if ( iAbandonmentLevel <= 1 )
    	// END FCMOD
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 3, 0, 1, 3, 0, 7, Block.waterMoving.blockID, Block.waterMoving.blockID, false);
        int var4;

		int cropGrowth;
		
        for (var4 = 1; var4 <= 7; ++var4)
        {
			cropGrowth = MathHelper.getRandomIntegerInRange(par2Random, 2, 5) + 3;
			placeBlockAtCurrentPosition(par1World, BTWBlocks.wheatCrop.blockID, cropGrowth, 1, 1, var4, par3StructureBoundingBox);
			if (cropGrowth == 7) {
				placeBlockAtCurrentPosition(par1World, BTWBlocks.wheatCropTop.blockID, 3, 1, 2, var4, par3StructureBoundingBox);
			}
	
			cropGrowth = MathHelper.getRandomIntegerInRange(par2Random, 2, 5) + 3;
			placeBlockAtCurrentPosition(par1World, BTWBlocks.wheatCrop.blockID, cropGrowth, 2, 1, var4, par3StructureBoundingBox);
			if (cropGrowth == 7) {
				placeBlockAtCurrentPosition(par1World, BTWBlocks.wheatCropTop.blockID, 3, 2, 2, var4, par3StructureBoundingBox);
			}
	
			cropGrowth = MathHelper.getRandomIntegerInRange(par2Random, 2, 5) + 3;
			placeBlockAtCurrentPosition(par1World, secondaryCrop, cropGrowth, 4, 1, var4, par3StructureBoundingBox);
			if (cropGrowth == 7 && secondaryCrop == BTWBlocks.wheatCrop.blockID) {
				placeBlockAtCurrentPosition(par1World, BTWBlocks.wheatCropTop.blockID, 3, 4, 2, var4, par3StructureBoundingBox);
			}
	
			cropGrowth = MathHelper.getRandomIntegerInRange(par2Random, 2, 5) + 3;
			placeBlockAtCurrentPosition(par1World, secondaryCrop, cropGrowth, 5, 1, var4, par3StructureBoundingBox);
			if (cropGrowth == 7 && secondaryCrop == BTWBlocks.wheatCrop.blockID) {
				placeBlockAtCurrentPosition(par1World, BTWBlocks.wheatCropTop.blockID, 3, 5, 2, var4, par3StructureBoundingBox);
			}

            // FCMOD: Added
        	if ( iAbandonmentLevel > 1 )
            {
            	// delete all generated crops after placing so as not to disturb random number generator
            	
                this.placeBlockAtCurrentPosition(par1World, 0, 0, 1, 1, var4, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, 0, 0, 2, 1, var4, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, 0, 0, 4, 1, var4, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, 0, 0, 5, 1, var4, par3StructureBoundingBox);
	
				this.placeBlockAtCurrentPosition(par1World, 0, 0, 1, 2, var4, par3StructureBoundingBox);
				this.placeBlockAtCurrentPosition(par1World, 0, 0, 2, 2, var4, par3StructureBoundingBox);
				this.placeBlockAtCurrentPosition(par1World, 0, 0, 4, 2, var4, par3StructureBoundingBox);
				this.placeBlockAtCurrentPosition(par1World, 0, 0, 5, 2, var4, par3StructureBoundingBox);
            }
        	else if ( iAbandonmentLevel == 1 )
        	{
        		// randomly destroy some of the crops to give the impression of the field being only partially tended
        		
        		for ( int iTempCount = 1; iTempCount <= 4; iTempCount += 3 )
        		{
        			// use the world random so as not to mess with generation
        			
        			if ( par1World.rand.nextInt( 3 ) != 0 )
        			{
                        this.placeBlockAtCurrentPosition(par1World, 0, 0, iTempCount, 1, var4, par3StructureBoundingBox);
						this.placeBlockAtCurrentPosition(par1World, 0, 0, iTempCount, 2, var4, par3StructureBoundingBox);
        			}
        			
        			if ( par1World.rand.nextInt( 3 ) != 0 )
        			{
                        this.placeBlockAtCurrentPosition(par1World, 0, 0, iTempCount + 1, 1, var4, par3StructureBoundingBox);
						this.placeBlockAtCurrentPosition(par1World, 0, 0, iTempCount + 1, 2, var4, par3StructureBoundingBox);
        			}
        		}
        	}        		
            // END FCMOD
        }

        for (var4 = 0; var4 < 9; ++var4)
        {
            for (int var5 = 0; var5 < 7; ++var5)
            {
                this.clearCurrentPositionBlocksUpwards(par1World, var5, 4, var4, par3StructureBoundingBox);
                this.fillCurrentPositionBlocksDownwards(par1World, Block.dirt.blockID, 0, var5, -1, var4, par3StructureBoundingBox);
            }
        }

        return true;
    }
}
