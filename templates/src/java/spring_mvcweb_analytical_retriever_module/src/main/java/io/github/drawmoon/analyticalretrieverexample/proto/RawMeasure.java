package io.github.drawmoon.analyticalretrieverexample.proto;

import static java.util.Objects.*;

import com.fasterxml.jackson.annotation.*;
import io.github.drawmoon.analyticalretrieverexample.common.*;
import java.util.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class RawMeasure extends Metadata {

  private String id;
  private String name;
  private String title;
  private String description;
  private List<String> labels;
  private String type;

  @JsonAlias({"filters", "filter_members", "filterMembers"})
  private List<Map<String, Object>>
      filters; // FilterMember, BinaryFilterObject are all possible types

  // Composite measure
  @JsonAlias({"metrics", "measures"})
  private List<RawMeasure> metrics;

  public boolean match(String label) {
    if (Objects.equals(label, name)) {
      return true;
    }
    if (Objects.equals(label, title)) {
      return true;
    }
    if (isNull(labels) || labels.isEmpty()) {
      return false;
    }
    return labels.contains(label);
  }
}
