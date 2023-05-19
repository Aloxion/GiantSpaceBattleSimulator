import gsbs.carrier.CarrierPlugin;
import gsbs.carrier.CarrierSpawner;
import gsbs.common.services.IEventListener;
import gsbs.common.services.IPlugin;

module CarrierPlugin {
    requires Common;

    provides IPlugin with CarrierPlugin;
    provides IEventListener with CarrierSpawner;
}