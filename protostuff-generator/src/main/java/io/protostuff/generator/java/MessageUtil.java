package io.protostuff.generator.java;

import io.protostuff.compiler.model.DynamicMessage;
import io.protostuff.compiler.model.Field;
import io.protostuff.compiler.model.Message;
import io.protostuff.compiler.model.Oneof;
import io.protostuff.compiler.parser.ExtensionRegistry;
import io.protostuff.generator.Formatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Custom message properties for java code generator.
 *
 * @author Kostiantyn Shchepanovskyi
 */
public class MessageUtil {

    private MessageUtil() {
        throw new IllegalAccessError("Utility class");
    }

    public static boolean hasFields(Message message) {
        return !message.getFields().isEmpty();
    }

    /**
     * Returns a list of bit fields used for field presence checks.
     */
    public static List<String> bitFieldNames(Message message) {
        int fieldCount = message.getFieldCount();
        if (fieldCount == 0) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        int n = (fieldCount - 1) / 32 + 1;
        for (int i = 0; i < n; i++) {
            result.add("__bitField" + i);
        }
        return result;
    }

    /**
     * Return message options.
     */
    public static List<String> getMessageOptions(Message message) {
        List<String> result = new ArrayList<>();
        ExtensionRegistry extension = message.getProto().getContext().getExtensionRegistry();
        Map<String, Field> extensionFields = extension.getExtensionFields(".google.protobuf.MessageOptions");
        DynamicMessage options = message.getOptions();
        for (Map.Entry<DynamicMessage.Key, DynamicMessage.Value> each : options.getFields()) {
            if (!each.getKey().isExtension()) {
                continue;
            }
            String name = each.getKey().getName();
            Field field = extensionFields.get("." + name);
            String fieldType = MessageFieldUtil.getFieldType(field);
            String s = "public static final " + fieldType + " " + MessageFieldUtil.getFieldName(field) + " = ";
            if (each.getValue().isEnumType()) {
                s += fieldType + "." + each.getValue();
            } else if (each.getValue().isMessageType()) {
                throw new IllegalStateException("Custom Options cannot support message type now!");
            } else {
                s += each.getKey().getName();
            }
            s += ";";
            result.add(s);
        }
        return result;
    }

    public static String getOneofEnumClassName(Oneof oneof) {
        String name = oneof.getName();
        return Formatter.toPascalCase(name) + "Case";
    }

    /**
     * Returns a "not set" name for one-of enum constant.
     */
    public static String getOneofNotSetConstantName(Oneof oneof) {
        String name = oneof.getName();
        String underscored = Formatter.toUnderscoreCase(name);
        return Formatter.toUpperCase(underscored) + "_NOT_SET";
    }

    public static String getOneofCaseGetterName(Oneof oneof) {
        String name = oneof.getName();
        return "get" + Formatter.toPascalCase(name) + "Case";
    }

    public static String getOneofCaseCleanerName(Oneof oneof) {
        String name = oneof.getName();
        return "clear" + Formatter.toPascalCase(name);
    }

    public static String getOneofFieldName(Oneof oneof) {
        String name = oneof.getName();
        return Formatter.toCamelCase(name) + "__";
    }

    public static String getOneofCaseFieldName(Oneof oneof) {
        String name = oneof.getName();
        return Formatter.toCamelCase(name) + "Case__";
    }
}
