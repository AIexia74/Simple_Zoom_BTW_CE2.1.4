package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.net.URL;

@Environment(EnvType.CLIENT)
class RunnableTitleScreen implements Runnable
{
    final GuiMainMenu field_104058_d;

    RunnableTitleScreen(GuiMainMenu par1GuiMainMenu)
    {
        this.field_104058_d = par1GuiMainMenu;
    }

    public void run()
    {
    	// FCMOD: Code removed to get rid of update pester
    }
}
