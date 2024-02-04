// FCMOD

package btw.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.EntityLiving;
import btw.entity.mob.SpiderEntity;
import net.minecraft.src.RenderSpider;

@Environment(EnvType.CLIENT)
public class SpiderRenderer extends RenderSpider
{
    public SpiderRenderer()
    {
    	super();
    }
    
    @Override
    protected int shouldRenderPass(EntityLiving entity, int iRenderPass, float par3)
    {
    	SpiderEntity spider = (SpiderEntity)entity;
    	
    	if ( !spider.doEyesGlow() )
    	{
    		return -1;
    	}
    	
        return setSpiderEyeBrightness( spider, iRenderPass, par3 );
    }
}
