package org.bs.jnonogram.wui.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResponseUtils {
    public static void errorResponse(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(code);
        writeResponse(response.getWriter(), ApiError.class, new ApiError(code, message));
    }
    public static <T> void writeResponse(Writer writer, Class<T> tClass, T ... entities) throws IOException {
        writeResponse(writer, tClass, Arrays.asList(entities));
    }

    public static <T> void writeResponse(Writer writer, Class<T> tClass, Stream<T> entities) throws IOException {
        writeResponse(writer, tClass, entities.collect(Collectors.toList()));
    }

    public static <T> void writeResponse(Writer writer, Class<T> tClass, Iterable<T> entities) throws IOException {
        ApiEntityName entityName = tClass.getAnnotation(ApiEntityName.class);

        Gson gson = new GsonBuilder().create();
        JsonWriter gsonWriter = gson.newJsonWriter(writer);
        gsonWriter.beginObject();
        gsonWriter.name(entityName.plural());
        gsonWriter.beginArray();
        for (T entity: entities) {
            gsonWriter.jsonValue(gson.toJson(entity));
        }

        gsonWriter.endArray();
        gsonWriter.endObject();
        gsonWriter.close();
    }
}
