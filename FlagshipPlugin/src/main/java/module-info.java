import gsbs.flagshipsystem.FlagshipControlSystem;
import gsbs.flagshipsystem.FlagshipPlugin;

module FlagshipPlugin {
    requires Common;
    provides gsbs.common.services.IPlugin with FlagshipPlugin;
    provides gsbs.common.services.IProcess with FlagshipControlSystem;
    provides gsbs.common.events.IEventListener with FlagshipControlSystem;
}