var serverAddr;

/**
 * Finds if an edge is parallel */
function findParallels(element, v){

    if(element.isEdge()) {
        var trg = cy.$("#" + element.data().target);

        if(v.edgesWith(trg).length >= 2){

            var pEdge = cy.$("#" + trg.id()).edgesTo(v).id();
            var iEdge = element.data().id;

            if(parallels.includes(iEdge) || parallels.includes(pEdge)){
                return;
            }
            parallels.push(iEdge);

        }
    }
}

function initCytoscape(arrow, _serverAddr) {
    serverAddr = _serverAddr;

    cy = cytoscape({
        container: document.getElementById('canvas'),
        style: [ // the stylesheet for the graph
            {
                selector: 'node',
                style: {
                    'background-color': 'lightgray',
                    'label': 'data(label)',
                    'text-valign': 'center'
                }
            },
            {
                selector: '.selected',
                style: {
                    'background-color': 'rgb(0,0,255,0.5)',
                    'label': 'data(label)',
                    'text-valign': 'center'
                }
            },
            {
                selector: 'edge',
                style: {
                    'curve-style': 'bezier',
                    'target-arrow-shape': arrow
                }
            }]
    });
}


/*
 * Generates a random string used for client identification.
 */
function guid() {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    }
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
        s4() + '-' + s4() + s4() + s4();
}

/*
 * This function is for resetting the labels of the vertices, since we
 * use the labels as ids to send to the backend, and the backend flattens the
 * ids of the vertices when one of them is removed.
 */
function setVertexIds() {
    var lowestId = 0;
    var len = cy.nodes().length;
    for (var i = 0; i < nodeId; i++) {
        if (cy.$('#' + i).length > 0) {
            cy.$('#' + i).data('label', lowestId);
            lowestId++;
        }
    }

}

function addSingleVertex(event) {
    $.get(serverAddr + 'addVertex/'
        + nodeId + "--" + event.position.x + "--" + event.position.y
        + "--" + uuid)
        .done(function (data) {

            cy.add({
                data: {id: nodeId, label: nodeId},
                position: event.position,
                renderedPosition: event.position
            });

            nodeId++;
            setVertexIds();
        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        });
}

function removeSingleVertex(node) {
    if(node === selectedNode) {
        cy.$('#'+selectedNode.data('id')).classes('node');
        selectedNode = null;
    }

    $.get(serverAddr + 'remove/'
        + node.data('label')
        + "--" + uuid)
        .done(function (data) {

            cy.remove(node);
            //nodeId--;
            setVertexIds();
            applyLayout();

        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        });
}


function addSingleEdge(source, target) {
    $.get(serverAddr + 'addEdge/'
        + source + "--" + target
        + "--" + uuid)
        .done(function (data) {
            var edges = data.edges;
            var nodes = data.nodes;

            reload(nodes, edges);

        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        });
}

/**
 * */
function removeSingleEdge(edge) {
    var sourceID = edge.source().id();
    var targetID = edge.target().id();

    $.get(serverAddr + 'removeEdge/'
        + sourceID + "--"
        + targetID + "--"
        + "none" + "--"
        + uuid
    ).done(function (data) {
        cy.remove(edge);
    }).fail(function (jqXHR, textStatus, errorThrown) {
        alert(errorThrown);
    });
}


function applyLayout(){
    var lay = $('#layouts').find('option:selected').text();
    if (lay == "Preset") {
        cy.layout({name: 'preset'}).run();
    } else if (lay == "Force Directed") {
        cy.layout({name: 'cose'}).run();
    }
}


/**
 * Updates the graph type to either directed or undirected
 * */
function selectType() {

    var type = $('#graphType').find('option:selected').text();

    if (type === 'directed') {
        cy.style()
            .selector('edge')
            .css({
                'curve-style': 'bezier',
                'target-arrow-shape': directed
            })
    } else if (type === "undirected") {
        cy.style()
            .selector('edge')
            .css({
                'target-arrow-shape': undirected
            })
    }

    $.get(serverAddr + 'selectType/'
        + type
        + "--" + uuid)
        .done(function (data) {
            if (data == null) {
                // The type was not different, so ignore.
                return;
            }

            var nodes = data.nodes;
            var edges = data.edges;

            smoothParallelEdges(edges);

            reload(nodes, edges);


        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        });


}

/**
 * Finds all edges that are parallel and 'smooths' them down into one edge.
 * */
function smoothParallelEdges(){
    parallels = [];

    var _root = cy.$('#0');
    var rt = BFSrun(_root, findParallels);

    // Runs BFS on all components, this could be refactored to run quicker.
    for(var j =0; j < cy.elements().length-1; j++) {
        if (rt[j] != null) {
            if (rt[j].id() !== cy.$('#' + j).id()) {
                BFSrun(cy.$('#' + j), findParallels);
            }
        } else {
            BFSrun(cy.$('#' + j), findParallels);
        }
    }


    if(parallels.length > 0) {

        var str = "", sArr = [];
        var len = parallels.length
        for (var i = len; i > 0; i--) {
            var edge = parallels.pop();
            sArr[i] = edge + "~~";
        }
        str = sArr.join("");

        $.get(serverAddr + 'condenseParallelEdges/'
            + str + "--"
            + uuid
        ).done(function (_data) {
            // TODO: Fix so JSON is returned from handler
            var data = JSON.parse(_data)

            var edges = data.edges;
            cy.edges().remove();
            cy.add(edges);

        }).fail(function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        });
    }
}

/**
 * Reloads the nodes and edges in cytoscape
 * */
var reload = function(nodes, edges){

    if(nodes == null || edges == null){
        return;
    }

    cy.batch(function(){
        cy.elements().remove();
        cy.add(nodes);
        cy.add(edges);
        applyLayout();
    });
    setTimeout(reload, 1000);
};

