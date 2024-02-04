// FCMOD

package btw.entity.mob;

import btw.block.tileentity.beacon.BeaconTileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class JungleSpiderEntity extends SpiderEntity
{
    public JungleSpiderEntity(World world )
    {
        super( world );
        
        texture = "/btwmodtex/fcSpiderJungle.png";
        
        setSize( 0.7F, 0.5F );
    }

    @Override
    public int getMaxHealth()
    {
        return 10;
    }

    @Override
    public boolean attackEntityAsMob( Entity targetEntity )
    {
        if ( super.attackEntityAsMob( targetEntity ) )
        {
            if ( targetEntity instanceof EntityLiving)
            {
                byte iPoisonDurationInSeconds = 0;

                if (this.worldObj.difficultySetting > 1)
                {
                    if (this.worldObj.difficultySetting == 2)
                    {
                        iPoisonDurationInSeconds = 3;
                    }
                    else if (this.worldObj.difficultySetting == 3)
                    {
                        iPoisonDurationInSeconds = 7;
                    }
                }
                else
                {
                	iPoisonDurationInSeconds = 1;
                }

                if (iPoisonDurationInSeconds > 0)
                {
                    ((EntityLiving)targetEntity).addPotionEffect( new PotionEffect( Potion.poison.id, iPoisonDurationInSeconds * 20, 0 ) );
                    
                    int hungerDuration;
                    
                    if (this.worldObj.getDifficulty().shouldReduceJungleSpiderFoodPoisoning()) {
                        hungerDuration = 10;
                    }
                    else {
                        hungerDuration = 30;
                    }
                    
                    ((EntityLiving)targetEntity).addPotionEffect( new PotionEffect( Potion.hunger.id, hungerDuration * 20, 0 ) );
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void initCreature() 
    {
    	// override so you don't get jockeys    	
    }
    
    @Override
	public boolean doesLightAffectAggessiveness()
	{
		return false;
	}
    
    @Override
    public boolean isAlwaysNeutral() {
        return !this.worldObj.getDifficulty().areJungleSpidersHostile();
    }
    
    @Override
    protected boolean isValidLightLevel()
    {
    	// spawn in any light level
    	
    	return true;
    }
    
    @Override
    public boolean getCanSpawnHere() {
        if (worldObj.getAmbientBeaconEffectAtLocation(BeaconTileEntity.JUNGLE_SPIDER_REPELLENT.EFFECT_NAME, (int) posX, (int) posY, (int) posZ) > 0) {
            return false;
        }
        
        // limit spawns to above and around sea level
        if ((int) posY >= worldObj.provider.getAverageGroundLevel() - 5) {
            return super.getCanSpawnHere();
        }
        
        return false;
    }
    
    @Override
    public String getEntityName()
    {
    	return "Jungle Spider";
    }
    
    protected boolean canSpawnOnBlock(int x, int y, int z) {
        Block block = Block.blocksList[worldObj.getBlockId(x, y, z)];
        return block != null && block.isLeafBlock(worldObj, x, y, z);
    }

    @Override
    public float getBlockPathWeight( int i, int j, int k )
    {
    	// jungle spiders love hanging out (and spawning) on leaves
    	
    	if ( worldObj.getBlockId(i, j - 1, k) == Block.leaves.blockID )
    	{
    		return 10F;
    	}
    	
        return super.getBlockPathWeight(i, j, k);
    }
    
    @Override
    protected float getSoundPitch()
    {
        return ( rand.nextFloat() - rand.nextFloat() ) * 0.2F + 0.7F;
    }
    
    @Override
    protected float getSoundVolume()
    {
        return 0.25F;
    }
    
    @Override
    public int getTalkInterval()
    {
        return 80 + rand.nextInt( 240 ); // default is 80
    }
    
    @Override
	public boolean doEyesGlow()
	{
		return false;
	}
    
    @Override
    public boolean attackEntityFrom( DamageSource damageSource, int iDamageAmount )
    {
    	// make immune to fall damage 
    	
        if ( damageSource == DamageSource.fall )
        {
        	return false;
        }
        
    	return super.attackEntityFrom( damageSource, iDamageAmount );
    }

    @Override
    protected void dropFewItems( boolean bKilledByPlayer, int iLootingModifier )
    {
        super.dropFewItems( bKilledByPlayer, iLootingModifier );

        if ( rand.nextInt( 16 ) - ( iLootingModifier << 1 ) <= 0 )
        {
        	// drop venom sacks
        	
            dropItem( Item.fermentedSpiderEye.itemID, 1 );
        }
    }
    
    @Override
    protected boolean dropsSpiderEyes()
    {
    	return false;
    }
    
    @Override
	protected void checkForSpiderSkeletonMounting()
	{
	}
	
	//----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public float spiderScaleAmount()
    {
        return 0.7F;
    }
}
