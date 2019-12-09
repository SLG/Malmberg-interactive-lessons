package nl.malmberg.interactive_lessons.message_objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonSerialize(as = ImmutableLeaveGameMessage.class)
@JsonDeserialize(as = ImmutableLeaveGameMessage.class)
public interface LeaveGameMessage {
    String gameId();

    String playerId();
}
