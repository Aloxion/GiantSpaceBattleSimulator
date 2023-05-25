import gsbs.flagshipaisystem.FlagshipAIControlSystem;

module FlagshipAIPlugin {
    requires Common;

    uses gsbs.common.services.IWeapon;

    provides gsbs.common.services.IProcess with FlagshipAIControlSystem;
}