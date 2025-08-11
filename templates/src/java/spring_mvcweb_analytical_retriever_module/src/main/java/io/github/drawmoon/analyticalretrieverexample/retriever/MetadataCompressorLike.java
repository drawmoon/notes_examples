package io.github.drawmoon.analyticalretrieverexample.retriever;

import io.github.drawmoon.analyticalretrieverexample.common.*;
import java.util.*;

/**
 * Interface defining metadata compression capabilities for analytical context extraction.
 *
 * <p>This interface implements the CompressorLike design pattern inspired by Python's langchain
 * retriever architecture. It serves as a specialized component for extracting structured metadata
 * (measures and dimensions) from AnalysisGraph/AnalysisTree. The compressed metadata forms the
 * foundation for analysis point retrieval and contextual understanding in the system.
 *
 * <p>Key architectural relationships:
 *
 * <ul>
 *   <li>Part of the dual-interface pattern with RetrieverLike for separation of concerns
 *   <li>Produces metadata consumed by ContextualCompressionRetriever for combined operations
 *   <li>Defines contract for AnalysisGraphCompressor and similar metadata extractors
 * </ul>
 *
 * <p>Implementations are expected to provide efficient traversal mechanisms for complex analytical
 * structures while maintaining type-specific metadata extraction capabilities for dimensional
 * analysis.
 */
public interface MetadataCompressorLike {

  /**
   * Compresses analytical structure into essential metadata components.
   *
   * <p>Performs structural analysis to extract critical metadata elements required for:
   *
   * <ul>
   *   <li>Measure identification (RawMeasure) for quantitative analysis
   *   <li>Dimension extraction (RawDimension) for categorical context
   *   <li>Schema validation for subsequent retrieval operations
   *   <li>Context preservation for contextual compression workflows
   * </ul>
   *
   * <p>The returned metadata collection serves as input for RawAnalysisPoint construction and
   * provides the semantic foundation for contextual query expansion in retrieval processes.
   *
   * @return List of Metadata objects containing structured analytical metadata
   * @see AnalysisGraphCompressor for primary implementation example
   * @see RawAnalysisPoint for metadata consumption pattern
   */
  List<Metadata> compress();
}
