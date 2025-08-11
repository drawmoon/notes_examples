package io.github.drawmoon.analyticalretrieverexample.controller.input;

import io.github.drawmoon.analyticalretrieverexample.common.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class AnalysisInput extends SnakeModel {

  private String query;
  private String domain = "default";
  private String v = "latest";
  private Integer minK = 0;
  private Integer maxK = 10;
  private Boolean noCache = Boolean.FALSE;
}
