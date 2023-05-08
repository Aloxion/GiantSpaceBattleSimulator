module WeaponPlugin {
    requires Common;

    exports gsbs.weapon;

    provides gsbs.common.services.IProcess with gsbs.weapon.BulletControlSystem;
}