package nl.malmberg.interactive_lessons;

import com.google.common.collect.ImmutableList;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;

public final class Topic {
    public static final String CREATE_GAME = "topic.create_game";
    public static final String JOIN_GAME = "topic.join_game";
    public static final String LEAVE_GAME = "topic.leave_game";
    public static final String START_GAME = "topic.start_game";
    public static final String ANSWER = "topic.answer";
    public static final String NEXT_QUESTION = "topic.next_question";
    public static final String GET_GAME = "topic.get_game";
    public static final String GAME_UPDATE = "game.update";
    public static final String ADD_GAME_MODEL = "game_model.add_game_model";
    public static final String GET_GAME_MODEL = "game_model.get_game_model";

    private Topic() {
        // Hidden
    }

    public static BridgeOptions addInAndOutboundPermittedOptions(final BridgeOptions bridgeOptions) {
        getPermittedOptions(getInbound()).forEach(bridgeOptions::addInboundPermitted);
        getPermittedOptions(getOutbound()).forEach(bridgeOptions::addOutboundPermitted);
        return bridgeOptions;
    }

    private static ImmutableList<String> getOutbound() {
        return ImmutableList.of(GAME_UPDATE);
    }

    private static ImmutableList<String> getInbound() {
        return ImmutableList.of(CREATE_GAME, JOIN_GAME, LEAVE_GAME, START_GAME, ANSWER, GET_GAME, NEXT_QUESTION);
    }

    private static ImmutableList<PermittedOptions> getPermittedOptions(final ImmutableList<String> topics) {
        return topics.stream().map(topic -> new PermittedOptions().setAddress(topic)).collect(ImmutableList.toImmutableList());
    }
}
