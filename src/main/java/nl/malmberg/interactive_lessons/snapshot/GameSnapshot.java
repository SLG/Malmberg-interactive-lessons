package nl.malmberg.interactive_lessons.snapshot;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import nl.malmberg.interactive_lessons.model.GameModel;
import nl.malmberg.interactive_lessons.model.GameState;
import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonSerialize(as = ImmutableGameSnapshot.class)
@JsonDeserialize(as = ImmutableGameSnapshot.class)
public interface GameSnapshot {
    UUID id();

    GameModel gameModel();

    String created();

    String joinKey();

    ImmutableList<PlayerSnapshot> playerList();

    GameState gameState();

    int progress();
}
