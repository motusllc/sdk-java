/*
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

package io.temporal.worker;

import com.google.common.base.Preconditions;
import io.temporal.context.ContextPropagator;
import io.temporal.internal.worker.PollerOptions;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class WorkerFactoryOptions {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static Builder newBuilder(WorkerFactoryOptions options) {
    return new Builder(options);
  }

  public static WorkerFactoryOptions getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final WorkerFactoryOptions DEFAULT_INSTANCE;

  static {
    DEFAULT_INSTANCE = WorkerFactoryOptions.newBuilder().build();
  }

  public static class Builder {
    private int stickyDecisionScheduleToStartTimeoutInSeconds = 5;
    private int cacheMaximumSize = 600;
    private int maxWorkflowThreadCount = 600;
    private PollerOptions stickyWorkflowPollerOptions;
    private List<ContextPropagator> contextPropagators;

    private Builder() {}

    private Builder(WorkerFactoryOptions options) {
      this.stickyDecisionScheduleToStartTimeoutInSeconds =
          options.stickyDecisionScheduleToStartTimeoutInSeconds;
      this.cacheMaximumSize = options.cacheMaximumSize;
      this.maxWorkflowThreadCount = options.maxWorkflowThreadCount;
      this.stickyWorkflowPollerOptions = options.stickyWorkflowPollerOptions;
      this.contextPropagators = options.contextPropagators;
    }

    /**
     * When Sticky execution is enabled this will set the maximum allowed number of workflows
     * cached. This cache is shared by all workers created by the Factory. Default value is 600
     */
    public Builder setCacheMaximumSize(int cacheMaximumSize) {
      this.cacheMaximumSize = cacheMaximumSize;
      return this;
    }

    /**
     * Maximum number of threads available for workflow execution across all workers created by the
     * Factory.
     */
    public Builder setMaxWorkflowThreadCount(int maxWorkflowThreadCount) {
      this.maxWorkflowThreadCount = maxWorkflowThreadCount;
      return this;
    }

    /**
     * Timeout for sticky workflow decision to be picked up by the host assigned to it. Once it
     * times out then it can be picked up by any worker. Default value is 5 seconds.
     */
    public Builder setStickyDecisionScheduleToStartTimeoutInSeconds(
        int stickyDecisionScheduleToStartTimeoutInSeconds) {
      this.stickyDecisionScheduleToStartTimeoutInSeconds =
          stickyDecisionScheduleToStartTimeoutInSeconds;
      return this;
    }

    /**
     * PollerOptions for poller responsible for polling for decisions for workflows cached by all
     * workers created by this factory.
     */
    public Builder setStickyWorkflowPollerOptions(PollerOptions stickyWorkflowPollerOptions) {
      this.stickyWorkflowPollerOptions = stickyWorkflowPollerOptions;
      return this;
    }

    public Builder setContextPropagators(List<ContextPropagator> contextPropagators) {
      this.contextPropagators = contextPropagators;
      return this;
    }

    public WorkerFactoryOptions build() {
      return new WorkerFactoryOptions(
          cacheMaximumSize,
          maxWorkflowThreadCount,
          stickyDecisionScheduleToStartTimeoutInSeconds,
          stickyWorkflowPollerOptions,
          contextPropagators);
    }
  }

  private final int cacheMaximumSize;
  private final int maxWorkflowThreadCount;
  private final int stickyDecisionScheduleToStartTimeoutInSeconds;
  private final PollerOptions stickyWorkflowPollerOptions;
  private List<ContextPropagator> contextPropagators;

  private WorkerFactoryOptions(
      int cacheMaximumSize,
      int maxWorkflowThreadCount,
      int stickyDecisionScheduleToStartTimeoutInSeconds,
      PollerOptions stickyWorkflowPollerOptions,
      List<ContextPropagator> contextPropagators) {
    Preconditions.checkArgument(cacheMaximumSize > 0, "cacheMaximumSize should be greater than 0");
    Preconditions.checkArgument(
        maxWorkflowThreadCount > 0, "maxWorkflowThreadCount should be greater than 0");
    Preconditions.checkArgument(
        stickyDecisionScheduleToStartTimeoutInSeconds > 0,
        "stickyDecisionScheduleToStartTimeoutInSeconds should be greater than 0");

    this.cacheMaximumSize = cacheMaximumSize;
    this.maxWorkflowThreadCount = maxWorkflowThreadCount;
    this.stickyDecisionScheduleToStartTimeoutInSeconds =
        stickyDecisionScheduleToStartTimeoutInSeconds;

    if (stickyWorkflowPollerOptions == null) {
      this.stickyWorkflowPollerOptions =
          PollerOptions.newBuilder()
              .setPollBackoffInitialInterval(Duration.ofMillis(200))
              .setPollBackoffMaximumInterval(Duration.ofSeconds(20))
              .setPollThreadCount(1)
              .build();
    } else {
      this.stickyWorkflowPollerOptions = stickyWorkflowPollerOptions;
    }

    if (contextPropagators != null) {
      this.contextPropagators = contextPropagators;
    } else {
      this.contextPropagators = new ArrayList<>();
    }
  }

  public int getCacheMaximumSize() {
    return cacheMaximumSize;
  }

  public int getMaxWorkflowThreadCount() {
    return maxWorkflowThreadCount;
  }

  public int getStickyDecisionScheduleToStartTimeoutInSeconds() {
    return stickyDecisionScheduleToStartTimeoutInSeconds;
  }

  public PollerOptions getStickyWorkflowPollerOptions() {
    return stickyWorkflowPollerOptions;
  }

  public List<ContextPropagator> getContextPropagators() {
    return contextPropagators;
  }
}