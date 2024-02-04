// FCMOD: More generalized replacement for PlayerInstance

package btw.world.chunk;

import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.List;

class ChunkTrackerEntry
{
    private final ChunkTracker chunkTracker;

    public final ChunkCoordIntPair coord;
    
    private final List<EntityPlayerMP> playersWatching = new ArrayList<EntityPlayerMP>();

    // Tracks blocks that need to be sent to players watching the chunk.
    // 16 bit encoded coordinates with top 4 bits x, mid 4 bits z, and bottom 8 bits y.       
    
    private short[] locationsRequiringClientUpdate = new short[64];
    private int numLocationsRequiringClientUpdate = 0; // a running count of active elements in above
    
    // bitfield that flags each vertical chunk of 16 blocks that needs to be sent to players
    
    private int verticalChunksToUpdatePlayersBitfield;

    public ChunkTrackerEntry(ChunkTracker chunkTracker, int iChunkX, int iChunkZ )
    {
        this.chunkTracker = chunkTracker;

        coord = new ChunkCoordIntPair(iChunkX, iChunkZ );
        
        chunkTracker.worldServer.theChunkProviderServer.loadChunk(iChunkX, iChunkZ);
    }

    public void addPlayerWatching(EntityPlayerMP player)
    {
        if ( playersWatching.contains(player) )
        {
            throw new IllegalStateException("Failed to add player. " + player +
                                            " already is in chunk " + coord.chunkXPos + ", " + coord.chunkZPos );
        }
        else
        {
            playersWatching.add(player);
            
            player.chunksToBeSentToClient.add(coord);
        }
    }
    
    public boolean requiresClientUpdate()
    {
    	return numLocationsRequiringClientUpdate > 0;
    }

    public void removePlayerWatching(EntityPlayerMP player)
    {
        if ( playersWatching.contains(player) )
        {
            player.playerNetServerHandler.sendPacket( new Packet51MapChunk(
                    chunkTracker.worldServer.getChunkFromChunkCoords(
                            coord.chunkXPos, coord.chunkZPos), true, 0 ));
            
            playersWatching.remove(player);
            player.chunksToBeSentToClient.remove(coord);

        	// FCTODO: Broaden this to only unload chunks once all watchers are gone
            if ( playersWatching.isEmpty() )
            {
            	chunkTracker.removeChunkFromTracker(this);
            }
        }
    }

    private short getBitEncodingForLocalPos(int iLocalX, int iLocalY, int iLocalZ)
    {
    	return (short)( iLocalX << 12 | iLocalZ << 8 | iLocalY );
    }
    
    public void flagBlockForUpdate(int iLocalX, int iLocalY, int iLocalZ)
    {
        verticalChunksToUpdatePlayersBitfield |= 1 << (iLocalY >> 4 );

        if (numLocationsRequiringClientUpdate < 64 )
        {
            short sBitCode = getBitEncodingForLocalPos(iLocalX, iLocalY, iLocalZ);

            for (int iTempIndex = 0; iTempIndex < numLocationsRequiringClientUpdate; iTempIndex++ )
            {
                if (locationsRequiringClientUpdate[iTempIndex] == sBitCode )
                {
                    return;
                }
            }

            locationsRequiringClientUpdate[numLocationsRequiringClientUpdate++] = sBitCode;
        }
    }

    private void sendToPlayersWatchingNotWaitingFullChunk(Packet packet)
    {
        for (int iTempCount = 0; iTempCount < playersWatching.size(); iTempCount++ )
        {
            EntityPlayerMP tempPlayer = (EntityPlayerMP) playersWatching.get(iTempCount);

            // don't send if the whole chunk is already going to be sent to the player
            
            if ( !tempPlayer.chunksToBeSentToClient.contains(coord) )
            {
                tempPlayer.playerNetServerHandler.sendPacket(packet);
            }
        }
    }

    public void sendUpdatesToWatchingPlayers()
    {
        if (numLocationsRequiringClientUpdate != 0 )
        {
            int iOffsetX = coord.chunkXPos * 16;
            int iOffsetZ = coord.chunkZPos * 16;
            
            if (numLocationsRequiringClientUpdate == 1 )
            {
                int iBlockX = iOffsetX + (locationsRequiringClientUpdate[0] >> 12 & 15 );
                int iBlockY = locationsRequiringClientUpdate[0] & 255;
                int iBlockZ = iOffsetZ + (locationsRequiringClientUpdate[0] >> 8 & 15 );
                
                sendToPlayersWatchingNotWaitingFullChunk(new Packet53BlockChange(
                        iBlockX, iBlockY, iBlockZ, chunkTracker.worldServer));

                if ( chunkTracker.worldServer.blockHasTileEntity(
                	iBlockX, iBlockY, iBlockZ) )
                {
                    sendTileEntityToPlayersWatchingChunk(
                            chunkTracker.worldServer.getBlockTileEntity(
                		iBlockX, iBlockY, iBlockZ));
                }
            }
            else if (numLocationsRequiringClientUpdate == 64 )
            {
            	// if the number of updates has hit maximum capacity, send the entire 
            	// modified vertical chunks to players in one combined packet instead of 
            	// individual blocks
            	
                sendToPlayersWatchingNotWaitingFullChunk(new Packet51MapChunk(
                        chunkTracker.worldServer.getChunkFromChunkCoords(
                                coord.chunkXPos, coord.chunkZPos), false, verticalChunksToUpdatePlayersBitfield));

                for ( int iTempVerticalChunk = 0; iTempVerticalChunk < 16; iTempVerticalChunk++ )
                {
                    if ((verticalChunksToUpdatePlayersBitfield & 1 << iTempVerticalChunk ) !=
                        0 )
                    {
                        int iTempY = iTempVerticalChunk << 4;
                        
                        List<TileEntity> tempTileEntities = 
                        	chunkTracker.worldServer.getAllTileEntityInBox(
                        	iOffsetX, iTempY, iOffsetZ, iOffsetX + 16, iTempY + 16, iOffsetZ + 16);

                        for ( int iTempCount = 0; iTempCount < tempTileEntities.size(); 
                        	iTempCount++ )
                        {
                            sendTileEntityToPlayersWatchingChunk(
                            	tempTileEntities.get( iTempCount ));
                        }
                    }
                }
            }
            else
            {
                sendToPlayersWatchingNotWaitingFullChunk(new Packet52MultiBlockChange(
                        coord.chunkXPos, coord.chunkZPos, locationsRequiringClientUpdate, numLocationsRequiringClientUpdate, chunkTracker.worldServer));

                for (int iTempIndex = 0; iTempIndex < numLocationsRequiringClientUpdate; iTempIndex++ )
                {
                    int iBlockX = iOffsetX + (locationsRequiringClientUpdate[iTempIndex] >> 12 & 15 );
                    int iBlockY = locationsRequiringClientUpdate[iTempIndex] & 255;
                    int iBlockZ = iOffsetZ + (locationsRequiringClientUpdate[iTempIndex] >> 8 & 15 );

                    if ( chunkTracker.worldServer.blockHasTileEntity(
                    	iBlockX, iBlockY, iBlockZ) )
                    {
                        sendTileEntityToPlayersWatchingChunk(
                                chunkTracker.worldServer.getBlockTileEntity(
                        		iBlockX, iBlockY, iBlockZ));
                    }
                }
            }

            numLocationsRequiringClientUpdate = 0;
            verticalChunksToUpdatePlayersBitfield = 0;
        }
    }

    private void sendTileEntityToPlayersWatchingChunk(TileEntity tileEntity)
    {
        Packet packet = tileEntity.getDescriptionPacket();

        if ( packet != null )
        {
            sendToPlayersWatchingNotWaitingFullChunk(packet);
        }
    }

    public ChunkCoordIntPair getChunkLocation()
    {
        return coord;
    }

    public List getPlayersInChunk()
    {
        return playersWatching;
    }
}
