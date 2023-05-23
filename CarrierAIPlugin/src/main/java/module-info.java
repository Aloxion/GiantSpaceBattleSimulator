import gsbs.carrieraisystem.CarrierAIControlSystem;
import gsbs.carrieraisystem.CarrierAIPlugin;

module CarrierAIPlugin {
    requires Common;

    uses gsbs.common.services.IWeapon;

    provides gsbs.common.services.IPlugin with CarrierAIPlugin;
    provides gsbs.common.services.IProcess with CarrierAIControlSystem;
}