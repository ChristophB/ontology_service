<#-- @ftlvariable name="" type="de.uni_leipzig.imise.webprotege.rest_api.views.WebProtegeProjectView" -->
<html>
	<head>
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
	</head>
	
	<body>
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
		</div>
	</body>
</html>