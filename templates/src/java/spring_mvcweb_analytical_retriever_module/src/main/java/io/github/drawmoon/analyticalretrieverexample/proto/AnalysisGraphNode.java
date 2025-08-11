package io.github.drawmoon.analyticalretrieverexample.proto;

import io.github.drawmoon.analyticalretrieverexample.common.*;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AnalysisGraphNode extends SnakeModel {

  private String id;
  private String name;
  private String type;
  private Map<String, Object> properties;
}
