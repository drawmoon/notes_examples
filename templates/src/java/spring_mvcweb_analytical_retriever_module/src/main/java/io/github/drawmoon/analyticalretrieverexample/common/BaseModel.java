package io.github.drawmoon.analyticalretrieverexample.common;

import com.fasterxml.jackson.annotation.*;
import java.io.Serializable;
import java.util.*;
import lombok.*;

/** Base class for all models. */
@Data
@JsonIgnoreProperties(value = {"modelExtra"})
public abstract class BaseModel implements Serializable {

  /** A dictionary containing extra values that are not explicitly defined in the model. */
  protected LinkedHashMap<String, Object> modelExtra = new LinkedHashMap<>();

  /**
   * Handles deserialization of unknown properties not defined in the model class. This method is
   * called by Jackson when encountering JSON fields without corresponding class fields. Stores
   * these unexpected values in the modelExtra map to preserve data integrity and prevent
   * information loss during deserialization.
   */
  @JsonAnySetter
  public void handleUnknown(String key, Object value) {
    modelExtra.put(key, value);
  }

  /**
   * Enables serialization of dynamic properties back to JSON format. This method is called by
   * Jackson during serialization to include all extra properties stored in modelExtra. Ensures that
   * any additional data captured during deserialization is preserved in the output JSON.
   */
  @JsonAnyGetter
  public HashMap<String, Object> handleModelExtraDump() {
    return modelExtra;
  }
}
