function startLoaderIcon() {
    document.getElementById("loading-go").style.visibility = 'visible';
}

function endLoaderIcon() {
    document.getElementById("loading-go").style.visibility = 'hidden';
}

function do_actions() {
    var action = $('#actions').find('option:selected').text();
    var act_name = $('#act_name').html();
    $('#parent_canvas').empty();
    $('#parent_canvas').append('<div id="canvas" class="main"></div>');
    startLoaderIcon();
    if (action == "Load and Draw") {
        if (act_name == 'gen') load_generator(true, false, endLoaderIcon);
        else if(act_name == 'free') load_free(endLoaderIcon);
        else load_graph(act_name, true, false, endLoaderIcon);
    } else if (action == "Load Only") {
        if (act_name == 'gen') load_generator(false, false, endLoaderIcon);
        else if(act_name == 'free') load_free(endLoaderIcon);
        else load_graph(act_name, false, false, endLoaderIcon);
    } else if (action == "Fit Canvas") {
        cy.fit();
    } else if (action == "Clear Canvas") {
        clearCanvas();
    } else if (action == "Canvas to Image") {
        var jpg64 = cy.jpg();
        window.open(jpg64);
    } else if (action == "WebGL") {
        if (act_name == 'gen') load_generator(true, true, endLoaderIcon);
        else if(act_name == 'free') load_free(endLoaderIcon)
        else load_graph(act_name, true, true, endLoaderIcon);
    }

    // trigger the graph creation event on the document node
    var event = new CustomEvent('newGraph', { detail: action });
    document.dispatchEvent(event);
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
