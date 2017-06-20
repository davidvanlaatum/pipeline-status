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

    public DataTable getTable(String name) {
        PipelineStatusAction status = PipelineStatusAction.getPipelineStatusAction(script.$build(), false);
        if (status != null)
            return status.getTable(name);
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
    }
}
