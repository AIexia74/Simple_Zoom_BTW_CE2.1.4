// FCMOD

package btw.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Entity;
import net.minecraft.src.Render;

@Environment(EnvType.CLIENT)
public class SoulSandRenderer extends Render
{
    public SoulSandRenderer()
    {
    }

    @Override
    public void doRender(Entity entity, double d, double d1, double d2,
                         float f, float f1 )
    {
    	// intentionally invisible renderer
    }
}