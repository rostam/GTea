// GraphTea Project: http://github.com/graphtheorysoftware/GraphTea
// Copyright (C) 2012 Graph Theory Software Foundation: http://GraphTheorySoftware.com
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/
// GraphTea Project: http://github.com/graphtheorysoftware/GraphTea
// Copyright (C) 2012 Graph Theory Software Foundation: http://GraphTheorySoftware.com
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/

package graphtea.extensions.io;

import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.plugins.main.saveload.core.GraphIOException;
import graphtea.plugins.main.saveload.core.extension.GraphWriterExtension;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SaveEdgeList implements GraphWriterExtension {

    public String getName() {
        return "Edge list";
    }

    public String getExtension() {
        return "el";
    }

    @Override
    public void write(File file, GraphModel graph) throws GraphIOException {
        try {
            FileWriter fw = new FileWriter(file,true);
            for(Edge e : graph.edges()) {
                fw.write(e.source.getId() + "\t" + e.target.getId() + "\n");
            }
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDescription() {
        return "Graph6 File Format";
    }


}