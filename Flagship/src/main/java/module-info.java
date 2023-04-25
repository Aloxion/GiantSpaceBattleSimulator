import gsbs.flagshipsystem.FlagshipControlSystem;
import gsbs.flagshipsystem.FlagshipPlugin;

module Flagship {
    requires Common;

    uses gsbs.common.services.IWeapon;

    provides gsbs.common.services.IPlugin with FlagshipPlugin;
    provides gsbs.common.services.IProcess with FlagshipControlSystem;
}