/*
 *  Copyright (C) 2020 Temporal Technologies, Inc. All Rights Reserved.
 *
 *  Copyright 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *  Modifications copyright (C) 2017 Uber Technologies, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"). You may not
 *  use this file except in compliance with the License. A copy of the License is
 *  located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 *  or in the "license" file accompanying this file. This file is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 */

package io.temporal.opentracing;

import com.google.common.base.MoreObjects;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public class OpenTracingOptions {
  private static final OpenTracingOptions DEFAULT_INSTANCE =
      OpenTracingOptions.newBuilder().build();

  private final Tracer tracer;
  private final Map<SpanOperationType, String> customSpanOperationNamePrefixes;

  public static OpenTracingOptions getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private OpenTracingOptions(
      Tracer tracer, Map<SpanOperationType, String> customSpanOperationNamePrefixes) {
    if (tracer == null) throw new IllegalArgumentException("tracer shouldn't be null");
    this.tracer = tracer;
    this.customSpanOperationNamePrefixes = customSpanOperationNamePrefixes;
  }

  @Nonnull
  public String getSpanOperationNamePrefix(SpanOperationType spanOperationType) {
    return customSpanOperationNamePrefixes.getOrDefault(
        spanOperationType, spanOperationType.getDefaultPrefix());
  }

  @Nonnull
  public Tracer getTracer() {
    return tracer;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private Tracer tracer;
    private final Map<SpanOperationType, String> customSpanOperationNamePrefixes = new HashMap<>();

    private Builder() {}

    public void setTracer(Tracer tracer) {
      this.tracer = tracer;
    }

    public Builder setSpanOperationNamePrefix(
        SpanOperationType spanOperationType, String customPrefix) {
      this.customSpanOperationNamePrefixes.put(spanOperationType, customPrefix);
      return this;
    }

    public OpenTracingOptions build() {
      return new OpenTracingOptions(
          MoreObjects.firstNonNull(tracer, GlobalTracer.get()), customSpanOperationNamePrefixes);
    }
  }
}
