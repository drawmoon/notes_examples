package io.github.drawmoon.analyticalretrieverexample.proto;

import io.github.drawmoon.analyticalretrieverexample.common.*;
import java.util.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GenerateStage extends SnakeModel {

  private String subject;
  private String subjectClass;
  private List<Map<String, Object>> attributes;
  private Map<String, Object> ranking;
  private List<BinaryFilterObject> filters;
  private AnalysisTree analysisTree;
  private AnalysisGraph graph;
}
