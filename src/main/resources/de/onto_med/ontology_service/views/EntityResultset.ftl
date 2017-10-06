<#-- @ftlvariable name="" type="de.onto_med.webprotege_rest_api.views.EntityResultsetView" -->
<#assign title = "Resultset">
<#assign current = "">
<#setting url_escaping_charset="UTF-8">
<#assign heading = "Resultset">
<#assign subHeading ="Set of OWL entities.">

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#include "partials/Navbar.ftl">
		<#include "partials/Heading.ftl">
		<#include "partials/Messages.ftl">

		<div class="container">
			<div class="row">
				<#list resultset as result>
					<fieldset class="entity-result well">
						<#if result.iri??>
							<h4><b>${result.iri?html}<b><#if result.projectId??> - ${result.projectId}</#if></h4>
						</#if>
						
						<table class="table">
							<#if result.javaClass??>
								<tr>
									<td>Javaclass:</td>
									<td>${result.javaClass?html}</td>
								</tr>
							</#if>
							
							<#if result.superclasses??>
								<tr>
									<td>Superclasses:</td>
									<td>
										<#list result.superclasses as superclass>
											<a href="${rootPath}/entity?iri=${superclass?url}&match=exact&ontologies=${result.projectId}">
												${superclass?html}
											</a><br>
										</#list>
									</td>
								</tr>
							</#if>
							
							<#if result.subclasses??>
								<tr>
									<td>Subclasses:</td>
									<td>
										<#list result.subclasses as subclass>
											<a href="${rootPath}/entity?iri=${subclass?url}&match=exact&ontologies=${result.projectId}">
												${subclass?html}
											</a><br>
										</#list>
									</td>
								</tr>
							</#if>
							
							<#if result.disjointClasses??>
								<tr>
									<td>Disjoint Classes:</td>
									<td>
										<#list result.disjointClasses as disjointClasses>
											<a href="${rootPath}/entity?iri=${disjointClasses?url}&match=exact&ontologies=${result.projectId}">
												${disjointClasses?html}
											</a><br>
										</#list>
									</td>
								</tr>
							</#if>
							
							<#if result.equivalentClasses??>
								<tr>
									<td>Equivalent Classes:</td>
									<td>
										<#list result.equivalentClasses as equivalentClasses>
											<a href="${rootPath}/entity?iri=${equivalentClasses?url}&match=exact&ontologies=${result.projectId}">
												${equivalentClasses?html}
											</a><br>
										</#list>
									</td>
								</tr>
							</#if>
							
							<#if result.types??>
								<tr>
									<td>Types:</td>
									<td>
										<#list result.types as type>
											<a href="${rootPath}/entity?iri=${type?url}&match=exact&ontologies=${result.projectId}">
												${type?html}
											</a><br>
										</#list>
									</td>
								</tr>
							</#if>
							
							<#if result.sameIndividuals??>
								<tr>
									<td>Same Individuals:</td>
									<td>
										<#list result.sameIndividuals as sameIndividual>
											<a href="${rootPath}/entity?iri=${sameIndividual?url}&match=exact&ontologies=${result.projectId}">
												${sameIndividual?html}
											</a><br>
										</#list>
									</td>
								</tr>
							</#if>
							
							<#if result.annotationProperties??>
								<tr>
									<td>AnnotationProperties:</td>
									<td>
										<#list result.annotationProperties?keys as key>
											<a href="${rootPath}/entity?iri=${key?url}&match=exact&ontologies=${result.projectId}">
												${key?html}
											</a>
											<ul>
												<#list result.annotationProperties[key] as value>
													<li>${value?html}</li>
												</#list>
											</ul>
										</#list>
									</td>
								</tr>
							</#if>
							
							<#if result.dataTypeProperties??>
								<tr>
									<td>DataTypeProperties:</td>
									<td>
										<#list result.dataTypeProperties?keys as key>
											<a href="${rootPath}/entity?iri=${key?url}&match=exact&ontologies=${result.projectId}">
												${key?html}
											</a>
											<ul>
												<#list result.dataTypeProperties[key] as value>
													<li>${value?html}</li>
												</#list>
											</ul>	
										</#list>
									</td>
								</tr>
							</#if>
							
							<#if result.objectProperties??>
								<tr>
									<td>ObjectProperties:</td>
									<td>
										<#list result.objectProperties?keys as key>
											<a href="${rootPath}/entity?iri=${key?url}&match=exact&ontologies=${result.projectId}">
												${key?html}
											</a>
											<ul>
												<#list result.objectProperties[key] as value>
													<li>${value?html}</li>
												</#list>
											</ul>
										</#list>
									</td>
								</tr>
							</#if>
							
							<#if result.individuals??>
								<tr>
									<td>Individuals:</td>
									<td>
										<#list result.individuals as individual>
											<a href="${rootPath}/entity?iri=${individual?url}&match=exact&ontologies=${result.projectId}">
												${individual?html}
											</a><br>
										</#list>
									</td>
								</tr>
							</#if>
						
						</table>
					</fieldset>
				</#list>
			</div>
		</div>
		
		<#include "partials/Footer.ftl">
	</body>
</html>