package nl.malmberg.interactive_lessons;

import nl.malmberg.interactive_lessons.model.GameModel;
import nl.malmberg.interactive_lessons.model.ImmutableAnswer;
import nl.malmberg.interactive_lessons.model.ImmutableGameModel;
import nl.malmberg.interactive_lessons.model.ImmutableQuestion;
import nl.malmberg.interactive_lessons.model.Question;
import nl.malmberg.interactive_lessons.model.QuestionType;

import java.util.UUID;

public final class MockData {
    public static final long DEFAULT_QUESTION_TIME = 5000;
    public static final long DEFAULT_ANSWER_TIME = 10000;

    private MockData() {
        //Hidden
    }

    public static GameModel mockGameModel() {
        return ImmutableGameModel.builder()
                                 .id(UUID.fromString("4c72ecbb-1eee-4586-99d0-1c1f3b37c1df"))
                                 .name("Interactieve les")
                                 .description("Dit is een voorbeeld les")
                                 .addQuestions(mockQuestion1())
                                 .addQuestions(mockQuestion2())
                                 .addQuestions(mockQuestion3())
                                 .addQuestions(mockQuestion4())
                                 .addQuestions(mockQuestion5())
                                 .build();
    }

    private static Question mockQuestion1() {
        return ImmutableQuestion.builder()
                                .questionType(QuestionType.MULTIPLE_CHOICE)
                                .value("3 * 3 = ?")
                                .addAnswers(ImmutableAnswer.builder().value("6").correct(false).build())
                                .addAnswers(ImmutableAnswer.builder().value("8").correct(true).build())
                                .addAnswers(ImmutableAnswer.builder().value("9").correct(false).build())
                                .questionTime(DEFAULT_QUESTION_TIME)
                                .answerTime(DEFAULT_ANSWER_TIME)
                                .build();
    }

    private static Question mockQuestion2() {
        return ImmutableQuestion.builder()
                                .questionType(QuestionType.MULTIPLE_CHOICE)
                                .value("6 * 4 = ?")
                                .addAnswers(ImmutableAnswer.builder().value("24").correct(true).build())
                                .addAnswers(ImmutableAnswer.builder().value("28").correct(false).build())
                                .addAnswers(ImmutableAnswer.builder().value("30").correct(false).build())
                                .questionTime(DEFAULT_QUESTION_TIME)
                                .answerTime(DEFAULT_ANSWER_TIME)
                                .build();
    }

    private static Question mockQuestion3() {
        return ImmutableQuestion.builder()
                                .questionType(QuestionType.MULTIPLE_CHOICE)
                                .value("9 * 4 = ?")
                                .addAnswers(ImmutableAnswer.builder().value("32").correct(false).build())
                                .addAnswers(ImmutableAnswer.builder().value("35").correct(false).build())
                                .addAnswers(ImmutableAnswer.builder().value("36").correct(true).build())
                                .questionTime(DEFAULT_QUESTION_TIME)
                                .answerTime(DEFAULT_ANSWER_TIME)
                                .build();
    }

    private static Question mockQuestion4() {
        return ImmutableQuestion.builder()
                                .questionType(QuestionType.TRUE_OR_FALSE)
                                .value("8 * 2 = 16")
                                .addAnswers(ImmutableAnswer.builder().value("True").correct(true).build())
                                .addAnswers(ImmutableAnswer.builder().value("False").correct(false).build())
                                .questionTime(DEFAULT_QUESTION_TIME)
                                .answerTime(DEFAULT_ANSWER_TIME)
                                .build();
    }

    private static Question mockQuestion5() {
        return ImmutableQuestion.builder()
                                .questionType(QuestionType.TRUE_OR_FALSE)
                                .value("9 * 3 = 26")
                                .addAnswers(ImmutableAnswer.builder().value("True").correct(false).build())
                                .addAnswers(ImmutableAnswer.builder().value("False").correct(true).build())
                                .questionTime(DEFAULT_QUESTION_TIME)
                                .answerTime(DEFAULT_ANSWER_TIME)
                                .build();
    }
}
