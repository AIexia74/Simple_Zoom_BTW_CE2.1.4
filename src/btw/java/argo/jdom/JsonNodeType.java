package argo.jdom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum JsonNodeType
{
    OBJECT,
    ARRAY,
    STRING,
    NUMBER,
    TRUE,
    FALSE,
    NULL;
}
