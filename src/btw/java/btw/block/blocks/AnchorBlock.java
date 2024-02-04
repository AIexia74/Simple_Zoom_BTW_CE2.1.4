// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.block.tileentity.PulleyTileEntity;
import btw.client.render.util.RenderUtils;
import btw.entity.mechanical.platform.MovingAnchorEntity;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class AnchorBlock extends Block
{
	public static double anchorBaseHeight = (6D / 16D );
	
	public AnchorBlock(int iBlockID)
	{
        super( iBlockID, Material.rock );

        setHardness( 2F );
        
    	initBlockBounds(0D, 0D, 0D, 1D, anchorBaseHeight, 1D);
    	
        setStepSound( soundStoneFootstep );
        
        setUnlocalizedName( "fcBlockAnchor" );        
        
        setCreativeTab( CreativeTabs.tabTransport );
	}
	
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
            IBlockAccess blockAccess, int i, int j, int k)
    {
        int iFacing = getFacing(blockAccess, i, j, k);
        
    	// this is necessary because the rendering code changes the block bounds
    	
        switch ( iFacing )
        {
	        case 0:
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB(
						0D, 1D - anchorBaseHeight, 0D,
						1D, 1D, 1D );
	        	
	        case 1:
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB(
						0D, 0D, 0D,
						1D, anchorBaseHeight, 1D);
	        	
	        case 2:
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB(         	
	        		0D, 0D, 1D - anchorBaseHeight,
            		1D, 1D, 1D );
	        	
	        case 3:
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB(
						0D, 0D, 0D,
						1D, 1D, anchorBaseHeight);
	        	
	        case 4:
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB(
						1D - anchorBaseHeight, 0D, 0D,
						1D, 1D, 1D );
	        	
	        default: // 5
	        	
	        	return AxisAlignedBB.getAABBPool().getAABB(
						0D, 0D, 0D, anchorBaseHeight, 1D, 1D);
        }    	
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
    public int onBlockPlaced( World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata )
    {
        int iAnchorFacing = iFacing;
        
        return setFacing(iMetadata, iAnchorFacing);
    }

	@Override
    public boolean onBlockActivated( World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
    {
    	ItemStack playerEquippedItem = player.getCurrentEquippedItem();
    	
    	if ( playerEquippedItem != null )
    	{
			return false;
    	}
    	
		retractRope(world, i, j, k, player);
		
		return true;    	
    }    

	@Override
	public int getFacing(int iMetadata)
	{
    	return iMetadata;
	}
	
	@Override
	public int setFacing(int iMetadata, int iFacing)
	{
		return iFacing;
	}
	
	@Override
	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k)
	{		
		int iFacing = getFacing(blockAccess, i, j, k);

		return iFacing != 0;
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
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
		return Block.getOppositeFacing(iFacing) == getFacing(blockAccess, i, j, k);
	}
    
    //------------- Class Specific Methods ------------//
    
	void retractRope(World world, int i, int j, int k, EntityPlayer entityPlayer)
	{
    	// scan downward towards bottom of rope
    	
    	for ( int tempj = j - 1; tempj >= 0; tempj-- )
    	{
    		int iTempBlockID = world.getBlockId( i, tempj, k );
    		
    		if ( iTempBlockID == BTWBlocks.ropeBlock.blockID )
    		{
        		if ( world.getBlockId( i, tempj - 1, k ) != BTWBlocks.ropeBlock.blockID )
        		{
        			// we've found the bottom of the rope
        			
                    addRopeToPlayerInventory(world, i, j, k, entityPlayer);
                    
                    Block targetBlock = BTWBlocks.ropeBlock;
                    
                    if ( !world.isRemote )
                    {    		        
	                    // destroy the block
	                    
	    		        world.playAuxSFX( 2001, i, j, k, iTempBlockID );
	    		        
	                    world.setBlockWithNotify( i, tempj, k, 0 );
                    }                    
                    
                    break;
        		}
    		}
    		else
    		{
    			break;
    		}
    	}        
	}
	
	private void addRopeToPlayerInventory(World world, int i, int j, int k, EntityPlayer entityPlayer)
	{
		ItemStack ropeStack = new ItemStack( BTWItems.rope);
		
		if ( entityPlayer.inventory.addItemStackToInventory( ropeStack ) )
		{
            world.playSoundAtEntity( entityPlayer, "random.pop", 0.2F, 
        		( ( world.rand.nextFloat() - world.rand.nextFloat() ) * 0.7F + 1F ) * 2F);
		}
		else
		{
			ItemUtils.ejectStackWithRandomOffset(world, i, j, k, ropeStack);
		}
	}
	
    /*
     * return true if the associated anchor is converted into an entity
     */
	public boolean notifyAnchorBlockOfAttachedPulleyStateChange(PulleyTileEntity tileEntityPulley,
																World world, int i, int j, int k)
	{
		int iMovementDirection = 0;
		
		if ( tileEntityPulley.isRaising() )
		{
			if ( world.getBlockId( i, j + 1, k ) == BTWBlocks.ropeBlock.blockID )
			{
				iMovementDirection = 1;
			}
		}
		else if ( tileEntityPulley.isLowering() )
		{
			if ( world.isAirBlock( i, j - 1, k )  || 
				world.getBlockId( i, j - 1, k ) == BTWBlocks.platform.blockID )
			{
				iMovementDirection = -1;
			}
		}
		
		if ( iMovementDirection != 0 )
		{
			convertAnchorToEntity(world, i, j, k, tileEntityPulley, iMovementDirection);
			
			return true;
		}
		
		return false;
	}
	
	private void convertAnchorToEntity(World world, int i, int j, int k, PulleyTileEntity attachedTileEntityPulley, int iMovementDirection)
	{
		BlockPos pulleyPos = new BlockPos( attachedTileEntityPulley.xCoord,
				attachedTileEntityPulley.yCoord, attachedTileEntityPulley.zCoord );
		
		MovingAnchorEntity entityAnchor = (MovingAnchorEntity) EntityList.createEntityOfType(MovingAnchorEntity.class, world,
	    		(float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, 
	    		pulleyPos, iMovementDirection ); 
				
        world.spawnEntityInWorld( entityAnchor );
        
        convertConnectedPlatformsToEntities(world, i, j, k, entityAnchor);
        
        world.setBlockWithNotify( i, j, k, 0 );
	}
	
    private void convertConnectedPlatformsToEntities(World world, int i, int j, int k, MovingAnchorEntity associatedAnchorEntity)
    {
    	int iTargetJ = j - 1;
    	
    	int iTargetBlockID = world.getBlockId( i, iTargetJ, k );
    	
    	if ( iTargetBlockID == BTWBlocks.platform.blockID )
    	{
    		( (PlatformBlock) BTWBlocks.platform).covertToEntitiesFromThisPlatform(
				world, i, iTargetJ, k, associatedAnchorEntity);
    	}
    }
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon iconFront;
    @Environment(EnvType.CLIENT)
    public Icon iconNub;
    @Environment(EnvType.CLIENT)
    private Icon iconRope;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		super.registerIcons( register );

		iconFront = register.registerIcon("fcBlockAnchor_front");
		iconNub = register.registerIcon("fcBlockAnchor_nub");
		iconRope = register.registerIcon("fcBlockRope");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
    	if ( iSide < 2 )
    	{
    		return iconFront;
    	}
    	
        return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture( IBlockAccess blockAccess, int i, int j, int k, int iSide )
    {
        int iFacing = blockAccess.getBlockMetadata( i, j, k );
        
    	if ( iSide == iFacing || iSide == Block.getOppositeFacing(iFacing) )
    	{
    		return iconFront;
    	}
    	
        return blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, 
    	int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
		return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(
			iNeighborI, iNeighborJ, iNeighborK, iSide);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
    	IBlockAccess blockAccess = renderer.blockAccess;
    	
        // render the base
        
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
        	renderer.blockAccess, i, j, k) );
        
        renderer.renderStandardBlock( this, i, j, k );
        
        int iFacing = getFacing(blockAccess, i, j, k);
        
        // render the loop

        double dHalfLength = 0.125F;
        double dHalfWidth = 0.125F;
        
        double dBlockHeight = 0.25F;
        
        renderer.setRenderBounds( 
        	0.5D - dHalfWidth, AnchorBlock.anchorBaseHeight, 0.5D - dHalfLength,
    		0.5D + dHalfWidth, AnchorBlock.anchorBaseHeight + dBlockHeight, 0.5D + dHalfLength);
        
        RenderUtils.renderStandardBlockWithTexture(renderer, this, i, j, k, iconNub);

        boolean bRenderRope = false;
        
        dHalfLength = RopeBlock.ROPE_WIDTH * 0.5F;
        dHalfWidth = RopeBlock.ROPE_WIDTH * 0.5F;
        dBlockHeight = anchorBaseHeight;
        
        if ( iFacing == 1 ) // facing up
        {
        	int iBlockAboveId = blockAccess.getBlockId( i, j + 1, k );
        	
	        if ( iBlockAboveId == BTWBlocks.ropeBlock.blockID ||
        		iBlockAboveId == BTWBlocks.pulley.blockID )
	        {
	        	renderer.setRenderBounds( 0.5F - dHalfWidth, dBlockHeight, 0.5F - dHalfLength, 
	        		0.5F + dHalfWidth, 1.0F, 0.5F + dHalfLength );
	            
	            bRenderRope = true;
	        }
        }
        else
        {
	        // if there is rope below, we need to render the connecting piece
	        
	        if ( blockAccess.getBlockId( i, j - 1, k ) == BTWBlocks.ropeBlock.blockID )
	        {
	        	renderer.setRenderBounds( 0.5F - dHalfWidth, 0.0F, 0.5F - dHalfLength, 
	        		0.5F + dHalfWidth, dBlockHeight, 0.5F + dHalfLength );
	            
	            bRenderRope = true;	            
	        }
        }
        
        if ( bRenderRope )
        {
            RenderUtils.renderStandardBlockWithTexture(renderer, this, i, j, k, iconRope);
        }
        
        return true;        
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
        // draw base
        
    	renderBlocks.setRenderBounds(0, 0, 0, 1, anchorBaseHeight, 1);
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, this, -0.5F, -0.25F, -0.5F, 1);
        
        // draw anchor point
        
        float fHalfLength = 0.125F;
        float fHalfWidth = 0.125F;
        float fBlockHeight = 0.25F;
        
        renderBlocks.setRenderBounds( 0.5F - fHalfWidth, AnchorBlock.anchorBaseHeight, 0.5F - fHalfLength,
    		0.5F + fHalfWidth, AnchorBlock.anchorBaseHeight + fBlockHeight, 0.5F + fHalfLength);
        
        RenderUtils.renderInvBlockWithTexture(renderBlocks, this, -0.5F, -0.25F, -0.5F, iconNub);
    }
}
