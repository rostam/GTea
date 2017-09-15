var serverAddr = "http://localhost:2342/";
var nodeId = 0;
var edgeId = -1;
var cy; //cytoscape object
var selectedNode;
var uuid = guid();
var directed = 'triangle', undirected = 'none';
var parallels = [];

initCytoscape(directed);

var original_data = {};
$.get(serverAddr + 'graphs/')
    .done(function (data) {
        original_data = data;
        var categoriesSelect = $('#categories');
        data.graphs.forEach(function (d) {
            categoriesSelect.append('<option>' + d.name + '</option>');
            original_data[d.name] = d.properties;
        });
        categoriesSelect.on('change', function () {
            var category = getSelectedCategory();
            var keys = "", vals = "";
            original_data[category].forEach(function (d) {
                var propNamesTypes = d.split(":");
                keys += propNamesTypes[0] + ", ";
                vals += propNamesTypes[1] + ", ";
            });
            keys = keys.substr(0, keys.length - 2);
            vals = vals.substr(0, vals.length - 2);
            $('#props_keys').html(keys);
            $('#props_vals').val(vals);
        });

        var reportsSelect = $('#reports');
        data.reports.forEach(function (d) {
            reportsSelect.append('<option>' + d.name + '</option>');
            original_data[d.name] = d.properties;
        });
        reportsSelect.on('change', function () {
            var report = getSelectedReport();
            var props = $('#reportProps');
            props.empty();
            var keys="",vals="";
            original_data[report].forEach(function (d) {
                var propNamesTypes = d.split(":");
                keys+=propNamesTypes[0]+", ";vals+=propNamesTypes[1]+", ";
            });
            keys=keys.substr(0,keys.length-2);
            vals=vals.substr(0,vals.length-2);
            if(keys=="") props.append("No parameters<br/>");
            else props.append('<span id="reportPropsKeys">' +keys + '</span>: ' +
                '<input id="reportPropsVals"' + 'name="' + keys + '"' + ' value="' + vals + '">');
        });
    })
    .fail(function (jqXHR, textStatus, errorThrown) {
        alert(errorThrown);
    });

function Report() {
    var reportProps = "";
    $('#reportProps').children('input').each(function (i, item) {
        reportProps += item.name + ":" + item.value + "-"
    });
    if (reportProps == "") {
        reportProps = "no";
    }
    $.get(serverAddr + 'report/'
        + $('#categories').find('option:selected').text() + "--"
        + $('#reports').find('option:selected').text() + "--"
        + ($('#props_keys').html() + ":" + $('#props_vals').val()) + "--"
        + ($('#reportPropsKeys').html() + ":" + $('#reportPropsVals').val())
        + "--" + uuid)
        .done(function (data) {
            $('#reportResults').html(JSON.stringify(data));
//                $('#results-body').html(JSON.stringify(data));
            if (data.titles != undefined) {
                var titles = data.titles.substr(1, data.titles.indexOf("]") - 1);
                var tts = titles.split(",");
                var builtHTML = "<table><tr>";
                tts.forEach(function (t) {
                    builtHTML += "<th>" + t + "</th>";
                });
                var results = JSON.parse(data.results);
                builtHTML += "</tr>";
                results.forEach(function (row) {
                    builtHTML += "<tr>";
                    row.forEach(function (col) {
                        builtHTML += "<td>" + col + "</td>";
                    })
                    builtHTML += "</tr>";
                });
                builtHTML += "</tr></table>";
                $('#results-body').html(builtHTML);
            }

        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        });
}


function initCytoscape(arrow) {
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
                    'background-color': 'blue',
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
 * Sends a request to the backend to delete all vertices
 * and edges from the current graph, then reloads the
 * graph
 */
function clearCanvas() {
    $.get(serverAddr + 'clear/'
        + uuid)
        .done(function(data) {
            var edges = data.edges;
            var nodes = data.nodes;
            cy.elements().remove();
            cy.add(nodes);
            cy.add(edges);
            //nodeId = 0;
        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        });
}

function setVertexIds() {
    /*
    This function is for resetting the labels of the vertices, since we
    use the labels as ids to send to the backend, and the backend flattens the
    ids of the vertices when one of them is removed.
     */
    var lowestId = 0;
    var len = cy.nodes().length;
    for (var i = 0; i < nodeId; i++) {
        if (cy.$('#' + i).length > 0) {
            cy.$('#' + i).data('label', lowestId);
            lowestId++;
        }
    }

}

function addSingleVertex() {
    var offset = $('#canvas').offset();
    var xPos = event.pageX - offset.left;
    var yPos = event.pageY - offset.top;

    $.get(serverAddr + 'addVertex/'
        + nodeId + "--" + xPos + "--" + yPos
        + "--" + uuid)
        .done(function (data) {

            cy.add({
                data: {id: nodeId, label: nodeId},
                renderedPosition: {x: xPos, y: yPos}
            });

            nodeId++;
            setVertexIds();

        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        });

}

function removeSingleVertex(node) {
    $.get(serverAddr + 'remove/'
        + node.data('label')
        + "--" + uuid)
        .done(function (data) {

            cy.remove(node);
            //nodeId--;
            setVertexIds();
            applyLayout(); // TODO: Don't apply layout if no vertices left (apply conditional)

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
            cy.elements().remove();

            cy.add(nodes);
            cy.add(edges);

            //console.log(nodes);
            applyLayout();

        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        });
}

function removeSingleEdge(edge) {
    console.log("drawing single vertex");
    //console.log(edge);

    var sourceID = edge.source().id();
    var targetID = edge.target().id();

    $.get(serverAddr + 'removeEdge/'
        + sourceID + "--"
        + targetID + "--"
        + "none" + "--"
        + uuid
    ).done(function (data) {
        console.log("Removed edge");
        cy.remove(edge);
    }).fail(function (jqXHR, textStatus, errorThrown) {
        alert(errorThrown);
    });
}

/**
 * Updates the graph type to either directed or undirected
 * */
var test = 0;
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
            if(data == null){
                // The type was not different, so ignore.
                console.log("type was not changed");
                return;
            }

            var nodes = data.nodes;
            var edges = data.edges;

            smoothParallelEdges(edges);

            cy.elements().remove();
            cy.add(nodes);
            cy.add(edges);
            applyLayout();

        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        });


}

function Draw() {
    var lay = $('#layouts').find('option:selected').text();
    var type = $('#graphType').find('option:selected').text();
    if (lay == "Botanical Tree") {
        drawBotanical();
        return;
    }
    $.get(serverAddr + 'draw/'
        + $('#categories').find('option:selected').text() + "--"
        + $('#reports').find('option:selected').text() + "--" +
        ($('#props_keys').html() + ":" + $('#props_vals').val())
        + "--" + type
        + "--" + uuid)
        .done(function (data) {
            nodeId = 0; //resets counter for freehand vertices
            var nodes = data.nodes;
            var edges = data.edges;
            cy.elements().remove();
            cy.add(nodes);
            cy.add(edges);
            nodeId += nodes.length; //adds the current amount of nodes, so the next freehand item will be max(ids)+1
            setVertexIds();
            applyLayout();
        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        });
}

cy.on('tap', function(event) {
    var evtTarget = event.target;

    if (evtTarget === cy) {
        addSingleVertex(event);
    }
    else if (evtTarget.isNode()) {
        if (selectedNode == null) {
            selectedNode = evtTarget;

            /*var label = selectedNode.data('label');
            var id = selectedNode.data('id');
            console.log("label: ", label);
            console.log("id: ", id);*/

            cy.$('#'+selectedNode.data('id')).classes('selected');
            //cy.$('#'+selectedNode.data('label')).classes('selected');
        }
        else {
            console.log(selectedNode.data('label'), evtTarget.data('label'));
            addSingleEdge(selectedNode.data('label'), evtTarget.data('label'));
            cy.$('#'+selectedNode.data('label')).classes('node');
            selectedNode = null;
        }
        console.log("Clicked a node");
    }
});

cy.on('cxttapend', 'node', function(event) {
    var evtTarget = event.target;
    if(evtTarget.isNode){
        removeSingleVertex(evtTarget);
    }
});

cy.on('cxttapend', 'edge', function(event) {
    var evtTarget = event.target;
    if(evtTarget.isEdge){
        console.log("clicked edge!");
        removeSingleEdge(evtTarget);
    }
});

cy.on('layoutstop', function() {
    cy.maxZoom(2.5);
    cy.fit();
    cy.maxZoom(100);
});

function getSelectedCategory() {
    return $('#categories').find('option:selected').text();
}

function getSelectedReport() {
    return $('#reports').find('option:selected').text();
}

/**
 * runs a BFS on graph, starting the given vertex as the root
 */
function BFSrun(treeRoot, f) {
    var q = [], ret = [];
    q.push(treeRoot);
    ret.push(treeRoot);
    treeRoot.setMark = true;
    treeRoot.obj = 0;
    while (q.length != 0) {
        var v = q.splice(0, 1)[0];
        v.neighborhood().forEach(function (vertex) {
            if (vertex.setMark == undefined || vertex.setMark == false) {
                vertex.setMark = true;
                vertex.obj = v.obj + 1;
                q.push(vertex);
                ret.push(vertex);
                f(vertex, v);
            }
        });
    }
    return ret;
}

function getAngle(p1, p2) {
    var angle = Math.atan2(p1.y - p2.y,
        p1.x - p2.x);
    if (angle < 0) {
        // atan2 returns getAngle in phase -pi to pi, which means
        // we have to convert the answer into 0 to 2pi range.
        angle += 2 * Math.PI;
    }
    return angle;
}

function getMiddlePoint(p1, p2) {
    return {x: (p1.x + p2.x) / 2, y: (p1.y + p2.y) / 2};
}

function sizeOfIntersectionOfArrays(arr1, arr2) {
    var cnt = 0;
    if (arr1.indexOf(arr2[0]) == -1) cnt++;
    if (arr1.indexOf(arr2[1]) == -1) cnt++;
    if (arr1.indexOf(arr2[2]) == -1) cnt++;
    return cnt;
}

$('#generators').show();
$('#g6format').hide();
function selectLoader() {
    var loader = $('#loaders ').find('option:selected').text();
    switch (loader) {
        case "Generators":
            $('#generators').show();
            $('#g6format').hide();
            break;
        case "Edge list":
        case "Adjacency matrix":
        case "G6 format":
            $('#generators').hide();
            $('#g6format').show();
            break;
    }
}

function loadG6() {
    $.get(serverAddr + 'g6/'+$('#g6string').val())
        .done(function (data) {

        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        });
}


function guid() {
  function s4() {
    return Math.floor((1 + Math.random()) * 0x10000)
      .toString(16)
      .substring(1);
  }
  return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
    s4() + '-' + s4() + s4() + s4();
}

 var modifiedPositions = cy.nodes().forEach(function(n){
    if(n.data )
    var x = n.data("x");
    var y = n.data("y");
 });

var preset = {
  name: 'preset',

  positions: undefined, // map of (node id) => (position obj); or function(node){ return somPos; }
  zoom: undefined, // the zoom level to set (prob want fit = false if set)
  pan: undefined, // the pan level to set (prob want fit = false if set)
  fit: true, // whether to fit to viewport
  padding: 30, // padding on fit
  animate: false, // whether to transition the node positions
  animationDuration: 500, // duration of animation in ms if enabled
  animationEasing: undefined, // easing of animation if enabled
  animateFilter: function ( node, i ){ return true; }, // a function that determines whether the node should be animated.  All nodes animated by default on animate enabled.  Non-animated nodes are positioned immediately when the layout starts
  ready: undefined, // callback on layoutready
  stop: undefined, // callback on layoutstop
  transform: function (node, position ){ return position; } // transform a given node position. Useful for changing flow direction in discrete layouts
};

function applyLayout(){
    var lay = $('#layouts').find('option:selected').text();
    if (lay == "Preset") {
        //cy.layout({name: 'preset'}).run();
        cy.layout(preset).run();
    } else if (lay == "Force Directed") {
        cy.layout({name: 'cose'}).run();
    }
}

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

function smoothParallelEdges(edges){
    parallels = [];

    var _root = cy.$('#0');
    BFSrun(_root, findParallels);

    if(parallels.length > 0) {

        var str = "", sArr = [];
        var len = parallels.length
        for (var i = len; i > 0; i--) {
            var edge = parallels.pop();
            sArr[i] = edge + "~~";
        }
        str = sArr.join("");
        console.log(str);

        $.get(serverAddr + 'condenseParallelEdges/'
            + str + "--"
            + uuid
        ).done(function (_data) {
            // TODO: Fix so JSON is returned from handler
            var data = JSON.parse(_data)

            var nodes = data.nodes;
            var curEdges = data.edges;

            // TODO: Below is also run when this function returns, it would be best to only be called once.
            cy.elements().remove();
            cy.add(nodes);
            cy.add(curEdges);
            applyLayout();
            //

            return curEdges;
        }).fail(function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        });
    }

}







