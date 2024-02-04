package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.io.File;

@Environment(EnvType.CLIENT)
public interface IDownloadSuccess
{
    void onSuccess(File var1);
}
