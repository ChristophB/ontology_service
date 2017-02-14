<#-- @ftlvariable name="" type="de.onto_med.webprotege_rest_api.views.ProjectView" -->
<#assign title = project.name?html>
<#assign current = "Projects">
<#assign current_submenue = "Overview">

<html>
	<#include "Head.ftl">
	
	<body>
		<#include "Navbar.ftl">
		
		<div class="jumbotron text-center" style="padding: 10 0 10">
			<h2>${project.name?html}</h2>
			<p>${project.description}</p>
		</div>
		
		<div class="container">
			<#include "ProjectLinks.ftl">
			
			<div class="row">
				<table class="table">
					<tbody>
						<tr><td><b>IRI:</b></td> <td><b>${project.projectIri}</b></td></tr>
						<tr><td><b>Axioms:</b></td> <td>${project.countAxioms}</td></tr>
						<tr><td><b>Logical Axioms:</b</td> <td>${project.countLogicalAxioms}</td></tr>
						<tr><td><b>Classes:</b></td> <td>${project.countClasses}</td></tr>
						<tr><td><b>Individuals:</b></td> <td>${project.countIndividuals}</td></tr>
						<tr><td><b>DataProperties:</b></td> <td>${project.countDataProperties}</td></tr>
						<tr><td><b>ObjectProperties:</b></td> <td>${project.countObjectProperties}</td></tr>
						<tr><td><b>AnnotationProperties:</b></td> <td>${project.countAnnotationProperties}</td></tr>
					</tbody>
				</table>
			</div>
			
			<#if project.importedOntologyIds??>
				<div class="row">
					<table class="table">
						<thead><tr><th>Imports</th></tr></thead>
						<tbody>
							<#list project.importedOntologyIds as import>
								<tr>
									<td>${import?html}</td>
								</tr>
							</#list>
						</tbody>
					</table>
				</div>
			</#if>
		</div>
		
		<#include "Footer.ftl">
	</body>
</html>