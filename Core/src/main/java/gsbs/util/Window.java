package gsbs.util;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.bgfx.BGFXInit;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;

import java.nio.IntBuffer;
import java.util.Objects;
import java.util.function.Consumer;

import static org.lwjgl.bgfx.BGFX.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVG.nvgBeginFrame;
import static org.lwjgl.nanovg.NanoVG.nvgEndFrame;
import static org.lwjgl.nanovg.NanoVGBGFX.nvgCreate;
import static org.lwjgl.nanovg.NanoVGBGFX.nvgDelete;
import static org.lwjgl.system.Configuration.GLFW_LIBRARY_NAME;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private final Configuration config;
    private final ImGuiImplBGFX imGuiBGFX = new ImGuiImplBGFX();
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final Consumer<Window> process;

    private final long nvgContext;
    private long handle;

    public Window(Configuration config, Consumer<Window> process) {
        this.config = config;
        this.process = process;
        initWindow();
        initImGui();
        imGuiGlfw.init(handle, true);
        imGuiBGFX.init();
        nvgContext = nvgCreate(true, 0, NULL);
    }

    public void run() {
        while (!shouldClose()) {
            processFrame();

            // BGFX has a bug in metal, where if the window goes offscreen, then the vsync stops working i.e. 100% CPU usage.
            if (config.isVsync() && ImGui.getIO().getDeltaTime() < 0.016) {
                try {
                    Thread.sleep((long) (16 - ImGui.getIO().getDeltaTime() * 1000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        dispose();
    }

    public long getNvgContext() {
        return nvgContext;
    }

    public long getHandle() {
        return handle;
    }

    private void dispose() {
        nvgDelete(nvgContext);
        imGuiBGFX.dispose();
        imGuiGlfw.dispose();
        disposeImGui();
        disposeWindow();
    }

    private boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(handle);
    }

    private void processFrame() {
        // Start frame
        GLFW.glfwPollEvents();
        clearBuffer();
        imGuiGlfw.newFrame();
        ImGui.newFrame();

        int[] width = new int[1];
        int[] height = new int[1];
        org.lwjgl.glfw.GLFW.glfwGetWindowSize(handle, width, height);
        nvgBeginFrame(nvgContext, width[0], height[0], ImGui.getIO().getDisplayFramebufferScaleX());

        // Run process
        this.process.accept(this);

        // Render frame
        nvgEndFrame(nvgContext);
        ImGui.render();
        imGuiBGFX.renderDrawData(ImGui.getDrawData());
        renderBuffer();
    }

    private void initWindow() {
        if (Platform.get() == Platform.MACOSX) {
            GLFW_LIBRARY_NAME.set("glfw_async");
        }

        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);

        handle = glfwCreateWindow(config.getWidth(), config.getHeight(), config.getTitle(), NULL, NULL);

        if (handle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        try (MemoryStack stack = stackPush()) {
            final IntBuffer pWidth = stack.mallocInt(1); // int*
            final IntBuffer pHeight = stack.mallocInt(1); // int*

            GLFW.glfwGetWindowSize(handle, pWidth, pHeight);
            final GLFWVidMode vidmode = Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()));
            GLFW.glfwSetWindowPos(handle, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
        }

        try (MemoryStack stack = stackPush()) {
            BGFXInit init = BGFXInit.malloc(stack);

            bgfx_init_ctor(init);
            init.resolution(it -> it
                    .width(config.getWidth())
                    .height(config.getHeight())
                    .reset(BGFX_RESET_VSYNC));
            switch (Platform.get()) {
                case LINUX:
                    init.platformData()
                            .ndt(GLFWNativeX11.glfwGetX11Display())
                            .nwh(GLFWNativeX11.glfwGetX11Window(handle));
                    break;
                case MACOSX:
                    init.platformData()
                            .nwh(GLFWNativeCocoa.glfwGetCocoaWindow(handle));
                    break;
                case WINDOWS:
                    init.platformData()
                            .nwh(GLFWNativeWin32.glfwGetWin32Window(handle));
                    break;
            }

            if (!bgfx_init(init)) {
                throw new RuntimeException("Error initializing bgfx renderer");
            }
        }

        if (config.isFullScreen()) {
            GLFW.glfwMaximizeWindow(handle);
        } else {
            GLFW.glfwShowWindow(handle);
        }

        clearBuffer();
        renderBuffer();

        GLFW.glfwSetWindowSizeCallback(handle, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(final long window, final int width, final int height) {
                bgfx_reset(width, height, BGFX_RESET_NONE, BGFX_TEXTURE_FORMAT_COUNT);
                processFrame();
            }
        });
    }

    private void initImGui() {
        ImGui.createContext();
        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);
    }


    private void clearBuffer() {
        int color = Math.round(config.getBackgroundColor().getRed() * 255);
        color = (color << 8) + Math.round(config.getBackgroundColor().getGreen() * 255);
        color = (color << 8) + Math.round(config.getBackgroundColor().getBlue() * 255);
        color = (color << 8) + Math.round(config.getBackgroundColor().getAlpha() * 255);
        bgfx_set_view_clear(0, BGFX_CLEAR_COLOR | BGFX_CLEAR_DEPTH, color, 1.0f, 0);
        int[] w = new int[1];
        int[] h = new int[1];
        glfwGetWindowSize(handle, w, h);
        bgfx_set_view_rect(0, 0, 0, w[0], h[0]);
    }

    private void renderBuffer() {
        bgfx_frame(false);
    }

    private void disposeImGui() {
        ImGui.destroyContext();
    }

    private void disposeWindow() {
        bgfx_shutdown();
        Callbacks.glfwFreeCallbacks(handle);
        GLFW.glfwDestroyWindow(handle);
        GLFW.glfwTerminate();
        Objects.requireNonNull(GLFW.glfwSetErrorCallback(null)).free();
    }
}