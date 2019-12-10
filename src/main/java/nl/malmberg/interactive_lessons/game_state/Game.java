package nl.malmberg.interactive_lessons.game_state;

import com.google.common.collect.ImmutableList;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import nl.malmberg.interactive_lessons.GameUtils;
import nl.malmberg.interactive_lessons.message_objects.AnswerMessage;
import nl.malmberg.interactive_lessons.model.Answer;
import nl.malmberg.interactive_lessons.model.GameModel;
import nl.malmberg.interactive_lessons.model.GameState;
import nl.malmberg.interactive_lessons.model.ImmutableAnswer;
import nl.malmberg.interactive_lessons.model.ImmutableProvidedAnswer;
import nl.malmberg.interactive_lessons.model.ProvidedAnswer;
import nl.malmberg.interactive_lessons.model.Question;
import nl.malmberg.interactive_lessons.snapshot.GameSnapshot;
import nl.malmberg.interactive_lessons.snapshot.ImmutableGameSnapshot;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.UUID.fromString;
import static nl.malmberg.interactive_lessons.Topic.GAME_UPDATE;
import static nl.malmberg.interactive_lessons.model.GameState.ASK_QUESTION;
import static nl.malmberg.interactive_lessons.model.GameState.SHOW_SCORES;
import static nl.malmberg.interactive_lessons.model.GameState.WAITING_FOR_ANSWERS;
import static nl.malmberg.interactive_lessons.model.GameState.WAITING_FOR_PLAYERS;

public class Game {
    private static final float MAX_SCORE = 100;
    private static final int STREAK_SCORE = 10;
    private static final Answer EMPTY_ANSWER = ImmutableAnswer.builder().value("<>").correct(false).build();

    private final UUID id;
    private final GameModel gameModel;
    private final Vertx vertx;

    private final OffsetDateTime created = OffsetDateTime.now();
    private final String joinKey = GameUtils.generateJoinKey();
    private final Map<UUID, Player> playerList = new HashMap<>();
    private final Map<Player, ProvidedAnswer> providedAnswers = new HashMap<>();

    private GameState gameState = WAITING_FOR_PLAYERS;
    private int progress = 0;
    private Question currentQuestion;
    private OffsetDateTime answerTime;
    private Long currentTimer;

    public Game(final UUID id, final GameModel gameModel, final Vertx vertx) {
        this.id = id;
        this.gameModel = gameModel;
        this.vertx = vertx;
    }

    public String getJoinKey() {
        return joinKey;
    }

    public UUID getId() {
        return id;
    }

    public void askQuestion() {
        currentQuestion = gameModel
                .getQuestion(progress)
                .orElseThrow(() -> new IllegalStateException("Question not found for game"));
        gameState = ASK_QUESTION;
        currentTimer = vertx.setTimer(currentQuestion.questionTime(), timerId -> showAnswers());
        broadcast();
    }

    private void showAnswers() {
        gameState = WAITING_FOR_ANSWERS;
        currentTimer = vertx.setTimer(currentQuestion.answerTime(), timerId -> showScore());
        answerTime = OffsetDateTime.now();
        broadcast();
    }

    public void join(final Player player) {
        playerList.put(player.getId(), player);
        broadcast();
    }

    public void leave(final String playerId) {
        playerList.remove(UUID.fromString(playerId));
        broadcast();
    }

    public void nextQuestion() {
        progress++;
        if (gameModel.getQuestion(progress).isPresent()) {
            askQuestion();
        } else {
            gameState = SHOW_SCORES;
        }
        broadcast();
    }

    private void handlePlayersWithoutAnswer() {
        playerList.values()
                  .stream()
                  .filter(player -> !providedAnswers.containsKey(player) || !providedAnswers
                          .get(player)
                          .question()
                          .id()
                          .equals(currentQuestion.id()))
                  .forEach(player -> player.addAnswer(currentQuestion, EMPTY_ANSWER, 0));
    }

    public void addAnswer(final AnswerMessage answerMessage) {
        final UUID playerId = fromString(answerMessage.playerId());
        final UUID questionId = fromString(answerMessage.questionId());
        if (playerList.containsKey(playerId)) {
            checkAnswer(answerMessage, playerId, questionId);
        } else {
            throw new IllegalStateException("Player not in player list: " + playerId);
        }
        if (providedAnswers
                .values()
                .stream()
                .filter(providedAnswer -> providedAnswer.question().id().equals(questionId))
                .count() == playerList.size()) {
            if (null != currentTimer) {
                vertx.cancelTimer(currentTimer);
                currentTimer = null;
            }
            showScore();
        }
        broadcast();
    }

    private void showScore() {
        handlePlayersWithoutAnswer();
        gameState = GameState.ANSWERS_RECEIVED;
        broadcast();
    }

    private void checkAnswer(final AnswerMessage answerMessage, final UUID playerId, final UUID questionId) {
        final Player player = playerList.get(playerId);
        if (providedAnswers.containsKey(player) && providedAnswers
                .get(player)
                .question()
                .id()
                .equals(questionId)) {
            throw new IllegalStateException("Player already answered this question: " + player);
        }
        final ImmutableList<Answer> answers = currentQuestion.answers();
        final String providedAnswerValue = answerMessage.answer();
        final Answer providedAnswer = answers.stream()
                                             .filter(answer -> answer.value().equals(providedAnswerValue))
                                             .findFirst()
                                             .orElseThrow(() -> new IllegalStateException("Answer not in answers: " + providedAnswerValue + ", " + answers));
        providedAnswers.put(player, ImmutableProvidedAnswer.builder().question(currentQuestion).answer(providedAnswer).build());
        final Duration answerDuration = Duration.between(answerTime, OffsetDateTime.now());
        final Integer playerScore = computeScore(providedAnswer, currentQuestion.answerTime(), answerDuration.toMillis(),
                player.getStreakLength());
        player.addAnswer(currentQuestion, providedAnswer, playerScore);
    }

    private Integer computeScore(final Answer providedAnswer, final float maxDuration, final float duration, final int streakLength) {
        if (providedAnswer.correct()) {
            final int baseScore = (int) ((maxDuration - duration) / maxDuration * MAX_SCORE);
            final int streakScore = streakLength * STREAK_SCORE;
            return baseScore + streakScore;
        }
        return 0;
    }

    private void broadcast() {
        final JsonObject message = JsonObject.mapFrom(toSnapshot());
        vertx.eventBus().publish(GAME_UPDATE, message);
    }

    public GameSnapshot toSnapshot() {
        return ImmutableGameSnapshot.builder()
                                    .created(created.toString())
                                    .gameModel(gameModel)
                                    .gameState(gameState)
                                    .id(id)
                                    .joinKey(joinKey)
                                    .playerList(playerList
                                            .values()
                                            .stream()
                                            .sorted(Player::compareTo)
                                            .map(Player::toSnapshot)
                                            .collect(ImmutableList.toImmutableList()))
                                    .progress(progress)
                                    .build();
    }
}
