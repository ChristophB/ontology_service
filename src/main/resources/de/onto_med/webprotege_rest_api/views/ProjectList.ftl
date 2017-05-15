<#-- @ftlvariable name="" type="de.onto_med.webprotege_rest_api.views.ProjectListView" -->
<#assign title = "Project List">
<#assign current = "Projects">

<html>
	<#include "Head.ftl">
	
	<body>
		<#include "Navbar.ftl">
		
		<div class="jumbotron text-center" style="padding: 10 0 10">
			<h2>WebProt&#233;g&#233; Project List</h2>
			<p>The following table contains all public readable projects/ontologies of the locally running WebProt&#233;g&#233; instance.</p>
		</div>
		
		<script>
			jQuery(document).ready(function($) {
    			$(".clickable-row").click(function() {
        			window.document.location = $(this).data("href");
    			});
			});
		</script>
		<div class="container">
			<div class="row">
				<table class="table table-hover">
					<thead>
						<tr>
							<th>ID</th>
							<th>Name</th>
							<th>Description</th>
						</tr>
					</thead>
					
					<tbody>
						<#list projects as project>
							<tr class="clickable-row" data-href="${rootPath}project/${project.projectId}/overview" style="cursor:pointer">
								<td>${project.projectId}</td>
								<td>${project.name}</td>
								<td>${project.description}</td>
							</tr>
						</#list>
					</tbody>
				</table>
			</div>
		</div>
		
		<#include "Footer.ftl">
	</body>
</html>