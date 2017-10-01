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