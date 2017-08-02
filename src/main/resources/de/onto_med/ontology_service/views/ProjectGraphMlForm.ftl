<#assign title = "GraphML Form">
<#assign current = "Projects">
<#assign current_submenu = "graphml-form">
<#assign heading = "GraphML Generator for Project <i>${project.projectIri}</i>">
<#assign subHeading ="Use the form to customize the GraphML.">

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#include "partials/Navbar.ftl">
		<#include "partials/Heading.ftl">
		<#include "partials/project/Links.ftl">
		<#include "partials/Messages.ftl">
		
		<div class="container">
			<div class="row well">
				<form action="/project/${rootPath}/graphml" method="get" class="form" role="form">
					<!-- TODO: create form fields -->
				</form>
			</div>
		</div>
		
		<#include "partials/Footer.ftl">
	</body>
</html>