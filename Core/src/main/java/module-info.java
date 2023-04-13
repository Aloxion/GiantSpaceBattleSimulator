module Core {
    requires Common;
    requires Flagship;

    // LibGDX
    requires com.badlogic.gdx;
    requires java.desktop;
    requires jdk.unsupported;

    uses gsbs.common.services.IProcess;
    uses gsbs.common.services.IPlugin;
    uses gsbs.common.services.IPostProcess;
}