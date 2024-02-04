package argo.format;

import argo.jdom.JsonRootNode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface JsonFormatter
{
    String format(JsonRootNode var1);
}
