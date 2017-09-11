function toggleValueDefinition() {
	$('#ucum-form-group, #is-decimal-form-group, #formula-form-group').addClass('hidden');

	if ($('#datatype').val() == 'numeric' || $('#datatype').val() == 'calculation')
		$('#ucum-form-group, #is-decimal-form-group').removeClass('hidden');
	if ($('#datatype').val() == 'calculation')
		$('#formula-form-group').removeClass('hidden');
}

function addRow(id) {
	var row = $('form:not(.hidden) ' + id + ' .hidden').clone();
	row.removeClass('hidden');
	row.addClass('generated');
	$('form:not(.hidden) ' + id).append(row);
}

function showMessage(text, state) {
	$('#messages-div').empty();
	$('#messages-div').append(
		'<div id="message" class="alert alert-' + state + ' col-sm-6 col-sm-offset-3">'
			+ '<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>'
			+ text
		+ '</div>'
	);
}

function createPhenotypeTree(id, url, withContext) {
	$('#' + id).jstree({
		core: {
			multiple: false,
			data: {
				url: url
			}
		},
		plugins: [ 'contextmenu', 'dnd' ],
		contextmenu: { items: withContext ? customMenu : null }
	});

	$(document).on('dnd_move.vakata', function (e, data) {
		var t = $(data.event.target);
		var attributes = data.element.attributes;
		var drop = t.closest('.drop');

		if (!t.closest('.jstree').length && drop.length) { // field with class "drop" outside of jstree
			if (attributes.type.value === "null" && drop.hasClass('category')) {
				data.helper.find('.jstree-icon').removeClass('jstree-er').addClass('jstree-ok');
				return;
			} else if (attributes.type.value !== "null" && drop.hasClass('phenotype')){
				if (drop[0].id === 'reason-form-drop-area') {
					if (attributes.singlePhenotype.value == "true") {
						data.helper.find('.jstree-icon').removeClass('jstree-er').addClass('jstree-ok');
						return;
					}
				} else if (drop[0].id !== 'formula' || ['string'].indexOf(attributes.type.value) == -1) {
					data.helper.find('.jstree-icon').removeClass('jstree-er').addClass('jstree-ok');
					return; // formula does not accepts string
				}
			}
		}
		data.helper.find('.jstree-icon').removeClass('jstree-ok').addClass('jstree-er');
	}).on('dnd_stop.vakata', function (e, data) {
		var t = $(data.event.target);
		var attributes = data.element.attributes;
		var drop = t.closest('.drop');

		if (!t.closest('.jstree').length && drop.length) { // field with class "drop" outside of jstree
			if (attributes.type.value === "null" && drop.hasClass('category')) {
				drop.val(drop.val() + ' ' + data.element.text + ' ');
				focusInputEnd(drop);
			} else if (attributes.type.value !== "null" && drop.hasClass('phenotype')) {
				if (drop[0].id === 'reason-form-drop-area') {
					if (attributes.singlePhenotype.value == "true")
						appendFormField(data.element, drop[0]);
				} else if (drop[0].id !== 'formula' || ['string'].indexOf(attributes.type.value) === -1) {
					drop.val(drop.val() + ' ' + getNodeId(data.element) + ' ');
					focusInputEnd(drop);
				} // else: formula does not accept string
			}
		}
	});
}

function appendFormField(element, target) {
	var id = element.id.replace("_anchor", "");
	var type = element.attributes.type.value;

	if (type === "numeric") type = "number";
	if (type === "string") type = "text";

	var inputField = '';
	console.log(element.attributes);
	if (element.attributes.restrictedPhenotype.value === "true") {
		inputField = '<input type="hidden" name="' + id + '">';
	} else if (['boolean', 'composite-boolean'].indexOf(type) !== -1) {
		inputField
			= '<select class="form-control" name="' + id + '">'
				+ '<option value="true">True</option>'
				+ '<option value="false">False</option>'
			+ '</select>';
	} else {
		inputField = '<input type="' + type + '" class="form-control" name="' + id + '">';
	}

	var html
		= '<div class="form-group row">'
			+ '<label for="' + id + '" class="control-label col-sm-4">' + element.text + '</label>'
			+ '<div class="col-sm-6">'
				+ inputField
			+ '</div>'
			+ '<a class="btn btn-danger" href="#" onclick="$(this).parent().remove()">'
				+ '<i class="fa fa-times fa-lg"></i>'
			+ '</a>'
		+ '</div>';

	$(target).append(html);
	$('.form-control:last').focus();
}

function showPhenotypeForm(id) {
	$('#abstract-phenotype-form, #phenotype-category-form').addClass('hidden');
	$('#numeric-phenotype-form, #string-phenotype-form, #date-phenotype-form, #boolean-phenotype-form').addClass('hidden');
	$('#calculation-phenotype-form, #composite-boolean-phenotype-form').addClass('hidden');

	$(id).removeClass('hidden');
	clearPhenotypeFormData();
}

function clearPhenotypeFormData() {
	$('form:not(.hidden) input[type!=checkbox].form-control, form:not(.hidden) textarea.form-control').val(null);
	$('.generated').remove();
}

function customMenu(node) {
	var items = {
		showCategoryForm: {
			label: 'Create Sub Category',
			action: function() {
				showPhenotypeForm('#phenotype-category-form');
				$('#super-category').val(node.text);
			}
		},
		showAbstractPhenotypeForm: {
			label: 'Create Abstract Phenotype',
			action: function() {
				showPhenotypeForm('#abstract-phenotype-form');
				$('form:not(.hidden) #categories').val(node.text);
			}
		},
		showRestrictedPhenotypeForm: {
			label: 'Create Restricted Phenotype',
			action: function() {
				switch (node.a_attr.type) {
					case 'date': showPhenotypeForm('#date-phenotype-form'); break;
					case 'string': showPhenotypeForm('#string-phenotype-form'); break;
					case 'numeric': showPhenotypeForm('#numeric-phenotype-form'); break;
					case 'boolean': showPhenotypeForm('#boolean-phenotype-form'); break;
					case 'composite-boolean': showPhenotypeForm('#composite-boolean-phenotype-form'); break;
					case 'calculation': showPhenotypeForm('#calculation-phenotype-form'); break;
					default: return;
				}

				$('form:not(.hidden) #super-phenotype').val(getNodeId(node));
			}
		},
		inspect: {
			label: 'Inspect',
			action: function() {
				$.getJSON(getNodeId(node), function(data) { showMessage(JSON.stringify(data, null, 2), "info") });
			}
		},
		getDecisionTreePng: {
			label: 'Get Decision Tree As PNG',
			action: function() {
				var win = window.open('decision-tree?phenotype=' + getNodeId(node) + '&format=png', '_blank');
				win.focus();
			}
		},
		getDecisionTreeGraphml: {
			label: 'Get Decision Tree As GraphML',
			action: function() {
				var win = window.open('decision-tree?phenotype=' + getNodeId(node) + '&format=graphml', '_blank');
				win.focus();
			}
		}
	};

	if (!node.a_attr.phenotype) {
		delete items.showRestrictedPhenotypeForm;
	} else {
		delete items.showCategoryForm;
		delete items.showAbstractPhenotypeForm;
	}

	if (!node.a_attr.abstractPhenotype) {
		delete items.getDecisionTreePng;
		delete items.getDecisionTreeGraphml;
	}
	return items;
}

function getNodeId(node) {
	return node.a_attr.id;
}

function focusInputEnd(input) {
	var length = input.val().length * 2;
	input.focus();
	input[0].setSelectionRange(length, length);
}