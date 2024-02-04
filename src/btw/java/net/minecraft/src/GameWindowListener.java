package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@Environment(EnvType.CLIENT)
public final class GameWindowListener extends WindowAdapter
{
    public void windowClosing(WindowEvent par1WindowEvent)
    {
        System.err.println("Someone is closing me!");
        System.exit(1);
    }
}
