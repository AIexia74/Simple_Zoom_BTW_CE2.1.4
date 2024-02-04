package btw.community.simplezoom;

import btw.AddonHandler;
import btw.BTWAddon;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GameSettings;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.StatCollector;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.Map;


public class SimpleZoomAddon extends BTWAddon {
    public static KeyBinding zoom_key;
    private static SimpleZoomAddon instance;

    private SimpleZoomAddon() {
        super("Simple Zoom", "1.0.1", "Zoom");
    }

    public static SimpleZoomAddon getInstance() {
        if (instance == null)
            instance = new SimpleZoomAddon();
        return instance;
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
    }

    @Override
    public void preInitialize() {

    }

    @Override
    public void handleConfigProperties(Map<String, String> propertyValues) {

    }

    public void initKeybind() {
        zoom_key = new KeyBinding(StatCollector.translateToLocal("Zoom"), Keyboard.KEY_C);

        GameSettings settings = Minecraft.getMinecraft().gameSettings;
        KeyBinding[] keyBindings = Arrays.copyOf(settings.keyBindings, settings.keyBindings.length + 1);

        keyBindings[keyBindings.length - 1] = zoom_key;
        settings.keyBindings = keyBindings;
    }
}
