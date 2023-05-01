module PistolPlugin {
    requires Common;

    provides gsbs.common.services.IWeapon with gsbs.weapon.implementations.pistol.PistolPlugin;
}