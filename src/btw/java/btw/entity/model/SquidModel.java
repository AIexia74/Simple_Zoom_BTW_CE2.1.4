// FCMOD

package btw.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Entity;
import btw.entity.mob.SquidEntity;
import net.minecraft.src.ModelSquid;

@Environment(EnvType.CLIENT)
public class SquidModel extends ModelSquid
{
    public SquidModel()
    {
    	super();
    }
    
    @Override
    public void render(Entity entity, float par2, float par3, float par4, float par5, float par6, float fScale )
    {
        setRotationAngles( par2, par3, par4, par5, par6, fScale, entity );
        
        squidBody.render( fScale );

        int iAttackTentacle = -1;
        
        if (((SquidEntity)entity).tentacleAttackInProgressCounter > 0 )
        {
        	iAttackTentacle = 6;
        }
        
        for ( int iTempTentacle = 0; iTempTentacle < squidTentacles.length; ++iTempTentacle )
        {
        	if ( iTempTentacle != iAttackTentacle )
        	{
                squidTentacles[iTempTentacle].render( fScale );
        	}
        }
    }
}
