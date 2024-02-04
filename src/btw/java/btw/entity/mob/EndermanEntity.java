// FCMOD

package btw.entity.mob;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.item.BTWItems;
import btw.world.util.BlockPos;
import net.minecraft.src.*;

import java.util.Iterator;
import java.util.List;

public class EndermanEntity extends EntityEnderman
{	
    public static boolean[] portableBlocks = new boolean[4096];
    
	// copies of private parent variables
    protected int teleportDelay = 0;
    protected int aggressionCounter = 0; // field_70826_g in parent
    protected boolean shouldBeScreaming; // field_104003_g in parent
	
    private static int maxEnstonePlaceWeight;
    private static final int ENDSTONE_PLACE_WEIGHT_POWER = 8;
    private static int endstonePlacementNeighborWeights[];
    
    static
    {
        portableBlocks[Block.grass.blockID] = true;
        portableBlocks[Block.dirt.blockID] = true;
        portableBlocks[Block.sand.blockID] = true;
        portableBlocks[Block.gravel.blockID] = true;
        portableBlocks[Block.tnt.blockID] = true;
        portableBlocks[Block.cactus.blockID] = true;
        portableBlocks[Block.pumpkin.blockID] = true;
        portableBlocks[Block.melon.blockID] = true;
        portableBlocks[Block.mycelium.blockID] = true;
        portableBlocks[Block.wood.blockID] = true;
        portableBlocks[Block.netherrack.blockID] = true;
    }
    
    static
    {
        endstonePlacementNeighborWeights = new int[7];
    	
    	for ( int iTemp = 0; iTemp < 7; iTemp++ )
    	{
            endstonePlacementNeighborWeights[iTemp] = iTemp;
    		
    		for (int iPower = 1; iPower < ENDSTONE_PLACE_WEIGHT_POWER; iPower++ )
    		{
                endstonePlacementNeighborWeights[iTemp] *= iTemp;
    		}
    	}

        maxEnstonePlaceWeight = endstonePlacementNeighborWeights[6];
    }    
    
    public EndermanEntity(World world )
    {
        super( world );
    }
    
    @Override
    protected void entityInit()
    {
        entityCreatureEntityInit(); // skip parent method as we've changed the type of object 16
        
        dataWatcher.addObject( 16, new Integer( 0 ) );
        dataWatcher.addObject( 17, new Byte( (byte)0 ) );
        dataWatcher.addObject( 18, new Byte( (byte)0 ) );
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag)
    {
        super.writeEntityToNBT(tag);
        
        if (this.entityToAttack != null && this.entityToAttack instanceof EntityPlayer) {
        	tag.setString("playerToAttack", ((EntityPlayer) this.entityToAttack).username);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag)
    {
        super.readEntityFromNBT(tag);
        
        if (tag.hasKey("playerToAttack")) {
        	String playerName = tag.getString("playerToAttack");
        	
        	EntityPlayer player = this.worldObj.getPlayerEntityByName(playerName);
        	
        	if (player != null && this.getDistanceToEntity(player) <= 64) {
        		this.entityToAttack = player;
        	}
        }
    }
    
    @Override
    protected void dropFewItems( boolean bKilledByPlayer, int iLootingModifier )
    {
    	super.dropFewItems( bKilledByPlayer, iLootingModifier );
        
        dropCarriedBlock();
    }
    
    @Override
    public void setCarried( int iBlockID )
    {
        dataWatcher.updateObject( 16, Integer.valueOf( iBlockID ) );
    }
    
    @Override
    public int getCarried()
    {
        return dataWatcher.getWatchableObjectInt( 16 );
    }
    
    @Override
    protected Entity findPlayerToAttack()
    {
        EntityPlayer target = worldObj.getClosestVulnerablePlayerToEntity( this, 64D );
        
        if ( target != null && isPlayerStaringAtMe(target) )
        {
            shouldBeScreaming = true;

            if (aggressionCounter == 0 )
            {
                worldObj.playSoundAtEntity( target, "mob.endermen.stare", 1F, 1F );
            }
            
            aggressionCounter++;

            if (aggressionCounter > 5 )
            {
                aggressionCounter = 0;
                setScreaming( true );
                
                angerNearbyEndermen(target);
                
                return target;
            }
        }
        else
        {
            aggressionCounter = 0;
        }

        return null;
    }

    @Override
    public void onLivingUpdate()
    {
    	moveSpeed = 0.3F;
    	
        if ( entityToAttack != null )
        {        	
        	moveSpeed = 6.5F;
        }
        
        if ( isWet() )
        {
            attackEntityFrom( DamageSource.drown, 1 );
        }

        if ( !worldObj.isRemote ) 
        {
        	if ( worldObj.getGameRules().getGameRuleBooleanValue( "mobGriefing" ) && 
        		entityToAttack == null )
        	{
	            if ( getCarried() == 0 )
	            {
	            	if ( !updateWithoutCarriedBlock() )
	            	{
	            		return;
	            	}
	            }
	            else if ( !updateWithCarriedBlock() )
	            {            	
	        		return;
	            }
        	}
        	
            if ( worldObj.isDaytime() )
            {
                float fBrightness = getBrightness( 1F );

                if ( fBrightness > 0.5F && 
                	worldObj.canBlockSeeTheSky( MathHelper.floor_double( posX ), 
	            		MathHelper.floor_double( posY ), 
	            		MathHelper.floor_double( posZ ) ) &&
        			rand.nextFloat() * 30F < ( fBrightness - 0.4F ) * 2F )
                {
                	panic();
                }
            }
        }
        else
        {
        	emitParticles();
        }

        if ( isWet() || isBurning() )
        {
        	panic();
        }

        if (isScreaming() && !shouldBeScreaming && rand.nextInt(100) == 0)
        {
            setScreaming( false );
        }

        isJumping = false;

        if ( entityToAttack != null )
        {
            faceEntity( entityToAttack, 100F, 100F );
        }

        if ( !worldObj.isRemote && isEntityAlive() )
        {
            if ( entityToAttack != null )
            {
                if (entityToAttack instanceof EntityPlayer &&
                    isPlayerStaringAtMe((EntityPlayer)entityToAttack) )
                {
                    moveStrafing = moveForward = 0F;
                    moveSpeed = 0F;

                    if ( entityToAttack.getDistanceSqToEntity( this ) < 16D )
                    {
                        teleportRandomly();
                    }

                    teleportDelay = 0;
                }
                else if ( entityToAttack.getDistanceSqToEntity( this ) > 256D &&
                          teleportDelay++ >= 30 && teleportToEntity(entityToAttack) )
                {
                    teleportDelay = 0;
                }
            }
            else
            {
                setScreaming( false );
                teleportDelay = 0;
            }
        }

        entityMobOnLivingUpdate(); // intentionally skip super method
    }
    
    @Override
    public boolean attackEntityFrom( DamageSource source, int iDamage )
    {
        if ( !isEntityInvulnerable() )
        {
            setScreaming( true );

            if ( source instanceof EntityDamageSource && source.getEntity() instanceof EntityPlayer )
            {
                shouldBeScreaming = true;
            }

            if ( source instanceof EntityDamageSourceIndirect )
            {
                shouldBeScreaming = false;

                for ( int iTempCount = 0; iTempCount < 64; ++iTempCount )
                {
                    if ( teleportRandomly() )
                    {
                        return true;
                    }
                }

                return false;
            }
            else
            {
	            if ( source.getEntity() instanceof EntityPlayer )
	            {
	                boolean bResult = entityMobAttackEntityFrom(source, iDamage);
	                
	                if ( isEntityAlive() )
	                {
	    	            for ( int iTempCount = 0; iTempCount < 64; iTempCount++ )
	    	            {
	    	            	if ( teleportRandomly() )
	    	            	{
	    	            		break;
	    	            	}
	    	            }
	                }
	                
	                angerNearbyEndermen((EntityPlayer)source.getEntity());
	                
	                return bResult;
				}

                return entityMobAttackEntityFrom(source, iDamage);
            }
        }
        
        return false;
    }

    @Override
    public void initCreature() 
    {
    	// this function handles creature specific initialization upon spawn
    	
        if ( worldObj.provider.dimensionId == 1 && worldObj.rand.nextInt( 5 ) == 0 )
    	{
        	setCarried( Block.whiteStone.blockID );
        	setCarryingData( 0 );
    	}
    }
    
    @Override
    public void checkForScrollDrop()
    {    	
    	if ( rand.nextInt( 1000 ) == 0 )
    	{
            ItemStack itemstack = new ItemStack( BTWItems.arcaneScroll, 1, Enchantment.silkTouch.effectId );
            
            entityDropItem(itemstack, 0.0F);
    	}
    }
    
    //------------- Class Specific Methods ------------//
    
    protected boolean isPlayerStaringAtMe(EntityPlayer player)
    {
        ItemStack headStack = player.inventory.armorInventory[3];

        if ( headStack == null || headStack.itemID != 
        	BTWItems.enderSpectacles.itemID )
        {
            Vec3 vLook = player.getLook( 1F ).normalize();
            
            Vec3 vDelta = worldObj.getWorldVec3Pool().getVecFromPool( 
            		posX - player.posX, 
            		(posY + getEyeHeight()) - ( player.posY + player.getEyeHeight()), 
            		posZ - player.posZ );
            
            double dDist = vDelta.lengthVector();
            
            vDelta = vDelta.normalize();
            
            double dotDelta = vLook.dotProduct( vDelta );
            
            if ( dotDelta > 1D - 0.025D / dDist )
            {
            	return player.canEntityBeSeen( this );
            }
        }
        
        return false;
    }

    public void dropCarriedBlock()
    {
        int iCarriedBlockID = getCarried();
        
        if ( iCarriedBlockID != 0 )
        {
        	Block block = Block.blocksList[iCarriedBlockID];
        	
        	if ( block != null )
        	{
        		int iDamageDropped = block.damageDropped( getCarryingData() );
        		
	        	entityDropItem( new ItemStack( iCarriedBlockID, 1, iDamageDropped ), 0F );
	        	
		        setCarried( 0 );
		        setCarryingData( 0 );
        	}
        }
    }
    
    private boolean canPickUpBlock(int i, int j, int k)
    {    	
    	int iBlockID = worldObj.getBlockId( i, j, k );
    	
        if ( portableBlocks[iBlockID] )
        {
        	if ( !worldObj.isBlockNormalCube( i, j, k ) && iBlockID != Block.cactus.blockID )
        	{
        		// blocks like flowers and such can always be picked up regardless of surrounding blocks
        		
        		return true;
        	}
        	else
        	{
        		// check if the block is on a valid "corner" so that Endermen don't grab blocks that create odd deformities in the terrain
        		
        		int iNeighboringNonSolidBlocks = 0;
        		
        		if ( !doesBlockBlockPickingUp(i, j - 1, k) )
        		{
        			iNeighboringNonSolidBlocks++;
        		}
        		else
        		{
        			// we have a solid block beneath.  Check for others above (including at angles), so that blocks are removed to form slopes)
        			
        			for ( int iTempI = i - 1; iTempI <= i + 1; iTempI++ )
        			{
            			for ( int iTempK = k - 1; iTempK <= k + 1; iTempK++ )
            			{
            				if ( doesBlockBlockPickingUp(iTempI, j + 1, iTempK) )
            				{
            					return false;
            				}
            			}
        			}
        		}
        		
    			for ( int iFacing = 1; iFacing < 6; iFacing++ )
    			{
    				BlockPos targetPos = new BlockPos( i, j, k );
    				
    				targetPos.addFacingAsOffset(iFacing);
    				
    				if ( !doesBlockBlockPickingUp(targetPos.x, targetPos.y, targetPos.z) )
    				{
            			iNeighboringNonSolidBlocks++;        			
    				}
    			}
    			
    			if ( iNeighboringNonSolidBlocks >= 3 )
    			{
    				return true;
    			}
        	}
        }
        
    	return false;
    }
    
    private boolean doesBlockBlockPickingUp(int i, int j, int k)
    {
    	if ( worldObj.isAirBlock( i, j, k ) )
    	{
    		return false;
    	}
    	else if ( worldObj.isBlockNormalCube( i, j, k ) )
    	{
    		return true;
    	}
    	
    	int iBlockID = worldObj.getBlockId( i, j, k );
    	
    	Block block = Block.blocksList[iBlockID];
    	
        return !( block == null || block == Block.waterMoving || block == Block.waterStill || 
        	block == Block.lavaMoving || block == Block.lavaStill || 
        	block == Block.fire || block == BTWBlocks.stokedFire ||
        	block.blockMaterial.isReplaceable() || block.blockMaterial == Material.plants || 
        	block.blockMaterial == Material.leaves );
    }
    
    /*
     * Returns false if the Enderman entity was set to dead during the update
     */
    private boolean updateWithCarriedBlock()
    {
    	int iCarriedBlockID = getCarried();
    	
    	if ( worldObj.provider.dimensionId == 1 ) 
    	{
			// we're in the end dimension with a block, and should attempt to place it
			
            int i = MathHelper.floor_double( posX ) + rand.nextInt( 5 ) - 2;
            int j = MathHelper.floor_double( posY ) + rand.nextInt( 7 ) - 3;
            int k = MathHelper.floor_double( posZ ) + rand.nextInt( 5 ) - 2;
            
			int iWeight = getPlaceEndstoneWeight(i, j, k);
			
            if (rand.nextInt(maxEnstonePlaceWeight >> 9) < iWeight )
            {
		        worldObj.playAuxSFX( BTWEffectManager.ENDERMAN_PLACE_BLOCK_EFFECT_ID, i, j, k, iCarriedBlockID + ( getCarryingData() << 12 ) );
		        
                worldObj.setBlockAndMetadataWithNotify( i, j, k, getCarried(), getCarryingData());
                
                setCarried(0);
            }
    	}
    	else
    	{
			// 	eventually the enderman should teleport away to the end with his block
			
    		if ( rand.nextInt( 2400 ) == 0 )
    		{
    			// play dimensional travel effects
    			
                int i = MathHelper.floor_double( posX );
                int j = MathHelper.floor_double( posY ) + 1;
                int k = MathHelper.floor_double( posZ );
                
		        worldObj.playAuxSFX( BTWEffectManager.ENDERMAN_CHANGE_DIMENSION_EFFECT_ID, i, j, k, 0 );
		        
                setDead();
                
                return false;
    		}
    	}
    	
    	return true;
    }
    
    /*
     * Returns false if the Enderman entity was set to dead during the update
     */
    private boolean updateWithoutCarriedBlock()
    {
        if ( rand.nextInt(20) == 0 )
        {
            int i = MathHelper.floor_double( ( posX - 3D ) + rand.nextDouble() * 6D );
            int j = MathHelper.floor_double( posY - 1D + rand.nextDouble() * 7D );
            int k = MathHelper.floor_double( ( posZ - 3D ) + rand.nextDouble() * 6D );
            
            int l1 = worldObj.getBlockId( i, j, k );

            if ( canPickUpBlock(i, j, k) )
            {
		        worldObj.playAuxSFX( BTWEffectManager.ENDERMAN_COLLECT_BLOCK_EFFECT_ID, i, j, k, l1 + ( worldObj.getBlockMetadata( i, j, k ) << 12 ) );
		        
                setCarried( worldObj.getBlockId( i, j, k ) );
                setCarryingData( worldObj.getBlockMetadata( i, j, k ) );
                worldObj.setBlockToAir( i, j, k );
            }
        }
        else if ( worldObj.provider.dimensionId == 1  )
        {
        	// Endermen in the end without a block in hand will eventually teleport back to the overworld
        	
    		if ( rand.nextInt( 9600 ) == 0 )
    		{
    			// play dimensional travel effects
    			
                int i = MathHelper.floor_double( posX );
                int j = MathHelper.floor_double( posY ) + 1;
                int k = MathHelper.floor_double( posZ );
                
		        worldObj.playAuxSFX( BTWEffectManager.ENDERMAN_CHANGE_DIMENSION_EFFECT_ID, i, j, k, 0 );
		        
                setDead();
                
                return false;
    		}
        }
        
        return true;
    }
    
    private int getPlaceEndstoneWeight(int i, int j, int k)
    {
    	int iNumValidNeighbors = 0;
    	
    	if ( worldObj.isAirBlock( i, j, k ) )
    	{
    		if ( worldObj.doesBlockHaveSolidTopSurface( i, j - 1, k ) )
    		{
    			iNumValidNeighbors++;
    		}
    		
			for ( int iFacing = 1; iFacing < 6; iFacing++ )
			{
				BlockPos targetPos = new BlockPos( i, j, k );
				
				targetPos.addFacingAsOffset(iFacing);
				
				int iTargetBlockID = worldObj.getBlockId(targetPos.x, targetPos.y, targetPos.z);
				
				if ( iTargetBlockID == Block.whiteStone.blockID )
				{
					// we can place endstone if there is endstone on any other side
					
					iNumValidNeighbors++;
				}
			}
    	}
    	
    	return endstonePlacementNeighborWeights[iNumValidNeighbors];
    }
    
    protected void angerNearbyEndermen(EntityPlayer targetPlayer)
    {
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(32D, 32D, 32D));
        
        Iterator iterator = list.iterator();

        while ( iterator.hasNext() )
        {
            Entity tempEntity = (Entity)iterator.next();

            if ( tempEntity instanceof EndermanEntity)
            {
                EndermanEntity enderman = (EndermanEntity)tempEntity;
                
                if ( enderman.entityToAttack == null )
                {
                	enderman.entityToAttack = targetPlayer;
                	enderman.setScreaming(true);
                }
            }
        }
    }
    
    protected void emitParticles()
    {
	    for ( int iTempCount = 0; iTempCount < 2; iTempCount++ )
	    {
	        worldObj.spawnParticle( "portal", 
	        	posX + ( rand.nextDouble() - 0.5D ) * width, 
	        	posY + rand.nextDouble() * height - 0.25D, 
	        	posZ + ( rand.nextDouble() - 0.5D ) * width, 
	        	( rand.nextDouble() - 0.5D ) * 2.0D, -rand.nextDouble(), 
	        	( rand.nextDouble() - 0.5D ) * 2.0D );
	    }
    }
    
    protected void panic()
    {
        entityToAttack = null;
        setScreaming( false );
        shouldBeScreaming = false;
        
        teleportRandomly();
    }
}
