import gsbs.asteroidsystem.AsteroidControlSystem;
import gsbs.asteroidsystem.AsteroidPlugin;

module Asteroid {
    requires Common;
    provides gsbs.common.services.IPlugin with AsteroidPlugin;
    provides gsbs.common.services.IProcess with AsteroidControlSystem;
}