package net.fabricmc.simplezoom;

import btw.community.simplezoom.SimpleZoomAddon;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class SimpleZoomPreLaunchInitializer implements PreLaunchEntrypoint {
    /**
     * Runs the PreLaunch entrypoint to register BTW-Addon.
     * Don't initialize anything else here, use
     * the method Initialize() in the Addon.
     */
    @Override
    public void onPreLaunch() {
        SimpleZoomAddon.getInstance();
    }
}
