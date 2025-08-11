package io.github.drawmoon.analyticalretrieverexample.controller;

import io.github.drawmoon.analyticalretrieverexample.controller.input.*;
import io.github.drawmoon.analyticalretrieverexample.proto.*;
import io.github.drawmoon.analyticalretrieverexample.service.*;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Analysis")
@Slf4j
@RestController
@RequestMapping("api/v1/analysis")
public class AnalysisController {

  @Autowired private AnalysisService analysisService;

  @PostMapping()
  public GenerateResult fetch(@RequestBody AnalysisInput input) {
    return analysisService.analysis(
        input.getQuery(),
        input.getDomain(),
        input.getV(),
        input.getMinK(),
        input.getMaxK(),
        input.getNoCache());
  }
}
