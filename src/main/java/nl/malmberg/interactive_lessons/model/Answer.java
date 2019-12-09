package nl.malmberg.interactive_lessons.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonSerialize(as = ImmutableAnswer.class)
@JsonDeserialize(as = ImmutableAnswer.class)
public interface Answer {
    String value();

    boolean correct();
}
