<#assign title = "Phenotypes">
<#assign current = "Phenotypes">
<#assign current_submenu = "overview">

<#macro active name><#if current_submenu == name>active</#if></#macro>

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#include "partials/Navbar.ftl">
    	
    	<div class="jumbotron text-center" style="padding: 10 0 10">
			<h2>Phenotypes</h2>
			<p>Create New Phenotypes or View Existing Ones</p>
		</div>
		
		<#include "partials/PhenotypeLinks.ftl">
		
    	<div class="container">
			<p class="text-center">
				Phenotype definition... And description of both kinds of phenotypes...
			</p>
    	</div>
	    
	    <#include "partials/Footer.ftl">
	</body>
</html>
