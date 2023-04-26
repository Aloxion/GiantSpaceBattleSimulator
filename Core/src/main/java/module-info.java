module Core {
    requires Common;

    uses gsbs.common.services.IPlugin;
    uses gsbs.common.services.IProcess;
    uses gsbs.common.services.IPostProcess;
    uses gsbs.common.events.IEventListener;

    requires imgui.lwjgl3;
    requires imgui.binding;

    requires org.lwjgl;
    requires org.lwjgl.glfw;
    requires org.lwjgl.nanovg;
    requires org.lwjgl.bgfx;

    requires org.lwjgl.natives;
    requires org.lwjgl.glfw.natives;
    requires org.lwjgl.bgfx.natives;
    requires org.lwjgl.nanovg.natives;
    requires org.lwjgl.stb.natives;
}