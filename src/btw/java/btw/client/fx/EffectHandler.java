package btw.client.fx;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class EffectHandler {
    public static Map<Integer, Effect> effectMap = new HashMap<>();

    public static boolean playEffect(int effectID, Minecraft mcInstance, World world, EntityPlayer player, int x, int y, int z, int data) {
        Effect effect = effectMap.get(effectID);

        if (effect != null) {
            effect.playEffect(mcInstance, world, player, x + 0.5, y + 0.5, z + 0.5, data);
            return true;
        }

        return false;
    }

    public interface Effect {
        void playEffect(Minecraft mcInstance, World world, EntityPlayer player, double x, double y, double z, int data);
    }
}
