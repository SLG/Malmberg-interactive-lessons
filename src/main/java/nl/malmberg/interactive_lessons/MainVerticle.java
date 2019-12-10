package nl.malmberg.interactive_lessons;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

public class MainVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

    private static final String ROUTE_EVENTBUS = "/eventbus/*";

    /* CORS HEADERS */
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    private static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";

    private EventBusHandler eventbusHandler;

    @Override
    public void start(final Promise<Void> startPromise) {
        eventbusHandler = new EventBusHandler(vertx);
        final Router router = Router.router(vertx);

        initCorsHeaders(router);
        initSockJSEventBus(router);
        initEventBus();

        router.route().handler(StaticHandler.create());
        vertx.createHttpServer().requestHandler(router).listen(8888, ar -> {
            if (ar.succeeded()) {
                LOG.info("HttpServer started and listening on " + ar.result().actualPort());
                startPromise.complete();
            } else {
                LOG.error("HttpServer not started: {}", ar.cause());
                startPromise.fail(ar.cause());
            }
        });
    }

    private void initCorsHeaders(final Router router) {
        router.route().handler(CorsHandler.create("http://localhost:4200")
                                          .allowedMethod(HttpMethod.GET)
                                          .allowedMethod(HttpMethod.POST)
                                          .allowedMethod(HttpMethod.OPTIONS)
                                          .allowedHeader(ACCESS_CONTROL_REQUEST_METHOD)
                                          .allowedHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS)
                                          .allowedHeader(ACCESS_CONTROL_ALLOW_ORIGIN)
                                          .allowedHeader(ACCESS_CONTROL_ALLOW_HEADERS)
                                          .allowedHeader(CONTENT_TYPE));
    }

    // you will need to allow outbound and inbound to allow eventbus communication.
    private void initSockJSEventBus(final Router router) {
        router.route(ROUTE_EVENTBUS).subRouter(SockJSHandler.create(vertx)
                                                            .bridge(Topic.addInAndOutboundPermittedOptions(new BridgeOptions())));
    }

    private void initEventBus() {
        vertx.eventBus().consumer(Topic.CREATE_GAME, eventbusHandler::handleCreateGame);
        vertx.eventBus().consumer(Topic.JOIN_GAME, eventbusHandler::handleJoinGame);
        vertx.eventBus().consumer(Topic.LEAVE_GAME, eventbusHandler::handleLeaveGame);
        vertx.eventBus().consumer(Topic.START_GAME, eventbusHandler::handleStartGame);
        vertx.eventBus().consumer(Topic.ANSWER, eventbusHandler::handleAnswer);
        vertx.eventBus().consumer(Topic.GET_GAME, eventbusHandler::handleGetGame);
        vertx.eventBus().consumer(Topic.NEXT_QUESTION, eventbusHandler::handleNextQuestion);
    }
} 