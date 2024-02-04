// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.client.fx.BTWEffectManager;
import btw.crafting.util.FurnaceBurnTime;
import btw.item.BTWItems;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class BloodWoodLogBlock extends Block
{
    private final static float HARDNESS = 2F;

    public BloodWoodLogBlock(int iBlockID )
    {
        super( iBlockID, BTWBlocks.logMaterial);
        
        setHardness(HARDNESS);
        setAxesEffectiveOn(true);
        
        setBuoyancy(1F);
    	setFurnaceBurnTime(4 * FurnaceBurnTime.PLANKS_BLOOD.burnTime);
		setFireProperties(Flammability.EXTREME);
		
        setTickRandomly( true );
		
        setStepSound( BTWBlocks.stepSoundSquish);
        
        setUnlocalizedName( "fcBlockBloodWood" );
        
		setCreativeTab( CreativeTabs.tabBlock );
    }
    
	@Override
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
	{
		iMetadata = setFacing(iMetadata, iFacing);
		
		return iMetadata;
	}
	
	@Override
    public void breakBlock( World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
        world.playAuxSFX( BTWEffectManager.GHAST_SCREAM_EFFECT_ID, i, j, k, 0 );
        
        notifySurroundingBloodLeavesOfBlockRemoval(world, i, j, k);
        
        super.breakBlock( world, i, j, k, iBlockID, iMetadata );
    }

	@Override
    public void updateTick( World world, int i, int j, int k, Random random )
    {
		if ( getCanGrow(world, i, j, k) )
		{
			int iGrowthDirection = getFacing(world, i, j, k);
			
			if ( iGrowthDirection != 0 )
			{
		        // verify if we're in the nether
				if (world.provider.dimensionId == -1) {
					grow(world, i, j, k, random);
				}
			}
			
			// A block of Blood Wood will only attempt to grow once, then stop
			
			setCanGrow(world, i, j, k, false);
		}
    }
    
	@Override
	public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop)
	{
		dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, 4, 0, fChanceOfDrop);
		dropItemsIndividually(world, i, j, k, BTWItems.bark.itemID, 1, 4, fChanceOfDrop);
		dropItemsIndividually(world, i, j, k, BTWItems.soulDust.itemID, 1, 0, fChanceOfDrop);
		
		return true;
	}
	
	@Override
	public int getFacing(int iMetadata)
	{
		return iMetadata & 7;
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
		iMetadata &= (~7); // filter out old state
		
		iMetadata |= iFacing;
		
		return iMetadata;
	}
	
	@Override
	public boolean toggleFacing(World world, int i, int j, int k, boolean bReverse)
	{
		int iFacing = getFacing(world, i, j, k);
		
		iFacing = Block.cycleFacing(iFacing, bReverse);

		setFacing(world, i, j, k, iFacing);
		
        world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
        
		return true;
	}
	
	@Override
	public boolean isLog(IBlockAccess blockAccess, int x, int y, int z) {
		return true;
	}
    
    //------------- Class Specific Methods ------------//
	
	public boolean getCanGrow(IBlockAccess blockAccess, int i, int j, int k)
	{
    	return ( blockAccess.getBlockMetadata( i, j, k ) & 8 ) > 0;
	}
    
	public void setCanGrow(World world, int i, int j, int k, boolean bCanGrow)
	{
    	int iMetaData = world.getBlockMetadata( i, j, k ) & (~8); // filter out old direction
    	
    	if ( bCanGrow )
    	{
    		iMetaData |= 8;
    	}
    	
    	world.setBlockMetadata( i, j, k, iMetaData );
	}
    
    public void grow(World world, int i, int j, int k, Random random)
    {
    	if (countBloodWoodNeighboringOnBlockWithSoulSand(world, i, j, k) >= 2 )
    	{
    		// too much neighboring wood to grow further
    		
    		return;
    	}
    	
    	int iGrowthDirection = getFacing(world, i, j, k);
    	
    	if ( iGrowthDirection == 1 )
    	{
    		// trunk growth
    		
    		int iRandomFactor = random.nextInt( 100 );
    		
    		if ( iRandomFactor < 25 )
    		{
    			// just continue growing upwards
    			
    			attemptToGrowIntoBlock(world, i, j + 1, k, 1);
    		}
    		else if ( iRandomFactor < 90 )
    		{
    			// split and grow upwards
    			
				int iTargetFacing = random.nextInt( 4 ) + 2;
				
	    		BlockPos targetPos = new BlockPos( i, j, k );
	    		
	    		targetPos.addFacingAsOffset(iTargetFacing);
	    		
	    		attemptToGrowIntoBlock(world, targetPos.x, targetPos.y, targetPos.z, iTargetFacing);
	    		
    			attemptToGrowIntoBlock(world, i, j + 1, k, 1);
    		}
    		else
    		{
    			// split
    			
    			for ( int iTempCount = 0; iTempCount < 2; iTempCount++ )
    			{
    				int iTargetFacing = random.nextInt( 4 ) + 2;
    				
    	    		BlockPos targetPos = new BlockPos( i, j, k );
    	    		
    	    		targetPos.addFacingAsOffset(iTargetFacing);
    	    		
    	    		attemptToGrowIntoBlock(world, targetPos.x, targetPos.y, targetPos.z, iTargetFacing);
    			}
    		}
    	}
    	else
    	{
    		// branch growth
    		
    		int iRandomFactor = random.nextInt( 100 );
    		
    		if ( iRandomFactor < 40 )
    		{
    			// grow upwards
    			
    			attemptToGrowIntoBlock(world, i, j + 1, k, iGrowthDirection);
    			
    			// reorient existing block so that it looks right
    			
    			setFacing(world, i, j, k, 1);
    		}
    		else if ( iRandomFactor < 65 )
    		{
    			// grow in the growth direction
    			
	    		BlockPos targetPos = new BlockPos( i, j, k );
	    		
	    		targetPos.addFacingAsOffset(iGrowthDirection);
	    		
	    		attemptToGrowIntoBlock(world, targetPos.x, targetPos.y, targetPos.z, iGrowthDirection);
    		}
    		else if ( iRandomFactor < 90 )
    		{
    			// split and keep going
    			
				int iTargetFacing = random.nextInt( 4 ) + 2;
				
				if ( iTargetFacing == iGrowthDirection )
				{
					iTargetFacing = 1;
				}
				
	    		BlockPos targetPos = new BlockPos( i, j, k );
	    		
	    		targetPos.addFacingAsOffset(iTargetFacing);
	    		
	    		int iTargetGrowthDirection = iGrowthDirection;
	    		
	    		if ( iTargetFacing >= 2 )
	    		{
	    			iTargetGrowthDirection = iTargetFacing;
	    		}
	    		
	    		attemptToGrowIntoBlock(world, targetPos.x, targetPos.y, targetPos.z, iTargetGrowthDirection);
	    		
	    		targetPos = new BlockPos( i, j, k );
	    		
	    		targetPos.addFacingAsOffset(iGrowthDirection);
	    		
	    		if (!attemptToGrowIntoBlock(world, targetPos.x, targetPos.y, targetPos.z, iGrowthDirection) && iTargetFacing == 1 )
	    		{
	    			// reorient existing block so that it looks right
	    			
	    			setFacing(world, i, j, k, 1);
	    		}
    		}
    		else
    		{
    			// split
    			
    			int iGrowthDirections[] = new int[2];
    			
    			for ( int iTempCount = 0; iTempCount < 2; iTempCount++ )
    			{
    				iGrowthDirections[iTempCount] = 0;
    				
    				int iTargetFacing = random.nextInt( 4 ) + 2;
    				
    				if ( iTargetFacing == iGrowthDirection )
    				{
    					iTargetFacing = 1;
    				}
    				
    	    		BlockPos targetPos = new BlockPos( i, j, k );
    	    		
    	    		targetPos.addFacingAsOffset(iTargetFacing);
    	    		
    	    		int iTargetGrowthDirection = iGrowthDirection;
    	    		
    	    		if ( iTargetFacing >= 2 )
    	    		{
    	    			iTargetGrowthDirection = iTargetFacing;
    	    		}
    	    		
    	    		if ( attemptToGrowIntoBlock(world, targetPos.x, targetPos.y, targetPos.z, iTargetGrowthDirection) )
    	    		{
    	    			iGrowthDirections[iTempCount] = iTargetFacing;
    	    		}
    			}
    			
    			if ( ( iGrowthDirections[0] == 1 && iGrowthDirections[1] <= 1 ) || ( iGrowthDirections[1] == 1 && iGrowthDirections[0] == 0 ) ) 
    			{
	    			// reorient existing block so that it looks right
	    			
	    			setFacing(world, i, j, k, 1);
    			}
    		}
    	}
    }
    
    public boolean attemptToGrowIntoBlock(World world, int i, int j, int k, int iGrowthDirection)
    {	    	
    	if (( !( world.isAirBlock( i, j, k ) ) &&  !isBloodLeafBlock(world, i, j, k) ) ||
			countBloodWoodNeighboringOnBlockWithSoulSand(world, i, j, k) >= 2 )
    	{
    		// not empty, or too much neighboring wood to grow further
    		
    		return false;
    	}
    	
    	world.setBlockAndMetadataWithNotify( i, j, k, blockID, iGrowthDirection | 8 );
    	
		growLeaves(world, i, j, k);
		
    	return true;
    }
    
    public void growLeaves(World world, int i, int j, int k)
    {
    	for ( int tempI = i - 1; tempI <= i + 1; tempI++ )
    	{
	    	for ( int tempJ = j - 1; tempJ <= j + 1; tempJ++ )
	    	{
		    	for ( int tempK = k - 1; tempK <= k + 1; tempK++ )
		    	{
		    		if ( world.isAirBlock( tempI, tempJ, tempK ) )
		    		{
	    				world.setBlockAndMetadataWithNotify( tempI, tempJ, tempK, 
    						BTWBlocks.bloodWoodLeaves.blockID, 0 );
		    		}
		    	}
	    	}
    	}
    }
    
    public boolean isBloodLeafBlock(World world, int i, int j, int k)
    {
    	int iBlockID = world.getBlockId( i, j, k );
    	
    	if ( iBlockID == BTWBlocks.bloodWoodLeaves.blockID )
    	{
    		return true;
    	}
    	else if ( iBlockID == BTWBlocks.aestheticVegetation.blockID )
    	{
    		int iSubType = world.getBlockMetadata( i, j, k );
    		
    		if ( iSubType == AestheticVegetationBlock.SUBTYPE_BLOOD_LEAVES)
    		{
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    public int countBloodWoodNeighboringOnBlockWithSoulSand(World world, int i, int j, int k)
    {
    	int iNeighborWoodCount = 0;
    	
    	for ( int iTempFacing = 0; iTempFacing < 6; iTempFacing++ )
    	{
    		BlockPos tempTargetPos = new BlockPos( i, j, k );
    		
    		tempTargetPos.addFacingAsOffset(iTempFacing);
    		
    		if (world.getBlockId(tempTargetPos.x, tempTargetPos.y, tempTargetPos.z) == blockID )
    		{
    			iNeighborWoodCount++;
    		}
    	}
    	
    	if ( world.getBlockId( i, j - 1, k ) == Block.slowSand.blockID )
    	{
    		iNeighborWoodCount++;
    	}
    	
    	return iNeighborWoodCount;
    }
    
    public int countBloodWoodNeighboringOnBlockIncludingDiagnals(World world, int i, int j, int k)
    {
    	int iNeighborWoodCount = 0;
    	
    	for ( int tempI = i - 1; tempI <= i + 1; tempI++ )
    	{
	    	for ( int tempJ = j - 1; tempJ <= j + 1; tempJ++ )
	    	{
		    	for ( int tempK = k - 1; tempK <= k + 1; tempK++ )
		    	{
		    		if ( world.getBlockId( tempI, tempJ, tempK ) == blockID )
		    		{
		    			if ( tempI != i || tempJ != j || tempK != k )
		    			{
		    				iNeighborWoodCount++;
		    			}
		    		}
		    	}
	    	}
    	}
    	
    	return iNeighborWoodCount;
    }
    
    public void notifySurroundingBloodLeavesOfBlockRemoval(World world, int i, int j, int k)
    {
    	// copied over from BlockLog breakBlock() with minor mods for blood leaves
        byte byte0 = 4;
        int l = byte0 + 1;
        if(world.checkChunksExist(i - l, j - l, k - l, i + l, j + l, k + l))
        {
            for(int i1 = -byte0; i1 <= byte0; i1++)
            {
                for(int j1 = -byte0; j1 <= byte0; j1++)
                {
                    for(int k1 = -byte0; k1 <= byte0; k1++)
                    {
                        int l1 = world.getBlockId(i + i1, j + j1, k + k1);
                        if(l1 != BTWBlocks.bloodWoodLeaves.blockID)
                        {
                            continue;
                        }
                        int i2 = world.getBlockMetadata(i + i1, j + j1, k + k1);
                        if((i2 & 8) == 0)
                        {
                            world.setBlockMetadata(i + i1, j + j1, k + k1, i2 | 8);
                        }
                    }
                }
            }
        }
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconSide;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

		iconSide = register.registerIcon("fcBlockBloodWood_side");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		int iFacing = iMetadata & (~8);
		
		if ( iFacing < 2 )
		{
	        if ( iSide >= 2 )
	        {
	            return iconSide;
	        }
		}
		else if ( iFacing < 4 )
		{
			if ( iSide != 2 && iSide != 3 )
	        {
	            return iconSide;
	        }
		}
		else
		{
			if ( iSide < 4 )
	        {
	            return iconSide;
	        }
		}
		
		return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
    	IBlockAccess blockAccess = renderer.blockAccess;
    	
    	int iFacing = getFacing(blockAccess, i, j, k);
    	
    	if ( iFacing == 2 )
    	{
    		renderer.setUVRotateSouth(1);
    		renderer.setUVRotateNorth(2);
    	}
    	else if ( iFacing == 3 )
    	{
    		renderer.setUVRotateSouth(2);
    		renderer.setUVRotateNorth(1);
    		renderer.setUVRotateTop(3);
    		renderer.setUVRotateBottom(3);
    	}
    	else if ( iFacing == 4 )
    	{    		
    		renderer.setUVRotateEast(1);
    		renderer.setUVRotateWest(2);
    		renderer.setUVRotateTop(2);
    		renderer.setUVRotateBottom(1);
    	}
    	else if (  iFacing == 5 )
    	{
    		renderer.setUVRotateEast(2);
    		renderer.setUVRotateWest(1);
    		renderer.setUVRotateTop(1);
    		renderer.setUVRotateBottom(2);
    	}
    	
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
        renderer.renderStandardBlock( this, i, j, k );

        renderer.clearUVRotation();
        
    	return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks renderBlocks, int i, int j, int k, boolean bFirstPassResult)
    {
        renderCookingByKiLnOverlay(renderBlocks, i, j, k, bFirstPassResult);
    }
}