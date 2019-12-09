package nl.malmberg.interactive_lessons;

import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import nl.malmberg.interactive_lessons.game_model.GameModelVerticle;
import nl.malmberg.interactive_lessons.message_objects.AddGameModelObject;
import nl.malmberg.interactive_lessons.message_objects.GetGameModelObject;
import nl.malmberg.interactive_lessons.model.GameModel;
import nl.malmberg.interactive_lessons.model.ImmutableGameModel;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.exceptionHandler(event -> LOG.error("CAUGHT A THROWABLE EXCEPTION: ", event));

        registerInternalObjects(vertx);

        vertx.deployVerticle(new MainVerticle());
        vertx.deployVerticle(new GameModelVerticle());

        initMockData(vertx);
    }

    private static void registerInternalObjects(final Vertx vertx) {
        SimpleCodec.register(vertx.eventBus(),
                AddGameModelObject.class,
                GetGameModelObject.class,
                ImmutableGameModel.class
        );
    }

    private static void initMockData(final Vertx vertx) {
        final GameModel gameModel = MockData.mockGameModel();
        LOG.info(gameModel.toString());
        vertx.eventBus().publish(Topic.ADD_GAME_MODEL, new AddGameModelObject(gameModel.id().toString(), gameModel));
    }
}
