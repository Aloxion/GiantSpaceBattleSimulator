module Common {
    opens gsbs.common.components;

    requires org.lwjgl.nanovg;
    requires imgui.binding;

    exports gsbs.common.data;
    exports gsbs.common.entities;
    exports gsbs.common.components;
    exports gsbs.common.services;
    exports gsbs.common.util;
    exports gsbs.common.math;
    exports gsbs.common.events;
    exports gsbs.common.data.enums;

    // Define all the service provider interfaces
    uses gsbs.common.services.IPlugin;
    uses gsbs.common.services.IProcess;
    uses gsbs.common.services.IPostProcess;
    uses gsbs.common.services.ISystemProcess;
    uses gsbs.common.services.ISystemPostProcess;
    uses gsbs.common.services.IEventListener;
    uses gsbs.common.services.IWeapon;

    // Register built-in services
    provides gsbs.common.services.IProcess with gsbs.common.processors.MovementProcessor;
}