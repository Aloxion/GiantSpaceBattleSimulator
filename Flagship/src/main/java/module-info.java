import gsbs.flagshipsystem.FlagshipControlSystem;
import gsbs.flagshipsystem.FlagshipPlugin;

module Player {
    requires Common;
    provides gsbs.common.services.IGamePluginService with FlagshipPlugin;
    provides gsbs.common.services.IEntityProcessingService with FlagshipControlSystem;
}