package io.github.drawmoon.analyticalretrieverexample.service;

import io.github.drawmoon.analyticalretrieverexample.proto.*;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "${app.external-service.metadata.provider[0]}")
public interface MetadataClient {

  @GetMapping("${app.external-service.metadata.api.all-metadata}")
  MetdataGeneralResult<List<RawAnalysisPoint>> allMetadata(@RequestParam("domain") String domain);
}
