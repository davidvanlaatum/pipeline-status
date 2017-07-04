package org.jenkinsci.plugins.pipelinestatus

import org.jenkinsci.plugins.workflow.cps.CpsScript

class StatusVars implements Serializable {
    private CpsScript script

    public StatusVars(CpsScript script) {
        this.script = script
    }

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
        PipelineStatusAction status = PipelineStatusAction.getPipelineStatusAction(script.$build(), true);
        status.set(name, value, type, null, null);
    }

    public void inc(String name) {
        inc(name, 1);
    }

    public void inc(String name, int value) {
        PipelineStatusAction status = PipelineStatusAction.getPipelineStatusAction(script.$build(), true);
        status.get(name, null, null).incValue(value);
    }

    public void dec(String name) {
        dec(name, 1);
    }

    public void dec(String name, int value) {
        PipelineStatusAction status = PipelineStatusAction.getPipelineStatusAction(script.$build(), true);
        status.get(name, null, null).decValue(value);
    }

    public Table getTable(String name) {
        PipelineStatusAction status = PipelineStatusAction.getPipelineStatusAction(script.$build(), false);
        if (status != null)
            return new Table(name, script);
        else
            return null;
    }

    public Table createTable(String name, List<Map<String, String>> columns) {
        PipelineStatusAction status = PipelineStatusAction.getPipelineStatusAction(script.$build(), true);
        status.createTable(name, columns);
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
            PipelineStatusAction status = PipelineStatusAction.getPipelineStatusAction(script.$build(), true);
            status.set(key, value, null, name, index);
        }

        public Object get(String key, Integer index) {
            PipelineStatusAction status = PipelineStatusAction.getPipelineStatusAction(script.$build(), true);
            return status.get(key, name, index);
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
            PipelineStatusAction status = PipelineStatusAction.getPipelineStatusAction(script.$build(), true);
            status.get(key, name, index).incValue(value);
        }

        public void dec(String name,int index) {
            dec(name, index,1);
        }

        public void dec(String key, int index, int value) {
            PipelineStatusAction status = PipelineStatusAction.getPipelineStatusAction(script.$build(), true);
            status.get(key, name, index).decValue(value);
        }

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

            Double rt;
            if (!values.empty) {
                Double sum = 0;
                values.each {
                    sum += it
                }
                rt = sum / values.size()
            }
            return rt;
        }

        public Map<String, Double> calcAverages(int valueIndex, int aveIndex, int builds = 30) {
            Map<String, Double> rt = [:]
            PipelineStatusAction status = PipelineStatusAction.getPipelineStatusAction(script.$build(), true);
            status.getTable(name).rows.each { row ->
                try {
                    rt[row.key] = getave(row.key, valueIndex, builds)
                    set(row.key, aveIndex, rt[row.key])
                } catch (Exception e) {
                }
            }
            return rt;
        }
    }
}
