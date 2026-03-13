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
package com.alibaba.assistant.agent.autoconfigure.hook;

import com.alibaba.assistant.agent.common.constant.CodeactStateKeys;
import com.alibaba.assistant.agent.common.constant.HookPriorityConstants;
import com.alibaba.assistant.agent.common.tools.CodeactTool;
import com.alibaba.assistant.agent.core.tool.CodeactToolRegistry;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.Prioritized;
import com.alibaba.cloud.ai.graph.agent.hook.AgentHook;
import com.alibaba.cloud.ai.graph.agent.hook.HookPosition;
import com.alibaba.cloud.ai.graph.agent.hook.HookPositions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * CodeactTools 状态初始化 Hook
 *
 * <p>在 BEFORE_AGENT 阶段最先执行，将 {@link CodeactToolRegistry} 中注册的所有
 * {@link CodeactTool} 注入到 {@link OverAllState} 的 {@code codeact_tools} 键中。
 *
 * <p>优先级为 {@value #ORDER}，确保在所有其他 BEFORE_AGENT Hook 之前执行。
 *
 * @author Assistant Agent Team
 * @since 1.0.0
 */
@HookPositions(HookPosition.BEFORE_AGENT)
public class CodeactToolsStateInitHook extends AgentHook implements Prioritized {

    private static final Logger log = LoggerFactory.getLogger(CodeactToolsStateInitHook.class);

    /**
     * 优先级设置为 {@link HookPriorityConstants#CODEACT_TOOLS_STATE_INIT_HOOK}，
     * 确保在所有其他 BEFORE_AGENT Hook 之前执行。
     * <p>
     * 执行顺序：
     * <ol>
     *   <li>CodeactToolsStateInitHook (5) - 注入 codeact_tools 到 state</li>
     *   <li>ReactExperienceHook (20) - React 经验注入</li>
     *   <li>TaskTreeInitHook (30) - 任务树初始化</li>
     *   <li>FastIntentHook (50) - 快速意图判断</li>
     *   <li>EvaluationHook (100) - 评估（读取 codeact_tools）</li>
     *   <li>PromptContributorHook (200) - Prompt 注入</li>
     * </ol>
     */
    private static final int ORDER = HookPriorityConstants.CODEACT_TOOLS_STATE_INIT_HOOK;

    private final CodeactToolRegistry codeactToolRegistry;

    public CodeactToolsStateInitHook(CodeactToolRegistry codeactToolRegistry) {
        this.codeactToolRegistry = codeactToolRegistry;
    }

    @Override
    public String getName() {
        return "CodeactToolsStateInitHook";
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    public CompletableFuture<Map<String, Object>> beforeAgent(OverAllState state, RunnableConfig config) {
        List<CodeactTool> allTools = codeactToolRegistry.getAllTools();

        Map<String, Object> updates = new HashMap<>();
        updates.put(CodeactStateKeys.CODEACT_TOOLS, allTools);

        log.info("CodeactToolsStateInitHook#beforeAgent - reason=注入codeact_tools到state, toolCount={}",
                allTools.size());

        return CompletableFuture.completedFuture(updates);
    }
}

