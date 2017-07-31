<#assign title = "Phenotypes">
<#assign current = "Phenotypes">
<#assign current_submenu = "overview">
<#assign heading = "Phenotypes">
<#assign subHeading ="Create New Phenotypes or View Existing Ones">

<#macro active name><#if current_submenu == name>active</#if></#macro>

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#include "partials/Navbar.ftl">
    	<#include "partials/Heading.ftl">
		<#include "partials/PhenotypeLinks.ftl">
		<#include "partials/Messages.ftl">
		
    	<div class="container">
			<p class="text-center">
				Phenotype definition... And description of both kinds of phenotypes...
			</p>
    	</div>
	    
	    <#include "partials/Footer.ftl">
	</body>
</html>
