// FCMOD

package btw.item.items;

import btw.block.BTWBlocks;
import net.minecraft.src.*;

public class ChiselItemIron extends ChiselItem
{
    public ChiselItemIron(int iItemID )
    {
    	// the number of uses has to be less than roughly 15% of pick to prevent it being more 
    	// efficient when used to harvest diamonds
    	
        super( iItemID, EnumToolMaterial.IRON, 50 );
        
        efficiencyOnProperMaterial /= 6;
        
        setFilterableProperties(Item.FILTERABLE_NARROW);
        
        setUnlocalizedName( "fcItemChiselIron" );        
    }
    
    @Override
    public boolean getCanBePlacedAsBlock()
    {
    	return true;
    }
    
    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, int iBlockID, int i, int j, int k, EntityLiving usingEntity )
    {
    	// extra damage for stump to workbench conversion
    	
        if ( iBlockID == Block.wood.blockID && world.getBlockId( i, j, k ) == 
        	BTWBlocks.workStump.blockID )
        {
            stack.damageItem( 5, usingEntity );
            
            return true;
        }

        return super.onBlockDestroyed( stack, world, iBlockID, i, j, k, usingEntity );
    }
    
    @Override
    public boolean isDamagedInCrafting()
    {
    	// used to split stone into bricks, and bricks to loose stone
    	
    	return true;
    }
    
    @Override
    public void onUsedInCrafting(EntityPlayer player, ItemStack outputStack)
    {
    	playStoneSplitSoundOnPlayer(player);
    }
    
    @Override
    public void onBrokenInCrafting(EntityPlayer player)
    {
    	playBreakSoundOnPlayer(player);
    }

    @Override
    public boolean canToolStickInBlock(ItemStack stack, Block block, World world, int i, int j, int k)
    {
		if ( block.blockMaterial == Material.rock && 
			block.blockID != Block.bedrock.blockID )
		{
			// ensures chisel will stick in cobble, despite not being efficient vs. it
			
			return true;
		}
    	
		return super.canToolStickInBlock(stack, block, world, i, j, k);
    }
    
    //------------- Class Specific Methods ------------//
	
    static public void playStoneSplitSoundOnPlayer(EntityPlayer player)
    {
		if (player.timesCraftedThisTick == 0 )
		{
			// note: the playSound function for player both plays the sound locally on the client, and plays it remotely on the server without it being sent again to the same player
			
			player.playSound( "random.anvil_land", 0.5F, player.worldObj.rand.nextFloat() * 
				0.25F + 1.75F );
		}
    }
    
    static public void playBreakSoundOnPlayer(EntityPlayer player)
    {
		// note: the playSound function for player both plays the sound locally on the client, and plays it remotely on the server without it being sent again to the same player
    	
		player.playSound( "random.break", 0.8F, 0.8F + player.worldObj.rand.nextFloat() * 0.4F );
    }
    
	//----------- Client Side Functionality -----------//
}
