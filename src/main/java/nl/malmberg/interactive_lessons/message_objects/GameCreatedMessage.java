package nl.malmberg.interactive_lessons.message_objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonSerialize(as = ImmutableGameCreatedMessage.class)
@JsonDeserialize(as = ImmutableGameCreatedMessage.class)
public interface GameCreatedMessage {
    String gameId();

    String joinKey();
}
