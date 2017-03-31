<#-- @ftlvariable name="" type="de.onto_med.webprotege_rest_api.views.ProjectTaxonomyView" -->
<#assign title = project.name?html>
<#assign current = "Projects">
<#assign current_submenu = "Taxonomy">
<#setting url_escaping_charset="UTF-8">

<#macro class node>
	<#if node.children??><a class="expander" href="#">[+]</a></#if>
	<a href="/webprotege-rest-api/entity?ontologies=${project.projectId}&iri=${node.iri?url}&match=exact" target="_blank">
		${node.name} <#if node.individuals?? && (node.individuals > 0)>[${node.individuals}]</#if>
	</a>
	<#if node.children??>
		<ul class="children">
			<#list node.children as child>
				<li><@class child /></li>
			</#list>
		</ul>
	</#if>
</#macro>

<html>
	<#include "Head.ftl">
	
	<body>
		<#include "Navbar.ftl">
		
		<div class="jumbotron text-center" style="padding: 10 0 10">
			<h2>${project.name?html}</h2>
			<p>${project.description}</p>
		</div>
		
		<div class="container">
			<#include "ProjectLinks.ftl">
			
			<div class="row">
				<div class="list">
					<ul>
						<li><@class taxonomy /></li>
					</ul>
				</div>
			</div>
		</div>
		
		<script type="text/javascript">
			<#noparse>
				$(document).ready(function() {
					$('.list li .expander').click(function() {
        				$(this).parent().children('ul').toggle();
    				});
    			});
    		</#noparse>
    	</script>
	
		<#include "Footer.ftl">
	</body>
</html>