import gsbs.common.services.IEventListener;
import gsbs.startmenu.StartMenuProcessor;

module StartMenuPlugin {
    requires Common;
    requires imgui.binding;
    requires org.lwjgl.nanovg;
    provides gsbs.common.services.ISystemProcess with StartMenuProcessor;
    provides IEventListener with StartMenuProcessor;
}