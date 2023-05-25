import gsbs.carrieraisystem.CarrierAIControlSystem;

module CarrierAIPlugin {
    requires Common;

    uses gsbs.common.services.IWeapon;

    provides gsbs.common.services.IProcess with CarrierAIControlSystem;
}