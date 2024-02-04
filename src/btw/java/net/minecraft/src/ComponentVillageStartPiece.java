package net.minecraft.src;

import btw.block.BTWBlocks;
import btw.util.hardcorespawn.HardcoreSpawnUtils;

import java.util.ArrayList;
import java.util.Random;

public class ComponentVillageStartPiece extends ComponentVillageWell
{
    public final WorldChunkManager worldChunkMngr;

    /** Boolean that determines if the village is in a desert or not. */
    public final boolean inDesert;
    private static ArrayList<BiomeGenBase> desertBiomes = new ArrayList();

    /** World terrain type, 0 for normal, 1 for flap map */
    public final int terrainType;
    public StructureVillagePieceWeight structVillagePieceWeight;

    /**
     * Contains List of all spawnable Structure Piece Weights. If no more Pieces of a type can be spawned, they are
     * removed from this list
     */
    public ArrayList structureVillageWeightedPieceList;
    public ArrayList field_74932_i = new ArrayList();
    public ArrayList field_74930_j = new ArrayList();

    public ComponentVillageStartPiece(WorldChunkManager par1WorldChunkManager, int par2, Random par3Random, int par4, int par5, ArrayList par6ArrayList, int par7)
    {
        super((ComponentVillageStartPiece)null, 0, par3Random, par4, par5);
        this.worldChunkMngr = par1WorldChunkManager;
        this.structureVillageWeightedPieceList = par6ArrayList;
        this.terrainType = par7;
        BiomeGenBase var8 = par1WorldChunkManager.getBiomeGenAt(par4, par5);
        this.inDesert = isDesertBiome(var8);
        this.startPiece = this;
    }

    public WorldChunkManager getWorldChunkManager()
    {
        return this.worldChunkMngr;
    }
    
    public static void addDesertBiome(BiomeGenBase biome) {
    	desertBiomes.add(biome);
    }
    
    public static boolean isDesertBiome(BiomeGenBase biome) {
    	return desertBiomes.contains(biome);
    }
    
    // FCMOD: Code added
    private int abandonmentLevel;  // 0 = not abandoned, 1 = partially abandoned 2 = fully abandonded
    private int primaryCropBlockID;
    private int secondaryCropBlockID;
    private boolean modSpecificDataInitialized = false;

    public int getAbandonmentLevel(World world)
    {
    	checkIfModSpecificDataRequiresInit(world);
    	
    	return abandonmentLevel;
    }
    
    public int getPrimaryCropBlockID(World world)
    {
    	checkIfModSpecificDataRequiresInit(world);
    	
    	return primaryCropBlockID;
    }
    
    public int getSecondaryCropBlockID(World world)
    {
    	checkIfModSpecificDataRequiresInit(world);
    	
    	return secondaryCropBlockID;
    }
    
    private void checkIfModSpecificDataRequiresInit(World world)
    {
    	if ( !modSpecificDataInitialized)
    	{
    		initializeModSpecificData(world);
    	}
    }
    
    private void initializeModSpecificData(World world)
    {
		modSpecificDataInitialized = true;
		abandonmentLevel = 0;
    	
    	int iSpawnX = world.getWorldInfo().getSpawnX();
    	int iSpawnZ = world.getWorldInfo().getSpawnZ();
    	
    	int iVillageX = boundingBox.getCenterX();
    	int iVillageZ = boundingBox.getCenterZ();
    	
    	double dDeltaX = (double)( iSpawnX - iVillageX );
    	double dDeltaZ = (double)( iSpawnZ - iVillageZ );
    	
    	double dDistSqFromSpawn = dDeltaX * dDeltaX + dDeltaZ * dDeltaZ;
    	double dAbandonedRadius = HardcoreSpawnUtils.getAbandonedVillageRadius(world);
    	
    	if ( dDistSqFromSpawn < ( dAbandonedRadius * dAbandonedRadius ) )
    	{
			abandonmentLevel = 2;
    	}
    	else
    	{
    		double dPartiallyAbandonedRadius = HardcoreSpawnUtils.getPartiallyAbandonedVillageRadius(world);
    		
    		if ( dDistSqFromSpawn < ( dPartiallyAbandonedRadius * dPartiallyAbandonedRadius ) )
    		{
				abandonmentLevel = 1;

				primaryCropBlockID = BTWBlocks.wheatCrop.blockID;
				secondaryCropBlockID = BTWBlocks.wheatCrop.blockID;
    		}
    		else
    		{
				primaryCropBlockID = BTWBlocks.wheatCrop.blockID;
    			
    			int iRandomFactor = world.rand.nextInt( 4 ); 
    			
    			if ( iRandomFactor == 3 )
    			{
					secondaryCropBlockID = Block.potato.blockID;
    			}
    			else if ( iRandomFactor == 2 )
    			{
					secondaryCropBlockID = BTWBlocks.carrotCrop.blockID;
    			}
    			else
    			{
					secondaryCropBlockID = BTWBlocks.wheatCrop.blockID;
    			}
    		}
    	}
    }
    // END FCMOD
}
