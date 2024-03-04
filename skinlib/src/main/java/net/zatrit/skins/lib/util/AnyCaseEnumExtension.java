package net.zatrit.skins.lib.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.jr.ob.JacksonJrExtension;
import com.fasterxml.jackson.jr.ob.api.ExtensionContext;
import com.fasterxml.jackson.jr.ob.api.ReaderWriterProvider;
import com.fasterxml.jackson.jr.ob.api.ValueReader;
import com.fasterxml.jackson.jr.ob.impl.JSONReader;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.val;

@AllArgsConstructor
public class AnyCaseEnumExtension extends JacksonJrExtension {
    private final Class<?> targetClass;
    private final Object[] variants;

    @Override
    protected void register(@NotNull ExtensionContext ctx) {
        ctx.insertProvider(new EnumReaderProvider());
    }

    class EnumReaderProvider extends ReaderWriterProvider {
        private final EnumReader reader = new EnumReader();

        @Override
        public ValueReader findValueReader(
            JSONReader readContext, Class<?> type) {
            if (type == targetClass) {
                return reader;
            }
            return super.findValueReader(readContext, type);
        }
    }

    class EnumReader extends ValueReader {
        protected EnumReader() {
            super(targetClass);
        }

        @Override
        public Object read(@NotNull JSONReader reader, JsonParser p)
            throws IOException {
            val value = reader.readValue();

            if (!(value instanceof String)) {
                throw new JsonParseException("Excepted string");
            }

            return Arrays.stream(variants).filter(v -> v.toString().toLowerCase()
                .equals(value)).findFirst();
        }
    }
}
