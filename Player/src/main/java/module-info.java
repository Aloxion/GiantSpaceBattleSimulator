module Player {
    requires Common;
    provides gsbs.common.services.IPlugin with gsbs.playersystem.PlayerPlugin;
    provides gsbs.common.services.IProcess with gsbs.playersystem.PlayerControlSystem;
}