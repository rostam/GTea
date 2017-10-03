function do_actions() {
    var action = $('#actions').find('option:selected').text();
    if(action == "Load and Draw") {
        load_generator(true);
    } else if(action == "Load Only") {
        load_generator(false);
    } else if(action == "Fit Canvas") {
        cy.fit();
    } else if(action == "Clear Canvas") {
        clearCanvas();
    } else if(action == "Canvas to Image") {
        var jpg64 = cy.jpg();
        window.open(jpg64);
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