package argo.jdom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
abstract class LeafFunctor implements Functor
{
    public final Object applyTo(Object par1Obj)
    {
        if (!this.matchesNode(par1Obj))
        {
            throw JsonNodeDoesNotMatchChainedJsonNodeSelectorException.func_74701_a(this);
        }
        else
        {
            return this.typeSafeApplyTo(par1Obj);
        }
    }

    protected abstract Object typeSafeApplyTo(Object var1);
}
