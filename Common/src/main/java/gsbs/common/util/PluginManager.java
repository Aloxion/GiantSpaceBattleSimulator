package gsbs.common.util;


import java.io.IOException;
import java.lang.module.ModuleFinder;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class PluginManager {
    private static final String PLUGINS_PATH = "../plugins";

    private static final Map<URL, Plugin> plugins = new HashMap<>();
    private static final Map<Class<?>, List<Object>> systemServiceMap = new HashMap<>();

    static {
        updatePluginLayers();
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> locateAll(Class<T> service) {
        // Load system services
        if (!systemServiceMap.containsKey(service)) {
            searchSystemServiceProviders(service);
        }

        List<T> services = new ArrayList<>((Collection<? extends T>) systemServiceMap.get(service));

        // Load plugin services
        // FIXME: This is super duper slow
        for (var plugin : plugins.values()) {
            var pluginServices = plugin.getServices(service);
            var toRemove = new ArrayList<T>();

            for (var pluginService : pluginServices) {
                for (var systemService : services) {
                    if (pluginService.getClass().getName().equals(systemService.getClass().getName())) {
                        toRemove.add(pluginService);
                    }
                }
            }

            pluginServices.removeAll(toRemove);
            services.addAll(pluginServices);
        }

        // Return the services from the serviceMap
        return services;
    }

    /**
     * Scans the classpath for services using the service loader
     */
    private static <T> void searchSystemServiceProviders(Class<T> serviceClass) {
        List<Object> systemServices = new ArrayList<>();

        // Start by scanning the classpath/module-path
        var loader = ServiceLoader.load(serviceClass, ClassLoader.getSystemClassLoader());

        for (T service : loader) {
            systemServices.add(service);
        }

        systemServiceMap.put(serviceClass, systemServices);
    }


    private static void addPlugin(URL pluginLocation) {
        if (plugins.containsKey(pluginLocation))
            return;

        var plugin = new Plugin(pluginLocation.getFile());
        plugins.put(pluginLocation, plugin);
    }

    private static URL getPathOfClass(Class<?> clazz) {
        return clazz.getProtectionDomain().getCodeSource().getLocation();
    }

    public static void reloadPlugins() {
        var pluginsToRemove = updatePluginLayers();
        for (var plugin : pluginsToRemove) {
            plugins.remove(plugin);
        }
    }

    public static List<URL> updatePluginLayers() {
        // Find all plugin jars
        var pluginsDir = Paths.get(getPathOfClass(PluginManager.class).getPath()).getParent().resolve(PLUGINS_PATH);
        Set<URL> pluginJars = new HashSet<>();

        try (Stream<Path> stream = Files.list(pluginsDir)) {
            for (var item : stream.collect(Collectors.toList())) {
                if (!item.getFileName().toString().endsWith(".jar")) {
                    continue;
                }

                pluginJars.add(item.toUri().toURL());
            }
        } catch (IOException e) {
            return new ArrayList<>();
        }

        var validPluginsFound = new ArrayList<>();

        // Load each jar as its own module layer
        for (var plugin : pluginJars) {
            // If the plugin already has a loaded module layer, then we can skip it
            if (plugins.get(plugin) != null && plugins.get(plugin).getModuleLayer() != null) {
                validPluginsFound.add(plugin);
                continue;
            }

            var pluginPath = Path.of(plugin.getPath());

            var mf = ModuleFinder.of(pluginPath);

            var moduleReference = mf.findAll().stream().findFirst().orElse(null);

            if (moduleReference == null) {
                continue;
            }

            var moduleName = moduleReference.descriptor().name();

            var cfg = ModuleLayer
                    .boot()
                    .configuration()
                    .resolve(mf, ModuleFinder.of(), Set.of(moduleName));
            var ml = ModuleLayer
                    .boot()
                    .defineModulesWithOneLoader(cfg, ClassLoader.getPlatformClassLoader());

            if (plugins.get(plugin) == null) {
                addPlugin(plugin);
            }

            plugins.get(plugin).setModuleLayer(ml);

            validPluginsFound.add(plugin);
        }

        // Remove any plugin that was not found
        List<URL> pluginsToRemove = new ArrayList<>();
        for (var plugin : plugins.keySet()) {
            if (!validPluginsFound.contains(plugin)) {
                pluginsToRemove.add(plugin);
            }
        }

        return pluginsToRemove;
    }

    public static Map<URL, Plugin> getPlugins() {
        return plugins;
    }
}
