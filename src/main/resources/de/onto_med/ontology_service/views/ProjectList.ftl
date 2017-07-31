<#-- @ftlvariable name="" type="de.onto_med.webprotege_rest_api.views.ProjectListView" -->
<#assign title = "Project List">
<#assign current = "Projects">
<#assign heading = "WebProt&#233;g&#233; Project List">
<#assign subHeading ="The following table contains all public readable projects/ontologies of the locally running WebProt&#233;g&#233; instance.">

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#include "partials/Navbar.ftl">
		<#include "partials/Heading.ftl">
		<#include "partials/ProjectLinks.ftl">
		
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
							<tr class="clickable-row" data-href="${rootPath}/project/${project.projectId}/overview" style="cursor:pointer">
								<td>${project.projectId}</td>
								<td>${project.name}</td>
								<td>${project.description}</td>
							</tr>
						</#list>
					</tbody>
				</table>
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