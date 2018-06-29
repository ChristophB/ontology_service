<div id="messages-div" class="container">
	<#list messages?keys as key>
		<#list messages[key] as message>
			<div class="alert alert-${key}">
				<!--<strong>${key}:</strong> -->
				${message?no_esc}
			</div>
		</#list>
	</#list>
</div>