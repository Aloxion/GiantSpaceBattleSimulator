module Player {
    requires Common;
    provides gsbs.common.services.IGamePluginService with gsbs.playersystem.PlayerPlugin;
    provides gsbs.common.services.IEntityProcessingService with gsbs.playersystem.PlayerControlSystem;
}