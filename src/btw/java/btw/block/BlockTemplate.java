// FCMOD

package btw.block;

import btw.block.util.Flammability;
import btw.crafting.util.FurnaceBurnTime;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.Material;

public class BlockTemplate extends Block {
    protected BlockTemplate(int blockID, Material material) {
        super(blockID, Material.rock);

        setHardness(1F);
        setResistance(10F); // most blocks don't need setResistance() as it's done in setHardness()
        setShovelsEffectiveOn(false);
        setPicksEffectiveOn(false);
        setAxesEffectiveOn(false);
        setChiselsEffectiveOn(false);

        initBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);

        setNonBuoyant();
        setFireProperties(Flammability.NONE);
        setFurnaceBurnTime(FurnaceBurnTime.NONE);
        setFilterableProperties(Item.FILTERABLE_SOLID_BLOCK);

        setLightOpacity(255); // most don't need. 255 is fully opaque
        Block.useNeighborBrightness[blockID] = false; // used by slabs and such

        setStepSound(soundStoneFootstep);

        setUnlocalizedName("fcBlockTemplate");
    }

    //------------- Class Specific Methods ------------//

    //------------ Client Side Functionality ----------//
}
