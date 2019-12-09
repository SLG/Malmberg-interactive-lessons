package nl.malmberg.interactive_lessons.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonSerialize(as = ImmutableQuestion.class)
@JsonDeserialize(as = ImmutableQuestion.class)
public interface Question {
    @Value.Default
    default UUID id() {
        return UUID.randomUUID();
    }

    QuestionType questionType();

    String value();

    ImmutableList<Answer> answers();

    long questionTime();

    long answerTime();
}
