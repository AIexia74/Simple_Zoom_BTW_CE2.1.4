// FCMOD

package btw.block.tileentity;

import btw.block.blocks.InfernalEnchanterBlock;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;

public class InfernalEnchanterTileEntity extends TileEntity
{
	private int timeSinceLastCandleFlame[];
	public boolean playerNear;
	
	private static final int MAX_TIME_BETWEEN_FLAME_UPDATES = 10;
	
	public InfernalEnchanterTileEntity()
	{
		super();

		timeSinceLastCandleFlame = new int[4];
		
		for ( int iTemp = 0; iTemp < 4; iTemp++ )
		{
			timeSinceLastCandleFlame[iTemp] = 0;
		}

		playerNear = false;
	}
	
    @Override
    public void updateEntity()
    {
        super.updateEntity();
    
        // note that this is done on the client as well, since it's entirely display related
        
        EntityPlayer entityplayer = worldObj.getClosestPlayer((float)xCoord + 0.5F, (float)yCoord + 0.5F, (float)zCoord + 0.5F, 4.5D );
        
        if (entityplayer != null)
        {
        	if ( !playerNear)
        	{
        		lightCandles();

				playerNear = true;
        	}
        	else
        	{
        		updateCandleFlames();
        	}
        }
        else
        {
			playerNear = false;
        }
    }
    
    //************* Class Specific Methods ************//
    
    private void lightCandles()
    {
    	for ( int iTemp = 0; iTemp < 4; iTemp++ )
    	{
    		displayCandleFlameAtIndex(iTemp);
    	}
    	
        worldObj.playSoundEffect( xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, "mob.ghast.fireball", 1.0F, worldObj.rand.nextFloat() * 0.4F + 0.8F );
    }
    
    private void updateCandleFlames()
    {
    	for ( int iTemp = 0; iTemp < 4; iTemp++ )
    	{
    		timeSinceLastCandleFlame[iTemp]++;
    		
    		if (timeSinceLastCandleFlame[iTemp] > MAX_TIME_BETWEEN_FLAME_UPDATES || worldObj.rand.nextInt(5) == 0 )
    		{   		    		
    			displayCandleFlameAtIndex(iTemp);
    		}
    	}
    }
    
    private void displayCandleFlameAtIndex(int iCandleIndex)
    {
        double flameX = xCoord + ( 2.0D / 16.0D );
        double flameY = yCoord + InfernalEnchanterBlock.BLOCK_HEIGHT + InfernalEnchanterBlock.CANDLE_HEIGHT + 0.175F;
        double flameZ = zCoord  + ( 2.0D / 16.0D );
        
        if ( iCandleIndex == 1 || iCandleIndex == 3 )
        {
        	flameX = xCoord + ( 14.0D / 16.0D );
        }
        
        if ( iCandleIndex == 2 || iCandleIndex == 3 )
        {
        	flameZ = zCoord + ( 14.0D / 16.0D );
        }
        
        displayCandleFlameAtLoc(flameX, flameY, flameZ);

		timeSinceLastCandleFlame[iCandleIndex] = 0;
    }

	private void displayCandleFlameAtLoc(double xCoord, double yCoord, double zCoord)
	{
        worldObj.spawnParticle( "smoke", xCoord, yCoord, zCoord, 0.0D, 0.0D, 0.0D);
        worldObj.spawnParticle( "flame", xCoord, yCoord, zCoord, 0.0D, 0.0D, 0.0D);
	}
	
}
