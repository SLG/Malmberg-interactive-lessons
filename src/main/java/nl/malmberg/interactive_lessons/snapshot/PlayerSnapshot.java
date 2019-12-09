package nl.malmberg.interactive_lessons.snapshot;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import nl.malmberg.interactive_lessons.model.ProvidedAnswer;
import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonSerialize(as = ImmutablePlayerSnapshot.class)
@JsonDeserialize(as = ImmutablePlayerSnapshot.class)
public interface PlayerSnapshot {
    @Value.Default
    default UUID id() {
        return UUID.randomUUID();
    }

    String name();

    ImmutableList<ProvidedAnswer> answers();

    int score();

    int streakLength();
}
