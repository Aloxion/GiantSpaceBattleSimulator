import gsbs.asteroidsystem.AsteroidPlugin;

module AsteroidPlugin {
    requires Common;
    provides gsbs.common.services.IPlugin with AsteroidPlugin;
}