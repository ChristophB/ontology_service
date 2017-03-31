<#-- @ftlvariable name="" type="de.onto_med.webprotege_rest_api.views.EntityResultsetView" -->
<#assign title = "Resultset">
<#assign current = "">
<#setting url_escaping_charset="UTF-8">

<html>
	<#include "Head.ftl">
	
	<body>
		<#include "Navbar.ftl">
		
		<div class="jumbotron text-center" style="padding: 10 0 10">
			<h2>Resultset</h2>
			<p>Set of OWL entities.</p>
		</div>
		
		<div class="container">
			<div class="row">
				<#list resultset as result>
					<fieldset class="well" style="padding-bottom: 0px">
						<#if result.iri??>
							<h4><b>${result.iri?html}<b></h4>
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
											<a href="/webprotege-rest-api/entity?iri=${superclass?url}&match=exact&ontologies=${result.projectId}">
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
											<a href="/webprotege-rest-api/entity?iri=${subclass?url}&match=exact&ontologies=${result.projectId}">
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
											<a href="/webprotege-rest-api/entity?iri=${disjointClasses?url}&match=exact&ontologies=${result.projectId}">
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
											<a href="/webprotege-rest-api/entity?iri=${equivalentClasses?url}&match=exact&ontologies=${result.projectId}">
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
											<a href="/webprotege-rest-api/entity?iri=${type?url}&match=exact&ontologies=${result.projectId}">
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
											<a href="/webprotege-rest-api/entity?iri=${sameIndividual?url}&match=exact&ontologies=${result.projectId}">
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
											<a href="/webprotege-rest-api/entity?iri=${key?url}&match=exact&ontologies=${result.projectId}">
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
											<a href="/webprotege-rest-api/entity?iri=${key?url}&match=exact&ontologies=${result.projectId}">
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
											<a href="/webprotege-rest-api/entity?iri=${key?url}&match=exact&ontologies=${result.projectId}">
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
											<a href="/webprotege-rest-api/entity?iri=${individual?url}&match=exact&ontologies=${result.projectId}">
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
		
		<#include "Footer.ftl">
	</body>
</html>