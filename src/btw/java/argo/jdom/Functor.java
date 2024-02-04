package argo.jdom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
interface Functor
{
    boolean matchesNode(Object var1);

    Object applyTo(Object var1);

    String shortForm();
}
