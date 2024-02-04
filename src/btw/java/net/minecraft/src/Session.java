package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Session
{
    public String username;
    public String sessionId;

    public Session(String par1Str, String par2Str)
    {
        this.username = par1Str;
        this.sessionId = par2Str;
    }
}
