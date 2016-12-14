<#-- @ftlvariable name="" type="de.uni_leipzig.imise.webprotege.rest_api.views.SimpleListView" -->
<#assign title = "Resultset">
<#assign current = "Projects">

<html>
	<#include "Head.ftl">
	
	<body>
		<#include "Navbar.ftl">
		
		<div class="jumbotron text-center" style="padding: 10 0 10">
			<h2>Resultset</h2>
			<p>Set of ${column?html}.</p>
		</div>
		
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
	</body>
</html>