/*
 * Copyright 2020 The MDSL Project Team
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
package io.mdsl.generator.model;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Represents an MDSL endpoint contract.
 * 
 */
public class EndpointContract {

	private String name;
	private List<Operation> operations;
	private ProtocolBinding protocolBinding; // TODO change to array list
	
	// could group these two into one class:
	private List<String> states;	
	private List<StateTransition> transitions;

	/**
	 * Creates a new endpoint contract.
	 * 
	 * @param name the name of the endpoint
	 */
	public EndpointContract(String name) {
		this.name = name;
		this.operations = Lists.newLinkedList();
		this.protocolBinding = new UndefinedProtocol(); 
		this.states = Lists.newLinkedList();
		this.transitions = Lists.newLinkedList();
	}

	/**
	 * Returns the name of the endpoint.
	 * 
	 * @return the name of the endpoint
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the list of operations in the given endpoint.
	 * 
	 * @return a list of operations in the given endpoint
	 */
	public List<Operation> getOperations() {
		return Lists.newLinkedList(operations);
	}

	/**
	 * Adds an operation to the represented endpoint.
	 * 
	 * @param operation the operation that shall be added to the endpoint
	 */
	public void addOperation(Operation operation) {
		this.operations.add(operation);
	}

	/**
	 * Sets the protocol the represented endpoint is bound to.
	 * 
	 * @param protocolBinding the protocol binding for the represented endpoint
	 */
	public void setProtocolBinding(ProtocolBinding protocolBinding) {
		if (protocolBinding != null)
			this.protocolBinding = protocolBinding;
	}

	/**
	 * Returns the protocol the represented endpoint is bound to, in case such a
	 * binding exits. Null otherwise.
	 * 
	 * @return the protocol binding, if existing, null otherwise
	 */
	public ProtocolBinding getProtocolBinding() {
		return protocolBinding;
	}
	
	/**
	 * Gets the list of states in the given endpoint.
	 * 
	 * @return a list of states in the given endpoint
	 */
	public List<String> getStates() {
		return Lists.newLinkedList(states);
	}

	/**
	 * Adds a state to the represented endpoint.
	 * 
	 * @param state the state that shall be added to the endpoint
	 */
	public void addState(String state) {
		this.states.add(state);
	}

	/**
	 * Gets the list of transitions in the given endpoint.
	 * 
	 * @return a list of transitions in the given endpoint
	 */
	public List<StateTransition> getTransitions() {
		return Lists.newLinkedList(transitions);
	}

	/**
	 * Adds a transition to the represented endpoint.
	 * 
	 * @param transition the operation that shall be added to the endpoint
	 */
	public void addStateTransition(StateTransition transition) {
		this.transitions.add(transition);
	}

}
