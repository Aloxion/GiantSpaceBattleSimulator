package gsbs.common.util;

import java.util.*;

public class Plugin {
    private final String name;
    private final boolean disabled = false;
    private final Map<Class<?>, List<Object>> serviceMap = new HashMap<>();
    private ModuleLayer moduleLayer = null;

    public Plugin(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ModuleLayer getModuleLayer() {
        return moduleLayer;
    }

    public void setModuleLayer(ModuleLayer moduleLayer) {
        this.moduleLayer = moduleLayer;
    }

    private <T> void searchForServices(Class<T> serviceClass) {
        List<Object> services = new ArrayList<>();

        if (moduleLayer != null) {
            var loader = ServiceLoader.load(moduleLayer, serviceClass);

            for (T service : loader) {
                services.add(service);
            }
        }

        serviceMap.put(serviceClass, services);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getServices(Class<T> service) {
        if (!serviceMap.containsKey(service)) {
            searchForServices(service);
        }

        return (List<T>) serviceMap.get(service);
    }
}
