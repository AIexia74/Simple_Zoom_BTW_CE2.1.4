package argo.jdom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
interface JsonListenerToJdomAdapter_NodeContainer
{
    void addNode(JsonNodeBuilder var1);

    void addField(JsonFieldBuilder var1);
}
