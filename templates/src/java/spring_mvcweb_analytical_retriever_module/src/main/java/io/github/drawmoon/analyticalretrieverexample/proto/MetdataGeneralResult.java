package io.github.drawmoon.analyticalretrieverexample.proto;

import io.github.drawmoon.analyticalretrieverexample.common.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class MetdataGeneralResult<T> extends BaseModel {

  private Boolean success;
  private String code;
  private T data;

  public T ok() {
    boolean ok = data != null && Boolean.TRUE.equals(success) && "0000".equals(code);
    if (!ok) {
      throw new RuntimeException("Failed to fetch data from AnalysisPoint service");
    }
    return data;
  }
}
