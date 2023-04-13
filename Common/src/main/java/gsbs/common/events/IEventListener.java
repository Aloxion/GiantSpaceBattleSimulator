package gsbs.common.events;

import gsbs.common.data.GameData;

public interface IEventListener {
    void onEvent(Event event, GameData gameData);
}
