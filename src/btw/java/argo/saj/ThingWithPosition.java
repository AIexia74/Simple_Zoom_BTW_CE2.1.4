package argo.saj;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
interface ThingWithPosition
{
    int getColumn();

    int getRow();
}
