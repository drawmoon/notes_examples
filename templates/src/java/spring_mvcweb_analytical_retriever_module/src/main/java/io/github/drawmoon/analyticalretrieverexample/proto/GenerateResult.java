package io.github.drawmoon.analyticalretrieverexample.proto;

import io.github.drawmoon.analyticalretrieverexample.common.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class GenerateResult extends SnakeModel {

  private Object version;
  private String query;
  private String title;
  private GenerateStage staging;
}
