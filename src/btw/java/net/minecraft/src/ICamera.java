package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ICamera
{
    /**
     * Returns true if the bounding box is inside all 6 clipping planes, otherwise returns false.
     */
    boolean isBoundingBoxInFrustum(AxisAlignedBB var1);

    void setPosition(double var1, double var3, double var5);
}
