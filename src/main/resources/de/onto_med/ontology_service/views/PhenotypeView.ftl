<#assign title = "Phenotyping">
<#assign current = "Phenotyping">
<#assign current_submenu = "overview">
<#assign heading = "Phenotyping">
<#assign subHeading = "Manage phenotype ontologies">

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#if navigationVisible><#include "partials/Navbar.ftl"></#if>
    	<#include "partials/Heading.ftl">
		<#include "partials/Messages.ftl">
		
    	<div class="container">

			<table class="table table-hover">
				<thead>
					<tr>
						<th>ID</th>
						<th>Size</th>
						<th>Actions</th>
					</tr>
				</thead>

				<tbody>
					<#list ontologies?keys as id>
						<tr class="clickable-row" data-href="${rootPath}/phenotype/${id}/phenotype-form">
							<td>${id}</td>
							<td>${ontologies[id]} kB</td>
							<td>
								<a href="${rootPath}/phenotype/${id}" class="btn btn-default">Download</a>
								<form class="form-inline" action="${rootPath}/phenotype/${id}/delete" method="post"
									  onsubmit="return confirm('Deletion is irrevocable! Really delete ontology \'${id}\'?')">
									<input type="submit" class="btn btn-danger" value="Delete">
								</form>
							</td>
						</tr>
					</#list>
				</tbody>
			</table>

			<div class="col-sm-6 col-sm-offset-3">
				<form class="form-inline" action="${rootPath}/phenotype/create" method="post">
					<div class="form-group">
						<label for="id">Ontology ID:</label>
						<input type="text" name="id" id="id" class="form-control" placeholder="Some ontology ID">
					</div>
					<input type="submit" class="btn btn-primary" value="Create ontology">
				</form>
			</div>
		</div>
	    
		<#include "partials/Footer.ftl">

		<script>
			jQuery(document).ready(function($) {
				$(".clickable-row").click(function() {
					window.document.location = $(this).data("href");
				});
			});
		</script>
	</body>
</html>
