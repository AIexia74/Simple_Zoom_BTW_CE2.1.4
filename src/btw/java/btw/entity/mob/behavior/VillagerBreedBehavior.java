// FCMOD

package btw.entity.mob.behavior;

import btw.client.fx.BTWEffectManager;
import btw.entity.mob.villager.VillagerEntity;
import net.minecraft.src.EntityAIBase;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VillagerBreedBehavior extends EntityAIBase
{
	static final double DISTANCE_TO_CHECK_FOR_MATE = 8F;

	private VillagerEntity villager;
	private VillagerEntity mate;
	private World world;

	private int spawnBabyDelay = 0;
	private int thrustDelay = 0;

	public VillagerBreedBehavior(VillagerEntity villager)
	{
		this.villager = villager;
		this.world = villager.worldObj;
		setMutexBits(3);
	}

	public boolean shouldExecute()
	{
		if (this.villager.getInLove() <= 0)
		{
			return false;
		}
		else
		{
			this.mate = getNearbyMate();

			return this.mate != null;
		}
	}

	public void resetTask()
	{
		this.mate = null;
		this.spawnBabyDelay = 0;
	}

	public boolean continueExecuting()
	{
		return this.mate != null && this.mate.isEntityAlive() && this.mate.getInLove() > 0 &&
				this.spawnBabyDelay < 100;
	}

	public void updateTask()
	{
		this.villager.getLookHelper().setLookPositionWithEntity(this.mate, 10F, 30F);

		if (this.villager.getDistanceSqToEntity(this.mate) > 4D)
		{
			this.villager.getNavigator().tryMoveToEntityLiving(this.mate, 0.25F);

			this.spawnBabyDelay = 0;

			this.thrustDelay = this.villager.rand.nextInt(5) + 15;
		}
		else
		{
			this.spawnBabyDelay++;

			if (this.spawnBabyDelay == 100)
			{
				giveBirth();
			}
			else
			{
				this.thrustDelay--;

				if (this.thrustDelay <= 0)
				{
					this.world.playSoundAtEntity(this.villager, 
							"random.classic_hurt", 1F +  this.villager.rand.nextFloat() * 0.25F, 
							this.villager.getSoundPitch() * 2F);

					this.thrustDelay = this.villager.rand.nextInt(5) + 15;

					if (this.villager.onGround)
					{
						this.villager.jump();                		
					}
				}
			}
		}        

	}

	private void giveBirth()
	{
		int babyProfession = this.villager.getProfessionFromClass();

		// 50% chance of baby inheriting profession from other parent

		if (this.villager.rand.nextInt(2) == 0)
		{
			babyProfession = this.mate.getProfessionFromClass();
		}
		
		// 33% chance of baby being of same "caste" but different profession

		if (this.villager.rand.nextInt(3) == 0)
		{
			int casteID = VillagerEntity.getCasteFromProfession(babyProfession);
			ArrayList<Integer> caste = VillagerEntity.casteMap.get(casteID);
			
			ArrayList<Integer> casteCopy = new ArrayList();
			
			for (int i : caste) {
				casteCopy.add(i);
			}
			
			//Cast to object ensures it removes the entry not the index
			casteCopy.remove((Object) babyProfession);
			
			if (casteCopy.size() > 0) {
				int index = this.villager.rand.nextInt(casteCopy.size());
				
				babyProfession = casteCopy.get(index);
			}
		}

		VillagerEntity babyVillager = this.villager.spawnBabyVillagerWithProfession(this.mate, babyProfession);

		this.mate.setGrowingAge(6000);
		this.villager.setGrowingAge(6000);

		this.mate.setInLove(0);
		this.villager.setInLove(0);

		babyVillager.setGrowingAge(-babyVillager.getTicksForChildToGrow());

		babyVillager.setLocationAndAngles(this.villager.posX, this.villager.posY, this.villager.posZ, 
				0F, 0F);

		this.world.spawnEntityInWorld(babyVillager);

		this.world.setEntityState(babyVillager, (byte)12);

		// birthing effects

		this.world.playAuxSFX(BTWEffectManager.ANIMAL_BIRTHING_EFFECT_ID,
				MathHelper.floor_double(babyVillager.posX),
				MathHelper.floor_double(babyVillager.posY), 
				MathHelper.floor_double(babyVillager.posZ), 
				0);
	}

	private VillagerEntity getNearbyMate()
	{        
		List potentialMateList = this.world.getEntitiesWithinAABB(VillagerEntity.class,
				this.villager.boundingBox.expand(DISTANCE_TO_CHECK_FOR_MATE, DISTANCE_TO_CHECK_FOR_MATE, DISTANCE_TO_CHECK_FOR_MATE));

		Iterator mateIterator = potentialMateList.iterator();

		VillagerEntity foundMate = null;

		while (mateIterator.hasNext())
		{
			VillagerEntity tempVillager = (VillagerEntity)mateIterator.next();

			if (canMateWith(tempVillager))
			{
				return tempVillager;
			}
		}

		return null;
	}

	private boolean canMateWith(VillagerEntity targetVillager)
	{
		if (targetVillager != this.villager && targetVillager.getInLove() > 0 &&
			!targetVillager.isLivingDead)
		{
			return true;
		}

		return false;
	}
}