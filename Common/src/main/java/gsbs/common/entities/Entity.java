package gsbs.common.entities;

import gsbs.common.components.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Entity implements Serializable {
    private final UUID ID = UUID.randomUUID();
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    /**
     * Get a unique identifier for the entity.
     */
    public String getID() {
        return ID.toString();
    }
    /**
     * Add a component to the entity.
     */
    public <T extends Component> void add(T part) {
        components.put(part.getClass(), part);
    }
    /**
     * Remove a component from the entity.
     */
    public void remove(Class<? extends Component> componentClass) {
        this.components.remove(componentClass);
    }
    /**
     * Get a component from the entity.
     */
    @SuppressWarnings("unchecked")
    public <E extends Component> E getComponent(Class<E> componentClass) {
        return (E) this.components.get(componentClass);
    }
    public List<? extends Component> getComponents() {
        return this.components.values().stream().collect(Collectors.toList());
    }

}