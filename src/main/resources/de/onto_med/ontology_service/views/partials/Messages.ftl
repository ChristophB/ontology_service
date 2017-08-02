<div class="container">
	<#list errorMessages as message>
		<div class="alert alert-danger">
			<strong>Error:</strong> ${message}
		</div>
	</#list>
	
	<#list successMessages as message>
		<div class="alert alert-success">
			<strong>Success:</strong> ${message}
		</div>
	</#list>
	
	<#list infoMessages as message>
		<div class="alert alert-info">
			<strong>Info:</strong> ${message}
		</div>
	</#list>
</div>