package net.minecraft.src;

import java.util.concurrent.Callable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.lwjgl.Sys;

@Environment(EnvType.CLIENT)
public class CallableLWJGLVersion implements Callable
{
    /** Reference to the Minecraft object. */
    final Minecraft mc;

    public CallableLWJGLVersion(Minecraft par1Minecraft)
    {
        this.mc = par1Minecraft;
    }

    public String getType()
    {
        return Sys.getVersion();
    }

    public Object call()
    {
        return this.getType();
    }
}
