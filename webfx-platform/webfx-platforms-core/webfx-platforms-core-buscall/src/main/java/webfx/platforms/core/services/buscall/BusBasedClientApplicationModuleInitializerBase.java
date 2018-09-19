package webfx.platforms.core.services.buscall;

import webfx.platforms.core.services.appcontainer.spi.ApplicationModuleInitializer;
import webfx.platforms.core.services.bus.BusService;

/**
 * @author Bruno Salmon
 */
public abstract class BusBasedClientApplicationModuleInitializerBase implements ApplicationModuleInitializer {

    @Override
    public void initModule() {
        // Initializing the bus immediately to make the connection connection process happen while the application is initializing
        initializeBusConnection();
    }

    protected void initializeBusConnection() {
        BusService.bus(); // Instantiating the bus (if not already done) is enough to open the connection
    }
}