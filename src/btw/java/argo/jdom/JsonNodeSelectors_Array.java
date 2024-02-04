package argo.jdom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

@Environment(EnvType.CLIENT)
final class JsonNodeSelectors_Array extends LeafFunctor
{
    public boolean matchesNode_(JsonNode par1JsonNode)
    {
        return JsonNodeType.ARRAY == par1JsonNode.getType();
    }

    public String shortForm()
    {
        return "A short form array";
    }

    public List typeSafeApplyTo(JsonNode par1JsonNode)
    {
        return par1JsonNode.getElements();
    }

    public String toString()
    {
        return "an array";
    }

    public Object typeSafeApplyTo(Object par1Obj)
    {
        return this.typeSafeApplyTo((JsonNode)par1Obj);
    }

    public boolean matchesNode(Object par1Obj)
    {
        return this.matchesNode_((JsonNode)par1Obj);
    }
}
