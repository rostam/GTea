var full_strings = {
    intro : "The graph can be loaded in different ways. It can be generated from an available" +
    "            list of generators. Also, it can be loaded from a given edge list, adjacency matrix," +
    "            or G6 format.",
    generator : "Here, a graph generator can be selected. The required parameters and their types appeared next to it." +
    "                All parameters should be filled separated with commas.",
    g6format  : "Here, you can paste your G6-formatted string to load that graph.",
    layout : "You can draw the graph in a layout which can be selected here. The preset layout " +
    "here means the computed layout in GraphTea.",
    report: "Now, you can compute the different reports on graph. When a report selected from" +
    "        the list, the parameters, if any, appears near to it. " +
    "        After the computation, the results would appear as strings at the botton. " +
    "        A styled version can be also generated.",
    adjMat: "The adjacency matrix should be written in the following format." +
    "        The elements in a row are separated with a comma." +
    "        Each row comes in a new line. ",
    save: "Here, you can save the current graph to different format. The outputs" +
    " that are textual are shown on the text area.",
    graphActions: "Here, you can apply a graph operator to the graph. The results will be shown as a new graph"
};
var strings = {
    intro : "Select how to load a graph:",
    generator : "Select a graph generator: ",
    g6format  : "Enter the G6 format string:",
    layout : "Load (and draw) the graph:",
    report: "Compute a report on graph:",
    adjMat: "The adjacency matrix:",
    save: "Save the graph:",
    graphActions: "Apply an operator on the graph:",
    graphAlgorithms: "Interactively running an algorithm on the graph:",
    freehand: "Freehand graph drawing by mouse."
};

$('#strings-intro').html(strings.intro);
$('#strings-generator').html(strings.generator);
$('#strings-g6format').html(strings.g6format);
$('#strings-freehand').html(strings.freehand);
$("#strings-layout").html(strings.layout);
$("#strings-report").html(strings.report);
$("#strings-adjMatformat").html(strings.adjMat);
$("#strings-save").html(strings.save);
$("#strings-graph-actions").html(strings.graphActions);
$("#strings-graph-algorithms").html(strings.graphAlgorithms);
$("#strings-elformat").html(strings.elist);
//
// strings.intro+= "<div class=\"tooltip\"><i class=\"fa fa-info-circle\" aria-hidden=\"true\"></i>\n" +
// "        <span id=\"loaders_tooltip\" class=\"tooltiptext\">A brief description</span></div>";
// $("#loaders_tooltip").html(full_strings.intro);

$("#strings-adjMatformat").qtip({
    content: {
        text: full_strings.adjMat,
        title: "Adjacency Matrix",
    },
    style: {
        classes: 'qtip-light'
    }
});

$("#strings-g6format").qtip({
    content: {
        text: full_strings.g6format,
        title: "Graph G6 Format",
    },
    style: {
        classes: 'qtip-light'
    }
});


$('#strings-intro').qtip({
    content: {
        text: full_strings.intro,
        title: "Load a graph",
    },
    style: {
        classes: 'qtip-light'
    }
});

$('#strings-generator').qtip({
    content: {
        text: full_strings.generator,
        title: "Select a generator"
    },
    style: {
        classes: 'qtip-light'
    }
});

$('#strings-layout').qtip({
    content: {
        text: full_strings.layout,
        title: "Draw a graph"
    },
    style: {
        classes: 'qtip-light'
    }
});

$('#strings-report').qtip({
    content: {
        text: full_strings.report,
        title: "Report on a graph"
    },
    style: {
        classes: 'qtip-light'
    }
});

$('#strings-save').qtip({
    content: {
        text: full_strings.save,
        title: "Save the graph"
    },
    style: {
        classes: 'qtip-light'
    }
});

$('#strings-graph-actions').qtip({
       content: {
           text: full_strings.graphActions,
           title: "Apply an operator on the graph"
       },
       style: {
           classes: 'qtip-light'
       }
   });
