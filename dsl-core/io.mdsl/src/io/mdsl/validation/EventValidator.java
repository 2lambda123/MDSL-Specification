/*
// optional within 'delivering' * generated by Xtext 2.20.0
 */
package io.mdsl.validation;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

import io.mdsl.apiDescription.ApiDescriptionPackage;
import io.mdsl.apiDescription.EndpointContract;
import io.mdsl.apiDescription.Event;

/**
 * This class contains custom validation rules. 
 *
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
public class EventValidator extends AbstractMDSLValidator {
	
	public final static String RECEIVED_EVENT_FOUND = "RECEIVED_EVENT_FOUND";

	@Override
	public void register(EValidatorRegistrar registrar) {
		// not needed for classes used as ComposedCheck
	}
	
	// @Check(CheckType.FAST)
	// @Check(CheckType.EXPENSIVE)
	@Check
	public void proposeEventAPI(EndpointContract apiSpec) {
		EList<Event> events = apiSpec.getEvents();
		for(Event event : events) {
			String ename = event.getType().getName();
			info("Event " + ename, event, ApiDescriptionPackage.eINSTANCE.getEvent_Type(), RECEIVED_EVENT_FOUND);
		}
	}
}