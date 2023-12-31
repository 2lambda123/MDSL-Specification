/*
 * This file is licensed to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package ${genModel.apiName};

// generated from orchestration flow part of ${fileName}
// The API description ${genModel.apiName} features ${genModel.orchestrationFlows?size} orchestration flow(s) (a.k.a. API call sequences).

import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.AggregationStrategy;

<#assign firstFlowGenerated=false>
<#list genModel.orchestrationFlows as oflow> 
public class ${oflow.name} extends RouteBuilder {
	<#list oflow.events as ename, event>
	class ${event.name} {} <#-- // TODO add data structure if present in source MDSL -->
    </#list>
    <#list oflow.commands as command>

    class ${command.name}Processor implements Processor {
	    private String name;
	
        ${command.name}Processor(String name) {
    	    this.name=name;
        }

        public void process(Exchange exchange) throws Exception {
    	    String message = exchange.getIn().getBody().toString();
    	    System.out.println("Command processor " + this.getClass().getSimpleName() + " activated, processing message: " + message);
            <#--
            // steer choices via condition headers as this (exchange is for instance used here: process(Exchange exchange)):
            // exchange.getIn().setHeader("ChoiceCondition", "choiceValue");
            -->
            exchange.getIn().setBody(message + ", processed by " + this.getClass().getSimpleName()); 
        }
    }
    </#list>

    <#-- TODO customize based on Aggregators in flow -->
    public class JoinAggregatorStrategy implements AggregationStrategy {
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {    	
            if (oldExchange == null) {
        	    System.out.println("First time in JoinAggregatorStrategy: " + newExchange.getIn().getBody().toString());
        	    // newExchange.getIn().setBody(newExchange.getIn().getBody().toString());
                return newExchange;
            }

            System.out.println("In JoinAggregatorStrategy: (oldin) " + oldExchange.getIn().getBody().toString()); 
            System.out.println("In JoinAggregatorStrategy: (newin) " + newExchange.getIn().getBody().toString());
        		
            newExchange.getIn().setBody(oldExchange.getIn().getBody().toString() + "; " + newExchange.getIn().getBody().toString());
            return newExchange;
        }
    
        public String appendStrings(String message1, String message2) {    	
        	return message1 + message2;
        }
    }
    
    public static void main(String[] args) throws Exception {
        ${oflow.name} builder = new ${oflow.name}();
        builder.run${oflow.name}();
    }
    
    public void run${oflow.name}() throws Exception {
        // create CamelContext
        DefaultCamelContext camelContext = new DefaultCamelContext();

        /* bindBeans(camelContext.getRegistry()); */ 
        camelContext.addRoutes(this);
        camelContext.setTracing(true);
        camelContext.start();

        // create a producer to send all initial events to the process flow
        ProducerTemplate template = camelContext.createProducerTemplate();
        String testMessage = "Test message for flow ${oflow.name} in ${genModel.apiName}";
        <#list oflow.initEvents() as ename, event>
		template.sendBody("direct:${ename}", testMessage);
        // TODO add header to message if flow contains a choice, example (replace "choice-nn" with names from flow):
        // template.sendBodyAndHeader("direct:${ename}", testMessage, "ChoiceCondition", "choiceValue"); 
    	</#list>
        <#list oflow.initCommands() as command>
        template.sendBody("direct:${command.name}", testMessage);
        // TODO add header to testMessage if flow contains a choice, example (replace "choice-nn" with names from flow):
        // template.sendBodyAndHeader("direct:${command.name}", testMessage, "ChoiceCondition", "choiceValue");
        </#list>
        camelContext.stop();
        camelContext.close();
    }

    @Override
    public void configure() {
        // routes for command invocation steps
        <#list oflow.eventsAsSet() as event>
        <#if event.join>
        <#-- case 1: generate Aggregator, find out whether AND or OR or XOR is joined (possible?); use generated custom Aggregator (inner class) to make gen code self contained -->
        <#list event.joinedEvents() as joinedEvent>
        from("direct:${joinedEvent.name}").to("direct:${event.name}:AggregatorIn");
        </#list>
        <#if event.triggersSingleCommand()>
        <#if event.triggersAndCommandComposition()>
        // case 1a: event1 and event 2 trigger command1 and command 2 (TODO known limitation: only first command considered, others ignored; add recipientList):
        <#assign firstAndedCommand=event.triggeredCommands()[0].containedCommands()[0]>
        from("direct:${event.name}:AggregatorIn").aggregate(new JoinAggregatorStrategy()).constant(true).completionSize(${event.conditionCount}).to("direct:${firstAndedCommand.name}");
        <#else>
        from("direct:${event.name}:AggregatorIn").aggregate(new JoinAggregatorStrategy()).constant(true).completionSize(${event.conditionCount}).to("direct:${event.triggeredCommands()[0].name}");
        </#if>
        <#elseif event.triggersOrCommandComposition()>
        // case 1b: event1 and event 2 trigger command1 (x)or command 2 (TODO known limitation: only first command considered, others ignored; add choice):
        from("direct:${event.name}:AggregatorIn").aggregate(new JoinAggregatorStrategy()).constant(true).completionSize(${event.conditionCount}).to("direct:${event.triggeredCommands()[0].name}");
        <#else>
        // TODO define route for trigger event ${event.name} manually <#-- default case, should not get here -->
        </#if>
        <#else><#-- handling join event of different types (of right hand side of flow step) done, handling single left hand event now -->
        <#if event.triggersAndCommandComposition()>
        <#list event.triggeredCommands() as command>
        <#-- // case 2: generate Recipient List to trigger parallel execution of ANDed commands -->
        from("direct:${event.name}").to("direct:${command.name}"); // event to composite command (recipient list)
        from("direct:${command.name}").process(new ${command.name}Processor("${command.name}")).recipientList(constant("${command.containedCommandsAsCommaSeparatedList("direct:")}")).parallelProcessing(); 
        </#list>
        <#elseif event.triggersSingleCommand()>
        <#-- case 3: simple command invocation only requires one route from event to command -->
        <#list event.triggeredCommands() as command>
        from("direct:${event.name}").to("direct:${command.name}"); // event to command
        </#list>
        <#elseif event.triggersOrCommandComposition()>
        <#-- case 4: must be ORed or XORed command invocation (event to commands), requiring introduction of Recipient List -->
        from("direct:${event.name}").choice() // event to multiple commands<#list event.triggeredCommands() as alternativeCommand> 
        .when(simple("${oflow.camelUtils().headerPrefix()}${event.name}CommandInvocationCondition} == '${alternativeCommand.optionValue()}'")).to("direct:${alternativeCommand.name}")</#list>
        .otherwise().to("mock:bye").stop();
        <#else>
        <#if oflow.processView().participatesInAnd(event)>
        <#list oflow.processView().getCompositeEventsWith(event) as compositeEvent>
        <#if compositeEvent.getTriggeredCommands()??&&compositeEvent.getTriggeredCommands()?size gt 0>
        from("direct:${event.name}").to("direct:${compositeEvent.name}"); // edge case: single AND event to join
        <#else>
        // note: ${event.name} is part of ${compositeEvent.name} which does not invoke any commands
        </#if>
        </#list>
        <#else>
        // note: ${event.name} does not invoke any commands <#-- termination events are handled separately (below) -->
        </#if>    
        </#if>
        </#if>
        </#list>

        // routes for domain event production steps
    	<#list oflow.commands as command>
        <#if command.emitsSingleEvent()>
        <#if command.emitsSingleCompositeEvent()&&command.singleCompositeEvent().composedEvents()?size gt 1>
        <#-- case 5: trigger parallelProcessing() via a recipientList endpoint --> 
        from("direct:${command.name}").process(new ${command.name}Processor("${command.name}")).recipientList(constant("${command.multipleConcurrentEventsAsCommaSeparatedList("direct:")}")).parallelProcessing(); 
        <#else>
        <#-- case 6: simple single event emisson, only one route required --> 
        from("direct:${command.name}").process(new ${command.name}Processor("${command.name}")).to("direct:${command.emits()[0].name}"); // command to single event
        </#if>
        <#else>
        <#if command.emitsMultipleAlternativeEvents()>
        <#-- case 7: active choice evaluation via CBR pattern; note: processor not present here (not generating custom one yet) -->
        from("direct:${command.name}").choice() // command to multiple events <#list command.emits() as alternativeEvent>
        .when(simple("${oflow.camelUtils().headerPrefix()}${command.name}EventEmissionCondition} == '${alternativeEvent.optionValue()}'")).to("${alternativeEvent.optionBranch("direct:")}")</#list>
        .otherwise().to("mock:bye").stop();
        <#else>
        <#list command.emits() as event>
        // TODO process ${command.name} (should not get here)
        </#list>
        </#if>
        </#if>
        </#list>
        
        <#assign terminationEvents=oflow.terminationEvents()>
        <#if terminationEvents??&&terminationEvents?size gt 0>
        // routes to terminate flow
        <#list terminationEvents as ename, event>
        from("direct:${event.name}").to("mock:bye").stop();
        </#list>
        </#if>
        <#assign terminationCommands=oflow.processView().getTerminationCommands()>
        <#if terminationCommands??&&terminationCommands?size gt 0>
        // routes to terminate flow via terminating command
        <#list terminationCommands as command>
        from("direct:${command.name}").process(new ${command.name}Processor("${command.name}")).to("mock:bye").stop();
        </#list>
        </#if>
    }
}
<#-- known limitation: public classes for multiple flows go to single file, which must be split manually -->
<#if genModel.orchestrationFlows?size!=1 && !firstFlowGenerated>
<#assign firstFlowGenerated=true>

// TODO split here and move each route builder class to a separate file so that compilation succeeds

</#if>
</#list>