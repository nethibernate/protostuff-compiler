package io.protostuff.generator.java;

import io.protostuff.compiler.model.DynamicMessage;
import io.protostuff.compiler.model.Enum;
import io.protostuff.compiler.model.EnumConstant;
import io.protostuff.compiler.model.Field;
import io.protostuff.compiler.parser.ExtensionRegistry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Custom enum properties for java generator.
 *
 * @author Kostiantyn Shchepanovskyi
 */
public class EnumUtil {

    private EnumUtil() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Returns constant name for java enum.
     */
    public static String getName(EnumConstant constant) {
        return constant.getName();
    }

    /**
     * Return enum value options.
     */
    public static List<String> getEnumValueOptions(Enum enumModel) {
        ExtensionRegistry extension = enumModel.getProto().getContext().getExtensionRegistry();
        Map<String, Field> extensionFields = extension.getExtensionFields(".google.protobuf.EnumValueOptions");
        HashSet<String> set = new HashSet<>();
        List<String> result = new ArrayList<>();
        for (EnumConstant constant : enumModel.getConstants()) {
            DynamicMessage options = constant.getOptions();
            if (options.size() == 0) {
                continue;
            }

            for (Map.Entry<DynamicMessage.Key, DynamicMessage.Value> each : options.getFields()) {
                if (!each.getKey().isExtension()) {
                    continue;
                }
                String name = each.getKey().getName();
                Field field = extensionFields.get("." + name);
                String fieldType = MessageFieldUtil.getFieldType(field);
                String fieldName = MessageFieldUtil.getFieldName(field);
                if (set.contains(fieldName)) {
                    continue;
                }
                set.add(fieldName);
                result.add("public " + fieldType + " " + fieldName + ";");
            }
        }
        return result;
    }

    /**
     * Return enum value options.
     */
    public static String getStaticInitEnumValueOptions(Enum enumModel) {
        ExtensionRegistry extension = enumModel.getProto().getContext().getExtensionRegistry();
        Map<String, Field> extensionFields = extension.getExtensionFields(".google.protobuf.EnumValueOptions");
        HashSet<String> set = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        for (EnumConstant constant : enumModel.getConstants()) {
            DynamicMessage options = constant.getOptions();
            if (options.size() == 0) {
                continue;
            }

            for (Map.Entry<DynamicMessage.Key, DynamicMessage.Value> each : options.getFields()) {
                if (each.getValue().isMessageType()) {
                    throw new IllegalStateException("Custom Options cannot support message type now!");
                }
                if (!each.getKey().isExtension()) {
                    continue;
                }
                String name = each.getKey().getName();
                Field field = extensionFields.get("." + name);
                String fieldName = MessageFieldUtil.getFieldName(field);
                if (set.contains(fieldName)) {
                    continue;
                }
                set.add(fieldName);
                if (sb.length() == 0) {
                    sb.append("static { \n");
                }
                sb.append("\t").append(constant.getName()).append(".").append(fieldName).append(" = ").append(each.getValue().toString()).append(";\n");
            }
            set.clear();
        }
        if (sb.length() > 0) {
            sb.append("}");
        }
        return sb.toString();
    }
}
