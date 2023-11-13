/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cassandra.tools.nodetool;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;

import java.util.ArrayList;
import java.util.List;

import org.apache.cassandra.tools.NodeProbe;
import org.apache.cassandra.tools.NodeTool.NodeToolCmd;

@Command(name = "scanmemtable", description = "meaningless scan memtable")
public class ScanMemtable extends NodeToolCmd {
    @Arguments(usage = "[<keyspace> <table>]", description = "")
    private List<String> args = new ArrayList<>();

    @Override
    public void execute(NodeProbe probe) {
        System.out.print("args: ");
        for (String arg : args) {
            System.out.printf("%s ", arg);
        }
        System.out.println();

        if (args.size() != 2) {
            throw new RuntimeException("Invalid argument count");
        }
        String keyspace = args.get(0);
        String[] tableNames = new String[] { args.get(1) };

        try {
            long startTime = System.currentTimeMillis();
            long sum = probe.getStorageService().sumAddr(keyspace, tableNames);
            long endTime = System.currentTimeMillis();
            System.out.printf("addr sum = %d, time = %d ms\n", sum, endTime - startTime);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during flushing", e);
        }

    }
}