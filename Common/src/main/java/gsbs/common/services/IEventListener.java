package gsbs.common.services;

import gsbs.common.data.GameData;
import gsbs.common.events.Event;

public interface IEventListener {
    void onEvent(Event event, GameData gameData);
}
