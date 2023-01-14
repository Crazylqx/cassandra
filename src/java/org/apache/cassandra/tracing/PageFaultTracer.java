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

package org.apache.cassandra.tracing;

import org.slf4j.Logger;
import org.apache.cassandra.utils.NativeLibrary;

// Not thread safe
public class PageFaultTracer {
    private String name;
    private Stat lastStartStat;
    private Stat stat;

    private static class Stat {
        public long time;
        public long pfCount;
        public int triggerCount;

        private Stat(long time, long pfCount) {
            this.time = time;
            this.pfCount = pfCount;
            this.triggerCount = 0;
        }

        public Stat() {
            this(0, 0);
        }

        public static Stat getCurrentStat() {
            long time = System.currentTimeMillis();
            long pfCount = NativeLibrary.getThreadMajorPageFaultCount();
            return new Stat(time, pfCount);
        }

        public void update(Stat start, Stat end) {
            time += end.time - start.time;
            pfCount += end.pfCount - start.pfCount;
            triggerCount += 1;
        }

        public String toString() {
            return String.format("%d ms, %d major faults, in %d times", time, pfCount, triggerCount);
        }
    }

    public PageFaultTracer(String name) {
        this.name = name;
        this.stat = new Stat();
    }

    public void logStats(Logger logger) {
        logger.info("[{}]: {}", name, stat.toString());
    }

    public void start() {
        lastStartStat = Stat.getCurrentStat();
    }

    public void end() {
        Stat endStat = Stat.getCurrentStat();
        stat.update(lastStartStat, endStat);
    }
}
