import gsbs.debugsystem.DebugProcessor;

module DebugPlugin {
    requires Common;
    requires imgui.binding;
    requires org.lwjgl.nanovg;
    requires org.lwjgl.bgfx;
    requires java.desktop;

    provides gsbs.common.services.ISystemPostProcess with DebugProcessor;
}