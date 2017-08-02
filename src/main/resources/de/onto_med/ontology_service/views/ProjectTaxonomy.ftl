<#-- @ftlvariable name="" type="de.onto_med.webprotege_rest_api.views.ProjectTaxonomyView" -->
<#assign title = project.name?html>
<#assign current = "Projects">
<#assign current_submenu = "Taxonomy">
<#setting url_escaping_charset="UTF-8">
<#assign heading = "${project.name?html}">
<#assign subHeading ="${project.description}">

<#macro class_node node>
	<span class="class-node" iri="${node.iri}">
		${node.name} <#if node.countInstances?? && (node.countInstances > 0)>[${node.countInstances}]</#if>
	</span>
	
	
	<#if node.subclasses??>
		<ul>
			<#list node.subclasses as subclass>
				<li><@class_node subclass /></li>
			</#list>
		</ul>
	</#if>
	
	<#if node.instances??>
		<ul>
			<#list node.instances as instance>
				<li data-jstree='{"icon":"glyphicon glyphicon-leaf"}'><span class="instance-node" iri="${instance.iri}">${instance.name}</span></li>
			</#list>
		</ul>
	</#if>
</#macro>

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#include "partials/Navbar.ftl">
		<#include "partials/Heading.ftl">
		<#include "partials/project/Links.ftl">
		<#include "partials/Messages.ftl">
		
		<div class="container">
			<div class="row">
				<div id="taxonomy-tree" class="well col-md-5">
					<ul>
						<li class="jstree-open"><@class_node taxonomy /></li>
					</ul>
				</div>
				
				<pre id="description" class="col-md-7"></pre>
			</div>
		</div>
	
		<#include "partials/Footer.ftl">
		
		<script type="text/javascript">
			$('#taxonomy-tree').jstree();
		
			$('#taxonomy-tree').bind('select_node.jstree', function(e, selected) {
				var data = {
					ontologies: '${project.projectId}',
					iri:         jQuery.parseHTML(selected.node.text)[0].getAttribute('iri'),
					match:       'exact'
				};
				
				$.getJSON('${rootPath}/entity', data, function(json) {
					$('#description').html(JSON.stringify(json, null, 2));
				}, 'application/json');
			});
		</script>
	</body>
</html>