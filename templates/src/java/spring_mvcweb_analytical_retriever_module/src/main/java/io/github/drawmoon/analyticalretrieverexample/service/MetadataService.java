package io.github.drawmoon.analyticalretrieverexample.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drawmoon.analyticalretrieverexample.proto.*;
import java.io.IOException;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MetadataService {

  @Value("${app.external-service.metadata.provider[1]}")
  private String metadataProvider;

  @Value("${app.external-service.metadata.api.all-metadata}")
  private String allMetadataApiPath;

  @Autowired private MetadataClient namedClient;

  @Autowired private OkHttpClient okHttpClient;

  @Autowired private ObjectMapper objectMapper;

  @Cacheable(cacheNames = "DEFAULT", key = "'ALL_METADATA_META_' + #domain")
  public List<RawAnalysisPoint> allMetadata(String domain) {
    log.info("Fetching metadata for domain {}", domain);
    MetdataGeneralResult<List<RawAnalysisPoint>> result = null;

    try {
      result = namedClient.allMetadata(domain);
    } catch (Exception e) {
      log.warn("Request metadata error by FeignClient", e);

      String apiUrl = metadataProvider + allMetadataApiPath;
      HttpUrl httpUrl =
          HttpUrl.get(apiUrl)
              .newBuilder()
              .addQueryParameter("domain", Optional.ofNullable(domain).orElse("default"))
              .build();

      Request request = new Request.Builder().url(httpUrl).get().build();

      try (Response response = okHttpClient.newCall(request).execute()) {
        ResponseBody body = response.body();
        if (body != null) {
          result =
              objectMapper.readValue(
                  body.byteStream(),
                  new TypeReference<MetdataGeneralResult<List<RawAnalysisPoint>>>() {});
        }
      } catch (IOException e0) {
        log.error("Request metadata error: {}", e0.getMessage());
      }
    }

    if (result != null) {
      List<RawAnalysisPoint> analysisPoints = result.ok();

      return analysisPoints;
    }
    return Collections.emptyList();
  }
}
