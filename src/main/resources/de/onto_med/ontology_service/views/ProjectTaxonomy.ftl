<#-- @ftlvariable name="" type="de.onto_med.webprotege_rest_api.views.ProjectTaxonomyView" -->
<#assign title = project.name?html>
<#assign current = "Projects">
<#assign current_submenu = "Taxonomy">
<#setting url_escaping_charset="UTF-8">
<#assign heading = "${project.name?html}">
<#assign subHeading ="${project.description}">

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#include "partials/Navbar.ftl">
		<#include "partials/Heading.ftl">
		<#include "partials/project/Links.ftl">
		<#include "partials/Messages.ftl">
		
		<div class="container">
			<div class="row">
				<div id="taxonomy-tree" class="well col-md-5"></div>
				
				<pre id="description" class="col-md-7"></pre>
			</div>
		</div>
	
		<#include "partials/Footer.ftl">

		<script type="text/javascript">
			$(document).ready(function() {
				$('[data-toggle="tooltip"]').tooltip();
			});

			$('#taxonomy-tree').jstree({
				core: {
					multiple: false,
					data: { url: '${rootPath}/project/${project.projectId}/taxonomy' }
				}
			}).bind('select_node.jstree', function(e, selected) {
				var data = {
					ontologies: '${project.projectId}',
					iri:        selected.node.a_attr.iri,
					match:      'exact'
				};

				$.getJSON('${rootPath}/entity', data, function(json) {
					$('#description').html(JSON.stringify(json, null, 2));
				}, 'application/json');
			});
		</script>
	</body>
</html>