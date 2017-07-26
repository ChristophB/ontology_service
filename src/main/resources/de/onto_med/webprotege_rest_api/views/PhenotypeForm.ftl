<!DOCTYPE html>

<#-- @ftlvariable name="" type="de.onto_med.webprotege_rest_api.views.EntityFormView" -->
<#assign title = "Phenotype Create Form">
<#assign current = "Phenotypes">

<html>
	<#include "Head.ftl">
	
	<body>
		<#include "Navbar.ftl">

		<main class="container">
			<section id="banner" class="row jumbotron text-center">
				<h2>Single or Composit Phenotype-Definition Form</h2>
				<p class="text-muted">On this page you can define a new <strong>single or composit phenotype</strong> with all required metadata and properties.</p>
			</section>
			
			<!-- The single phenotype form -->
			<section name="content" class="row">
				<div class="col-md-9">
					<form id="composit-phenotype-form" class="" role="form" action="${rootPath}/phenotype/create" method="post" accept-charset="UTF-8">
						
						<!-- Primary label -->
						<div class="form-group">
							<label for="label-en" class="control-label">Label</label>
							<div class="row">
								<div class="col-sm-6">
									<div class="input-group">
										<span class="input-group-addon" data-toggle="tooltip" title="English" data-placement="right">EN</span>
										<input type="text" class="form-control" id="label-en" name="label-en" placeholder="English Label" aria-descripedby="label-en-help" required>
									</div>
								</div>
								<div class="col-sm-6">
									<div class="input-group">
										<span class="input-group-addon" data-toggle="tooltip" title="German" data-placement="right">DE</span>
										<input type="text" class="form-control" id="label-de" name="label-de" placeholder="German Label" aria-descripedby="label-en-help">
									</div>
								</div>
							</div>
							<small id="label-en-help" class="form-text text-muted">Please try to formulate the label as clear as possible and as short as possible.</small>
						</div>

						<!-- Aliases -->
						<div class="form-group" id="alias-form-group">
							<label>Aliases</label>
							<a class="btn btn-primary btn-xs" onclick="addRow('alias-form-group')" data-toggle="tooltip" title="Add an alias" data-placement="right">
								<i class="fa fa-plus" aria-hidden="true"></i>
							</a>
							<div class="row hidden">
								<div class="col-sm-6">
									<div class="input-group">
										<span class="input-group-addon" data-toggle="tooltip" title="English" data-placement="right">EN</span>
										<input type="text" class="form-control" name="alias-en[]" placeholder="English Alias">
									</div>
								</div>
								<div class="col-sm-6">
									<div class="input-group">
										<span class="input-group-addon" data-toggle="tooltip" title="German" data-placement="right">DE</span>
										<input type="text" class="form-control" name="alias-de[]" placeholder="German Alias">
									</div>
								</div>
							</div>
						</div>

						<!-- Super phenotype -->
						<div class="form-group">
							<div class="row">
								<div class="checkbox col-sm-3">
									<label for="has-super-phenotype"  class="control-label">
										<input type="checkbox" id="has-super-phenotype" name="has-super-phenotype" onchange="toggleSuperPhenotype()" value="true">Has Super-Phenotype
									</label>
								</div>
								<small class="form-text text-muted col-sm-6">
									Check this box, if this phenotype is a derivation of another phenotype.<br>
									e.g.: "Weight between 50kg and 60kg" is a derivation of phenotype "Weight".
								</small>
							</div>
							<div id="super-phenotype-div" class="row hidden">
								<div class="col-sm-4">
									<input type="text" class="form-control" id="super-phenotype" name="super-phenotype">
								</div>
								<small id="label-en-help" class="form-text text-muted col-sm-5">Right-Click on a phenotype on the right site and select "Set as Super-Phenotype".</small>
							</div>
						</div>

						<!-- Category -->
						<div class="row">
							<div class="form-group col-sm-6">
								<label for="category" class="control-label">Category</label>
								<select id="category" name="category" class="form-control" onchange="toggleNewCategoryField()">
									<option />
									<option value="new_category">Create new Category</option>
									<option value="Category_1">Category 1</option>
									<option value="Category_2">Category 2</option>
								</select>
							</div>

							<div id="new-category-form-group" class="form-group col-sm-6" style="display:none">
								<label for="new-category" class="control-label">New Category</label>
								<input type="text" id="new-category" name="new-category" class="form-control" placeholder="New Category Name">
							</div>
						</div>

						<!-- Definition -->
						<div class="form-group">
							<label for="definition-en" class="control-label">Definition</label>
							<div class="input-group">
								<span class="input-group-addon" data-toggle="tooltip" title="English" data-placement="right">EN</span>
								<textarea class="form-control" id="definition-en" name="definition-en" placeholder="English Definition"></textarea>
							</div>
							<div class="input-group">
								<span class="input-group-addon" data-toggle="tooltip" title="German" data-placement="right">DE</span>
								<textarea class="form-control" name="definition-de" placeholder="German Definition"></textarea>
							</div>
						</div>

						<!-- Specifications -->
						<div class="row">
							<!-- Datatype -->
							<div class="form-group col-sm-3">
								<label for="datatype" class="control-label">Datatype</label>
								<select id="datatype" name="datatype" class="form-control" onchange="toggleValueDefinition()" required>
									<option />
									<option value="integer">Integer</option>
									<option value="double">Floating Number</option>
									<option value="string">String</option>
									<option value="formula">Composit Formula</option>
									<option value="expression">Composit Boolean Expression</option>
								</select>
							</div>

							<div id="datatype-specification" class="col-sm-9">
								<!-- Value definition: Enum -->
								<div id="enum-form-group" class="form-group" style="display:none">
									<label class="control-label">Possible Values</label>
									<a class="btn btn-primary btn-xs" onclick="addRow('enum-form-group')">
										<i class="fa fa-plus" aria-hidden="true"></i>
									</a><br>
									<small class="form-text text-muted">
										Define specific values and add appropriate labels.
									</small>
									<div class="row hidden">
										<div class="col-sm-5">
											<input type="text" class="form-control" name="enum-value[]" placeholder="Value">
										</div>
										<div class="col-sm-1" style="margin-top:5px">=></div>
										<div class="col-sm-6">
											<input type="text" class="form-control" name="enum-label[]" placeholder="Label">
										</div>
									</div>
								</div>

								<!-- Formula definition -->
								<div id="formula-form-group" class="form-group" style="display:none">
									<label for="formula" class="control-label">Formula</label>
									<textarea class="form-control drop phenotype" id="formula" name="formula" aria-describedby="formula-help" placeholder="(Phenotype_1 + Phenotype_2) / Phenotype_3"></textarea>
									<small id="formula-help" class="form-text text-muted">Click on a phenotype on the right site to add it into your formula.</small>
								</div>

								<!-- Value definition: UCUM -->
								<div id="ucum-form-group" class="form-group" style="display:none">
									<label for="ucum" class="control-label">UCUM</label>
									<input type="text" class="form-control" id="ucum" name="ucum" aria-describedby="ucum-help">
									<small id="ucum-help" class="form-text text-muted">If the numeric values do have a unit, specify it as <a href="http://unitsofmeasure.org/trac">UCUM</a>.</small>
								</div>

								<!-- Range specifications -->
								<div id="range-form-group" class="form-group" style="display:none">
									<label class="control-label">Ranges</label> 
									<a class="btn btn-primary btn-xs" onclick="addRow('range-form-group')">
										<i class="fa fa-plus" aria-hidden="true"></i>
									</a><br>
									<small class="form-text text-muted">
										Specify min and/or max values and give the resulting intervals a label.<br>
										e.g.: >20, &le;34.5 => normal | &le;20 => too low | &gt;34.5 => too high
									</small>

									<div class="row hidden">
										<div class="col-sm-2" style="padding-right:0">
											<select class="form-control" name="range-min-operator[]">
												<option />
												<option value="=">=</option>
												<option value="&ge;">&ge;</option>
												<option value="&gt;">&gt;</option>
											</select>
										</div>
										<div class="col-sm-2" style="padding-left:0">
											<input type="number" step="any" class="form-control" name="range-min[]" placeholder="Min">
										</div>
										
										<div class="col-sm-2" style="padding-right:0">
											<select class="form-control" name="range-max-operator[]">
												<option />
												<option value="&lt;">&lt;</option>
												<option value="&le;">&le;</option>
												<option value="=">=</option>
											</select>
										</div>
										<div class="col-sm-2" style="padding-left:0">
											<input type="number" step="any" class="form-control" name="range-max[]" placeholder="Max">
										</div>

										<div class="col-sm-4">
											<input type="text" class="form-control" name="enum" placeholder="Label">
										</div>
									</div>
								</div>
							
								<!-- Value definition: expression -->
								<div id="expression-form-group" class="form-group" style="display:none">
									<label for="expression" class="control-label">Boolean-Expression</label>
									<input type="button" value="AND"><input type="button" value="OR"><input type="button" value="XOR">
									<input type="button" value="NOT"><input type="button" value="("><input type="button" value=")">
									<textarea class="form-control drop phenotype" id="expression" name="expression"
										placeholder="Phenotype_1 AND (Phenotype_2 OR Phenotype_3)" aria-describedby="expression-help"></textarea>
									<small id="expression-help" class="form-text text-muted">
										Drag-and-drop a phenotype from the right site into your expression.
									</small>
								</div>

								<!-- Value definition: Boolean -->
								<div id="boolean-form-group" class="form-group" style="display:none">
									<label for="boolean" class="control-label">Possible Values</label><br>
									<small class="form-text text-muted">
										Define appropriate labels for True and False.
									</small>
									<div class="row">
										<label for="boolean-true-label" class="control-label col-sm-1">True:</label>
										<div class="col-sm-5">
											<input type="text" class="form-control" name="boolean-true-label" placeholder="Label">
										</div>
										<label for="boolean-false-label" class="control-label col-sm-1">False:</label>
										<div class="col-sm-5">
											<input type="text" class="form-control" name="boolean-false-label" placeholder="Label">
										</div>
									</div>
								</div>
							</div>
						</div>

						<!-- Relations -->
						<div id="relation-form-group" class="form-group">
							<label for="relation" class="control-label">Relations</label>
							<a class="btn btn-primary btn-xs" onclick="addTextField('#relation-form-group', 'relation[]', 'IRI to a related entity')"
								data-toggle="tooltip" title="Add a relation" data-placement="right">
								<i class="fa fa-plus" aria-hidden="true"></i>
							</a>
							<input type="text" class="form-control" id="relation[]" name="relation[]" placeholder="IRI to a related Entity">
						</div>

						<div class="form-group">
							<input type="submit" class="btn btn-primary" value="Create Phenotype">
						</div>
					</form>
				</div>

				<!-- Form sidebar for additional information -->
				<div class="col-md-3">
					<div data-spy="affix" data-offset-top="200">
						<div id="phenotype-tree" class="well pre-scrollable "></div>
					</div>
				</div>
			</section>
		</main>

		<#include "Footer.ftl">
		
		<!-- <footer>
			<div class="navbar navbar-default navbar-bottom">
					<div class="container">
						<p class="text-muted navbar-text">
							Provided by the <a href="http://www.onto-med.de">Onto-Med Research Group</a>
						</p>
						<p class="text-muted navbar-text pull-right">
							<a href="#">Help</a>
						</p>
					</div>
			</div>
		</footer>-->

		

		<script type="text/javascript">
			$(document).ready(function() {
				$('[data-toggle="tooltip"]').tooltip();
				createPhenotypeTree('phenotype-tree', '${rootPath}/phenotype/all');

				$('#expression-form-group input[type="button"]').on('click', function(button) {
					var expInput = $('#expression');
					expInput.val(expInput.val() + ' ' + this.value + ' ');
					focusInputEnd(expInput);
				});

				$('#expression').change(function() {
					$('#expression').scrollTop($('#expression')[0].scrollHeight);
				});
				$('#formula').change(function() {
					$('#formula').scrollTop($('#formula')[0].scrollHeight);
				});
			});
		</script>
	</body>
</html>