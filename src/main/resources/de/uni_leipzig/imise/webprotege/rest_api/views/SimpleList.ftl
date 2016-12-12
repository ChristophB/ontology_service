<#-- @ftlvariable name="" type="de.uni_leipzig.imise.webprotege.rest_api.views.SimpleListView" -->
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
			<p>Set of ${column?html}.</p>
		</div>
		
		<div class="container">
			<div class="row">
				<table class="table">
					<thead>
						<tr>
							<th>${column?html}</th>
						</tr>
					</thead>
					
					<tbody>
						<#list resultset as result>
							<tr>
								<td>${result?html}</td>
							</tr>
						</#list>
					</tbody>
				</table>
			</div>
		</div>
	</body>
</html>