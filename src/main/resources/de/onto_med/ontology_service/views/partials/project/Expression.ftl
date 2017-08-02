<div class="form-group row">
	<label for="ce" class="col-md-2">Class Expression:</label>
	<div class="col-md-6">
		<textarea name="ce" col="3" placeholder="Manchestersyntax" class="form-control"><#if ce??>${ce}</#if></textarea>
		<small class="form-text text-muted">
			Whenever you refere to an OWLEntity, add the shortform as a prefix.<br>
			e.g.: class <i>Example</i> in ontology "http://example.com/example_ontology" becomes "example_ontology:Example".
		</small>
	</div>
</div>