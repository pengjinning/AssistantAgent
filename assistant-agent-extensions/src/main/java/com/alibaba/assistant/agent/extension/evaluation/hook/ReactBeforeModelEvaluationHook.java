/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.assistant.agent.extension.evaluation.hook;

import com.alibaba.assistant.agent.extension.evaluation.config.CodeactEvaluationContextFactory;
import com.alibaba.assistant.agent.evaluation.EvaluationService;
import com.alibaba.assistant.agent.evaluation.model.EvaluationContext;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;

/**
 * React 阶段的 BEFORE_MODEL 评估 Hook
 * 
 * <p>在 React Agent（主 Agent）的模型调用前进行评估，
 * 关注用户输入和对话历史的增强与评估。
 * 
 * <p>4.1 重构后：取消了 Codeact 阶段的 LLM 调用，所有 Hooks 统一应用于 React 阶段，
 * 不再需要 @HookPhases 注解区分阶段。
 *
 * @author Assistant Agent Team
 * @since 1.0.0
 * @see BeforeModelEvaluationHook
 */
public class ReactBeforeModelEvaluationHook extends BeforeModelEvaluationHook {

    public ReactBeforeModelEvaluationHook(
            EvaluationService evaluationService,
            CodeactEvaluationContextFactory contextFactory,
            String suiteId) {
        super(evaluationService, contextFactory, suiteId);
    }

    public ReactBeforeModelEvaluationHook(
            EvaluationService evaluationService,
            CodeactEvaluationContextFactory contextFactory,
            String suiteId,
            int order) {
        super(evaluationService, contextFactory, suiteId, order);
    }

    @Override
    protected EvaluationContext createEvaluationContext(OverAllState state, RunnableConfig config) {
        // React 阶段关注用户输入和对话历史
        return getContextFactory().createInputRoutingContext(state, config);
    }
}
