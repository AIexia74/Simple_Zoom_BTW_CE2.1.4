package argo.jdom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.Iterator;

@Environment(EnvType.CLIENT)
final class JsonArray_NodeList extends ArrayList
{
    final Iterable elements;

    JsonArray_NodeList(Iterable par1Iterable)
    {
        this.elements = par1Iterable;
        Iterator var2 = this.elements.iterator();

        while (var2.hasNext())
        {
            JsonNode var3 = (JsonNode)var2.next();
            this.add(var3);
        }
    }
}
