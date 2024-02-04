package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.awt.Canvas;
import java.awt.Dimension;

@Environment(EnvType.CLIENT)
class CanvasCrashReport extends Canvas
{
    public CanvasCrashReport(int par1)
    {
        this.setPreferredSize(new Dimension(par1, par1));
        this.setMinimumSize(new Dimension(par1, par1));
    }
}
