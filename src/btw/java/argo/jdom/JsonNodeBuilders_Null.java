package argo.jdom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
final class JsonNodeBuilders_Null implements JsonNodeBuilder
{
    public JsonNode buildNode()
    {
        return JsonNodeFactories.aJsonNull();
    }
}
