package io.github.drawmoon.analyticalretrieverexample.proto;

import io.github.drawmoon.analyticalretrieverexample.common.*;
import java.util.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class BinaryFilterObject extends Metadata {

  private String member;
  private String operator;
  private List<String> values;
  private String fragment;
  private String comment;
  private List<String> altMembers;
}
