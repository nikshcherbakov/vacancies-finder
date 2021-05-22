const button = $('#submitButton');
const orig = [];

$.fn.getType = function () {
    return this[0].tagName === "INPUT" ? $(this[0]).attr("type").toLowerCase() : this[0].tagName.toLowerCase();
}

$("form :input").each(function () {
    const type = $(this).getType();
    const tmp = {
        'type': type,
        'value': $(this).val()
    };
    if (type === 'radio' || type === 'checkbox') {
        tmp.checked = $(this).is(':checked');
    }
    orig[$(this).attr('id')] = tmp;
});

$('form').bind('change keyup', function () {

    let disable = true;
    $("form :input").each(function () {
        let type = $(this).getType();
        let id = $(this).attr('id');

        if (type === 'text' || type === 'select') {
            disable = (orig[id].value === $(this).val());
        } else if (type === 'radio' || type === 'checkbox') {
            disable = (orig[id].checked === $(this).is(':checked'));
        } else if (id === 'googleMapsLong' || id === 'googleMapsLat') {
            disable = parseFloat(orig[id].value) === parseFloat($(this).val());
        }

        if (!disable) {
            return false; // break out of loop
        }
    });

    button.prop('disabled', disable);
});

$('#salary').on("keypress keyup blur", function (event) {
    $(this).val($(this).val().replace(/[^\d].+/, ""));
    if ((event.which < 48 || event.which > 57)) {
        event.preventDefault();
    }
});
