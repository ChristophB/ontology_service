<#-- @ftlvariable name="" type="de.uni_leipzig.imise.webprotege.rest_api.views.EntityResultsetView" -->
<html>
	<head>
		<title>Resultset | WebProt&#233;g&#233; REST-Interface</title>
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
	</head>
	
	<body>
		<nav class="navbar navbar-default navbar-fixed-top">
			<div class="container">
				<ul class="nav navbar-nav">
					<li><a href="/">Documentation</a></li>
					<li class="active"><a href="/projects">Projects</a></li>
					<li><a href="/entity-form">Query Entities</a></li>
					<li><a href="/reason-form">Reasoning</a></li>
				</ul>
			</div>
		</nav>
		<div class="row" style="height: 50px; width:100%"></div>
		
		<div class="jumbotron text-center">
			<h2>Resultset</h2>
			<p>Set of OWL entities.</p>
		</div>
		
		<div class="container">
			<div class="row">
				<#list resultset as result>
					<table class="table">
						<thead>
							<tr>
								<th>Property</th>
								<th>Values</th>
							</tr>
						</thead>
						
						<tbody>
							<#if result.iri??>
								<tr>
									<td>IRI:</td>
									<td><b>${result.iri?html}</b></td>
								</tr>
							</#if>
							
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
									
										<ul>
											<#list result.superclasses as superclass>
												<li>${superclass?html}</li>
											</#list>
										</ul>
									</td>
								</tr>
							</#if>
							
							<#if result.subclasses??>
								<tr>
									<td>Subclasses:</td>
									<td>
										<ul>
											<#list result.subclasses as subclass>
												<li>${subclass?html}</li>
											</#list>
										</ul>
									</td>
								</tr>
							</#if>
							
							<#if result.types??>
								<tr>
									<td>Types:</td>
									<td>
										<ul>
											<#list result.types as type>
												<li>${type?html}</li>
											</#list>
										</ul>
									</td>
								</tr>
							</#if>
							
							<#if result.annotationProperties??>
								<tr>
									<td>AnnotationProperties:</td>
									<td>
										<ul>
											<#list result.annotationProperties?keys as key>
												<li>
													${key?html}:
													<ul>
														<#list result.annotationProperties[key] as value>
															<li>${value?html}</li>
														</#list>
													</ul>
												</li>
											</#list>
										</ul>
									</td>
								</tr>
							</#if>
							
							<#if result.dataTypeProperties??>
								<tr>
									<td>DataTypeProperties:</td>
									<td>
										<ul>
											<#list result.dataTypeProperties?keys as key>
												<li>
													${key?html}:
													<ul>
														<#list result.dataTypeProperties[key] as value>
															<li>${value?html}</li>
														</#list>
													</ul>	
												</li>
											</#list>
										</ul>
									</td>
								</tr>
							</#if>
							
							<#if result.objectProperties??>
								<tr>
									<td>ObjectProperties:</td>
									<td>
										<ul>
											<#list result.objectProperties?keys as key>
												<li>
													${key?html}:
													<ul>
														<#list result.objectProperties[key] as value>
															<li>${value?html}</li>
														</#list>
													</ul>
												</li>
											</#list>
										</ul>
									</td>
								</tr>
							</#if>
						</tbody>
					</table>
				</#list>
			</div>
		</div>
	</body>
</html>