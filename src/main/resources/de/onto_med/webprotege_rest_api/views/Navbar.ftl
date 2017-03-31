<#macro active name><#if current == name> class="active"</#if></#macro>

<nav class="navbar navbar-default navbar-fixed-top">
	<div class="container">
		<a class="navbar-brand" href="https://github.com/ChristophB/webprotege-rest-api">WebProt&#233;g&#233; REST-API User Interface</a>
		<ul class="nav navbar-nav navbar-right">
			<li<@active "Documentation" />><a href="/webprotege-rest-api">Documentation</a></li>
			<li<@active "Projects" />><a href="/webprotege-rest-api/projects">Projects</a></li>
			<li<@active "Query Entities" />><a href="/webprotege-rest-api/entity-form">Query Entities</a></li>
			<li<@active "Reasoning" />><a href="/webprotege-rest-api/reason-form">Reasoning</a></li>
		</ul>
	</div>
</nav>
<div class="row" style="height: 50px; width:100%"></div>