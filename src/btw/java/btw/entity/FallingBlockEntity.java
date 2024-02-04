// FCMOD

package btw.entity;

import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.Iterator;

public class FallingBlockEntity extends EntityFallingSand
{
	protected boolean hasBlockBrokenOnLand = false;
	
	// copies of parent class variables due to being private	
	protected boolean hurtsEntities = false;
	protected float hurtsAmount = 2F;
	protected int hurtsMaxDamage = 40;

    public FallingBlockEntity(World world )
    {
    	super( world );
    }
	
    public FallingBlockEntity(World world, double dPosX, double dPosY, double dPosZ, int iBlockID, int iBlockMetadata )
    {
        super( world, dPosX, dPosY, dPosZ, iBlockID, iBlockMetadata );
    }
    
    @Override
    protected void readEntityFromNBT( NBTTagCompound tag )
    {
    	super.readEntityFromNBT( tag );
    	
        if ( tag.hasKey( "HurtEntities" ) )
        {
            hurtsEntities = tag.getBoolean("HurtEntities");
            hurtsAmount = tag.getFloat("FallHurtAmount");
            hurtsMaxDamage = tag.getInteger("FallHurtMax");
        }
        else if (this.blockID == Block.anvil.blockID)
        {
            hurtsEntities = true;
        }
    }
    
    @Override
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        
        fallTime++;
        
        motionY -= 0.03999999910593033D;
        
        moveEntity(this.motionX, this.motionY, this.motionZ);
        
        motionX *= 0.9800000190734863D;
        motionY *= 0.9800000190734863D;
        motionZ *= 0.9800000190734863D;

        if ( !worldObj.isRemote )
        {
            int i = MathHelper.floor_double( posX );
            int j = MathHelper.floor_double( posY );
            int k = MathHelper.floor_double( posZ );

            if ( fallTime == 1 )
            {
                if ( worldObj.getBlockId( i, j, k ) != blockID )
                {
                    setDead();
                    
                    return;
                }

                worldObj.setBlockToAir( i, j, k );
            }

            if ( onGround )
            {
                motionX *= 0.699999988079071D;
                motionZ *= 0.699999988079071D;
                motionY *= -0.5D;

                int iBlockBelowID = worldObj.getBlockId( i, j - 1, k );
                Block blockBelow = Block.blocksList[iBlockBelowID];
                
                if ( blockBelow != null && blockBelow.canBeCrushedByFallingEntity(worldObj, i, j - 1, k, this) )
                {
            		blockBelow.onCrushedByFallingEntity(worldObj, i, j - 1, k, this);
            		
            		worldObj.setBlockToAir( i, j - 1, k );
                }
                else if ( worldObj.getBlockId( i, j, k ) != Block.pistonMoving.blockID )
                {
                    setDead();

                    if ( attemptToReplaceBlockAtPosition(i, j, k) )
                    {
                        Block.blocksList[this.blockID].onFinishFalling( worldObj, i, j, k, metadata );
                        
                        // FCNOTE: Tile entity handling has been removed here, since it is unused
                    }
                    else if ( shouldDropItem && !hasBlockBrokenOnLand)
                    {
                    	int iDestinationBlockID = worldObj.getBlockId( i, j, k );
                    	
                    	if ( iDestinationBlockID != 0 )
                    	{
                    		if ( Block.blocksList[iDestinationBlockID].attemptToCombineWithFallingEntity(worldObj, i, j, k, this) )
                    		{
                    			return;
                    		}
                    	}
                    	
                    	Block.blocksList[blockID].onBlockDestroyedLandingFromFall(worldObj, i, j, k, metadata);
                    }
                }
            }
            else if ( fallTime > 100 && ( j < 1 || j > 256 ) || fallTime > 600 )
            {
                if ( shouldDropItem )
                {
                    entityDropItem( new ItemStack( blockID, 1, Block.blocksList[blockID].damageDropped( metadata ) ), 0.0F );
                }

                setDead();
            }
        }
        
        if ( isEntityAlive() )
        {
        	Block.blocksList[this.blockID].onFallingUpdate(this);
        }
    }
    
    @Override
    protected void fall( float fFallDistance )
    {
        if (hurtsEntities)
        {
            int iFallDamage = MathHelper.ceiling_float_int( fFallDistance - 1F );

            if ( iFallDamage > 0 )
            {
                ArrayList entityList = new ArrayList( worldObj.getEntitiesWithinAABBExcludingEntity( this, boundingBox ));
                
                Iterator entityIterator = entityList.iterator();

                while ( entityIterator.hasNext() )
                {
                    Entity tempEntity = (Entity)entityIterator.next();
                    
                    tempEntity.attackEntityFrom( DamageSource.fallingBlock, 
                    	Math.min(MathHelper.floor_float((float)iFallDamage * hurtsAmount), hurtsMaxDamage));
                }
            }
        }
        
    	Block block = Block.blocksList[blockID];
    	
    	if ( block != null )
    	{
    		if ( !block.onFinishedFalling(this, fFallDistance) )
    		{
                hasBlockBrokenOnLand = true;
    		}
    	}
    }
    
    @Override
    public void setIsAnvil( boolean bIsAnvil )
    {
    	super.setIsAnvil( bIsAnvil );

        hurtsEntities = bIsAnvil;
    }
    
    //------------- Class Specific Methods ------------//
    
    private boolean attemptToReplaceBlockAtPosition(int i, int j, int k)
    {
    	if (!hasBlockBrokenOnLand &&
            canReplaceBlockAtPosition(i, j, k) &&
            !Block.blocksList[this.blockID].canFallIntoBlockAtPos(this.worldObj, i, j - 1, k) )
    	{
        	Block destBlock = Block.blocksList[worldObj.getBlockId( i, j, k )];

        	if ( destBlock != null )
        	{
        		destBlock.onCrushedByFallingEntity(worldObj, i, j, k, this);
        	}
        	
    		return worldObj.setBlock( i, j, k, this.blockID, this.metadata, 3 );
    	}
    	
    	return false;
    }
    
    private boolean canReplaceBlockAtPosition(int i, int j, int k)
    {
    	if ( worldObj.canPlaceEntityOnSide( this.blockID, i, j, k, true, 1, (Entity)null, (ItemStack)null) )
    	{
    		return true;
    	}
    	
    	Block destBlock = Block.blocksList[worldObj.getBlockId( i, j, k )];
    	
    	if ( destBlock != null && destBlock.canBeCrushedByFallingEntity(worldObj, i, j, k, this) )
		{
    		return true;
		}
    	
    	return false;
    }
}
