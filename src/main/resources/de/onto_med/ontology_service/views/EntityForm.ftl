<#assign title = "Query Entities">
<#assign current = "Query Entities">
<#assign heading = "Query Entities">
<#assign subHeading ="<!-- Description -->">

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#include "partials/Navbar.ftl">
		<#include "partials/Heading.ftl">
		<#include "partials/Messages.ftl">
		
		<div class="container">
			<div class="row well">
				<form action="${rootPath}/entity" method="get" class="form" role="form">
					<#include "partials/project/Type.ftl">
					<#include "partials/project/Name.ftl">
					<#include "partials/project/Iri.ftl">
					<#include "partials/project/Property.ftl">
					<div class="form-group row">
						<#include "partials/project/Match.ftl">
						<#include "partials/project/Operator.ftl">
					</div>
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