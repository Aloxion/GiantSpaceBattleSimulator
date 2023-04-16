package gsbs.common.events;

import gsbs.common.data.GameData;
import gsbs.common.entities.Entity;
import gsbs.common.events.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The EventManagerTest class contains a set of tests to validate the functionality
 * of the EventManager and its interaction with events and event listeners.
 * The tests cover the following scenarios:
 *   - Adding events to the event queue.
 *   - Adding and removing event listeners.
 *   - Dispatching events to registered listeners.
 */
class EventManagerTest {

    private EventManager eventManager;
    private GameData gameData;

    @BeforeEach
    void setUp() {
        eventManager = new EventManager();
        gameData = new GameData(); // Initialize gameData, or use a mock object
    }

    @Test
    void testAddEvent() {
        Entity source = new Entity();
        int keyCode = 37; // Left arrow key
        boolean keyPressed = true;
        PlayerControlEvent event = new PlayerControlEvent(source, keyCode, keyPressed);
        eventManager.addEvent(event);
        assertFalse(eventManager.getEventQueue().isEmpty());
    }

    @Test
    void testEventListener() {
        Entity source = new Entity();
        int keyCode = 37; // Left arrow key
        boolean keyPressed = true;
        PlayerControlEvent event = new PlayerControlEvent(source, keyCode, keyPressed);

        class SampleListener implements IEventListener {
            boolean eventReceived = false;

            @Override
            public void onEvent(Event event, GameData gameData) {
                if (event.getEventType() == EventType.PLAYER_CONTROL) {
                    eventReceived = true;
                }
            }
        }

        SampleListener listener = new SampleListener();
        eventManager.addEventListener(listener);
        eventManager.addEvent(event);
        eventManager.dispatchEvents(gameData);
        assertTrue(listener.eventReceived);
    }

    @Test
    void testRemoveEventListener() {
        class SampleListener implements IEventListener {
            @Override
            public void onEvent(Event event, GameData gameData) {
            }
        }

        SampleListener listener = new SampleListener();
        eventManager.addEventListener(listener);
        eventManager.removeEventListener(listener);
        assertTrue(eventManager.getListeners().isEmpty());
    }

    @Test
    void testDispatchEvents() {
        Entity source = new Entity();
        int keyCode = 37; // Left arrow key
        boolean keyPressed = true;
        PlayerControlEvent event = new PlayerControlEvent(source, keyCode, keyPressed);
        eventManager.addEvent(event);

        class SampleListener implements IEventListener {
            int eventCount = 0;

            @Override
            public void onEvent(Event event, GameData gameData) {
                if (event.getEventType() == EventType.PLAYER_CONTROL) {
                    eventCount++;
                }
            }
        }

        SampleListener listener = new SampleListener();
        eventManager.addEventListener(listener);
        eventManager.dispatchEvents(gameData);
        assertEquals(1, listener.eventCount);
        assertTrue(eventManager.getEventQueue().isEmpty());
    }
}