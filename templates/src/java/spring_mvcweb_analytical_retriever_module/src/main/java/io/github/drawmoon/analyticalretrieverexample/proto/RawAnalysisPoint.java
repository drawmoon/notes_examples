package io.github.drawmoon.analyticalretrieverexample.proto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.github.drawmoon.analyticalretrieverexample.common.Metadata;
import java.util.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class RawAnalysisPoint extends Metadata {

  private String id;
  private String name;
  private String title;
  private String description;
  private String chartType;
  private Map<String, Object> granularity;
  private List<String> availableChartTypes;
  private Integer useTimes;
  private Map<String, Object> attributes;

  @JsonAlias({"filters", "filter_members", "filterMembers"})
  private List<Map<String, Object>>
      filters; // FilterMember, BinaryFilterObject are all possible types

  @JsonAlias({"metrics", "measures"})
  private List<RawMeasure> metrics;

  private List<RawDimension> dimensions;
}
