package net.minecraft.src;

import java.util.concurrent.Callable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public class CallableTexturePack implements Callable
{
    final Minecraft theMinecraft;

    public CallableTexturePack(Minecraft par1Minecraft)
    {
        this.theMinecraft = par1Minecraft;
    }

    public String callTexturePack()
    {
        return this.theMinecraft.gameSettings.skin;
    }

    public Object call()
    {
        return this.callTexturePack();
    }
}
