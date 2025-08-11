package io.github.drawmoon.analyticalretrieverexample.proto;

import io.github.drawmoon.analyticalretrieverexample.common.*;
import java.util.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisTreeNode extends SnakeModel {

  private String name;
  private String value;
  private String type;
  private List<AnalysisTreeNode> children;
  private List<String> measures;
  private List<String> dimensions;
  private List<String> nonPeriodicMeasures;
  private List<Map<String, Object>> analysisPoints;
  private List<Map<String, Object>> analysisPointsNotFound;
  private String status;
  private String statisticalPeriod;
  private String childType;
  private List<BinaryFilterObject> childFilters;
}
