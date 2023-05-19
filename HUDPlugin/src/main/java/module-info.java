import gsbs.common.services.ISystemPostProcess;
import gsbs.hud.HUDProcessor;

module HUDPlugin {
    requires Common;
    requires org.lwjgl.nanovg;

    provides ISystemPostProcess with HUDProcessor;
}