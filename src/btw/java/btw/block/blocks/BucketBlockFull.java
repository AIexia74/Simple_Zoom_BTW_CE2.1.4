// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.model.BucketModel;
import btw.block.model.BucketFullModel;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public abstract class BucketBlockFull extends BucketBlock
{
    public BucketBlockFull(int iBlockID )
    {
        super( iBlockID );
    }
    
    @Override
    public void updateTick(World world, int i, int j, int k, Random rand )
    {
    	if ( !checkForFall(world, i, j, k) )
    	{    	
    		checkForSpillContents(world, i, j, k);
    	}
    }
    
	@Override
	protected void initModels()
	{
        model = new BucketFullModel();
	    
		// must initialize transformed model due to weird vanilla getIcon() calls that 
		// occur outside of regular rendering

		modelTransformed = model;
	}
	
	//------------- Class Specific Methods ------------//
	
    public void checkForSpillContents(World world, int i, int j, int k)
    {
    	int iFacing = getFacing(world, i, j, k);
    	
		// note that upside down isn't spilled as it's assumed the bucket is resting
    	// on a center hardpoint that would block it
    	
    	if ( iFacing >= 2 )
    	{
    		BlockPos targetPos = new BlockPos( i, j, k, iFacing );
    		
    		if ( attemptToSpillIntoBlock(world, targetPos.x, targetPos.y, targetPos.z) )
    		{
    			// verify the block hasn't already been displaced by the fluid
    			
    	    	if ( world.getBlockId( i, j, k ) == blockID )
    	    	{
    	    		world.setBlockAndMetadataWithNotify(i, j, k,
														BTWBlocks.placedBucket.blockID,
														setFacing(0, iFacing));
    	    	}
    		}
    	}
    }
    
    public abstract boolean attemptToSpillIntoBlock(World world, int i, int j, int k);
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRenderedOnFallingBlock(int iSide, int iMetadata)
    {
    	int iFacing = getFacing(iMetadata);
		
		int iActiveID = modelTransformed.getActivePrimitiveID();
		
		if ( iActiveID == BucketModel.ASSEMBLY_ID_CONTENTS)
		{
			return iFacing == iSide;
		}
		
		return super.shouldSideBeRenderedOnFallingBlock(iSide, iMetadata);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		if (modelTransformed.getActivePrimitiveID() == model.ASSEMBLY_ID_CONTENTS)
		{
			return getContentsIcon();
		}
		
		return super.getIcon( iSide, iMetadata );
    }

    @Environment(EnvType.CLIENT)
    protected abstract Icon getContentsIcon();
}
