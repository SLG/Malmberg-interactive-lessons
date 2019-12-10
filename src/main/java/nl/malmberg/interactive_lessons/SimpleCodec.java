package nl.malmberg.interactive_lessons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageCodec;

import java.io.IOException;
import java.util.stream.Stream;

public class SimpleCodec<T> implements MessageCodec<T, T> {
    private final Class<T> clazz;
    private final ObjectMapper mapper = new ObjectMapper().enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

    public SimpleCodec(final Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T decodeFromWire(final int pos, final Buffer buffer) {
        final int start = pos + 4;
        final int end = start + buffer.getInt(pos);

        try {
            final String json = buffer.getString(start, end);
            return mapper.readValue(json, clazz);
        } catch (final IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void encodeToWire(final Buffer buffer, final T s) {
        try {
            final String json = mapper.writeValueAsString(s);
            buffer.appendInt(json.getBytes().length);
            buffer.appendString(json);
        } catch (final JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String name() {
        return clazz.getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }

    @Override
    public T transform(final T o) {
        return o;
    }

    public static void register(final EventBus eventBus, final Class<?>... clazzes) {
        Stream.of(clazzes).forEach(c -> register(eventBus, c));
    }

    public static <C> void register(final EventBus eventBus, final Class<C> clazz) {
        /* Unregister in case already registered */
        eventBus.unregisterDefaultCodec(clazz);
        eventBus.registerDefaultCodec(clazz, new SimpleCodec<>(clazz));
    }

}