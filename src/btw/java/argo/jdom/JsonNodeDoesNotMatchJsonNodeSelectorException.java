package argo.jdom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class JsonNodeDoesNotMatchJsonNodeSelectorException extends IllegalArgumentException
{
    JsonNodeDoesNotMatchJsonNodeSelectorException(String par1Str)
    {
        super(par1Str);
    }
}
