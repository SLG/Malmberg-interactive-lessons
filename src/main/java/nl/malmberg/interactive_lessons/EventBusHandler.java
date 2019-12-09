package nl.malmberg.interactive_lessons;

import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import nl.malmberg.interactive_lessons.game_state.Game;
import nl.malmberg.interactive_lessons.game_state.Player;
import nl.malmberg.interactive_lessons.message_objects.AnswerMessage;
import nl.malmberg.interactive_lessons.message_objects.CreateGameMessage;
import nl.malmberg.interactive_lessons.message_objects.GetGameModelObject;
import nl.malmberg.interactive_lessons.message_objects.ImmutableGameCreatedMessage;
import nl.malmberg.interactive_lessons.message_objects.ImmutableGameJoinedMessage;
import nl.malmberg.interactive_lessons.message_objects.JoinGameMessage;
import nl.malmberg.interactive_lessons.message_objects.LeaveGameMessage;
import nl.malmberg.interactive_lessons.message_objects.StartGameMessage;
import nl.malmberg.interactive_lessons.model.GameModel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static java.util.UUID.fromString;

public class EventBusHandler {
    private static final Logger LOG = LoggerFactory.getLogger(EventBusHandler.class);
    private static final String GAME_ID_NOT_FOUND = "Game Id not found";

    private final Map<UUID, Game> games = new HashMap<>();
    private final Map<String, UUID> joinKeys = new HashMap<>();
    private final Vertx vertx;
    private final EventBus eventBus;

    public EventBusHandler(final Vertx vertx) {
        this.vertx = vertx;
        eventBus = vertx.eventBus();
    }

    public void handleCreateGame(final Message<JsonObject> message) {
        final CreateGameMessage createGameMessage = message.body().mapTo(CreateGameMessage.class);
        final UUID gameId = fromString(createGameMessage.gameId());
        if (!games.containsKey(gameId)) {
            eventBus.<GameModel>request(Topic.GET_GAME_MODEL, new GetGameModelObject(createGameMessage.gameModelId()),
                    ar -> handleGetGameModelResult(message, gameId, ar));
        } else {
            LOG.error("Game Id already exists");
            message.fail(500, "Game Id already exists");
        }
    }

    private void handleGetGameModelResult(final Message<JsonObject> message, final UUID gameId,
            final AsyncResult<Message<GameModel>> ar) {
        if (ar.succeeded()) {
            final GameModel gameModel = ar.result().body();
            final Game game = new Game(gameId, gameModel, vertx);
            games.put(game.getId(), game);
            joinKeys.put(game.getJoinKey(), game.getId());
            message.reply(JsonObject.mapFrom(ImmutableGameCreatedMessage
                    .builder()
                    .gameId(game.getId().toString())
                    .joinKey(game.getJoinKey())
                    .build()));
        } else {
            LOG.error("Failed to get game model", ar.cause());
            message.fail(500, ar.cause().getMessage());
        }
    }

    public void handleJoinGame(final Message<JsonObject> message) {
        final JoinGameMessage joinGameMessage = message.body().mapTo(JoinGameMessage.class);
        if (joinKeys.containsKey(joinGameMessage.joinKey())) {
            final UUID gameId = joinKeys.get(joinGameMessage.joinKey());
            final Game game = games.get(gameId);
            final Player player = new Player(joinGameMessage.playerName());
            game.join(player);
            message.reply(JsonObject.mapFrom(ImmutableGameJoinedMessage
                    .builder()
                    .gameId(game.getId().toString())
                    .playerId(player.getId().toString())
                    .build()));
        } else {
            LOG.error(GAME_ID_NOT_FOUND);
            message.fail(500, GAME_ID_NOT_FOUND);
        }
    }

    public void handleLeaveGame(final Message<JsonObject> message) {
        final LeaveGameMessage leaveGameMessage = message.body().mapTo(LeaveGameMessage.class);
        final UUID gameId = fromString(leaveGameMessage.gameId());
        doIfGameExists(gameId, message, game -> {
            game.leave(leaveGameMessage.playerId());
            message.reply("");
        });
    }

    public void handleStartGame(final Message<JsonObject> message) {
        final StartGameMessage startGameMessage = message.body().mapTo(StartGameMessage.class);
        final UUID gameId = fromString(startGameMessage.gameId());
        doIfGameExists(gameId, message, game -> {
            game.askQuestion();
            message.reply("");
        });
    }

    public void handleAnswer(final Message<JsonObject> message) {
        final AnswerMessage answerMessage = message.body().mapTo(AnswerMessage.class);
        final UUID gameId = fromString(answerMessage.gameId());
        doIfGameExists(gameId, message, game -> {
            game.addAnswer(answerMessage);
            message.reply("");
        });
    }

    public void handleGetGame(final Message<String> message) {
        final UUID gameId = fromString(message.body());
        doIfGameExists(gameId, message, game -> message.reply(JsonObject.mapFrom(game.toSnapshot())));
    }

    public void handleNextQuestion(final Message<String> message) {
        final UUID gameId = fromString(message.body());
        doIfGameExists(gameId, message, game -> {
            game.nextQuestion();
            message.reply("");
        });
    }

    private <T> void doIfGameExists(final UUID gameId, final Message<T> message, Consumer<Game> action) {
        if (games.containsKey(gameId)) {
            final Game game = games.get(gameId);
            action.accept(game);
        } else {
            LOG.error(GAME_ID_NOT_FOUND);
            message.fail(500, GAME_ID_NOT_FOUND);
        }
    }
}
