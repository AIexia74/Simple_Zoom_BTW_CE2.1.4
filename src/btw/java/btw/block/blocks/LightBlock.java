// FCMOD

package btw.block.blocks;

import btw.block.BTWBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class LightBlock extends Block
{
    private final static int LIGHT_BULB_TICK_RATE = 2;
    
    private boolean glowing;
    
    public LightBlock(int iBlockID, boolean bGlowing )
    {
    	super( iBlockID, Material.glass );

        setHardness( 0.4F );
        setPicksEffectiveOn();
        
        setStepSound( Block.soundGlassFootstep );
        
        setUnlocalizedName( "fcBlockLightBlock" );

        glowing = bGlowing;
        
        if ( bGlowing )
        {
        	setLightValue( 1F );
        }
        else
        {
            setCreativeTab( CreativeTabs.tabRedstone );
        }
        
        setTickRandomly( true );
    }
    
	@Override
    public int tickRate( World world )
    {
        return LIGHT_BULB_TICK_RATE;
    }    

	@Override
    public void onBlockAdded( World world, int i, int j, int k )
    {
    	world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
    }
    
	@Override
    public int idDropped( int iMetaData, Random random, int iFortuneModifier )
    {
    	// override the dropped id so we won't get dropped lit lightbulbs
    	
        return BTWBlocks.lightBlockOff.blockID;
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
    public void updateTick( World world, int i, int j, int k, Random random )
    {
        boolean bPowered = world.isBlockIndirectlyGettingPowered(i, j, k);

        if ( bPowered )
        {
        	if ( !isLightOn(world, i, j, k) )
        	{
        		lightBulbTurnOn(world, i, j, k);
        		
        		return;
        	}
        }
    	else
    	{
    		if ( isLightOn(world, i, j, k) )
    		{
    			lightBulbTurnOff(world, i, j, k);
    			
        		return;
    		}    		
    	}
    }
    
	@Override
    public void randomUpdateTick(World world, int i, int j, int k, Random rand)
    {
		if ( !isCurrentStateValid(world, i, j, k) )
		{
			// verify we have a tick already scheduled to prevent jams on chunk load
			
			if ( !world.isUpdateScheduledForBlock(i, j, k, blockID) )
			{
		        world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );        
			}
		}
    }
	
	public boolean isCurrentStateValid(World world, int i, int j, int k)
	{
        boolean bPowered = world.isBlockIndirectlyGettingPowered( i, j, k );

        return bPowered == isLightOn(world, i, j, k);
	}
	
	@Override
    public void onNeighborBlockChange(World world, int i, int j, int k, int l)
    {
		if (!isCurrentStateValid(world, i, j, k) &&
            !world.isUpdatePendingThisTickForBlock(i, j, k, blockID) )
		{
			world.scheduleBlockUpdate( i, j, k, blockID, tickRate( world ) );
		}
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
	public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
	{
		return bIgnoreTransparency;
	}
	
	//------------- Class Specific Methods ------------//
    
    private void lightBulbTurnOn(World world, int i, int j, int k)
    {
        world.setBlockWithNotify( i, j, k, BTWBlocks.lightBlockOn.blockID );
    }
    
    private void lightBulbTurnOff(World world, int i, int j, int k)
    {
        world.setBlockWithNotify( i, j, k, BTWBlocks.lightBlockOff.blockID );
   }
    
    public boolean isLightOn(World world, int i, int j, int k)
    {
    	return world.getBlockId(i, j, k) == BTWBlocks.lightBlockOn.blockID;
    }
    
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        if (glowing)
        {
            blockIcon = register.registerIcon( "fcBlockLightBlock_lit" );
        }
        else
        {
            blockIcon = register.registerIcon( "fcBlockLightBlock" );
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public float getBlockBrightness( IBlockAccess iblockaccess, int i, int j, int k )
    {		
		if ( blockID == BTWBlocks.lightBlockOn.blockID )
		{
			// not sure what max brightness is, but this certainly gets the job done :)
			
			return 100F;			
		}
		else
		{
			return iblockaccess.getLightBrightness( i, j, k );
		}
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked( World world, int i, int j, int k )
    {
        return idDropped( world.getBlockMetadata( i, j, k ), world.rand, 0 );
    }
}
