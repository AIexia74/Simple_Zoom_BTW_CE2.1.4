package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.net.URL;

@Environment(EnvType.CLIENT)
public class SoundPoolEntry
{
    public String soundName;
    public URL soundUrl;

    public SoundPoolEntry(String par1Str, URL par2URL)
    {
        this.soundName = par1Str;
        this.soundUrl = par2URL;
    }
}
