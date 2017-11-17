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
	$('#message').remove();
	$('body').append(
		'<div id="message" class="alert alert-' + state + ' fade in">'
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
					drop.val(drop.val() + ' ' + attributes.aliasEn.value + ' ');
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

function showPhenotypeForm(id, clear = false) {
	$('#abstract-phenotype-form, #phenotype-category-form').addClass('hidden');
	$('#numeric-phenotype-form, #string-phenotype-form, #date-phenotype-form, #boolean-phenotype-form').addClass('hidden');
	$('#calculation-phenotype-form, #composite-boolean-phenotype-form').addClass('hidden');

	$(id).removeClass('hidden');
	if (clear === true) clearPhenotypeFormData();
}

function clearPhenotypeFormData() {
	$('#message').remove();
	$('form:not(.hidden) input[type!=checkbox].form-control, form:not(.hidden) textarea.form-control, form:not(.hidden) select').val(null);
	$('form:not(.hidden) input[type=checkbox]').removeAttr('checked');
	$('.generated').remove();
	$('.hidden-language').val('en');
	$('form:not(.hidden) #title-languages').val('en');
	toggleValueDefinition();

	$.getJSON('all?type=list', function(data) {
		var input = document.querySelector('form:not(.hidden) input.awesomplete#titles');
    	var awesomplete = new Awesomplete(input, { list: data });
    });
}

function customMenu(node) {
	var items = {
		showCategoryForm: {
			label: 'Create Sub Category',
			action: function() {
				showPhenotypeForm('#phenotype-category-form', true);
				$('#super-category').val(node.a_attr.id);
			}
		},
		showAbstractPhenotypeForm: {
			label: 'Create Abstract Phenotype',
			action: function() {
				showPhenotypeForm('#abstract-phenotype-form', true);
				$('form:not(.hidden) #categories').val(node.a_attr.id);
			}
		},
		showRestrictedPhenotypeForm: {
			label: 'Create Restricted Phenotype',
			action: function() {
				switch (node.a_attr.type) {
					case 'date': showPhenotypeForm('#date-phenotype-form', true); break;
					case 'string': showPhenotypeForm('#string-phenotype-form', true); break;
					case 'numeric': showPhenotypeForm('#numeric-phenotype-form', true); break;
					case 'boolean': showPhenotypeForm('#boolean-phenotype-form', true); break;
					case 'composite-boolean': showPhenotypeForm('#composite-boolean-phenotype-form', true); break;
					case 'calculation': showPhenotypeForm('#calculation-phenotype-form', true); break;
					default: return;
				}

				$('form:not(.hidden) #super-phenotype').val(node.a_attr.id);
			}
		},
		inspect: {
			label: 'Inspect',
			action: function() {
				$.getJSON(node.a_attr.id, function(data) {
					inspectPhenotype(data);
				});
			}
		},
		getDecisionTreePng: {
			label: 'Get Decision Tree As PNG',
			action: function() {
				var win = window.open('decision-tree?phenotype=' + node.a_attr.id + '&format=png', '_blank');
				win.focus();
			}
		},
		getDecisionTreeGraphml: {
			label: 'Get Decision Tree As GraphML',
			action: function() {
				var win = window.open('decision-tree?phenotype=' + node.a_attr.id + '&format=graphml', '_blank');
				win.focus();
			}
		},
		delete: {
			label: 'Delete',
			action: function() {
				// TODO: implement deletion of phenotypes and categories
				alert('Not implemented.');
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

	if (node.a_attr.id === 'Phenotype_Category') {
		delete items.delete;
		delete items.inspect;
	}
	return items;
}

function focusInputEnd(input) {
	var length = input.val().length * 2;
	input.focus();
	input[0].setSelectionRange(length, length);
}

function inspectIfExists(id) {
	$.getJSON(id, function(data) {
    	if (data != undefined) inspectPhenotype(data);
    });
}

function inspectPhenotype(data) {
	clearPhenotypeFormData();

	var form;
	if (data.abstractPhenotype === true) {
		form = '#abstract-phenotype-form';
		$(form + ' #ucum').val(data.unit);
		$(form + ' #datatype').val(getDatatype(data));
		toggleValueDefinition();
	} else if (data.restrictedPhenotype === true) {
		switch (getDatatype(data)) {
			case 'date':    form = '#date-phenotype-form';    break;
			case 'string':  form = '#string-phenotype-form';  break;
			case 'numeric': form = '#numeric-phenotype-form'; break;
			case 'boolean': form = '#boolean-phenotype-form'; break;
			case 'composite-boolean':
				form = '#composite-boolean-phenotype-form';
				$(form + ' #expression').val(data.manchesterSyntaxExpression); // TODO: print original string
				$(form + ' #score').val(data.score);
				break;
			case 'calculation': form = '#calculation-phenotype-form'; break;
		}
		$(form + ' #super-phenotype').val(data.abstractPhenotypeName);
	} else {
    	form = '#phenotype-category-form';
    	// TODO: add super category value to form
    }

	showPhenotypeForm(form);

	var counter = 1;
	for (var lang in data.titles) {
		var title = data.titles[lang];

		if (counter == 1) {
			$(form + ' #title-div #title-languages').val(lang);
            $(form + ' #title-div .input-group:not(.hidden):first input[type=text]#titles').val(title.titleText);
            if (title.alias != null) $(form + ' #title-div .input-group:not(.hidden):first input[type=text]#aliases').val(title.alias);
		} else {
			addRow('#title-div');
			$(form + ' #title-div .generated:last select').val(lang);
			$(form + ' #title-div .generated:last input[type=text]#titles').val(title.titleText);
			if (title.alias != null) $(form + ' #title-div .generated:last input[type=text]#aliases').val(title.alias);
		}
		counter++;
    }

	$(form + ' #categories').val(data.phenotypeCategories !== undefined ? data.phenotypeCategories.join('; ') : null);

	for (var lang in data.labels) {
		data.labels[lang].forEach(function(label) {
			addRow('#label-div');
			$(form + ' #label-div .generated:last select').val(lang);
			$(form + ' #label-div .generated:last input[type=text]').val(label);
		});
	}
	for (var lang in data.descriptions) {
		data.descriptions[lang].forEach(function(description) {
			addRow('#description-div');
            $(form + ' #description-div .generated:last select').val(lang);
            $(form + ' #description-div .generated:last textarea').val(description);
		});
    }
	data.relatedConcepts.forEach(function(relation) {
    	addRow('#relation-div');
        $(form + ' #relation-div .generated:last input[type=text]').val(relation);
    });
    addRange(form, data.phenotypeRange);
}

function addRange(form, range) {
	if (!range) return;

	var asDate = range.dateValue || range.dateValues || range.dateRange;

    var value       = range.stringValue || range.dateValue || range.integerValue || range.doubleValue;
    var values      = range.stringValues || range.dateValues || range.integerValues || range.doubleValues;
    var rangeValues = range.dateRange || range.integerRange || range.doubleRange;

	if (value) {
		addEnumFieldWithValue(form, convertValue(value, asDate));
	} else if (values) {
		values.forEach(function(value) {
			addEnumFieldWithValue(form, convertValue(value, asDate));
		});
	} else if (rangeValues) {
		if (rangeValues.MIN_INCLUSIVE) {
			$(form + ' #range-min-operator').val('>=');
			$(form + ' #range-min').val(convertValue(rangeValues.MIN_INCLUSIVE, asDate));
		} else if (rangeValues.MIN_EXCLUSIVE) {
			$(form + ' #range-min-operator').val('>');
            $(form + ' #range-min').val(convertValue(rangeValues.MIN_EXCLUSIVE, asDate));
		}
		if (rangeValues.MAX_INCLUSIVE) {
           	$(form + ' #range-max-operator').val('<=');
           	$(form + ' #range-max').val(convertValue(rangeValues.MAX_INCLUSIVE, asDate));
        } else if (rangeValues.MAX_EXCLUSIVE) {
            $(form + ' #range-max-operator').val('<');
           	$(form + ' #range-max').val(convertValue(rangeValues.MAX_EXCLUSIVE, asDate));
        }
	}
}

function convertValue(value, asDate) {
	if (asDate) {
		return new Date(value).toISOString().substring(0, 10);
	} else {
		return value;
	}
}

function addEnumFieldWithValue(form, value) {
	addRow('#enum-form-group');
    $(form + ' #enum-form-group .generated:last input[type=text]').val(value);
}

function getDatatype(data) {
	if (data.abstractBooleanPhenotype === true || data.restrictedBooleanPhenotype === true) {
		return "composite-boolean";
    } else if (data.abstractCalculationPhenotype === true || data.restrictedCalculationPhenotype === true) {
    	return "calculation";
    } else if (data.datatype == 'XSD_STRING') {
    	return "string";
    } else if (data.datatype == 'XSD_DATE_TIME' || data.datatype == 'XSD_LONG') {
    	return "date";
    } else if (data.datatype == 'XSD_INTEGER' || data.datatype == 'XSD_DOUBLE') {
    	return "numeric";
    } else if (data.datatype == 'XSD_BOOLEAN') {
    	return "boolean";
    }
}