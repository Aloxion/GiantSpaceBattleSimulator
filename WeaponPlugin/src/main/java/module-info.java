module WeaponPlugin {
    requires Common;

    exports gsbs.weapon.implementations;
    exports gsbs.weapon;

    provides gsbs.common.services.IWeapon with gsbs.weapon.implementations.Pistol;
    provides gsbs.common.services.IProcess with gsbs.weapon.BulletControlSystem;
}