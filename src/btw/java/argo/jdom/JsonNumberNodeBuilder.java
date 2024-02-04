package argo.jdom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
final class JsonNumberNodeBuilder implements JsonNodeBuilder
{
    private final JsonNode builtNode;

    JsonNumberNodeBuilder(String par1Str)
    {
        this.builtNode = JsonNodeFactories.aJsonNumber(par1Str);
    }

    public JsonNode buildNode()
    {
        return this.builtNode;
    }
}
