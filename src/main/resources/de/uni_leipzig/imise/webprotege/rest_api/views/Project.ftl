<#-- @ftlvariable name="" type="de.uni_leipzig.imise.webprotege.rest_api.views.ProjectView" -->
<html>
	<head>
		<title>Project: ${project.name?html} | WebProt&#233;g&#233; REST-Interface</title>
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
			<h2>${project.name?html}</h2>
			<p>${project.description}</p>
		</div>
		
		<div class="container">
			<div class="row">
				<table class="table">
					<tbody>
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
	</body>
</html>