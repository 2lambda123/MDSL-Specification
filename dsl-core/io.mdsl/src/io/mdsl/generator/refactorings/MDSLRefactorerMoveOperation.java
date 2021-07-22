package io.mdsl.generator.refactorings;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import io.mdsl.MDSLResource;
import io.mdsl.apiDescription.EndpointContract;
import io.mdsl.apiDescription.ServiceSpecification;
import io.mdsl.dsl.ServiceSpecificationAdapter;
import io.mdsl.generator.AbstractMDSLGenerator;

import io.mdsl.transformations.OperationTransformations;

public class MDSLRefactorerMoveOperation extends AbstractMDSLGenerator {

	public MDSLRefactorerMoveOperation(String sourceEndpoint, String sourceOperation, String targetEndpoint) {
		super();
		this.sourceEndpoint = sourceEndpoint;
		this.sourceOperation = sourceOperation;
		this.targetEndpoint = targetEndpoint;
	}

	private String sourceEndpoint = "TestName";
	private String sourceOperation = "testOp";
	private String targetEndpoint = "NewEndpointName"; // could be null, created then
	
	@Override
	protected void generateFromServiceSpecification(ServiceSpecification mdslSpecification, IFileSystemAccess2 fsa, URI inputFileURI) {
		for (EndpointContract endpoint : new ServiceSpecificationAdapter(mdslSpecification).getEndpointContracts()) {
			if(endpoint.getName().equals(sourceEndpoint)) {
				for(io.mdsl.apiDescription.Operation operation : endpoint.getOps()) {
					if(operation.getName().equals(sourceOperation)) {
						
						OperationTransformations moveOp = new OperationTransformations();
						MDSLResource targetResource = moveOp.moveOperation(operation, targetEndpoint);

						CharSequence result = "// Interface refactoring 'Move Operation' done:\n";
						
						// other hint: org.eclipse.xtext.util.EmfFormatter.(targetResource);
						// final String serialized = ((XtextResource)model.eResource()).getSerializer().serialize(model)
						result = result + targetResource.getXtextResource().getSerializer().serialize(mdslSpecification);
							
						// output file name ignored as we generate to main memory (string fsa)?
						fsa.generateFile(inputFileURI.trimFileExtension().lastSegment() + "_refactored.mdsl", result);
						System.out.println("Endpoint " + sourceEndpoint + " and/or operation " + sourceOperation + " refactored.");
						return;
					}
				}
			}
		}
		System.err.println("[W] Endpoint " + sourceEndpoint + " and/or operation " + sourceOperation + " not found in input file.");
	}
}
