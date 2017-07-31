<#assign title = "All Phenotypes">
<#assign current = "Phenotypes">
<#assign current_submenu = "all">

<#macro active name><#if current_submenu == name>active</#if></#macro>

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#include "partials/Navbar.ftl">
    	
    	<div class="jumbotron text-center" style="padding: 10 0 10">
			<h2>Show All Phenotypes</h2>
			<p>A List of All Defined Phenotypes</p>
		</div>
		
		<#include "partials/PhenotypeLinks.ftl">
		
    	<div class="container">
    		<div class="row">
	    		<div class="col-sm-6">
					<div id="phenotype-tree" class="well pre-scrollable " style="height:60%"></div>
				</div>
				
				<div class="well col-sm-6" id="description-container" style="height:60%">
				
				</div>
			</div>
    	</div>
	    
	    <#include "partials/Footer.ftl">
	    
	    <script type="text/javascript">
			$(document).ready(function() {
				createPhenotypeTree('phenotype-tree', '${rootPath}/phenotype/all');
			});
		</script>
	</body>
</html>