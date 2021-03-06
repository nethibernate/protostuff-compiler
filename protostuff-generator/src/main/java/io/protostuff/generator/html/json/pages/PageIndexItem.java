package io.protostuff.generator.html.json.pages;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutablePageIndexItem.class)
@JsonDeserialize(as = ImmutablePageIndexItem.class)
public interface PageIndexItem {

    String name();

    String ref();
}
