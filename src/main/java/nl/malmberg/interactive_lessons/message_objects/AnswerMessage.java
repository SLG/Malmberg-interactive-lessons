package nl.malmberg.interactive_lessons.message_objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonSerialize(as = ImmutableAnswerMessage.class)
@JsonDeserialize(as = ImmutableAnswerMessage.class)
public interface AnswerMessage {
    String gameId();

    String playerId();

    String questionId();

    String answer();
}
