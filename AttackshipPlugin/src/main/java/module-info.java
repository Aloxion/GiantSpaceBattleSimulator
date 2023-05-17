import gsbs.attackshipsystem.AttackshipSpawner;
import gsbs.common.services.IEventListener;
import gsbs.common.services.IProcess;

module AttackshipPlugin {
    requires Common;
    requires imgui.binding;

    // provides IProcess with gsbs.attackshipsystem.
    provides IEventListener with AttackshipSpawner;

    provides IProcess with gsbs.attackshipsystem.BoidProcessor;

}