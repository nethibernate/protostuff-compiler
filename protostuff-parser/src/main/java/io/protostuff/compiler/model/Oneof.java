package io.protostuff.compiler.model;

import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * "oneof" tree node.
 *
 * @author Kostiantyn Shchepanovskyi
 */
public class Oneof extends AbstractDescriptor implements FieldContainer, GroupContainer {

    protected final Message parent;
    protected List<Field> fields;
    protected List<Group> groups;

    protected String namespace;

    public Oneof(Message parent) {
        this.parent = parent;
    }

    @Override
    public DescriptorType getDescriptorType() {
        return DescriptorType.ONEOF;
    }

    @Override
    public Message getParent() {
        return parent;
    }

    public String getNamespace() {
        return parent.getNamespace();
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public List<Field> getFields() {
        if (fields == null) {
            return Collections.emptyList();
        }
        return fields;
    }

    @Override
    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    @Override
    public int getFieldCount() {
        if (fields == null) {
            return 0;
        }
        return fields.size();
    }

    @Override
    public void addField(Field field) {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        fields.add(field);
        parent.addField(field);
    }

    @Override
    public Field getField(String name) {
        for (Field field : getFields()) {
            if (name.equals(field.getName())) {
                return field;
            }
        }
        return null;
    }

    @Override
    public Field getField(int tag) {
        for (Field field : getFields()) {
            if (tag == field.getTag()) {
                return field;
            }
        }
        return null;
    }

    @Override
    public List<Group> getGroups() {
        if (groups == null) {
            return Collections.emptyList();
        }
        return groups;
    }

    @Override
    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @Override
    public void addGroup(Group group) {
        if (groups == null) {
            groups = new ArrayList<>();
        }
        groups.add(group);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("name", name)
                .add("namespace", namespace)
                .add("fields", fields)
                .add("options", getOptions())
                .toString();
    }

}
