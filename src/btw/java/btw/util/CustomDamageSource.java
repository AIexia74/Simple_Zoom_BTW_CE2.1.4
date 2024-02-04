// FCMOD

package btw.util;

import net.minecraft.src.DamageSource;

public class CustomDamageSource extends DamageSource
{
    public static DamageSource damageSourceSaw = new CustomDamageSource("fcSaw" );
    public static DamageSource damageSourceChoppingBlock = new CustomDamageSource("fcChoppingBlock" );
    public static DamageSource damageSourceGroth = new CustomDamageSource("fcGroth" ).setDamageBypassesArmor();
    public static DamageSource damageSourceGrothSpores = new CustomDamageSource("fcGrothSpores" ).setDamageBypassesArmor();
    public static DamageSource damageSourceMelon = new CustomDamageSource("fcMelon" );
    public static DamageSource damageSourcePumpkin = new CustomDamageSource("fcPumpkin" );
    public static DamageSource damageSourceGloom = new CustomDamageSource("fcGloom" ).setDamageBypassesArmor();
	
    protected CustomDamageSource(String sName )
    {
    	super( sName );
    }
}
