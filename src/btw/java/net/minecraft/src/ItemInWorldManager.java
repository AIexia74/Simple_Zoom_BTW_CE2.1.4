package net.minecraft.src;

import btw.world.util.WorldUtils;

public class ItemInWorldManager
{
    /** The world object that this object is connected to. */
    public World theWorld;

    /** The EntityPlayerMP object that this object is connected to. */
    public EntityPlayerMP thisPlayerMP;
    private EnumGameType gameType;

    /** True if the player is destroying a block */
    private boolean isDestroyingBlock;
    private int initialDamage;
    private int partiallyDestroyedBlockX;
    private int partiallyDestroyedBlockY;
    private int partiallyDestroyedBlockZ;
    private int curblockDamage;

    /**
     * Set to true when the "finished destroying block" packet is received but the block wasn't fully damaged yet. The
     * block will not be destroyed while this is false.
     */
    private boolean receivedFinishDiggingPacket;
    private int posX;
    private int posY;
    private int posZ;
    private int field_73093_n;
    private int durabilityRemainingOnBlock;
    // FCMOD: Added
    private int harvestingFromFacing;
    // END FCMOD

    public ItemInWorldManager(World par1World)
    {
        this.gameType = EnumGameType.NOT_SET;
        this.durabilityRemainingOnBlock = -1;
        this.theWorld = par1World;
    }

    public void setGameType(EnumGameType par1EnumGameType)
    {
        this.gameType = par1EnumGameType;
        par1EnumGameType.configurePlayerCapabilities(this.thisPlayerMP.capabilities);
        this.thisPlayerMP.sendPlayerAbilities();
    }

    public EnumGameType getGameType()
    {
        return this.gameType;
    }

    /**
     * Get if we are in creative game mode.
     */
    public boolean isCreative()
    {
        return this.gameType.isCreative();
    }

    /**
     * if the gameType is currently NOT_SET then change it to par1
     */
    public void initializeGameType(EnumGameType par1EnumGameType)
    {
        if (this.gameType == EnumGameType.NOT_SET)
        {
            this.gameType = par1EnumGameType;
        }

        this.setGameType(this.gameType);
    }

    public void updateBlockRemoving()
    {
        ++this.curblockDamage;
        int var1;
        float var4;
        int var5;

        if (this.receivedFinishDiggingPacket)
        {
            var1 = this.curblockDamage - this.field_73093_n;
            int var2 = this.theWorld.getBlockId(this.posX, this.posY, this.posZ);

            if (var2 == 0)
            {
                this.receivedFinishDiggingPacket = false;
            }
            else
            {
                Block var3 = Block.blocksList[var2];
                var4 = var3.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, this.posX, this.posY, this.posZ) * (float)(var1 + 1);
                var5 = (int)(var4 * 10.0F);

                if (var5 != this.durabilityRemainingOnBlock)
                {
                    this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.entityId, this.posX, this.posY, this.posZ, var5);
                    this.durabilityRemainingOnBlock = var5;
                }

                if (var4 >= 1.0F)
                {
                    this.receivedFinishDiggingPacket = false;
                    this.tryHarvestBlock(this.posX, this.posY, this.posZ, this.harvestingFromFacing);
                }
            }
        }
        else if (this.isDestroyingBlock)
        {
            var1 = this.theWorld.getBlockId(this.partiallyDestroyedBlockX, this.partiallyDestroyedBlockY, this.partiallyDestroyedBlockZ);
            Block var6 = Block.blocksList[var1];

            if (var6 == null)
            {
                this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.entityId, this.partiallyDestroyedBlockX, this.partiallyDestroyedBlockY, this.partiallyDestroyedBlockZ, -1);
                this.durabilityRemainingOnBlock = -1;
                this.isDestroyingBlock = false;
            }
            else
            {
                int var7 = this.curblockDamage - this.initialDamage;
                var4 = var6.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, this.partiallyDestroyedBlockX, this.partiallyDestroyedBlockY, this.partiallyDestroyedBlockZ) * (float)(var7 + 1);
                var5 = (int)(var4 * 10.0F);

                if (var5 != this.durabilityRemainingOnBlock)
                {
                    this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.entityId, this.partiallyDestroyedBlockX, this.partiallyDestroyedBlockY, this.partiallyDestroyedBlockZ, var5);
                    this.durabilityRemainingOnBlock = var5;
                }
            }
        }
    }

    /**
     * if not creative, it calls destroyBlockInWorldPartially untill the block is broken first. par4 is the specific
     * side. tryHarvestBlock can also be the result of this call
     */
    public void onBlockClicked(int par1, int par2, int par3, int par4)
    {
        if (!this.gameType.isAdventure() || this.thisPlayerMP.canCurrentToolHarvestBlock(par1, par2, par3))
        {
            if (this.isCreative())
            {
                if (!this.theWorld.extinguishFire((EntityPlayer)null, par1, par2, par3, par4))
                {
                    this.tryHarvestBlock(par1, par2, par3, par4);
                }
            }
            else
            {
                this.initialDamage = this.curblockDamage;
                float var5 = 1.0F;
                int var6 = this.theWorld.getBlockId(par1, par2, par3);

                if (var6 > 0)
                {
                    Block.blocksList[var6].onBlockClicked(this.theWorld, par1, par2, par3, this.thisPlayerMP);
                    var5 = Block.blocksList[var6].getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, par1, par2, par3);
                }

                if (var6 > 0 && var5 >= 1.0F)
                {
                    this.tryHarvestBlock(par1, par2, par3, par4);
                }
                else
                {
                    this.isDestroyingBlock = true;
                    this.partiallyDestroyedBlockX = par1;
                    this.partiallyDestroyedBlockY = par2;
                    this.partiallyDestroyedBlockZ = par3;
                    int var7 = (int)(var5 * 10.0F);
                    this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.entityId, par1, par2, par3, var7);
                    this.durabilityRemainingOnBlock = var7;
                }
            }
        }
    }

    public void uncheckedTryHarvestBlock(int par1, int par2, int par3 ) { uncheckedTryHarvestBlock( par1, par2, par3, 1 ); }

    public void uncheckedTryHarvestBlock(int par1, int par2, int par3, int iFromFacing)
    {
        if (par1 == this.partiallyDestroyedBlockX && par2 == this.partiallyDestroyedBlockY && par3 == this.partiallyDestroyedBlockZ)
        {
            int var4 = this.curblockDamage - this.initialDamage;
            int var5 = this.theWorld.getBlockId(par1, par2, par3);

            if (var5 != 0)
            {
                Block var6 = Block.blocksList[var5];
                float var7 = var6.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, par1, par2, par3) * (float)(var4 + 1);

                if (var7 >= 0.7F)
                {
                    this.isDestroyingBlock = false;
                    this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.entityId, par1, par2, par3, -1);
                    this.tryHarvestBlock(par1, par2, par3, iFromFacing);
                }
                else if (!this.receivedFinishDiggingPacket)
                {
                    this.isDestroyingBlock = false;
                    this.receivedFinishDiggingPacket = true;
                    this.posX = par1;
                    this.posY = par2;
                    this.posZ = par3;
                    this.field_73093_n = this.initialDamage;
                    // FCMOD: Added
                    harvestingFromFacing = iFromFacing;
                    // END FCMOD
                }
            }
        }
    }

    /**
     * note: this ignores the pars passed in and continues to destroy the onClickedBlock
     */
    public void cancelDestroyingBlock(int par1, int par2, int par3)
    {
        this.isDestroyingBlock = false;
        this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.entityId, this.partiallyDestroyedBlockX, this.partiallyDestroyedBlockY, this.partiallyDestroyedBlockZ, -1);
    }

    /**
     * Removes a block and triggers the appropriate events
     */
    private boolean removeBlock(int par1, int par2, int par3)
    {
        Block var4 = Block.blocksList[this.theWorld.getBlockId(par1, par2, par3)];
        int var5 = this.theWorld.getBlockMetadata(par1, par2, par3);

        if (var4 != null)
        {
            var4.onBlockHarvested(this.theWorld, par1, par2, par3, var5, this.thisPlayerMP);
        }

        boolean var6 = this.theWorld.setBlockToAir(par1, par2, par3);

        if (var4 != null && var6)
        {
            var4.onBlockDestroyedByPlayer(this.theWorld, par1, par2, par3, var5);
            
            // FCMOD: Panick animals when blocks are destroyed near them
            theWorld.notifyNearbyAnimalsOfPlayerBlockAddOrRemove(thisPlayerMP, var4, par1, par2, par3);
            // END FCMOD
        }

        return var6;
    }

    /**
     * Attempts to right-click use an item by the given EntityPlayer in the given World
     */
    public boolean tryUseItem(EntityPlayer par1EntityPlayer, World par2World, ItemStack par3ItemStack)
    {
        int var4 = par3ItemStack.stackSize;
        int var5 = par3ItemStack.getItemDamage();
        ItemStack var6 = par3ItemStack.useItemRightClick(par2World, par1EntityPlayer);

        if (var6 == par3ItemStack && (var6 == null || var6.stackSize == var4 && var6.getMaxItemUseDuration() <= 0 && var6.getItemDamage() == var5))
        {
            return false;
        }
        else
        {
            par1EntityPlayer.inventory.mainInventory[par1EntityPlayer.inventory.currentItem] = var6;

            if (this.isCreative())
            {
                var6.stackSize = var4;

                if (var6.isItemStackDamageable())
                {
                    var6.setItemDamage(var5);
                }
            }

            if (var6.stackSize == 0)
            {
                par1EntityPlayer.inventory.mainInventory[par1EntityPlayer.inventory.currentItem] = null;
            }

            if (!par1EntityPlayer.isUsingItem())
            {
                ((EntityPlayerMP)par1EntityPlayer).sendContainerToPlayer(par1EntityPlayer.inventoryContainer);
            }

            return true;
        }
    }

    /**
     * Activate the clicked on block, otherwise use the held item. Args: player, world, itemStack, x, y, z, side,
     * xOffset, yOffset, zOffset
     */
    public boolean activateBlockOrUseItem(EntityPlayer par1EntityPlayer, World par2World, ItemStack par3ItemStack, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        int var11;

        if (!par1EntityPlayer.isSneaking() || par1EntityPlayer.getHeldItem() == null)
        {
            var11 = par2World.getBlockId(par4, par5, par6);

            if (var11 > 0 && Block.blocksList[var11].onBlockActivated(par2World, par4, par5, par6, par1EntityPlayer, par7, par8, par9, par10))
            {
                return true;
            }
        }

        if (par3ItemStack == null)
        {
            return false;
        }
        else if (this.isCreative())
        {
            var11 = par3ItemStack.getItemDamage();
            int var12 = par3ItemStack.stackSize;
            boolean var13 = par3ItemStack.tryPlaceItemIntoWorld(par1EntityPlayer, par2World, par4, par5, par6, par7, par8, par9, par10);
            par3ItemStack.setItemDamage(var11);
            par3ItemStack.stackSize = var12;
            return var13;
        }
        else
        {
            return par3ItemStack.tryPlaceItemIntoWorld(par1EntityPlayer, par2World, par4, par5, par6, par7, par8, par9, par10);
        }
    }

    /**
     * Sets the world instance.
     */
    public void setWorld(WorldServer par1WorldServer)
    {
        this.theWorld = par1WorldServer;
    }
    
    // FCMOD: Added
    
	// "overloaded" function required for DemoWorldManager
    public boolean tryHarvestBlock( int i, int j, int k ) { return tryHarvestBlock( i, j, k, 1 ); }
    public boolean tryHarvestBlock( int i, int j, int k, int iFromSide )
    {
        if ( gameType.isAdventure() && !thisPlayerMP.canCurrentToolHarvestBlock( i, j, k ) )
        {
            return false;
        }
        else if ( isCreative() )
        {
            int iBlockID = theWorld.getBlockId( i, j, k );
            int iBlockMetadata = theWorld.getBlockMetadata( i, j, k );
            
            theWorld.playAuxSFXAtEntity( thisPlayerMP, 2001, i, j, k, iBlockID + ( theWorld.getBlockMetadata( i, j, k ) << 12 ) );
            
            boolean bBlockRemoved = removeBlock( i, j, k );

            WorldUtils.sendPacketToPlayer(thisPlayerMP.playerNetServerHandler, new Packet53BlockChange(i, j, k, this.theWorld));

            return bBlockRemoved;
        }
        else
        {
    		return survivalTryHarvestBlock(i, j, k, iFromSide);
        }
    }
    
    public boolean survivalTryHarvestBlock(int i, int j, int k, int iFromSide)
    {
        int iBlockID = theWorld.getBlockId(i, j, k);
    	Block block = Block.blocksList[iBlockID];
    	
    	if ( block == null )
    	{
    		return false;
    	}
    	
        int iMetadata = theWorld.getBlockMetadata(i, j, k);
        
        theWorld.playAuxSFXAtEntity( thisPlayerMP, 2001, i, j, k, iBlockID + ( iMetadata << 12 ) );
        
        ItemStack currentStack = thisPlayerMP.getCurrentEquippedItem();
        
        boolean bRemovingBlock = true;
        boolean bConvertingBlock = false;
        
        boolean bHarvestingBlock = thisPlayerMP.canHarvestBlock( Block.blocksList[iBlockID], i, j, k );
        
        if ( !bHarvestingBlock )
        {
        	bConvertingBlock = block.canConvertBlock(currentStack, theWorld, i, j, k);
        	
        	if ( bConvertingBlock )
        	{
                bRemovingBlock = !( block.convertBlock(currentStack, theWorld, i, j, k, iFromSide) );
        	}
        }

        if ( bRemovingBlock )
        {
        	bRemovingBlock = removeBlock( i, j, k );

            if ( bRemovingBlock && !bConvertingBlock )
            {
    	        if ( bHarvestingBlock )
    	        {
    	            Block.blocksList[iBlockID].harvestBlock( theWorld, thisPlayerMP, i, j, k, iMetadata );
    	        }
    	        else 
    	        {
    	        	Block.blocksList[iBlockID].onBlockDestroyedWithImproperTool(theWorld, thisPlayerMP, i, j, k, iMetadata);
    	        }
            }
        }

        if ( currentStack != null )
        {
            currentStack.onBlockDestroyed( theWorld, iBlockID, i, j, k, thisPlayerMP );

            if ( currentStack.stackSize == 0 )
            {
                thisPlayerMP.destroyCurrentEquippedItem();
            }
        }

        return bRemovingBlock;
    }
    // END FCMOD
}