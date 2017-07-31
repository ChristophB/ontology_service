<#-- @ftlvariable name="" type="de.onto_med.webprotege_rest_api.views.ProjectView" -->
<#assign title = project.name?html>
<#assign current = "Projects">
<#assign current_submenu = "Overview">
<#assign heading = "${project.name?html}">
<#assign subHeading ="${project.description}">

<#macro coloredBoolean boolean><#if boolean><font color="green">true</font><#else><font color="red">false</font></#if></#macro>

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#include "partials/Navbar.ftl">
		<#include "partials/Heading.ftl">
		<#include "partials/ProjectLinks.ftl">
		<#include "partials/Messages.ftl">
		
		<div class="container">
			<div class="row">
				<table class="table">
					<tbody>
						<tr><td><b>IRI:</b></td> <td><b>${project.projectIri}</b></td></tr>
						<tr><td><b>ProjectID:</b></td> <td>${project.projectId}</td></tr>
						<tr><td><b>Is Consistent:</b></td> <td><@coloredBoolean project.isConsistent /></td></tr>
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
			
			<#if project.importedOntologyIds?? && project.importedOntologyIds?size != 0>
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
		
		<#include "partials/Footer.ftl">
	</body>
</html>