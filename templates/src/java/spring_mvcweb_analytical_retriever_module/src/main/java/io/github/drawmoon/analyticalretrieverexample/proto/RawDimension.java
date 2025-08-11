package io.github.drawmoon.analyticalretrieverexample.proto;

import static java.util.Objects.*;

import io.github.drawmoon.analyticalretrieverexample.common.*;
import java.util.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class RawDimension extends Metadata {

  private String id;
  private String name;
  private String title;
  private String description;
  private List<String> labels;
  private String type;
  private List<String> highlight;

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
