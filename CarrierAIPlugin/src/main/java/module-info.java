import gsbs.carrierai.CarrierAIProcessor;
import gsbs.common.services.IProcess;

module CarrierAIPlugin {
    requires Common;

    provides IProcess with CarrierAIProcessor;
}