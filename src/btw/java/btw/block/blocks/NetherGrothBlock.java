// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.util.CustomDamageSource;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class NetherGrothBlock extends Block
{
	private static final float BLOCK_HARDNESS = 0.2F;
	
	public NetherGrothBlock(int iBlockID)
	{
        super( iBlockID, BTWBlocks.netherGrothMaterial);
        
        setHardness(BLOCK_HARDNESS);
        setAxesEffectiveOn(true);
        
        setStepSound( BTWBlocks.stepSoundSquish);
        
        setUnlocalizedName( "fcBlockGroth" );
   
        setTickRandomly( true );
	}

	@Override
    public void onBlockAdded(World world, int i, int j, int k )
    {
        super.onBlockAdded( world, i, j, k );

        int iBlockBelowID = world.getBlockId( i, j - 1, k );
        
        if ( iBlockBelowID == Block.netherrack.blockID )
        {
        	// no notify required as it's strictly an aesthetic change
        	world.setBlockAndMetadata( i, j - 1, k, BTWBlocks.aestheticOpaque.blockID, AestheticOpaqueBlock.SUBTYPE_NETHERRACK_WITH_GROWTH);
        }        
    }
	
	@Override
    public void breakBlock( World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
		int iHeight = getHeightLevel(world, i, j, k);
		
		if ( iHeight == 7 )
		{
			releaseSpores(world, i, j, k);
		}
		
        super.breakBlock( world, i, j, k, iBlockID, iMetadata );
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
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
            IBlockAccess blockAccess, int i, int j, int k)
    {
		double dHeight = (double)(getHeightLevel(blockAccess, i, j, k) + 1 ) / 16D;
    	
    	return AxisAlignedBB.getAABBPool().getAABB( 0D, 0D, 0D, 1D, dHeight, 1D );
    }
    
	@Override
    public int getMobilityFlag()
    {
		// block breaks when pushed by piston
		
        return 1;
    }
	
	@Override
    public int quantityDropped( Random random )
    {
        return 0;
    }
    
	@Override
    public int idDropped( int iMetaData, Random random, int iFortuneModifier )
    {
        return 0;
    }
	
	@Override
    public boolean canPlaceBlockAt( World world, int i, int j, int k )
    {
    	return world.doesBlockHaveSolidTopSurface( i, j - 1, k );
    }
    
	@Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iChangedBlockID )
    {
		if ( !canPlaceBlockAt( world, i, j, k ) )
		{
			// play block destroy sound and particles
			
	        world.playAuxSFX( 2001, i, j, k, blockID + ( world.getBlockMetadata( i, j, k ) << 12 ) );

			// destroy the block if we no longer have a solid base
			
			world.setBlockWithNotify( i, j, k, 0 );
			
		}
    }
    
	@Override
    public void updateTick( World world, int i, int j, int k, Random random )
    {
		int iHeight = getHeightLevel(world, i, j, k);
		int iBlockBelowID = world.getBlockId( i, j - 1, k );
		boolean bOnNetherrack = false;
				
        if ( iBlockBelowID == Block.netherrack.blockID )
        {
        	// convert regular netherrack to one with a growth texture
        	
        	// no notify required as it's strictly an aesthetic change
        	world.setBlockAndMetadata( i, j - 1, k, BTWBlocks.aestheticOpaque.blockID, AestheticOpaqueBlock.SUBTYPE_NETHERRACK_WITH_GROWTH);
        	
        	bOnNetherrack = true;
        }
        else if ( iBlockBelowID ==  BTWBlocks.aestheticOpaque.blockID )
        {
        	int iSubtype = world.getBlockMetadata( i, j - 1, k );
        	
        	if ( iSubtype == AestheticOpaqueBlock.SUBTYPE_NETHERRACK_WITH_GROWTH)
        	{
        		bOnNetherrack = true;
        	}
        }
        
		// attempt to grow
		
		if ( iHeight < 7 )		
		{
			boolean bGrow = false;
			
			if ( bOnNetherrack )
			{
				bGrow = true;
			}
			else
			{
				if (getMaxHeightOfNeighbors(world, i, j, k) > iHeight + 1 )
				{
					bGrow = true;
				}
			}
			
			if ( bGrow )
			{
				iHeight++;
				
				setHeightLevel(world, i, j, k, iHeight);
				
		        world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
			}
		}
		
		// attempt to spread
		
		if ( iHeight >= 1 )
		{
			// select a random directional on horizontal plane to attempt to spread to
			
			int iFacing = random.nextInt( 4 ) + 2;
			
			BlockPos targetPos = new BlockPos( i, j, k );
			
			targetPos.addFacingAsOffset(iFacing);
			
			if ( isBlockOpenToSpread(world, targetPos.x, targetPos.y, targetPos.z) )
			{
				if ( world.doesBlockHaveSolidTopSurface(targetPos.x, targetPos.y - 1, targetPos.z) )
				{
					spreadToBlock(world, targetPos.x, targetPos.y, targetPos.z);
				}
				else
				{
					// the moss can only spread to downward neighbors if it is on netherrack
					
					if ( bOnNetherrack )
					{
						targetPos.y -= 1;
						
						if ( isBlockOpenToSpread(world, targetPos.x, targetPos.y, targetPos.z) )
						{
							if ( world.doesBlockHaveSolidTopSurface(targetPos.x, targetPos.y - 1, targetPos.z) )
							{
								spreadToBlock(world, targetPos.x, targetPos.y, targetPos.z);
							}
						}
					}
				}
			}
			else
			{
				// the moss can only spread upwards onto a netherrack block and if there is empty space above where it's currently at
				
				if ( world.isAirBlock( i, j + 1, k ) && world.getBlockId(targetPos.x, targetPos.y, targetPos.z) == Block.netherrack.blockID )
				{
					targetPos.y += 1;
					
					if ( isBlockOpenToSpread(world, targetPos.x, targetPos.y, targetPos.z) )
					{
						spreadToBlock(world, targetPos.x, targetPos.y, targetPos.z);
					}
				}
			}
		}
    }
	
	@Override
    public void onEntityCollidedWithBlock( World world, int i, int j, int k, Entity entity )
    {
		if ( !entity.isDead && !world.isRemote )
		{
			int iHeight = getHeightLevel(world, i, j, k);
			
			// only mature blood moss does damage
			
			if ( iHeight >= 7 )
			{
				if ( entity instanceof EntityLiving )
				{					
					boolean bAttack = true;
					
					if ( entity instanceof EntityPlayer )
					{
						EntityPlayer player = (EntityPlayer)entity;
						
						if ( player.isWearingSoulforgedBoots() )
						{
							bAttack = false;
						}
					}
					
					if ( bAttack )
					{
				        if ( entity.attackEntityFrom(CustomDamageSource.damageSourceGroth, 2) )
				        {
				        	entity.isAirBorne = true;
				            entity.motionY += 0.84D;
				            
			                world.playAuxSFX( BTWEffectManager.GHAST_SCREAM_EFFECT_ID, i, j, k, 0 );
				        }			        
					}
				}
				else if ( entity instanceof EntityItem )
				{
					// full grown growth eats food and mush
					
					EntityItem entityItem = (EntityItem)entity;
					
					if ( entityItem.delayBeforeCanPickup <= 0 )
					{					
						if ( entityItem.getEntityItem().getItem() instanceof ItemFood ||
							entityItem.getEntityItem().itemID == Block.mushroomRed.blockID || 
							entityItem.getEntityItem().itemID == Block.mushroomBrown.blockID )
						{
							entityItem.setDead();
							
			                world.playAuxSFX( BTWEffectManager.BURP_SOUND_EFFECT_ID, i, j, k, 0 );
						}
					}
				}
			}
		}
    }
    
	@Override
    public float getMovementModifier(World world, int i, int j, int k)
    {
		return 0.8F;        
    }
	
    //------------- Class Specific Methods ------------//
	
	public int getHeightLevel(IBlockAccess blockAccess, int i, int j, int k)
	{
		return getHeightLevelFromMetadata(blockAccess.getBlockMetadata(i, j, k));
	}
	
	public int getHeightLevelFromMetadata(int iMetdata)
	{
		return iMetdata & 7;
	}
	
	public void setHeightLevel(World world, int i, int j, int k, int iHeight)
	{
		int iMetadata = world.getBlockMetadata( i, j, k ) & 8; // filter out old height
		
		iMetadata |= iHeight;
		
		world.setBlockMetadataWithNotify( i, j, k, iMetadata );
	}
	
	private int getMaxHeightOfNeighbors(World world, int i, int j, int k)
	{
		int iMaxHeight = -1;
		
		for ( int iTempFacing = 2; iTempFacing <= 5; iTempFacing++ )
		{
			BlockPos tempPos = new BlockPos( i, j, k );
			
			tempPos.addFacingAsOffset(iTempFacing);
			
			if (world.getBlockId(tempPos.x, tempPos.y, tempPos.z) == blockID )
			{
				int iTempHeight = getHeightLevel(world, tempPos.x, tempPos.y, tempPos.z);
				
				if ( iTempHeight > iMaxHeight )
				{
					iMaxHeight = iTempHeight;
				}
			}
		}
		
		return iMaxHeight;
	}
	
	private void spreadToBlock(World world, int i, int j, int k)
	{
		if ( world.getBlockId( i, j, k ) == Block.fire.blockID )
		{
            world.playAuxSFX( BTWEffectManager.FIRE_FIZZ_EFFECT_ID, i, j, k, 0 );
		}
		else if ( world.getBlockId( i, j, k ) == Block.mushroomBrown.blockID ||
			world.getBlockId( i, j, k ) == Block.mushroomRed.blockID )
		{				
            world.playAuxSFX( BTWEffectManager.BURP_SOUND_EFFECT_ID, i, j, k, 0 );
		}
		
		if ( world.setBlockWithNotify( i, j, k, blockID ) )
		{
            world.playAuxSFX( BTWEffectManager.GHAST_MOAN_EFFECT_ID, i, j, k, 0 );
		}
	}
	
	private boolean isBlockOpenToSpread(World world, int i, int j, int k)
	{
		if ( world.isAirBlock( i, j, k )  )
		{
			return true;
		}
		else
		{
			int iBlockID = world.getBlockId( i, j, k );
			
			if ( iBlockID == Block.fire.blockID || iBlockID == Block.mushroomRed.blockID ||
				iBlockID == Block.mushroomBrown.blockID )
			{
				return true;
			}
		}
		
		return false;
	}
	
	private void releaseSpores(World world, int i, int j, int k)
	{
        world.playAuxSFX( BTWEffectManager.NETHER_GROTH_SPORES_EFFECT_ID, i, j, k, 0 );

        // spread growth to nearby blocks
        
        for ( int iTempI = i - 3; iTempI <= i + 3; iTempI++ )
        {
            for ( int iTempJ = j - 3; iTempJ <= j + 3; iTempJ++ )
            {
                for ( int iTempK = k - 3; iTempK <= k + 3; iTempK++ )
                {
                	if ( iTempI != i || iTempJ  != j || iTempK != k )
                	{
                		if ( isBlockOpenToSpread(world, iTempI, iTempJ, iTempK) )
                		{
                			if ( world.doesBlockHaveSolidTopSurface( iTempI, iTempJ - 1, iTempK ) )
                			{
                				if ( world.rand.nextInt( 2 ) == 0 )
                				{
                					world.setBlockWithNotify( iTempI, iTempJ, iTempK, blockID );
                				}
                			}
                		}
                	}
                }
            }
        }
        
        // damage living stuff in the vicinity
        
		double posX = (double)i + 0.5D;
		double posY = (double)j + 0.5D;
		double posZ = (double)k + 0.5D;
		
        List list = world.getEntitiesWithinAABB( EntityLiving.class, 
    		AxisAlignedBB.getAABBPool().getAABB( 
    			posX - 5.0D, posY - 5.0D, posZ - 5.0D,
    			posX + 5.0D, posY + 5.0D, posZ + 5.0D ) );
    			
        if ( list != null && list.size() > 0 )
        {
            for ( int listIndex = 0; listIndex < list.size(); listIndex++ )
            {
	    		EntityLiving targetEntity = (EntityLiving)list.get( listIndex );
	    		
	    		boolean bDamageEntity = true;
	    		
	    		if ( targetEntity instanceof EntityPlayer )
	    		{
	    			EntityPlayer player = (EntityPlayer)targetEntity;
	    			
	    			if ( player.isWearingFullSuitSoulforgedArmor() )
	    			{
	    				bDamageEntity = false;
	    			}
	    		}
	    		
	    		if ( bDamageEntity )
	    		{
	    			targetEntity.attackEntityFrom(CustomDamageSource.damageSourceGrothSpores, 4);
	    			
                    targetEntity.addPotionEffect(new PotionEffect(Potion.poison.id, 300, 0));
	    		}
            }
	    		
        }
	}
	
	@Override
	public boolean attemptToAffectBlockWithSoul(World world, int x, int y, int z) {
		
		int iHeightLevel = getHeightLevel(world, x, y, z);
		
		if (iHeightLevel < 7) {
			setHeightLevel(world, x, y, z, 7);
			return true;
		}
		
		return false;
	}
	
	
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconBySideArray = new Icon[6];
    @Environment(EnvType.CLIENT)
    private Icon iconTopGrown;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
		Icon bottomIcon = register.registerIcon( "fcBlockGroth_bottom" );
		
        blockIcon = bottomIcon; // for hit effects

		iconBySideArray[0] = bottomIcon;
		iconBySideArray[1] = register.registerIcon("fcBlockGroth_top");
        
        Icon sideIcon = register.registerIcon( "fcBlockGroth_side" );

		iconBySideArray[2] = sideIcon;
		iconBySideArray[3] = sideIcon;
		iconBySideArray[4] = sideIcon;
		iconBySideArray[5] = sideIcon;

		iconTopGrown = register.registerIcon("fcBlockGroth_top_grown");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
		if ( iSide == 1 )
		{
			int iHeight = getHeightLevelFromMetadata(iMetadata);
			
			if ( iHeight >= 7 )
			{
				return iconTopGrown;
			}
		}
		
		return iconBySideArray[iSide];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick( World world, int i, int j, int k, Random random )
    {
        super.randomDisplayTick( world, i, j, k, random );

        if ( random.nextInt(10) == 0)
        {
    		float fHeight = ( (float)(getHeightLevel(world, i, j, k) + 1 ) ) / 16.0F;
    		
            world.spawnParticle( "townaura", (float)i + random.nextFloat(), (float)j + fHeight + 0.1F, (float)k + random.nextFloat(), 0.0D, 0.0D, 0.0D );
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered( IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide )
    {
    	if ( iSide == 0 )
    	{
    		// bottom never renders given groth has to rest on a solid block
    		
    		return false;
    	}
    	else if ( iSide != 1 )
    	{
    		int iNeighborBlockID = blockAccess.getBlockId( iNeighborI, iNeighborJ, iNeighborK );
    		
    		if ( iNeighborBlockID == blockID )
    		{        		
    			int iNeighborHeightLevel = getHeightLevel(blockAccess, iNeighborI, iNeighborJ, iNeighborK);
    			
        		// check for fully grown groth to side to eliminate a large number of faces
        		
    			if ( iNeighborHeightLevel >= 7 )
    			{
    				return false;
    			}
    			else
    			{
	    			BlockPos myPos = new BlockPos( iNeighborI, iNeighborJ, iNeighborK, Block.getOppositeFacing(iSide) );
	    			int iMyHeightLevel = getHeightLevel(blockAccess, myPos.x, myPos.y, myPos.z);
	    			
	    			if ( iNeighborHeightLevel >= iMyHeightLevel )
	    			{
	    				return false;
	    			}
	    		}
    		}
    		else
    		{
    			Block neighborBlock = Block.blocksList[iNeighborBlockID];
    			
    			if ( neighborBlock != null )
    			{
    		        return neighborBlock.shouldRenderNeighborHalfSlabSide(blockAccess, iNeighborI, iNeighborJ, iNeighborK, iSide, false);
    			}
    		}
    	}
    	
    	return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRenderNeighborHalfSlabSide(IBlockAccess blockAccess, int i, int j, int k, int iNeighborSlabSide, boolean bNeighborUpsideDown)
    {
		return bNeighborUpsideDown || getHeightLevel(blockAccess, i, j, k) < 7;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRenderNeighborFullFaceSide(IBlockAccess blockAccess, int i, int j, int k, int iNeighborSide)
    {
		return iNeighborSide != 1;
    }
}