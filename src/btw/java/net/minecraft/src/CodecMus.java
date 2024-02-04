package net.minecraft.src;

import java.io.IOException;
import java.io.InputStream;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import paulscode.sound.codecs.CodecJOrbis;

@Environment(EnvType.CLIENT)
public class CodecMus extends CodecJOrbis
{
    protected InputStream openInputStream()
    {
        try
        {
            return new MusInputStream(this, this.url, this.urlConnection.getInputStream());
        }
        catch (IOException e)
        {
            return null;
        }
    }
}
