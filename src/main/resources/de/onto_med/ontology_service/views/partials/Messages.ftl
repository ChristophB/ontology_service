<div class="container">
	<#if errorMessage??>
		<div class="alert alert-danger">
			<strong>Error:</strong> ${errorMessage}
		</div>
	</#if>
	
	<#if successMessage??>
		<div class="alert alert-success">
			<strong>Error:</strong> ${successMessage}
		</div>
	</#if>
	
	<#if infoMessage??>
		<div class="alert alert-info">
			<strong>Error:</strong> ${infoMessage}
		</div>
	</#if>
</div>