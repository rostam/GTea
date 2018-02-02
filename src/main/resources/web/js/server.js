function server(url,response) {
    $.get(url).done(function (data) {
        response(data);
    }).fail(function (jqXHR, textStatus, errorThrown) {
        alert(errorThrown);
    });
}