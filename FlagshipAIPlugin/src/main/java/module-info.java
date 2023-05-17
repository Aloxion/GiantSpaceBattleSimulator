import gsbs.flagshipaisystem.FlagshipAIControlSystem;
import gsbs.flagshipaisystem.FlagshipAIPlugin;

module FlagshipAIPlugin {
    requires Common;

    uses gsbs.common.services.IWeapon;

    provides gsbs.common.services.IPlugin with FlagshipAIPlugin;
    provides gsbs.common.services.IProcess with FlagshipAIControlSystem;
}