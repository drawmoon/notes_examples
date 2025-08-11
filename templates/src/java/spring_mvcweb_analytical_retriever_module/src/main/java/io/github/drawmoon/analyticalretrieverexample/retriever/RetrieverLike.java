package io.github.drawmoon.analyticalretrieverexample.retriever;

import io.github.drawmoon.analyticalretrieverexample.common.*;
import io.github.drawmoon.analyticalretrieverexample.proto.RawAnalysisPoint;
import java.util.*;

/**
 * Interface abstracting the retrieval pattern from Python's langchain ecosystem, serving as a
 * foundational component for analytical knowledge retrieval systems.
 *
 * <p>This interface enables:
 *
 * <ul>
 *   <li>Standardized access to metadata sources for analytical point generation
 *   <li>Modular implementation of different retrieval strategies
 *   <li>Composable architecture through combination with compressor components
 *   <li>Consistent interface for both local and distributed retrieval operations
 * </ul>
 *
 * <p>Designed to work with:
 *
 * <ul>
 *   <li>{@link MetadataCompressorLike} for metadata extraction from domain objects
 *   <li>{@link CompressorRetrieverChain} for combined compression-retrieval workflows
 *   <li>{@link RawAnalysisPoint} for final analytical point materialization
 * </ul>
 *
 * <p>Implements strategy pattern for retrieval mechanisms while maintaining separation of concerns
 * with metadata compression responsibilities.
 */
public interface RetrieverLike {

  /**
   * Executes retrieval operation to gather metadata required for analysis point generation.
   *
   * <p>This method serves as the primary interface for:
   *
   * <ul>
   *   <li>Extracting metadata from domain-specific structures
   *   <li>Preparing raw material for subsequent analysis point creation
   *   <li>Providing input to contextual compression pipelines
   *   <li>Enabling traceability through metadata provenance tracking
   * </ul>
   *
   * <p>Metadata returned contains:
   *
   * <ul>
   *   <li>RawMeasure instances representing quantitative metrics
   *   <li>RawDimension instances representing categorical dimensions
   *   <li>Temporal context information for time-series analysis
   *   <li>Source provenance metadata for auditability
   * </ul>
   *
   * @return List of metadata objects containing structured retrieval results
   * @see RawAnalysisPoint for downstream processing of retrieved metadata
   * @see MetadataCompressorLike for metadata extraction mechanisms
   */
  List<Metadata> retrieveRelevantMetadata(List<Metadata> metadataElements);
}
