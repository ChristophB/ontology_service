<#assign title = "All Phenotypes">
<#assign current = "Phenotypes">
<#assign current_submenu = "all">
<#assign heading = "Show All Phenotypes">
<#assign subHeading ="A List of All Defined Phenotypes">

<#macro active name><#if current_submenu == name>active</#if></#macro>

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#include "partials/Navbar.ftl">
    	<#include "partials/Heading.ftl">
		<#include "partials/PhenotypeLinks.ftl">
		<#include "partials/Messages.ftl">
		
    	<div class="container">
    		<div class="row">
	    		<div class="col-sm-6">
					<div id="phenotype-tree" class="well pre-scrollable " style="height:60%"></div>
				</div>
				
				<pre class="well col-sm-6" id="description-container" style="height:60%"></pre>
			</div>
    	</div>
	    
	    <#include "partials/Footer.ftl">
	    
	    <script type="text/javascript">
			$(document).ready(function() {
				$.getJSON('${rootPath}/phenotype/all', function(data) {
					data.forEach(function(node) {
						preProcessPhenotype(node); 
					});
					
					$('#' + 'phenotype-tree').jstree({
						core : {
							multiple : false,
							data : data
						}
					});
				});
				
				$('#phenotype-tree').bind('select_node.jstree', function(e, selected) {
					var iri = selected.node.a_attr.iri;
					
					$.getJSON('${rootPath}/phenotype/' + encodeURIComponent(iri), function(json) {
						$('#description-container').html(JSON.stringify(json, null, 2));
					}, 'application/json');
				});
			});
		</script>
	</body>
</html>