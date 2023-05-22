import gsbs.carriersystem.CarrierControlSystem;
import gsbs.carriersystem.CarrierPlugin;

module CarrierPlugin {
    requires Common;

    uses gsbs.common.services.IWeapon;

    provides gsbs.common.services.IPlugin with CarrierPlugin;
    provides gsbs.common.services.IProcess with CarrierControlSystem;
}