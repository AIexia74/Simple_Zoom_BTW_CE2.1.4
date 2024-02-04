package net.minecraft.src;

import btw.block.BTWBlocks;
import btw.block.tileentity.WickerBasketTileEntity;
import btw.item.BTWItems;
import btw.item.util.RandomItemStack;

import java.util.Random;

public class WorldGenDesertWells extends WorldGenerator
{
    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        while (par1World.isAirBlock(par3, par4, par5) && par4 > 2)
        {
            --par4;
        }

        int var6 = par1World.getBlockId(par3, par4, par5);

        if (var6 != Block.sand.blockID)
        {
            return false;
        }
        else
        {
            int var7;
            int var8;

            for (var7 = -2; var7 <= 2; ++var7)
            {
                for (var8 = -2; var8 <= 2; ++var8)
                {
                    if (par1World.isAirBlock(par3 + var7, par4 - 1, par5 + var8) && par1World.isAirBlock(par3 + var7, par4 - 2, par5 + var8))
                    {
                        return false;
                    }
                }
            }

            for (var7 = -1; var7 <= 0; ++var7)
            {
                for (var8 = -2; var8 <= 2; ++var8)
                {
                    for (int var9 = -2; var9 <= 2; ++var9)
                    {
                        par1World.setBlock(par3 + var8, par4 + var7, par5 + var9, Block.sandStone.blockID, 0, 2);
                    }
                }
            }

            par1World.setBlock(par3, par4, par5, Block.waterMoving.blockID, 0, 2);
            par1World.setBlock(par3 - 1, par4, par5, Block.waterMoving.blockID, 0, 2);
            par1World.setBlock(par3 + 1, par4, par5, Block.waterMoving.blockID, 0, 2);
            par1World.setBlock(par3, par4, par5 - 1, Block.waterMoving.blockID, 0, 2);
            par1World.setBlock(par3, par4, par5 + 1, Block.waterMoving.blockID, 0, 2);

            for (var7 = -2; var7 <= 2; ++var7)
            {
                for (var8 = -2; var8 <= 2; ++var8)
                {
                    if (var7 == -2 || var7 == 2 || var8 == -2 || var8 == 2)
                    {
                        par1World.setBlock(par3 + var7, par4 + 1, par5 + var8, Block.sandStone.blockID, 0, 2);
                    }
                }
            }

            par1World.setBlock(par3 + 2, par4 + 1, par5, Block.stoneSingleSlab.blockID, 1, 2);
            par1World.setBlock(par3 - 2, par4 + 1, par5, Block.stoneSingleSlab.blockID, 1, 2);
            par1World.setBlock(par3, par4 + 1, par5 + 2, Block.stoneSingleSlab.blockID, 1, 2);
            par1World.setBlock(par3, par4 + 1, par5 - 2, Block.stoneSingleSlab.blockID, 1, 2);

            for (var7 = -1; var7 <= 1; ++var7)
            {
                for (var8 = -1; var8 <= 1; ++var8)
                {
                    if (var7 == 0 && var8 == 0)
                    {
                        par1World.setBlock(par3 + var7, par4 + 4, par5 + var8, Block.sandStone.blockID, 0, 2);
                    }
                    else
                    {
                        par1World.setBlock(par3 + var7, par4 + 4, par5 + var8, Block.stoneSingleSlab.blockID, 1, 2);
                    }
                }
            }

            for (var7 = 1; var7 <= 3; ++var7)
            {
                par1World.setBlock(par3 - 1, par4 + var7, par5 - 1, Block.sandStone.blockID, 0, 2);
                par1World.setBlock(par3 - 1, par4 + var7, par5 + 1, Block.sandStone.blockID, 0, 2);
                par1World.setBlock(par3 + 1, par4 + var7, par5 - 1, Block.sandStone.blockID, 0, 2);
                par1World.setBlock(par3 + 1, par4 + var7, par5 + 1, Block.sandStone.blockID, 0, 2);
            }
            
            // FCMOD: Added
            addModBlocks(par1World, par3, par4, par5);
            // END FCMOD

            return true;
        }
    }
    
    // FCMOD: Added
    private static RandomItemStack[] lootBasketContents = null;
    
    private void initContentsArray()
    {
        lootBasketContents = new RandomItemStack[] {
    		new RandomItemStack( BTWItems.hempSeeds.itemID, 0, 1, 4, 5 ),
        	new RandomItemStack( Item.glassBottle.itemID, 0, 2, 8, 10 ),
        }; 
    }
    
    private void addModBlocks(World world, int i, int j, int k)
    {
    	int iNumBaskets = world.rand.nextInt( 3 );
    	
    	for ( int iTempCount = 0; iTempCount < iNumBaskets; iTempCount++ )
    	{
	    	int iBasketI = i + ( world.rand.nextInt( 2 ) == 0 ? -2 : 2 );
	    	int iBasketJ = j + 2;
	    	int iBasketK = k + ( world.rand.nextInt( 2 ) == 0 ? -2 : 2 );
	    	
	    	addLootBasket(world, iBasketI, iBasketJ, iBasketK);
    	}
    }
    
    private void addLootBasket(World world, int i, int j, int k)
    {
    	if (lootBasketContents == null )
    	{
    		// only initialize array on first use to ensure referenced mod items are intialized
    		initContentsArray();
    	}
    	
        if ( world.getBlockId( i, j, k ) != BTWBlocks.wickerBasket.blockID )
        {
	    	world.setBlock( i, j, k, BTWBlocks.wickerBasket.blockID, world.rand.nextInt( 4 ) | 4, 2 );
	    	
	    	WickerBasketTileEntity tileEntity = (WickerBasketTileEntity)world.getBlockTileEntity( i, j, k );
	    	
	    	if ( tileEntity != null )
	    	{
	    		tileEntity.setStorageStack(RandomItemStack.getRandomStack(world.rand, lootBasketContents));
	    	}
        }	    	
    }
    // END FCMOD
}
