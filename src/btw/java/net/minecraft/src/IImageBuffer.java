package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.awt.image.BufferedImage;

@Environment(EnvType.CLIENT)
public interface IImageBuffer
{
    BufferedImage parseUserSkin(BufferedImage var1);
}
