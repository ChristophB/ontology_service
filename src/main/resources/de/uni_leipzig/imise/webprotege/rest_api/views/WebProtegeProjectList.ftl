<#-- @ftlvariable name="" type="de.uni_leipzig.imise.webprotege.rest_api.views.WebProtegeProjectListView" -->
<html>
	<head>
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
	</head>
	
	<body>
		<div class="jumbotron text-center">
			<h2>WebProt&#233;g&#233; Project List</h2>
			<p>The following table contains all public readable projects/ontologies of the locally running WebProt&#233;g&#233; instance.</p>
		</div>
		
		<div class="container">
			<div class="row">
				<table class="table">
					<thead>
						<tr>
							<th>ID</th>
							<th>Name</th>
							<th>Description</th>
						</tr>
					</thead>
					
					<tbody>
						<#list projects as project>
							<tr>
								<td><a href="/project/${project.projectId}">${project.projectId}</a></td>
								<td>${project.name}</td>
								<td>${project.description}</td>
							</tr>
						</#list>
					</tbody>
				</tabe>
			</div>
		</div>
	</body>
</html>