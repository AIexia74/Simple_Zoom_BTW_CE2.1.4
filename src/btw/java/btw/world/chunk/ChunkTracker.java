// FCMOD: More generalized replacement for PlayerManager that keeps track of all 
// chunks around any loaders (players, wither, etc.) to decide which should be 
// loaded and communicated to players

package btw.world.chunk;

import net.minecraft.src.*;

import java.util.LinkedList;
import java.util.Iterator;

public class ChunkTracker
{
    public final WorldServer worldServer;

    private final LinkedList<EntityPlayerMP> playersTracked = new LinkedList<EntityPlayerMP>();

    public final LongHashMap trackerEntriesMap = new LongHashMap();

    private final LinkedList<ChunkTrackerEntry> entriesRequiringClientUpdate =
    	new LinkedList<ChunkTrackerEntry>();

    private final int chunkViewDistance;

    /** x, z direction vectors: east, south, west, north */
    private final int[][] offsetsXZ = new int[][] {
    	{ 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };

    public ChunkTracker(WorldServer world, int iChunkViewDistance )
    {
        if ( iChunkViewDistance > 15 )
        {
            throw new IllegalArgumentException( "Too big view radius!" );
        }
        else if ( iChunkViewDistance < 3 )
        {
            throw new IllegalArgumentException( "Too small view radius!" );
        }
        else
        {
            chunkViewDistance = iChunkViewDistance;
            worldServer = world;
        }
    }

    public void update()
    {
    	if ( !entriesRequiringClientUpdate.isEmpty() )
    	{
	    	Iterator<ChunkTrackerEntry> updateIterator =
	    		entriesRequiringClientUpdate.iterator();
	    	
	        while ( updateIterator.hasNext() )
	        {
	        	ChunkTrackerEntry tempEntry = updateIterator.next();
	        	
	            tempEntry.sendUpdatesToWatchingPlayers();
	        }
	
	        entriesRequiringClientUpdate.clear();
        }

    	// FCTODO: Broaden this to only unload worlds once all tracked chunks are gone
    	// not just players
    	//if ( m_trackerEntriesMap.getNumHashElements() <= 0 )
        if ( playersTracked.isEmpty() )
        {
            WorldProvider provider = worldServer.provider;

            if ( !provider.canRespawnHere() )
            {
                this.worldServer.theChunkProviderServer.unloadAllChunks();
            }
        }
    }

    public void flagBlockForClientUpdate(int i, int j, int k)
    {
        int iChunkX = i >> 4;
        int iChunkZ = k >> 4;
        
        ChunkTrackerEntry entry = getTrackerEntry(iChunkX, iChunkZ);

        if ( entry != null )
        {
        	if ( !entry.requiresClientUpdate() )
        	{
        		entriesRequiringClientUpdate.add(entry);
        	}
        		
    		entry.flagBlockForUpdate(i & 15, j, k & 15);
        }
    }

    public void addPlayer(EntityPlayerMP player)
    {
        int iPlayerChunkX = MathHelper.floor_double( player.posX / 16D );
        int iPlayerChunkZ = MathHelper.floor_double( player.posZ / 16D );
        
        player.managedPosX = player.posX;
        player.managedPosZ = player.posZ;

        for (int iTempChunkX = iPlayerChunkX - chunkViewDistance;
        	iTempChunkX <= iPlayerChunkX + chunkViewDistance; iTempChunkX++ )
        {
            for (int iTempChunkZ = iPlayerChunkZ - chunkViewDistance;
            	iTempChunkZ <= iPlayerChunkZ + chunkViewDistance; iTempChunkZ++ )
            {
                getOrCreateTrackerEntry(iTempChunkX, iTempChunkZ).addPlayerWatching(player);
            }
        }

        playersTracked.add(player);
        filterChunksToBeSentToClient(player);
    }

    public void updateMovingPlayer(EntityPlayerMP player)
    {
        double dDeltaManagedX = player.posX - player.managedPosX;
        double dDeltaManagedZ = player.posZ - player.managedPosZ;
        
        double dDistManagedSq = dDeltaManagedX * dDeltaManagedX + dDeltaManagedZ * dDeltaManagedZ;

    	// prevents jittery loading and unloading of chunks by requiring player to move
    	// a minimum distance before fully updating
    	
        if ( dDistManagedSq >= ( 8D * 8D ) )
    	{
	        int iPlayerChunkX = MathHelper.floor_double( player.posX / 16D );
	        int iPlayerChunkZ = MathHelper.floor_double( player.posZ / 16D );
	        
	        int iManagedChunkX = MathHelper.floor_double( player.managedPosX / 16D );
	        int iManagedChunkZ = MathHelper.floor_double( player.managedPosZ / 16D );
	            
	        if ( iManagedChunkX != iPlayerChunkX || iManagedChunkZ != iPlayerChunkZ )
	        {
	        	// cycle through all chunks around player's new location
	        	
	            for (int iTempChunkX = iPlayerChunkX - chunkViewDistance;
	            	iTempChunkX <= iPlayerChunkX + chunkViewDistance; iTempChunkX++ )
	            {
	                for (int iTempChunkZ = iPlayerChunkZ - chunkViewDistance;
	                	iTempChunkZ <= iPlayerChunkZ + chunkViewDistance; iTempChunkZ++ )
	                {
	                    if ( !areWithinAxisDistance(iTempChunkX, iTempChunkZ,
                                                    iManagedChunkX, iManagedChunkZ, chunkViewDistance) )
	                    {
	                    	// chunk isn't within previous managed zone
	                    	
	                    	ChunkTrackerEntry tempEntry = getOrCreateTrackerEntry(
	                    		iTempChunkX, iTempChunkZ);
	                    	
	                    	tempEntry.addPlayerWatching(player);
	                    }	                    
	                }
	            }
	            
	        	// cycle through all chunks around player's old managed location
	        	
	            for (int iTempChunkX = iManagedChunkX - chunkViewDistance;
            		iTempChunkX <= iManagedChunkX + chunkViewDistance; iTempChunkX++ )
	            {
	                for (int iTempChunkZ = iManagedChunkZ - chunkViewDistance;
	                	iTempChunkZ <= iManagedChunkZ + chunkViewDistance; iTempChunkZ++ )
                	{
	                    if ( !areWithinAxisDistance(iTempChunkX, iTempChunkZ,
                                                    iPlayerChunkX, iPlayerChunkZ, chunkViewDistance) )
	                    {
	                    	// chunk isn't within the player's new zone
	                    	
	                        ChunkTrackerEntry tempEntry = getTrackerEntry(
	                        	iTempChunkX, iTempChunkZ);
	
	                        if ( tempEntry != null)
	                        {
	                        	tempEntry.removePlayerWatching(player);
	                        }
	                    }
                	}
	            }
	
	            filterChunksToBeSentToClient(player);
	            
	            player.managedPosX = player.posX;
	            player.managedPosZ = player.posZ;
	        }
    	}
    }

    public void removeChunkFromTracker(ChunkTrackerEntry entry)
    {
    	ChunkCoordIntPair coord = entry.coord;
    	
        long lKey = computeTrackerEntryKey(coord.chunkXPos, coord.chunkZPos);
        
        trackerEntriesMap.remove(lKey);
        
        if ( entry.requiresClientUpdate() )
        {
        	entriesRequiringClientUpdate.remove(this);
        }

        worldServer.theChunkProviderServer.unloadChunksIfNotNearSpawn(
        	coord.chunkXPos, coord.chunkZPos);
    }

    // FCTODO: Old method, get rid of this once sure
    /*
    public void FilterChunksToBeSentToClient( EntityPlayerMP player )
    {
        LinkedList<ChunkCoordIntPair> oldChunksList = player.m_chunksToBeSentToClient;
        
        player.m_chunksToBeSentToClient = new LinkedList<ChunkCoordIntPair>();

        int iPlayerChunkX = MathHelper.floor_double( player.posX / 16D );
        int iPlayerChunkZ = MathHelper.floor_double( player.posZ / 16D );
        
        FCChunkTrackerEntry playerChunkEntry = GetOrCreateTrackerEntry( iPlayerChunkX, 
        	iPlayerChunkZ );        
        
        if ( oldChunksList.contains( playerChunkEntry.getChunkLocation() ) )
        {
            player.m_chunksToBeSentToClient.add( playerChunkEntry.getChunkLocation() );
        }

        int iRunningDir = 0;
        
        int iRunningXOffset = 0;
        int iRunningZOffset = 0;
        
        for ( int iTempDist = 1; iTempDist <= m_iChunkViewDistance * 2; iTempDist++ )
        {
            for ( int var11 = 0; var11 < 2; var11++ )
            {
                int[] iTempOffset = m_xzOffsets[iRunningDir % 4];
                
                iRunningDir++;

                for ( int var13 = 0; var13 < iTempDist; ++var13 )
                {
                    iRunningXOffset += iTempOffset[0];
                    iRunningZOffset += iTempOffset[1];
                    
                    FCChunkTrackerEntry tempEntry =
                    	GetOrCreateTrackerEntry( iPlayerChunkX + iRunningXOffset, 
                		iPlayerChunkZ + iRunningZOffset );

                    if ( oldChunksList.contains( tempEntry.getChunkLocation() ) )
                    {
                        player.m_chunksToBeSentToClient.add( tempEntry.getChunkLocation() );
                    }
                }
            }
        }

        iRunningDir %= 4;

        for ( int iTempDist = 0; iTempDist < m_iChunkViewDistance * 2; iTempDist++ )
        {
            iRunningXOffset += this.m_xzOffsets[iRunningDir][0];
            iRunningZOffset += this.m_xzOffsets[iRunningDir][1];
            
            FCChunkTrackerEntry tempEntry = 
            	GetOrCreateTrackerEntry( iPlayerChunkX + iRunningXOffset, iPlayerChunkZ + iRunningZOffset );

            if ( oldChunksList.contains( tempEntry.getChunkLocation() ) )
            {
                player.m_chunksToBeSentToClient.add( tempEntry.getChunkLocation() );
            }
        }
    }
    */
    // END FCTODO

    public void filterChunksToBeSentToClient(EntityPlayerMP player)
    {
        LinkedList<ChunkCoordIntPair> oldChunksList = player.chunksToBeSentToClient;
        
        player.chunksToBeSentToClient = new LinkedList<ChunkCoordIntPair>();

        int iPlayerChunkX = MathHelper.floor_double( player.posX / 16D );
        int iPlayerChunkZ = MathHelper.floor_double( player.posZ / 16D );
        
        ChunkTrackerEntry playerChunkEntry = getOrCreateTrackerEntry(iPlayerChunkX,
                                                                     iPlayerChunkZ);
        
        if ( oldChunksList.contains( playerChunkEntry.getChunkLocation() ) )
        {
            player.chunksToBeSentToClient.add(playerChunkEntry.getChunkLocation());
        }

        // goes through all the chunks in a spiral outwards from the player 
        // check if the chunk is waiting to be sent to the client, and ordering
        // the chunks that are so that closest are sent first.
        
        for (int iTempDist = 1; iTempDist <= chunkViewDistance; iTempDist++ )
        {
            int iRunningChunkX = iPlayerChunkX - iTempDist;
            int iRunningChunkZ = iPlayerChunkZ - iTempDist;
            
            for ( int iRunningSide = 0; iRunningSide < 4; iRunningSide++ )
            {
            	int iSideWidth = ( iTempDist * 2 ) + 1;
            	
            	int iSideOffsetX = offsetsXZ[iRunningSide][0];
            	int iSideOffsetZ = offsetsXZ[iRunningSide][1];
            	
            	for ( int iTempCount = 0; iTempCount < iSideWidth - 1; iTempCount++ )
            	{
                    ChunkTrackerEntry tempEntry = getOrCreateTrackerEntry(
                    	iRunningChunkX, iRunningChunkZ);

                    if ( oldChunksList.contains( tempEntry.getChunkLocation() ) )
                    {
                        player.chunksToBeSentToClient.add(tempEntry.getChunkLocation());
                    }
                    
                    iRunningChunkX += iSideOffsetX;
                    iRunningChunkZ += iSideOffsetZ;
            	}
            }            
        }
    }
    
    /**
     * Removes an EntityPlayerMP from the PlayerManager.
     */
    public void removePlayer(EntityPlayerMP player)
    {
        int iPlayerChunkX = MathHelper.floor_double( player.managedPosX / 16D );
        int iPlayerChunkZ = MathHelper.floor_double( player.managedPosZ / 16D );

        for (int iTempChunkX = iPlayerChunkX - chunkViewDistance;
        	iTempChunkX <= iPlayerChunkX + chunkViewDistance; iTempChunkX++ )
        {
            for (int iTempChunkZ = iPlayerChunkZ - chunkViewDistance;
            	iTempChunkZ <= iPlayerChunkZ + chunkViewDistance; iTempChunkZ++ )
            {
                ChunkTrackerEntry tempEntry = getTrackerEntry(iTempChunkX, iTempChunkZ);

                if ( tempEntry != null )
                {
                    tempEntry.removePlayerWatching(player);
                }
            }
        }

        playersTracked.remove(player);
    }

    public boolean isChunkWatchedByPlayerAndSentToClient(EntityPlayerMP player,
                                                         int iChunkX, int iChunkZ)
    {
    	ChunkTrackerEntry entry = getTrackerEntry(iChunkX, iChunkZ);
    	
    	if ( entry != null )
    	{
    		if ( entry.getPlayersInChunk().contains( player ) && 
    			!player.chunksToBeSentToClient.contains(entry.getChunkLocation()) )
			{
    			return true;
			}
    	}
    	
    	return false;
    }

    /**
     * Tests each axis to see if the two points are within specified distance on each axis.
     */
    private boolean areWithinAxisDistance(int iX1, int iZ1, int iX2, int iZ2, int iAxisDist)
    {
        int iDeltaX = iX1 - iX2;
        int iDeltaZ = iZ1 - iZ2;
        
        if ( iDeltaX >= -iAxisDist && iDeltaX <= iAxisDist )
        {
        	return iDeltaZ >= -iAxisDist && iDeltaZ <= iAxisDist;
        }
        
        return false;
    }

    public static int getFurthestViewableBlock(int iChunkViewDistance)
    {
        return iChunkViewDistance * 16 - 16;
    }
    
    public boolean isChunkBeingWatched(int iChunkX, int iChunkZ)
    {
    	return getTrackerEntry(iChunkX, iChunkZ) != null;
    }

    private long computeTrackerEntryKey(int iChunkX, int iChunkZ)
    {
        return (long)iChunkX + 2147483647L | (long)iChunkZ + 2147483647L << 32;        
    }
    
    private ChunkTrackerEntry getTrackerEntry(int iChunkX, int iChunkZ)
    {
        long lKey = computeTrackerEntryKey(iChunkX, iChunkZ);
        
        return (ChunkTrackerEntry) trackerEntriesMap.getValueByKey(lKey);
    }
    
    private ChunkTrackerEntry getOrCreateTrackerEntry(int iChunkX, int iChunkZ)
    {
        ChunkTrackerEntry entry = getTrackerEntry(iChunkX, iChunkZ);

        if ( entry == null )
        {
            entry = new ChunkTrackerEntry( this, iChunkX, iChunkZ );
            
            trackerEntriesMap.add(computeTrackerEntryKey(iChunkX, iChunkZ), entry);
        }

        return entry;
    }
}
