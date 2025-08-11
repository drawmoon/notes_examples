package io.github.drawmoon.analyticalretrieverexample.retriever;

import static java.util.Objects.*;

import com.fasterxml.jackson.databind.*;
import io.github.drawmoon.analyticalretrieverexample.common.*;
import io.github.drawmoon.analyticalretrieverexample.proto.*;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnalysisGraphCompressor implements MetadataCompressorLike {

  private final AnalysisGraph analysisGraph;
  private final ObjectMapper objectMapper;

  public AnalysisGraphCompressor(AnalysisGraph analysisGraph, ObjectMapper objectMapper) {
    this.analysisGraph = analysisGraph;
    this.objectMapper = objectMapper;
  }

  @Override
  public List<Metadata> compress() {
    List<Metadata> metadataList = new ArrayList<>();

    Iterator<AnalysisGraphNode> nodeIter = analysisGraph.nodeIterator();
    while (nodeIter.hasNext()) {
      AnalysisGraphNode node = nodeIter.next();
      if ("AnalysisPoint".equals(node.getType())) {
        Map<String, Object> properties = node.getProperties();
        if (nonNull(properties)) {
          RawAnalysisPoint analysisPoint = null;

          // try cast to RawAnalysisPoint
          try {
            analysisPoint = objectMapper.convertValue(properties, RawAnalysisPoint.class);
          } catch (Exception e) {
            log.warn(
                "The graph AnalysisPoint type node properties attribute is not a valid JSON object, excpetion: {}",
                e.getMessage());
            return Collections.emptyList();
          }

          if (nonNull(analysisPoint.getDimensions())) {
            log.info(
                "Dimensions: {}",
                analysisPoint.getDimensions().stream()
                    .map(RawDimension::getName)
                    .collect(Collectors.toList()));
            for (RawDimension dimension : analysisPoint.getDimensions()) {
              metadataList.add(dimension);
            }
          }
          if (nonNull(analysisPoint.getMetrics())) {
            log.info(
                "Metrics: {}",
                analysisPoint.getMetrics().stream()
                    .map(RawMeasure::getName)
                    .collect(Collectors.toList()));
            for (RawMeasure measure : analysisPoint.getMetrics()) {
              metadataList.add(measure);

              List<Map<String, Object>> metricFilters = measure.getFilters();
              if (nonNull(metricFilters)) {
                List<BinaryFilterObject> filterObjects = tryCastFilters(metricFilters);

                log.info(
                    "{} Filters: {}",
                    measure.getName(),
                    filterObjects.stream()
                        .map(BinaryFilterObject::getMember)
                        .collect(Collectors.toList()));
                for (BinaryFilterObject filter : filterObjects) {
                  metadataList.add(filter);
                }
              }
            }
          }
          if (nonNull(analysisPoint.getFilters())) {
            List<BinaryFilterObject> filterObjects = tryCastFilters(analysisPoint.getFilters());

            log.info(
                "Filters: {}",
                filterObjects.stream()
                    .map(BinaryFilterObject::getMember)
                    .collect(Collectors.toList()));
            for (BinaryFilterObject filter : filterObjects) {
              metadataList.add(filter);
            }
          }
        } else {
          log.warn("The graph AnalysisPoint type node properties attribute is empty");
        }
      }
    }

    return metadataList;
  }

  // TODO used in many places, refactor
  private List<BinaryFilterObject> tryCastFilters(List<Map<String, Object>> filters) {
    List<BinaryFilterObject> newFilters = new ArrayList<>();
    if (isNull(filters)) {
      return newFilters;
    }

    for (Map<String, Object> filter : filters) {
      try {
        BinaryFilterObject filterObject =
            objectMapper.convertValue(filter, BinaryFilterObject.class);
        newFilters.add(filterObject);
      } catch (Exception e) {
        log.warn("The filter is not a valid JSON object, excpetion: {}", e.getMessage());
      }
    }

    return newFilters;
  }
}
