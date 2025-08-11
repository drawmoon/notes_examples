package io.github.drawmoon.analyticalretrieverexample.proto;

import io.github.drawmoon.analyticalretrieverexample.common.*;
import java.util.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisTree extends SnakeModel {

  private List<AnalysisTreeNode> children;
  private List<Object> analysisPoints;
  private String childType;
  private List<Object> childFilters;
}
