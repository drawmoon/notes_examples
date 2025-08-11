package io.github.drawmoon.analyticalretrieverexample.service;

import static java.util.Objects.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drawmoon.analyticalretrieverexample.proto.*;
import io.github.drawmoon.analyticalretrieverexample.retriever.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AnalysisService {

  @Autowired private LlmService llmService;

  @Autowired private MetadataService analysisPointService;

  @Autowired private ObjectMapper objectMapper;

  public GenerateResult analysis(
      String query, String domain, String v, Integer minK, Integer maxK, Boolean noCache) {
    GenerateResult llmGenerateResult =
        llmService.callLlmGenerate(query, domain, v, minK, maxK, noCache);
    if (isNull(llmGenerateResult)) {
      return null;
    }

    GenerateStage staging = llmGenerateResult.getStaging();
    if (nonNull(staging)) {
      AnalysisGraph calculationGraph = staging.getGraph();

      if (nonNull(calculationGraph)) {
        AnalysisGraphCompressor compressor =
            new AnalysisGraphCompressor(calculationGraph, objectMapper);
        SingleAnalysisRetriever retriever =
            new SingleAnalysisRetriever(analysisPointService, domain, staging.getFilters());

        CompressorRetrieverChain compressorRetrieverChain =
            new CompressorRetrieverChain(staging, compressor, retriever);
        GenerateStage newStaging = compressorRetrieverChain.invoke();

        llmGenerateResult.setStaging(newStaging);
      } else {
        log.info("No analysis graph found {} {}", domain, query);
        // ! here is supposed to be subject analysis
      }
    } else {
      log.info("No staging found {} {}", domain, query);
    }

    return llmGenerateResult;
  }
}
