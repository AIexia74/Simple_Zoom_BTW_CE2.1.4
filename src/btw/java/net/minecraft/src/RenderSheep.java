package net.minecraft.src;

import btw.entity.mob.SheepEntity;
import com.prupe.mcpatcher.mal.resource.FakeResourceLocation;
import com.prupe.mcpatcher.mob.MobRandomizer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class RenderSheep extends RenderLiving
{
	private ModelBase woolModel;
	
	public RenderSheep(ModelBase par1ModelBase, ModelBase par2ModelBase, float par3)
	{
		super(par1ModelBase, par3);
		this.woolModel = par2ModelBase;
		this.setRenderPassModel(woolModel);
	}

	protected int setWoolColorAndRender(EntitySheep sheep, int par2, float par3)
	{
		if (par2 == 0)
		{
			if (!sheep.getSheared()) {
				this.renderPassModel = this.woolModel;
				this.renderPassModel.isChild = sheep.isChild();
				
				this.loadTexture(FakeResourceLocation.unwrap(MobRandomizer.randomTexture((Entity)sheep, FakeResourceLocation.wrap("/mob/sheep_fur.png"))));
				float var4 = 1.0F;
				int var5 = sheep.getFleeceColor();
				GL11.glColor3f(var4 * EntitySheep.fleeceColorTable[var5][0], var4 * EntitySheep.fleeceColorTable[var5][1], var4 * EntitySheep.fleeceColorTable[var5][2]);
				return 1;
			}
		}
		else {
			this.renderPassModel = this.mainModel;
			
			String furTexture = "/btwmodtex/fcSheepFurSheared.png";
			
			int hungerLevel = ((SheepEntity) sheep).getHungerLevel();
			
			if (hungerLevel == 1) {
				furTexture = "/btwmodtex/fcSheepFamishedFur.png";
			}
			else if (hungerLevel == 2) {
				furTexture = "/btwmodtex/fcSheepStarvingFur.png";
			}
			
			this.loadTexture(FakeResourceLocation.unwrap(MobRandomizer.randomTexture((Entity)sheep, FakeResourceLocation.wrap(furTexture))));
			float var4 = 1.0F;
			int colorIndex;
			
			if (sheep.getSheared()) {
				colorIndex = ((SheepEntity) sheep).getOriginalFleeceColor();
			}
			else {
				colorIndex = sheep.getFleeceColor();
			}
			
			GL11.glColor3f(var4 * EntitySheep.fleeceColorTable[colorIndex][0], var4 * EntitySheep.fleeceColorTable[colorIndex][1], var4 * EntitySheep.fleeceColorTable[colorIndex][2]);
			return 1;
		}

		return -1;
	}

	/**
	 * Queries whether should render the specified pass or not.
	 */
	protected int shouldRenderPass(EntityLiving par1EntityLiving, int par2, float par3)
	{
		return this.setWoolColorAndRender((EntitySheep)par1EntityLiving, par2, par3);
	}
}
