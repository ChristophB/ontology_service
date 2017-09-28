<#assign title = "Phenotypes">
<#assign current = "Phenotypes">
<#assign current_submenu = "overview">
<#assign heading = "Phenotypes">
<#assign subHeading ="Create New Phenotypes or View Existing Ones">

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#include "partials/Navbar.ftl">
    	<#include "partials/Heading.ftl">
		<#include "partials/phenotype/Links.ftl">
		<#include "partials/Messages.ftl">
		
    	<div class="container">
    		<p class="text-center">
				Phenotype definition... And description of both kinds of phenotypes...
			</p>

			<form action="${rootPath}/phenotype" method="get" class="form-horizontal row center">
				<label for="id" class="control-label col-sm-3">Ontology ID:</label>
				<div class="col-sm-4">
					<input type="text" class="form-control" name="id" placeholder="Ontology ID" value="<#if id??>${id}</#if>">
				</div>

				<input type="submit" class="btn btn-primary col-sm-2">
			</form>
		</div>
	    
	    <#include "partials/Footer.ftl">
	</body>
</html>
