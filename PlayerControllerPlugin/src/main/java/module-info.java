import gsbs.playercontrolsystem.PlayerControllerControlSystem;
import gsbs.playercontrolsystem.PlayerControllerPlugin;

module PlayerController {
    requires Common;
    provides gsbs.common.services.IPlugin with PlayerControllerPlugin;
    provides gsbs.common.services.IProcess with PlayerControllerControlSystem;
    provides gsbs.common.services.IEventListener with PlayerControllerControlSystem;
}