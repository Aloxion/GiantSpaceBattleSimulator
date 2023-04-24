import gsbs.asteroidsystem.AsteroidPlugin;

module Asteroid {
    requires Common;
    provides gsbs.common.services.IPlugin with AsteroidPlugin;
}