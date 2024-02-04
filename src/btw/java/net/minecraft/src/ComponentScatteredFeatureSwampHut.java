package net.minecraft.src;

import btw.block.BTWBlocks;
import btw.block.tileentity.WickerBasketTileEntity;
import btw.entity.mob.WitchEntity;
import btw.item.BTWItems;
import btw.item.util.RandomItemStack;

import java.util.Random;

public class ComponentScatteredFeatureSwampHut extends ComponentScatteredFeature
{
    /** Whether this swamp hut has a witch. */
    private boolean hasWitch;

    public ComponentScatteredFeatureSwampHut(Random par1Random, int par2, int par3)
    {
        super(par1Random, par2, 64, par3, 7, 5, 9);
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...
     */
    public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
    {
        if (!this.func_74935_a(par1World, par3StructureBoundingBox, 0))
        {
            return false;
        }
        else
        {
            this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 1, 1, 1, 5, 1, 7, Block.planks.blockID, 1, Block.planks.blockID, 1, false);
            this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 1, 4, 2, 5, 4, 7, Block.planks.blockID, 1, Block.planks.blockID, 1, false);
            this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 2, 1, 0, 4, 1, 0, Block.planks.blockID, 1, Block.planks.blockID, 1, false);
            this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 2, 2, 2, 3, 3, 2, Block.planks.blockID, 1, Block.planks.blockID, 1, false);
            this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 1, 2, 3, 1, 3, 6, Block.planks.blockID, 1, Block.planks.blockID, 1, false);
            this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 5, 2, 3, 5, 3, 6, Block.planks.blockID, 1, Block.planks.blockID, 1, false);
            this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 2, 2, 7, 4, 3, 7, Block.planks.blockID, 1, Block.planks.blockID, 1, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 0, 2, 1, 3, 2, Block.wood.blockID, Block.wood.blockID, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 5, 0, 2, 5, 3, 2, Block.wood.blockID, Block.wood.blockID, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 0, 7, 1, 3, 7, Block.wood.blockID, Block.wood.blockID, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 5, 0, 7, 5, 3, 7, Block.wood.blockID, Block.wood.blockID, false);
            this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, 2, 3, 2, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, 3, 3, 7, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, 0, 0, 1, 3, 4, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, 0, 0, 5, 3, 4, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, 0, 0, 5, 3, 5, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.flowerPot.blockID, 7, 1, 3, 5, par3StructureBoundingBox);
            // FCMOD: Code added to add brewing stand to witch huts
            this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 1, 2, 2, 6, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.brewingStand.blockID, 0, 2, 3, 6, par3StructureBoundingBox);
            // END FCMOD
            this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, 1, 2, 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, 5, 2, 1, par3StructureBoundingBox);
            int var4 = this.getMetadataWithOffset(Block.stairsWoodOak.blockID, 3);
            int var5 = this.getMetadataWithOffset(Block.stairsWoodOak.blockID, 1);
            int var6 = this.getMetadataWithOffset(Block.stairsWoodOak.blockID, 0);
            int var7 = this.getMetadataWithOffset(Block.stairsWoodOak.blockID, 2);
            this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 0, 4, 1, 6, 4, 1, Block.stairsWoodSpruce.blockID, var4, Block.stairsWoodSpruce.blockID, var4, false);
            this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 0, 4, 2, 0, 4, 7, Block.stairsWoodSpruce.blockID, var6, Block.stairsWoodSpruce.blockID, var6, false);
            this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 6, 4, 2, 6, 4, 7, Block.stairsWoodSpruce.blockID, var5, Block.stairsWoodSpruce.blockID, var5, false);
            this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 0, 4, 8, 6, 4, 8, Block.stairsWoodSpruce.blockID, var7, Block.stairsWoodSpruce.blockID, var7, false);
            int var8;
            int var9;

            for (var8 = 2; var8 <= 7; var8 += 5)
            {
                for (var9 = 1; var9 <= 5; var9 += 4)
                {
                    this.fillCurrentPositionBlocksDownwards(par1World, Block.wood.blockID, 0, var9, -1, var8, par3StructureBoundingBox);
                }
            }

            // FCMOD: Added
            if ( !hasLootBasket)
            {
            	addLootBasket(par1World, par3StructureBoundingBox, 3, 2, 6);
            }
            // END FCMOD
            
            if (!this.hasWitch)
            {
                var8 = this.getXWithOffset(2, 5);
                var9 = this.getYWithOffset(2);
                int var10 = this.getZWithOffset(2, 5);

                if (par3StructureBoundingBox.isVecInside(var8, var9, var10))
                {
                    this.hasWitch = true;
                    WitchEntity var11 = (WitchEntity) EntityList.createEntityOfType(WitchEntity.class, par1World);
                    var11.preInitCreature();
                    var11.setLocationAndAngles((double)var8 + 0.5D, (double)var9, (double)var10 + 0.5D, 0.0F, 0.0F);
                    var11.initCreature();
                    // FCMOD: Added
                    var11.setPersistent(true);
                    // END FCMOD
                    par1World.spawnEntityInWorld(var11);
                }
                
            	// FCMOD: Added
            	spawnAdditionalWitches(par1World);
            	// END FCMOD
            }

            return true;
        }
    }
    
    // FCMOD: Added
    private boolean hasLootBasket = false;
    
    private static RandomItemStack[] lootBasketContents = null;
    
    private void initContentsArray()
    {
        lootBasketContents = new RandomItemStack[] {
    		new RandomItemStack( BTWItems.hempSeeds.itemID, 0, 1, 4, 5 ),
        	new RandomItemStack( Item.glassBottle.itemID, 0, 2, 8, 10 ),
        	new RandomItemStack( BTWItems.redMushroom.itemID, 0, 5, 16, 5 )
        }; 
    }
    
    private void spawnAdditionalWitches(World world)
    {
    	int iNumWitches = 2;
    	
    	if ( !hasWitch )
    	{
    		iNumWitches++;
    	}

    	// constrain spawning to the chunk containing the structure component that we know is loaded
    	// note that we can't rely on neighboring chunks with a component, like we can with a structure
    	
        int iMinSpawnX = ( boundingBox.minX >> 4 ) << 4;
        int iMinSpawnZ = ( boundingBox.minZ >> 4 ) << 4;
        
        int iSpawnZoneWidth = 16;
    	
    	for ( int iTempCount = 0; iTempCount < iNumWitches; iTempCount++ )
    	{
    		for ( int iTempTries = 0; iTempTries < 20; iTempTries++ )
    		{
    			int x = iMinSpawnX + world.rand.nextInt( iSpawnZoneWidth );
    			int z = iMinSpawnZ + world.rand.nextInt( iSpawnZoneWidth );
    			
    			int y = world.getTopSolidOrLiquidBlock( x, z );
    			
    			if (SpawnerAnimals.canEntitySpawnDuringWorldGen(WitchEntity.class, world, x, y, z ))
				{
                    hasWitch = true;
                    
                    WitchEntity witch = (WitchEntity) EntityList.createEntityOfType(WitchEntity.class, world);
                    
                    // FCMOD: Added
                    witch.preInitCreature();
                    // END FCMOD
                    witch.setLocationAndAngles( (double)x + 0.5D, (double)y, (double)z + 0.5D, 0.0F, 0.0F);                    
                    witch.initCreature();                    
                    witch.setPersistent(true);
                    
                    world.spawnEntityInWorld( witch );
                    
                    break;
				}
    		}
    	}
    }
    
    private void addLootBasket(World world, StructureBoundingBox boundingBox, int iRelX, int iRelY, int iRelZ)
    {
    	if (lootBasketContents == null )
    	{
    		// only initialize array on first use to ensure referenced mod items are intialized
    		initContentsArray();
    	}
    	
    	int i = getXWithOffset( iRelX, iRelZ );
        int j = getYWithOffset( iRelY );
        int k = getZWithOffset( iRelX, iRelZ );
    	
        if ( boundingBox.isVecInside( i, j, k) && world.getBlockId( i, j, k ) != BTWBlocks.wickerBasket.blockID )
        {
            hasLootBasket = true;
        	
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
