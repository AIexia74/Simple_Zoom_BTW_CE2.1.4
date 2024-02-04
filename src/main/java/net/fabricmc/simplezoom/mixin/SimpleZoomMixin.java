package net.fabricmc.simplezoom.mixin;

import btw.community.simplezoom.SimpleZoomAddon;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



    @Mixin(Minecraft.class)
    public class SimpleZoomMixin {
        @Shadow
        private GameSettings gameSettings;

        private boolean zoomKeyPressed = false;
        private float originalFovSetting;

        @Inject(method = "runTick", at = @At("HEAD"))
        private void onRunTick(CallbackInfo info) {
            // Handle key pressed
            if (SimpleZoomAddon.zoom_key.pressed) {
                handleKeyPressed();
            } else { // Handle key released
                handleKeyReleased();
            }
        }

        private void handleKeyPressed() {
            // If the zoom key is pressed during this tick
            if (!zoomKeyPressed) {
                originalFovSetting = gameSettings.fovSetting;
                zoomKeyPressed = true;
            }

            gameSettings.smoothCamera = true;
            gameSettings.fovSetting =  0.0F;

        }

        private void handleKeyReleased() {
            if (zoomKeyPressed) {
                gameSettings.fovSetting = originalFovSetting;
                gameSettings.smoothCamera = false;
                zoomKeyPressed = false;
            }
        }
    }