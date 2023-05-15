import gsbs.debugsystem.DebugProcessor;

module DebugPlugin {
    requires Common;
    requires imgui.binding;
    requires org.lwjgl.nanovg;
    requires org.lwjgl.bgfx;

    provides gsbs.common.services.ISystemProcess with DebugProcessor;
}