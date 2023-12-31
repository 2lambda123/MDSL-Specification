/*
 * Copyright 2020 The Context Mapper Project Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mdsl.generator.bpmn;

import java.util.Map;

import io.mdsl.apiDescription.Orchestration;
import io.mdsl.generator.SketchMinerGenerator;
import io.mdsl.generator.model.composition.converter.Flow2SketchMinerConverter;

public class SketchMinerModelCreator extends AbstractFreemarkerTextCreator<Orchestration> {

	public SketchMinerModelCreator() {
		super(SketchMinerGenerator.class, TEMPLATE_NAME);
	}

	private static final String TEMPLATE_NAME = "sketchminer.ftl";

	@Override
	protected void registerModelObjects(Map<String, Object> root, Orchestration flow) {
		Flow2SketchMinerConverter converter = new Flow2SketchMinerConverter(flow);
		root.put("model", converter.convert());
		root.put("flowName", flow.getName());
	}

	@Override
	protected String getTemplateName() {
		return TEMPLATE_NAME;
	}

	@Override
	protected Class<?> getTemplateClass() {
		return SketchMinerModelCreator.class;
	}
}
