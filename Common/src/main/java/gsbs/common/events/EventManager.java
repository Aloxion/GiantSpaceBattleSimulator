package gsbs.common.events;

import gsbs.common.data.GameData;
import gsbs.common.services.IEventListener;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class EventManager {
    private final Queue<Event> eventQueue;

    public EventManager() {
        eventQueue = new LinkedList<>();
    }

    public void addEvent(Event event) {
        eventQueue.offer(event);
    }


    public void dispatchEvents(GameData gameData, Collection<? extends IEventListener> listeners) {
        while (!eventQueue.isEmpty()) {
            Event event = eventQueue.poll();
            for (IEventListener listener : listeners) {
                listener.onEvent(event, gameData);
            }
        }
    }

    public Queue<Event> getEventQueue() {
        return eventQueue;
    }

}
