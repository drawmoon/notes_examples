package io.github.drawmoon.analyticalretrieverexample.retriever;

import static java.util.Objects.*;

import com.google.common.collect.Lists;
import io.github.drawmoon.analyticalretrieverexample.common.*;
import io.github.drawmoon.analyticalretrieverexample.proto.*;
import java.util.*;
import java.util.stream.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompressorRetrieverChain {

  private final GenerateStage originalStaging;
  private final MetadataCompressorLike baseCompressor;
  private final RetrieverLike baseRetriever;

  public CompressorRetrieverChain(
      GenerateStage originalStaging,
      MetadataCompressorLike baseCompressor,
      RetrieverLike baseRetriever) {
    this.originalStaging = originalStaging;
    this.baseCompressor = baseCompressor;
    this.baseRetriever = baseRetriever;
  }

  public GenerateStage invoke() {
    // ! just a shallow copy, don't use it to modify the original object
    GenerateStage newStaging = originalStaging.toBuilder().build();

    List<Metadata> metadataElements = baseCompressor.compress();
    List<Metadata> relevantMetadata = baseRetriever.retrieveRelevantMetadata(metadataElements);

    if (relevantMetadata.isEmpty()
        || relevantMetadata.stream().anyMatch(m -> !(m instanceof RawAnalysisPoint))) {
      log.warn(
          "Relevant metadata contains non-RawAnalysisPoint elements. Skipping analysis tree creation.");
      return newStaging;
    }

    log.info("Retrieve relevant analysis-point count: {}", relevantMetadata.size());

    List<RawAnalysisPoint> analysisPoints =
        relevantMetadata.stream().map(m -> (RawAnalysisPoint) m).collect(Collectors.toList());
    AnalysisTree analysisTree = makeAnalysisTree(analysisPoints, metadataElements);
    newStaging.setAnalysisTree(analysisTree);

    return newStaging;
  }

  private AnalysisTree makeAnalysisTree(
      List<RawAnalysisPoint> analysisPoints, List<Metadata> metadataElements) {
    List<Map<String, Object>> attributes = originalStaging.getAttributes();

    if (analysisPoints.size() > 1) {
      log.warn("RawAnalysisPoint elements contains more than one element.");
      // ! here is supposed to be subject analysis
      // TODO not implemented
    }

    // ! Single analysis analysis points must be A
    analysisPoints = analysisPoints.stream().limit(1).collect(Collectors.toList());

    List<RawMeasure> rawMetrics =
        metadataElements.stream()
            .filter(m -> m instanceof RawMeasure)
            .map(m -> (RawMeasure) m)
            .collect(Collectors.toList());
    List<RawDimension> rawDimensions =
        metadataElements.stream()
            .filter(m -> m instanceof RawDimension)
            .map(m -> (RawDimension) m)
            .collect(Collectors.toList());

    List<String> metricNames =
        Optional.ofNullable(rawMetrics).orElse(new ArrayList<>()).stream()
            .map(RawMeasure::getName)
            .collect(Collectors.toList());
    List<String> dimensionNames =
        Optional.ofNullable(rawDimensions).orElse(new ArrayList<>()).stream()
            .map(RawDimension::getName)
            .collect(Collectors.toList());

    String topicName = null;
    String topicValue = null;

    Optional<Map<String, Object>> topicAttrOp =
        attributes.stream().filter(a -> "Topic".equals(a.get("type"))).findFirst();
    if (topicAttrOp.isPresent()) {
      Map<String, Object> topicAttr = topicAttrOp.get();

      topicName = (String) topicAttr.get("name");
      topicValue = (String) ((List<?>) topicAttr.get("values")).get(0);
    } else {
      topicName = analysisPoints.get(0).getName();
      topicValue = analysisPoints.get(0).getName();
    }

    AnalysisTreeNode node =
        AnalysisTreeNode.builder()
            .name(topicName)
            .value(topicValue)
            .type("Topic")
            .measures(metricNames)
            .dimensions(dimensionNames)
            .analysisPoints(
                analysisPoints.stream()
                    .map(
                        a -> {
                          LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                          map.put("id", a.getId());
                          map.put("title", a.getTitle());
                          return map;
                        })
                    .collect(Collectors.toList()))
            .status("active")
            .build();

    // with subject tree node
    String subject = null;
    String subjectValue = null;

    if (nonNull(attributes)) {
      Optional<Map<String, Object>> subjectAttrOp =
          attributes.stream().filter(a -> "Subject".equals(a.get("type"))).findFirst();
      if (subjectAttrOp.isPresent()) {
        Map<String, Object> subjectAttr = subjectAttrOp.get();

        subject = (String) subjectAttr.get("name");
        subjectValue = (String) ((List<?>) subjectAttr.get("values")).get(0);
      }
    }

    // Try to compare the global filter condition with the metric condition to extract the subject
    if (isNull(subject) || isNull(subjectValue)) {
      // ! Multiple measures, as well as composite measures, may not meet expectations
      List<Map<String, Object>> metricFilters =
          rawMetrics.stream().flatMap(m -> m.getFilters().stream()).collect(Collectors.toList());

      List<BinaryFilterObject> globalFilters = originalStaging.getFilters();
      if (nonNull(globalFilters)) {
        Set<String> globalMembers =
            globalFilters.stream().map(BinaryFilterObject::getMember).collect(Collectors.toSet());
        for (Map<String, Object> metricFilter : metricFilters) {
          String member = (String) metricFilter.get("member");
          String operator = (String) metricFilter.get("operator");
          @SuppressWarnings("unchecked")
          List<String> values = (List<String>) metricFilter.getOrDefault("values", null);
          if (globalMembers.contains(member) || !"equals".equals(operator) || isNull(values)) {
            continue;
          }

          subject = member;
          subjectValue = values.get(0);
          break;
        }
      }
    }

    if (nonNull(subject) && nonNull(subjectValue)) {
      node =
          AnalysisTreeNode.builder()
              .name(subject)
              .value(subjectValue)
              .type("Subject")
              .children(Lists.newArrayList(node))
              .childType("Topic")
              .build();
    }

    return AnalysisTree.builder().children(Lists.newArrayList(node)).build();
  }
}
