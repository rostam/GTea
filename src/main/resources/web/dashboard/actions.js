function do_actions() {
    var action = $('#actions').find('option:selected').text();
    var act_name = $('#act_name').html();
    $('#parent_canvas').empty();
    $('#parent_canvas').append('<div id="canvas" class="main"></div>');
    if (action == "Load and Draw") {
        if (act_name == 'gen') load_generator(true,false);
        else if (act_name == 'g6') load_graph('g6', true,false);
        else if (act_name == 'adj') load_graph('adj', true,false);
        else if (act_name == 'el') load_graph('el', true,false);
    } else if (action == "Load Only") {
        if (act_name == 'gen') load_generator(false,false);
        else if (act_name == 'g6') load_graph('g6', false,false);
        else if (act_name == 'adj') load_graph('adj', false, false);
        else if (act_name == 'el') load_graph('el', false, false);
    } else if (action == "Fit Canvas") {
        cy.fit();
    } else if (action == "Clear Canvas") {
        clearCanvas();
    } else if (action == "Canvas to Image") {
        var jpg64 = cy.jpg();
        window.open(jpg64);
    } else if (action == "WebGL") {
        if (act_name == 'gen') load_generator(true,true);
        else if (act_name == 'g6') load_graph('g6', true,true);
        else if (act_name == 'adj') load_graph('adj', true,true);
        else if (act_name == 'el') load_graph('el', true,true);
    }
}

function do_saves() {
    var output = $('#output').find('option:selected').text();
    if(output == "G6 Format") {
        server(serverAddr + 'save/g6--'+uuid,function (data) {
            $("#outputResults").html(data.results);
        });
    } else if(output == "Adjacency List") {

    } else if(output == "Adjacency Matrix") {

    } else if(output == "GraphTea Format") {
        window.open(serverAddr + 'tea/currentGraph--'+uuid);
        // server(serverAddr + 'tea/currentGraph--'+uuid,function (data) {
        // });
    } else if(output == "LaTeX") {
        window.open(serverAddr + 'tex/currentGraph--'+uuid);
    }
}