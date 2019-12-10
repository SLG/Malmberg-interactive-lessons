package nl.malmberg.interactive_lessons.game_model;

import io.vertx.core.eventbus.Message;
import nl.malmberg.interactive_lessons.message_objects.AddGameModelObject;
import nl.malmberg.interactive_lessons.message_objects.GetGameModelObject;
import nl.malmberg.interactive_lessons.model.GameModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.UUID.fromString;

public class GameModelRepository {
    private final Map<UUID, GameModel> gameModels = new HashMap<>();

    public void handleAddGameModel(final Message<AddGameModelObject> message) {
        final AddGameModelObject addGameModelObject = message.body();
        gameModels.put(fromString(addGameModelObject.getId()), addGameModelObject.getGameModel());
    }

    public void handleGetGameModel(final Message<GetGameModelObject> message) {
        final GetGameModelObject getGameModelObject = message.body();
        Optional.ofNullable(gameModels.get(fromString(getGameModelObject.getId())))
                .ifPresentOrElse(message::reply, () -> message.fail(404, "No Game Model found"));
    }
}
