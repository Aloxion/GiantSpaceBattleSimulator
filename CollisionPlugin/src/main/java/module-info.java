import gsbs.common.services.IPostProcess;

module CollisionPlugin {
    requires Common;

    provides IPostProcess with gsbs.collision.CollisionControlSystem;
}