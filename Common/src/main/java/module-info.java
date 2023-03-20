module Common {
    exports gsbs.common.data;
    exports gsbs.common.entities;
    exports gsbs.common.components;
    exports gsbs.common.services;
    exports gsbs.common.util;
    exports gsbs.common.math;

    // Define all the service provider interfaces
    uses gsbs.common.services.IGamePluginService;
    uses gsbs.common.services.IEntityProcessingService;
    uses gsbs.common.services.IPostEntityProcessingService;

    // Register built-in services
    provides gsbs.common.services.IEntityProcessingService with gsbs.common.processors.MovementProcessor;
}