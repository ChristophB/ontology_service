<#-- @ftlvariable name="" type="de.onto_med.webprotege_rest_api.views.ProjectView" -->
<#assign title = project.name?html>
<#assign current = "Projects">

<html>
	<#include "Head.ftl">
	
	<body>
		<#include "Navbar.ftl">
		
		<div class="jumbotron text-center" style="padding: 10 0 10">
			<h2>${project.name?html}</h2>
			<p>${project.description}</p>
		</div>
		
		<div class="container">
			<div class="row">
				<table class="table">
					<tbody>
						<tr><td><b>IRI:</b></td> <td><b>${project.projectIri}</b></td></tr>
						<tr><td><b>Axioms:</b></td> <td>${project.countAxioms}</td></tr>
						<tr><td><b>Classes:</b></td> <td>${project.countClasses}</td></tr>
						<tr><td><b>Individuals:</b></td> <td>${project.countIndividuals}</td></tr>
						<tr><td><b>DataTypeProperties:</b></td> <td>${project.countDataTypeProperties}</td></tr>
						<tr><td><b>ObjectProperties:</b></td> <td>${project.countObjectProperties}</td></tr>
						<tr><td><b>AnnotationProperties:</b></td> <td>${project.countAnnotationProperties}</td></tr>
					</tbody>
				</table>
			</div>
			
			<div class="row">
				<ul>
					<li><a href="/project/${project.projectId}/entity-form">Query this ontology</a></li>
					<li><a href="/project/${project.projectId}/reason-form">Reason this ontology</a></li>
					<li><a href="/project/${project.projectId}/imports">List Imports</a></li>
					<li><a href="/project/${project.projectId}">OWL File (RDF/XML)</a></li>
				</ul>
			</div>
		</div>
		
		<#include "Footer.ftl">
	</body>
</html>