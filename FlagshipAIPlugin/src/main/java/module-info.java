import gsbs.flagshipsystem.FlagshipAIControlSystem;
import gsbs.flagshipsystem.FlagshipAIPlugin;

module FlagshipPlugin {
    requires Common;

    uses gsbs.common.services.IWeapon;

    provides gsbs.common.services.IPlugin with FlagshipAIPlugin;
    provides gsbs.common.services.IProcess with FlagshipAIControlSystem;
}