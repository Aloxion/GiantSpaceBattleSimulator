package gsbs.common.events;

import gsbs.common.data.GameData;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
public class EventManager {
    private final Queue<Event> eventQueue;
    private final List<IEventListener> listeners;

    public EventManager() {
        eventQueue = new LinkedList<>();
        listeners = new LinkedList<>();
    }

    public void addEvent(Event event) {
        eventQueue.offer(event);
    }

    public void addEventListener(IEventListener listener) {
        listeners.add(listener);
    }

    public void removeEventListener(IEventListener listener) {
        listeners.remove(listener);
    }

    public void dispatchEvents(GameData gameData) {
        while (!eventQueue.isEmpty()) {
            Event event = eventQueue.poll();
            for (IEventListener listener : listeners) {
                listener.onEvent(event, gameData);
            }
        }
    }
}
