package io.github.drawmoon.analyticalretrieverexample.proto;

import static java.util.Objects.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.drawmoon.analyticalretrieverexample.common.*;
import java.util.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AnalysisGraph extends SnakeModel {

  private List<AnalysisGraphNode> nodes;

  @JsonIgnore
  public Iterator<AnalysisGraphNode> nodeIterator() {
    if (isNull(nodes)) {
      return Collections.emptyIterator();
    }
    return nodes.iterator();
  }

  @JsonIgnore
  public boolean isSingleAnalysis() {
    return true;
  }
}
