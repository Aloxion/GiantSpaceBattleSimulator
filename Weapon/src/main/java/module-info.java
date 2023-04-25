module Weapon {
    requires Common;

    exports gsbs.weapon.implementations;

    provides gsbs.common.services.IWeapon with gsbs.weapon.implementations.Pistol;
}