package nl.malmberg.interactive_lessons.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import org.immutables.value.Value;

import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonSerialize(as = ImmutableGameModel.class)
@JsonDeserialize(as = ImmutableGameModel.class)
public interface GameModel {
    UUID id();

    String name();

    String description();

    ImmutableList<Question> questions();

    default Optional<Question> getQuestion(final int index) {
        if (questions().size() <= index) {
            return empty();
        }
        return of(questions().get(index));
    }
}
