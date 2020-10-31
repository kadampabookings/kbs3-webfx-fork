// Generated by WebFx

module webfx.platform.java.webworker.impl {

    // Direct dependencies modules
    requires java.base;
    requires webfx.platform.client.uischeduler;
    requires webfx.platform.shared.json;
    requires webfx.platform.shared.log;
    requires webfx.platform.shared.webworker;

    // Exported packages
    exports webfx.platform.java.services.webworker.spi.impl;

    // Provided services
    provides webfx.platform.shared.services.webworker.spi.WorkerServiceProvider with webfx.platform.java.services.webworker.spi.impl.JavaWorkerServiceProvider;

}