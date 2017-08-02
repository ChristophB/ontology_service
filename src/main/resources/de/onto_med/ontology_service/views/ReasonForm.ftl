<#-- @ftlvariable name="" type="de.onto_med.webprotege_rest_api.views.ReasonFormView" -->
<#assign title = "Reasoning">
<#assign current = "Reasoning">
<#assign heading = "Reasoning">
<#assign subHeading ="<!-- Description -->">

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#include "partials/Navbar.ftl">
		<#include "partials/Heading.ftl">
		<#include "partials/Messages.ftl">
		
		<div class="container">
			<div class="row well">
				<form action="${rootPath}/reason" method="get" class="form" role="form">
					<#include "partials/project/Expression.ftl">
					<#include "partials/project/Ontologies.ftl">
					
					<div class="form-group row">
						<div class="col-md-8">
							<input type="submit" class="btn btn-default pull-right">
						</div>
					</div>
				</form>
			</div>
		</div>
		
		<#include "partials/Footer.ftl">
	</body>
</html>