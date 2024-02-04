// FCMOD

package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public abstract class CarrotBlockBase extends DailyGrowthCropsBlock
{
    public CarrotBlockBase(int iBlockID )
    {
        super( iBlockID );
    }
	
    @Override
    public ItemStack getStackRetrievedByBlockDispenser(World world, int i, int j, int k)
    {
    	int iMetadata = world.getBlockMetadata( i, j, k );
		
		if (getGrowthLevel(iMetadata) >= 7 )
		{
			return super.getStackRetrievedByBlockDispenser(world, i, j, k);
		}
    	
    	return null;
    }
    
    @Override
    protected boolean requiresNaturalLight() {
    	return false;
    }
    
    //------------- Class Specific Methods ------------//
    
	//----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] iconArray;

    @Environment(EnvType.CLIENT)
    public Icon getIcon(int par1, int par2)
    {
		par2 = getGrowthLevel(par2);
		
        if (par2 < 7)
        {
            if (par2 == 6)
            {
                par2 = 5;
            }

            return this.iconArray[par2 >> 1];
        }
        else
        {
            return this.iconArray[3];
        }
    }

    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.iconArray = new Icon[4];

        for (int var2 = 0; var2 < this.iconArray.length; ++var2)
        {
            this.iconArray[var2] = par1IconRegister.registerIcon(this.getUnlocalizedName2() + "_" + var2);
        }
    }
}
