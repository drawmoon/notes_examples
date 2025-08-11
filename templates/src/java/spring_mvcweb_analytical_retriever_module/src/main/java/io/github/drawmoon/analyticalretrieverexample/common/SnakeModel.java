package io.github.drawmoon.analyticalretrieverexample.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * This class uses SnakeCase naming strategy to ensure compatibility with Python backend services.
 * The <code>@JsonNaming</code> annotation configures Jackson to serialize/deserialize fields using
 * snake_case format, which is the standard naming convention in Python ecosystems.
 *
 * <p>Key purposes:
 *
 * <ul>
 *   <li>Enables proper parsing of Python API responses (e.g., converting 'user_name' to 'userName'
 *       in Java)
 *   <li>Maintains consistent response format when returning data to Python clients
 *   <li>Bridges naming convention differences between Java (camelCase) and Python (snake_case)
 * </ul>
 *
 * This maintains seamless interoperability while preserving each language's idiomatic style.
 */
@JsonIgnoreProperties(value = {"model_extra"})
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public abstract class SnakeModel extends BaseModel {}
