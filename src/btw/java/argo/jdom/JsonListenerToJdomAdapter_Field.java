package argo.jdom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
class JsonListenerToJdomAdapter_Field implements JsonListenerToJdomAdapter_NodeContainer
{
    final JsonFieldBuilder fieldBuilder;

    final JsonListenerToJdomAdapter listenerToJdomAdapter;

    JsonListenerToJdomAdapter_Field(JsonListenerToJdomAdapter par1JsonListenerToJdomAdapter, JsonFieldBuilder par2JsonFieldBuilder)
    {
        this.listenerToJdomAdapter = par1JsonListenerToJdomAdapter;
        this.fieldBuilder = par2JsonFieldBuilder;
    }

    public void addNode(JsonNodeBuilder par1JsonNodeBuilder)
    {
        this.fieldBuilder.withValue(par1JsonNodeBuilder);
    }

    public void addField(JsonFieldBuilder par1JsonFieldBuilder)
    {
        throw new RuntimeException("Coding failure in Argo:  Attempt to add a field to a field.");
    }
}
