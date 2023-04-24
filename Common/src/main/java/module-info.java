module Common {
    exports gsbs.common.data;
    exports gsbs.common.entities;
    exports gsbs.common.components;
    exports gsbs.common.services;
    exports gsbs.common.util;
    exports gsbs.common.math;
    exports gsbs.common.events;
    exports gsbs.common.data.enums;

    requires com.badlogic.gdx;
    // Define all the service provider interfaces
    uses gsbs.common.services.IPlugin;
    uses gsbs.common.services.IProcess;
    uses gsbs.common.services.IPostProcess;

    // Register built-in services
    provides gsbs.common.services.IProcess with gsbs.common.processors.MovementProcessor;
}