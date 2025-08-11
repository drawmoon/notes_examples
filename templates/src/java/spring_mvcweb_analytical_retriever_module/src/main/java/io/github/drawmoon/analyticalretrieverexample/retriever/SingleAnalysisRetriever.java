package io.github.drawmoon.analyticalretrieverexample.retriever;

import static io.github.drawmoon.analyticalretrieverexample.common.Sequence.*;
import static java.util.Objects.*;

import com.google.common.collect.Lists;
import io.github.drawmoon.analyticalretrieverexample.common.*;
import io.github.drawmoon.analyticalretrieverexample.matcher.AnalysisPointMatcherBuilder;
import io.github.drawmoon.analyticalretrieverexample.proto.*;
import io.github.drawmoon.analyticalretrieverexample.service.*;
import java.util.*;
import java.util.stream.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SingleAnalysisRetriever implements RetrieverLike {

  private MetadataService store;
  private String domain;
  private List<BinaryFilterObject> globalFilters;

  public SingleAnalysisRetriever(
      MetadataService store, String domain, List<BinaryFilterObject> globalFilters) {
    this.store = store;
    this.domain = domain;
    this.globalFilters = globalFilters;
  }

  @Override
  public List<Metadata> retrieveRelevantMetadata(List<Metadata> metadataElements) {
    List<RawAnalysisPoint> allAnalysisPoints = store.allMetadata(domain);
    if (isNull(allAnalysisPoints)) {
      log.info("Domain '{}' no analysis points found", domain);
      return Collections.emptyList();
    }

    AnalysisPointMatcherBuilder builder = new AnalysisPointMatcherBuilder(it(allAnalysisPoints));

    List<RawMeasure> metrics =
        metadataElements.stream()
            .filter(m -> m instanceof RawMeasure)
            .map(m -> (RawMeasure) m)
            .collect(Collectors.toList());
    List<RawDimension> dimensions =
        metadataElements.stream()
            .filter(m -> m instanceof RawDimension)
            .map(m -> (RawDimension) m)
            .collect(Collectors.toList());

    if (metrics.isEmpty() && dimensions.isEmpty()) {
      log.info("No metrics or dimensions specified");
      return Collections.emptyList();
    } else {
      AnalysisPointMatcherBuilder candidateBuilder = null;

      // filter by metrics or dimensions
      if (!metrics.isEmpty()) {
        List<String> expectedMetrics =
            metrics.stream().map(m -> m.getName()).collect(Collectors.toList());

        if (dimensions.isEmpty()) {
          log.info("Building candidate with only metrics: {}", expectedMetrics);
          candidateBuilder = builder.withOnlyMeasures(expectedMetrics);
        } else {
          List<String> expectedDimensions =
              dimensions.stream().map(m -> m.getName()).collect(Collectors.toList());

          log.info(
              "Building candidate with metrics and dimensions. Metrics: {}, Dimensions: {}",
              expectedMetrics,
              expectedDimensions);
          candidateBuilder =
              builder.withStrictOrderMetricsDims(expectedMetrics, expectedDimensions);
        }
      } else {
        List<String> expectedDimensions =
            dimensions.stream().map(m -> m.getName()).collect(Collectors.toList());

        log.info("Building candidate with only dimensions: {}", expectedDimensions);
        candidateBuilder = builder.withOnlyDimensions(expectedDimensions);
      }

      if (isNull(candidateBuilder)) {
        log.info("No candidates AnalysisPoint found for query");
        return Collections.emptyList();
      }

      // filter by filter members
      List<BinaryFilterObject> metricFilters =
          metadataElements.stream()
              .filter(m -> m instanceof BinaryFilterObject)
              .map(m -> (BinaryFilterObject) m)
              .collect(Collectors.toList());
      if (!metricFilters.isEmpty()) {
        List<String> expectedFilters =
            metricFilters.stream().map(BinaryFilterObject::getMember).collect(Collectors.toList());
        log.info("Building candidate with all filter members: {}", expectedFilters);

        candidateBuilder = candidateBuilder.withAllFilterMembers(expectedFilters);
      } else {
        log.info("No metric filters specified");
      }

      // filter by statistical period
      BinaryFilterObject statisticalPeriodFilter = extractStatisticalPeriodFilter();
      if (nonNull(statisticalPeriodFilter)) {
        String statisticalPeriod = statisticalPeriodFilter.getMember();
        log.info("Building candidate with statistical period: {}", statisticalPeriod);

        candidateBuilder = candidateBuilder.withStatisticalPeriod(statisticalPeriod);
      } else {
        log.info("No statistical period found");
      }

      Sequence<RawAnalysisPoint> candidates = candidateBuilder.finish();
      return candidates.map(c -> (Metadata) c).toList();
    }
  }

  static final List<String> PERIOD_MEMBERS =
      Lists.newArrayList("year", "year_month", "month", "date", "day");

  private BinaryFilterObject extractStatisticalPeriodFilter() {
    if (nonNull(globalFilters) && !globalFilters.isEmpty()) {
      return globalFilters.stream()
          .filter(m -> PERIOD_MEMBERS.contains(m.getMember()))
          .findFirst()
          .orElse(null);
    }
    return null;
  }
}
