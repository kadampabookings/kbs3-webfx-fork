// Generated by WebFx

module webfx.platform.shared.bus.vertx {

    // Direct dependencies modules
    requires vertx.core;
    requires webfx.platform.providers.vertx.instance;
    requires webfx.platform.shared.bus;
    requires webfx.platform.shared.json;
    requires webfx.platform.shared.util;

    // Exported packages
    exports webfx.platform.shared.services.bus.spi.impl.vertx;

    // Provided services
    provides webfx.platform.shared.services.bus.spi.BusServiceProvider with webfx.platform.shared.services.bus.spi.impl.vertx.VertxBusServiceProvider;

}