package net.minecraft.src;

import btw.block.BTWBlocks;
import btw.world.util.WorldUtils;

public class PathFinder
{
    /** Used to find obstacles */
    private IBlockAccess worldMap;

    /** The path being generated */
    private Path path = new Path();

    /** The points in the path */
    private IntHashMap pointMap = new IntHashMap();

    /** Selection of path points to add to the path */
    private PathPoint[] pathOptions = new PathPoint[32];

    /** should the PathFinder go through wodden door blocks */
    private boolean isWoddenDoorAllowed;

    /**
     * should the PathFinder disregard BlockMovement type materials in its path
     */
    private boolean isMovementBlockAllowed;
    private boolean isPathingInWater;

    /** tells the FathFinder to not stop pathing underwater */
    private boolean canEntityDrown;

    public PathFinder(IBlockAccess par1IBlockAccess, boolean par2, boolean par3, boolean par4, boolean par5)
    {
        this.worldMap = par1IBlockAccess;
        this.isWoddenDoorAllowed = par2;
        this.isMovementBlockAllowed = par3;
        this.isPathingInWater = par4;
        this.canEntityDrown = par5;
    }

    /**
     * Creates a path from one entity to another within a minimum distance
     */
    public PathEntity createEntityPathTo(Entity par1Entity, Entity par2Entity, float par3)
    {
        return this.createEntityPathTo(par1Entity, par2Entity.posX, par2Entity.boundingBox.minY, par2Entity.posZ, par3);
    }

    /**
     * Creates a path from an entity to a specified location within a minimum distance
     */
    public PathEntity createEntityPathTo(Entity par1Entity, int par2, int par3, int par4, float par5)
    {
        return this.createEntityPathTo(par1Entity, (double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), par5);
    }

    /**
     * Adds a path from start to end and returns the whole path (args: unused, start, end, unused, maxDistance)
     */
    private PathEntity addToPath(Entity par1Entity, PathPoint par2PathPoint, PathPoint par3PathPoint, PathPoint par4PathPoint, float par5)
    {
        par2PathPoint.totalPathDistance = 0.0F;
        par2PathPoint.distanceToNext = par2PathPoint.func_75832_b(par3PathPoint);
        par2PathPoint.distanceToTarget = par2PathPoint.distanceToNext;
        this.path.clearPath();
        this.path.addPoint(par2PathPoint);
        PathPoint var6 = par2PathPoint;

        while (!this.path.isPathEmpty())
        {
            PathPoint var7 = this.path.dequeue();

            if (var7.equals(par3PathPoint))
            {
                return this.createEntityPath(par2PathPoint, par3PathPoint);
            }

            if (var7.func_75832_b(par3PathPoint) < var6.func_75832_b(par3PathPoint))
            {
                var6 = var7;
            }

            var7.isFirst = true;
            int var8 = this.findPathOptions(par1Entity, var7, par4PathPoint, par3PathPoint, par5);

            for (int var9 = 0; var9 < var8; ++var9)
            {
                PathPoint var10 = this.pathOptions[var9];
                float var11 = var7.totalPathDistance + var7.func_75832_b(var10);

                if (!var10.isAssigned() || var11 < var10.totalPathDistance)
                {
                    var10.previous = var7;
                    var10.totalPathDistance = var11;
                    var10.distanceToNext = var10.func_75832_b(par3PathPoint);

                    if (var10.isAssigned())
                    {
                        this.path.changeDistance(var10, var10.totalPathDistance + var10.distanceToNext);
                    }
                    else
                    {
                        var10.distanceToTarget = var10.totalPathDistance + var10.distanceToNext;
                        this.path.addPoint(var10);
                    }
                }
            }
        }

        if (var6 == par2PathPoint)
        {
            return null;
        }
        else
        {
            return this.createEntityPath(par2PathPoint, var6);
        }
    }

    /**
     * populates pathOptions with available points and returns the number of options found (args: unused1, currentPoint,
     * unused2, targetPoint, maxDistance)
     */
    private int findPathOptions(Entity par1Entity, PathPoint par2PathPoint, PathPoint par3PathPoint, PathPoint par4PathPoint, float par5)
    {
        int var6 = 0;
        byte var7 = 0;

        if (this.getVerticalOffset(par1Entity, par2PathPoint.xCoord, par2PathPoint.yCoord + 1, par2PathPoint.zCoord, par3PathPoint) == 1)
        {
            var7 = 1;
        }

        PathPoint var8 = this.getSafePoint(par1Entity, par2PathPoint.xCoord, par2PathPoint.yCoord, par2PathPoint.zCoord + 1, par3PathPoint, var7);
        PathPoint var9 = this.getSafePoint(par1Entity, par2PathPoint.xCoord - 1, par2PathPoint.yCoord, par2PathPoint.zCoord, par3PathPoint, var7);
        PathPoint var10 = this.getSafePoint(par1Entity, par2PathPoint.xCoord + 1, par2PathPoint.yCoord, par2PathPoint.zCoord, par3PathPoint, var7);
        PathPoint var11 = this.getSafePoint(par1Entity, par2PathPoint.xCoord, par2PathPoint.yCoord, par2PathPoint.zCoord - 1, par3PathPoint, var7);

        if (var8 != null && !var8.isFirst && var8.distanceTo(par4PathPoint) < par5)
        {
            this.pathOptions[var6++] = var8;
        }

        if (var9 != null && !var9.isFirst && var9.distanceTo(par4PathPoint) < par5)
        {
            this.pathOptions[var6++] = var9;
        }

        if (var10 != null && !var10.isFirst && var10.distanceTo(par4PathPoint) < par5)
        {
            this.pathOptions[var6++] = var10;
        }

        if (var11 != null && !var11.isFirst && var11.distanceTo(par4PathPoint) < par5)
        {
            this.pathOptions[var6++] = var11;
        }

        return var6;
    }

    /**
     * Returns a point that the entity can safely move to
     */
    private PathPoint getSafePoint(Entity par1Entity, int par2, int par3, int par4, PathPoint par5PathPoint, int par6)
    {
        PathPoint var7 = null;
        int var8 = this.getVerticalOffset(par1Entity, par2, par3, par4, par5PathPoint);

        if (var8 == 2)
        {
            return this.openPoint(par2, par3, par4);
        }
        else
        {
            if (var8 == 1)
            {
                var7 = this.openPoint(par2, par3, par4);
            }

            if (var7 == null && par6 > 0 && var8 != -3 && var8 != -4 && this.getVerticalOffset(par1Entity, par2, par3 + par6, par4, par5PathPoint) == 1)
            {
                var7 = this.openPoint(par2, par3 + par6, par4);
                par3 += par6;
            }

            if (var7 != null)
            {
                int var9 = 0;
                int var10 = 0;

                while (par3 > 0)
                {
                    var10 = this.getVerticalOffset(par1Entity, par2, par3 - 1, par4, par5PathPoint);

                    if (this.isPathingInWater && var10 == -1)
                    {
                        return null;
                    }

                    if (var10 != 1)
                    {
                        break;
                    }

                    if (var9++ >= par1Entity.func_82143_as())
                    {
                        return null;
                    }

                    --par3;

                    if (par3 > 0)
                    {
                        var7 = this.openPoint(par2, par3, par4);
                    }
                }

                if (var10 == -2)
                {
                    return null;
                }
            }

            return var7;
        }
    }

    /**
     * Returns a mapped point or creates and adds one
     */
    private final PathPoint openPoint(int par1, int par2, int par3)
    {
        int var4 = PathPoint.makeHash(par1, par2, par3);
        PathPoint var5 = (PathPoint)this.pointMap.lookup(var4);

        if (var5 == null)
        {
            var5 = new PathPoint(par1, par2, par3);
            this.pointMap.addKey(var4, var5);
        }

        return var5;
    }

    /**
     * Checks if an entity collides with blocks at a position. Returns 1 if clear, 0 for colliding with any solid block,
     * -1 for water(if avoiding water) but otherwise clear, -2 for lava, -3 for fence, -4 for closed trapdoor, 2 if
     * otherwise clear except for open trapdoor or water(if not avoiding)
     */
    public int getVerticalOffset(Entity par1Entity, int par2, int par3, int par4, PathPoint par5PathPoint)
    {
    	int pathWeight = getPathWeightAtLocation(par1Entity, par2, par3, par4, par5PathPoint);
    	
    	// Stop babies from pathing up solid blocks
    	if (pathWeight == 0 
    			&& par1Entity instanceof EntityAnimal 
    			&& ((EntityAnimal)par1Entity).getGrowingAge() < 0
    			&& WorldUtils.doesBlockHaveSmallCenterHardpointToFacing(par1Entity.worldObj, par2, par3, par4, 1)) {
    		pathWeight = -3;
    	}
    	
    	return pathWeight;
    }

    public static int func_82565_a(Entity par0Entity, int par1, int par2, int par3, PathPoint par4PathPoint, boolean par5, boolean par6, boolean par7)
    {
    	// FCNOTE: *Mostly* dead code.  The mod prevents it being called above, but it's still called from EntityAIControlledByPlayer 
        boolean var8 = false;

        for (int var9 = par1; var9 < par1 + par4PathPoint.xCoord; ++var9)
        {
            for (int var10 = par2; var10 < par2 + par4PathPoint.yCoord; ++var10)
            {
                for (int var11 = par3; var11 < par3 + par4PathPoint.zCoord; ++var11)
                {
                    int var12 = par0Entity.worldObj.getBlockId(var9, var10, var11);

                    if (var12 > 0)
                    {
                        if (var12 == Block.trapdoor.blockID)
                        {
                            var8 = true;
                        }
                        else if (var12 != Block.waterMoving.blockID && var12 != Block.waterStill.blockID)
                        {
                            if (!par7 && ( var12 == Block.doorWood.blockID || var12 == BTWBlocks.woodenDoor.blockID ) )
                            {
                                return 0;
                            }
                        }
                        else
                        {
                            if (par5)
                            {
                                return -1;
                            }

                            var8 = true;
                        }

                        Block var13 = Block.blocksList[var12];
                        int var14 = var13.getRenderType();

                        // FCNOTE: 9 is the render type of BlockRailBase
                        if (par0Entity.worldObj.blockGetRenderType(var9, var10, var11) == 9)
                        {
                            int var18 = MathHelper.floor_double(par0Entity.posX);
                            int var16 = MathHelper.floor_double(par0Entity.posY);
                            int var17 = MathHelper.floor_double(par0Entity.posZ);

                            if (par0Entity.worldObj.blockGetRenderType(var18, var16, var17) != 9 && par0Entity.worldObj.blockGetRenderType(var18, var16 - 1, var17) != 9)
                            {
                                return -3;
                            }
                        }
                        else if (!var13.getBlocksMovement(par0Entity.worldObj, var9, var10, var11) && (!par6 || var12 != Block.doorWood.blockID))
                        {
                        	// FCNOTE: 11 is the render type of BlockFence, 32 is that of BlockWall
                            if (var14 == 11 || var12 == Block.fenceGate.blockID || var14 == 32)
                            {
                                return -3;
                            }

                            if (var12 == Block.trapdoor.blockID)
                            {
                                return -4;
                            }

                            Material var15 = var13.blockMaterial;

                            if (var15 != Material.lava)
                            {
                                return 0;
                            }

                            if (!par0Entity.handleLavaMovement())
                            {
                                return -2;
                            }
                        }
                    }
                }
            }
        }

        return var8 ? 2 : 1;
    }

    /**
     * Returns a new PathEntity for a given start and end point
     */
    private PathEntity createEntityPath(PathPoint par1PathPoint, PathPoint par2PathPoint)
    {
        int var3 = 1;
        PathPoint var4;

        for (var4 = par2PathPoint; var4.previous != null; var4 = var4.previous)
        {
            ++var3;
        }

        PathPoint[] var5 = new PathPoint[var3];
        var4 = par2PathPoint;
        --var3;

        for (var5[var3] = par2PathPoint; var4.previous != null; var5[var3] = var4)
        {
            var4 = var4.previous;
            --var3;
        }

        return new PathEntity(var5);
    }
    
    // FCMOD: Added New
    public int getPathWeightAtLocation(Entity entity, int i, int j, int k, PathPoint pathPoint)
    {
    	boolean bAvoidsWater = isPathingInWater;
    	boolean bPathThroughClosedWoodDoor = isMovementBlockAllowed;
    	boolean bPathThroughOpenWoodDoor = isWoddenDoorAllowed;
    	
    	// this is pathfinding's inner loop, and has been rewritten to optimize
    	
    	World world = entity.worldObj;
        int iReturnWeight = 1;
        
        int iMaxI = i + pathPoint.xCoord;
        int iMaxJ = j + pathPoint.yCoord;
        int iMaxK = k + pathPoint.zCoord;

        for ( int iTempI = i; iTempI < iMaxI; ++iTempI )
        {
            for ( int iTempJ = j; iTempJ < iMaxJ; ++iTempJ )
            {
                for ( int iTempK = k; iTempK < iMaxK; ++iTempK )
                {
                    int iTempBlockID = world.getBlockId( iTempI, iTempJ, iTempK );

                    if ( iTempBlockID > 0 )
                    {
                        Block tempBlock = Block.blocksList[iTempBlockID];

                        if ( !tempBlock.canPathThroughBlock(world, iTempI, iTempJ, iTempK, entity, this) )
                        {                        	
                        	return tempBlock.getWeightOnPathBlocked(world, iTempI, iTempJ, iTempK);
                        }
                        else
                        {
                        	iReturnWeight = tempBlock.adjustPathWeightOnNotBlocked(iReturnWeight);
                        }
                    }
                }
            }
        }

        return iReturnWeight;
    }
    
    public boolean CanPathThroughClosedWoodDoor()
    {
    	return isMovementBlockAllowed;
    }
    
    public boolean canPathThroughOpenWoodDoor()
    {
    	return isWoddenDoorAllowed;
    }
    
    public boolean canPathThroughWater()
    {
    	return !isPathingInWater; // member variable is misnamed
    }
    
    private PathEntity createEntityPathTo( Entity entity, double dDestX, double dDestY, 
    	double dDestZ, float fTolerance )
    {
        path.clearPath();
        pointMap.clearMap();
        
        boolean bTempPathingInWater = isPathingInWater; // not sure what's up with how this is used
        
        int iIStart = MathHelper.floor_double( entity.boundingBox.minX );
        int iJStart = MathHelper.floor_double( entity.boundingBox.minY + 0.5D );
        int iKStart = MathHelper.floor_double( entity.boundingBox.minZ );
        
        if ( canEntityDrown && entity.isInWater() )
        {
            iJStart = (int)entity.boundingBox.minY;

            for ( int iTempBlockID = worldMap.getBlockId( MathHelper.floor_double( entity.posX ), 
            	iJStart, MathHelper.floor_double( entity.posZ ) ); 
            	iTempBlockID == Block.waterMoving.blockID || 
            	iTempBlockID == Block.waterStill.blockID; 
            	iTempBlockID = worldMap.getBlockId( MathHelper.floor_double( entity.posX ), 
        		iJStart, MathHelper.floor_double( entity.posZ ) ) )
            {
                ++iJStart;
            }

            bTempPathingInWater = isPathingInWater;
            
            isPathingInWater = false;
        }        
        else if ( shouldOffsetPositionIfPathingOutOfBlock(iIStart, iJStart, iKStart, entity) )
		{
    		// smaller entities can get stuck on fence corners and such.  This bit
        	// offsets the start point for pathing to open neighboring blocks.
        	// Intentionally uses horizontal center of entity to determine offset.

    		double dPartialX = entity.posX - (double)iIStart;
    		
    		int iIOffset = iIStart + ( dPartialX < 0.5 ? -1: 1 );

        	if ( !canPathThroughBlock(iIOffset, iJStart, iKStart, entity) )
    		{
        		double dPartialZ = entity.posZ - (double)iKStart;	            		
        		
        		int iKOffset = iKStart + ( dPartialZ < 0.5 ? -1: 1 );
        		
            	if ( !canPathThroughBlock(iIStart, iJStart, iKOffset, entity) )
        		{
            		if ( canPathThroughBlock(iIOffset, iJStart, iKOffset, entity) )
            		{
                		iIStart = iIOffset;
                		iKStart = iKOffset;
            		}
        		}
            	else
            	{
            		iKStart = iKOffset;
            	}
    		}
        	else
        	{
        		iIStart = iIOffset;
        	}
        }

        PathPoint startPoint = openPoint( iIStart, iJStart, iKStart ); 
        
        PathPoint endPoint = openPoint( 
        	MathHelper.floor_double( dDestX - ( entity.width / 2F ) ), 
        	MathHelper.floor_double( dDestY ), 
        	MathHelper.floor_double( dDestZ - ( entity.width / 2F ) ) );
        
        PathPoint spaceNeeded = new PathPoint( MathHelper.floor_float( entity.width + 1F ), 
        	MathHelper.floor_float( entity.height + 1F ), 
        	MathHelper.floor_float( entity.width + 1F ) );
        
        PathEntity path = addToPath( entity, startPoint, endPoint, spaceNeeded, fTolerance );
        
        isPathingInWater = bTempPathingInWater;
        
        return path;
    }
    
    private boolean shouldOffsetPositionIfPathingOutOfBlock(int i, int j, int k, Entity entity)
    {
        int iStartBlockID = worldMap.getBlockId( i, j, k );

        if ( iStartBlockID > 0 )
        {
	        Block startBlock = Block.blocksList[iStartBlockID];
	    	
	        return startBlock.shouldOffsetPositionIfPathingOutOf(worldMap, i, j, k, entity, this);
        }
        
        return false;
    }
    
    private boolean canPathThroughBlock(int i, int j, int k, Entity entity)
    {
        int iStartBlockID = worldMap.getBlockId( i, j, k );

        if ( iStartBlockID > 0 )
        {
	        Block startBlock = Block.blocksList[iStartBlockID];
	    	
	        return startBlock.canPathThroughBlock(worldMap, i, j, k, entity, this);
        }
        
        return true;
    }
    // END FCMOD
}
