<#-- @ftlvariable name="" type="de.onto_med.webprotege_rest_api.views.SimpleListView" -->
<#assign title = "Resultset">
<#assign current = "Projects">
<#assign heading = "Resultset">
<#assign subHeading ="Set of ${column?html}.">

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#include "partials/Navbar.ftl">
		<#include "partials/Heading.ftl">
		<#include "partials/Messages.ftl">
		
		<div class="container">
			<div class="row">
				<table class="table">
					<thead>
						<tr>
							<th>${column?html}</th>
						</tr>
					</thead>
					
					<tbody>
						<#list resultset as result>
							<tr>
								<td>${result?html}</td>
							</tr>
						</#list>
					</tbody>
				</table>
			</div>
		</div>
		
		<#include "partials/Footer.ftl">
	</body>
</html>