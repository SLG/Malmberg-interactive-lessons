package nl.malmberg.interactive_lessons.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonSerialize(as = ImmutableProvidedAnswer.class)
@JsonDeserialize(as = ImmutableProvidedAnswer.class)
public interface ProvidedAnswer {
    Question question();

    Answer answer();
}
