var renderer, layout, prev_node_id = undefined, prev_node_color, prev_node_size;
function viva_action(data) {
    var lay = $('#layouts').find('option:selected').text();

    useDefaultLabel = true;
    useForceLayout = true;
    $("#infos").empty();
// update vertex and edge count after sampling
//     Object.keys(data.extra).forEach(function (t) {
//         $("#infos").append("<tr><td>" + t + "</td><td>" + data.extra[t] + "</td>");
//     });
    var nodeColor = 0x009ee8, // hex rrggbb
        nodeSize = 20;
    var graph = Viva.Graph.graph();
    var uniqueVertexIds = {};

    if (lay == "Preset" && data.nodes[0].position.x != undefined) {
        data.nodes.forEach(function (t) {
            t.x = t.position.x;
            t.y = t.position.y;
            graph.addNode(t.data.id, t);
        });
    } else {
        data.nodes.forEach(function (t) {
            graph.addNode(t.data.id, t);
        });
    }
    data.edges.forEach(function (t) {
        graph.addLink(t.data.source, t.data.target);
    });
    var graphics = Viva.Graph.View.webglGraphics();
// first, tell webgl graphics we want to use custom shader
// to render nodes:
    var circleNode = buildCircleNodeShader();
    graphics.setNodeProgram(circleNode);

// second, change the node ui model, which can be understood
// by the custom shader:
    graphics.node(function (node) {
        if (node.data.data.properties.color != undefined) {
            var color = node.data.data.properties.color.replace('#', '0x');
            return new WebglCircle(nodeSize, color);
        } else
            return new WebglCircle(nodeSize, nodeColor);
    });

    if (lay == "Preset" && data.nodes[0].position.x != undefined) {
        layout = Viva.Graph.Layout.constant(graph);
        renderer = Viva.Graph.View.renderer(graph, {
            container: document.getElementById('canvas'),
            graphics: graphics,
            layout: layout
        });
    } else {
        // let's create layout first, but not render anything
        layout = Viva.Graph.Layout.forceDirected(graph, {
            springLength: 80,
                springCoeff: 0.0002,
        });
        renderer = Viva.Graph.View.renderer(graph, {
            container: document.getElementById('canvas'),
            graphics: graphics,
            layout : layout
        });
    }

    var events = Viva.Graph.webglInputEvents(graphics, graph);
    events.mouseEnter(function (node) {
        // console.log(node.data);
    }).mouseLeave(function (node) {
        // console.log('Mouse left node: ' + node.id);
    }).dblClick(function (node) {
        // console.log('Double click on node: ' + node.id);
    }).click(function (node) {
        // console.log('Single click on node: ' + node.id);
        document.getElementById('view_data').innerHTML = json_to_table(node.data.data);
        if(prev_node_id != undefined) {
            var nodeUI = graphics.getNodeUI(prev_node_id);
            nodeUI.color = prev_node_color;
            nodeUI.size = prev_node_size;
        }
        graph.beginUpdate();
        var nodeUI = graphics.getNodeUI(node.id);
        prev_node_id = node.id;
        prev_node_color = nodeUI.color;
        prev_node_size = nodeUI.size;
        nodeUI.color = 0xFFFF00;
        nodeUI.size = prev_node_size;
        graph.endUpdate();
        // renderer.run();
    });

    if (lay == "Preset" && data.nodes[0].position.x != undefined) {
        layout.placeNode(function (node) {
            // node.id - points to its position but you can do your
            // random logic here. E.g. read from specific node.data
            // attributes. This callback is expected to return object {x : .. , y : .. }
            return {x: node.data.x, y: node.data.y};
        });
    }

    renderer.run();
    if (lay == "Preset" && data.nodes[0].position.x != undefined) {
        renderer.moveTo(data.nodes[0].position.x, data.nodes[0].position.y);
    }
}

function json_to_table(json) {
    // var table = "" +
    //     "<table style='-ms-word-wrap: break-word;word-wrap: break-word;" +
    //     "margin: 15px;font-size: 12px;border: 1px solid;" +
    //     "table-layout: fixed;width: 80%;' cellpadding='4px'>" +
    //     "<tr><th>Key</th><th>Value</th></tr>";
    //
    // Object.keys(json.properties).forEach(function (key) {
    //     table+="<tr><td>" + key + "</td><td>"+ json.properties[key] + "</td></tr>"
    // });
    // table+="</table>";
    var table = "<div id='prop-table' style='margin: 15px;font-size: 12px;'>";
    Object.keys(json.properties).forEach(function (key) {
        table+="<span style='font-weight: bold;'>" + key +"</span><br/>";
        table+="<textarea cols='10' rows='1' style='font-size: 12px;height:20px;width: 100px;bottom: 5px;margin-bottom: 2px;'>"+json.properties[key] +"</textarea><br/>";
    });
    table+="</div>";
    return table;
}


function Pause() {
    renderer.pause();
    var graphRect = layout.getGraphRect();
    var graphSize = Math.min(graphRect.x2 - graphRect.x1, graphRect.y2 - graphRect.y1);
    var screenSize = Math.min(document.getElementById("canvas").clientWidth,
        document.getElementById("canvas").clientHeight);
    var desiredScale = screenSize / graphSize;
    console.log(desiredScale + " " + screenSize + " " + graphSize);
    zoomOut(desiredScale, 1);
}

function zoomOut(desiredScale, currentScale) {
    // zoom API in vivagraph 0.5.x is silly. There is no way to pass transform
    // directly. Maybe it will be fixed in future, for now this is the best I could do:
    if (desiredScale < currentScale) {
        currentScale = renderer.zoomOut();
        setTimeout(function () {
            zoomOut(desiredScale, currentScale);
        }, 16);
    }
}