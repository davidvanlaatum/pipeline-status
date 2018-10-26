package org.jenkinsci.plugins.pipelinestatus

import com.cloudbees.groovy.cps.NonCPS
import org.jenkinsci.plugins.workflow.cps.CpsScript
import java.util.logging.Level
import java.util.logging.Logger

class StatusVars implements Serializable {
    private CpsScript script
    private static Logger LOG = Logger.getLogger(StatusVars.getClass().getName());

    public StatusVars(CpsScript script) {
        this.script = script
    }

    @NonCPS
    public Object get(String name) {
        PipelineStatusAction status = PipelineStatusAction.getPipelineStatusAction(script.$build(), false);
        if (status != null)
            return status.get(name, null, null);
        else
            return null;
    }

    public void set(String name, Object value) {
        set(name, value, DataType.OBJECT);
    }

    public void set(String name, Object value, String type) {
        set(name, value, DataType.valueOf(type));
    }

    public void set(String name, Object value, DataType type) {
        PipelineStatusAction.getPipelineStatusAction(script.$build(), true).set(name, value, type, null, null);
    }

    public void inc(String name) {
        inc(name, 1);
    }

    public void inc(String name, int value) {
        PipelineStatusAction.getPipelineStatusAction(script.$build(), true).get(name, null, null).incValue(value);
    }

    public void dec(String name) {
        dec(name, 1);
    }

    public void dec(String name, int value) {
        PipelineStatusAction.getPipelineStatusAction(script.$build(), true).get(name, null, null).decValue(value);
    }

    @NonCPS
    public void append(String name, Object value) {
        PipelineStatusAction status = PipelineStatusAction.getPipelineStatusAction(script.$build(), true);
        def var = status.get(name, null, null);
        if (var == null) {
            status.set(name, value, DataType.LIST, null, null);
        } else if (var.isList()) {
            var.append(value);
        } else {
            var.setType(DataType.LIST);
            var.setValue(new ArrayList<>(Collections.singletonList(value)));
        }
    }

    public Table getTable(String name) {
        if (PipelineStatusAction.getPipelineStatusAction(script.$build(), false) != null)
            return new Table(name, script);
        else
            return null;
    }

    public Table createTable(String name, List<Map<String, String>> columns) {
        PipelineStatusAction.getPipelineStatusAction(script.$build(), true).createTable(name, columns);
        return new Table(name, script);
    }

    static public class Table implements Serializable {
        private CpsScript script
        private String name;

        Table(String name, CpsScript script) {
            this.name = name;
            this.script = script;
        }

        public void set(String key, Integer index, Object value) {
            PipelineStatusAction.getPipelineStatusAction(script.$build(), true).set(key, value, null, name, index);
        }

        public Object get(String key, Integer index) {
            return PipelineStatusAction.getPipelineStatusAction(script.$build(), true).get(key, name, index);
        }

        public void append(String name, Integer index, Object value) {
            def var = get(name, index);
            if (var.isList()) {
                var.append(value);
            } else {
                var.setType(DataType.LIST);
                var.setValue(new ArrayList<>(Collections.singletonList(value)));
            }
        }

        public <V> V time(String key, Integer index, Closure<V> body) {
            def start = System.currentTimeMillis();
            try {
                return body();
            } finally {
                def end = System.currentTimeMillis();
                set(key, index, end - start);
            }
        }

        public void inc(String name, int index) {
            inc(name, index, 1);
        }

        public void inc(String key, int index, int value) {
            PipelineStatusAction.getPipelineStatusAction(script.$build(), true).get(key, name, index).incValue(value);
        }

        public void dec(String name, int index) {
            dec(name, index, 1);
        }

        public void dec(String key, int index, int value) {
            PipelineStatusAction.getPipelineStatusAction(script.$build(), true).get(key, name, index).decValue(value);
        }

        @NonCPS
        public Double getave(String key, int index, int builds = 30) {
            def values = [];
            def build = script.$build().previousCompletedBuild
            while (build && builds > 0) {
                PipelineStatusAction status = PipelineStatusAction.getPipelineStatusAction(build, false)
                builds--;
                if (status) {
                    try {
                        def val = status.get(key, name, index)
                        if (val.getValue() instanceof Number) {
                            values.push(val.getValue());
                        }
                    } catch (Exception e) {
                    }
                }
                build = build.previousCompletedBuild
            }

            Double rt = null;
            if (!values.empty) {
                Double sum = 0;
                values.each {
                    sum += it
                }
                rt = sum / values.size()
            }
            return rt;
        }

        @NonCPS
        public Map<String, Double> calcAverages(int valueIndex, int aveIndex, int builds = 30) {
            Map<String, Double> rt = [:]
            PipelineStatusAction status = PipelineStatusAction.getPipelineStatusAction(script.$build(), true);
            def table = status.getTable(name)
            table.rows.each { row ->
                try {
                    rt[row.key] = getave(row.key, valueIndex, builds)
                    def v = row.getColumn(aveIndex)
                    if (!v) {
                        v = new DataValue()
                        v.setType(DataType.INTERVAL)
                        row.setColumn(aveIndex, v)
                    }
                    v.setValue(rt[row.key])
                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Exception during average calculation", e)
                }
            }
            return rt
        }
    }
}
