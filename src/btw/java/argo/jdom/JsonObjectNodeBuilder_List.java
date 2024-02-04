package argo.jdom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.Iterator;

@Environment(EnvType.CLIENT)
class JsonObjectNodeBuilder_List extends HashMap
{
    final JsonObjectNodeBuilder nodeBuilder;

    JsonObjectNodeBuilder_List(JsonObjectNodeBuilder par1JsonObjectNodeBuilder)
    {
        this.nodeBuilder = par1JsonObjectNodeBuilder;
        Iterator var2 = JsonObjectNodeBuilder.func_74607_a(this.nodeBuilder).iterator();

        while (var2.hasNext())
        {
            JsonFieldBuilder var3 = (JsonFieldBuilder)var2.next();
            this.put(var3.func_74724_b(), var3.buildValue());
        }
    }
}
