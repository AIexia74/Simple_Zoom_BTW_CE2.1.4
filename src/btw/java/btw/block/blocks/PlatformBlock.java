// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.client.render.util.RenderUtils;
import btw.entity.mechanical.platform.BlockLiftedByPlatformEntity;
import btw.entity.mechanical.platform.MovingAnchorEntity;
import btw.entity.mechanical.platform.MovingPlatformEntity;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class PlatformBlock extends Block
{
	private boolean platformAlreadyConsideredForEntityConversion[][][];
	private boolean platformAlreadyConsideredForConnectedTest[][][];
	
	public PlatformBlock(int iBlockID)
	{
        super( iBlockID, Material.wood );

        setHardness( 2F );
        setAxesEffectiveOn();
        
        setBuoyancy(1F);
        
		setFireProperties(Flammability.WICKER);
		
        setStepSound( soundWoodFootstep );
        
        setUnlocalizedName( "fcBlockPlatform" );

		platformAlreadyConsideredForEntityConversion = new boolean[5][5][5];
		platformAlreadyConsideredForConnectedTest = new boolean[5][5][5];
        
        resetPlatformConsideredForEntityConversionArray();
        resetPlatformConsideredForConnectedTestArray();
        
        setCreativeTab( CreativeTabs.tabTransport );
    }	
    
	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }

	@Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
    @Override
	public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
    	return true;
	}
    
    @Override
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
    	return iFacing <= 1;
	}
    
	@Override
	public boolean canRotateOnTurntable(IBlockAccess iBlockAccess, int i, int j, int k)
	{
		return true;
	}
	
	@Override
	public boolean canTransmitRotationHorizontallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
	@Override
	public boolean canTransmitRotationVerticallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{
		return true;
	}
	
	@Override
    public boolean isNormalCube(IBlockAccess blockAccess, int i, int j, int k)
    {
		// so that torches can be placed on its sides and such
		
		return true;
    }
    
    //------------- Class Specific Methods ------------//
    
    private void convertToEntity(World world, int i, int j, int k, MovingAnchorEntity associatedAnchorEntity)
    {
		MovingPlatformEntity entityPlatform = (MovingPlatformEntity) EntityList.createEntityOfType(MovingPlatformEntity.class, world,
	    		(float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, 
	    		associatedAnchorEntity ); 
				
        world.spawnEntityInWorld( entityPlatform );
        
        // convert any suitable blocks above to entities as well
        
        attemptToLiftBlockWithPlatform(world, i, j + 1, k);
        
        world.setBlockWithNotify( i, j, k, 0 );        
    }
    
    private void attemptToLiftBlockWithPlatform(World world, int i, int j, int k)
    {
        if ( BlockLiftedByPlatformEntity.canBlockBeConvertedToEntity(world, i, j, k) )
        {
        	EntityList.createEntityOfType(BlockLiftedByPlatformEntity.class, world, i, j, k );
        }
    }
    
    private int getDistToClosestConnectedAnchorPoint(World world, int i, int j, int k)
    {
    	// returns -1 if no anchor point found within range
    	
    	int iClosestDist = -1;
    	
    	for ( int tempI = i - 2; tempI <= i + 2; tempI++ )
    	{
        	for ( int tempJ = j - 2; tempJ <= j + 2; tempJ++ )
        	{
            	for ( int tempK = k - 2; tempK <= k + 2; tempK++ )
            	{
            		int iTempBlockID = world.getBlockId( tempI, tempJ, tempK );
            		
            		if ( iTempBlockID == blockID )
            		{
            			// this is another platform, check if it has an attached anchor
            			
            			int iUpwardsBlockID = world.getBlockId( tempI, tempJ + 1, tempK );
            			
            			if ( iUpwardsBlockID == BTWBlocks.anchor.blockID )
            			{
            				if (((AnchorBlock)(BTWBlocks.anchor)).getFacing(world, tempI, tempJ + 1, tempK) == 1 )
            				{
            					if ( isPlatformConnectedToAnchorPoint(world, i, j, k, tempI, tempJ, tempK) )
            					{            							
	            					int iTempDist = Math.abs( tempI - i ) + Math.abs( tempJ - j )  +  
	            						Math.abs( tempK - k );
	            					
	            					if ( iClosestDist == -1 || iTempDist < iClosestDist )
	            					{
	            						iClosestDist = iTempDist;
	            					}
            					}
            				}
            			}
            		}            		
            	}
        	}
    	}
    	
    	return iClosestDist;
    }
    
    private boolean isPlatformConnectedToAnchorPoint(World world,
													 int platformI, int platformJ, int platformK,
													 int anchorPointI, int anchorPointJ, int anchorPointK)
    {
    	resetPlatformConsideredForConnectedTestArray();
    	
    	if ( platformI == anchorPointI &&
			platformJ == anchorPointJ &&
			platformK == anchorPointK )
    	{
    		return true;    		
    	}
    	
    	return propogateTestForConnected(world,
										 anchorPointI, anchorPointJ, anchorPointK,
										 anchorPointI, anchorPointJ, anchorPointK,
										 platformI, platformJ, platformK);
    }    
    
    private boolean propogateTestForConnected(World world, int i, int j, int k,
											  int sourceI, int sourceJ, int sourceK,
											  int targetI, int targetJ, int targetK)
    {
    	int iDeltaI = i - sourceI;
    	int iDeltaJ = j - sourceJ;
    	int iDeltaK = k - sourceK;
    	
		if ( platformAlreadyConsideredForConnectedTest[iDeltaI + 2][iDeltaJ + 2][iDeltaK + 2] )
		{
			return false;
		}

		platformAlreadyConsideredForConnectedTest[iDeltaI + 2][iDeltaJ + 2][iDeltaK + 2] = true;
		
    	for ( int iFacing = 0; iFacing < 6; iFacing++ )
    	{
    		BlockPos tempPos = new BlockPos( i, j, k );
    		
    		tempPos.addFacingAsOffset(iFacing);
    		
    		if (tempPos.x == targetI &&
				tempPos.y == targetJ &&
				tempPos.z == targetK )
    		{
    			return true;
    		}
    		
    		int iTempBlockID = world.getBlockId(tempPos.x, tempPos.y, tempPos.z);
    		
    		if ( iTempBlockID == blockID )
    		{
	    		int tempDistI = Math.abs( sourceI - tempPos.x);
	    		int tempDistJ = Math.abs( sourceJ - tempPos.y);
	    		int tempDistK = Math.abs( sourceK - tempPos.z);
	    		
	    		if ( tempDistI <= 2 && tempDistJ <= 2 && tempDistK <= 2  )
	    		{
	    			if ( propogateTestForConnected(world, tempPos.x, tempPos.y, tempPos.z,
												   sourceI, sourceJ, sourceK, targetI, targetJ, targetK) )
	    			{
	    				return true;
	    			}
    			}
    		}
    	}
    	
    	return false;
    }
    
    void resetPlatformConsideredForEntityConversionArray()
    {
    	for ( int tempI = 0; tempI < 5; tempI++ )
    	{
        	for ( int tempJ = 0; tempJ < 5; tempJ++ )
        	{
            	for ( int tempK = 0; tempK < 5; tempK++ )
            	{
					platformAlreadyConsideredForEntityConversion[tempI][tempJ][tempK] = false;
            	}
        	}
    	}
    }
    
    void resetPlatformConsideredForConnectedTestArray()
    {
    	for ( int tempI = 0; tempI < 5; tempI++ )
    	{
        	for ( int tempJ = 0; tempJ < 5; tempJ++ )
        	{
            	for ( int tempK = 0; tempK < 5; tempK++ )
            	{
					platformAlreadyConsideredForConnectedTest[tempI][tempJ][tempK] = false;
            	}
        	}
    	}
    }
    
    public void covertToEntitiesFromThisPlatform(World world, int i, int j, int k,
												 MovingAnchorEntity associatedAnchorEntity)
    {
    	resetPlatformConsideredForEntityConversionArray();
    	
    	propagateCovertToEntity(world, i, j, k, associatedAnchorEntity, i, j, k);
    }
    
    private void propagateCovertToEntity(World world, int i, int j, int k,
										 MovingAnchorEntity associatedAnchorEntity, int sourceI, int sourceJ, int sourceK)
    {
    	int iDeltaI = i - sourceI;
    	int iDeltaJ = j - sourceJ;
    	int iDeltaK = k - sourceK;
    	
		if ( platformAlreadyConsideredForEntityConversion[iDeltaI + 2][iDeltaJ + 2][iDeltaK + 2] )
		{
			return;
		}

		platformAlreadyConsideredForEntityConversion[iDeltaI + 2][iDeltaJ + 2][iDeltaK + 2] = true;
		
    	int distToSource = Math.abs( iDeltaI ) + Math.abs( iDeltaJ )  +  Math.abs( iDeltaK );
    	
    	int closestAnchorDist = getDistToClosestConnectedAnchorPoint(world, i, j, k);

    	if ( closestAnchorDist == -1 || distToSource <= closestAnchorDist )
    	{
    		convertToEntity(world, i, j, k, associatedAnchorEntity);
    	}
    	else
    	{
    		return;
    	}
    	
    	for ( int iFacing = 0; iFacing < 6; iFacing++ )
    	{
    		BlockPos tempPos = new BlockPos( i, j, k );
    		
    		tempPos.addFacingAsOffset(iFacing);
    		
    		int iTempBlockID = world.getBlockId(tempPos.x, tempPos.y, tempPos.z);
    		
    		if ( iTempBlockID == blockID )
    		{
	    		int tempDistI = Math.abs( sourceI - tempPos.x);
	    		int tempDistJ = Math.abs( sourceJ - tempPos.y);
	    		int tempDistK = Math.abs( sourceK - tempPos.z);
	    		
	    		if ( tempDistI <= 2 && tempDistJ <= 2 && tempDistK <= 2  )
	    		{
	    			propagateCovertToEntity(world, tempPos.x, tempPos.y, tempPos.z,
											associatedAnchorEntity, sourceI, sourceJ, sourceK);
    			}
    		}
    	}
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconBySideArray = new Icon[6];

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        Icon topIcon = register.registerIcon( "fcBlockPlatform_top" );
        
        blockIcon = topIcon; // for hit effects

		iconBySideArray[0] = register.registerIcon("fcBlockPlatform_bottom");
		iconBySideArray[1] = topIcon;
        
        Icon sideIcon = register.registerIcon( "fcBlockPlatform_side" );

		iconBySideArray[2] = sideIcon;
		iconBySideArray[3] = sideIcon;
		iconBySideArray[4] = sideIcon;
		iconBySideArray[5] = sideIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		return iconBySideArray[iSide];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int i, int j, int k)
    {	
    	IBlockAccess blockAccess = renderBlocks.blockAccess;
    	
    	// render sides

    	if ( blockAccess.getBlockId( i - 1, j, k ) != blockID )
    	{
	        renderBlocks.setRenderBounds( 0.0001F, ( 1F / 16F ), 0.0001F, 
	        		( 1F / 16F ), 1F - ( 1F / 16F ), 0.9999F );
	        
	        renderBlocks.renderStandardBlock( this, i, j, k );	        
    	}        
        
    	if ( blockAccess.getBlockId( i, j, k + 1 ) != blockID )
    	{
	        renderBlocks.setRenderBounds( 0.0F, ( 1F / 16F ), 1F - ( 1F / 16F ), 
	        		1F, 1F - ( 1F / 16F ), 1F );
	        
	        renderBlocks.renderStandardBlock( this, i, j, k );
	        
    	}
        
    	if ( blockAccess.getBlockId( i + 1, j, k ) != blockID )
    	{
	        renderBlocks.setRenderBounds( 1F - ( 1F / 16F ), ( 1F / 16F ), 0.0001F, 
		    		0.9999F, 1F - ( 1F / 16F ), 0.9999F );
	        
	        renderBlocks.renderStandardBlock( this, i, j, k );
	        
    	}
        
    	if ( blockAccess.getBlockId( i, j, k - 1 ) != blockID )
    	{
	        renderBlocks.setRenderBounds( 0, ( 1F / 16F ), 0F, 
		    		1.0F, 1F - ( 1F / 16F ), ( 1F / 16F ) );
	        
	        renderBlocks.renderStandardBlock( this, i, j, k );	        
    	}
        
        // render bottom
        
        renderBlocks.setRenderBounds( 0, 0, 0, 
	    		1, ( 1F / 16F ), 1 );
        
        renderBlocks.renderStandardBlock( this, i, j, k );
        
        // render top
        
        renderBlocks.setRenderBounds( 0, 1F - ( 1F / 16F ), 0, 
	    		1, 1, 1 );
        
        renderBlocks.renderStandardBlock( this, i, j, k );
        
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
    	// render sides
    	
    	Icon sideTexture = iconBySideArray[2];
    	  
        renderBlocks.setRenderBounds( 0.00001F, 0.00001F, 0.00001F, 
        		( 1F / 16F ), 0.99999F, 0.99999F );
        
        RenderUtils.renderInvBlockWithTexture(renderBlocks, this, -0.5F, -0.5F, -0.5F, sideTexture);
    
        renderBlocks.setRenderBounds( 0.0F, 0, 1F - ( 1F / 16F ), 
        		1F, 1, 1F );
        
        RenderUtils.renderInvBlockWithTexture(renderBlocks, this, -0.5F, -0.5F, -0.5F, sideTexture);
    
        renderBlocks.setRenderBounds( 1F - ( 1F / 16F ), 0.00001F, 0.00001F, 
	    		0.99999F, 0.99999F, 0.99999F );
        
        RenderUtils.renderInvBlockWithTexture(renderBlocks, this, -0.5F, -0.5F, -0.5F, sideTexture);
    
        renderBlocks.setRenderBounds( 0, 0, 0F, 
	    		1.0F, 1, ( 1F / 16F ) );
        
        RenderUtils.renderInvBlockWithTexture(renderBlocks, this, -0.5F, -0.5F, -0.5F, sideTexture);
        
        // render bottom
        
        renderBlocks.setRenderBounds( 0.0001F, 0.001F, 0.0001F, 
	    		0.9999F, ( 1F / 16F ), 0.9999F );
        
        RenderUtils.renderInvBlockWithTexture(renderBlocks, this, -0.5F, -0.5F, -0.5F, iconBySideArray[0]);
        
        // render top
        
        renderBlocks.setRenderBounds( 0.0001F, 1F - ( 1F / 16F ), 0.0001F, 
	    		0.9999F, 0.999F, 0.9999F );
        
        RenderUtils.renderInvBlockWithTexture(renderBlocks, this, -0.5F, -0.5F, -0.5F, iconBySideArray[1]);
    }
}
