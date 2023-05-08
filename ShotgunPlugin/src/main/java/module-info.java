module ShotgunPlugin {
    requires Common;

    provides gsbs.common.services.IWeapon with gsbs.weapon.implementations.shotgun.ShotgunPlugin;
}