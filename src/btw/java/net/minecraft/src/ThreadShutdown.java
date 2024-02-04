package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public final class ThreadShutdown extends Thread
{
    public void run()
    {
        Minecraft.stopIntegratedServer();
    }
}
