package nl.malmberg.interactive_lessons.game_state;

import com.google.common.collect.ImmutableList;
import nl.malmberg.interactive_lessons.model.Answer;
import nl.malmberg.interactive_lessons.model.ImmutableProvidedAnswer;
import nl.malmberg.interactive_lessons.model.Question;
import nl.malmberg.interactive_lessons.snapshot.ImmutablePlayerSnapshot;
import nl.malmberg.interactive_lessons.snapshot.PlayerSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Player {
    private final String name;

    private final UUID id = UUID.randomUUID();
    private final Map<Question, Answer> answers = new HashMap<>();

    private int score;
    private int streakLength = 0;

    public Player(final String name) {
        this.name = name;
    }

    public void addAnswer(final Question question, final Answer answer, final Integer score) {
        this.score += score;
        answers.put(question, answer);
        streakLength = answer.correct() ? streakLength + 1 : 0;
    }

    public UUID getId() {
        return id;
    }

    public int getStreakLength() {
        return streakLength;
    }

    public PlayerSnapshot toSnapshot() {
        return ImmutablePlayerSnapshot
                .builder()
                .id(id)
                .name(name)
                .answers(answers
                        .entrySet()
                        .stream()
                        .map(entry -> ImmutableProvidedAnswer
                                .builder()
                                .question(entry.getKey())
                                .answer(entry.getValue())
                                .build())
                        .collect(ImmutableList.toImmutableList()))
                .score(score)
                .streakLength(streakLength)
                .build();
    }

    public static int compareTo(final Player a, final Player b) {
        if (null != a && null != b) {
            return Integer.compare(b.score, a.score);
        }
        return 0;
    }
}
