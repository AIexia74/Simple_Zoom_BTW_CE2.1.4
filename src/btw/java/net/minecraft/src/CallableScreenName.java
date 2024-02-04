package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.concurrent.Callable;

@Environment(EnvType.CLIENT)
class CallableScreenName implements Callable
{
    final EntityRenderer entityRender;

    CallableScreenName(EntityRenderer par1EntityRenderer)
    {
        this.entityRender = par1EntityRenderer;
    }

    public String callScreenName()
    {
        return EntityRenderer.getRendererMinecraft(this.entityRender).currentScreen.getClass().getCanonicalName();
    }

    public Object call()
    {
        return this.callScreenName();
    }
}
