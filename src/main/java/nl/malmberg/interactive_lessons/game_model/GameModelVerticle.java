package nl.malmberg.interactive_lessons.game_model;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import nl.malmberg.interactive_lessons.Topic;

public class GameModelVerticle extends AbstractVerticle {
    private final GameModelRepository gameModelRepository = new GameModelRepository();

    @Override
    public void start(Promise<Void> startPromise) {
        initEventBus();
        startPromise.complete();
    }

    private void initEventBus() {
        vertx.eventBus().consumer(Topic.ADD_GAME_MODEL, gameModelRepository::handleAddGameModel);
        vertx.eventBus().consumer(Topic.GET_GAME_MODEL, gameModelRepository::handleGetGameModel);
    }
} 