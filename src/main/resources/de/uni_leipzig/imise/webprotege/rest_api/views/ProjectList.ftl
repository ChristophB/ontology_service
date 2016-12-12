<#-- @ftlvariable name="" type="de.uni_leipzig.imise.webprotege.rest_api.views.ProjectListView" -->
<html>
	<head>
		<title>Projects | WebProt&#233;g&#233; REST-Interface</title>
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
								<td><a href="/project/${project.projectId}/overview">${project.projectId}</a></td>
								<td>${project.name}</td>
								<td>${project.description}</td>
							</tr>
						</#list>
					</tbody>
				</table>
			</div>
		</div>
	</body>
</html>