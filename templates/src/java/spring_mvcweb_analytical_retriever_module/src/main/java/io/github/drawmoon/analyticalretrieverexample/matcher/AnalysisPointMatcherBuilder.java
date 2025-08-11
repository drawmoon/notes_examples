package io.github.drawmoon.analyticalretrieverexample.matcher;

import io.github.drawmoon.analyticalretrieverexample.common.*;
import io.github.drawmoon.analyticalretrieverexample.proto.*;
import java.util.*;
import org.springframework.cache.annotation.Cacheable;

public class AnalysisPointMatcherBuilder extends MatcherBuilder<RawAnalysisPoint> {
  public AnalysisPointMatcherBuilder(Sequence<RawAnalysisPoint> seq) {
    super(seq);
  }

  public AnalysisPointMatcherBuilder withOnlyMeasures(List<String> measures) {
    return new AnalysisPointMatcherBuilder(apply(makeOnlyMeasuresMatcher(measures)).finish());
  }

  public AnalysisPointMatcherBuilder withOnlyDimensions(List<String> dimensions) {
    return new AnalysisPointMatcherBuilder(apply(makeOnlyDimensionsMatcher(dimensions)).finish());
  }

  public AnalysisPointMatcherBuilder withAllMetricsAnyOneDims(
      List<String> measures, List<String> dimensions) {
    return new AnalysisPointMatcherBuilder(
        apply(makeAllMetricsAnyOneDimsMatcher(measures, dimensions)).finish());
  }

  public AnalysisPointMatcherBuilder withStrictOrderMetricsDims(
      List<String> measures, List<String> dimensions) {
    return new AnalysisPointMatcherBuilder(
        apply(makeStrictOrderMetricsDimsMatcher(measures, dimensions)).finish());
  }

  public AnalysisPointMatcherBuilder withStatisticalPeriod(String statisticalPeriod) {
    return new AnalysisPointMatcherBuilder(
        apply(makeStatisticalPeriodMatcher(statisticalPeriod)).finish());
  }

  public AnalysisPointMatcherBuilder withAllFilterMembers(List<String> filterMembers) {
    return new AnalysisPointMatcherBuilder(
        apply(makeAllFilterMembersMatcher(filterMembers)).finish());
  }

  @Cacheable(cacheNames = "DEFAULT") // TODO Not in effect
  private static Matcher<RawAnalysisPoint> makeOnlyMeasuresMatcher(List<String> measures) {
    return null;
  }

  @Cacheable(cacheNames = "DEFAULT")
  private static Matcher<RawAnalysisPoint> makeOnlyDimensionsMatcher(List<String> dimensions) {
    return null;
  }

  @Cacheable(cacheNames = "DEFAULT")
  private static Matcher<RawAnalysisPoint> makeAllMetricsAnyOneDimsMatcher(
      List<String> measures, List<String> dimensions) {
    return null;
  }

  @Cacheable(cacheNames = "DEFAULT")
  private static Matcher<RawAnalysisPoint> makeStrictOrderMetricsDimsMatcher(
      List<String> measures, List<String> dimensions) {
    return null;
  }

  @Cacheable(cacheNames = "DEFAULT")
  public static Matcher<RawAnalysisPoint> makeStatisticalPeriodMatcher(String statisticalPeriod) {
    return null;
  }

  @Cacheable(cacheNames = "DEFAULT")
  public static Matcher<RawAnalysisPoint> makeAllFilterMembersMatcher(List<String> filterMembers) {
    return null;
  }
}
